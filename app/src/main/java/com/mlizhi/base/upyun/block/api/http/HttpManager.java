package com.mlizhi.base.upyun.block.api.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.mlizhi.base.upyun.block.api.common.Params;
import com.mlizhi.base.upyun.block.api.listener.LoadingCompleteListener;
import com.mlizhi.base.upyun.block.api.listener.LoadingProgressListener;
import java.io.ByteArrayInputStream;
import org.java_websocket.framing.CloseFrame;

public class HttpManager {
    private static AsyncHttpClient client;

    static {
        client = new AsyncHttpClient();
    }

    public HttpManager() {
        client.setConnectTimeout(60000);
        client.setResponseTimeout(60000);
        client.setEnableRedirects(true);
        client.getHttpClient().getParams().setParameter("http.protocol.max-redirects", Integer.valueOf(3));
    }

    public void setConnectTimeout(int connectTimeout) {
        client.setConnectTimeout(connectTimeout * CloseFrame.NORMAL);
    }

    public void setResponseTimeout(int responseTimeout) {
        client.setResponseTimeout(responseTimeout * CloseFrame.NORMAL);
    }

    public void doPost(String URL, RequestParams requestParams, LoadingProgressListener loadingProgressListener, LoadingCompleteListener loadingCompletionListener) {
        client.post(URL, requestParams, new ResponseHandler(loadingCompletionListener, loadingProgressListener));
    }

    public void doMutipartPost(String url, PostData postData, LoadingProgressListener loadingProgressListener, LoadingCompleteListener loadingCompletionListener) {
        RequestParams requestParams = new RequestParams(postData.params);
        requestParams.put(Params.BLOCK_DATA, new ByteArrayInputStream(postData.data), postData.fileName);
        client.post(url, requestParams, new ResponseHandler(loadingCompletionListener, loadingProgressListener));
    }
}
