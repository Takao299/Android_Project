package com.example.spbtex.ui.member;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.spbtex.ResultMessages;
import com.example.spbtex.SafeClick;
import com.example.spbtex.SessionData;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.MemberModel;
import java.util.ArrayList;
import java.util.List;

public class UpdateEtcActivity extends MenuActivity {

    EditText name_ed;
    Button update_button;
    Button delete_button;
    Intent intent;
    String id;
    String email;
    String name;
    DeleteDialogFragment dialogFragment;
    SafeClick update_safeClick;
    SafeClick delete_safeClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_etc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name_ed = findViewById(R.id.member_name);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        intent = getIntent();
        id = intent.getStringExtra("EXTRA_DATA_ID");
        email = intent.getStringExtra("EXTRA_DATA_EMAIL");
        name = intent.getStringExtra("EXTRA_DATA_NAME");
        update_safeClick = new SafeClick();
        delete_safeClick = new SafeClick();
    }

    //更新ボタン
    public void updateMember(View v){
        if(!update_safeClick.isPassTime()) return;

        String new_name = name_ed.getText().toString();

        boolean formIsValid = false;
        if (new_name.equals("") || new_name == null) {
            Toast.makeText(this, "名前を入力してください", Toast.LENGTH_SHORT).show();
        }else{
            formIsValid = true;
        }
        if(!formIsValid){
            return;
        }

        String result = "no_data";
        List<String> messages = new ArrayList<String>();
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            if(new MemberModel(getApplicationContext()).updateEtc(new_name,id))
                result = "success";
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            //コンストラクタのオーバーロードで被るので今だけ個別セット
            Member _member = new Member();
            _member.setId(Long.valueOf(id));
            _member.setEmail(email);
            _member.setName(new_name);
            MyAsyncTask task = new MyAsyncTask(_member);
            task.setUrl(Urls.URL_UPDATE_ETC_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(ResultMessages.class);
            task.execute();

            ResultMessages resultMessages = (ResultMessages) task.getReceiveObject();
            result = resultMessages.getResult();
            if (resultMessages.getErrors() != null) {
                for(ResultMessages.Error er : resultMessages.getErrors()){
                    messages.add(er.getMessage());
                }
            }
        }

        if (result.equals("success")) {
            Toast.makeText(this, "会員情報変更完了", Toast.LENGTH_SHORT).show();

            //プリファレンスへの書き込み
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("text_displayName", new_name);
            editor.apply();

            //ログイン中の会員名の更新のため、ホーム画面に戻り再表示
            //Activityを起動する前に、既存のタスクを破棄する。このフラグはFLAG_ACTIVITY_NEW_TASKと組み合わせてのみ使用できる
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (result.equals("error")) {
            //全てのエラーメッセージを表示する
            for(String str:messages){
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "エラー", Toast.LENGTH_SHORT).show();
        }
    }

    //退会ボタン
    public void deleteMember(View v){
        if(!delete_safeClick.isPassTime()) return;
        dialogFragment = new DeleteDialogFragment();
        dialogFragment.setActivity(this);
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    //退会を実行
    public void deleteMemberExecute(){
        if (!isLogin()) {
            //未ログイン
            Toast.makeText(this, "ログインして下さい", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //ログイン中
            String result = "no_data";
            if(DbOpenHelper.STAND_ALONE){
                //(2) SQLiteを使用
                //SQLiteでは有効な予約が残っているかまでは調べない
                if(new MemberModel(getApplicationContext()).deleteData(user_id))
                    result = "success";
            }else{
                //(1) Asyncタスククラスのインスタンスを作成し、実行する
                MyAsyncTask task = new MyAsyncTask(new UpdatePassData(email,new SessionData(session_id,user_id)));
                task.setUrl(Urls.URL_DELETE_MEMBER_POST);
                task.setMethod(MyAsyncTask.POST);
                task.setaClass(ResultMessage.class);
                task.execute();
                result = ((ResultMessage) task.getReceiveObject()).getResult();
            }

            if(result.equals("success")){
                logout(R.string.logout_message_delete_member);
            } else if (result.equals("no_session")) {
                logout(R.string.logout_message_session_error3);
            } else if (result.equals("remain")) {
                Toast.makeText(this, "有効な予約があります", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "エラー", Toast.LENGTH_SHORT).show();
            }
        }
    }


}