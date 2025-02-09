package com.example.spbtex.ui.facility;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.FacilityModel;

import java.util.Collections;
import java.util.List;

public class CardRecyclerView extends RecyclerView {
    public CardRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRecyclerAdapter(context);
    }

    public void setRecyclerAdapter(Context context) {

        List<Facility> facilityList = null;
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLiteを使用
            FacilityModel facilityModel = new FacilityModel(context);
            facilityModel.readSql();
            facilityList = facilityModel.getFacilityList();
        }else{
            //(1) 外部DBを使用。Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask();
            task.setUrl(Urls.URL_FACILITIES_GET);
            task.setMethod(MyAsyncTask.GET);
            task.setaClass(Facility.class);

            task.execute();
            facilityList = (List<Facility>) task.getReceiveObject();
        }

        //(1)(2)共通
        setLayoutManager(new LinearLayoutManager(context));
        setAdapter(new CardRecyclerAdapter(context,facilityList));
    }
}

