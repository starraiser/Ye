package com.example.administrator.sqlite;

/**
 * Created by Administrator on 2015/12/23.
 */
public class Item {
    private int id;
    private String Title;
    private String Content;
    private String Path;

    public int getId(){ return id; }

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
