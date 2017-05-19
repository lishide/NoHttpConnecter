package com.lishide.nohttpconnecter.activity.upload;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.config.AppConfig;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.List;

/**
 * <p>上传单个文件</p>
 */
public class UploadSingleFileActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 单个文件上传监听的标志
     */
    private final int WHAT_UPLOAD_SINGLE = 0x01;

    /**
     * 文件的上传状态
     */
    private TextView mTvResult;
    /**
     * 进度条
     */
    private ProgressBar mPbProgress;
    private Button mBtnUploadDemo;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_upload_single);
    }

    @Override
    protected void initView() {
        mBtnUploadDemo = (Button) findViewById(R.id.btn_upload_request_demo);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mPbProgress = (ProgressBar) findViewById(R.id.pb_progress);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle("单个文件上传");

        mBtnUploadDemo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_request_demo:
                if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    uploadSingleFile();
                else
                    AndPermission.with(this)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .requestCode(100)
                            .callback(permissionListener)
                            .start();
                break;
        }
    }

    /**
     * 上传单个文件
     */
    private void uploadSingleFile() {
        mTvResult.setText(null);
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_UPLOAD, RequestMethod.POST);

        // 添加普通参数。
        request.add("user", "yolanda");

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBitnary、BitmapBinary。

        // FileBinary用法
        String filePath = AppConfig.getInstance().APP_PATH_ROOT + "/image1.jpg";
        BasicBinary binary = new FileBinary(new File(filePath));

        /**
         * 监听上传过程，如果不需要监听就不用设置。
         * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
         * 第二个参数： 监听器。
         */
        binary.setUploadListener(WHAT_UPLOAD_SINGLE, mOnUploadListener);

        request.add("image0", binary);// 添加1个文件
//            request.add("image1", fileBinary1);// 添加2个文件

        startRequest(0, request, httpListener, false, true);
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            showMessageDialog(R.string.request_succeed, response.get());
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            showMessageDialog(R.string.request_failed, response.getException().getMessage());
        }
    };

    /**
     * 文件上传监听
     */
    private OnUploadListener mOnUploadListener = new OnUploadListener() {

        @Override
        public void onStart(int what) {// 这个文件开始上传
            mTvResult.setText(R.string.upload_start);
        }

        @Override
        public void onCancel(int what) {// 这个文件的上传被取消
            mTvResult.setText(R.string.upload_cancel);
        }

        @Override
        public void onProgress(int what, int progress) {// 这个文件的上传进度发生变化
            mPbProgress.setProgress(progress);
        }

        @Override
        public void onFinish(int what) {// 文件上传完成
            mTvResult.setText(R.string.upload_succeed);
        }

        @Override
        public void onError(int what, Exception exception) {// 文件上传发生错误
            mTvResult.setText(R.string.upload_error);
        }
    };

    /**
     * 权限回调监听
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            uploadSingleFile();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
        }
    };
}