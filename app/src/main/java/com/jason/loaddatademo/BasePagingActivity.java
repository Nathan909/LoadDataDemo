package com.jason.loaddatademo;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jason.loaddatademo.server.IPagingService;

import java.util.List;

import rx.Observer;

/**
 * @author zjh
 * @date 2016/9/18
 */
public class BasePagingActivity<T> extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private static boolean DEBUG = true;
    private static String LOG = "txxz";

    private static final int PAGE_SIZE = 20;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter mQuickAdapter;
    private IPagingService<List<T>> mPagingService;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int currentPage;
    private int lastPage;

    protected void startGetData(RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout, BaseQuickAdapter quickAdapter, IPagingService<List<T>> pagingService) {
        mPagingService = pagingService;
        setRecyclerView(recyclerView);
        setSwipeRefreshLayout(swipeRefreshLayout);
        setQuickAdapter(quickAdapter);
        onLoadFirstData();
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        if (mRecyclerView.getLayoutManager() == null)
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 配置线性布局管理器
    }

    private void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            mSwipeRefreshLayout = swipeRefreshLayout;
            // 刷新球动画参数（是否缩放出现，启停位置，中间下拉最深位置）
            exLog("BasePaingActivity.setSwipeRefreshLayout()--end=" + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);// 刷新球背景色
            // 刷新球动画颜色
            mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        } else {
            throw new NullPointerException("swipeRefreshLayout is null");
        }
    }

    private void setQuickAdapter(BaseQuickAdapter quickAdapter) {
        if (quickAdapter != null) {
            mQuickAdapter = quickAdapter;
            mQuickAdapter.openLoadAnimation();// 开启custom动画
            mQuickAdapter.openLoadMore(PAGE_SIZE);
            mQuickAdapter.setOnLoadMoreListener(this);
            mRecyclerView.setAdapter(mQuickAdapter);
        } else {
            throw new NullPointerException("quickAdapter is null");
        }
    }

    public void onLoadFirstData() {
        lastPage = currentPage = 1;
        mSwipeRefreshLayout.setRefreshing(true);
        mPagingService.getData(currentPage, PAGE_SIZE, new Observer<List<T>>() {
            @Override
            public void onCompleted() {
                exLog("BasePaingActivity.onLoadFirstData()--onCompleted()");
                toastText("加载完成");
            }

            @Override
            public void onError(Throwable e) {
                exLog("BasePaingActivity.onLoadFirstData()--onError()");
                toastText("加载失败");
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(List<T> list) {
                exLog("BasePaingActivity.onLoadFirstData()--onNext()");
                if (list == null) return;
                mQuickAdapter.addData(list);
                mQuickAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        mPagingService.getData(currentPage, PAGE_SIZE, new Observer<List<T>>() {
            @Override
            public void onCompleted() {
                toastText("刷新成功");
            }

            @Override
            public void onError(Throwable e) {
                toastText("刷新失败");
                mSwipeRefreshLayout.setRefreshing(false);
                currentPage = lastPage;
            }

            @Override
            public void onNext(List<T> list) {
                if (list == null) return;
                if (!mQuickAdapter.isLoading()) {
                    mQuickAdapter.openLoadMore(PAGE_SIZE);
                }
                mQuickAdapter.getData().clear();
                mQuickAdapter.addData(list);
                mQuickAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        lastPage = currentPage;
        currentPage++;
        mPagingService.getData(currentPage, PAGE_SIZE, new Observer<List<T>>() {
            @Override
            public void onCompleted() {
                toastText("加载完成");
            }

            @Override
            public void onError(Throwable e) {
                toastText("加载失败");
                currentPage = lastPage;
            }

            @Override
            public void onNext(List<T> list) {
                if ((list != null && list.isEmpty())) {
                    toastText("没有更多数据了");
                    mQuickAdapter.addData(list);
                    mQuickAdapter.loadComplete();
                } else {
                    mQuickAdapter.addData(list);
                }
                lastPage = currentPage;
            }
        });
    }

    private void toastText(String str) {
        Toast.makeText(BasePagingActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void exLog(String str) {
        if (DEBUG)
            android.util.Log.d(LOG, str);
    }
}
