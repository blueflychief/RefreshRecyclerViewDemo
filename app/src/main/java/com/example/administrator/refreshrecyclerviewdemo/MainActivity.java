package com.example.administrator.refreshrecyclerviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.example.administrator.refreshrecyclerviewdemo.recycler.RefreshRecycleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RefreshRecycleView rv_refresh;
    private MyAdapter myAdapter;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_refresh = (RefreshRecycleView) findViewById(R.id.rv_refresh);
        rv_refresh.setLayoutManager(new LinearLayoutManager(this));
        TextView header = new TextView(this);
        header.setText("这是头部");

        final TextView footer = new TextView(this);
        footer.setText("这是底部");
        rv_refresh.setHeader(header);
//        rv_refresh.setFooter(footer);
        myAdapter = new MyAdapter(this, initData(), rv_refresh);
        rv_refresh.setAdapter(myAdapter);
        rv_refresh.setNoMoreShowFooter(false);
        rv_refresh.setLoadMoreEnable(true);
        rv_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count = 0;
                        myAdapter.addData(true, initData());
                        rv_refresh.setRefreshing(false);

                        if (count == 2) {
                            rv_refresh.setLoadMoreEnable(false);
                        } else {
                            rv_refresh.setLoadMoreEnable(true);
                        }

                    }
                }, 2000);
            }
        });

        rv_refresh.setOnLoadMoreListener(new RefreshRecycleView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.addData(false, initData());
                        count++;
                        if (count == 2) {
                            rv_refresh.setLoadMoreEnable(false);
                        } else {
                            rv_refresh.setLoadMoreEnable(true);
                        }
                    }
                }, 2000);
            }
        });
    }

    @NonNull
    private List<String> initData() {
        List<String> list = new ArrayList();
        for (int i = 0; i < 12; i++) {
            list.add("这是i+" + i);
        }
        return list;
    }
}
