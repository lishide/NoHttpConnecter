package com.lishide.nohttputils.nohttp;

import android.app.Activity;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * Created by lishide on 2017/3/6.
 * 队列的单例模式封装 -- 提供外部调用进行网络请求
 */
public class CallServer {

    private static CallServer instance;

    /**
     * 请求队列
     */
    private RequestQueue requestQueue;

    private CallServer() {
        // 初始化请求队列，传入的参数是请求并发值
        requestQueue = NoHttp.newRequestQueue(3);
    }

    /**
     * 请求队列
     */
    public synchronized static CallServer getInstance() {
        if (instance == null)
            synchronized (CallServer.class) {
                if (instance == null)
                    instance = new CallServer();
            }
        return instance;
    }

    /**
     * 添加一个请求到请求队列
     *
     * @param activity     Context
     * @param what         用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what。
     * @param request      请求对象
     * @param httpCallback 回调函数
     * @param canCancel    是否能被用户取消
     * @param isLoading    是否显示加载框
     * @param <T>
     */
    public <T> void add(Activity activity, int what, Request<T> request,
                        HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        requestQueue.add(what, request, new HttpResponseListener<>(activity, request, httpCallback,
                canCancel, isLoading));
    }

    /**
     * 取消这个sign标记的所有请求
     *
     * @param sign 请求的取消标志
     */
    public void cancelBySign(Object sign) {
        requestQueue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求
     */
    public void cancelAll() {
        requestQueue.cancelAll();
    }

    /**
     * 停止队列
     */
    public void stop() {
        requestQueue.stop();
    }
}