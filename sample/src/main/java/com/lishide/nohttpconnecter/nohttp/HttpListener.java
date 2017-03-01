package com.lishide.nohttpconnecter.nohttp;

import com.yanzhenjie.nohttp.rest.Response;

/**
 * <p>接受回调结果.</p>
 * Created in 2017/1/1 15:09.
 *
 * @author Li Shide.
 */
public interface HttpListener<T> {

    void onSucceed(int what, Response<T> response);

    void onFailed(int what, Response<T> response);

}
