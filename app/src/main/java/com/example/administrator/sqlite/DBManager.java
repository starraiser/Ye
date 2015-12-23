package com.example.administrator.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DBManager {

    private DBOpenHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context){
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void add(Item item){

    }

    public void delete(Item item){

    }
}
