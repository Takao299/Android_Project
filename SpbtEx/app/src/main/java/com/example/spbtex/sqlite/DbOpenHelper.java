package com.example.spbtex.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

    public static final Boolean STAND_ALONE = false;

    private static final String TAG = "DbOpenHelper";
    private static final String DATABASE_NAME = "chotecho.db";
    private static final String MEMBER_TABLE_NAME = "member";
    private static final String FACILITY_TABLE_NAME = "facility";
    private static final String RESERVATION_TABLE_NAME = "reservation";
    private static final String FILES_TABLE_NAME = "files";
    private static final int DATABASE_VERSION = 19;

    public DbOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        createMemberTable(db);
        createMemberTableData(db);
        createFacilityTable(db);
        createFacilityTableData(db);
        createReservationTable(db);
        createFilesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + FACILITY_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + RESERVATION_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + FILES_TABLE_NAME + ";");
        onCreate(db);
    }

    private void createMemberTable(SQLiteDatabase db){
        // テーブル作成SQL
        String sql = "CREATE TABLE member ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " email TEXT,"
                + " name TEXT,"
                + " password TEXT,"
                + " deleteDateTime TEXT,"
                + " version INTEGER"
                + ");";
        db.execSQL(sql);
        Log.i(TAG,"テーブルmemberが作成されました");
    }

    private void createMemberTableData(SQLiteDatabase db){
        // テーブルデータ作成SQL
        db.execSQL("INSERT INTO member(email, name, password, version) VALUES('member3@a.a','test_user','aaaa1111',0);");
        Log.i(TAG,"テーブルmemberデータが作成されました");
    }

    private void createFacilityTable(SQLiteDatabase db){
        // テーブル作成SQL
        String sql = "CREATE TABLE facility ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT,"
                + " amount INTEGER,"
                + " memo TEXT,"
                + " deleteDateTime TEXT,"
                + " deleteUser TEXT,"
                + " updateDateTime TEXT,"
                + " updateUser TEXT,"
                + " createDateTime TEXT,"
                + " createUser TEXT,"
                + " version INTEGER"
                + ");";
        db.execSQL(sql);
        Log.i(TAG,"テーブルfacilityが作成されました");
    }

    private void createFacilityTableData(SQLiteDatabase db){
        // テーブルデータ作成SQL
        db.execSQL("INSERT INTO facility(name, amount, memo, version) VALUES('facility1',1,'20人用',0);");
        db.execSQL("INSERT INTO facility(name, amount, memo, version) VALUES('facility2',1,'給湯室あり',0);");
        db.execSQL("INSERT INTO facility(name, amount, memo, version) VALUES('facility3',1,'１７時まで',0);");
        db.execSQL("INSERT INTO facility(name, amount, memo, version) VALUES('エアロバイク',2,'テスト文章　テスト',0);");
        db.execSQL("INSERT INTO facility(name, amount, memo, version) VALUES('ランニング',3," +
                "'改行テスト\n" +
                "改行A\n" +
                "改行B1　B2',0);");
        Log.i(TAG,"テーブルfacilityデータが作成されました");
    }

    private void createReservationTable(SQLiteDatabase db){
        // テーブル作成SQL
        String sql = "CREATE TABLE reservation ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " member_id INTEGER,"
                + " facility_id INTEGER,"
                + " rday INTEGER,"
                + " rstart INTEGER,"
                + " rend INTEGER,"
                + " deleteDateTime TEXT"
                + ");";
        db.execSQL(sql);
        Log.i(TAG,"テーブルreservationが作成されました");
    }

    private void createFilesTable(SQLiteDatabase db){
        // テーブル作成SQL
        String sql = "CREATE TABLE files ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " afId INTEGER,"
                + " foreignId INTEGER,"
                + " fileName TEXT,"
                + " createTime TEXT,"
                + " delete_pic TEXT,"
                + " deleteDateTime TEXT"
                + ");";
        db.execSQL(sql);
        Log.i(TAG,"テーブルfilesが作成されました");
    }

}


