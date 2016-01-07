package com.example.administrator.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.administrator.sqlite.database.DBManager;
import com.tandong.sa.avatars.AvatarDrawableFactory;

import java.util.logging.LogRecord;


public class Login extends Activity {

    private EditText username;
    private EditText password;
    private TextView login;
    private TextView register;
    private ImageView avatar;
    private CheckBox rememberPass;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private int runningFlag=0;
    private int mProgressStatus=0;
    private Handler mHandler;
    private Thread thread;
    private boolean isInterrupted=false;

    DBManager database;

    public static Login temp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SysApplication.getInstance().addActivity(this);

        temp=this;
        database=new DBManager(this);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (TextView)findViewById(R.id.login);
        register = (TextView)findViewById(R.id.register);
        avatar = (ImageView)findViewById(R.id.loginAvatar);
        rememberPass = (CheckBox)findViewById(R.id.rememberPass);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout)findViewById(R.id.loginRelative);


        BitmapFactory.Options options = new BitmapFactory.Options();  // 添加圆形头像
        options.inMutable = false;
        options.inSampleSize=4;
        AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getResources(),this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mumu, options);
        Drawable avatarDrawable = avatarDrawableFactory.getRoundedAvatarDrawable(bitmap);
        avatar.setImageDrawable(avatarDrawable);

        relativeLayout.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.INVISIBLE);
        String cacheName = database.getCacheName();
        username.setText(cacheName);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==0x111){
                    runningFlag = 1;
                    System.out.println(isInterrupted);
                    relativeLayout.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.VISIBLE);
                }else{
                        String name = username.getText().toString();  // 获取edittext里的用户名密码
                        String pass = password.getText().toString();
                        if (database.checkUser(name, pass)) {
                            SharedPreferences mySharedPreferences = getSharedPreferences("info", Activity.MODE_PRIVATE);  // 利用SharedPreferences保存当前用户id
                            SharedPreferences.Editor editor = mySharedPreferences.edit();

                            editor.putInt("userId", database.getIdByName(name));
                            editor.commit();

                            if (rememberPass.isChecked()) {
                                database.addCache(name, pass, 1);  // 修改缓存的用户名
                            } else {
                                database.addCache(name, pass, 0);
                            }

                            Intent intentToMain = new Intent();
                            intentToMain.setClass(Login.this, MainActivity.class);
                            startActivity(intentToMain);
                            finish();
                        } else {
                            relativeLayout.setVisibility(View.INVISIBLE);
                            runningFlag = 0;
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "用户名或密码不正确", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                }
            }
        };
        if(1 == database.getCacheFlag()){
            String pass = database.getCachePassword();
            password.setText(pass);
            rememberPass.setChecked(true);
        }

        password.setImeOptions(EditorInfo.IME_ACTION_DONE);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    login.performClick();
                    return true;
                }
                return false;
            }
        });
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isInterrupted = false;
                mProgressStatus = 0;
                if (0 == username.getText().length()) {  // 判断用户名是否为空
                    //relativeLayout.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "请输入用户名", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (0 == password.getText().length()) {  // 判断密码是否为空
                    //relativeLayout.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "请输入密码", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    thread =new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!isInterrupted) {
                                mProgressStatus = doWork();
                                Message m = new Message();
                                if (mProgressStatus < 100) {
                                    m.what = 0x111;
                                    mHandler.sendMessage(m);
                                } else {
                                    m.what = 0x110;
                                    mHandler.sendMessage(m);
                                    break;
                                }
                            }
                        }

                        private int doWork() {
                            mProgressStatus += 10;
                            try {
                                //progressBar.setVisibility(View.VISIBLE);
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return mProgressStatus;
                        }
                    });
                    thread.start();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(1 == runningFlag){
                relativeLayout.setVisibility(View.INVISIBLE);
                isInterrupted=true;
                System.out.println("test"+isInterrupted);
                //thread.interrupt();
                runningFlag = 0;
            }
            else{
                finish();
            }
        }
        return false;
    }
}
