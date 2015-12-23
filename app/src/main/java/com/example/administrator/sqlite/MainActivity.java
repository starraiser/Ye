package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private DBManager database;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //initDataBase();

        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list);

        Button newItem = (Button)findViewById(R.id.newItem);
        newItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Edit.class);
                startActivity(intent);
            }
        });
    }

    public void initDataBase(){
        //db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE,null);
        //db.execSQL("DROP TABLE IF EXISTS Record");

        //db.execSQL("CREATE TABLE IF NOT EXISTS " +
        //        "Record (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        //        "title TEXT, content TEXT, img BLOB);");
        /*try {
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
        c.close();*/
    }
}
