package com.lishide.nohttpconnecter.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.nohttp.HttpListener;
import com.lishide.nohttpconnecter.nohttp.HttpResponseListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * Created in 2017/1/1 15:09.
 *
 * @author Li Shide.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected BaseActivity context;
    protected Toolbar mToolbar;
    protected TextView mTvCenterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initContentView(savedInstanceState);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            initTitle();
        }

        initView();
        initLogic();
        // 初始化请求队列，传入的参数是请求并发值。
        mQueue = NoHttp.newRequestQueue(1);

    }

    //-------------- NoHttp -----------//

    /**
     * 用来标记取消。
     */
    private Object object = new Object();

    /**
     * 请求队列。
     */
    private RequestQueue mQueue;

    /**
     * 发起请求。
     *
     * @param what      what.
     * @param request   请求对象。
     * @param callback  回调函数。
     * @param canCancel 是否能被用户取消。
     * @param isLoading 实现显示加载框。
     * @param <T>       想请求到的数据类型。
     */
    public <T> void request(int what, Request<T> request, HttpListener<T> callback, boolean canCancel, boolean
            isLoading) {
        request.setCancelSign(object);
        mQueue.add(what, request, new HttpResponseListener<>(this, request, callback, canCancel, isLoading));
    }

    @Override
    protected void onDestroy() {
        // 和声明周期绑定，退出时取消这个队列中的所有请求，当然可以在你想取消的时候取消也可以，不一定和声明周期绑定。
        mQueue.cancelBySign(object);

        // 因为回调函数持有了activity，所以退出activity时请停止队列。
        mQueue.stop();

        super.onDestroy();
    }

    protected void cancelAll() {
        mQueue.cancelAll();
    }

    protected void cancelBySign(Object object) {
        mQueue.cancelBySign(object);
    }

    // -------------------- BaseActivity的辅助封装 --------------------- //

    protected abstract void initContentView(Bundle bundle);

    protected abstract void initView();

    protected abstract void initLogic();

    protected void initTitle() {
        //  设置Toolbar title文字颜色
        mToolbar.setTitleTextColor(Color.WHITE);
        //设置NavigationIcon
        mToolbar.setNavigationIcon(R.mipmap.navigation_back_white);
        // 设置navigation button 点击事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvCenterTitle = (TextView) findViewById(R.id.tv_centerTitle);
    }

    public void setCenterTitle(String string) {
        mTvCenterTitle.setText(string);
    }

    public void setCenterTitle(int id) {
        mTvCenterTitle.setText(id);
    }
}
