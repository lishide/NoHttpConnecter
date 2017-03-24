package com.lishide.nohttpconnecter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.List;
import java.util.Locale;

public class MethodActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnGet;
    private Button mBtnPost;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_method);
    }

    @Override
    protected void initView() {
        mBtnGet = (Button) findViewById(R.id.btn_get);
        mBtnPost = (Button) findViewById(R.id.btn_post);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle("各种请求协议演示");

        mBtnGet.setOnClickListener(this);
        mBtnPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Request<String> request = null;
        switch (v.getId()) {
            case R.id.btn_get:
                request = NoHttp.createStringRequest(Constants.URL_NOHTTP_METHOD, RequestMethod.GET);
                break;
            case R.id.btn_post:
                request = NoHttp.createStringRequest(Constants.URL_NOHTTP_METHOD, RequestMethod.POST);
                break;
        }
        if (request != null) {
            request.add("name", "yanzhenjie");// String类型
            request.add("pwd", 123);
            request.add("userAge", 20);// int类型
            request.add("userSex", '1');// char类型，还支持其它类型

            // 添加到请求队列
            startRequest(0, request, httpListener, true, true);
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (response.getHeaders().getResponseCode() == 501) {
                showMessageDialog(R.string.request_succeed, R.string.request_method_patch);
            } else if (RequestMethod.HEAD == response.request().getRequestMethod()) {// 请求方法为HEAD时没有响应内容
                showMessageDialog(R.string.request_succeed, R.string.request_method_head);
            } else if (response.getHeaders().getResponseCode() == 405) {
                List<String> allowList = response.getHeaders().getValues("Allow");
                String allow = getString(R.string.request_method_not_allow);
                if (allowList != null && allowList.size() > 0) {
                    allow = String.format(Locale.getDefault(), allow, allowList.get(0));
                }
                showMessageDialog(R.string.request_succeed, allow);
            } else {
                showMessageDialog(R.string.request_succeed, response.get());
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            showMessageDialog(R.string.request_failed, response.getException().getMessage());
        }
    };
}
