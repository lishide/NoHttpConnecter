package com.lishide.nohttpconnecter.activity.cache;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * <p>请求网络失败后去读取缓存</p>
 */
public class CacheRequestFailedReadCacheActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvCacheTip;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_cache_demo);
    }

    @Override
    protected void initView() {
        Button mBtnCacheReqStr = (Button) findViewById(R.id.btn_cache_req_str);
        Button mBtnCacheReqImg = (Button) findViewById(R.id.btn_cache_req_img);
        mTvCacheTip = (TextView) findViewById(R.id.tv_cache_tip);
        mBtnCacheReqStr.setOnClickListener(this);
        mBtnCacheReqImg.setOnClickListener(this);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_cache_request_failed_read_cache_activity));
        mTvCacheTip.setText(getString(R.string.request_cache_request_failed_read_cache_tip));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cache_req_str:
                requestString();
                break;
            case R.id.btn_cache_req_img:
                requestImage();
                break;
        }
    }

    /**
     * 请求 String
     */
    private void requestString() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_METHOD);
        request.add("name", "yanzhenjie");
        request.add("pwd", 123);
        request.setCacheKey("CacheKeyRequestNetworkFailedReadCacheString");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
        //设置为REQUEST_NETWORK_FAILED_READ_CACHE表示请求服务器失败，就返回上次的缓存，如果缓存为空才会请求失败。
        startRequest(0, request, stringHttpListener, false, true);
    }

    private HttpListener<String> stringHttpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String string = response.isFromCache() ? getString(R.string.request_from_cache) : getString(R.string
                    .request_from_network);
            showMessageDialog(string, response.get());
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            showMessageDialog(R.string.request_failed, response.getException().getMessage());
        }
    };

    /**
     * 请求 Image
     */
    private void requestImage() {
        Request<Bitmap> request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE);
        request.setCacheKey("CacheKeyRequestNetworkFailedReadCacheImage");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
        //设置为REQUEST_NETWORK_FAILED_READ_CACHE表示请求服务器失败，就返回上次的缓存，如果缓存为空才会请求失败。
        startRequest(0, request, imageHttpListener, false, true);
    }

    private HttpListener<Bitmap> imageHttpListener = new HttpListener<Bitmap>() {
        @Override
        public void onSucceed(int what, Response<Bitmap> response) {
            String string = response.isFromCache() ? getString(R.string.request_from_cache) : getString(R.string
                    .request_from_network);
            showImageDialog(string, response.get());
        }

        @Override
        public void onFailed(int what, Response<Bitmap> response) {
            showMessageDialog(R.string.request_failed, response.getException().getMessage());
        }
    };

}
