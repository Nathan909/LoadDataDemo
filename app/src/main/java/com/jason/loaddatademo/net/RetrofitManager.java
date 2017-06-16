package com.jason.loaddatademo.net;


import com.jason.loaddatademo.AppConfig;
import com.jason.loaddatademo.net.interceptor.RspCheckInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author zjh
 * @date 2016/6/29
 */
public class RetrofitManager {
    private static RetrofitManager mRetrofitManager;
    private Retrofit mRetrofit;

    private RetrofitManager() {
        initRetrofit();
    }

    public static RetrofitManager getInstance() {//单例模式——双重检查锁定
        if (mRetrofitManager == null)
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null)
                    mRetrofitManager = new RetrofitManager();
            }
        return mRetrofitManager;
    }

    private void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (AppConfig.DEBUG)
            builder.addInterceptor(interceptor);
        builder.addInterceptor(new RspCheckInterceptor());
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();

        mRetrofit = new Retrofit.Builder()//引入Retrofit网请框架
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//引入Gson网析框架，增加返回值为Gson的支持(以实体类返回)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//引入RxJava响编框架，增加返回值为Oservable<T>的支持
                .client(client)//引入OkHttp网请框架
                .build();
    }

    public <T> T createReq(Class<T> reqServer) {
        return mRetrofit.create(reqServer);
    }
}
