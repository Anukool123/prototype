package com.mlizhi.base.imageloader.cache.memory.impl;

import android.graphics.Bitmap;
import com.mlizhi.base.imageloader.cache.memory.BaseMemoryCache;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WeakMemoryCache extends BaseMemoryCache {
    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference(value);
    }
}
