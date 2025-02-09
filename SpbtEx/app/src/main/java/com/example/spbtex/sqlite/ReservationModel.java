package com.example.spbtex.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.spbtex.ui.history.HistoryData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    DbOpenHelper helper = null;
    SQLiteDatabase db = null;
    public static final String RESERVATION_TABLE_NAME = "reservation"; //searchRemainのSQL文は参照してないので注意

    private String resultReservation = null;
    public String getResultReservation() {
        return resultReservation;
    }

    private List<String> messages;
    public List<String> getMessages() {
        return messages;
    }

    public ReservationModel(Context context){
        if(helper == null || db == null) {
            helper = new DbOpenHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    //バリデーションチェック無しで予約するのみ
    public void insertData(String member_id, String facility_id, String _rday, String _rstart, String _rend){

        Integer rday = Math.toIntExact(LocalDate.parse(_rday).toEpochDay());
        Integer rstart = LocalTime.parse(_rstart).toSecondOfDay();
        Integer rend = LocalTime.parse(_rend).toSecondOfDay();

        String sql = "insert into " + RESERVATION_TABLE_NAME
                + " (member_id, facility_id, rday, rstart, rend) values(?,?,?,?,?);";
        String[] bindStr = new String[]{
                member_id,
                facility_id,
                rday.toString(),
                rstart.toString(),
                rend.toString()
        };
        try {
            db.execSQL(sql, bindStr);
            this.resultReservation = "success";
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
            this.resultReservation = "error";
            List<String> messages = new ArrayList<String>();
            messages.add("sample_error");
            this.messages = messages;
        }
    }

    //予約リストを取得
    @SuppressLint("Range")
    public List<HistoryData> searchRemain(String member_id, boolean remain) {

        List<HistoryData> reservationList = new ArrayList<>();
        Integer nowDate = Math.toIntExact(LocalDate.now(ZoneId.of("Asia/Tokyo")).toEpochDay());
        Integer nowTime = LocalTime.now(ZoneId.of("Asia/Tokyo")).toSecondOfDay();
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try {
            //SQL文
            String sql = null;
            if(remain){
                //残っている有効な予約 //+ " AND ((rday = ? AND rend >= ?) OR rday > ?)"
                //　対象会員かつ　（予約日が今日かつ予約終了時間が現在以降　もしくは　予約日が明日以降）かつ未削除
                sql = "select reservation.id, reservation.member_id, reservation.facility_id, reservation.rday, reservation.rstart, reservation.rend, facility.name" +
                        " from reservation inner join facility on reservation.facility_id = facility.id"
                        + " WHERE member_id = ?"
                        + " AND ((rday = ? AND rend >= ?) OR rday > ?)"
                        + " AND reservation.deleteDateTime IS NULL;";
            }else{
                //過去の予約
                //　対象会員かつ　（予約日が今日かつ予約終了時間が現在より前　もしくは　予約日が昨日以前）かつ未削除
                sql = "select reservation.id, reservation.member_id, reservation.facility_id, reservation.rday, reservation.rstart, reservation.rend, facility.name" +
                        " from reservation inner join facility on reservation.facility_id = facility.id"
                        + " WHERE member_id = ?"
                        + " AND ((rday = ? AND rend < ?) OR rday < ?)"
                        + " AND reservation.deleteDateTime IS NULL;";
            }

            //SQL文の実行
            cursor = db.rawQuery(sql, new String[]{member_id,nowDate.toString(),nowTime.toString(),nowDate.toString()});
            if(!readCursor(cursor).isEmpty()) {
                cursor.moveToFirst();
                 do {
                     String id = cursor.getString(cursor.getColumnIndex("id"));
                    String memberId = cursor.getString(cursor.getColumnIndex("member_id"));
                    String facilityId = cursor.getString(cursor.getColumnIndex("facility_id"));
                    String _rday = cursor.getString(cursor.getColumnIndex("rday"));
                    String rday = LocalDate.ofEpochDay(Long.valueOf(_rday)).toString();
                    String _rstart = cursor.getString(cursor.getColumnIndex("rstart"));
                    String rstart = LocalTime.ofSecondOfDay(Long.valueOf(_rstart)).toString();
                    String _rend = cursor.getString(cursor.getColumnIndex("rend"));
                    String rend = LocalTime.ofSecondOfDay(Long.valueOf(_rend)).toString();
                    String memberName = null;
                    String facilityName = cursor.getString(cursor.getColumnIndex("name"));
                     reservationList.add(new HistoryData(Long.valueOf(id),Long.valueOf(memberId),Long.valueOf(facilityId),memberName,facilityName,rday,rstart,rend));
                }while (cursor.moveToNext());
            }
        } finally{
            // Cursorを忘れずにcloseする
            if( cursor != null ){
                cursor.close();
            }
        }
        return reservationList;
    }

    /** 検索結果の読み込み */
    private String readCursor(Cursor cursor ){
        //カーソル開始位置を先頭にする
        cursor.moveToFirst();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= cursor.getCount(); i++) {
            //SQL文の結果から、必要な値を取り出す
            sb.append(cursor.getString(i));//何か処理
            cursor.moveToNext();
        }
        return sb.toString();
    }

    /*
    //予約の詳細を取得
    @SuppressLint("Range")
    public Reservation searchReservation(String id) {

        Reservation reservation = null;
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try {
            //SQL文
            String sql = "select * from reservation inner join facility on reservation.facility_id = facility.id"
                    + " WHERE reservation.id = ?";
            //SQL文の実行
            cursor = db.rawQuery(sql, new String[]{id});
            if(!readCursor(cursor).isEmpty()) {
                cursor.moveToFirst();
                String memberid = cursor.getString(cursor.getColumnIndex("member_id"));
                String facility_id = cursor.getString(cursor.getColumnIndex("facility_id"));
                String _rday = cursor.getString(cursor.getColumnIndex("rday"));
                String rday = LocalDate.ofEpochDay(Long.valueOf(_rday)).toString();
                String _rstart = cursor.getString(cursor.getColumnIndex("rstart"));
                String rstart = LocalTime.ofSecondOfDay(Long.valueOf(_rstart)).toString();
                String _rend = cursor.getString(cursor.getColumnIndex("rend"));
                String rend = LocalTime.ofSecondOfDay(Long.valueOf(_rend)).toString();
                String facility_name = cursor.getString(cursor.getColumnIndex("name"));
                reservation = new Reservation(memberid,facility_id,rday,rstart,rend,facility_name);
            }
        } finally{
            // Cursorを忘れずにcloseする
            if( cursor != null ){
                cursor.close();
            }
        }
        return reservation;
    }

     */

    //予約をキャンセル
    public void deleteReservation(String id){

        System.out.println("resmodeldeleteid:"+id);
        // ContentValuesのインスタンスにデータを格納
        String sql = "DELETE FROM " + RESERVATION_TABLE_NAME
                + " WHERE id = ?;";
        String[] bindStr = new String[]{id};
        try {
            db.execSQL(sql,bindStr);
            this.resultReservation = "success";
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
            this.resultReservation = "error";
            List<String> messages = new ArrayList<String>();
            messages.add("sample_error");
            this.messages = messages;
        }
    }

}



