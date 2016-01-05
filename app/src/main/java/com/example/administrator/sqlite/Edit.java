package com.example.administrator.sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoView;

public class Edit extends Activity {

    private SQLiteDatabase db;
    private DBManager database;

    private Button takePhoto;
    private Button confirm;
    private EditText editTitle;
    private EditText editContent;
    private ImageView photo;

    private String time;
    private String savePath="mnt/sdcard/Ye/";
    private String photoPath="";

    private int id;
    private int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new);

        database = new DBManager(this);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        time = formatter.format(curDate);

        confirm = (Button)findViewById(R.id.confirm);
        takePhoto = (Button)findViewById(R.id.takePhoto);
        editTitle = (EditText)findViewById(R.id.editTitle);
        editContent = (EditText)findViewById(R.id.editContent);
        photo = (ImageView)findViewById(R.id.photo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            flag = bundle.getInt("flag");
            id = bundle.getInt("id");
            if (flag == 1) {
                init(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }

                photoPath=savePath + dateToNum(time) + ".jpg";
                File photo = new File(photoPath);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, 1);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有正文
                if(0==editContent.length()){
                    AlertDialog noContent = new AlertDialog.Builder(Edit.this).create();
                    noContent.setTitle("提示");
                    noContent.setMessage("未输入内容！");
                    noContent.show();
                }
                //有正文但没有题目,将正文前10个字设成题目
                else if(0==editTitle.length()){
                    String tempTitle;
                    String tempContent=editContent.getText().toString();
                    if(editContent.length()>10){
                        tempTitle=tempContent.substring(0, 10);
                    }
                    else{
                        tempTitle=tempContent.substring(0, editContent.length());
                    }
                    if(flag==0){
                        Item item = new Item(time,tempTitle,tempContent,photoPath);
                        database.addwithPic(item);
                    }else{
                        Item item = new Item(id,time,tempTitle,tempContent,photoPath);
                        database.update(item);
                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                //题目和正文都有
                else{
                    String tempTitle=editTitle.getText().toString();
                    String tempContent=editContent.getText().toString();

                    if(flag==0) {
                        Item item = new Item(time, tempTitle, tempContent, photoPath);
                        database.addwithPic(item);
                    }else{
                        Item item = new Item(id,time,tempTitle,tempContent,photoPath);
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
            try {/*
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
                ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getApplicationContext());
                ImageLoader.getInstance().init(config);
                ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);
            }catch (Exception e){

            }
        }
    }

    public void init(int id){
        Item item = database.query(id);
        editTitle.setText(item.getTitle());
        editContent.setText(item.getContent());
        String photoStr = item.getPhotoPath();
        photoPath=photoStr;
        if(photoStr!=null) {
            try {/*
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

                ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getApplicationContext());
                ImageLoader.getInstance().init(config);
                ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public String dateToNum(String date){
        String num="";
        for(int i=0;i<date.length();i++){
            if(date.charAt(i)>='0'&&date.charAt(i)<='9'){
                num+=date.charAt(i);
            }
        }
        return num;
    }
}
