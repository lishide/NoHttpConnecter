package com.lishide.nohttpconnecter.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.adapter.LoadFileAdapter;
import com.lishide.nohttpconnecter.config.AppConfig;
import com.lishide.nohttpconnecter.entity.LoadFile;
import com.lishide.nohttpconnecter.utils.Constants;
import com.lishide.nohttputils.utils.Toast;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.tools.IOUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 1、下载单个文件
 * 2、下载多个文件。这里为了简单就把下载写在当前activity中，建议封装到service中。
 */
public class DownloadFileActivity extends BaseActivity implements View.OnClickListener {
    //-------------- 下载单个文件 —— 开始 -----------//
    private final static String PROGRESS_KEY_SINGLE = "download_single_progress";
    /**
     * 下载按钮、暂停、开始等
     */
    private Button mBtnStart;
    /**
     * 删除文件
     */
    private Button mBtnDelete;

    /**
     * 下载状态
     */
    private TextView mTvResult;
    /**
     * 下载进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 下载请求
     */
    private DownloadRequest mDownloadRequest;
    //-------------- 下载单个文件 —— 结束 -----------//


    //-------------- 下载多个文件 —— 开始 -----------//
    /**
     * 文件下载进度记录
     */
    private final static String PROGRESS_KEY_LIST = "download_list_progress";

    /**
     * 文件列表适配器
     */
    private LoadFileAdapter mLoadFileAdapter;
    /**
     * 文件列表
     */
    private List<LoadFile> mFileList;

    /**
     * 下载任务列表
     */
    private List<DownloadRequest> mDownloadRequests;
    private Button mBtnMultiStart;
    private Button mBtnMultiDelete;
    //-------------- 下载多个文件 —— 结束 -----------//

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_download_file);
    }

    @Override
    protected void initView() {
        mBtnStart = (Button) findViewById(R.id.btn_single_start_download);
        mBtnDelete = (Button) findViewById(R.id.btn_single_delete_file);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
        mTvResult = (TextView) findViewById(R.id.tv_result);

        initMultiDownLoad();
    }

    /**
     * 初始化多文件下载
     */
    private void initMultiDownLoad() {
        mBtnMultiStart = (Button) findViewById(R.id.btn_multi_start_download);
        mBtnMultiDelete = (Button) findViewById(R.id.btn_multi_delete_file);
        mFileList = new ArrayList<>();
        mDownloadRequests = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            // 读取每个文件的进度
            int progress = AppConfig.getInstance().getInt(PROGRESS_KEY_LIST + i, 0);

            // 设置每个文件的状态
            String title = getString(R.string.upload_file_status_wait);
            if (progress == 100)
                title = getString(R.string.download_status_finish);
            else if (progress > 0)
                title = getSProgress(progress, 0);

            LoadFile downloadFile = new LoadFile(title, progress);
            mFileList.add(downloadFile);

            /*
             * 这里不传文件名称、不断点续传，则会从响应头中读取文件名自动命名，如果响应头中没有则会从url中截取。
             */
            // url 下载地址。
            // fileFolder 文件保存的文件夹。
            // isDeleteOld 在指定的文件夹发现同名的文件是否删除后重新下载，true则删除重新下载，false则直接通知下载成功。
            // mDownloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[0], AppConfig.getInstance()
            // .APP_PATH_ROOT, true);

            /*
             * 如果要使用断点续传下载，则一定要指定文件名。
             */
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 在指定的文件夹发现同名的文件是否删除后重新下载，true则删除重新下载，false则直接通知下载成功。
            DownloadRequest downloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[i], AppConfig
                    .getInstance().APP_PATH_ROOT, "nohttp_list" + i + ".apk", true, true);
            mDownloadRequests.add(downloadRequest);
        }

        mLoadFileAdapter = new LoadFileAdapter(mFileList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_download_file);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mLoadFileAdapter);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle("文件下载");
        mBtnStart.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnMultiStart.setOnClickListener(this);
        mBtnMultiDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_single_start_download:
                if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downloadSingle();
                } else {
                    AndPermission.with(this)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .requestCode(99)
                            .send();
                }
                break;
            case R.id.btn_single_delete_file:
                IOUtils.delFileOrFolder(AppConfig.getInstance().APP_PATH_ROOT + "/nohttp.apk");
                Toast.show(context, R.string.delete_succeed);
                break;
            case R.id.btn_multi_start_download:
                if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downloadMulti();
                } else {
                    AndPermission.with(this)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .requestCode(100)
                            .send();
                }
                break;
            case R.id.btn_multi_delete_file:
                if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    deleteMulti();
                } else {
                    AndPermission.with(this)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .requestCode(101)
                            .send();
                }
                break;
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
                        switch (requestCode) {
                            case 99:
                                downloadSingle();
                                break;
                            case 100:
                                downloadMulti();
                                break;
                            case 101:
                                deleteMulti();
                                break;
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                    }
                });
    }

    /**
     * 开始下载单个文件
     */
    private void downloadSingle() {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。

            /*
             * 这里不传文件名称、不断点续传，则会从响应头中读取文件名自动命名，如果响应头中没有则会从url中截取。
             */
            // url 下载地址。
            // fileFolder 文件保存的文件夹。
            // isDeleteOld 在指定的文件夹发现同名的文件是否删除后重新下载，true则删除重新下载，false则直接通知下载成功。
            // mDownloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[0], AppConfig.getInstance()
            // .APP_PATH_ROOT, true);

            /*
             * 如果使用断点续传的话，一定要指定文件名。
             */
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 在指定的文件夹发现同名的文件是否删除后重新下载，true则删除重新下载，false则直接通知下载成功。
            mDownloadRequest = NoHttp.createDownloadRequest(
                    Constants.URL_DOWNLOADS[0], AppConfig.getInstance().APP_PATH_ROOT, "nohttp.apk", true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            NoHttp.getDownloadQueueInstance().add(0, mDownloadRequest, downloadSingleListener);

            // 添加到队列，在没响应的时候让按钮不可用。
            mBtnStart.setEnabled(false);
        }
    }

    /**
     * 单个文件下载监听
     */
    private DownloadListener downloadSingleListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            int progress = AppConfig.getInstance().getInt(PROGRESS_KEY_SINGLE, 0);
            if (allCount != 0) {
                progress = (int) (beforeLength * 100 / allCount);
                mProgressBar.setProgress(progress);
            }
            updateProgress(progress, 0);

            mBtnStart.setText(R.string.download_status_pause);
            mBtnStart.setEnabled(true);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);
            mBtnStart.setText(R.string.download_status_again_download);
            mBtnStart.setEnabled(true);

            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
            mTvResult.setText(message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            updateProgress(progress, speed);
            mProgressBar.setProgress(progress);
            AppConfig.getInstance().putInt(PROGRESS_KEY_SINGLE, progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish, file path: " + filePath);
            Toast.show(context, R.string.download_status_finish);// 提示下载完成
            mTvResult.setText(R.string.download_status_finish);

            mBtnStart.setText(R.string.download_status_re_download);
            mBtnStart.setEnabled(true);
        }

        @Override
        public void onCancel(int what) {
            mTvResult.setText(R.string.download_status_be_pause);
            mBtnStart.setText(R.string.download_status_resume);
            mBtnStart.setEnabled(true);
        }

        private void updateProgress(int progress, long speed) {
            double newSpeed = speed / 1024D;
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            String sProgress = getString(R.string.download_progress);
            sProgress = String.format(Locale.getDefault(), sProgress, progress, decimalFormat.format(newSpeed));
            mTvResult.setText(sProgress);
        }
    };


    /**
     * 开始下载多个文件
     */
    private void downloadMulti() {
        for (int i = 0; i < mDownloadRequests.size(); i++) {
            NoHttp.getDownloadQueueInstance().add(i, mDownloadRequests.get(i), downloadListener);
        }
    }

    /**
     * 多个文件下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            int progress = AppConfig.getInstance().getInt(PROGRESS_KEY_LIST, 0);
            if (allCount != 0) {
                progress = (int) (beforeLength * 100 / allCount);
            }

            updateProgress(what, progress, 0);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);
            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);

            mFileList.get(what).setTitle(message);
            mLoadFileAdapter.notifyItemInserted(what);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            AppConfig.getInstance().putInt(PROGRESS_KEY_LIST + what, progress);
            updateProgress(what, progress, speed);
        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish");

            mFileList.get(what).setTitle(R.string.download_status_finish);
            mLoadFileAdapter.notifyItemChanged(what);
        }

        @Override
        public void onCancel(int what) {
            mFileList.get(what).setTitle(R.string.download_status_be_pause);
        }

        /**
         * 更新进度
         * @param what 哪个item
         * @param progress 进度值
         */
        private void updateProgress(int what, int progress, long speed) {
            mFileList.get(what).setTitle(getSProgress(progress, speed));
            mFileList.get(what).setProgress(progress);
            mLoadFileAdapter.notifyItemChanged(what);
        }
    };

    /**
     * 格式化进度标题
     *
     * @param progress 进度
     * @return 直接可以用的标题
     */
    private String getSProgress(int progress, long speed) {
        double newSpeed = speed / 1024D;
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        String sProgress = getString(R.string.download_progress);
        return String.format(Locale.getDefault(), sProgress, progress, decimalFormat.format(newSpeed));
    }

    /**
     * 删除文件
     */
    private void deleteMulti() {
        for (int i = 0; i < 4; i++) {
            File file = new File(AppConfig.getInstance().APP_PATH_ROOT, "nohttp_list" + i + ".apk");
            IOUtils.delFileOrFolder(file);

            // 还原页面状态。
            AppConfig.getInstance().putInt(PROGRESS_KEY_LIST + i, 0);
            mFileList.get(i).setProgress(0);
            mFileList.get(i).setTitle(getString(R.string.upload_file_status_wait));
            mLoadFileAdapter.notifyItemChanged(i);
        }
    }

    @Override
    protected void onDestroy() {
        // 暂停下载
        if (mDownloadRequest != null) {
            mDownloadRequest.cancel();
        }

        for (DownloadRequest downloadRequest : mDownloadRequests) {
            downloadRequest.cancel();
        }
        super.onDestroy();
    }

}
