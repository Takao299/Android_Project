package com.example.spbtex.dataload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.ui.facility.CardRecyclerAdapter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShowAsyncTask {

    private boolean canceled;
    private boolean wait = true; //スレッドの完了まで待機するか
    private Context context;
    private String fileName;
    private ImageView imageView;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
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

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
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
        try {
            //読み込むファイル
            FileInputStream in = new FileInputStream(context.getFilesDir() +"/"+ fileName);
            BufferedInputStream bis = new BufferedInputStream(in);

            //読み込んだファイルをビットマップに変換
            Bitmap bitmap = BitmapFactory.decodeStream(bis);

            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
                //System.out.println("show finish:"+fileName);
            }

            bis.close();
            in.close();
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
