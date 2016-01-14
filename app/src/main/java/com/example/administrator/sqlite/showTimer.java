package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.Item;
import com.example.administrator.sqlite.entity.Timer;
import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class showTimer extends Activity {

    private DBManager database;
    private Timer timer;
    private int objectId;

    private String curTime;
    private String targetTime;

    private TextView title;  // 各个控件
    private TextView content;
    private TextView titleBar;
    private ImageView imageView;
    private Button edit;
    private Button delete;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        database=new DBManager(this);

        titleBar = (TextView)findViewById(R.id.showTitlebar);
        title = (TextView)findViewById(R.id.showTitle);
        content = (TextView)findViewById(R.id.showContent);
        imageView = (ImageView)findViewById(R.id.showphoto);
        edit = (Button)findViewById(R.id.showEdit);
        delete =(Button)findViewById(R.id.delete);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        objectId = bundle.getInt("id");  // 获取记录的id
        timer = database.queryTimer(objectId);  // 根据id从数据库中获取记录

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currDate = new Date(System.currentTimeMillis());
        curTime = formatter.format(currDate);

        Date targetDate,curDate;
        targetDate=new Date();
        curDate=new Date();
        try {
            curDate = formatter.parse(curTime);
            targetDate = formatter.parse(formatDate(timer.getTimerTime()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        long leftTime = (targetDate.getTime()-curDate.getTime())/(1000*60*60*24);

        title.setText(timer.getTitle());  // 根据数据库的数据初始化控件中的内容
        content.setText(timer.getContent());
        titleBar.setText(Long.toString(leftTime)+" days left");
        photoPath = timer.getPhotoPath();

        if(photoPath != null) {  // 初始化ImageView
            //FileInputStream file = new FileInputStream(photoPath);
            //BitmapFactory.Options opts = new BitmapFactory.Options();
            //为位图设置100K的缓存
            //opts.inTempStorage = new byte[100 * 1024];
            //设置位图颜色显示优化方式
            //opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //设置图片可以被回收
            //opts.inPurgeable = true;
            //设置位图缩放比例
            //opts.inSampleSize = 4;
            //设置解码位图的尺寸信息
            //opts.inInputShareable = true;
            //解码位图
            //Bitmap bitmap1 = BitmapFactory.decodeStream(file, null, opts);
            //imageView.setImageBitmap(bitmap1);

            ImageLoaderConfiguration config =
                    ImageLoaderConfiguration.createDefault(getApplicationContext());  //设置属性
            ImageLoader.getInstance().init(config);  // 初始化ImageLoader
            ImageLoader.getInstance().displayImage("file:///" + photoPath, imageView); // 载入图片

        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteTimer(timer);
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToEdit = new Intent();
                intentToEdit.setClass(showTimer.this, editTimer.class);
                Bundle bundleToEdit = new Bundle();
                bundleToEdit.putInt("flag",1);  // 传递flag到编辑页面以判定是从此页面跳转到编辑页面
                bundleToEdit.putInt("id",objectId);  // 传递记录数据
                intentToEdit.putExtras(bundleToEdit);
                startActivityForResult(intentToEdit, 1);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 点击图片进入查看大图页面
                Intent intentToPic = new Intent();
                Bundle bundleToPic = new Bundle();
                intentToPic.setClass(showTimer.this, completePic.class);
                bundleToPic.putString("photoPath", photoPath);
                intentToPic.putExtras(bundleToPic);
                startActivity(intentToPic);
            }
        });
    }

    public String formatDate(String date){
        String newDate="";
        for(int i=0;i<8;i++){
            if(i==4||i==6){
                newDate+="-"+date.charAt(i);
            }
            else{
                newDate+=date.charAt(i);
            }
        }
        return newDate;
    }

    public String dateToYMD(String date){  // 将日期转化为XXXX-XX-XX格式
        String num=date.toString().substring(0,10);
        return num;
    }
}
