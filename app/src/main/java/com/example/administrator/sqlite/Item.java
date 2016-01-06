package com.example.administrator.sqlite;

/**
 * Created by Administrator on 2015/12/23.
 * 实体
 */
public class Item {

    //ID
    private int _id;
    private int userId;
    //创建时间
    private String Time;
    //标题
    private String Title;
    //正文
    private String Content;
    //图片保存路径
    private String Path;

    public Item() {}
    public Item(int id,int userId, String time,String title,String content,String path){
        _id = id;
        this.userId = userId;
        Time = time;
        Title = title;
        Content = content;
        Path = path;
    }

    public Item(int userId,String time,String title,String content,String path){
        this.userId = userId;
        Time = time;
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

    public String getDate(){ return Time; }

    public void setDate(String time){ Time = time; }

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
