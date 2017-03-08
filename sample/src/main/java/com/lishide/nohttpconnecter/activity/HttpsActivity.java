package com.lishide.nohttpconnecter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.utils.SSLContextUtil;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import javax.net.ssl.SSLContext;

public class HttpsActivity extends BaseActivity implements View.OnClickListener,
        HttpListener<String> {

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_https);
    }

    @Override
    protected void initView() {
        Button mBtnHttpsVerify = (Button) findViewById(R.id.btn_https_verify);
        Button mBtnHttpsNoVerify = (Button) findViewById(R.id.btn_https_no_verify);
        mBtnHttpsVerify.setOnClickListener(this);
        mBtnHttpsNoVerify.setOnClickListener(this);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle("Https 请求");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_https_verify:
                httpsVerify();
                break;
            case R.id.btn_https_no_verify:
                httpsNoVerify();
                break;
        }
    }

    /**
     * Https请求，带证书。
     */
    private void httpsVerify() {
        Request<String> httpsRequest = NoHttp.createStringRequest("https://kyfw.12306.cn/otn/",
                RequestMethod.GET);
        SSLContext sslContext = SSLContextUtil.getSSLContext();

        // 主要是需要一个SocketFactory对象，这个对象是java通用的，具体用法还请Google、Baidu。
        if (sslContext != null)
            httpsRequest.setSSLSocketFactory(sslContext.getSocketFactory());
        request(0, httpsRequest, this, false, true);
    }

    /**
     * Https请求，不带证书。
     */
    private void httpsNoVerify() {
        Request<String> httpsRequest = NoHttp.createStringRequest("https://kyfw.12306.cn/otn/",
                RequestMethod.GET);
        SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
        if (sslContext != null)
            httpsRequest.setSSLSocketFactory(sslContext.getSocketFactory());
        httpsRequest.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
        request(0, httpsRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        showMessageDialog(R.string.request_succeed, response.get());
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        showMessageDialog(R.string.request_failed, response.getException().getMessage());
    }
}
