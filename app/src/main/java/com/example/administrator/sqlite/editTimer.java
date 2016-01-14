package com.example.administrator.sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.sqlite.Adapter.BVAdapter;
import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.Item;
import com.example.administrator.sqlite.entity.Timer;
import com.tandong.sa.bv.BottomView;
import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class editTimer extends Activity {

    private DBManager database;

    final private int NEW_ITEM = 0;
    final private int UPDATE_ITEM = 1;


    private TextView title;
    private TextView content;
    private Button confirmTimer;
    private Button takePhoto;
    private ImageView photo;

    private BottomView bottomView;

    private DatePicker datePicker;
    private String curTime;
    private int targetYear;
    private int targetMonth;
    private int targetDay;
    private String targetTime = "";
    private String tempTime="";
    private String savePath = "mnt/sdcard/Ye/";
    private String photoPath;

    private int userId;
    private int id;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        database = new DBManager(this);

        title = (TextView)findViewById(R.id.editTimerTitle);
        content = (TextView)findViewById(R.id.editTimerContent);
        confirmTimer = (Button)findViewById(R.id.confirmTimer);
        takePhoto = (Button)findViewById(R.id.takePhoto);
        photo = (ImageView)findViewById(R.id.photo);
        datePicker = (DatePicker)findViewById(R.id.datePicker);

        SharedPreferences sharedPreferences = getSharedPreferences("info",Activity.MODE_PRIVATE);  // 获取当前用户id
        userId = sharedPreferences.getInt("userId",-1);

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

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        curTime = formatter.format(curDate);

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = new Date(System.currentTimeMillis());
        tempTime = formatter2.format(curDate);

        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1,d2;
        d1=new Date();
        d2=new Date();
        try {
            d1 = df.parse("2015-01-01");
            d2 = df.parse("2016-01-01");
        }catch (ParseException e){
            e.printStackTrace();
        }
        //long time = (d2.getTime()-d1.getTime())/(1000*60*60*24);

        //content.setText(Long.toString(time));
        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                targetYear = year;
                targetMonth = monthOfYear+1;
                targetDay = dayOfMonth;
            }
        });

        confirmTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetTime="";
                targetTime += Integer.toString(targetYear);
                if(targetMonth<10){
                    targetTime +="0"+Integer.toString(targetMonth);
                } else{
                    targetTime +=Integer.toString(targetMonth);
                }
                if(targetDay<10){
                    targetTime +="0"+Integer.toString(targetDay);
                } else{
                    targetTime +=Integer.toString(targetDay);
                }
                if(targetTime.equals("00000")){
                    AlertDialog noContent = new AlertDialog.Builder(editTimer.this).create();
                    noContent.setTitle("提示");
                    noContent.setMessage("时间错误！");
                    noContent.show();
                }else {
                    if (0 == content.length()) {  // 没有正文
                        AlertDialog noContent = new AlertDialog.Builder(editTimer.this).create();
                        noContent.setTitle("提示");
                        noContent.setMessage("未输入内容！");
                        noContent.show();
                    } else if (0 == title.length()) {  // 有正文但没有题目,将正文前10个字设成题目
                        String tempTitle;
                        String tempContent = content.getText().toString();

                        if (content.length() > 10) {
                            tempTitle = tempContent.substring(0, 10);
                        } else {
                            tempTitle = tempContent.substring(0, content.length());
                        }

                        if (NEW_ITEM == flag) {  // 新建记录
                            Timer timer = new Timer(userId, curTime, targetTime, tempTitle, tempContent, photoPath);
                            database.addTimer(timer);
                        } else {  // 更新记录
                            Timer timer = new Timer(userId, curTime, targetTime, tempTitle, tempContent, photoPath);
                            database.updateTimer(timer);
                        }

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {  // 题目和正文都有
                        String tempTitle = title.getText().toString();
                        String tempContent = content.getText().toString();

                        if (NEW_ITEM == flag) {  // 新建记录
                            Timer timer = new Timer(userId, curTime, targetTime, tempTitle, tempContent, photoPath);
                            database.addTimer(timer);
                        } else {  // 更新记录
                            Timer timer = new Timer(userId, curTime, targetTime, tempTitle, tempContent, photoPath);
                            database.updateTimer(timer);
                        }

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {  // 拍照
            @Override
            public void onClick(View v) {

                ListView choiceList;
                final ArrayList<String> menus = new ArrayList<String>();
                menus.add("拍摄照片");
                menus.add("从文件中选择");

                bottomView = new BottomView(editTimer.this,R.style.BottomViewTheme_Defalut,R.layout.bottom_view);
                bottomView.setAnimation(R.style.BottomToTopAnim);
                bottomView.showBottomView(true);

                choiceList = (ListView)bottomView.getView().findViewById(R.id.lv_list);
                BVAdapter adapter = new BVAdapter(editTimer.this,menus);
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

                            photoPath=savePath + dateToNum(curTime) + ".jpg";
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
                photoPath=savePath + dateToNum(curTime) + ".jpg";
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
        Timer timer = database.queryTimer(id);
        title.setText(timer.getTitle());
        content.setText(timer.getContent());
        String photoStr = timer.getPhotoPath();
        photoPath = photoStr;

        String tmpTime = timer.getTimerTime();
        String tmpY=tmpTime.substring(0, 4);
        String tmpM=tmpTime.substring(4, 6);
        String tmpD=tmpTime.substring(5,7);
        int Y = Integer.parseInt(tmpY);
        int M = Integer.parseInt(tmpM);
        int D = Integer.parseInt(tmpD);
        /*
        System.out.println(tmpY + tmpM + tmpD);
        datePicker.init(Y, M, D, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                targetYear = year;
                targetMonth = monthOfYear + 1;
                targetDay = dayOfMonth;
            }
        });*/
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
