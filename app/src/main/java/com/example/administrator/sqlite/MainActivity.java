package com.example.administrator.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBase();
        setContentView(R.layout.activity_main);
    }

    public void initDataBase(){
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE,null);
        //db.execSQL("DROP TABLE IF EXISTS Record");

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "Record (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, content TEXT);");
        try {
            db.execSQL("INSERT INTO Record VALUES (NULL,'AAA','AAA');");
            db.execSQL("INSERT INTO Record VALUES (NULL,'BBB','BBB');");
        }catch (Exception e){
            e.printStackTrace();
        }
        ContentValues cv = new ContentValues();
        Cursor c =db.rawQuery("SELECT * FROM Record WHERE title = ?",new String[]{"AAA"});
        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("_id"));
            String title = c.getString(c.getColumnIndex("title"));
            String content = c.getString(c.getColumnIndex("content"));
            System.out.println(_id+"        "+title+"          "+content);
        }
        c.close();
    }
}
