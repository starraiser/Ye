package com.example.administrator.sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Edit extends Activity {

    private SQLiteDatabase db;

    private Button takePhoto;
    private Button confirm;
    private EditText editTitle;
    private EditText editContent;

    private String time;
    private String savePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        confirm = (Button)findViewById(R.id.confirm);
        takePhoto = (Button)findViewById(R.id.takePhoto);
        editTitle = (EditText)findViewById(R.id.editTitle);
        editContent = (EditText)findViewById(R.id.editContent);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
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
                    if(editContent.length()>10){
                        tempTitle=editContent.getText().toString().substring(0,10);
                    }
                    else{
                        tempTitle=editContent.getText().toString().substring(0,editContent.length());
                    }
                    System.out.println(tempTitle);
                }
                //题目和正文都有
                else{
                    System.out.println("333333333333");
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
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            time = formatter.format(curDate);
        }
    }
}
