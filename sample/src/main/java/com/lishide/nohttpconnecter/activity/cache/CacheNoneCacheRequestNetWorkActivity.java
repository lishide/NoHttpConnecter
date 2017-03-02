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
 * <p>如果缓存为空才去请求网络。</p>
 */
public class CacheNoneCacheRequestNetWorkActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_cache_demo);
    }

    @Override
    protected void initView() {
        Button mRvCacheReqStr = (Button) findViewById(R.id.rv_cache_req_str);
        Button mRvCacheReqImg = (Button) findViewById(R.id.rv_cache_req_img);
        mRvCacheReqStr.setOnClickListener(this);
        mRvCacheReqImg.setOnClickListener(this);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_cache_none_request_network_activity));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rv_cache_req_str:
                requestString();
                break;
            case R.id.rv_cache_req_img:
                requestImage();
                break;
        }
    }

    /**
     * 请求String。
     */
    private void requestString() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_METHOD);
        request.add("name", "yanzhenjie");
        request.add("pwd", 123);
        request.setCacheKey("CacheKeyNoneCacheRequestNetworkString");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
        //设置为NONE_CACHE_REQUEST_NETWORK表示先去读缓存，如果没有缓存才请求服务器。
        request(0, request, stringHttpListener, false, true);
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
        Request<Bitmap> request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE);
        request.setCacheKey("CacheKeyNoneCacheRequestNetworkImage");//
        // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
        //设置为NONE_CACHE_REQUEST_NETWORK表示先去读缓存，如果没有缓存才请求服务器。
        request(0, request, imageHttpListener, false, true);
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
