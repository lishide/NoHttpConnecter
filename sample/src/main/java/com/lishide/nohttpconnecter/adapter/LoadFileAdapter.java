
package com.lishide.nohttpconnecter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.entity.LoadFile;

import java.util.List;

/**
 * <p>文件列表适配器</p>
 */
public class LoadFileAdapter extends RecyclerView.Adapter<LoadFileAdapter.DownloadFileViewHolder> {

    /**
     * 需要加载的文件列表
     */
    private List<LoadFile> loadFiles;

    public LoadFileAdapter(List<LoadFile> loadFiles) {
        this.loadFiles = loadFiles;
    }

    @Override
    public int getItemCount() {
        return loadFiles == null ? 0 : loadFiles.size();
    }

    @Override
    public DownloadFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadFileViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_load_file_status, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadFileViewHolder holder, int position) {
        holder.setData();
    }

    class DownloadFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * 文件状态
         */
        TextView mTvResult;
        /**
         * 文件进度
         */
        ProgressBar mPbProgress;

        public DownloadFileViewHolder(View itemView) {
            super(itemView);
            mPbProgress = (ProgressBar) itemView.findViewById(R.id.pb_progress);
            mTvResult = (TextView) itemView.findViewById(R.id.tv_result);
            itemView.setOnClickListener(this);
        }

        public void setData() {
            LoadFile loadFile = loadFiles.get(getAdapterPosition());
            mTvResult.setText(loadFile.getTitle());
            mPbProgress.setProgress(loadFile.getProgress());
        }

        @Override
        public void onClick(View v) {
        }
    }

}
