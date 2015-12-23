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

    public void addwithPic(Item item){
        db.execSQL("insert int Record values(null,?,?,?)",
                new Object[]{item.getTitle(),item.getContent(),item.getPhotoPath()});
    }

    public void addwithoutPic(Item item){
        db.execSQL("insert int Record values(null,?,?,null)",
                new Object[]{item.getTitle(),item.getContent()});
    }

    public void update(Item item){
        db.execSQL("update Record set title=?,content=?,imgpath=? where _id=?",
                new Object[]{item.getTitle(),item.getContent(),item.getPhotoPath(),item.getId()});
    }

    public void delete(int id){
        db.execSQL("delete from Record where _id=?",new Object[]{id});
    }
}
