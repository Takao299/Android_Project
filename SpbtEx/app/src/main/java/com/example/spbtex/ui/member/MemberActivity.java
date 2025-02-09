package com.example.spbtex.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.SessionData;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.MemberModel;


public class MemberActivity extends MenuActivity {

    TextView email_tv;
    TextView name_tv;
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email_tv = findViewById(R.id.member_email);
        name_tv = findViewById(R.id.member_name);

        if (!isLogin()) {
            //未ログイン
            Toast.makeText(this, "ログインして下さい", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //ログイン中
            Long result = null;
            if(DbOpenHelper.STAND_ALONE){
                //(2) SQLite使用
                MemberModel memberModel = new MemberModel(getApplicationContext());
                member = memberModel.searchMember(user_id,"id");
                result = member.getId();
            }else{
                //(1) Asyncタスククラスのインスタンスを作成し、実行する
                MyAsyncTask task = new MyAsyncTask(new SessionData(session_id,user_id));
                task.setUrl(Urls.URL_MEMBER_HOME_POST);
                task.setMethod(MyAsyncTask.POST);
                task.setaClass(Member.class);

                task.execute();
                member = (Member) task.getReceiveObject();
                result = member.getId();
            }

            if(result!=-1){
                //会員情報の取得に成功
                email_tv.setText(member.getEmail());
                name_tv.setText(member.getName());
            }else{
                //何らかのエラー
                logout("エラーです。再ログインしてください");
            }
        }
    }

    //メアド変更画面へ遷移
    public void moveUpdateMail(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MemberActivity.this, UpdateMailActivity.class);
        startActivity(intent);
    }

    //パス変更画面へ遷移
    public void moveUpdatePass(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MemberActivity.this, UpdatePassActivity.class);
        intent.putExtra("EXTRA_DATA_ID", member.getId().toString());
        intent.putExtra("EXTRA_DATA_EMAIL", member.getEmail());
        intent.putExtra("EXTRA_DATA_NAME", member.getName());
        startActivity(intent);
    }

    //その他会員情報変更画面へ遷移
    public void moveUpdateEtc(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MemberActivity.this, UpdateEtcActivity.class);
        intent.putExtra("EXTRA_DATA_ID", member.getId().toString());
        intent.putExtra("EXTRA_DATA_EMAIL", member.getEmail());
        intent.putExtra("EXTRA_DATA_NAME", member.getName());
        startActivity(intent);
    }


}