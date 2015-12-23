package com.example.administrator.sqlite;

/**
 * Created by Administrator on 2015/12/23.
 */
public class Item {
    private String Title;
    private String Content;
    private byte[] Photo;

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

    public byte[] getPhoto(){
        return Photo;
    }

    public void setPhoto(byte[] photo){
        Photo = photo;
    }
}
