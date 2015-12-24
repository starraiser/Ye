package com.example.administrator.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DBManager {

    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private ArrayList<Item> list;

    public DBManager(Context context){
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void addwithPic(Item item){
        db.execSQL("insert into Record values(null,?,?,?,?)",
                new Object[]{item.getDate(),item.getTitle(),item.getContent(),item.getPhotoPath()});
    }

    public void addwithoutPic(Item item){
        db.execSQL("insert into Record values(null,?,?,?,null)",
                new Object[]{item.getDate(),item.getTitle(),item.getContent()});
    }

    public void update(Item item){
        db.execSQL("update Record set date=?,title=?,content=?,imgpath=? where _id=?",
                new Object[]{item.getDate(),item.getTitle(),item.getContent(),item.getPhotoPath(),item.getId()});
    }

    public void delete(int id){
        db.execSQL("delete from Record where _id=?",new Object[]{id});
    }

    public Item query(int id) {
        Item item = new Item();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select * from Record where _id=?",new String[]{Integer.toString(id)});
        while(cursor.moveToNext()){
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String time = cursor.getString(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String path = cursor.getString(cursor.getColumnIndex("imgpath"));

            item = new Item(_id, time, title, content, path);

        }
        return item;
    }
    public List<Item> getList(){
        list = new ArrayList<Item>();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select * from Record", null);
        while(cursor.moveToNext()){
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String time = cursor.getString(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String path = cursor.getString(cursor.getColumnIndex("imgpath"));

            Item item = new Item(_id, time, title, content, path);

            list.add(item);
        }
        cursor.close();
        return list;
    }
}
