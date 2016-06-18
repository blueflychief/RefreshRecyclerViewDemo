package com.example.administrator.refreshrecyclerviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.refreshrecyclerviewdemo.recycler.FooterTypeHandle;
import com.example.administrator.refreshrecyclerviewdemo.recycler.OnFooterClickListener;
import com.example.administrator.refreshrecyclerviewdemo.recycler.RefreshRecycleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private RefreshRecycleView rv_refresh;
    private Button bt_clear;
    private MyAdapter myAdapter;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_refresh = (RefreshRecycleView) findViewById(R.id.rv_refresh);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.clearData();
            }
        });
        rv_refresh.setLayoutManager(new LinearLayoutManager(this));
        TextView header = new TextView(this);
        header.setText("这是头部");

        final TextView footer = new TextView(this);
        footer.setText("这是底部");
        rv_refresh.setHeader(header);
//        rv_refresh.setFooter(footer);
        myAdapter = new MyAdapter(this, new ArrayList<String>(), rv_refresh);
        rv_refresh.setAdapter(myAdapter);
        rv_refresh.setShowFooterWithNoMore(true);
        rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_LOADING_MORE);
        rv_refresh.setOnRefreshListener(refreshListener);

        rv_refresh.setOnLoadMoreListener(loadMoreListener);
        rv_refresh.setOnFooterClickListener(new OnFooterClickListener() {
            @Override
            public void onErrorClick() {
                super.onErrorClick();
                rv_refresh.setRefreshing(true);
                refreshListener.onRefresh();
            }

            @Override
            public void onLoadMoreClick() {
                super.onLoadMoreClick();
                loadMoreListener.onLoadMore();
            }
        });
        refreshListener.onRefresh();
    }

    @NonNull
    private List<String> initData() {
        List<String> list = new ArrayList();
        for (int i = 0; i < 8; i++) {
            list.add("这是i+" + i);
        }
        return list;
    }


    SwipeRefreshLayout.OnRefreshListener refreshListener=new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                    myAdapter.addData(true, initData());
                    rv_refresh.setRefreshing(false);
                    if (myAdapter.getDatas()!=null&&myAdapter.getDatas().size()>4) {
                        rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_PULL_LOAD_MORE);
                    } else {
                        rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_NO_MORE);
                    }

                }
            }, 2000);
        }
    };


    RefreshRecycleView.OnLoadMoreListener loadMoreListener=new RefreshRecycleView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    myAdapter.addData(false, initData());
                    rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_ERROR);
                    rv_refresh.setRefreshing(false);
//                    count++;
//                    if (myAdapter.getDatas()!=null&&myAdapter.getDatas().size()>4) {
//                        rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_PULL_LOAD_MORE);
//                    } else {
//                        rv_refresh.setLoadMoreEnable(FooterTypeHandle.TYPE_NO_MORE);
//                    }
                }
            }, 2000);
        }
    };
}
