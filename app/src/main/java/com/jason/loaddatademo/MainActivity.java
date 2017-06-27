package com.jason.loaddatademo;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jason.loaddatademo.entity.WelfareEntity;
import com.jason.loaddatademo.server.WelfareServer;

import java.util.ArrayList;

public class MainActivity extends BasePagingActivity<WelfareEntity> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //框架部分
        setContentView(R.layout.activity_main);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.list);
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        //数据部分
        startGetData(mRecyclerView, mSwipeRefreshLayout, new BaseQuickAdapter<WelfareEntity>(R.layout.item_welfare, new ArrayList()) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, WelfareEntity welfareEntity) {
                Glide.with(MainActivity.this)//引入Glide图载框架
                        .load(welfareEntity.getUrl())
                        .placeholder(R.mipmap.load_image_bg)
                        .into((ImageView) baseViewHolder.getView(R.id.iv));
            }
        }, new WelfareServer());
    }
}
