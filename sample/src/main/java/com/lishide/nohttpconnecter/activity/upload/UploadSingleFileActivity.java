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

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
    private Button mBtnUploadTest;
    private String imgOriPath;
    private String picName;
    private String imgLsFile; // Luban 压缩后图片绝对路径

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_upload_single);
    }

    @Override
    protected void initView() {
        mBtnUploadDemo = (Button) findViewById(R.id.btn_upload_request_demo);
        mBtnUploadTest = (Button) findViewById(R.id.btn_upload_request_test);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mPbProgress = (ProgressBar) findViewById(R.id.pb_progress);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle("单个文件上传");

        mBtnUploadDemo.setOnClickListener(this);
        mBtnUploadTest.setOnClickListener(this);
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
                            .send();
                break;
            case R.id.btn_upload_request_test:
                RxGalleryFinal
                        .with(context)
                        .image()
                        .radio()
                        .crop()
                        .imageLoader(ImageLoaderType.GLIDE)
                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent)
                                    throws Exception {
                                imgOriPath = imageRadioResultEvent.getResult().getOriginalPath();
                                picName = getFileName();

                                compressWithLs(new File(imgOriPath));
                            }
                        })
                        .openGallery();
                break;
        }
    }

    private String getFileName() {
        //UUID uuid = UUID.randomUUID();//生成随机文件名
        long timeMillis = System.currentTimeMillis();
        return "li_IMG_" + timeMillis + ".jpg";
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
     * 附：我公司使用的文件上传方式，提交参数（"uploadedfile", file）及PHP服务端接收文件的代码
     * 另附一篇参考博文：http://www.jianshu.com/p/46758ea483dd
     */
    private void uploadFileTest() {
        String url_upload_test_own = "http://api.magicmirrormedia.cn/market/up_file.php";
        Request<String> request = NoHttp.createStringRequest(url_upload_test_own, RequestMethod.POST);

        FileBinary binary = new FileBinary(new File(imgLsFile), picName);
        binary.setUploadListener(WHAT_UPLOAD_SINGLE, mOnUploadListener);

        request.add("uploadedfile", binary);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults,
                new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantPermissions) {
                        uploadSingleFile();
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                    }
                });
    }
}