package com.lishide.nohttputils.nohttp;

import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by lishide on 2017/3/1.
 * <p>接受回调结果</p>
 */
public interface HttpListener<T> {

    void onSucceed(int what, Response<T> response);

    void onFailed(int what, Response<T> response);

}
