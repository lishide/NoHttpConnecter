package com.lishide.nohttpconnecter.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.nohttp.HttpListener;
import com.lishide.nohttpconnecter.utils.Constants;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.List;
import java.util.Locale;

public class GetPostActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnGet;
    private Button mBtnPost;
    private TextView mTvResult;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_get_post);
    }

    @Override
    protected void initView() {
        mBtnGet = (Button) findViewById(R.id.btn_get);
        mBtnPost = (Button) findViewById(R.id.btn_post);
        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void initLogic() {
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
            request(0, request, httpListener, true, true);
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (response.getHeaders().getResponseCode() == 501) {
                Log.e("lishide", "请求成功, 服务器不支持的请求方法。");
            } else if (RequestMethod.HEAD == response.request().getRequestMethod())// 请求方法为HEAD时没有响应内容
                Log.e("lishide", "请求成功, 请求方式为HEAD, 没有响应内容。");
            else if (response.getHeaders().getResponseCode() == 405) {
                List<String> allowList = response.getHeaders().getValues("Allow");
                String allow = "服务器仅仅支持请求方法：%1$s";
                if (allowList != null && allowList.size() > 0) {
                    allow = String.format(Locale.getDefault(), allow, allowList.get(0));
                }
                Log.e("lishide", "请求成功：" + allow);
            } else {
                Log.e("lishide", "请求成功：" + response.get());
                mTvResult.setText("请求成功：" + response.get());
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            Log.e("lishide", "请求失败：" + response.getException().getMessage());
        }
    };
}
