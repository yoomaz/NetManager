package com.graypn.netmanager;

import android.content.Context;
import android.os.Handler;

import com.graypn.netmanager.body.ProgressResponseBody;
import com.graypn.netmanager.callback.DownloadCallBack;
import com.graypn.netmanager.utils.FileUtils;
import com.graypn.netmanager.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 封装网络部分
 * <p/>
 * 基于 Okhttp3
 * Created by graypn on 16/8/4.
 */
public class NetManager {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static WeakReference<Context> mContext;

    public static void init(Context context) {
        mContext = new WeakReference<>(context);
    }

    /**
     * 同步直接获取字符串
     * <p/>
     * Get方式
     */
    public static String getData(String url) throws IOException {
        if (NetworkUtils.isConnected(mContext.get())) {
            Request request = new Request
                    .Builder()
                    .url(url)
                    .build();
            Response response = mOkHttpClient
                    .newCall(request)
                    .execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
        return null;
    }

    /**
     * 异步访问网络
     * <p/>
     * Get方式
     */
    public static void getDataAsync(String url, Callback responseCallback) {
        if (NetworkUtils.isConnected(mContext.get())) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            mOkHttpClient.newCall(request)
                    .enqueue(responseCallback);
        }
    }

    /**
     * 异步访问网络
     * <p/>
     * POST方式
     */
    public static void getDataAsyncInPost(String url, Map<String, String> params, Callback responseCallback) {
        if (NetworkUtils.isConnected(mContext.get())) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            mOkHttpClient.newCall(request)
                    .enqueue(responseCallback);
        }
    }

    public static void download(String url, String filePath, String fileName, final DownloadCallBack callBack) {
        if (NetworkUtils.isConnected(mContext.get())) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            mOkHttpClient.newBuilder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response response = chain.proceed(chain.request());
                            return response.newBuilder()
                                    .body(new ProgressResponseBody(response.body(), callBack))
                                    .build();
                        }
                    })
                    .build()
                    .newCall(request)
                    .enqueue(new OkhttpDownloadCallBack(filePath, fileName, new Handler(), callBack));
        }
    }

    private static class OkhttpDownloadCallBack implements Callback {

        private String mFilePath;
        private String mFleName;
        private Handler mHandler;
        private DownloadCallBack mDownloadCallBack;

        public OkhttpDownloadCallBack(String filePath, String fileName, Handler handler, DownloadCallBack downloadCallBack) {
            mFilePath = filePath;
            mFleName = fileName;
            mHandler = handler;
            mDownloadCallBack = downloadCallBack;
        }

        @Override
        public void onFailure(Call call, final IOException e) {
            if (mDownloadCallBack != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadCallBack.onFailure(e.toString());
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (mDownloadCallBack != null) {
                if (response.isSuccessful()) {
                    final File file = FileUtils.saveFile(mFilePath, mFleName, response.body().byteStream());
                    if (file != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDownloadCallBack.onFinish(file);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDownloadCallBack.onFailure("save file faied");

                            }
                        });
                    }

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallBack.onFailure("onResponse fail , status code is " + response.code());

                        }
                    });
                }
            }
        }
    }

}
