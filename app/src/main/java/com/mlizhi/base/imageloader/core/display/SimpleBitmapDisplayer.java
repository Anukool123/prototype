package com.mlizhi.base.imageloader.core.display;

import android.graphics.Bitmap;
import com.mlizhi.base.imageloader.core.assist.LoadedFrom;
import com.mlizhi.base.imageloader.core.imageaware.ImageAware;

public final class SimpleBitmapDisplayer implements BitmapDisplayer {
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }
}
