package com.graypn.netmanager.callback;

import java.io.File;

/**
 * 下载回调
 * Created by zhulei on 17/2/6.
 */
public interface DownloadCallBack {

    void onFinish(File file);

    void onProgress(long currentBytes, long totalBytes);

    void onFailure(String error);
}
