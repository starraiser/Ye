/**
 * 编辑页面
 */

package com.example.administrator.sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.administrator.sqlite.Adapter.BVAdapter;
import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.Item;
import com.tandong.sa.bv.BottomView;

import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

public class Edit extends Activity {

    private DBManager database;
    private int userId;

    final private int NEW_ITEM = 0;
    final private int UPDATE_ITEM = 1;

    private Button takePhoto;
    private Button confirm;
    private EditText editTitle;
    private EditText editContent;
    private ImageView photo;
    private BottomView bottomView;

    private String time;
    private String savePath="mnt/sdcard/Ye/";
    private String photoPath="";

    private int id;
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new);
        ActivityTaskManager.getInstance().putActivity("Edit", this);

        database = new DBManager(this);

        SharedPreferences sharedPreferences = getSharedPreferences("info",Activity.MODE_PRIVATE);  // 获取当前用户id
        userId = sharedPreferences.getInt("userId",-1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        time = formatter.format(curDate);

        confirm = (Button)this.findViewById(R.id.confirm);
        takePhoto = (Button)findViewById(R.id.takePhoto);
        editTitle = (EditText)findViewById(R.id.editTitle);
        editContent = (EditText)findViewById(R.id.editContent);
        photo = (ImageView)findViewById(R.id.photo);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            flag = bundle.getInt("flag");
            id = bundle.getInt("id");
            System.out.println(flag);
            if (UPDATE_ITEM == flag) {  // 判断是修改记录还是新建记录
                init(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {  // 拍照
            @Override
            public void onClick(View v) {/*
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }

                photoPath=savePath + dateToNum(time) + ".jpg";
                File photo = new File(photoPath);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, 1);*/
                ListView choiceList;
                final ArrayList<String> menus = new ArrayList<String>();
                menus.add("拍摄照片");
                menus.add("从文件中选择");

                bottomView = new BottomView(Edit.this,R.style.BottomViewTheme_Defalut,R.layout.bottom_view);
                bottomView.setAnimation(R.style.BottomToTopAnim);
                bottomView.showBottomView(true);

                choiceList = (ListView)bottomView.getView().findViewById(R.id.lv_list);
                BVAdapter adapter = new BVAdapter(Edit.this,menus);
                choiceList.setAdapter(adapter);

                choiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String s_menu = menus.get(arg2);
                        if (s_menu.contains("拍摄照片")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            File file = new File(savePath);
                            if (!file.exists()) {
                                file.mkdir();
                            }

                            photoPath=savePath + dateToNum(time) + ".jpg";
                            File photo = new File(photoPath);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            startActivityForResult(intent, 1);
                        } else if (s_menu.contains("从文件中选择")) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent,2);
                        }
                        bottomView.dismissBottomView();

                    }
                });
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 点击图片进入查看大图页面
                Intent intentToPic = new Intent();
                Bundle bundleToPic = new Bundle();
                intentToPic.setClass(Edit.this, completePic.class);
                bundleToPic.putString("photoPath", photoPath);
                intentToPic.putExtras(bundleToPic);
                startActivity(intentToPic);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == editContent.length()) {  // 没有正文
                    AlertDialog noContent = new AlertDialog.Builder(Edit.this).create();
                    noContent.setTitle("提示");
                    noContent.setMessage("未输入内容！");
                    noContent.show();
                } else if (0 == editTitle.length()) {  // 有正文但没有题目,将正文前10个字设成题目
                    String tempTitle;
                    String tempContent = editContent.getText().toString();

                    if (editContent.length() > 10) {
                        tempTitle = tempContent.substring(0, 10);
                    } else {
                        tempTitle = tempContent.substring(0, editContent.length());
                    }

                    if (NEW_ITEM == flag) {  // 新建记录
                        Item item = new Item(userId, time, tempTitle, tempContent, photoPath);
                        database.addwithPic(item);
                    } else {  // 更新记录
                        Item item = new Item(id, userId, time, tempTitle, tempContent, photoPath);
                        database.update(item);
                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {  // 题目和正文都有
                    String tempTitle = editTitle.getText().toString();
                    String tempContent = editContent.getText().toString();

                    if (NEW_ITEM == flag) {  // 新建记录
                        Item item = new Item(userId, time, tempTitle, tempContent, photoPath);
                        database.addwithPic(item);
                    } else {  // 更新记录
                        Item item = new Item(id, userId, time, tempTitle, tempContent, photoPath);
                        database.update(item);
                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_CANCELED == resultCode){
            return;
        }
        if(1==requestCode){
            /*
                FileInputStream file = new FileInputStream(photoPath);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                //为位图设置100K的缓存
                opts.inTempStorage = new byte[100 * 1024];
                //设置位图颜色显示优化方式
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                //设置图片可以被回收
                opts.inPurgeable = true;
                //设置位图缩放比例
                opts.inSampleSize = 4;
                //设置解码位图的尺寸信息
                opts.inInputShareable = true;
                //解码位图
                System.out.println();
                Bitmap bitmap1 = BitmapFactory.decodeStream(file, null, opts);
                photo.setImageBitmap(bitmap1);*/
            ImageLoaderConfiguration config =
                    ImageLoaderConfiguration.createDefault(getApplicationContext());  // 配置
            ImageLoader.getInstance().init(config);  // 初始化
            ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);  // 载入图片
        }
        else if(2 == requestCode){
            Uri uri = data.getData();
            AssetFileDescriptor afd;
            try{
                afd = getContentResolver().openAssetFileDescriptor(uri,"r");
                byte[] buffer = new byte[16*1024];
                FileInputStream fis = afd.createInputStream();
                photoPath=savePath + dateToNum(time) + ".jpg";
                FileOutputStream fos = new FileOutputStream(new File(photoPath));
                ByteArrayOutputStream temp_byte = new ByteArrayOutputStream();
                int size;
                while((size=fis.read(buffer))!=-1){
                    fos.write(buffer,0,size);
                    temp_byte.write(buffer,0,size);
                }
                ImageLoaderConfiguration config =
                        ImageLoaderConfiguration.createDefault(getApplicationContext());  // 配置
                ImageLoader.getInstance().init(config);  // 初始化
                ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);  // 载入图片
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println(uri);
        }
    }

    public void init(int id){  // 初始化页面
        Item item = database.query(id);

        editTitle.setText(item.getTitle());
        editContent.setText(item.getContent());
        String photoStr = item.getPhotoPath();
        photoPath = photoStr;

        if(photoStr!=null) {
            /*
                FileInputStream file = new FileInputStream(photoStr);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                //为位图设置100K的缓存
                opts.inTempStorage = new byte[100 * 1024];
                //设置位图颜色显示优化方式
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                //设置图片可以被回收
                opts.inPurgeable = true;
                //设置位图缩放比例
                opts.inSampleSize = 4;
                //设置解码位图的尺寸信息
                opts.inInputShareable = true;
                //解码位图

                Bitmap bitmap1 = BitmapFactory.decodeStream(file, null, opts);

                photo.setImageBitmap(bitmap1);*/

            ImageLoaderConfiguration config =
                    ImageLoaderConfiguration.createDefault(getApplicationContext());  //配置
            ImageLoader.getInstance().init(config);  // 初始化
            ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);  // 载入图片
        }
    }

    public String dateToNum(String date){  // 提取时间中的数字作为文件名
        String num="";
        for(int i=0;i<date.length();i++){
            if(date.charAt(i)>='0'&&date.charAt(i)<='9'){
                num+=date.charAt(i);
            }
        }
        return num;
    }
}
