package com.example.spbtex.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.ResultMessages;
import com.example.spbtex.SafeClick;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.ReservationModel;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends MenuActivity {

    Button cancel_button;
    TextView facility_tv;
    TextView date_tv;
    TextView time_tv;
    Intent intent;
    String r_id;
    String f_name;
    //String m_name;
    String rday;
    String rstart;
    String rend;
    String dlt;
    String pst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cancel_button = findViewById(R.id.cancel_button);
        facility_tv = findViewById(R.id.facility_text);
        date_tv = findViewById(R.id.date_text);
        time_tv = findViewById(R.id.time_text);
        intent = getIntent();
        r_id = intent.getStringExtra("EXTRA_DATA_ID");
        f_name = intent.getStringExtra("EXTRA_DATA_F_NAME");
        //m_name = intent.getStringExtra("EXTRA_DATA_M_NAME");
        rday = intent.getStringExtra("EXTRA_DATA_RDAY");
        rstart = intent.getStringExtra("EXTRA_DATA_RSTART");
        rend = intent.getStringExtra("EXTRA_DATA_REND");
        dlt = intent.getStringExtra("EXTRA_DATA_DELETE");
        pst = intent.getStringExtra("EXTRA_DATA_ISPAST");
        facility_tv.setText(f_name);
        date_tv.setText(rday);
        time_tv.setText(rstart+"~"+rend);

        //キャンセルか過去ならキャンセルボタン非表示
        if(dlt!=null || pst.equals("true")) cancel_button.setVisibility(View.GONE);

        SafeClick safeClick = new SafeClick();
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!safeClick.isPassTime()) return;
                reservationCancel();
            }
        });
    }

    //キャンセルボタン
    public void reservationCancel(){
        String result = "no_data";
        List<String> messages = new ArrayList<String>();
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLite使用
            ReservationModel reservationModel = new ReservationModel(getApplicationContext());
            reservationModel.deleteReservation(r_id);
            result = reservationModel.getResultReservation();
            messages = reservationModel.getMessages();
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new Reservation(user_id,r_id));
            task.setUrl(Urls.URL_R_CANCEL_POST);
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
            Toast.makeText(this, "キャンセル完了", Toast.LENGTH_SHORT).show();
            //finish();
            //予約一覧画面を更新して再表示
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (result.equals("error")) {
            //全てのエラーメッセージを表示する
            for(String str:messages){
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }
        } else if (result.equals("dif_member_id")) {
            Toast.makeText(this, "不正な会員", Toast.LENGTH_SHORT).show();
        } else if (result.equals("alreadyCanceled")) {
            Toast.makeText(this, "キャンセル済", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "エラー", Toast.LENGTH_SHORT).show();
        }
    }
}