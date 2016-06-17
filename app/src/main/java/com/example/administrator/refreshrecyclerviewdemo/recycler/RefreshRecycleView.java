package com.example.administrator.refreshrecyclerviewdemo.recycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.administrator.refreshrecyclerviewdemo.R;

import static android.support.v7.widget.RecyclerView.LayoutManager;

/**
 * <p>
 * 可刷新的RecycleView, 也可分页
 */
public class RefreshRecycleView extends SwipeRefreshLayout {

    /**
     * 加载更多
     */
    public interface OnLoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    private RefreshRecycleView refreshView;
    private LoadMoreRecyclerView mRecycleView;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean loadMoreEnable = true;
    private View header, footer; // 头部和脚部

    public RefreshRecycleView(Context context) {
        this(context, null);
    }

    public RefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshView = this;
        mRecycleView = new LoadMoreRecyclerView(context);
        addView(mRecycleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // 设置刷新动画的颜色
        setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(RefreshAdapter adapter) {
        mRecycleView.setAdapter(adapter);
    }


    public LoadMoreRecyclerView getRecycleView() {
        return mRecycleView;
    }

    /**
     * @return 当前正在使用的适配器
     */
    public RefreshAdapter getAdapter() {
        return (RefreshAdapter) mRecycleView.getAdapter();
    }

    /**
     * 设置布局管理器
     *
     * @param manager 布局管理器
     */
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

    /**
     * @return 布局管理器
     */
    public LayoutManager getLayoutManager() {
        return mRecycleView.getLayoutManager();
    }

    /**
     * 添加分割线
     *
     * @param decor 分割线
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecycleView.addItemDecoration(decor);
    }

    /**
     * 设置是否可以加载更多, 默认true
     */
    public void setLoadMoreEnable(boolean enable) {
        loadMoreEnable = enable;
        if (loadMoreEnable) {
            if (getFooter() != null) {
                getFooter().findViewById(R.id.ll_more).setVisibility(VISIBLE);
                getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
            }
        } else {
            if (getFooter() != null) {
                getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                getFooter().findViewById(R.id.tv_no_more).setVisibility(VISIBLE);
            }
        }
    }

    private boolean mShowFooter = true;

    public void setNoMoreShowFooter(boolean show) {
        mShowFooter = show;
    }

    public boolean getNoMoreShowFooter() {
        return mShowFooter;
    }

    /**
     * @return 是否可以加载更多
     */
    public boolean getLoadMoreEnable() {
        return loadMoreEnable;
    }

    /**
     * 设置加载更多监听
     *
     * @param listener {@link OnLoadMoreListener}
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    /**
     * 初始化默认的footer
     */
    private void initDefaultFooter() {
        // 默认的footer
        setFooter(R.layout.refresh_recyclerview_load_more);
    }

    /**
     * 添加头部
     *
     * @param header 作为头部的布局
     */
    public void setHeader(View header) {
        this.header = header;
    }

    /**
     * 删除头部
     */
    public void removeHeader() {
        this.header = null;
    }

    /**
     * 添加头部
     *
     * @param resId 作为头部的布局资源id
     */
    public void setHeader(int resId) {
        this.header = LayoutInflater.from(getContext()).inflate(resId, mRecycleView, false);
    }

    /**
     * @return 头部视图
     */
    public View getHeader() {
        return header;
    }

    /**
     * 添加脚部
     *
     * @param footer 作为脚部的布局
     */
    public void setFooter(View footer) {
        this.footer = footer;
    }

    /**
     * 添加头部
     *
     * @param resId 作为脚部的布局资源id
     */
    public void setFooter(int resId) {
        this.footer = LayoutInflater.from(getContext()).inflate(resId, mRecycleView, false);
    }

    /**
     * @return 脚部视图
     */
    public View getFooter() {
        return footer;
    }

    /**
     * @return 获取最后一个可见视图的位置
     */
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
                        setLoadMoreEnable(false); // 未充满则不能加载更多
                        getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onScrollStateChanged(int state) {
            super.onScrollStateChanged(state);
            if (state == SCROLL_STATE_IDLE && // 停止滚动
                    loadMoreEnable
                    && refreshView.mOnLoadMoreListener != null  // 可以加载更多, 且有加载监听
                    && mIsUp
                    && findLastVisibleItemPosition() == getLayoutManager().getItemCount() - 1) { // 滚动到了最后一个子视图
                refreshView.mOnLoadMoreListener.onLoadMore(); // 执行加载更多
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
