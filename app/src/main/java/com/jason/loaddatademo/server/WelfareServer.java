package com.jason.loaddatademo.server;

import com.jason.loaddatademo.api.GankIo;
import com.jason.loaddatademo.entity.WelfareEntity;
import com.jason.loaddatademo.net.RetrofitManager;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zjh
 * @date 2016/9/18
 */
public class WelfareServer implements IPagingService<List<WelfareEntity>> {
    @Override
    public void getData(int page, int limit, Observer<List<WelfareEntity>> observer) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        GankIo gankIo = retrofitManager.createReq(GankIo.class);
        Observable<List<WelfareEntity>> observable = gankIo.getWelfareImg(limit, page);
        observable.subscribeOn(Schedulers.io())//改变订阅线程,切换之前的线程
                .observeOn(AndroidSchedulers.mainThread())//改变发送线程,切换之后的线程，其效果可以覆盖之后的subscribeOn
                .subscribe(observer);
    }
}