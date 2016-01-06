package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class Login extends Activity {

    private EditText username;
    private EditText password;
    private TextView login;
    private TextView register;

    DBManager database;

    public Activity temp = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SysApplication.getInstance().addActivity(this);

        database=new DBManager(this);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (TextView)findViewById(R.id.login);
        register = (TextView)findViewById(R.id.register);


        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == username.getText().length()) {  // 判断用户名是否为空
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "请输入用户名",Toast.LENGTH_SHORT);
                    toast.show();
                } else if (0 == password.getText().length()) {  // 判断密码是否为空
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "请输入密码",Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String name = username.getText().toString();  // 获取edittext里的用户名密码
                    String pass = password.getText().toString();
                    if(database.checkUser(name,pass)){
                        SharedPreferences mySharedPreferences = getSharedPreferences("test",Activity.MODE_PRIVATE);  // 利用SharedPreferences保存当前用户id
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putInt("userId", database.getIdByName(name));
                        editor.commit();

                        Intent intentToMain = new Intent();
                        intentToMain.setClass(Login.this,MainActivity.class);
                        startActivity(intentToMain);
                        finish();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "用户名或密码不正确",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToReg = new Intent();
                intentToReg.setClass(Login.this, Register.class);
                startActivity(intentToReg);
            }
        });
    }


}
