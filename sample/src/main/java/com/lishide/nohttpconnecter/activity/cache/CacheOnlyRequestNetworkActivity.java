package com.lishide.nohttpconnecter.activity.cache;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * <p>仅仅请求网络。</p>
 */
public class CacheOnlyRequestNetworkActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_cache_demo);
    }

    @Override
    protected void initView() {
        Button mBtnCacheReqStr = (Button) findViewById(R.id.btn_cache_req_str);
        Button mBtnCacheReqImg = (Button) findViewById(R.id.btn_cache_req_img);
        mBtnCacheReqStr.setOnClickListener(this);
        mBtnCacheReqImg.setOnClickListener(this);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_cache_only_request_network));
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
     * 请求String。
     */
    private void requestString() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_CACHE_STRING);
        request.add("name", "yanzhenjie");
        request.add("pwd", 123);
        request.setCacheKey("CacheKeyOnlyRequestNetworkString");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);// ONLY_REQUEST_NETWORK表示仅仅请求网络，不会读取缓存，但是数据可能被缓存。
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
     * 请求Image。
     */
    private void requestImage() {
        Request<Bitmap> request = NoHttp.createImageRequest(Constants.URL_NOHTTP_CACHE_IMAGE);
        request.setCacheKey("CacheKeyOnlyRequestNetworkImage");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);// ONLY_REQUEST_NETWORK表示仅仅请求网络，不会读取缓存，但是数据可能被缓存。
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