package com.mlizhi.base.upyun.block.api.http;

import android.os.Looper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mlizhi.base.upyun.block.api.listener.LoadingCompleteListener;
import com.mlizhi.base.upyun.block.api.listener.LoadingProgressListener;
import org.apache.http.Header;

public class ResponseHandler extends AsyncHttpResponseHandler {
    private LoadingCompleteListener loadingCompleteListener;
    private LoadingProgressListener loadingProgressListener;

    public ResponseHandler(LoadingCompleteListener loadingCompleteListener, LoadingProgressListener loadingProgressListener) {
        super(Looper.getMainLooper());
        this.loadingCompleteListener = loadingCompleteListener;
        this.loadingProgressListener = loadingProgressListener;
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        this.loadingCompleteListener.result(false, null, ResponseJson.errorResponseJsonFormat(statusCode, headers, responseBody));
    }

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        this.loadingCompleteListener.result(true, ResponseJson.okResposneJsonFormat(statusCode, headers, responseBody), null);
    }

    public void onProgress(int bytesWritten, int totalSize) {
        if (this.loadingProgressListener != null) {
            this.loadingProgressListener.onProgress(bytesWritten, totalSize);
        }
    }

    public void onStart() {
        super.onStart();
    }
}
