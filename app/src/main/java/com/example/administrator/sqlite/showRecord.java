/**
 * 查看记录页面
 */
package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

public class showRecord extends Activity {

    private DBManager database;
    private Item item;
    private int objectId;

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
        setContentView(R.layout.activity_show_record_new);

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
        item = database.query(objectId);  // 根据id从数据库中获取记录

        title.setText(item.getTitle());  // 根据数据库的数据初始化控件中的内容
        content.setText(item.getContent());
        titleBar.setText(dateToYMD(item.getDate()));
        photoPath = item.getPhotoPath();

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

        edit.setOnClickListener(new View.OnClickListener() {  // 编辑按钮监听器
            @Override
            public void onClick(View v) {
                Intent intentToEdit = new Intent();
                intentToEdit.setClass(showRecord.this, Edit.class);
                Bundle bundleToEdit = new Bundle();
                bundleToEdit.putInt("flag",1);  // 传递flag到编辑页面以判定是从此页面跳转到编辑页面
                bundleToEdit.putInt("id",objectId);  // 传递记录数据
                intentToEdit.putExtras(bundleToEdit);
                startActivityForResult(intentToEdit,1);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {  // 删除按钮监听器
            @Override
            public void onClick(View v) {
                database.delete(objectId);  // 删除此记录
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 点击图片进入查看大图页面
                Intent intentToPic = new Intent();
                Bundle bundleToPic = new Bundle();
                intentToPic.setClass(showRecord.this,completePic.class);
                bundleToPic.putString("photoPath",photoPath);
                intentToPic.putExtras(bundleToPic);
                startActivity(intentToPic);
            }
        });
    }


    public String dateToYMD(String date){  // 将日期转化为XXXX-XX-XX格式
        String num=date.toString().substring(0,10);
        return num;
    }
}
