package com.mlizhi.base.imageloader.cache.memory;

import android.graphics.Bitmap;
import com.mlizhi.base.imageloader.utils.C0126L;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LimitedMemoryCache extends BaseMemoryCache {
    private static final int MAX_NORMAL_CACHE_SIZE = 16777216;
    private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
    private final AtomicInteger cacheSize;
    private final List<Bitmap> hardCache;
    private final int sizeLimit;

    protected abstract int getSize(Bitmap bitmap);

    protected abstract Bitmap removeNext();

    public LimitedMemoryCache(int sizeLimit) {
        this.hardCache = Collections.synchronizedList(new LinkedList());
        this.sizeLimit = sizeLimit;
        this.cacheSize = new AtomicInteger();
        if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
            C0126L.m38w("You set too large memory cache size (more than %1$d Mb)", Integer.valueOf(MAX_NORMAL_CACHE_SIZE_IN_MB));
        }
    }

    public boolean put(String key, Bitmap value) {
        boolean putSuccessfully = false;
        int valueSize = getSize(value);
        int sizeLimit = getSizeLimit();
        int curCacheSize = this.cacheSize.get();
        if (valueSize < sizeLimit) {
            while (curCacheSize + valueSize > sizeLimit) {
                Bitmap removedValue = removeNext();
                if (this.hardCache.remove(removedValue)) {
                    curCacheSize = this.cacheSize.addAndGet(-getSize(removedValue));
                }
            }
            this.hardCache.add(value);
            this.cacheSize.addAndGet(valueSize);
            putSuccessfully = true;
        }
        super.put(key, value);
        return putSuccessfully;
    }

    public Bitmap remove(String key) {
        Bitmap value = super.get(key);
        if (value != null && this.hardCache.remove(value)) {
            this.cacheSize.addAndGet(-getSize(value));
        }
        return super.remove(key);
    }

    public void clear() {
        this.hardCache.clear();
        this.cacheSize.set(0);
        super.clear();
    }

    protected int getSizeLimit() {
        return this.sizeLimit;
    }
}
