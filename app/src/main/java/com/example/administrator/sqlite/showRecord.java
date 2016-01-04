package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;

import uk.co.senab.photoview.PhotoView;

public class showRecord extends Activity {

    private DBManager database;
    private Item item;
    private int objectId;

    private TextView title;
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
        objectId = bundle.getInt("id");
        item = database.query(objectId);

        title.setText(item.getTitle());
        content.setText(item.getContent());
        titleBar.setText(dateToYMD(item.getDate()));

        photoPath = item.getPhotoPath();
        if(photoPath!=null) {
            try {
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
                System.out.println("321");
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(showRecord.this, Edit.class);
                Bundle bundle=new Bundle();
                bundle.putInt("flag",1);
                bundle.putInt("id",objectId);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.delete(objectId);
                finish();
            }
        });
    }


    public String dateToYMD(String date){
        String num=date.toString().substring(0,10);
        return num;
    }
}
