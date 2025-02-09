package com.example.spbtex.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.spbtex.ui.facility.Facility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacilityModel {

    DbOpenHelper helper = null;
    SQLiteDatabase db = null;
    public static final String FACILITY_TABLE_NAME = "facility"; //searchRemainのSQL文は参照してないので注意

    List<Facility> facilityList = null;
    public List<Facility> getFacilityList() {
        return facilityList;
    }

    public FacilityModel(Context context){
        if(helper == null || db == null) {
            helper = new DbOpenHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    @SuppressLint("Range")
    public void readSql() { //public JSONArray readSql(String sql)
        SQLiteDatabase db = this.helper.getReadableDatabase();
        JSONArray jsonArr = new JSONArray();
        try {
            String sql    = "SELECT * FROM " + FACILITY_TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                JSONObject jsonObj = new JSONObject();
                for (String collumn : cursor.getColumnNames()) {
                    jsonObj.put(collumn, cursor.getString(cursor.getColumnIndex(collumn)));
                }
                jsonArr.put(jsonObj);
            }
            cursor.close();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            db.close();
            //return jsonArr;
            try {
                createList(jsonArr);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createList(JSONArray jarray) throws JSONException {

        List<Facility> fs = new ArrayList<Facility>();
        for (int i = 0; i < jarray.length(); ++ i) {
            JSONObject json = jarray.getJSONObject(i);
            Long id   = Long.valueOf(json.getString("id"));
            String name = json.getString("name");
            Integer amount = Integer.parseInt(json.getString("amount"));
            String memo ="memo"+i;
            if(!json.getString("memo").isEmpty()) memo = json.getString("memo");
            Facility facility = new Facility(id,name,amount,memo);
            fs.add(facility);
        }
        this.facilityList = fs;
    }

}



