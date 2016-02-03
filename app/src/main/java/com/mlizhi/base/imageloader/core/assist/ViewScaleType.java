package com.mlizhi.base.imageloader.core.assist;

import android.widget.ImageView;

public enum ViewScaleType {
    FIT_INSIDE,
    CROP;

    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (imageView.getScaleType().ordinal()) {
            case /*dv.f2157d */3:
            case /*dv.f2158e*/ 4:
            case /*dh.f2146f*/ 5:
            case /*cg.f2089g*/ 6:
            case /*cg.f2090h*/ 7:
                return FIT_INSIDE;
            default:
                return CROP;
        }
    }
}
