package com.example.administrator.refreshrecyclerviewdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lsq on 6/17/2016.--7:43 PM
 */
public class ItemHolder extends RecyclerView.ViewHolder {
   public TextView tv;
   public ImageView iv;

    public ItemHolder(View itemView) {
        super(itemView);
        tv = (TextView) itemView.findViewById(R.id.tv);
        iv = (ImageView) itemView.findViewById(R.id.iv);
    }
}
