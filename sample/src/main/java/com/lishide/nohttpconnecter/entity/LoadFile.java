package com.lishide.nohttpconnecter.entity;

import com.lishide.nohttpconnecter.application.MyApplication;

public class LoadFile {

    private String title;
    private int progress;

    public LoadFile() {
    }

    public LoadFile(String title, int progress) {
        this.title = title;
        this.progress = progress;
    }

    public LoadFile(int title, int progress) {
        this.title = MyApplication.getInstance().getString(title);
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int title) {
        this.title = MyApplication.getInstance().getString(title);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
