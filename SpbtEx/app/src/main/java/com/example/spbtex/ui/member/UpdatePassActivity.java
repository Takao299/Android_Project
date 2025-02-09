package com.example.spbtex.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.ResultMessage;
import com.example.spbtex.SafeClick;
import com.example.spbtex.SessionData;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.MemberModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePassActivity extends MenuActivity {

    EditText password0_ed;
    EditText password1_ed;
    EditText password2_ed;
    CheckBox password_cb0;
    CheckBox password_cb1;
    Button update_button;
    Intent intent;
    String id;
    String email;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        password0_ed = findViewById(R.id.password0);
        password1_ed = findViewById(R.id.password1);
        password2_ed = findViewById(R.id.password2);
        password_cb0 = findViewById(R.id.password_display_check0);
        password_cb1 = findViewById(R.id.password_display_check1);
        intent = getIntent();
        id = intent.getStringExtra("EXTRA_DATA_ID");
        email = intent.getStringExtra("EXTRA_DATA_EMAIL");
        name = intent.getStringExtra("EXTRA_DATA_NAME");

        // スクリーンキャプチャを無効化する
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        // パスワードを表示するオプションのチェック変更リスナーを設定
        password_cb0.setOnCheckedChangeListener(new UpdatePassActivity.OnPasswordDisplayCheckedChangeListener0());
        password_cb1.setOnCheckedChangeListener(new UpdatePassActivity.OnPasswordDisplayCheckedChangeListener1());

        SafeClick safeClick = new SafeClick();
        update_button = findViewById(R.id.update_pass_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!safeClick.isPassTime()) return;
                updatePassword();
            }
        });
    }

    //更新処理
    public void updatePassword(){

        String password0 = password0_ed.getText().toString();
        String password1 = password1_ed.getText().toString();
        String password2 = password2_ed.getText().toString();

        boolean formIsValid = false;
        if (password0.equals("") || password0 == null) {
            Toast.makeText(this, "現在のパスワードを入力してください", Toast.LENGTH_SHORT).show();
        }else if (!checkPass(password0)) {
            Toast.makeText(this, "パスワードは英数字混在で8～24文字で入力してください", Toast.LENGTH_SHORT).show();
        }else if (password1.equals("") || password1 == null) {
            Toast.makeText(this, "新しいパスワードを入力してください", Toast.LENGTH_SHORT).show();
        }else if (!checkPass(password1)) {
            Toast.makeText(this, "パスワードは英数字混在で8～24文字で入力してください", Toast.LENGTH_SHORT).show();
        }else if (password2.equals("") || password2 == null) {
            Toast.makeText(this, "パスワードを再度入力してください", Toast.LENGTH_SHORT).show();
        }else if (!password1.equals(password2)) {
            Toast.makeText(this, "再入力パスワードが不一致", Toast.LENGTH_SHORT).show();
        }else{
            formIsValid = true;
        }
        if(!formIsValid){
            return;
        }

        if (!isLogin()) {
            //未ログイン
            Toast.makeText(this, "ログインして下さい", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //ログイン中
            String result = "no_data";

            if(DbOpenHelper.STAND_ALONE){
                //(2) SQLiteを使用
                if(new MemberModel(getApplicationContext()).updatePass(password1,user_id,password0))
                    result = "success";
            }else{
                //(1) Asyncタスククラスのインスタンスを作成し、実行する
                MyAsyncTask task = new MyAsyncTask(new UpdatePassData(email,password0,password1,new SessionData(session_id,user_id)));
                task.setUrl(Urls.URL_UPDATE_PASS_POST);
                task.setMethod(MyAsyncTask.POST);
                task.setaClass(ResultMessage.class);
                task.execute();
                result = ((ResultMessage) task.getReceiveObject()).getResult();
            }

            if(result.equals("success")){
                Toast.makeText(this, "パスワード変更完了", Toast.LENGTH_SHORT).show();
                finish();
            } else if (result.equals("no_session")) {
                //Toast.makeText(this, "セッションエラーです", Toast.LENGTH_SHORT).show();
                logout("セッションが無効です。再ログインしてください");
            } else if (result.equals("no_pass")) {
                Toast.makeText(this, "現在のパスワードが間違っています", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "エラーです", Toast.LENGTH_SHORT).show();
            }
        }
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
        //System.out.println("pattern_result:" + str.matches(pattern));
        return str.matches(pattern);
    }


    /**
     * パスワードの表示オプションチェックを変更した場合の処理
     */
    private class OnPasswordDisplayCheckedChangeListener0 implements
            CompoundButton.OnCheckedChangeListener {

        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

            // カーソル位置が最初に戻るので今のカーソル位置を記憶する
            int pos = password0_ed.getSelectionStart();

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
            password0_ed.setInputType(type);

            // カーソル位置を設定する
            password0_ed.setSelection(pos);
        }
    }

    private class OnPasswordDisplayCheckedChangeListener1 implements
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