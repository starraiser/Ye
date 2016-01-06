package com.example.administrator.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.sqlite.entity.User;
import com.example.administrator.sqlite.entity.Item;

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

    public void addCache(String name){
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select _id from USER where userName=?",new String[]{name});
        if(true){
            db.execSQL("insert into CACHE values(null,?)",new String[]{name});
        }
        else{
            cv.put("userName", name);
            //db.update("CACHE",cv,"_id=?",new String[]{Integer.toString(1)});
            db.execSQL("update CACHE set userName=? where _id=?",new String[]{name,"1"});
        }
    }

    public String getCache(){
        String name="";
        ContentValues cv = new ContentValues();
        Cursor tempcursor = db.rawQuery("select _id from USER",null);
        int count = tempcursor.getCount();
        Cursor cursor = db.rawQuery("select userName from USER where _id=?",new String[]{Integer.toString(count)});
        while(cursor.moveToNext()){
            name=cursor.getString(cursor.getColumnIndex("userName"));
        }

        return name;
    }
    public void addUser(User user){  // 添加用户
        String name = user.getUserName();
        String password = user.getPassword();
        db.execSQL("insert into USER values(null,?,?)",
                new Object[]{name, password});
    }

    public boolean checkUser(String name, String password){  // 检查用户是否存在or密码是否正确
        String userPassword="";
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select * from USER where userName=?",new String[]{name});

        if(cursor.getCount() == 0){  // 判断是否存在该用户
            return false;
        }

        while(cursor.moveToNext()){
            userPassword = cursor.getString(cursor.getColumnIndex("password"));  // 获取用户名对应密码
        }

        if(userPassword.equals(password)){  // 密码正确
            return true;
        } else{  // 密码错误
            return false;
        }
    }

    public int getIdByName(String name){  // 通过用户名查找用户id
        int id=-1;
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select _id from USER where userName=?",new String[]{name});
        while(cursor.moveToNext()){
            id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        return id;
    }

    public void addwithPic(Item item){  // 增加有图片记录
        db.execSQL("insert into Record values(null,?,?,?,?,?)",
                new Object[]{item.getUserId(),item.getDate(),item.getTitle(),item.getContent(),item.getPhotoPath()});
    }

    public void addwithoutPic(Item item){  // 增加无图片记录
        db.execSQL("insert into Record values(null,?,?,?,?,null)",
                new Object[]{item.getUserId(),item.getDate(),item.getTitle(),item.getContent()});
    }

    public void update(Item item){  // 更新记录
        db.execSQL("update Record set date=?,title=?,content=?,imgpath=? where _id=?",
                new Object[]{item.getDate(), item.getTitle(), item.getContent(), item.getPhotoPath(), item.getId()});
    }

    public void delete(int id){  // 删除记录
        db.execSQL("delete from Record where _id=?",new Object[]{id});
    }

    public Item query(int id) {  // 获取单条记录
        Item item = new Item();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select * from Record where _id=?",new String[]{Integer.toString(id)});
        while(cursor.moveToNext()){
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int userId = cursor.getInt(cursor.getColumnIndex("userId"));
            String time = cursor.getString(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String path = cursor.getString(cursor.getColumnIndex("imgpath"));

            item = new Item(_id, time, title, content, path);

        }
        return item;
    }

    public List<Item> getListOfUser(int userId){  // 获取所有记录
        list = new ArrayList<Item>();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select * from Record where userId=?", new String[]{Integer.toString(userId)});
        while(cursor.moveToNext()){
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int _userId = cursor.getInt(cursor.getColumnIndex("userId"));
            String time = cursor.getString(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String path = cursor.getString(cursor.getColumnIndex("imgpath"));

            Item item = new Item(_id, userId, time, title, content, path);

            list.add(item);
        }
        cursor.close();
        return list;
    }
}
