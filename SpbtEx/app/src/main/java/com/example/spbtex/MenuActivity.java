package com.example.spbtex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.ui.login.LoginActivity;

import java.util.Objects;


public class MenuActivity extends AppCompatActivity {

    protected SharedPreferences pref;
    protected String user_id;
    protected String session_id;
    protected String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pref = getSharedPreferences("PreferencesEx", MODE_PRIVATE);
        user_id = pref.getString("text_userId", "login_data_id");
        session_id = pref.getString("text_sessionId", "no_session");
        //名前表示
        name = pref.getString("text_displayName", getResources().getString(R.string.login_guest_name));
        getSupportActionBar().setTitle(name);

        //アクションバーに「戻るボタン」を追加
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isLogin()){
            //未ログイン　ログインボタン表示
            menu.findItem(R.id.bar_logout).setVisible(false);
        } else {
            //ログイン中　ログアウトボタン表示
            menu.findItem(R.id.bar_login).setVisible(false);

            //セッション有効チェック
            String result = "";
            if(DbOpenHelper.STAND_ALONE){
                //(2) SQLite使用
                result = "true";
            }else{
                //(1) Asyncタスククラスのインスタンスを作成し、実行する
                MyAsyncTask task = new MyAsyncTask(new SessionData(session_id,user_id));
                task.setUrl(Urls.URL_CHECK_SESSION_POST);
                task.setMethod(MyAsyncTask.POST);
                task.setaClass(ResultMessage.class);

                task.execute();
                result = ((ResultMessage) task.getReceiveObject()).getResult();
            }

            if (result.equals("true")) {
                //セッションが有効である
            } else if (result.equals("false")) {
                //セッションが無効である
                logout(R.string.logout_message_session_error3);
            } else if(result.equals("no_data")){
                //サーバーに接続したがエラー
                logout(R.string.logout_message_session_error2);
            }else{
                //サーバーに接続できず
                logout(R.string.logout_message_session_error1);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sample, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //switchではR.id.bar_loginが使えず
        if (item.getItemId() == R.id.bar_login) {
            //ログインボタン押下

            //FLAG_ACTIVITY_CLEAR_TOP
            //新しいActivityを起動する際に、同じタスク内で既存の同じActivityのインスタンスが存在する場合、
            // その既存のActivityとその上にあるすべてのActivityをクリアしてから新しいActivityを起動する。

            //FLAG_ACTIVITY_NEW_TASK
            //新しいタスク（新しいバックスタック）を開始し、その中で新しいActivityを起動する。
            //このフラグを使用することで、別のタスクとして新しいActivityを起動することができる。

            Intent intent = new Intent(this, LoginActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.bar_logout) {
            //ログアウトボタン押下
            logout(R.string.logout_message_push_bar);
        } else if (item.getItemId() == android.R.id.home) {
            //戻るボタンが押されたとき
            //画面を終了させる
            finish();
        }
        return true;
    }

    public void logout(Integer message){
        logout(getString(message));
    }

    public void logout(String message) {
        //String result = "";
        if( !DbOpenHelper.STAND_ALONE && !message.equals(getString(R.string.logout_message_delete_member)) ){
            //DBからセッションを削除する
            MyAsyncTask task = new MyAsyncTask(new SessionData(session_id,user_id));
            task.setUrl(Urls.URL_DELETE_SESSION_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(ResultMessage.class);

            task.execute();
            //result = ((ResultMessage) task.getReceiveObject()).getResult();
            //resultは処理しない
        }

        //プリファレンスからセッションを削除する
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("text_userId");
        editor.remove("text_displayName");
        editor.remove("text_sessionId");
        editor.apply(); // 状態を更新

        //トースト表示
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        //Activityを起動する前に、既存のタスクを破棄する。このフラグはFLAG_ACTIVITY_NEW_TASKと組み合わせてのみ使用できる
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean isLogin(){
        //ログイン判定
        return    user_id != null && !user_id.isEmpty() && !user_id.equals("login_data_id")
                && session_id != null && !session_id.isEmpty() && !session_id.equals("no_session");
                //&& user_id.equals(id);
    }
}