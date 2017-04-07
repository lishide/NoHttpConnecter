package com.lishide.nohttpconnecter.activity.upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.adapter.RvMultiAdapter;
import com.lishide.nohttpconnecter.config.AppConfig;
import com.lishide.nohttpconnecter.entity.ListItemInfo;
import com.lishide.nohttpconnecter.listener.OnItemClickListener;
import com.lishide.nohttputils.dialog.WaitDialog;
import com.yanzhenjie.nohttp.tools.IOUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 上传文件示例
 */
public class UploadActivity extends BaseActivity {

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_upload);
    }

    @Override
    protected void initView() {
        List<ListItemInfo> listItems = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.activity_upload);
        for (int i = 0; i < titles.length; i++) {
            listItems.add(new ListItemInfo(titles[i], ""));
        }
        RecyclerView mRvUpload = ButterKnife.findById(this, R.id.rv_upload_activity);
        mRvUpload.setLayoutManager(new LinearLayoutManager(this));
        mRvUpload.setItemAnimator(new DefaultItemAnimator());
        RvMultiAdapter listAdapter = new RvMultiAdapter(listItems, mItemClickListener);
        mRvUpload.setAdapter(listAdapter);

    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_upload_activity));

        if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            saveFile();
        else
            AndPermission.with(this)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .requestCode(100)
                    .send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults,
                new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantPermissions) {
                        AppConfig.getInstance().initialize();
                        saveFile();
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                        finish();
                    }
                });
    }

    /**
     * list item 单击
     */
    private OnItemClickListener mItemClickListener = (v, position) -> {
        Intent intent = null;
        switch (position) {
            case 0:// 上传单个文件
                intent = new Intent(this, UploadSingleFileActivity.class);
                break;
            case 1:// 上传多个文件
//                intent = new Intent(this, UploadSingleFileActivity.class);
                break;
            case 2:// 上传文件 List
//                intent = new Intent(this, UploadSingleFileActivity.class);
                break;
            case 3:// 从相册选择图片上传
                intent = new Intent(this, UploadAlbumActivity.class);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    };

    /* ====================先保存文件到SD卡==================== */

    private WaitDialog mDialog;

    private void saveFile() {
        mDialog = new WaitDialog(this);
        mDialog.show();
        new Thread(saveFileThread).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mDialog.dismiss();
        }
    };

    private Runnable saveFileThread = () -> {
        try {
            AppConfig.getInstance().initialize();

            InputStream inputStream = getAssets().open("123.jpg");
            IOUtils.write(inputStream, new FileOutputStream(AppConfig.getInstance().APP_PATH_ROOT
                    + File.separator + "image1.jpg"));
            IOUtils.closeQuietly(inputStream);

            inputStream = getAssets().open("234.jpg");
            IOUtils.write(inputStream, new FileOutputStream(AppConfig.getInstance().APP_PATH_ROOT
                    + File.separator + "image2.jpg"));
            IOUtils.closeQuietly(inputStream);

            inputStream = getAssets().open("456.png");
            IOUtils.write(inputStream, new FileOutputStream(AppConfig.getInstance().APP_PATH_ROOT
                    + File.separator + "image3.png"));
            IOUtils.closeQuietly(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.obtainMessage().sendToTarget();
    };
}
