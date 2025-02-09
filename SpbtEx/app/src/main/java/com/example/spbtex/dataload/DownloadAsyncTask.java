package com.example.spbtex.dataload;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.example.spbtex.MyAsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadAsyncTask {

    private boolean canceled;
    private String url;
    private boolean wait = true; //スレッドの完了まで待機するか
    private Context context;
    private String fileName;
    private Bitmap bitmap;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public DownloadAsyncTask() {
    }

    private class AsyncRunnable implements Runnable {

        private String result;
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            onPreExecute();
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
            }else{
                Future<?> fooFuture = executorService.submit(new AsyncRunnable()); //submit 非同期戻り値有り //callable入れても良い
                fooFuture.get(); //完了まで待機する
            }
        } catch(InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        } finally {
            executorService.shutdown();
        }
    }

    protected void onPreExecute() {}

    protected String doInBackground() {
        String _result = "abnormal termination";
        HttpURLConnection con = null;
        try {
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();

            //書き込むファイル
            FileOutputStream out = context.openFileOutput(fileName, MODE_PRIVATE);

            //サーバーから読み込むファイル
            InputStream in = con.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(in);

            //読み込んだファイルを変換
            byte[] buff = new byte[8 * 1024];
            int nRead = 0;
            while ((nRead = bis.read(buff)) != -1) {
                    out.write(buff, 0, nRead);
            }
            //ファイルを保存
            out.flush();

            bis.close();
            out.close();
            _result = "successful termination";

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            cancel(true);
        } catch (IOException e) {
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

    protected void onPostExecute() {}

    protected void cancel(Boolean flag) {
        canceled = flag;
    }

    protected void onCancelled() {
    }
}
