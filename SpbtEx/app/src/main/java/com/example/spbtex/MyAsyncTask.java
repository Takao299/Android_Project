package com.example.spbtex;

import android.os.Handler;
import android.os.Looper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyAsyncTask {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private boolean canceled;

    public interface SendObject{ }
    //new MyAsyncTask(new MyAsyncTask.SendObject(){});の形で実装しても良い。フィールドにはgetter,setterが必要そう
    private Object sendObject;
    private String send; //直接Jsonを書いてもいい 送信用json
    private String receive; //返信用json
    private String url;
    private String method; //"GET","POST"を指定
    private String tag; //ログ用
    private Class<?> aClass; //返信jsonを変換する型
    private boolean listClass; //返信jsonがリストかどうか
    private Object receiveObject;
    private boolean wait = true; //スレッドの完了まで待機するか

    public void setSend(String send) {
        this.send = send;
    }
    public String getReceive() {
        return receive;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }
    public void setListClass(boolean listClass) {
        this.listClass = listClass;
    }
    public Object getReceiveObject() {
        return receiveObject;
    }
    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public MyAsyncTask() {
    }

    public MyAsyncTask(Object sendObject) {
        this.sendObject = sendObject;
    }

    private class AsyncRunnable implements Runnable {

        private String result;
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                onPreExecute();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            result = doInBackground();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!canceled) {
                        onPostExecute();
                    } else {
                        onCancelled();
                    }
                }
            });
        }
    }

    public void execute() {
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        try {
            if(!wait){ //完了まで待機しない
                executorService.execute(new AsyncRunnable());
            }else{                                                                      //ForkJoinのinvokeも有り
                Future<?> fooFuture = executorService.submit(new AsyncRunnable()); //submit 非同期戻り値有り //callable入れても良い
                fooFuture.get(); //完了まで待機する
            }
        } catch(InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        } finally {
            executorService.shutdown();
        }
    }

    protected void onPreExecute() throws JsonProcessingException {
        if (sendObject != null) {
            ObjectMapper mapper = new ObjectMapper();
            send = mapper.writeValueAsString(sendObject);
        }
    }

    protected String doInBackground() {
        String _result = "abnormal termination";
        HttpURLConnection con = null;
        try {
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();

            if(method.equals(POST)){
                //送信するための設定
                con.setRequestMethod(method);
                con.setInstanceFollowRedirects(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                //送信するjsonの作成
                JSONObject send_json = new JSONObject(send);
                //送信するための設定
                con.setRequestProperty("Accept-Language", "jp");
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //ostreamへjsonを書き込む
                OutputStream os = con.getOutputStream();
                PrintStream ps = new PrintStream(os);
                ps.print(send_json);
                ps.close();
            }

            //Apiから返ってきたjsonの処理
            InputStream stream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null)
              builder.append(line);
            stream.close();
            receive = builder.toString();

            if(receive.substring(0, 1).equals("[")) //リストかどうか
                listClass = true;
            
            //指定した型に変換
            //例外処理がついて回るためこの一つのスレッドで全部済ませてしまう
            ObjectMapper mapper = new ObjectMapper();
            if(listClass){
                //List型に変換する必要がある
                JavaType stringCollection = mapper.getTypeFactory().constructCollectionType(List.class, aClass);
                receiveObject = mapper.readValue(receive, stringCollection);
            }else{
                receiveObject = mapper.readValue(receive, aClass);
            }

            _result = "successful termination";

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            cancel(true);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        } finally{
            con.disconnect();
        }
        return _result;
    }

    protected void onPostExecute() {
        if(tag!=null)
            System.out.println(tag + ":" + receive);
    }

    protected void cancel(Boolean flag) {
        canceled = flag;
    }

    protected void onCancelled() {

    }
}
