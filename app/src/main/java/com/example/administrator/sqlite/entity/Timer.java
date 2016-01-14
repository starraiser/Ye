package com.example.administrator.sqlite.entity;

/**
 * Created by Administrator on 2015/12/23.
 * 实体
 */
public class Timer {

    //ID
    private int _id;
    private int userId;
    //创建时间
    private String createTime;
    private String timerTime;
    //标题
    private String Title;
    //正文
    private String Content;
    //图片保存路径
    private String Path;

    public Timer() {}
    public Timer(int id,int userId, String time,String timerTime,String title,String content,String path){
        _id = id;
        this.userId = userId;
        this.timerTime = timerTime;
        createTime = time;
        Title = title;
        Content = content;
        Path = path;
    }

    public Timer(int userId,String time,String timerTime,String title,String content,String path){
        this.userId = userId;
        createTime = time;
        this.timerTime =timerTime;
        Title = title;
        Content = content;
        Path = path;
    }

    public void setId (int id) { _id=id; }

    public int getId () { return _id; }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getCreateTime(){ return createTime; }

    public void setCreateTime(String time){ createTime = time; }

    public String getTimerTime(){return timerTime;}

    public void setTimerTime(String timerTime){this.timerTime = timerTime;}

    public String getTitle(){
        return Title;
    }

    public void setTitle(String title){
        Title = title;
    }

    public String getContent(){
        return Content;
    }

    public void setContent(String content){
        Content = content;
    }

    public String getPhotoPath(){
        return Path;
    }

    public void setPhotoPath(String photo){
        Path = photo;
    }
}
