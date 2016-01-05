/**
 * 查看大图
 */
package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tandong.sa.zUImageLoader.core.ImageLoader;
import com.tandong.sa.zUImageLoader.core.ImageLoaderConfiguration;

import uk.co.senab.photoview.PhotoView;

public class completePic extends Activity {

    PhotoView completePic;  // 显示图片的控件

    String photoPath;  // 图片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_pic);

        completePic = (PhotoView)findViewById(R.id.completePic);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            photoPath = bundle.getString("photoPath");  // 从bundle中取出图片保存路径
        }catch (Exception e){
            e.printStackTrace();
        }

        ImageLoaderConfiguration config =
                ImageLoaderConfiguration.createDefault(getApplicationContext());  // 设置ImageLoader
        ImageLoader.getInstance().init(config);  // 初始化设置
        ImageLoader.getInstance().displayImage("file:///" + photoPath, completePic);  // 载入图片
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){  // 监听返回键
            finish();  // 点击返回键finish当前Activity
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }
}
