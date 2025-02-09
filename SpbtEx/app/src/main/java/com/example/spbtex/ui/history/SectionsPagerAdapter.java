package com.example.spbtex.ui.history;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.R;
import com.example.spbtex.SessionData;
import com.example.spbtex.Urls;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.ReservationModel;
import com.example.spbtex.ui.history.fragPack.ItemFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    //追加
    private String user_id;
    private String session_id;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    //追加
    public SectionsPagerAdapter(Context context, FragmentManager fm, String user_id, String session_id) {
        super(fm);
        mContext = context;
        this.user_id = user_id;
        this.session_id = session_id;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment.
        //return PlaceholderFragment.newInstance(position + 1);

        //ここから自作
        //PlaceholderFragmentとPageViewModelは使わなくなる
        return new ItemFragment((makeReservationList(position)), position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    //追加　サーバーから予約履歴を取ってくる
    public List<HistoryData> makeReservationList(int position){
        List<HistoryData> reservationList = new ArrayList<>();
        if(DbOpenHelper.STAND_ALONE){
            //(2) SQLite使用
            ReservationModel reservationModel = new ReservationModel(mContext.getApplicationContext());
            switch (position){
                case 0:
                    reservationList = reservationModel.searchRemain(user_id,true);
                    break;
                case 1:
                    reservationList = reservationModel.searchRemain(user_id,false);
            }
        }else{
            //(1) Asyncタスククラスのインスタンスを作成し、実行する
            MyAsyncTask task = new MyAsyncTask(new SessionData(session_id,user_id));
            switch (position){
                case 0:
                    task.setUrl(Urls.URL_REMAIN_POST);
                    break;
                case 1:
                    task.setUrl(Urls.URL_PAST_POST);
            }
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(HistoryData.class);
            task.execute();
            reservationList= (List<HistoryData>) task.getReceiveObject();
        }
        return  reservationList;
    }

}