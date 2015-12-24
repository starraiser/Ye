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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private DBManager database;

    private List<Item> listData;
    private ListView listView;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=new DBManager(this);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //initDataBase();

        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list);
        Button newItem = (Button)findViewById(R.id.newItem);

        adapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int id = database.getList().get(arg2).getId();
                //System.out.println(id);
                Intent intent = new Intent(MainActivity.this,showRecord.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        listView.setAdapter(adapter);
        newItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Edit.class);
                startActivityForResult(intent,1);
            }
        });
    }

    private List<Map<String ,Object>> getData(){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        //Map<String,Object> map = new HashMap<String,Object>();
        listData = database.getList();
        for(int i = 0; i < listData.size(); i++){
            Map<String,Object> map = new HashMap<String,Object>();
            int id = listData.get(i).getId();
            String title = listData.get(i).getTitle();
            String time = listData.get(i).getDate();
            map.put("title",title);
            map.put("time",time);
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_CANCELED == resultCode){
            return;
        }

        SimpleAdapter newadapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        listView.setAdapter(newadapter);

        if(1==requestCode){
            //System.out.println("aaaaaaaaaaaaaaaaaaaa");
            //SimpleAdapter newadapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
            //        new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
            //listView.setAdapter(newadapter);
        }
    }

    protected void onResume(){
        super.onResume();
        System.out.println("resumeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        SimpleAdapter newadapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        listView.setAdapter(newadapter);
    }
    public void initDataBase(){

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
