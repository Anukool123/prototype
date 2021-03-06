package com.mlizhi.base.imageloader.core.download;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore.Video.Thumbnails;
import android.webkit.MimeTypeMap;
import com.mlizhi.base.imageloader.core.assist.ContentLengthInputStream;
import com.mlizhi.base.imageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaseImageDownloader implements ImageDownloader {
    private static /* synthetic */ int[] f1072xe23f1bc6 = null;
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    protected static final int BUFFER_SIZE = 32768;
    protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5000;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20000;
    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";
    protected static final int MAX_REDIRECT_COUNT = 5;
    protected final int connectTimeout;
    protected final Context context;
    protected final int readTimeout;

    static /* synthetic */ int[] m1380xe23f1bc6() {
        int[] iArr = f1072xe23f1bc6;
        if (iArr == null) {
            iArr = new int[Scheme.values().length];
            try {
                iArr[Scheme.ASSETS.ordinal()] = MAX_REDIRECT_COUNT;
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
            f1072xe23f1bc6 = iArr;
        }
        return iArr;
    }

    public BaseImageDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (m1380xe23f1bc6()[Scheme.ofUri(imageUri).ordinal()]) {
            case 1 /*1*/:
            case 2 /*2*/:
                return getStreamFromNetwork(imageUri, extra);
            case 3 /*3*/:
                return getStreamFromFile(imageUri, extra);
            case 4 /*4*/:
                return getStreamFromContent(imageUri, extra);
            case MAX_REDIRECT_COUNT /*5*/:
                return getStreamFromAssets(imageUri, extra);
            case 6 /*6*/:
                return getStreamFromDrawable(imageUri, extra);
            default:
                return getStreamFromOtherSource(imageUri, extra);
        }
    }

    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = createConnection(imageUri, extra);
        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"), extra);
            redirectCount++;
        }
        try {
            InputStream imageStream = conn.getInputStream();
            if (shouldBeProcessed(conn)) {
                return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
            }
            IoUtils.closeSilently(imageStream);
            throw new IOException("Image request failed with response code " + conn.getResponseCode());
        } catch (IOException e) {
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
    }

    protected boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() == 200;
    }

    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(Uri.encode(url, ALLOWED_URI_CHARS)).openConnection();
        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);
        return conn;
    }

    protected InputStream getStreamFromFile(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        if (isVideoFileUri(imageUri)) {
            return getVideoThumbnailStream(filePath);
        }
        return new ContentLengthInputStream(new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE), (int) new File(filePath).length());
    }

    @TargetApi(8)
    private InputStream getVideoThumbnailStream(String filePath) {
        if (VERSION.SDK_INT >= 8) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 2);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        }
        return null;
    }

    protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
        ContentResolver res = this.context.getContentResolver();
        Uri uri = Uri.parse(imageUri);
        if (isVideoContentUri(uri)) {
            Bitmap bitmap = Thumbnails.getThumbnail(res, Long.valueOf(uri.getLastPathSegment()).longValue(), 1, null);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        } else if (imageUri.startsWith(CONTENT_CONTACTS_URI_PREFIX)) {
            return Contacts.openContactPhotoInputStream(res, uri);
        }
        return res.openInputStream(uri);
    }

    protected InputStream getStreamFromAssets(String imageUri, Object extra) throws IOException {
        return this.context.getAssets().open(Scheme.ASSETS.crop(imageUri));
    }

    protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
        return this.context.getResources().openRawResource(Integer.parseInt(Scheme.DRAWABLE.crop(imageUri)));
    }

    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, new Object[]{imageUri}));
    }

    private boolean isVideoContentUri(Uri uri) {
        String mimeType = this.context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    private boolean isVideoFileUri(String uri) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri));
        return mimeType != null && mimeType.startsWith("video/");
    }
}
