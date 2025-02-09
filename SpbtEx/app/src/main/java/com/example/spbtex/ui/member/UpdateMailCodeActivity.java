package com.example.spbtex.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.MainActivity;
import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.ResultMessage;
import com.example.spbtex.SafeClick;
import com.example.spbtex.SessionData;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.MemberModel;
import com.example.spbtex.ui.register.MailCodeData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateMailCodeActivity extends MenuActivity {

    EditText inputText;
    Button code_button;
    Intent intent;
    String updateEmail;
    String page_sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_mail_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputText = findViewById(R.id.input_code);
        intent = getIntent();
        updateEmail = intent.getStringExtra("EXTRA_DATA_EMAIL");
        page_sessionId = intent.getStringExtra("EXTRA_DATA_SESSION");

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


    public void codePost(){
        String code = inputText.getText().toString();
        String result = "no_data";
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            if(code.equals("0000")){
                if(new MemberModel(getApplicationContext()).updateEmail(updateEmail,user_id))
                    result = "success";
            }
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(
                    new MailCodeData(updateEmail,code,page_sessionId, new SessionData(session_id,user_id)));
            task.setUrl(Urls.URL_UPDATE_MAIL_CODE_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(ResultMessage.class);

            task.execute();
            result = ((ResultMessage) task.getReceiveObject()).getResult();
        }

        if(result.equals("success")){
            Toast.makeText(this, "更新完了", Toast.LENGTH_SHORT).show();
            //ログイン中の会員名の更新のため、ホーム画面に戻り再表示
            //Activityを起動する前に、既存のタスクを破棄する。このフラグはFLAG_ACTIVITY_NEW_TASKと組み合わせてのみ使用できる
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (result.equals("no_session")) {
            Toast.makeText(this, "コードの有効期限を過ぎています", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "更新失敗", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkCode(String str) {
        Pattern p = Pattern.compile("^(?=.*[0-9])[0-9]{4}$");
        Matcher m = p.matcher(str);
        Boolean result = m.matches();
        return result;
    }

}