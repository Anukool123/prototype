package com.mlizhi.base.dao.async;

public interface AsyncOperationListener {
    void onAsyncOperationCompleted(AsyncOperation asyncOperation);
}
