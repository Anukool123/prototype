package com.mlizhi.widgets.wave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build.VERSION;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.philips.skincare.skincareprototype.R;

import org.java_websocket.WebSocket;

@SuppressLint({"NewApi"})
public class CircularView extends View {
    protected static final int LARGE = 1;
    protected static final int LITTLE = 3;
    protected static final int MIDDLE = 2;
    public final int DEFAULT_ABOVE_WAVE_ALPHA;
    public final int DEFAULT_BLOW_WAVE_ALPHA;
    private final double PI2;
    private final int WAVE_HEIGHT_LARGE;
    private final int WAVE_HEIGHT_LITTLE;
    private final int WAVE_HEIGHT_MIDDLE;
    private final float WAVE_HZ_FAST;
    private final float WAVE_HZ_NORMAL;
    private final float WAVE_HZ_SLOW;
    private final float WAVE_LENGTH_MULTIPLE_LARGE;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE;
    private final float X_SPACE;
    private Bitmap ballBitmap;
    private int bottom;
    private Context context;
    private int left;
    private float mAboveOffset;
    private Paint mAboveWavePaint;
    private Path mAboveWavePath;
    private Paint mBackgroundColorPaint;
    private float mBlowOffset;
    private Paint mBlowWavePaint;
    private Path mBlowWavePath;
    private final RectF mCircleBounds;
    private int mCircleStrokeWidth;
    private int mGravity;
    private int mHorizontalInset;
    private boolean mIsInitializing;
    private boolean mIsThumbEnabled;
    private float mLastRadius;
    private float mMaxRight;
    private Path mPath;
    private float mProgress;
    private int mProgressBackgroundColor;
    private int mProgressColor;
    private Paint mProgressColorPaint;
    private float mRadius;
    private Paint mThumbColorPaint;
    private float mThumbPosX;
    private float mThumbPosY;
    private int mThumbRadius;
    private float mTranslationOffsetX;
    private float mTranslationOffsetY;
    private int mVerticalInset;
    private int mWaveHeight;
    private float mWaveHz;
    private float mWaveLength;
    private float mWaveMultiple;
    private double omega;
    private int right;

    public CircularView(Context context) {
        super(context);
        this.mCircleBounds = new RectF();
        this.mBackgroundColorPaint = null;
        this.mCircleStrokeWidth = 10;
        this.mGravity = 17;
        this.mHorizontalInset = 0;
        this.mVerticalInset = 0;
        this.mIsInitializing = true;
        this.mIsThumbEnabled = true;
        this.mProgress = 0.0f;
        this.mThumbColorPaint = null;
        this.mThumbRadius = 20;
        this.mPath = new Path();
        this.WAVE_HEIGHT_LARGE = 16;
        this.WAVE_HEIGHT_MIDDLE = 8;
        this.WAVE_HEIGHT_LITTLE = 5;
        this.WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
        this.WAVE_LENGTH_MULTIPLE_MIDDLE = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;
        this.WAVE_HZ_FAST = 0.13f;
        this.WAVE_HZ_NORMAL = 0.09f;
        this.WAVE_HZ_SLOW = 0.05f;
        this.DEFAULT_ABOVE_WAVE_ALPHA = StatusCode.ST_CODE_SUCCESSED;
        this.DEFAULT_BLOW_WAVE_ALPHA = 50;
        this.mAboveWavePath = new Path();
        this.mBlowWavePath = new Path();
        this.mWaveMultiple = 0.5f;
        this.mWaveHeight = 8;
        this.mWaveHz = 0.05f;
        this.X_SPACE = 10.0f;
        this.PI2 = 6.283185307179586d;
        this.mAboveOffset = 0.0f;
        this.mBlowOffset = ((float) this.mWaveHeight) * 0.4f;
    }

    public CircularView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circularStyle);
    }

    public CircularView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCircleBounds = new RectF();
        this.mBackgroundColorPaint = null;
        this.mCircleStrokeWidth = 10;
        this.mGravity = 17;
        this.mHorizontalInset = 0;
        this.mVerticalInset = 0;
        this.mIsInitializing = true;
        this.mIsThumbEnabled = true;
        this.mProgress = 0.0f;
        this.mThumbColorPaint = null;
        this.mThumbRadius = 20;
        this.mPath = new Path();
        this.WAVE_HEIGHT_LARGE = 16;
        this.WAVE_HEIGHT_MIDDLE = 8;
        this.WAVE_HEIGHT_LITTLE = 5;
        this.WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
        this.WAVE_LENGTH_MULTIPLE_MIDDLE = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;
        this.WAVE_HZ_FAST = 0.13f;
        this.WAVE_HZ_NORMAL = 0.09f;
        this.WAVE_HZ_SLOW = 0.05f;
        this.DEFAULT_ABOVE_WAVE_ALPHA = StatusCode.ST_CODE_SUCCESSED;
        this.DEFAULT_BLOW_WAVE_ALPHA = 50;
        this.mAboveWavePath = new Path();
        this.mBlowWavePath = new Path();
        this.mWaveMultiple = 0.5f;
        this.mWaveHeight = 8;
        this.mWaveHz = 0.05f;
        this.X_SPACE = 10.0f;
        this.PI2 = 6.283185307179586d;
        this.mAboveOffset = 0.0f;
        this.mBlowOffset = ((float) this.mWaveHeight) * 0.4f;
        this.context = context;
        int [] CircularView = {};
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,  CircularView, defStyle, 0);
        if (attributes != null) {
            try {
                //setProgressColor(attributes.getColor(LITTLE, getResources().getColor(R.color.color_skin_health4toastbg)));
               // setProgressBackgroundColor(attributes.getColor(4, getResources().getColor(R.color.color_skin_health4toastbg)));
              //  setProgress(attributes.getFloat(MIDDLE, 0.0f));
              //  setWheelSize((int) attributes.getDimension(LARGE, 10.0f));
              //  setThumbEnabled(attributes.getBoolean(5, true));
              //  setWaveHeight(attributes.getInt(9, MIDDLE));
              //  setWaveMultiple(attributes.getInt(8, LITTLE));
              //  setWaveHz(attributes.getInt(10, LITTLE));
                //this.mGravity = attributes.getInt(0, 17);
            } finally {
                attributes.recycle();
            }
        }
        this.mThumbRadius = this.mCircleStrokeWidth * MIDDLE;
        this.mBackgroundColorPaint = new Paint();
        this.mThumbColorPaint = new Paint();
        updateBackgroundColor();
        updateProgressColor();

        this.ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_detection_track);
        this.ballBitmap = toSmallImg(this.ballBitmap);
        initializePainters();
        this.mIsInitializing = false;
    }

    private Bitmap toSmallImg(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.2f, 1.2f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void initializePainters() {
        this.mAboveWavePaint = new Paint();
        this.mAboveWavePaint.setAlpha(StatusCode.ST_CODE_SUCCESSED);
        this.mAboveWavePaint.setColor(this.context.getResources().getColor(R.color.theme_second_blue));
        this.mAboveWavePaint.setStyle(Style.FILL);
        this.mAboveWavePaint.setAntiAlias(true);
        this.mBlowWavePaint = new Paint();
        this.mBlowWavePaint.setColor(this.context.getResources().getColor(R.color.theme_second_blue));
        this.mBlowWavePaint.setAlpha(50);
        this.mBlowWavePaint.setStyle(Style.FILL);
        this.mBlowWavePaint.setAntiAlias(true);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        canvas.translate(this.mTranslationOffsetX, this.mTranslationOffsetY);
        canvas.drawArc(this.mCircleBounds, 270.0f, 360.0f, false, this.mBackgroundColorPaint);
        canvas.save();
        this.mPath.reset();
        canvas.clipPath(this.mPath);
        this.mPath.addCircle(0.0f, 0.0f, this.mRadius, Direction.CW);
        canvas.clipPath(this.mPath, Op.REPLACE);
        canvas.drawPath(this.mBlowWavePath, this.mBlowWavePaint);
        canvas.drawPath(this.mAboveWavePath, this.mAboveWavePaint);
        canvas.drawArc(this.mCircleBounds, 270.0f, 360.0f, false, this.mBackgroundColorPaint);
        canvas.restore();
        if (isThumbEnabled()) {
            float progressRotation = getCurrentRotation();
            canvas.drawArc(this.mCircleBounds, 270.0f, progressRotation, false, this.mProgressColorPaint);
            canvas.save();
            canvas.rotate(progressRotation - 90.0f);
            canvas.drawBitmap(this.ballBitmap, this.mThumbPosX - (((float) this.ballBitmap.getWidth()) / 2.0f), this.mThumbPosY - (((float) this.ballBitmap.getHeight()) / 2.0f), this.mThumbColorPaint);
            canvas.restore();
        }
        if (this.mLastRadius != this.mRadius) {
            this.mLastRadius = this.mRadius;
            startWave();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int diameter;
        float drawedWith;
        int height = getDefaultSize((getSuggestedMinimumHeight() + getPaddingTop()) + getPaddingBottom(), heightMeasureSpec);
        int width = getDefaultSize((getSuggestedMinimumWidth() + getPaddingLeft()) + getPaddingRight(), widthMeasureSpec);
        if (heightMeasureSpec == 0) {
            diameter = width;
            computeInsets(0, 0);
        } else if (widthMeasureSpec == 0) {
            diameter = height;
            computeInsets(0, 0);
        } else {
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }
        setMeasuredDimension(diameter, diameter);
        float halfDiameter = ((float) diameter) * 0.5f;
        if (isThumbEnabled()) {
            drawedWith = ((float) this.mThumbRadius) * 0.8333333f;
        } else {
            drawedWith = (float) this.mCircleStrokeWidth;
        }
        this.mRadius = (halfDiameter - drawedWith) - 10.5f;
        this.mCircleBounds.set(-this.mRadius, -this.mRadius, this.mRadius, this.mRadius);
        this.mThumbPosX = (float) (((double) this.mRadius) * Math.cos(0.0d));
        this.mThumbPosY = (float) (((double) this.mRadius) * Math.sin(0.0d));
        this.mTranslationOffsetX = ((float) this.mHorizontalInset) + halfDiameter;
        this.mTranslationOffsetY = ((float) this.mVerticalInset) + halfDiameter;
    }

    public int getCircleStrokeWidth() {
        return this.mCircleStrokeWidth;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public int getProgressColor() {
        return this.mProgressColor;
    }

    public boolean isThumbEnabled() {
        return this.mIsThumbEnabled;
    }

    public void setProgress(float progress) {
        if (progress >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.mProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else {
            this.mProgress = progress % DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        calculatePath();
        invalidate();
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && this.mWaveLength == 0.0f) {
            startWave();
        }
    }

    public void setProgressBackgroundColor(int color) {
        this.mProgressBackgroundColor = color;
        updateBackgroundColor();
    }

    public void setProgressColor(int color) {
        this.mProgressColor = color;
        updateProgressColor();
    }

    public void setThumbEnabled(boolean enabled) {
        this.mIsThumbEnabled = enabled;
    }

    public void setWheelSize(int dimension) {
        this.mCircleStrokeWidth = dimension;
        updateBackgroundColor();
        updateProgressColor();
    }

    private void computeInsets(int dx, int dy) {
        int absoluteGravity = this.mGravity;
        if (VERSION.SDK_INT >= 16) {
            absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection());
        }
        switch (absoluteGravity & 7) {
            case LITTLE /*3*/:
                this.mHorizontalInset = 0;
                break;
            case 5 /*5*/:
                this.mHorizontalInset = dx;
                break;
            default:
                this.mHorizontalInset = dx / MIDDLE;
                break;
        }
        switch (absoluteGravity & 112) {
            case 48 /*48*/:
                this.mVerticalInset = 0;
            case WebSocket.DEFAULT_PORT /*80*/:
                this.mVerticalInset = dy;
            default:
                this.mVerticalInset = dy / MIDDLE;
        }
    }

    private float getCurrentRotation() {
        return 360.0f * this.mProgress;
    }

    private void updateBackgroundColor() {
        this.mBackgroundColorPaint = new Paint(LARGE);
        this.mBackgroundColorPaint.setColor(this.mProgressBackgroundColor);
        this.mBackgroundColorPaint.setStyle(Style.STROKE);
        this.mBackgroundColorPaint.setStrokeWidth((float) this.mCircleStrokeWidth);
        invalidate();
    }

    private void updateProgressColor() {
        this.mProgressColorPaint = new Paint(LARGE);
        this.mProgressColorPaint.setColor(this.mProgressColor);
        this.mProgressColorPaint.setStyle(Style.STROKE);
        this.mProgressColorPaint.setStrokeWidth((float) this.mCircleStrokeWidth);
        this.mThumbColorPaint = new Paint(LARGE);
        this.mThumbColorPaint.setColor(this.mProgressColor);
        this.mThumbColorPaint.setStyle(Style.FILL_AND_STROKE);
        this.mThumbColorPaint.setStrokeWidth((float) this.mCircleStrokeWidth);
        invalidate();
    }

    private void setWaveMultiple(int size) {
        switch (size) {
            case LARGE /*1*/:
                this.mWaveMultiple = 1.5f;
            case MIDDLE /*2*/:
                this.mWaveMultiple = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            case LITTLE /*3*/:
                this.mWaveMultiple = 0.5f;
            default:
                this.mWaveMultiple = 0.5f;
        }
    }

    private void setWaveHeight(int size) {
        switch (size) {
            case LARGE /*1*/:
                this.mWaveHeight = 16;
            case MIDDLE /*2*/:
                this.mWaveHeight = 8;
            case LITTLE /*3*/:
                this.mWaveHeight = 5;
            default:
                this.mWaveHeight = 5;
        }
    }

    private void setWaveHz(int size) {
        switch (size) {
            case LARGE /*1*/:
                this.mWaveHz = 0.13f;
            case MIDDLE /*2*/:
                this.mWaveHz = 0.09f;
            case LITTLE /*3*/:
                this.mWaveHz = 0.05f;
            default:
                this.mWaveHz = 0.05f;
        }
    }

    private void calculatePath() {
        float x;
        this.mAboveWavePath.reset();
        this.mBlowWavePath.reset();
        getWaveOffset();
        this.mAboveWavePath.moveTo((float) this.left, (float) this.bottom);
        for (x = -this.mMaxRight; x <= this.mMaxRight; x += 10.0f) {
            this.mAboveWavePath.lineTo(x, ((float) ((((double) this.mWaveHeight) * Math.sin((this.omega * ((double) x)) + ((double) this.mAboveOffset))) + ((double) this.mWaveHeight))) + (this.mRadius / 5.0f));
        }
        this.mAboveWavePath.lineTo((float) this.right, (float) this.bottom);
        this.mBlowWavePath.moveTo((float) this.left, (float) this.bottom);
        for (x = -this.mMaxRight; x <= this.mMaxRight; x += 10.0f) {
            this.mBlowWavePath.lineTo(x, ((float) ((((double) this.mWaveHeight) * Math.sin((this.omega * ((double) x)) + ((double) this.mBlowOffset))) + ((double) this.mWaveHeight))) + (this.mRadius / 5.0f));
        }
        this.mBlowWavePath.lineTo((float) this.right, (float) this.bottom);
    }

    public void startWave() {
        if (getWidth() != 0) {
            this.mWaveLength = ((float) getWidth()) * this.mWaveMultiple;
            this.bottom = getBottom();
            this.left = (int) (-this.mRadius);
            this.right = (int) this.mRadius;
            this.mMaxRight = (float) ((int) this.mRadius);
            this.omega = 6.283185307179586d / ((double) this.mWaveLength);
            calculatePath();
            invalidate();
        }
    }

    private void getWaveOffset() {
        if (this.mBlowOffset > AutoScrollHelper.NO_MAX) {
            this.mBlowOffset = 0.0f;
        } else {
            this.mBlowOffset += this.mWaveHz;
        }
        if (this.mAboveOffset > AutoScrollHelper.NO_MAX) {
            this.mAboveOffset = 0.0f;
        } else {
            this.mAboveOffset += this.mWaveHz;
        }
    }

    class StatusCode {
        public static final int ST_CODE_ACCESS_EXPIRED = 5027;
        public static final int ST_CODE_ACCESS_EXPIRED2 = 5028;
        public static final int ST_CODE_CONTENT_REPEAT = 5016;
        public static final int ST_CODE_ERROR = 40002;
        public static final int ST_CODE_ERROR_CANCEL = 40000;
        public static final int ST_CODE_ERROR_INVALID_DATA = 40001;
        public static final int ST_CODE_ERROR_WEIXIN = 5029;
        public static final int ST_CODE_NO_AUTH = 5014;
        public static final int ST_CODE_NO_SMS = 10086;
        public static final int ST_CODE_RESERVE_CODE = 5037;
        public static final int ST_CODE_SDK_INITQUEUE_FAILED = -104;
        public static final int ST_CODE_SDK_NORESPONSE = -103;
        public static final int ST_CODE_SDK_NO_OAUTH = -101;
        public static final int ST_CODE_SDK_SHARE_PARAMS_ERROR = -105;
        public static final int ST_CODE_SDK_UNKNOW = -102;
        public static final int ST_CODE_SUCCESSED = 200;
        public static final int ST_CODE_USER_BANNED = 505;
    }
}
