package com.mlizhi.base.imageloader.core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import com.mlizhi.base.imageloader.cache.disc.DiskCache;
import com.mlizhi.base.imageloader.cache.disc.impl.UnlimitedDiskCache;
import com.mlizhi.base.imageloader.cache.disc.impl.ext.LruDiskCache;
import com.mlizhi.base.imageloader.cache.disc.naming.FileNameGenerator;
import com.mlizhi.base.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.mlizhi.base.imageloader.cache.memory.MemoryCache;
import com.mlizhi.base.imageloader.cache.memory.impl.LruMemoryCache;
import com.mlizhi.base.imageloader.core.assist.QueueProcessingType;
import com.mlizhi.base.imageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.mlizhi.base.imageloader.core.decode.BaseImageDecoder;
import com.mlizhi.base.imageloader.core.decode.ImageDecoder;
import com.mlizhi.base.imageloader.core.display.BitmapDisplayer;
import com.mlizhi.base.imageloader.core.display.SimpleBitmapDisplayer;
import com.mlizhi.base.imageloader.core.download.BaseImageDownloader;
import com.mlizhi.base.imageloader.core.download.ImageDownloader;
import com.mlizhi.base.imageloader.utils.C0126L;
import com.mlizhi.base.imageloader.utils.StorageUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultConfigurationFactory {

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber;
        private final ThreadGroup group;
        private final String namePrefix;
        private final AtomicInteger threadNumber;
        private final int threadPriority;

        static {
            poolNumber = new AtomicInteger(1);
        }

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadNumber = new AtomicInteger(1);
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = new StringBuilder(String.valueOf(threadNamePrefix)).append(poolNumber.getAndIncrement()).append("-thread-").toString();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(this.threadPriority);
            return t;
        }
    }

    public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType) {
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0, TimeUnit.MILLISECONDS, tasksProcessingType == QueueProcessingType.LIFO ? new LIFOLinkedBlockingDeque() : new LinkedBlockingQueue(), createThreadFactory(threadPriority, "uil-pool-"));
    }

    public static Executor createTaskDistributor() {
        return Executors.newCachedThreadPool(createThreadFactory(5, "uil-pool-d-"));
    }

    public static FileNameGenerator createFileNameGenerator() {
        return new HashCodeFileNameGenerator();
    }

    public static DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator, long diskCacheSize, int diskCacheFileCount) {
        File reserveCacheDir = createReserveDiskCacheDir(context);
        if (diskCacheSize > 0 || diskCacheFileCount > 0) {
            try {
                return new LruDiskCache(StorageUtils.getIndividualCacheDirectory(context), reserveCacheDir, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
            } catch (IOException e) {
                C0126L.m35e(e);
            }
        }
        return new UnlimitedDiskCache(StorageUtils.getCacheDirectory(context), reserveCacheDir, diskCacheFileNameGenerator);
    }

    private static File createReserveDiskCacheDir(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context, false);
        File individualDir = new File(cacheDir, "uil-images");
        if (individualDir.exists() || individualDir.mkdir()) {
            return individualDir;
        }
        return cacheDir;
    }

    public static MemoryCache createMemoryCache(Context context, int memoryCacheSize) {
        if (memoryCacheSize == 0) {
            ActivityManager am = (ActivityManager) context.getSystemService("activity");
            int memoryClass = am.getMemoryClass();
            if (hasHoneycomb() && isLargeHeap(context)) {
                memoryClass = getLargeMemoryClass(am);
            }
            memoryCacheSize = (AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_START * memoryClass) / 8;
        }
        return new LruMemoryCache(memoryCacheSize);
    }

    private static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    @TargetApi(11)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_START) != 0;
    }

    @TargetApi(11)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }

    public static ImageDownloader createImageDownloader(Context context) {
        return new BaseImageDownloader(context);
    }

    public static ImageDecoder createImageDecoder(boolean loggingEnabled) {
        return new BaseImageDecoder(loggingEnabled);
    }

    public static BitmapDisplayer createBitmapDisplayer() {
        return new SimpleBitmapDisplayer();
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }
}
