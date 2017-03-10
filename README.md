# NoHttpConnecter
基于 NoHttp 的封装，主要包括字符串、Bitmap、JsonArray 等的 GET 和 POST 请求、文件上传下载方法的简单封装，以及五种缓存模式的使用。

首先对 NoHttp 网络框架做一个简介
> Nohttp 是一个 Android Http 标准框架，底层可动态切换 OkHttp、URLConnection，与 RxJava 完美结合，支持缓存数据到数据库或 SD 卡（缓存数据自动加密），支持请求 Restful 风格的接口，比 Retrofit 更简单易用。

> **Nohttp** 框架特性
* 动态配置底层框架为 **OkHttp**、HttpURLConnection
* 支持异步请求、支持同步请求
* 多文件上传，支持大文件上传，表单提交数据
* 文件下载、上传下载、上传和下载的进度回调、错误回调
* 支持 Json、xml、Map、List 的提交
* 完美的 Http 缓存模式，可指定缓存到数据库、SD 卡，缓存数据已安全加密
* 自定义 Request，直接请求 JsonObject、JavaBean 等
* Cookie 的自动维持，App 重启、关开机后还持续维持
* http 301 302 303 304 307 重定向，支持多层嵌套重定向
* Https、自签名网站 Https 的访问、支持双向验证
* 失败重试机制，支持请求优先级
* GET、POST、PUT、PATCH、HEAD、DELETE、OPTIONS、TRACE 等请求协议
* 用队列保存请求，平均分配多线程的资源，支持多个请求并发
* 支持取消某个请求、取消指定多个请求、取消所有请求

这么多好用的功能，难道你不想试试？

NoHttp 开源框架地址：[https://github.com/yanzhenjie/NoHttp](https://github.com/yanzhenjie/NoHttp)

### 使用方法

#### 1. Gradle添加依赖（推荐）

```java
compile 'com.yanzhenjie.nohttp:okhttp:1.1.1'
```

#### 2. 需要的权限

```java
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### 3. 初始化
初始化 NoHttp，并设置 NoHttp 底层采用那种网络框架去请求，建议把初始化方法放到 **Application** 中 *onCreate* 生命周期方法里面。还有别忘了在`manifest.xml`中注册`Application`。

```java
//初始化 NoHttp
NoHttp.initialize(this, new NoHttp.Config()
        .setConnectTimeout(30 * 1000)  // 设置全局连接超时时间，单位毫秒，默认10s。
        .setReadTimeout(30 * 1000)  // 设置全局服务器响应超时时间，单位毫秒，默认10s。
        // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
        .setCacheStore(
                new DBCacheStore(this).setEnable(true) // 如果不使用缓存，设置setEnable(false)禁用。
        )
        // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
        .setCookieStore(
                new DBCookieStore(this).setEnable(true) // 如果不维护cookie，设置false禁用。
        )
        // 配置网络层，默认使用URLConnection，如果想用OkHttp：OkHttpNetworkExecutor。
        .setNetworkExecutor(new OkHttpNetworkExecutor())
);
```

#### 4.接下来，你就可以愉快的进行网络请求了：

 - new 队列

  ```java
  RequestQueue requestQueue = NoHttp.newRequestQueue();
  ```

 - new 请求

  比如这样，

  ```java
  Request<String> request = NoHttp.createStringRequest(url, RequestMethod.GET);
  ```

  或者这样，

  ```java
  Request<JSONObject> objRequest = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
  ```

  ...等等（支持更多，如 JsonArray、Bitmap、byte[] 或自定义请求类型）。然后把需要的请求参数添加进来：

  ```java
  .add("name", "name") // String类型
  ...
  ```

 - 把请求添加到队列，完成请求

  ```java
  requestQueue.add(what, request, responseListener);
  ```

 - 回调对象，接受请求结果

  处理成功、失败等方法的回调，实现当前界面的业务和逻辑。

  > * 添加请求到队列时有一个what，这个what会在`responseLisetener`响应时回调回来，所以可以用一个`responseLisetener`接受多个请求的响应，用 what 来区分结果。
  > * **强烈建议**把生成队列写成懒汉单例模式，因为每新建队列就会 new 出相应个数的线程来，同时只有线程数固定了，队列的作用才会发挥到最大。

 - 取消请求

  在组件销毁的时候（*onDestroy()*）调用队列的按照 sign 取消的方法即可取消

这时我们发现有很多重复的操作，每个 Activity 和 Fragment 都这么写就显得有点麻烦了，再加上上面的两条重要提示，所以我们这里把队列进行单例模式封装，并把这些操作封装在 `BaseActivity`、`BaseFragment` 中。

**对 NoHttp 的封装，请看源码：HttpResponseListener、HttpListener、CallServer、BaseActivity 等。**

### 五大缓存模式
 - 1、Default 模式，实现 http304 重定向缓存

  ```java
  request.setCacheMode(CacheMode.DEFAULT);
  ```

 - 2、请求网络失败返回缓存

  ```java
  request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
  ```

 - 3、没有缓存才去请求网络

  ```java
  request.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
  ```

 - 4、仅仅请求网络

  ```java
  request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
  ```

 - 5、仅仅读取缓存

  ```java
  request.setCacheMode(CacheMode.ONLY_READ_CACHE);
  ```

### 文件下载
 - 1、单个文件下载
 - 2、多个文件下载

文件下载也是队列，队列和开头所说的请求的队列是一样的。

 - 发起下载请求

  ```java
  mDownloadRequest = NoHttp.createDownloadRequest(url, path, filename, true, true);
  downloadQueue.add(0, mDownloadRequest, downloadListener);
  ```

 - 暂停或者停止下载

  ```java
  mDownloadRequest.cancel();
  ```

 - 监听下载过程

  ```java
  private DownloadListener downloadListener = new DownloadListener() {

	// 下载开始
    @Override
    public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {

    }

	// 下载发生错误
    @Override
    public void onDownloadError(int what, Exception exception) {

    }

	// 更新下载进度和下载网速
    @Override
    public void onProgress(int what, int progress, long fileCount, long speed) {

    }

	// 下载完成
    @Override
    public void onFinish(int what, String filePath) {

    }

	// 下载被取消或者暂停
    @Override
    public void onCancel(int what) {

    }
};
  ```

关于文件下载，具体的请参考 Demo。

### 文件上传
 - 1、单个文件上传

  ```java
  Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
  request.add("file", new FileBinary(file));
  ```

 - 2、多个文件上传
 	这里可以添加各种形式的文件，File、Bitmap、InputStream、ByteArray
 	- **多个Key多个文件形式**
    ```java
    Request<String> request = ...
    request.add("file1", new FileBinary(File));
    request.add("file2", new FileBinary(File));
    request.add("file3", new InputStreamBinary(InputStream));
    request.add("file4", new ByteArrayBinary(byte[]));
    request.add("file5", new BitmapBinary(Bitmap));
    ```

 	- **一个Key多个文件形式**

    ```java
    Request<String> request = ...
    fileList.add("image", new FileBinary(File));
    fileList.add("image", new InputStreamBinary(InputStream));
    fileList.add("image", new ByteArrayBinary(byte[]));
    fileList.add("image", new BitmapBinary(Bitmap));
    ```

	或者：

    ```java
    Request<String> request = ...
    List<Binary> fileList = ...
    fileList.add(new FileBinary(File));
    fileList.add(new InputStreamBinary(InputStream));
    fileList.add(new ByteArrayBinary(byte[]));
    fileList.add(new BitmapStreamBinary(Bitmap));
    request.add("file_list", fileList);
    ```

......

---
本人仅是简单地对 NoHttp 网络请求框架进行轻量级的封装，后期还会进行持续维护，更多关于 NoHttp 的使用可直接查看原作。

**NoHttp —— 一个有情怀的网络框架 ，让你的网络请求更简单。**

像上面说的一样，NoHttp 真的很强大、很好用，嗯，没错。

未完待续。。。
