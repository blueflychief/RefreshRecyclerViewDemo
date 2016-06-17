package com.example.administrator.refreshrecyclerviewdemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.refreshrecyclerviewdemo.recycler.RefreshAdapter;
import com.example.administrator.refreshrecyclerviewdemo.recycler.RefreshRecycleView;

import java.util.List;

/**
 * Created by Lsq on 6/17/2016.--7:43 PM
 */
public class MyAdapter extends RefreshAdapter<ItemHolder> {

    private Context mContext;
    private List mDatas;


    public MyAdapter(Context context, List list, RefreshRecycleView refreshView) {
        super(context, list, refreshView);
        this.mContext = context;
        this.mDatas = list;
    }

    @Override
    public ItemHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_holder, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindHolder(ItemHolder holder, int position) {

    }


    public void addData(boolean is_refresh, List list) {
        if (is_refresh) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }
}
