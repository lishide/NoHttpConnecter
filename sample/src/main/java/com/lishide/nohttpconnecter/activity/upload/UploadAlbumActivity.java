package com.lishide.nohttpconnecter.activity.upload;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.nohttp.HttpListener;
import com.lishide.nohttputils.utils.Toast;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>从相册选择图片上传</p>
 */
public class UploadAlbumActivity extends BaseActivity {

    /**
     * 相册选择回调
     */
    private static final int RESULT_BACK_ALBUM = 0x01;

    /**
     * 展示选择的图片
     */
    @BindView(R.id.iv_icon)
    ImageView mIvIcon;
    /**
     * 选择的图片路径
     */
    private String mImagePath;
    /**
     * 显示状态
     */
    @BindView(R.id.tv_result)
    TextView mTvResult;
    /**
     * 显示进度
     */
    @BindView(R.id.pb_progress)
    ProgressBar mProgressBar;

    private ArrayList<String> mImageList;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_upload_album);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_upload_album_activity));
    }

    /**
     * 按钮点击
     */
    @OnClick({R.id.btn_album, R.id.btn_start})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_album) {
            if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                selectImageFormAlbum();
            else
                AndPermission.with(this)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .requestCode(100)
                        .callback(permissionListener)
                        .start();
        } else if (v.getId() == R.id.btn_start) {
            upload();
        }
    }

    /**
     * 权限回调监听
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            selectImageFormAlbum();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
        }
    };

    /**
     * 选择图片
     */
    private void selectImageFormAlbum() {
        Album.album(this)
                .requestCode(RESULT_BACK_ALBUM) // 请求码，返回时onActivityResult()的第一个参数。
                .toolBarColor(ContextCompat.getColor(this, R.color.colorPrimary)) // Toolbar 颜色，默认蓝色。
                .statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)) // StatusBar 颜色，默认蓝色。
                .navigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary)) // NavigationBar 颜色，默认黑色，建议使用默认。
                .title("图库") // 配置title。
                .selectCount(1) // 最多选择几张图片。
                .columnCount(2) // 相册展示列数，默认是2列。
                .camera(true) // 是否有拍照功能。
                .checkedList(mImageList) // 已经选择过得图片，相册会自动选中选过的图片，并计数。
                .start();
    }

    /**
     * 上传图片
     */
    private void upload() {
        if (TextUtils.isEmpty(mImagePath))
            Toast.show(context, R.string.upload_file_select_album_null);
        else {
            File file = new File(mImagePath);
            if (file.exists())
                executeUpload(file);
            else
                Toast.show(context, R.string.upload_file_select_album_null_again);
        }
    }

    /**
     * 执行上传任务
     */
    private void executeUpload(File file) {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_UPLOAD, RequestMethod.POST);

        // 添加普通参数
        request.add("user", "yolanda");

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBinary、BitmapBinary。
        FileBinary fileBinary0 = new FileBinary(file);
        /**
         * 监听上传过程，如果不需要监听就不用设置。
         * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
         * 第二个参数： 监听器。
         */
        fileBinary0.setUploadListener(0, mOnUploadListener);

        request.add("userHead", fileBinary0);// 添加1个文件

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
        public void onStart(int what) {// 这个文件开始上传。
            mTvResult.setText(R.string.upload_start);
        }

        @Override
        public void onCancel(int what) {// 这个文件的上传被取消时。
            mTvResult.setText(R.string.upload_cancel);
        }

        @Override
        public void onProgress(int what, int progress) {// 这个文件的上传进度发生边耍
            mProgressBar.setProgress(progress);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_BACK_ALBUM) {
            if (resultCode == RESULT_OK) { // Successfully.
                // 不要质疑你的眼睛，就是这么简单。
                mImageList = Album.parseResult(data);
                mImagePath = mImageList.get(0);
                Glide.with(context)
                        .load(mImagePath)
                        .into(mIvIcon);
            } else if (resultCode == RESULT_CANCELED) { // User canceled.
                // 用户取消了操作。
            }
        }
    }

}
