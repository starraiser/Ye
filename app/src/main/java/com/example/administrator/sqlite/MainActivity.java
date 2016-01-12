/**
 * @author 梁嘉升
 * 主页面
 */
package com.example.administrator.sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.Item;
import com.tandong.sa.avatars.AvatarDrawableFactory;
import com.slidingmenu.lib.SlidingMenu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private DBManager database;
    private int userId;

    private ListView listView;
    private Button newItem;
    private ImageView avatar;
    private ImageView slidingAvatar;

    private List<Item> listData;  // 数据
    private SimpleAdapter adapter;  // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityTaskManager.getInstance().putActivity("Main", this);
        //Login.temp.finish();  // finish登录页，否则返回键会返回到登录页

        database = new DBManager(this);

        SharedPreferences sharedPreferences = getSharedPreferences("info",Activity.MODE_PRIVATE);  // 获取当前用户id
        userId = sharedPreferences.getInt("userId",-1);

        listView = (ListView)findViewById(R.id.list);
        newItem = (Button)findViewById(R.id.newItem);
        avatar = (ImageView)findViewById(R.id.mainAvatar);
        //slidingAvatar = (ImageView)findViewById(R.id.slidingAvatar);

        BitmapFactory.Options options = new BitmapFactory.Options();  // 添加圆形头像
        options.inMutable = false;
        options.inSampleSize=4;
        AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getResources(),this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mumu, options);
        Drawable avatarDrawable = avatarDrawableFactory.getRoundedAvatarDrawable(bitmap);
        avatar.setImageDrawable(avatarDrawable);

        adapter = new SimpleAdapter(this,getData(),R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // 列表监听器
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int id = database.getListOfUser(userId).get(arg2).getId();  // 获取点击列表中的数据的id

                Intent intent = new Intent(MainActivity.this, showRecord.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);  // 传递id到显示页面
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        listView.setAdapter(adapter);  // 设置适配器

        newItem.setOnClickListener(new View.OnClickListener() {  // 新建按钮监听器
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Edit.class);
                Bundle bundleToEdit = new Bundle();
                bundleToEdit.putInt("flag", 0);  // 传递flag到编辑页面以判定是从此页面跳转到编辑页面
                intent.putExtras(bundleToEdit);
                startActivityForResult(intent, 1);
            }
        });

        final SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.setBehindWidth(300);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding);

        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                BitmapFactory.Options options2 = new BitmapFactory.Options();  // 添加圆形头像
                options2.inMutable = false;
                options2.inSampleSize = 4;
                slidingAvatar = (ImageView) findViewById(R.id.slidingAvatar);
                AvatarDrawableFactory avatarDrawableFactory2 = new AvatarDrawableFactory(getResources(), MainActivity.this);
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.mumu, options2);
                Drawable avatarDrawable2 = avatarDrawableFactory2.getRoundedAvatarDrawable(bitmap2);
                slidingAvatar.setImageDrawable(avatarDrawable2);

                ListView listView = (ListView) findViewById(R.id.slidingList);
                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                        getData2(), R.layout.slidinglist,
                        new String[]{"string"}, new int[]{R.id.func});
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // 列表监听器
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int num = arg2;
                        if(num==0){
                            ActivityTaskManager.getInstance().closeAllActivity();
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,Login.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
            }
        });
    }

    private List<Map<String ,Object>> getData(){  // 获取数据库记录
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        listData = database.getListOfUser(userId);
        for (int i = 0; i < listData.size(); i++){
            Map<String,Object> map = new HashMap<String,Object>();

            String title = listData.get(i).getTitle();
            String time = listData.get(i).getDate();
            map.put("title",title);
            map.put("time",time);
            list.add(map);
        }
        return list;
    }

    private List<Map<String ,String>> getData2(){  // 获取数据库记录
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        for (int i = 0; i < 1; i++){
            Map<String,String> map = new HashMap<String,String>();
            map.put("string","退出");
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

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){

            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("Warning")
                    .setMessage("确定要退出吗")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                            ActivityTaskManager.getInstance().closeAllActivity();  // 使用控制类完全退出程序
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.show();
            //Login.temp.finish();  // finish登录页，否则返回键会返回到登录页
            //finish();
            //SysApplication.getInstance().exit();  // 使用控制类完全退出程序
            return false;
        }
        return super.onKeyDown(KeyCode,event);
    }
}