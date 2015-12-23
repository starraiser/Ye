package com.example.administrator.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static String DB_NAME="test.db";
    private static int DB_VERSION=1;

    public DBOpenHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "Record (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, content TEXT, img BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
