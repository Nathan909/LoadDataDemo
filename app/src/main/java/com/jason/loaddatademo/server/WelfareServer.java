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
    /**
     * 为框架加载数据
     *
     * @param page     加载第几页
     * @param limit    1页加载多少条
     * @param observer
     */
    @Override
    public void getData(int page, int limit, Observer<List<WelfareEntity>> observer) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        GankIo gankIo = retrofitManager.createReq(GankIo.class);
        Observable<List<WelfareEntity>> observable = gankIo.getWelfareImg(limit, page);
        observable.subscribeOn(Schedulers.io())//改变订阅线程，即call()执行的线程,切换之前的线程
                // observeOn-1 改变发送线程，即onNext()执行的线程,切换之后的线程，其效果可以覆盖之后的subscribeOn
                // observeOn-2 对之前的序列产生的结果先缓存起来，然后再在指定的线程上，推送给最终的subscriber
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);//订阅观察对象
    }
}
