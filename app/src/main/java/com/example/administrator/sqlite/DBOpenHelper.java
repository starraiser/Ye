package com.example.administrator.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "my3.db";
    private static int DB_VERSION = 4;

    public DBOpenHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        //db.execSQL("DROP TABLE Record");
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "Record (_id INTEGER PRIMARY KEY AUTOINCREMENT,userId INTEGER, " +
                "date TEXT,title TEXT,content TEXT,imgpath TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "USER (_id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, password TEXT);");

        String name = "叶慧靖";
        String password = "19931030";
        db.execSQL("insert into USER values(null,?,?)",
                new Object[]{name, password});

        name="test";
        password="123456";
        db.execSQL("insert into USER values(null,?,?)",
                new Object[]{name, password});

        name="liang";
        password="123456";
        db.execSQL("insert into USER values(null,?,?)",
                new Object[]{name, password});

        name="haha";
        password="123456";
        db.execSQL("insert into USER values(null,?,?)",
                new Object[]{name, password});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
}
