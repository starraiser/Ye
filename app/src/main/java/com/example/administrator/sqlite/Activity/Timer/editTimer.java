/**
 * 编辑Timer记录
 */
package com.example.administrator.sqlite.Activity.Timer;

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

import com.example.administrator.sqlite.ActivityManager.ActivityTaskManager;
import com.example.administrator.sqlite.Adapter.BVAdapter;
import com.example.administrator.sqlite.R;
import com.example.administrator.sqlite.database.DBManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class editTimer extends Activity {

    private DBManager database;  // 数据库管理

    final private int NEW_ITEM = 0;  // 判断是新建记录还是更新记录
    final private int UPDATE_ITEM = 1;


    private TextView title;  // 标题
    private TextView content;  // 正文
    private Button confirmTimer;  // 确认按钮
    private Button takePhoto;  // 拍照按钮
    private ImageView photo;  // 显示图片
    private BottomView bottomView;  // 选择拍照/选择照片菜单
    private DatePicker datePicker;  // 时间拾取器

    private String curTime;  // 当前时间
    private int targetYear;  // 目标时间 - 年
    private int targetMonth;  // 目标时间 - 月
    private int targetDay;  // 目标时间 - 日
    private String targetTime = "";  // 目标时间 - 年月日
    private String savePath = "mnt/sdcard/Ye/";  // 文件夹路径
    private String photoPath;  // 文件全名（带路径）

    private int userId;
    private int id;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        ActivityTaskManager.getInstance().putActivity("editTimer", this);
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
            Intent intent = getIntent();  // 获取上一个activity传递的值
            Bundle bundle = intent.getExtras();
            flag = bundle.getInt("flag");
            id = bundle.getInt("id");
            System.out.println(flag);
            if (UPDATE_ITEM == flag) {  // 判断是修改记录还是新建记录
                init(id);  // 初始化页面内容
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // 获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        curTime = formatter.format(curDate);

        //SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        //Date tempDate = new Date(System.currentTimeMillis());
        //tempTime = formatter2.format(curDate);

        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        /*
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
        long time = (d2.getTime()-d1.getTime())/(1000*60*60*24);
        */

        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  // 时间拾取器监听
                targetYear = year;  // 修改年月日
                targetMonth = monthOfYear+1;  // 月份范围为0-11，故真实月份要+1
                targetDay = dayOfMonth;
            }
        });

        confirmTimer.setOnClickListener(new View.OnClickListener() {  // 确认按钮监听
            @Override
            public void onClick(View v) {

                targetTime = "";  // 初始化目标时间
                targetTime += Integer.toString(targetYear);
                if (targetMonth < 10) {  // 月若是个位数则补零
                    targetTime += "0" + Integer.toString(targetMonth);
                } else {
                    targetTime += Integer.toString(targetMonth);
                }
                if (targetDay < 10) {  // 日若是个位数则补零
                    targetTime += "0" + Integer.toString(targetDay);
                } else {
                    targetTime += Integer.toString(targetDay);
                }

                if (targetTime.equals("00000")) {  // 时间拾取器改变，即设置时间为当天时错误
                    AlertDialog noContent = new AlertDialog.Builder(editTimer.this).create();
                    noContent.setTitle("提示");
                    noContent.setMessage("不能选择当天！");
                    noContent.show();
                } else {
                    if (0 == content.length()) {  // 没有正文
                        AlertDialog noContent = new AlertDialog.Builder(editTimer.this).create();
                        noContent.setTitle("提示");
                        noContent.setMessage("未输入内容！");
                        noContent.show();
                    } else if (0 == title.length()) {  // 有正文但没有题目,将正文前10个字设成题目
                        String tempTitle;
                        String tempContent = content.getText().toString();

                        if (content.length() > 10) {  // 正文长度大于10，取前10个字符
                            tempTitle = tempContent.substring(0, 10);
                        } else {  // 正文长度小于10，全取
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
                        finish();  // 结束，返回主页面

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
                        finish();  // 结束，返回主页面

                    }
                }
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {  // 拍照
            @Override
            public void onClick(View v) {

                ListView choiceList;  // 初始化选择菜单
                final ArrayList<String> menus = new ArrayList<String>();
                menus.add("拍摄照片");
                menus.add("从文件中选择");

                bottomView = new BottomView(editTimer.this, R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
                bottomView.setAnimation(R.style.BottomToTopAnim);
                bottomView.showBottomView(true);  // 显示菜单

                choiceList = (ListView) bottomView.getView().findViewById(R.id.lv_list);
                BVAdapter adapter = new BVAdapter(editTimer.this, menus);  // 设置适配器
                choiceList.setAdapter(adapter);

                choiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String s_menu = menus.get(arg2);
                        if (s_menu.contains("拍摄照片")) {  // 拍照
                            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // 打开系统相机

                            File file = new File(savePath);  // 建立图片保存路径
                            if (!file.exists()) {
                                file.mkdir();
                            }

                            photoPath = savePath + dateToNum(curTime) + ".jpg";  //保存图片
                            File photo = new File(photoPath);

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // 打开系统相机
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));  // 打开系统相机
                            startActivityForResult(intent, 1);

                        } else if (s_menu.contains("从文件中选择")) {  // 选择照片
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 2);
                        }
                        bottomView.dismissBottomView();  // 隐藏菜单
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
        if(1 == requestCode){
            ImageLoaderConfiguration config =
                    ImageLoaderConfiguration.createDefault(getApplicationContext());  // 配置
            ImageLoader.getInstance().init(config);  // 初始化
            ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);  // 载入图片
        }
        else if(2 == requestCode){
            Uri uri = data.getData();
            AssetFileDescriptor afd;
            try{
                afd = getContentResolver().openAssetFileDescriptor(uri,"r");  // 保存选择的图片
                byte[] buffer = new byte[16*1024];
                FileInputStream fis = afd.createInputStream();
                photoPath=savePath + dateToNum(curTime) + ".jpg";
                FileOutputStream fos = new FileOutputStream(new File(photoPath));
                ByteArrayOutputStream temp_byte = new ByteArrayOutputStream();
                int size;
                while((size = fis.read(buffer)) != -1){
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
        }
    }

    public void init(int id){  // 初始化页面
        Timer timer = database.queryTimer(id);  // 获取Timer实例
        title.setText(timer.getTitle());  // 初始化标题
        content.setText(timer.getContent());  // 初始化正文
        String photoStr = timer.getPhotoPath();  // 获取图片路径
        photoPath = photoStr;

        /*
        String tmpTime = timer.getTimerTime();
        String tmpY=tmpTime.substring(0, 4);
        String tmpM=tmpTime.substring(4, 6);
        String tmpD=tmpTime.substring(5,7);
        int Y = Integer.parseInt(tmpY);
        int M = Integer.parseInt(tmpM);
        int D = Integer.parseInt(tmpD);

        System.out.println(tmpY + tmpM + tmpD);
        datePicker.init(Y, M, D, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                targetYear = year;
                targetMonth = monthOfYear + 1;
                targetDay = dayOfMonth;
            }
        });*/
        if(photoStr != null) {
            ImageLoaderConfiguration config =
                    ImageLoaderConfiguration.createDefault(getApplicationContext());  //配置
            ImageLoader.getInstance().init(config);  // 初始化
            ImageLoader.getInstance().displayImage("file:///" + photoPath, photo);  // 载入图片
        }
    }

    public String dateToNum(String date){  // 提取时间中的数字作为文件名
        String num = "";
        for(int i = 0;i < date.length();i++){
            if(date.charAt(i) >= '0' && date.charAt(i) <= '9'){
                num += date.charAt(i);
            }
        }
        return num;
    }
}
