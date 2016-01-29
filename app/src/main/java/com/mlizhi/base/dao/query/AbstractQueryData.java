package com.mlizhi.base.dao.query;

import android.os.Process;
import android.util.SparseArray;
import com.mlizhi.base.dao.AbstractDao;
import java.lang.ref.WeakReference;

abstract class AbstractQueryData<T, Q extends AbstractQuery<T>> {
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Q>> queriesForThreads;
    final String sql;

    protected abstract Q createQuery();

    AbstractQueryData(AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        this.dao = dao;
        this.sql = sql;
        this.initialValues = initialValues;
        this.queriesForThreads = new SparseArray();
    }

    Q forCurrentThread(Q query) {
        if (Thread.currentThread() != query.ownerThread) {
            return forCurrentThread();
        }
        System.arraycopy(this.initialValues, 0, query.parameters, 0, this.initialValues.length);
        return query;
    }

    Q forCurrentThread() {
        Q query;
        int threadId = Process.myTid();
        synchronized (this.queriesForThreads) {
            WeakReference<Q> queryRef = (WeakReference) this.queriesForThreads.get(threadId);
            query = queryRef != null ? (AbstractQuery) queryRef.get() : null;
            if (query == null) {
                gc();
                query = createQuery();
                this.queriesForThreads.put(threadId, new WeakReference(query));
            } else {
                System.arraycopy(this.initialValues, 0, query.parameters, 0, this.initialValues.length);
            }
        }
        return query;
    }

    void gc() {
        synchronized (this.queriesForThreads) {
            for (int i = this.queriesForThreads.size() - 1; i >= 0; i--) {
                if (((WeakReference) this.queriesForThreads.valueAt(i)).get() == null) {
                    this.queriesForThreads.remove(this.queriesForThreads.keyAt(i));
                }
            }
        }
    }
}
