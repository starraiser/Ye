/**
 * @author 梁嘉升
 * 主页面
 */
package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private DBManager database;

    private ListView listView;
    private Button newItem;

    private List<Item> listData;  // 数据
    private SimpleAdapter adapter;  // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DBManager(this);

        listView = (ListView)findViewById(R.id.list);
        newItem = (Button)findViewById(R.id.newItem);

        adapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // 列表监听器
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int id = database.getList().get(arg2).getId();  // 获取点击列表中的数据的id

                Intent intent = new Intent(MainActivity.this,showRecord.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);  // 传递id到显示页面
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });

        listView.setAdapter(adapter);  // 设置适配器

        newItem.setOnClickListener(new View.OnClickListener() {  // 新建按钮监听器
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Edit.class);
                startActivityForResult(intent,1);
            }
        });
    }

    private List<Map<String ,Object>> getData(){  // 获取数据库记录
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        listData = database.getList();
        for(int i = 0; i < listData.size(); i++){
            Map<String,Object> map = new HashMap<String,Object>();

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

        //SimpleAdapter newadapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
        //        new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        //listView.setAdapter(newadapter);

        if(1 == requestCode){

        }
    }

    protected void onResume(){  // 重新设置适配器，刷新数据
        super.onResume();
        SimpleAdapter newAdapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        listView.setAdapter(newAdapter);
    }

}