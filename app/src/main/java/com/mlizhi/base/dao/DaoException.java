package com.mlizhi.base.dao;

import android.database.SQLException;

public class DaoException extends SQLException {
    private static final long serialVersionUID = -5877937327907457779L;

    public DaoException(String error) {
        super(error);
    }

    public DaoException(String error, Throwable cause) {
        super(error);
        safeInitCause(cause);
    }

    public DaoException(Throwable th) {
        safeInitCause(th);
    }

    protected void safeInitCause(Throwable cause) {
        try {
            initCause(cause);
        } catch (Throwable e) {
            DaoLog.m23e("Could not set initial cause", e);
            DaoLog.m23e("Initial cause is:", cause);
        }
    }
}
