package com.mlizhi.base.imageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import com.android.volley.DefaultRetryPolicy;
import com.mlizhi.base.imageloader.core.assist.LoadedFrom;
import com.mlizhi.base.imageloader.core.display.RoundedBitmapDisplayer.RoundedDrawable;
import com.mlizhi.base.imageloader.core.imageaware.ImageAware;
import com.mlizhi.base.imageloader.core.imageaware.ImageViewAware;

public class RoundedVignetteBitmapDisplayer extends RoundedBitmapDisplayer {

    protected static class RoundedVignetteDrawable extends RoundedDrawable {
        RoundedVignetteDrawable(Bitmap bitmap, int cornerRadius, int margin) {
            super(bitmap, cornerRadius, margin);
        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            float centerX = this.mRect.centerX();
            float centerY = (this.mRect.centerY() * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / 0.7f;
            float centerX2 = this.mRect.centerX() * 1.3f;
            int[] iArr = new int[3];
            iArr[2] = 2130706432;
            RadialGradient vignette = new RadialGradient(centerX, centerY, centerX2, iArr, new float[]{0.0f, 0.7f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}, TileMode.CLAMP);
            Matrix oval = new Matrix();
            oval.setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.7f);
            vignette.setLocalMatrix(oval);
            this.paint.setShader(new ComposeShader(this.bitmapShader, vignette, Mode.SRC_OVER));
        }
    }

    public RoundedVignetteBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
        super(cornerRadiusPixels, marginPixels);
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (imageAware instanceof ImageViewAware) {
            imageAware.setImageDrawable(new RoundedVignetteDrawable(bitmap, this.cornerRadius, this.margin));
            return;
        }
        throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
    }
}
