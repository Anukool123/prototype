package com.mlizhi.base.imageloader.core.decode;

import android.graphics.Bitmap;
import java.io.IOException;

public interface ImageDecoder {
    Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException;
}
