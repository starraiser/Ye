package com.example.administrator.sqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "test.db";
    private static int DB_VERSION = 1;

    public DBOpenHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "USER (_id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, password TEXT);");  // 用户表

        db.execSQL("CREATE TABLE IF NOT EXISTS CACHE (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userName TEXT, password TEXT, flag INTEGER, auto INTEGER);");  // 登录信息缓存表

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "Record (_id INTEGER PRIMARY KEY AUTOINCREMENT,userId INTEGER, " +
                "date TEXT,title TEXT,content TEXT,imgpath TEXT);");  // 记录表

        db.execSQL("CREATE TABLE IF NOT EXISTS Timer (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, createDate TEXT, timerDate TEXT, " +
                "title TEXT, content TEXT,imgPath TEXT);");  // 倒计时记录表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
}
