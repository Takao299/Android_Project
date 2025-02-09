package com.example.spbtex.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.spbtex.ui.login.data.model.LoggedInUser;
import com.example.spbtex.ui.member.Member;

import org.json.JSONException;
import org.json.JSONObject;


public class MemberModel {

    DbOpenHelper helper = null;
    SQLiteDatabase db = null;
    public static final String MEMBER_TABLE_NAME = "member";

    public MemberModel(Context context){
        if(helper == null || db == null) {
            helper = new DbOpenHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    //ログイン用
    public LoggedInUser searchData(String email, String password){ //public String searchData
        LoggedInUser loggedInUser = null;
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try{
            //SQL文
            String sql    = "SELECT * FROM " + MEMBER_TABLE_NAME
                    + " WHERE email = ?"
                    + " AND password = ?"
                    + " AND deleteDateTime IS NULL;";

            //SQL文の実行
            cursor = db.rawQuery(sql , new String[]{email,password});

            if(!readCursor(cursor).isEmpty()) {
                //一致するデータが1件以上あれば
                cursor.moveToFirst();
                JSONObject jsonObj = new JSONObject();
                for (String collumn : cursor.getColumnNames()) {
                    @SuppressLint("Range")
                    String str = cursor.getString(cursor.getColumnIndex(collumn));
                    //System.out.println("cursor[]:"+cursor.getString(cursor.getColumnIndex(collumn)));
                    jsonObj.put(collumn, str);
                }
                loggedInUser = createLoggedInUser(jsonObj);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally{
            // Cursorを忘れずにcloseする
            if( cursor != null ){
                cursor.close();
            }
        }
        return loggedInUser;
    }

    public LoggedInUser createLoggedInUser(JSONObject jsonObject) throws JSONException {
        String id   = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        return new LoggedInUser(id,name);
    }

    public Member createMemberData(JSONObject jsonObject) throws JSONException {
        Long id = Long.valueOf(jsonObject.getString("id"));
        String email = jsonObject.getString("email");
        String name = jsonObject.getString("name");
        String password = jsonObject.getString("password");
        //LocalDateTime deleteDateTime = LocalDateTime.parse(jsonObject.getString("deleteDateTime"));
        //Integer version = Integer.valueOf(jsonObject.getString("version"));
        return new Member(id,email,name,password,null,null);
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


    //会員情報登録1 会員情報取得
    //同じメールアドレスで既存の有効な会員がいないか
    public Member searchMember(String columData, String columName) {
        Member member = null;
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try {
            //SQL文
            String sql = "SELECT * FROM " + MEMBER_TABLE_NAME
                    + " WHERE " + columName + "= ?"
                    + " AND deleteDateTime IS NULL;";

            //SQL文の実行
            cursor = db.rawQuery(sql, new String[]{columData});
            if(!readCursor(cursor).isEmpty()) {
                //一致するデータが1件以上あれば
                cursor.moveToFirst();
                JSONObject jsonObj = new JSONObject();
                for (String collumn : cursor.getColumnNames()) {
                    @SuppressLint("Range")
                    String str = cursor.getString(cursor.getColumnIndex(collumn));
                    jsonObj.put(collumn, str);
                }
                member = createMemberData(jsonObj);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally{
            // Cursorを忘れずにcloseする
            if( cursor != null ){
                cursor.close();
            }
        }
        return member;
    }

    //会員情報登録2
    public boolean insertData(String name, String password, String email){
        boolean result = false;

        String sql = "insert into " + MEMBER_TABLE_NAME
                + " (name,password,email,enabled) values(?,?,?,true);";
        String[] bindStr = new String[]{name, password, email};
        try {
            db.execSQL(sql, bindStr);
            result = true;
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return result;
    }

    public boolean updateEmail(String email,String id){
        boolean result = false;

        String sql = "update " + MEMBER_TABLE_NAME
                + " SET email =?"
                + " WHERE id = ?"
                + " AND deleteDateTime IS NULL;";
        String[] bindStr = new String[]{email, id};
        try {
            db.execSQL(sql,bindStr);
            result = true;
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return result;
    }

    public boolean updatePass(String password1,String id,String password0){
        boolean result = false;

        String sql = "update " + MEMBER_TABLE_NAME
                + " SET password =?"
                + " WHERE id = ?"
                + " AND password = ?"
                + " AND deleteDateTime IS NULL;";
        String[] bindStr = new String[]{password1, id, password0};
        try {
            db.execSQL(sql,bindStr);
            result = true;
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return result;
    }

    public boolean updateEtc(String name,String id){
        boolean result = false;

        String sql = "update " + MEMBER_TABLE_NAME
                + " SET name =?"
                + " WHERE id = ?"
                + " AND deleteDateTime IS NULL;";
        String[] bindStr = new String[]{name, id};
        try {
            db.execSQL(sql,bindStr);
            result = true;
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return result;
    }

    //物理削除
    public boolean deleteData(String id){
        boolean result = false;
        // ContentValuesのインスタンスにデータを格納
        String sql = "DELETE FROM " + MEMBER_TABLE_NAME
                + " WHERE id = ?;";
        String[] bindStr = new String[]{id};
        try {
            db.execSQL(sql,bindStr);
            result = true;
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return result;
    }

    public boolean validateData(String inputTitle, String inputPlace){
        //何かValid処理
        return true;
    }
}



