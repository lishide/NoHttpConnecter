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
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 图片（或其他类型文件）上传示例
 * <p>
 * 附本人用上传方法，详见代码和注释，包括 Android 端上传方式及 API，
 * 本功能以图片上传为例，使用严大的 Album 开源相册作为图片选取工具，
 * 另外使用 Luban 对图片进行了压缩（可选），如不需要压缩，则跳过此方法。
 * 其他文件上传方式与此类似，打开相应的系统文件管理软件选择文件即可。
 */
public class UploadAlbumDemoActivity extends BaseActivity {

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
    private String picName;
    private String imgLsFile; // Luban 压缩后图片绝对路径

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
        mToolbar.setTitle(getString(R.string.title_upload_album_demo_activity));
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
                        .send();
        } else if (v.getId() == R.id.btn_start) {
            upload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults,
                new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantPermissions) {
                        selectImageFormAlbum();
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                    }
                });
    }

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
            if (file.exists()) {
                Toast.show(context, "暂时关闭了文件上传的 URL，下面的功能是好用的。如果需要测试，打开下面一行代码。");
                //compressWithLs(file);
            } else {
                Toast.show(context, R.string.upload_file_select_album_null_again);
            }
        }
    }

    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(File file) {
        Luban.get(this)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setFilename(System.currentTimeMillis() + "")
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        imgLsFile = file.getAbsolutePath();
                        uploadFileTest();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    /**
     * 执行上传任务
     * 提交参数（"uploadedfile", file）及PHP服务端接收文件的代码
     * 另附一篇参考博文：http://www.jianshu.com/p/46758ea483dd
     */
    private void uploadFileTest() {
        String url_upload_test_own = "http://api.magicmirrormedia.cn/market/up_file.php";
        Request<String> request = NoHttp.createStringRequest(url_upload_test_own, RequestMethod.POST);

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBinary、BitmapBinary。
        FileBinary binary = new FileBinary(new File(imgLsFile), picName);
        /**
         * 监听上传过程，如果不需要监听就不用设置。
         * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
         * 第二个参数： 监听器。
         */
        binary.setUploadListener(0, mOnUploadListener);

        request.add("uploadedfile", binary);// 添加1个文件
        startRequest(0, request, httpListener, false, true);
        /*<?php
            $filename = '';
            if(isset($_REQUEST['name'])){
                $filename = $_REQUEST['name'];
                $filename = $filename . '.' . strtolower(pathinfo($_FILES['uploadedfile']['name'], PATHINFO_EXTENSION));
            }
            $target_path  = "./img/";//接收文件目录
            if($filename){
                $target_path = $target_path . $filename;
            }else{
                $target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
            }

            if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
                //echo "The file ".  basename( $_FILES['uploadedfile']['name']). " has been uploaded";
                //echo 0;
                $result = 0;
            }  else{
                //echo "There was an error uploading the file, please try again!" . $_FILES['uploadedfile']['error'];
                //echo 1;
                $result = 1;
            }

            $arr = array(
                    'result' => $result,
                    'filename' => $filename
        );

            $strr = json_encode($arr);
            echo($strr);

        ?>
        */
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
        public void onError(int what, Exception exception) {
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
