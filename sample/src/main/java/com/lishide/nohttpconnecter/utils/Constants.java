package com.lishide.nohttpconnecter.utils;

public class Constants {

    /**
     * 服务器地址.
     */
    public static final String SERVER = "http://api.nohttp.net/";

    /**
     * 各种方法测试。
     */
    public static final String URL_NOHTTP_METHOD = SERVER + "method";

    /**
     * 支持304缓存的接口--返回text。
     */
    public static final String URL_NOHTTP_CACHE_STRING = SERVER + "cache";

    /**
     * 支持304缓存的接口--返回image。
     */
    public static final String URL_NOHTTP_CACHE_IMAGE = SERVER + "imageCache";

    /**
     * 请求图片的接口，支持各种方法。
     */
    public static final String URL_NOHTTP_IMAGE = SERVER + "image";
    /**
     * 上传文件接口。
     */
    public static final String URL_NOHTTP_UPLOAD = SERVER + "upload";
    /**
     * 下载文件。
     */
    public static final String[] URL_DOWNLOADS = {
            "http://api.nohttp.net/download/1.apk",
            "http://api.nohttp.net/download/2.apk",
            "http://api.nohttp.net/download/3.apk",
            "http://api.nohttp.net/download/4.apk"
    };
}
