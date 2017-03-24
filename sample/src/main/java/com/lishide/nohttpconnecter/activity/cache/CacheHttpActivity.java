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
 * <p>Http相应头304缓存演示.</p>
 */
public class CacheHttpActivity extends BaseActivity implements View.OnClickListener {
    /*
     * 先来普及一下响应码304缓存是什么意思：
     * 在RFC2616中，当http响应码是304时，表示客户端缓存有效， 客户端可以使用缓存；
     * <p>
     * NoHttp实现了Http协议1.1，很好的支持RESTFUL风格的接口；根据协议当请求方式是GET时， 且服务器响应头包涵Last-Modified时，
     * 响应内容可以被客户端缓存起来，下次请求时只需要验证缓存，验证缓存时如果服务器响应码为304时，表示客户端缓存有效， 可以
     * 继续使用缓存数据。
     * <p>
     * 由于NoHttp只是缓存了byte[]，所以不论图片，还是String都可以很好的被缓存。
     */
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
        mToolbar.setTitle(getString(R.string.title_cache_http_activity));
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
        request.setCacheKey("CacheKeyDefaultString");// 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.DEFAULT);//默认就是DEFAULT，所以这里可以不用设置，DEFAULT代表走Http标准协议。
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
        request.setCacheKey("CacheKeyDefaultImage");// 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
        request.setCacheMode(CacheMode.DEFAULT);//默认就是DEFAULT，所以这里可以不用设置，DEFAULT代表走Http标准协议。
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
