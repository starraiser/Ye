package com.example.administrator.sqlite.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.sqlite.MainActivity;
import com.example.administrator.sqlite.R;
import com.example.administrator.sqlite.database.DBManager;
import com.example.administrator.sqlite.entity.Item;
import com.example.administrator.sqlite.showRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends ListFragment{
    private SimpleAdapter adapter=null;
    private List<Item> listData;  // 数据
    private int userId;
    private DBManager database;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        database = new DBManager(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("info", Activity.MODE_PRIVATE);  // 获取当前用户id
        userId = sharedPreferences.getInt("userId",-1);

        adapter =  new SimpleAdapter(getActivity(),getData(), R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        setListAdapter(adapter);
    }

    private List<Map<String ,Object>> getData(){  // 获取数据库记录
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        listData = database.getListOfUser(userId);
        for (int i = 0; i < listData.size(); i++){
            Map<String,Object> map = new HashMap<String,Object>();

            String title = listData.get(i).getTitle();
            String time = listData.get(i).getDate();
            System.out.println(title);
            map.put("title",title);
            map.put("time",time);
            list.add(map);
        }
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record,container,false);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter =  new SimpleAdapter(getActivity(),getData(), R.layout.list_layout,
                new String[]{"title","time"},new int[]{R.id.list_title,R.id.list_time});
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l,View v,int position,long id){
        int tmp = database.getListOfUser(userId).get(position).getId();  // 获取点击列表中的数据的id

        Intent intent = new Intent(getActivity(), showRecord.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", tmp);  // 传递id到显示页面
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
        //getActivity().(getActivity(),showRecord.class);
    }
}