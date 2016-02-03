package com.mlizhi.base.dao.async;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.mlizhi.base.dao.DaoException;
import com.mlizhi.base.dao.DaoLog;
import com.mlizhi.base.dao.async.AsyncOperation.OperationType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class AsyncOperationExecutor implements Runnable, Callback {
    private static /* synthetic */ int[] f0x8b8bd382;
    private static ExecutorService executorService;
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private volatile int maxOperationCountToMerge;
    private final BlockingQueue<AsyncOperation> queue;
    private volatile int waitForMergeMillis;

    static /* synthetic */ int[] m31x8b8bd382() {
        int[] iArr = f0x8b8bd382;
        if (iArr == null) {
            iArr = new int[OperationType.values().length];
            try {
                iArr[OperationType.Count.ordinal()] = 21;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[OperationType.Delete.ordinal()] = 10;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[OperationType.DeleteAll.ordinal()] = 14;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[OperationType.DeleteByKey.ordinal()] = 13;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[OperationType.DeleteInTxArray.ordinal()] = 12;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[OperationType.DeleteInTxIterable.ordinal()] = 11;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[OperationType.Insert.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[OperationType.InsertInTxArray.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[OperationType.InsertInTxIterable.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[OperationType.InsertOrReplace.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[OperationType.InsertOrReplaceInTxArray.ordinal()] = 6;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[OperationType.InsertOrReplaceInTxIterable.ordinal()] = 5;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[OperationType.Load.ordinal()] = 19;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[OperationType.LoadAll.ordinal()] = 20;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[OperationType.QueryList.ordinal()] = 17;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[OperationType.QueryUnique.ordinal()] = 18;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[OperationType.Refresh.ordinal()] = 22;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[OperationType.TransactionCallable.ordinal()] = 16;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[OperationType.TransactionRunnable.ordinal()] = 15;
            } catch (NoSuchFieldError e19) {
            }
            try {
                iArr[OperationType.Update.ordinal()] = 7;
            } catch (NoSuchFieldError e20) {
            }
            try {
                iArr[OperationType.UpdateInTxArray.ordinal()] = 9;
            } catch (NoSuchFieldError e21) {
            }
            try {
                iArr[OperationType.UpdateInTxIterable.ordinal()] = 8;
            } catch (NoSuchFieldError e22) {
            }
            f0x8b8bd382 = iArr;
        }
        return iArr;
    }

    static {
        executorService = Executors.newCachedThreadPool();
    }

    AsyncOperationExecutor() {
        this.queue = new LinkedBlockingQueue();
        this.maxOperationCountToMerge = 50;
        this.waitForMergeMillis = 50;
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            int i = this.lastSequenceNumber + 1;
            this.lastSequenceNumber = i;
            operation.sequenceNumber = i;
            this.queue.add(operation);
            this.countOperationsEnqueued++;
            if (!this.executorRunning) {
                this.executorRunning = true;
                executorService.execute(this);
            }
        }
    }

    public int getMaxOperationCountToMerge() {
        return this.maxOperationCountToMerge;
    }

    public void setMaxOperationCountToMerge(int maxOperationCountToMerge) {
        this.maxOperationCountToMerge = maxOperationCountToMerge;
    }

    public int getWaitForMergeMillis() {
        return this.waitForMergeMillis;
    }

    public void setWaitForMergeMillis(int waitForMergeMillis) {
        this.waitForMergeMillis = waitForMergeMillis;
    }

    public AsyncOperationListener getListener() {
        return this.listener;
    }

    public void setListener(AsyncOperationListener listener) {
        this.listener = listener;
    }

    public AsyncOperationListener getListenerMainThread() {
        return this.listenerMainThread;
    }

    public void setListenerMainThread(AsyncOperationListener listenerMainThread) {
        this.listenerMainThread = listenerMainThread;
    }

    public synchronized boolean isCompleted() {
        return this.countOperationsEnqueued == this.countOperationsCompleted;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void waitForCompletion() {
        /*
        r3 = this;
        monitor-enter(r3);
    L_0x0001:
        r1 = r3.isCompleted();	 Catch:{ all -> 0x0016 }
        if (r1 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r3);
        return;
    L_0x0009:
        r3.wait();	 Catch:{ InterruptedException -> 0x000d }
        goto L_0x0001;
    L_0x000d:
        r0 = move-exception;
        r1 = new com.mlizhi.base.dao.DaoException;	 Catch:{ all -> 0x0016 }
        r2 = "Interrupted while waiting for all operations to complete";
        r1.<init>(r2, r0);	 Catch:{ all -> 0x0016 }
        throw r1;	 Catch:{ all -> 0x0016 }
    L_0x0016:
        r1 = move-exception;
        monitor-exit(r3);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.dao.async.AsyncOperationExecutor.waitForCompletion():void");
    }

    public synchronized boolean waitForCompletion(int maxMillis) {
        if (!isCompleted()) {
            try {
                wait((long) maxMillis);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
        return isCompleted();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r9 = this;
        r8 = 0;
    L_0x0001:
        r4 = r9.queue;	 Catch:{ InterruptedException -> 0x0043 }
        r5 = 1;
        r7 = java.util.concurrent.TimeUnit.SECONDS;	 Catch:{ InterruptedException -> 0x0043 }
        r2 = r4.poll(r5, r7);	 Catch:{ InterruptedException -> 0x0043 }
        r2 = (com.mlizhi.base.dao.async.AsyncOperation) r2;	 Catch:{ InterruptedException -> 0x0043 }
        if (r2 != 0) goto L_0x0024;
    L_0x000f:
        monitor-enter(r9);	 Catch:{ InterruptedException -> 0x0043 }
        r4 = r9.queue;	 Catch:{ all -> 0x0065 }
        r4 = r4.poll();	 Catch:{ all -> 0x0065 }
        r0 = r4;
        r0 = (com.mlizhi.base.dao.async.AsyncOperation) r0;	 Catch:{ all -> 0x0065 }
        r2 = r0;
        if (r2 != 0) goto L_0x0023;
    L_0x001c:
        r4 = 0;
        r9.executorRunning = r4;	 Catch:{ all -> 0x0065 }
        monitor-exit(r9);	 Catch:{ all -> 0x0065 }
        r9.executorRunning = r8;
    L_0x0022:
        return;
    L_0x0023:
        monitor-exit(r9);	 Catch:{ all -> 0x0065 }
    L_0x0024:
        r4 = r2.isMergeTx();	 Catch:{ InterruptedException -> 0x0043 }
        if (r4 == 0) goto L_0x0073;
    L_0x002a:
        r4 = r9.queue;	 Catch:{ InterruptedException -> 0x0043 }
        r5 = r9.waitForMergeMillis;	 Catch:{ InterruptedException -> 0x0043 }
        r5 = (long) r5;	 Catch:{ InterruptedException -> 0x0043 }
        r7 = java.util.concurrent.TimeUnit.MILLISECONDS;	 Catch:{ InterruptedException -> 0x0043 }
        r3 = r4.poll(r5, r7);	 Catch:{ InterruptedException -> 0x0043 }
        r3 = (com.mlizhi.base.dao.async.AsyncOperation) r3;	 Catch:{ InterruptedException -> 0x0043 }
        if (r3 == 0) goto L_0x0073;
    L_0x0039:
        r4 = r2.isMergeableWith(r3);	 Catch:{ InterruptedException -> 0x0043 }
        if (r4 == 0) goto L_0x006c;
    L_0x003f:
        r9.mergeTxAndExecute(r2, r3);	 Catch:{ InterruptedException -> 0x0043 }
        goto L_0x0001;
    L_0x0043:
        r1 = move-exception;
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0068 }
        r5 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0068 }
        r5 = r5.getName();	 Catch:{ all -> 0x0068 }
        r5 = java.lang.String.valueOf(r5);	 Catch:{ all -> 0x0068 }
        r4.<init>(r5);	 Catch:{ all -> 0x0068 }
        r5 = " was interruppted";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0068 }
        r4 = r4.toString();	 Catch:{ all -> 0x0068 }
        com.mlizhi.base.dao.DaoLog.m29w(r4, r1);	 Catch:{ all -> 0x0068 }
        r9.executorRunning = r8;
        goto L_0x0022;
    L_0x0065:
        r4 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x0065 }
        throw r4;	 Catch:{ InterruptedException -> 0x0043 }
    L_0x0068:
        r4 = move-exception;
        r9.executorRunning = r8;
        throw r4;
    L_0x006c:
        r9.executeOperationAndPostCompleted(r2);	 Catch:{ InterruptedException -> 0x0043 }
        r9.executeOperationAndPostCompleted(r3);	 Catch:{ InterruptedException -> 0x0043 }
        goto L_0x0001;
    L_0x0073:
        r9.executeOperationAndPostCompleted(r2);	 Catch:{ InterruptedException -> 0x0043 }
        goto L_0x0001;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.dao.async.AsyncOperationExecutor.run():void");
    }

    private void mergeTxAndExecute(AsyncOperation operation1, AsyncOperation operation2) {
        ArrayList<AsyncOperation> mergedOps = new ArrayList();
        mergedOps.add(operation1);
        mergedOps.add(operation2);
        SQLiteDatabase db = operation1.getDatabase();
        db.beginTransaction();
        boolean success = false;
        int i = 0;
        while (i < mergedOps.size()) {
            try {
                AsyncOperation operation = (AsyncOperation) mergedOps.get(i);
                executeOperation(operation);
                if (!operation.isFailed()) {
                    if (i == mergedOps.size() - 1) {
                        AsyncOperation peekedOp = (AsyncOperation) this.queue.peek();
                        if (i >= this.maxOperationCountToMerge || !operation.isMergeableWith(peekedOp)) {
                            db.setTransactionSuccessful();
                            success = true;
                            break;
                        }
                        AsyncOperation removedOp = (AsyncOperation) this.queue.remove();
                        if (removedOp != peekedOp) {
                            throw new DaoException("Internal error: peeked op did not match removed op");
                        }
                        mergedOps.add(removedOp);
                    }
                    i++;
                }
            } finally {
                try {
                    db.endTransaction();
                } catch (RuntimeException e) {
                    DaoLog.m25i("Async transaction could not be ended, success so far was: " + success, e);
                    success = false;
                }
            }
        }
        break;
        Iterator it;
        if (success) {
            int mergedCount = mergedOps.size();
            it = mergedOps.iterator();
            while (it.hasNext()) {
                AsyncOperation asyncOperation = (AsyncOperation) it.next();
                asyncOperation.mergedOperationsCount = mergedCount;
                handleOperationCompleted(asyncOperation);
            }
            return;
        }
        DaoLog.m24i("Reverted merged transaction because one of the operations failed. Executing operations one by one instead...");
        it = mergedOps.iterator();
        while (it.hasNext()) {
            asyncOperation = (AsyncOperation) it.next();
            asyncOperation.reset();
            executeOperationAndPostCompleted(asyncOperation);
        }
    }

    private void handleOperationCompleted(AsyncOperation operation) {
        operation.setCompleted();
        AsyncOperationListener listenerToCall = this.listener;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted(operation);
        }
        if (this.listenerMainThread != null) {
            if (this.handlerMainThread == null) {
                this.handlerMainThread = new Handler(Looper.getMainLooper(), this);
            }
            this.handlerMainThread.sendMessage(this.handlerMainThread.obtainMessage(1, operation));
        }
        synchronized (this) {
            this.countOperationsCompleted++;
            if (this.countOperationsCompleted == this.countOperationsEnqueued) {
                notifyAll();
            }
        }
    }

    private void executeOperationAndPostCompleted(AsyncOperation operation) {
        executeOperation(operation);
        handleOperationCompleted(operation);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeOperation(AsyncOperation r5) {
        /*
        r4 = this;
        r1 = java.lang.System.currentTimeMillis();
        r5.timeStarted = r1;
        r1 = m31x8b8bd382();	 Catch:{ Throwable -> 0x002c }
        r2 = r5.type;	 Catch:{ Throwable -> 0x002c }
        r2 = r2.ordinal();	 Catch:{ Throwable -> 0x002c }
        r1 = r1[r2];	 Catch:{ Throwable -> 0x002c }
        switch(r1) {
            case 1: goto L_0x0052;
            case 2: goto L_0x005a;
            case 3: goto L_0x0064;
            case 4: goto L_0x006e;
            case 5: goto L_0x0076;
            case 6: goto L_0x0080;
            case 7: goto L_0x008a;
            case 8: goto L_0x0092;
            case 9: goto L_0x009c;
            case 10: goto L_0x0036;
            case 11: goto L_0x003e;
            case 12: goto L_0x0048;
            case 13: goto L_0x00c6;
            case 14: goto L_0x00cf;
            case 15: goto L_0x00a6;
            case 16: goto L_0x00aa;
            case 17: goto L_0x00ae;
            case 18: goto L_0x00ba;
            case 19: goto L_0x00d6;
            case 20: goto L_0x00e2;
            case 21: goto L_0x00ec;
            case 22: goto L_0x00fa;
            default: goto L_0x0015;
        };	 Catch:{ Throwable -> 0x002c }
    L_0x0015:
        r1 = new com.mlizhi.base.dao.DaoException;	 Catch:{ Throwable -> 0x002c }
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x002c }
        r3 = "Unsupported operation: ";
        r2.<init>(r3);	 Catch:{ Throwable -> 0x002c }
        r3 = r5.type;	 Catch:{ Throwable -> 0x002c }
        r2 = r2.append(r3);	 Catch:{ Throwable -> 0x002c }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x002c }
        r1.<init>(r2);	 Catch:{ Throwable -> 0x002c }
        throw r1;	 Catch:{ Throwable -> 0x002c }
    L_0x002c:
        r0 = move-exception;
        r5.throwable = r0;
    L_0x002f:
        r1 = java.lang.System.currentTimeMillis();
        r5.timeCompleted = r1;
        return;
    L_0x0036:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.delete(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x003e:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002c }
        r2.deleteInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0048:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002c }
        r2.deleteInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0052:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.insert(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x005a:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002c }
        r2.insertInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0064:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002c }
        r2.insertInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x006e:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.insertOrReplace(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0076:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002c }
        r2.insertOrReplaceInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0080:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002c }
        r2.insertOrReplaceInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x008a:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.update(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x0092:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002c }
        r2.updateInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x009c:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002c }
        r2.updateInTx(r1);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00a6:
        r4.executeTransactionRunnable(r5);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00aa:
        r4.executeTransactionCallable(r5);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00ae:
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (com.mlizhi.base.dao.query.Query) r1;	 Catch:{ Throwable -> 0x002c }
        r1 = r1.list();	 Catch:{ Throwable -> 0x002c }
        r5.result = r1;	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00ba:
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = (com.mlizhi.base.dao.query.Query) r1;	 Catch:{ Throwable -> 0x002c }
        r1 = r1.unique();	 Catch:{ Throwable -> 0x002c }
        r5.result = r1;	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00c6:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.deleteByKey(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00cf:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1.deleteAll();	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00d6:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1 = r1.load(r2);	 Catch:{ Throwable -> 0x002c }
        r5.result = r1;	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00e2:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r1.loadAll();	 Catch:{ Throwable -> 0x002c }
        r5.result = r1;	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00ec:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r1 = r1.count();	 Catch:{ Throwable -> 0x002c }
        r1 = java.lang.Long.valueOf(r1);	 Catch:{ Throwable -> 0x002c }
        r5.result = r1;	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
    L_0x00fa:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002c }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002c }
        r1.refresh(r2);	 Catch:{ Throwable -> 0x002c }
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.dao.async.AsyncOperationExecutor.executeOperation(com.mlizhi.base.dao.async.AsyncOperation):void");
    }

    private void executeTransactionRunnable(AsyncOperation operation) {
        SQLiteDatabase db = operation.getDatabase();
        db.beginTransaction();
        try {
            ((Runnable) operation.parameter).run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void executeTransactionCallable(AsyncOperation operation) throws Exception {
        SQLiteDatabase db = operation.getDatabase();
        db.beginTransaction();
        try {
            operation.result = ((Callable) operation.parameter).call();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean handleMessage(Message msg) {
        AsyncOperationListener listenerToCall = this.listenerMainThread;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted((AsyncOperation) msg.obj);
        }
        return false;
    }
}
