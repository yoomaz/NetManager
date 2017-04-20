package com.graypn.netmanager.body;

import com.graypn.netmanager.callback.DownloadCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by ZhuLei on 2017/2/6.
 * Email: zhuleineuq@gmail.com
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private DownloadCallBack mDownloadCallBack;
    private BufferedSource mBufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, DownloadCallBack downloadCallBack) {
        mResponseBody = responseBody;
        mDownloadCallBack = downloadCallBack;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            long currentReadByte = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long count = super.read(sink, byteCount);
                if (count != -1) {
                    currentReadByte += count;
                    if (mDownloadCallBack != null) {
                        mDownloadCallBack.onProgress(currentReadByte, contentLength());
                    }
                }
                return count;
            }
        };
    }

}
