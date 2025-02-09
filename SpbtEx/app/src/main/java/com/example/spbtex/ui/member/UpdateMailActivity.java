package com.example.spbtex.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.SafeClick;
import com.example.spbtex.Urls;
import com.example.spbtex.ui.login.data.model.LoggedInUser;
import com.example.spbtex.sqlite.DbOpenHelper;

public class UpdateMailActivity extends MenuActivity {

    EditText inputText;
    Button email_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_mail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputText = findViewById(R.id.input_email);

        SafeClick safeClick = new SafeClick();
        inputText = findViewById(R.id.input_email);
        email_button = findViewById(R.id.button_email_post);
        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!safeClick.isPassTime()) return;
                emailPost();
            }
        });
    }

    //メール送信処理
    public void emailPost(){
        String username = inputText.getText().toString();
        if (username.equals("") || username == null) {
            Toast.makeText(this, "文字を入力してください", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( !username.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show();
            return;
            //コピペで貼り付けたメアドは駄目だけど初めから手入力したらOK
        }

        String sessionId = null;
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            sessionId = "dummy_session";
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new Member(username, name, "dummy_password")); //受信メール,受信者名,引数3つのオーバーロードを使うため
            task.setUrl(Urls.URL_UPDATE_MAIL_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(LoggedInUser.class);

            task.execute();
            LoggedInUser loggedInUser = (LoggedInUser) task.getReceiveObject();
            sessionId = loggedInUser.getSessionId(); //次ページだけ有効なセッションを保存
        }

        if (sessionId!=null) {
            Toast.makeText(this, "メール送信完了", Toast.LENGTH_SHORT).show();
            //コード認証画面へ
            Intent intent = new Intent(UpdateMailActivity.this, UpdateMailCodeActivity.class);
            intent.putExtra("EXTRA_DATA_EMAIL", username);
            intent.putExtra("EXTRA_DATA_SESSION", sessionId); //次の画面でだけ有効なセッション
            startActivity(intent);
        } else{
            Toast.makeText(this, "エラーです", Toast.LENGTH_SHORT).show();
        }
    }

}