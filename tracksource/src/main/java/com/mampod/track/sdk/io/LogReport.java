package com.mampod.track.sdk.io;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * @package： com.mampod.track.sdk.io
 * @Des: 日志上报请求类
 * @author: Jack-Lu
 * @time:
 * @change:
 * @changtime:
 * @changelog:
 */
public class LogReport {
    public static final String TAG = "OkHttpManger";
    private static OkHttpClient okHttpClient;
    private static LogReport manager;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private LogReport() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Cookie cookie = cookies.get(0);
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }

    public static LogReport getInstance() {
        synchronized (LogReport.class) {
            if (manager == null) {
                return new LogReport();
            }
        }
        return manager;
    }

    /**
     * get请求
     *
     * @param url      请求地址
     * @param callback 请求完成回调
     */
    public void getData(String url, Callback callback) {
        //通过Builder辅助类构建一个Request对象
        Request request = new Request.Builder().get().url(url).build();
        //通过入队的方式,进行异步操作
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 提交单个键值对
     *
     * @param url      请求地址
     * @param key      请求键
     * @param value    请求值
     * @param callback 请求完成回调
     */
    public static void postKeyValuePaire(String url, String key, String value, Callback callback) {
        //提交键值对需要用到FormBody,因为FormBody是继承RequestBody的,所以拥有RequestBody的一切属性
        FormBody formBody = new FormBody.Builder()
                //添加键值对
                .add(key, value)
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 提交多个键值对
     *
     * @param url      提交的路径
     * @param map      用来放置键值对,map的key对应键,value对应值
     * @param callback 请求完成回调
     */
    public static void postKeyValuePaires(String url, Map<String, Object> map, Callback callback) {
        FormBody.Builder build = new FormBody.Builder();
        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                build.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        FormBody formBody = build.build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 提交多个键值对
     *
     * @param url      提交的路径
     * @param params   用来放置键值对,map的key对应键,value对应值
     * @param callback 请求完成回调
     */
    public static void postKeyValuePaires(String url, Map<String, String> headers, Map<String, String> params, Callback callback) {
        FormBody.Builder build = new FormBody.Builder();
        if (params != null) {
            //增强for循环遍历
            for (Map.Entry<String, String> entry : params.entrySet()) {
                build.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody formBody = build.build();

        Request request = new Request.Builder()
                .headers(Headers.of(headers))
                .post(formBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 上传单一文件
     *
     * @param url      提交的路径
     * @param file     文件对象
     * @param callback 请求完成回调
     */
    public void upLoadFile(String url, File file, Callback callback) {
        // //提交键值对需要用到MultipartBody,因为MultipartBody是继承RequestBody的,
        // 所以拥有RequestBody的一切属性,类似于javaEE中的表单提交
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(
                MediaType.parse(getMediaType(file.getName())), file);
        //这里的uploadfile是文件上传的标识,用于服务器识别文件
        builder.addFormDataPart("uploadfile", file.getName(), fileBody);
        MultipartBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 上传多个文件
     *
     * @param url      提交的路径
     * @param files    文件数组
     * @param callback 请求完成回调
     */
    public void upLoadFiles(String url, File[] files, Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.length; i++) {
            RequestBody fileBody = RequestBody.create(MediaType.parse(getMediaType(files[i].getName())), files[i]);
            builder.addFormDataPart("uploadfile", files[i].getName(), fileBody);
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 上传多个文件和参数
     * *@param url 提交的路径
     *
     * @param map      用来放置键值对,map的key对应键,value对应值,参数文件混合
     * @param callback 请求完成回调
     */
    public void upLoadMultiFiles(String url, File[] files, Map<String, Object> map, Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //添加文件
        if (files.length != 0) {
            for (int i = 0; i < files.length; i++) {
                RequestBody fileBody = RequestBody.create(
                        MediaType.parse(getMediaType(files[i].getName())), files[i]);
                builder.addFormDataPart("uploadfile", files[i].getName(), fileBody);
            }
        }
        //添加参数
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    /**
     * 根据文件的名称判断文件的MediaType
     */
    private String getMediaType(String fileName) {
        FileNameMap map = URLConnection.getFileNameMap();
        String contentTypeFor = map.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 使用post方式提交json字符串
     *
     * @param url      提交的路径
     * @param content  提交的内容
     * @param callback 请求完成回调
     */
    public void postString(String url, String content, Callback callback) {
        //构建一个RequestBody对象,,因为提交的是json字符串需要添加一个MediaType为"application/json",
        // 普通的字符串直接是null就可以了
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }
}
