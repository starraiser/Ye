package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.User;

public class Register extends Activity {

    private EditText userName;
    private EditText password;
    private EditText passwordAgain;
    private TextView confirm;

    private DBManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SysApplication.getInstance().addActivity(this);

        database = new DBManager(this);

        userName = (EditText)findViewById(R.id.usernameReg);
        password = (EditText)findViewById(R.id.passwordReg);
        passwordAgain = (EditText)findViewById(R.id.passwordRegAgain);
        confirm = (TextView)findViewById(R.id.comfirmReg);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPassword = password.getText().toString();
                String tempPasswordAgain = passwordAgain.getText().toString();
                String tempName = userName.getText().toString();
                if((0 !=tempName.length())&&(0 != tempPasswordAgain.length())
                        &&(0 != tempPassword.length())) {
                    if (tempPassword.equals(tempPasswordAgain)) {
                        if (-1 != database.getIdByName(tempName)) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "用户已存在", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            User user = new User(tempName, tempPassword);

                            database.addUser(user);  // 向数据库添加新用户
                            database.addCache(tempName,tempPassword,0,0);  // 修改缓存的用户名

                            SharedPreferences mySharedPreferences =
                                    getSharedPreferences("info", Activity.MODE_PRIVATE);  // 利用SharedPreferences保存当前用户id
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            editor.putInt("userId", database.getIdByName(tempName));
                            editor.commit();

                            Intent intentToMain = new Intent();
                            intentToMain.setClass(Register.this, MainActivity.class);
                            startActivity(intentToMain);
                            finish();
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "两次密码输入不一致", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "请输入用户名和密码", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        passwordAgain.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    System.out.println(confirm.performClick());
                    return true;
                }
                return false;
            }
        });
    }
}
