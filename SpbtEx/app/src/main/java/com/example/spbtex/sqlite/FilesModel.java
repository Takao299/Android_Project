package com.example.spbtex.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.spbtex.dataload.AttachedFile;
import com.example.spbtex.ui.history.HistoryData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FilesModel {

    DbOpenHelper helper = null;
    SQLiteDatabase db = null;
    public static final String FILES_TABLE_NAME = "files";

    public FilesModel(Context context){
        if(helper == null || db == null) {
            helper = new DbOpenHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    //
    public boolean insertData(String afId, String foreignId, String fileName, String createTime, String delete_pic, String deleteDateTime){

        String sql = "insert into " + FILES_TABLE_NAME
                + " (afId, foreignId, fileName, createTime, delete_pic, deleteDateTime) values(?,?,?,?,?,?);";
        String[] bindStr = new String[]{
                afId, foreignId, fileName, createTime, delete_pic, deleteDateTime
        };
        try {
            db.execSQL(sql, bindStr);
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return true;
    }

    public boolean searchData(String fileName, String createTime){
        boolean result = false;
        Cursor cursor = null;
        try{
            //SQL文
            String sql = "SELECT * FROM " + FILES_TABLE_NAME
                    + " WHERE fileName = ?"
                    + " AND createTime = ?;";
            //SQL文の実行
            cursor = db.rawQuery(sql , new String[]{fileName, createTime});
            if(!readCursor(cursor).isEmpty()) result = true; //存在する
        } finally{
            if( cursor != null ) cursor.close();
        }
        return result;
    }

    //リストなのは画像は3枚まで登録できるため
    @SuppressLint("Range")
    public List<AttachedFile> searchAllData(Long f_id){
        String f_id_str = String.valueOf(f_id);

        List<AttachedFile> attachedFileList = new ArrayList<>();
        Cursor cursor = null;
        try{
            //SQL文
            String sql    = "SELECT * FROM " + FILES_TABLE_NAME
                    + " WHERE foreignId = ?"
                    + " AND deleteDateTime IS NULL;";

            //SQL文の実行
            cursor = db.rawQuery(sql , new String[]{f_id_str});

            if(!readCursor(cursor).isEmpty()) {
                cursor.moveToFirst();
                do {
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    Integer afId = Integer.valueOf(cursor.getString(cursor.getColumnIndex("afId")));
                    Integer foreignId = Integer.valueOf(cursor.getString(cursor.getColumnIndex("foreignId")));
                    String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                    String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                    String delete_pic = cursor.getString(cursor.getColumnIndex("delete_pic"));
                    String deleteDateTime = cursor.getString(cursor.getColumnIndex("deleteDateTime"));
                    attachedFileList.add(new AttachedFile(afId, foreignId, fileName, createTime, delete_pic, deleteDateTime));
                }while (cursor.moveToNext());
            }
        } finally{
            if( cursor != null ){
                cursor.close();
            }
        }
        return attachedFileList;
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

    //物理削除
    public boolean deleteFile(String fileName, String createTime){

        String sql = "DELETE FROM " + FILES_TABLE_NAME
                + " WHERE fileName = ?"
                + " AND createTime = ?;";
        String[] bindStr = new String[]{fileName, createTime};
        try {
            db.execSQL(sql,bindStr);
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return  true;
    }

    //物理削除 全て
    public boolean deleteAllFiles(){
        String sql = "DELETE FROM " + FILES_TABLE_NAME + ";";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return  true;
    }

}



