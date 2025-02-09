package com.example.spbtex.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.spbtex.sqlite.MemberModel;
import com.example.spbtex.ui.login.LoginActivity;
import com.example.spbtex.ui.member.Member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistActivity extends AppCompatActivity {

    TextView email_tv;
    EditText password1_ed;
    EditText password2_ed;
    CheckBox password_cb;
    EditText name_ed;
    Button register_button;
    Intent intent;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email_tv = findViewById(R.id.selected_email);
        password1_ed = findViewById(R.id.password1);
        password2_ed = findViewById(R.id.password2);
        password_cb = findViewById(R.id.password_display_check);
        name_ed = findViewById(R.id.member_name);
        register_button = findViewById(R.id.register_button);
        intent = getIntent();
        email = intent.getStringExtra("EXTRA_DATA_EMAIL");
        email_tv.setText(email);

        // スクリーンキャプチャを無効化する
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        // パスワードを表示するオプションのチェック変更リスナーを設定
        password_cb.setOnCheckedChangeListener(new OnPasswordDisplayCheckedChangeListener());

        SafeClick safeClick = new SafeClick();
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!safeClick.isPassTime()) return;
                registerConfirmed();
            }
        });

    }

    //会員登録処理
    public void registerConfirmed(){
        String password1 = password1_ed.getText().toString();
        String password2 = password2_ed.getText().toString();
        String name = name_ed.getText().toString();
        boolean formIsValid = false;
        if (password1.equals("") || password1 == null) {
            Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_SHORT).show();
        }else if (!checkPass(password1)) {
            Toast.makeText(this, "パスワードは英数字混在で8～24文字で入力してください", Toast.LENGTH_SHORT).show();
        }else if (password2.equals("") || password2 == null) {
            Toast.makeText(this, "パスワードを再度入力してください", Toast.LENGTH_SHORT).show();
        }else if (!password1.equals(password2)) {
            Toast.makeText(this, "再入力パスワードが不一致", Toast.LENGTH_SHORT).show();
        }else if (name.equals("") || name == null) {
            Toast.makeText(this, "名前を入力してください", Toast.LENGTH_SHORT).show();
        }else{
            formIsValid = true;
        }
        if(!formIsValid){
            return;
        }

        String result = "no_data";
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            MemberModel memberModel = new MemberModel(getApplicationContext());
            if( memberModel.searchMember(email,"email")==null )
                //有効な既存会員がいないので新規登録
                if(memberModel.insertData(name,password1,email))
                    result = "success";
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new Member(email,name,password1));
            task.setUrl(Urls.URL_MEMBER_REGISTER_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(ResultMessage.class);

            task.execute();
            result = ((ResultMessage) task.getReceiveObject()).getResult();
        }

        if(result.equals("success")){
            Toast.makeText(this, "会員登録完了", Toast.LENGTH_SHORT).show();
            //登録完了したらログイン画面へ
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "登録失敗", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonFinish(View v){
        finish();
    }

    public boolean checkPass(String str) {
        /*
         * 【条件】
         * ・8文字以上24文字以下であること。
         * ・使用できる文字は半角数字、半角英小文字、半角英大文字、ハイフンのみであること。
         * ・数字、英小文字、英大文字、ハイフンの混在であること。→・数字、英文字の混在であること。
         */
        //Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[-])[a-zA-Z0-9-]{8,24}$");
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9-]{8,24}$");
        Matcher m = p.matcher(str);
        Boolean result = m.matches();
        return result;
    }

    public boolean checkPass2(String str) {
        String pattern = "[a-z]";
        return str.matches(pattern);
    }


    /**
     * パスワードの表示オプションチェックを変更した場合の処理
     */
    private class OnPasswordDisplayCheckedChangeListener implements
            CompoundButton.OnCheckedChangeListener {

        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

            // カーソル位置が最初に戻るので今のカーソル位置を記憶する
            int pos = password1_ed.getSelectionStart();
            int pos2 = password2_ed.getSelectionStart();

            // ★ポイント2★ パスワードを平文表示するオプションを用意する
            // InputTypeの作成
            int type = InputType.TYPE_CLASS_TEXT;
            if (isChecked) {
                // チェックON時は平文表示
                type |= InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            } else {
                // チェックOFF時はマスク表示
                type |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            // パスワードEditTextにInputTypeを設定
            password1_ed.setInputType(type);
            password2_ed.setInputType(type);

            // カーソル位置を設定する
            password1_ed.setSelection(pos);
            password2_ed.setSelection(pos2);
        }
    }

}