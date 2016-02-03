package com.mlizhi.base.imageloader.core;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mlizhi.base.imageloader.cache.disc.DiskCache;
import com.mlizhi.base.imageloader.cache.disc.naming.FileNameGenerator;
import com.mlizhi.base.imageloader.cache.memory.MemoryCache;
import com.mlizhi.base.imageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.mlizhi.base.imageloader.core.assist.FlushedInputStream;
import com.mlizhi.base.imageloader.core.assist.ImageSize;
import com.mlizhi.base.imageloader.core.assist.QueueProcessingType;
import com.mlizhi.base.imageloader.core.decode.ImageDecoder;
import com.mlizhi.base.imageloader.core.download.ImageDownloader;
import com.mlizhi.base.imageloader.core.process.BitmapProcessor;
import com.mlizhi.base.imageloader.utils.C0126L;
import com.mlizhi.base.imageloader.utils.MemoryCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public final class ImageLoaderConfiguration {
    final boolean customExecutor;
    final boolean customExecutorForCachedImages;
    final ImageDecoder decoder;
    final DisplayImageOptions defaultDisplayImageOptions;
    final DiskCache diskCache;
    final ImageDownloader downloader;
    final int maxImageHeightForDiskCache;
    final int maxImageHeightForMemoryCache;
    final int maxImageWidthForDiskCache;
    final int maxImageWidthForMemoryCache;
    final MemoryCache memoryCache;
    final ImageDownloader networkDeniedDownloader;
    final BitmapProcessor processorForDiskCache;
    final Resources resources;
    final ImageDownloader slowNetworkDownloader;
    final Executor taskExecutor;
    final Executor taskExecutorForCachedImages;
    final QueueProcessingType tasksProcessingType;
    final int threadPoolSize;
    final int threadPriority;

    public static class Builder {
        public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE;
        public static final int DEFAULT_THREAD_POOL_SIZE = 3;
        public static final int DEFAULT_THREAD_PRIORITY = 3;
        private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
        private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
        private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.";
        private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
        private Context context;
        private boolean customExecutor;
        private boolean customExecutorForCachedImages;
        private ImageDecoder decoder;
        private DisplayImageOptions defaultDisplayImageOptions;
        private boolean denyCacheImageMultipleSizesInMemory;
        private DiskCache diskCache;
        private int diskCacheFileCount;
        private FileNameGenerator diskCacheFileNameGenerator;
        private long diskCacheSize;
        private ImageDownloader downloader;
        private int maxImageHeightForDiskCache;
        private int maxImageHeightForMemoryCache;
        private int maxImageWidthForDiskCache;
        private int maxImageWidthForMemoryCache;
        private MemoryCache memoryCache;
        private int memoryCacheSize;
        private BitmapProcessor processorForDiskCache;
        private Executor taskExecutor;
        private Executor taskExecutorForCachedImages;
        private QueueProcessingType tasksProcessingType;
        private int threadPoolSize;
        private int threadPriority;
        private boolean writeLogs;

        static {
            DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;
        }

        public Builder(Context context) {
            this.maxImageWidthForMemoryCache = 0;
            this.maxImageHeightForMemoryCache = 0;
            this.maxImageWidthForDiskCache = 0;
            this.maxImageHeightForDiskCache = 0;
            this.processorForDiskCache = null;
            this.taskExecutor = null;
            this.taskExecutorForCachedImages = null;
            this.customExecutor = false;
            this.customExecutorForCachedImages = false;
            this.threadPoolSize = DEFAULT_THREAD_PRIORITY;
            this.threadPriority = DEFAULT_THREAD_PRIORITY;
            this.denyCacheImageMultipleSizesInMemory = false;
            this.tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;
            this.memoryCacheSize = 0;
            this.diskCacheSize = 0;
            this.diskCacheFileCount = 0;
            this.memoryCache = null;
            this.diskCache = null;
            this.diskCacheFileNameGenerator = null;
            this.downloader = null;
            this.defaultDisplayImageOptions = null;
            this.writeLogs = false;
            this.context = context.getApplicationContext();
        }

        public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache) {
            this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
            this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
            return this;
        }

        @Deprecated
        public Builder discCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache, BitmapProcessor processorForDiskCache) {
            return diskCacheExtraOptions(maxImageWidthForDiskCache, maxImageHeightForDiskCache, processorForDiskCache);
        }

        public Builder diskCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache, BitmapProcessor processorForDiskCache) {
            this.maxImageWidthForDiskCache = maxImageWidthForDiskCache;
            this.maxImageHeightForDiskCache = maxImageHeightForDiskCache;
            this.processorForDiskCache = processorForDiskCache;
            return this;
        }

        public Builder taskExecutor(Executor executor) {
            if (!(this.threadPoolSize == DEFAULT_THREAD_PRIORITY && this.threadPriority == DEFAULT_THREAD_PRIORITY && this.tasksProcessingType == DEFAULT_TASK_PROCESSING_TYPE)) {
                C0126L.m38w(WARNING_OVERLAP_EXECUTOR, new Object[0]);
            }
            this.taskExecutor = executor;
            return this;
        }

        public Builder taskExecutorForCachedImages(Executor executorForCachedImages) {
            if (!(this.threadPoolSize == DEFAULT_THREAD_PRIORITY && this.threadPriority == DEFAULT_THREAD_PRIORITY && this.tasksProcessingType == DEFAULT_TASK_PROCESSING_TYPE)) {
                C0126L.m38w(WARNING_OVERLAP_EXECUTOR, new Object[0]);
            }
            this.taskExecutorForCachedImages = executorForCachedImages;
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            if (!(this.taskExecutor == null && this.taskExecutorForCachedImages == null)) {
                C0126L.m38w(WARNING_OVERLAP_EXECUTOR, new Object[0]);
            }
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder threadPriority(int threadPriority) {
            if (!(this.taskExecutor == null && this.taskExecutorForCachedImages == null)) {
                C0126L.m38w(WARNING_OVERLAP_EXECUTOR, new Object[0]);
            }
            if (threadPriority < 1) {
                this.threadPriority = 1;
            } else if (threadPriority > 10) {
                this.threadPriority = 10;
            } else {
                this.threadPriority = threadPriority;
            }
            return this;
        }

        public Builder denyCacheImageMultipleSizesInMemory() {
            this.denyCacheImageMultipleSizesInMemory = true;
            return this;
        }

        public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
            if (!(this.taskExecutor == null && this.taskExecutorForCachedImages == null)) {
                C0126L.m38w(WARNING_OVERLAP_EXECUTOR, new Object[0]);
            }
            this.tasksProcessingType = tasksProcessingType;
            return this;
        }

        public Builder memoryCacheSize(int memoryCacheSize) {
            if (memoryCacheSize <= 0) {
                throw new IllegalArgumentException("memoryCacheSize must be a positive number");
            }
            if (this.memoryCache != null) {
                C0126L.m38w(WARNING_OVERLAP_MEMORY_CACHE, new Object[0]);
            }
            this.memoryCacheSize = memoryCacheSize;
            return this;
        }

        public Builder memoryCacheSizePercentage(int availableMemoryPercent) {
            if (availableMemoryPercent <= 0 || availableMemoryPercent >= 100) {
                throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
            }
            if (this.memoryCache != null) {
                C0126L.m38w(WARNING_OVERLAP_MEMORY_CACHE, new Object[0]);
            }
            this.memoryCacheSize = (int) (((float) Runtime.getRuntime().maxMemory()) * (((float) availableMemoryPercent) / 100.0f));
            return this;
        }

        public Builder memoryCache(MemoryCache memoryCache) {
            if (this.memoryCacheSize != 0) {
                C0126L.m38w(WARNING_OVERLAP_MEMORY_CACHE, new Object[0]);
            }
            this.memoryCache = memoryCache;
            return this;
        }

        @Deprecated
        public Builder discCacheSize(int maxCacheSize) {
            return diskCacheSize(maxCacheSize);
        }

        public Builder diskCacheSize(int maxCacheSize) {
            if (maxCacheSize <= 0) {
                throw new IllegalArgumentException("maxCacheSize must be a positive number");
            }
            if (this.diskCache != null) {
                C0126L.m38w(WARNING_OVERLAP_DISK_CACHE_PARAMS, new Object[0]);
            }
            this.diskCacheSize = (long) maxCacheSize;
            return this;
        }

        @Deprecated
        public Builder discCacheFileCount(int maxFileCount) {
            return diskCacheFileCount(maxFileCount);
        }

        public Builder diskCacheFileCount(int maxFileCount) {
            if (maxFileCount <= 0) {
                throw new IllegalArgumentException("maxFileCount must be a positive number");
            }
            if (this.diskCache != null) {
                C0126L.m38w(WARNING_OVERLAP_DISK_CACHE_PARAMS, new Object[0]);
            }
            this.diskCacheFileCount = maxFileCount;
            return this;
        }

        @Deprecated
        public Builder discCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            return diskCacheFileNameGenerator(fileNameGenerator);
        }

        public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            if (this.diskCache != null) {
                C0126L.m38w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR, new Object[0]);
            }
            this.diskCacheFileNameGenerator = fileNameGenerator;
            return this;
        }

        @Deprecated
        public Builder discCache(DiskCache diskCache) {
            return diskCache(diskCache);
        }

        public Builder diskCache(DiskCache diskCache) {
            if (this.diskCacheSize > 0 || this.diskCacheFileCount > 0) {
                C0126L.m38w(WARNING_OVERLAP_DISK_CACHE_PARAMS, new Object[0]);
            }
            if (this.diskCacheFileNameGenerator != null) {
                C0126L.m38w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR, new Object[0]);
            }
            this.diskCache = diskCache;
            return this;
        }

        public Builder imageDownloader(ImageDownloader imageDownloader) {
            this.downloader = imageDownloader;
            return this;
        }

        public Builder imageDecoder(ImageDecoder imageDecoder) {
            this.decoder = imageDecoder;
            return this;
        }

        public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
            this.defaultDisplayImageOptions = defaultDisplayImageOptions;
            return this;
        }

        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        public ImageLoaderConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new ImageLoaderConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (this.taskExecutor == null) {
                this.taskExecutor = DefaultConfigurationFactory.createExecutor(this.threadPoolSize, this.threadPriority, this.tasksProcessingType);
            } else {
                this.customExecutor = true;
            }
            if (this.taskExecutorForCachedImages == null) {
                this.taskExecutorForCachedImages = DefaultConfigurationFactory.createExecutor(this.threadPoolSize, this.threadPriority, this.tasksProcessingType);
            } else {
                this.customExecutorForCachedImages = true;
            }
            if (this.diskCache == null) {
                if (this.diskCacheFileNameGenerator == null) {
                    this.diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
                }
                this.diskCache = DefaultConfigurationFactory.createDiskCache(this.context, this.diskCacheFileNameGenerator, this.diskCacheSize, this.diskCacheFileCount);
            }
            if (this.memoryCache == null) {
                this.memoryCache = DefaultConfigurationFactory.createMemoryCache(this.context, this.memoryCacheSize);
            }
            if (this.denyCacheImageMultipleSizesInMemory) {
                this.memoryCache = new FuzzyKeyMemoryCache(this.memoryCache, MemoryCacheUtils.createFuzzyKeyComparator());
            }
            if (this.downloader == null) {
                this.downloader = DefaultConfigurationFactory.createImageDownloader(this.context);
            }
            if (this.decoder == null) {
                this.decoder = DefaultConfigurationFactory.createImageDecoder(this.writeLogs);
            }
            if (this.defaultDisplayImageOptions == null) {
                this.defaultDisplayImageOptions = DisplayImageOptions.createSimple();
            }
        }
    }

    private static class NetworkDeniedImageDownloader implements ImageDownloader {
        private static /* synthetic */ int[] f1070xe23f1bc6;
        private final ImageDownloader wrappedDownloader;

        static /* synthetic */ int[] m1378xe23f1bc6() {
            int[] iArr = f1070xe23f1bc6;
            if (iArr == null) {
                iArr = new int[Scheme.values().length];
                try {
                    iArr[Scheme.ASSETS.ordinal()] = 5;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Scheme.CONTENT.ordinal()] = 4;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Scheme.DRAWABLE.ordinal()] = 6;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Scheme.FILE.ordinal()] = 3;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Scheme.HTTP.ordinal()] = 1;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Scheme.HTTPS.ordinal()] = 2;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Scheme.UNKNOWN.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                f1070xe23f1bc6 = iArr;
            }
            return iArr;
        }

        public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }

        public InputStream getStream(String imageUri, Object extra) throws IOException {
            switch (m1378xe23f1bc6()[Scheme.ofUri(imageUri).ordinal()]) {
                case 1 /*1*/:
                case 2 /*2*/:
                    throw new IllegalStateException();
                default:
                    return this.wrappedDownloader.getStream(imageUri, extra);
            }
        }
    }

    private static class SlowNetworkImageDownloader implements ImageDownloader {
        private static /* synthetic */ int[] f1071xe23f1bc6;
        private final ImageDownloader wrappedDownloader;

        static /* synthetic */ int[] m1379xe23f1bc6() {
            int[] iArr = f1071xe23f1bc6;
            if (iArr == null) {
                iArr = new int[Scheme.values().length];
                try {
                    iArr[Scheme.ASSETS.ordinal()] = 5;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Scheme.CONTENT.ordinal()] = 4;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Scheme.DRAWABLE.ordinal()] = 6;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Scheme.FILE.ordinal()] = 3;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Scheme.HTTP.ordinal()] = 1;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Scheme.HTTPS.ordinal()] = 2;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Scheme.UNKNOWN.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                f1071xe23f1bc6 = iArr;
            }
            return iArr;
        }

        public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }

        public InputStream getStream(String imageUri, Object extra) throws IOException {
            InputStream imageStream = this.wrappedDownloader.getStream(imageUri, extra);
            switch (m1379xe23f1bc6()[Scheme.ofUri(imageUri).ordinal()]) {
                case 1 /*1*/:
                case 2 /*2*/:
                    return new FlushedInputStream(imageStream);
                default:
                    return imageStream;
            }
        }
    }

    private ImageLoaderConfiguration(Builder builder) {
        this.resources = builder.context.getResources();
        this.maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
        this.maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
        this.maxImageWidthForDiskCache = builder.maxImageWidthForDiskCache;
        this.maxImageHeightForDiskCache = builder.maxImageHeightForDiskCache;
        this.processorForDiskCache = builder.processorForDiskCache;
        this.taskExecutor = builder.taskExecutor;
        this.taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
        this.threadPoolSize = builder.threadPoolSize;
        this.threadPriority = builder.threadPriority;
        this.tasksProcessingType = builder.tasksProcessingType;
        this.diskCache = builder.diskCache;
        this.memoryCache = builder.memoryCache;
        this.defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
        this.downloader = builder.downloader;
        this.decoder = builder.decoder;
        this.customExecutor = builder.customExecutor;
        this.customExecutorForCachedImages = builder.customExecutorForCachedImages;
        this.networkDeniedDownloader = new NetworkDeniedImageDownloader(this.downloader);
        this.slowNetworkDownloader = new SlowNetworkImageDownloader(this.downloader);
        C0126L.writeDebugLogs(builder.writeLogs);
    }

    public static ImageLoaderConfiguration createDefault(Context context) {
        return new Builder(context).build();
    }

    ImageSize getMaxImageSize() {
        DisplayMetrics displayMetrics = this.resources.getDisplayMetrics();
        int width = this.maxImageWidthForMemoryCache;
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        int height = this.maxImageHeightForMemoryCache;
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        return new ImageSize(width, height);
    }
}
