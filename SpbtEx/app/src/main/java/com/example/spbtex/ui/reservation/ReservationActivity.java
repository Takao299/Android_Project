package com.example.spbtex.ui.reservation;

//import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.MainActivity;
import com.example.spbtex.MenuActivity;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.ResultMessages;
import com.example.spbtex.SafeClick;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.ReservationModel;
import com.example.spbtex.ui.history.Reservation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReservationActivity extends MenuActivity implements DatePickerDialog.OnDateSetListener {

    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    LinearLayout linearLayout;
    Button res_button;
    ReservationDialogFragment dialogFragment;
    TextView facility_tv;
    TextView date_tv;
    TextView time_tv;
    Intent intent;
    String f_id;
    String f_name;
    String r_date;
    String r_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linearLayout = findViewById(R.id.linearLayout);
        facility_tv = findViewById(R.id.facility_text);
        date_tv = findViewById(R.id.date_text);
        time_tv = findViewById(R.id.time_text);
        intent = getIntent();
        f_id = intent.getStringExtra("EXTRA_DATA_ID");
        f_name = intent.getStringExtra("EXTRA_DATA_NAME");
        facility_tv.setText(f_name);

        //予約ボタン 最初は非活性
        SafeClick res_safeClick = new SafeClick();
        res_button = findViewById(R.id.reservation_button);
        res_button.setEnabled(false);
        res_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!res_safeClick.isPassTime()) return;
                dialogFragment = new ReservationDialogFragment();
                dialogFragment.setActivity((ReservationActivity) getParent());
                dialogFragment.show(getSupportFragmentManager(), "my_dialog");
            }
        });

        //日付を選択ボタン
        Button dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //android.app.DatePickerDialog;ではなく
                //com.wdullaer.materialdatetimepicker.date.DatePickerDialog;という特殊なDatePickerDialogを使っている
                //本来、DialogFragmentのサブクラスを分けて作るところ

                linearLayout.removeAllViewsInLayout(); //前回のTime Blockボタンを消去

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //特定の日付を選択できなくする
                DatePickerDialog dialog = DatePickerDialog.newInstance(ReservationActivity.this, year,month,day);

                //営業日・休業日設定
                //サーバーから取得
                CalendarDays calendarDays = new CalendarDays();
                if(DbOpenHelper.STAND_ALONE){
                    //(2) SQLite使用
                    //SQLiteは休業日設定なし
                    dialog.show(getSupportFragmentManager(),"DatePickerDialog");
                    return;
                }else{
                    //(1) AsyncTask
                    MyAsyncTask task = new MyAsyncTask();
                    task.setUrl(Urls.URL_CALENDER_GET);
                    task.setMethod(MyAsyncTask.GET);
                    task.setaClass(CalendarDaysJsonConverter.class);

                    task.execute();
                    calendarDays = ((CalendarDaysJsonConverter)task.getReceiveObject()).converter(calendarDays);
                }

                //選択可能範囲を設定 ここはサーバーで設定しても良い
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = LocalDate.now().plusYears(1); //LocalDate.of(2025, 2, 20);
                //スタートからエンドまで1日ずつ取り出して営業日か休業日か選り分ける
                int plusDay = 1;
                LocalDate localDate = startDate;
                List<LocalDate> onDayList = new ArrayList<LocalDate>();
                List<LocalDate> offDayList = new ArrayList<LocalDate>();
                while(true){
                    //臨時休業・営業日を優先
                    if(calendarDays.getTempOffDayList().contains(localDate)){
                        //臨時休業日
                        offDayList.add(localDate);
                    } else if (calendarDays.getTempOnDayList().contains(localDate)) {
                        //臨時営業日
                        onDayList.add(localDate);
                    } else {
                        //通常日であれば曜日ごとに分ける
                        // 月曜日:1 ~~~ 日曜日:7
                        Integer offDayOfWeek = localDate.getDayOfWeek().getValue();
                        if(calendarDays.getOffDayList().contains(offDayOfWeek)){
                            //休業曜日
                            offDayList.add(localDate);
                        }else{
                            //営業曜日
                            onDayList.add(localDate);
                        }
                    }
                    localDate = startDate.plusDays(plusDay);
                    plusDay++;
                    if(localDate.isAfter(endDate)) break;
                }
                //営業日をセット
                dialog.setSelectableDays(  dateListToCalendars(onDayList) );
                //休業日をセット
                dialog.setDisabledDays(  dateListToCalendars(offDayList) );
                //[DatePickerDialog] setSelectableDays(Calendar[] days)
                //[DatePickerDialog] setDisabledDays(Calendar[] days)

                dialog.show(getSupportFragmentManager(),"DatePickerDialog");
            }
        });
    }

    //カレンダーの日付を選択してOKを押した
    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int dayOfMonth) {
        TextView dateText =findViewById(R.id.date_text);
        //String selectedDate = year+"-" +(month+1) +"-"+dayOfMonth;
        //dateText.setText(selectedDate); //これでは2024-12-5の形に

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; //2024-07-22
        LocalDate date = LocalDate.of(year, (month+1), dayOfMonth);
        String formatted = date.format(formatter);
        this.r_date = formatted;
        //System.out.println("Select Day:"+formatted);
        dateText.setText(formatted); //これで2024-12-05に

        List<TimeBlock> timeBlockList = new ArrayList<TimeBlock>();
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLite使用
            timeBlockList = createDummyTimeBlockList(formatted);
        }else{
            //(1) AsyncTask
            MyAsyncTask task = new MyAsyncTask(new MyAsyncTask.SendObject(){
                String facility_id = f_id;
                String rday = formatted;
                public String getFacility_id() {return facility_id;}
                public void setFacility_id(String facility_id) {this.facility_id = facility_id;}
                public String getRday() {return rday;}
                public void setRday(String rday) {this.rday = rday;}
            });

            task.setUrl(Urls.URL_SELECT_DAY_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(TimeBlock.class);
            task.setListClass(true);

            task.execute();
            timeBlockList = (List<TimeBlock>) task.getReceiveObject();
        }
        //ボタンの生成
        for(TimeBlock tb:timeBlockList) {
            linearLayout.addView(makeButton(tb.getRstart()+"~"+tb.getRend(), tb.isReserved()));
        }
    }

    //時間リストボタンの生成
    private Button makeButton(String text, boolean reserved) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(text);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        button.setEnabled(!reserved);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_tv.setText(view.getTag().toString());
                r_time = view.getTag().toString();
                res_button.setEnabled(true);
            }
        });
        return button;
    }


    //予約処理を実行
    public void reservationConfirmed(){
        String date = date_tv.getText().toString();
        String time = time_tv.getText().toString();
        String[] times = time.split("~");

        if(!isLogin()){
            Toast.makeText(this, "ログインしてください", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = "no_data";
        List<String> messages = new ArrayList<String>();
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLite使用
            ReservationModel reservationModel = new ReservationModel(getApplicationContext());
            reservationModel.insertData(user_id,f_id,date,times[0],times[1]);
            result = reservationModel.getResultReservation();
            messages = reservationModel.getMessages();
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new Reservation(user_id,f_id,date,times[0],times[1]));
            task.setUrl(Urls.URL_RESERVATION_POST);
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
            Toast.makeText(this, "予約完了", Toast.LENGTH_SHORT).show();
            //メイン画面に遷移。予約画面、施設画面は破棄
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    //SQLite用ダミー
    List<TimeBlock> createDummyTimeBlockList(String rday){
        List<TimeBlock> timeBlockList = new ArrayList<TimeBlock>();
        for(int i=10; i<24 ; i++) {
            TimeBlock tb = new TimeBlock(rday, i + ":00" , i + ":59" ,false);
            timeBlockList.add(tb);
        }
        return timeBlockList;
    }

    public Calendar[] dateListToCalendars(List<LocalDate> localDateList){
        List<Calendar> calendarList = new ArrayList<Calendar>();
        for(LocalDate date:localDateList){
            Calendar cal = Calendar.getInstance();
            cal.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth());
            calendarList.add(cal);
        }
        Calendar[] calendars = calendarList.toArray(new Calendar[calendarList.size()]);
        return calendars;
    }

}