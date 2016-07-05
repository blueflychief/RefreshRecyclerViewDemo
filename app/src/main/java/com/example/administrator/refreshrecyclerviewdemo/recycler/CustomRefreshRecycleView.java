package com.example.administrator.refreshrecyclerviewdemo.recycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.administrator.refreshrecyclerviewdemo.R;

import static android.support.v7.widget.RecyclerView.LayoutManager;


/**
 * <p/>
 * 可刷新的RecycleView, 也可分页，使用时会填充整个父布局
 */
public class CustomRefreshRecycleView extends SwipeRefreshLayout {

    private static final String TAG = "RecycleView";
    private CustomRefreshRecycleView refreshView;
    private LoadMoreRecyclerView mRecycleView;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnFooterClickListener mOnFooterClickListener;  //底部点击监听
    private boolean mLoadMoreEnable = true;  //是否能加载更多
    private boolean mShowFooter = true;      //是否显示Footer
    private View mHeader, mFooter;             // 头部和脚部
    private FooterTypeHandle mFooterStatus;  //Footer状态

    public CustomRefreshRecycleView(Context context) {
        this(context, null);
    }

    public CustomRefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshView = this;
        mRecycleView = new LoadMoreRecyclerView(context);
        addView(mRecycleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 设置Adapter
     */
    public void setAdapter(CustomRefreshAdapter adapter) {
        mRecycleView.setAdapter(adapter);
    }


    public LoadMoreRecyclerView getRecycleView() {
        return mRecycleView;
    }


    public CustomRefreshAdapter getAdapter() {
        return (CustomRefreshAdapter) mRecycleView.getAdapter();
    }


    public void setLayoutManager(final LayoutManager manager) {
        if (manager instanceof GridLayoutManager) {
            ((GridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getAdapter().isFooter(position) || getAdapter().isHeader(position) ?
                            ((GridLayoutManager) manager).getSpanCount() : 1;
                }
            });
        }

        mRecycleView.setLayoutManager(manager);
        initDefaultFooter();
    }


    public LayoutManager getLayoutManager() {
        return mRecycleView.getLayoutManager();
    }

    /**
     * 添加分割线
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecycleView.addItemDecoration(decor);
    }


    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mOnFooterClickListener = listener;
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE);
        super.setOnRefreshListener(listener);
    }

    /**
     * 设置Footer显示状态
     */
    public void setFooterStatus(FooterTypeHandle type) {
        if (mRecycleView == null) {
            throw new NullPointerException("-----mRecycleView is null!!!");
        }
        mFooterStatus = type;
        switch (type) {
            case TYPE_PULL_LOAD_MORE:
                mRecycleView.mIsLoadMore = false;
                mLoadMoreEnable = true;
                break;
            case TYPE_LOADING_MORE:
                mRecycleView.mIsLoadMore = true;
                mLoadMoreEnable = true;
                break;
            case TYPE_ERROR:
                mRecycleView.mIsLoadMore = false;
                mLoadMoreEnable = true;
                break;
            case TYPE_NO_MORE:
                mRecycleView.mIsLoadMore = false;
                mLoadMoreEnable = false;
                break;
        }
        refreshFooter(type);
    }

    private void refreshFooter(FooterTypeHandle type) {
        if (mLoadMoreEnable) {
            if (getFooter() != null) {
                if (type == FooterTypeHandle.TYPE_PULL_LOAD_MORE) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(VISIBLE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                    return;
                }

                if (type == FooterTypeHandle.TYPE_LOADING_MORE) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(VISIBLE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                    return;
                }

                if (type == FooterTypeHandle.TYPE_ERROR) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(VISIBLE);
                }
            }
        } else {
            if (getFooter() != null) {
                getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                if (getShowFooterWithNoMore()) {
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(VISIBLE);
                } else {
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                }
            }
        }
    }


    /**
     * @return 是否可以加载更多
     */
    public boolean getLoadMoreEnable() {
        return mLoadMoreEnable;
    }


    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE);
        mOnLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public void setHeader(View mHeader) {
        this.mHeader = mHeader;
    }


    public void removeHeader() {
        this.mHeader = null;
    }


    public void setHeader(int resId) {
        this.mHeader = LayoutInflater.from(getContext()).inflate(resId, mRecycleView, false);
    }

    public View getHeader() {
        return mHeader;
    }


    public void setFooter(View mFooter) {
        this.mFooter = mFooter;
    }


    public void setFooter(int resId) {
        this.mFooter = LayoutInflater.from(getContext()).inflate(resId, mRecycleView, false);

        mFooter.findViewById(R.id.tv_error).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFooterClickListener != null) {
                    setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE);
                    mOnFooterClickListener.onErrorClick();
                }
            }
        });

        mFooter.findViewById(R.id.tv_pull_load_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFooterClickListener != null) {
                    setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE);
                    mOnFooterClickListener.onLoadMoreClick();
                }
            }
        });
    }

    private void initDefaultFooter() {
        setFooter(R.layout.custom_refresh_recyclerview_load_more);
    }


    public View getFooter() {
        return mFooter;
    }


    //没有更多时是否显示Footer
    public void setShowFooterWithNoMore(boolean show) {
        mShowFooter = show;
    }

    public boolean getShowFooterWithNoMore() {
        return mShowFooter;
    }


    public int findLastVisibleItemPosition() {
        LayoutManager manager = mRecycleView.getLayoutManager();
        // 获取最后一个正在显示的View
        if (manager instanceof GridLayoutManager) {
            return ((GridLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) manager).getSpanCount()];
            ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(into);
            return findMax(into);
        }
        return -1;
    }

    /**
     * 设置子视图充满一行
     *
     * @param view 子视图
     */
    public void setFullSpan(View view) {
        LayoutManager manager = getLayoutManager();
        // 根据布局设置参数, 使"加载更多"的布局充满一行
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            view.setLayoutParams(params);
        }
    }

    /**
     * 可分页加载更多的RecyclerView
     */
    public class LoadMoreRecyclerView extends RecyclerView {

        public LoadMoreRecyclerView(Context context) {
            super(context);
            post(new Runnable() {
                @Override
                public void run() {
                    if (getBottom() != 0 && getChildAt(findLastVisibleItemPosition()) != null && getBottom() >= getChildAt(findLastVisibleItemPosition()).getBottom()) {
                        // 最后一条正在显示的子视图在RecyclerView的上面, 说明子视图未充满RecyclerView
                        setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE); // 未充满则不能加载更多
                        getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }

        public boolean mIsLoadMore = false;

        @Override
        public void onScrollStateChanged(int state) {
            super.onScrollStateChanged(state);
            if (state == SCROLL_STATE_IDLE && // 停止滚动
                    mLoadMoreEnable
                    && refreshView.mOnLoadMoreListener != null  // 可以加载更多, 且有加载监听
                    && mIsUp
                    && !mIsLoadMore
                    && findLastVisibleItemPosition() == getLayoutManager().getItemCount() - 1) { // 滚动到了最后一个子视图
                Log.i(TAG, "----------loadmore");
                if (mFooterStatus != FooterTypeHandle.TYPE_ERROR) {
                    setFooterStatus(FooterTypeHandle.TYPE_LOADING_MORE);
                    refreshView.mOnLoadMoreListener.onLoadMore(); // 执行加载更多
                }
            }
        }


        private float mOldY = 0.0f;
        private float mNewY = 0.0f;
        private boolean mIsUp = false;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldY = e.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mNewY = e.getY();
                    //下拉
                    if (mNewY - mOldY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
                        mIsUp = false;
                    }
                    //上拉
                    else if (mOldY - mNewY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
                        mIsUp = true;
                    }
                    mOldY = mNewY;
                    break;

                case MotionEvent.ACTION_UP:
                    mOldY = 0.0f;
                    mNewY = 0.0f;
                    break;
            }
            return super.onTouchEvent(e);
        }
    }


    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
