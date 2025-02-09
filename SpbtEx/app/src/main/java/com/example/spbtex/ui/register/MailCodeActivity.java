package com.example.spbtex.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.ResultMessage;
import com.example.spbtex.SafeClick;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailCodeActivity extends AppCompatActivity {

    EditText inputText;
    Button code_button;
    Intent intent;
    String email;
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mail_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputText = findViewById(R.id.input_code);
        intent = getIntent();
        email = intent.getStringExtra("EXTRA_DATA_EMAIL");
        sessionId = intent.getStringExtra("EXTRA_DATA_SESSION");

        SafeClick safeClick = new SafeClick();
        code_button = findViewById(R.id.button_code_post);
        code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!safeClick.isPassTime()) return;
                codePost();
            }
        });
    }

    //コード送信処理
    public void codePost(){
        // ボタン押下時の動作
        String code = inputText.getText().toString();

        String result = "no_data";
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            if(code.equals("0000")) result = "success";
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new MailCodeData(email,code,sessionId));
            task.setUrl(Urls.URL_CODE_REGISTER_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(ResultMessage.class);

            task.execute();
            result = ((ResultMessage) task.getReceiveObject()).getResult();
        }

        if(result.equals("success")){
            Toast.makeText(this, "認証完了", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MailCodeActivity.this, RegistActivity.class);
            intent.putExtra("EXTRA_DATA_EMAIL", email);
            startActivity(intent);
        } else if (result.equals("no_session")) {
            Toast.makeText(this, "コードの有効期限を過ぎています", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "認証失敗", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonFinish(View v){
        finish();
    }

    public boolean checkCode(String str) {
        Pattern p = Pattern.compile("^(?=.*[0-9])[0-9]{4}$");
        Matcher m = p.matcher(str);
        Boolean result = m.matches();
        return result;
    }

}