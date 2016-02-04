package com.mlizhi.widgets.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.mlizhi.base.Utils;
import com.mlizhi.utils.Constants;
import com.philips.skincare.skincareprototype.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import p016u.aly.bq;

public class ChartView4Line extends View {
    private TypedArray array4axis;
    private TypedArray array4line;
    private TypedArray array4point;
    private TypedArray array4scale;
    private TypedArray array4text;
    private Canvas canvas;
    private Context context;
    private float[] points4post;
    private float[] points4pre;
    private boolean showFlag;
    private float tipX;
    private float tipY;
    private String[] xLabel4post;
    private String[] xLabel4pre;
    private String[] yLabel;

    public ChartView4Line(Context context) {
        super(context);
        this.array4line = null;
        this.array4text = null;
        this.array4point = null;
        this.array4axis = null;
        this.array4scale = null;
        this.yLabel = new String[]{"20%", "30%", "40%", "50%", "60%"};
        this.xLabel4pre = new String[0];
        this.xLabel4post = new String[0];
        this.points4pre = new float[0];
        this.points4post = new float[0];
        this.showFlag = false;
    }

    public ChartView4Line(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.array4line = null;
        this.array4text = null;
        this.array4point = null;
        this.array4axis = null;
        this.array4scale = null;
        this.yLabel = new String[]{"20%", "30%", "40%", "50%", "60%"};
        this.xLabel4pre = new String[0];
        this.xLabel4post = new String[0];
        this.points4pre = new float[0];
        this.points4post = new float[0];
        this.showFlag = false;
        this.context = context;
        this.array4line = context.obtainStyledAttributes(attrs, R.styleable.ChartView4LineSeries);
        this.array4text = context.obtainStyledAttributes(attrs, R.styleable.ChartView4LineText);
        this.array4point = context.obtainStyledAttributes(attrs, R.styleable.ChartView4LinePoint);
        this.array4axis = context.obtainStyledAttributes(attrs, R.styleable.ChartView4LineAxis);
        this.array4scale = context.obtainStyledAttributes(attrs, R.styleable.ChartView4LineScale);
    }

    private void draw4axis(Canvas canvas) {
        if (this.array4axis != null) {
            boolean axis4antiAlias = this.array4axis.getBoolean(1, false);
            int axis4x = getMeasuredWidth();
            int axis4y = getMeasuredHeight();
            int axis4color = this.array4axis.getColor(2, Color.argb(MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK));
            float axis4strokeWidth = this.array4axis.getDimension(3, 3.0f);
            boolean arrow4x = this.array4axis.getBoolean(5, false);
            boolean arrow4y = this.array4axis.getBoolean(6, false);
            Paint paint4axis = new Paint();
            paint4axis.setStyle(Style.FILL);
            paint4axis.setAntiAlias(axis4antiAlias);
            paint4axis.setColor(axis4color);
            paint4axis.setStrokeWidth(axis4strokeWidth);
            canvas.drawLine(0.1f * ((float) axis4x), 0.9f * ((float) axis4y), 0.92f * ((float) axis4x), 0.9f * ((float) axis4y), paint4axis);
            canvas.drawLine(0.1f * ((float) axis4x), 0.05f * ((float) axis4y), 0.1f * ((float) axis4x), 10.0f + (((float) axis4y) * 0.9f), paint4axis);
            canvas.drawLine(0.92f * ((float) axis4x), 0.05f * ((float) axis4y), 0.92f * ((float) axis4x), 10.0f + (((float) axis4y) * 0.9f), paint4axis);
            if (arrow4x) {
                canvas.drawLine((((float) axis4x) * 0.1f) - 10.0f, 10.0f + (((float) axis4y) * 0.1f), 0.1f * ((float) axis4x), 0.1f * ((float) axis4y), paint4axis);
                canvas.drawLine(10.0f + (((float) axis4x) * 0.1f), 10.0f + (((float) axis4y) * 0.1f), 0.1f * ((float) axis4x), 0.1f * ((float) axis4y), paint4axis);
            }
            if (arrow4y) {
                canvas.drawLine((((float) axis4x) * 0.92f) - 10.0f, (((float) axis4y) * 0.9f) - 10.0f, 0.92f * ((float) axis4x), 0.9f * ((float) axis4y), paint4axis);
                canvas.drawLine((((float) axis4x) * 0.92f) - 10.0f, 10.0f + (((float) axis4y) * 0.9f), 0.92f * ((float) axis4x), 0.9f * ((float) axis4y), paint4axis);
                return;
            }
            return;
        }
        this.array4axis.recycle();
    }

    private void draw4scale(Canvas canvas) {
        int axis4x = getMeasuredWidth();
        int axis4y = getMeasuredHeight();
        if (this.array4scale != null) {
            int i;
            boolean scale4gridx = this.array4scale.getBoolean(1, false);
            boolean scale4gridy = this.array4scale.getBoolean(2, false);
            boolean scale4antiAlias = this.array4scale.getBoolean(3, true);
            int scale4color = this.array4scale.getColor(4, Color.argb(MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK));
            float scale4strokeWidth = this.array4scale.getDimension(5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            boolean scale4showXLabel = this.array4scale.getBoolean(6, false);
            float text4size = this.array4text.getDimension(6, Constants.BODY_PART_HAND_MORMAL_MIN);
            Paint paint4scale = new Paint();
            paint4scale.setStyle(Style.FILL);
            paint4scale.setAntiAlias(scale4antiAlias);
            paint4scale.setColor(scale4color);
            paint4scale.setStrokeWidth((float) Utils.dip2px(this.context, scale4strokeWidth));
            paint4scale.setTextSize((float) Utils.dip2px(this.context, 12.0f));
            for (i = 0; i < this.yLabel.length; i++) {
                canvas.drawCircle(((float) axis4x) * 0.1f, (((float) axis4y) * 0.9f) - ((float) ((i * axis4y) / (this.yLabel.length + 1))), 5.0f, paint4scale);
                canvas.drawText(this.yLabel[i], ((((float) axis4x) * 0.1f) - paint4scale.measureText(this.yLabel[i])) - 8.0f, (((float) axis4y) * 0.9f) - ((float) ((i * axis4y) / (this.yLabel.length + 1))), paint4scale);
            }
            if (scale4showXLabel) {
                Canvas canvas2;
                if (this.xLabel4pre.length > 1) {
                    i = 0;
                    while (i < this.xLabel4pre.length) {
                        if (!(this.xLabel4pre[i] == null || bq.f888b.equals(this.xLabel4pre[i]))) {
                            canvas.drawCircle((((float) axis4x) * 0.1f) + ((((float) (i * axis4x)) * 0.82f) / ((float) (this.xLabel4pre.length - 1))), ((float) axis4y) * 0.9f, 5.0f, paint4scale);
                        }
                        canvas2 = canvas;
                        canvas2.drawText(this.xLabel4pre[i], ((((float) axis4x) * 0.1f) + ((((float) (i * axis4x)) * 0.82f) / ((float) (this.xLabel4pre.length - 1)))) - (paint4scale.measureText(this.xLabel4pre[i]) * 0.5f), (((float) axis4y) * 0.9f) + (paint4scale.getFontMetrics().bottom - paint4scale.getFontMetrics().top), paint4scale);
                        i++;
                    }
                }
                if (this.xLabel4post.length > 1) {
                    i = 0;
                    while (i < this.xLabel4post.length) {
                        if (!(this.xLabel4post[i] == null || bq.f888b.equals(this.xLabel4post[i]))) {
                            canvas.drawCircle((((float) axis4x) * 0.1f) + ((((float) (i * axis4x)) * 0.82f) / ((float) (this.xLabel4post.length - 1))), ((float) axis4y) * 0.9f, 5.0f, paint4scale);
                        }
                        canvas2 = canvas;
                        canvas2.drawText(this.xLabel4post[i], ((((float) axis4x) * 0.1f) + ((((float) (i * axis4x)) * 0.82f) / ((float) (this.xLabel4post.length - 1)))) - (paint4scale.measureText(this.xLabel4post[i]) * 0.5f), (((float) axis4y) * 0.9f) + (paint4scale.getFontMetrics().bottom - paint4scale.getFontMetrics().top), paint4scale);
                        i++;
                    }
                }
            }
            paint4scale.setColor(0);
            paint4scale.setStyle(Style.STROKE);
            Path path = new Path();
            if (scale4gridx) {
                paint4scale.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f, 5.0f, 5.0f}, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if (this.xLabel4pre.length > 1) {
                    i = 1065353216;
                    while (i < ((float) this.xLabel4pre.length)) {
                        i += 1065353216;
                        canvas.drawLine((((((float) axis4x) * i) * 0.82f) / ((float) (this.xLabel4pre.length - 1))) + (((float) axis4x) * 0.1f), 0.1f * ((float) axis4y), (((((float) axis4x) * i) * 0.82f) / ((float) (this.xLabel4pre.length - 1))) + (((float) axis4x) * 0.1f), 0.9f * ((float) axis4y), paint4scale);
                    }
                }
                if (this.xLabel4post.length > 1) {
                    i = 1065353216;
                    while (i < ((float) this.xLabel4post.length)) {
                        i += 1065353216;
                        canvas.drawLine((((((float) axis4x) * i) * 0.82f) / ((float) (this.xLabel4post.length - 1))) + (((float) axis4x) * 0.1f), 0.1f * ((float) axis4y), (((((float) axis4x) * i) * 0.82f) / ((float) (this.xLabel4post.length - 1))) + (((float) axis4x) * 0.1f), 0.9f * ((float) axis4y), paint4scale);
                    }
                }
            }
            if (scale4gridy) {
                paint4scale.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f, 5.0f, 5.0f}, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                for (i = 1; i < this.yLabel.length; i++) {
                    if (i % 2 == 1) {
                        path.moveTo(((float) axis4x) * 0.1f, (((float) axis4y) * 0.9f) - ((float) ((i * axis4y) / (this.yLabel.length + 1))));
                        path.lineTo(((float) axis4x) * 0.92f, (((float) axis4y) * 0.9f) - ((float) ((i * axis4y) / (this.yLabel.length + 1))));
                        canvas.drawPath(path, paint4scale);
                    }
                }
                return;
            }
            return;
        }
        this.array4scale.recycle();
    }

    private void draw4line(Canvas canvas) {
        int axis4x = getMeasuredWidth();
        int axis4y = getMeasuredHeight();
        if (this.array4line != null) {
            int i;
            float pointX;
            float pointY;
            boolean line4antiAlias = this.array4line.getBoolean(1, true);
            int line4preColor = this.array4line.getColor(2, Color.argb(250, 97, 173, 244));
            int line4postColor = this.array4line.getColor(3, Color.argb(250, 123, 207, 35));
            float line4strokeWidth = this.array4line.getDimension(4, 1.5f);
            String line4type = this.array4line.getString(0);
            Paint paint4line = new Paint();
            paint4line.setStyle(Style.STROKE);
            paint4line.setAntiAlias(line4antiAlias);
            paint4line.setColor(line4preColor);
            paint4line.setStrokeWidth((float) Utils.dip2px(this.context, line4strokeWidth));
            paint4line.setTextSize((float) Utils.dip2px(this.context, 12.0f));
            Path path = new Path();
            List<float[]> validPointList4pre = new ArrayList();
            if (this.points4pre.length > 1) {
                i = 0;
                while (i < this.points4pre.length) {
                    if (i == 0 || i == this.points4pre.length - 1) {
                        if (this.points4pre[i] < 0.2f) {
                            this.points4pre[i] = 0.2f;
                        }
                        pointX = getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) (this.points4pre.length - 1)));
                        pointY = getYCoordinate(this.points4pre[i], axis4y);
                        validPointList4pre.add(new float[]{pointX, pointY});
                    } else if (this.points4pre[i] >= 0.2f && this.points4pre[i] <= 0.6f) {
                        pointX = getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) (this.points4pre.length - 1)));
                        pointY = getYCoordinate(this.points4pre[i], axis4y);
                        validPointList4pre.add(new float[]{pointX, pointY});
                    }
                    i++;
                }
            }
            for (i = 0; i < validPointList4pre.size() - 1; i++) {
                canvas.drawLine(((float[]) validPointList4pre.get(i))[0], ((float[]) validPointList4pre.get(i))[1], ((float[]) validPointList4pre.get(i + 1))[0], ((float[]) validPointList4pre.get(i + 1))[1], paint4line);
            }
            paint4line.setColor(line4postColor);
            paint4line.setStyle(Style.STROKE);
            List<float[]> validPointList4post = new ArrayList();
            if (this.points4post.length > 1) {
                i = 0;
                while (i < this.points4post.length) {
                    if (i == 0 || i == this.points4post.length - 1) {
                        if (this.points4post[i] < 0.2f) {
                            this.points4post[i] = 0.2f;
                        }
                        pointX = getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) (this.points4post.length - 1)));
                        pointY = getYCoordinate(this.points4post[i], axis4y);
                        validPointList4post.add(new float[]{pointX, pointY});
                    } else if (this.points4post[i] >= 0.2f && this.points4post[i] <= 0.6f) {
                        pointX = getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) (this.points4post.length - 1)));
                        pointY = getYCoordinate(this.points4post[i], axis4y);
                        validPointList4post.add(new float[]{pointX, pointY});
                    }
                    i++;
                }
            }
            for (i = 0; i < validPointList4post.size() - 1; i++) {
                canvas.drawLine(((float[]) validPointList4post.get(i))[0], ((float[]) validPointList4post.get(i))[1], ((float[]) validPointList4post.get(i + 1))[0], ((float[]) validPointList4post.get(i + 1))[1], paint4line);
            }
            return;
        }
        this.array4line.recycle();
    }

    private void draw4point(Canvas canvas) {
        int axis4x = getMeasuredWidth();
        int axis4y = getMeasuredHeight();
        if (this.array4point != null) {
            boolean point4antiAlias = this.array4point.getBoolean(1, true);
            int point4preColor = this.array4line.getColor(2, Color.argb(220, 97, 173, 244));
            int point4postColor = this.array4line.getColor(3, Color.argb(220, 123, 207, 35));
            float point4strokeWidth = this.array4point.getDimension(4, 3.0f);
            boolean point4fill = this.array4point.getBoolean(6, true);
            Paint paint4point = new Paint();
            if (point4fill) {
                paint4point.setStyle(Style.FILL_AND_STROKE);
            } else {
                paint4point.setStyle(Style.STROKE);
            }
            paint4point.setAntiAlias(point4antiAlias);
            paint4point.setColor(point4preColor);
            paint4point.setStrokeWidth((float) Utils.dip2px(this.context, point4strokeWidth));
            int points4preNum = 0;
            if (this.points4pre.length == 1) {
                points4preNum = 1;
            } else if (this.points4pre.length > 1) {
                points4preNum = this.points4pre.length - 1;
            }
            int points4postNum = 0;
            if (this.points4post.length == 1) {
                points4postNum = 1;
            } else if (this.points4post.length > 1) {
                points4postNum = this.points4post.length - 1;
            }
            int i = 0;
            while (i < this.points4pre.length) {
                if (this.points4pre[i] >= 0.2f && this.points4pre[i] <= 0.6f) {
                    canvas.drawCircle(getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) points4preNum)), getYCoordinate(this.points4pre[i], axis4y), 5.0f, paint4point);
                }
                i++;
            }
            paint4point.setColor(point4postColor);
            i = 0;
            while (i < this.points4post.length) {
                if (this.points4post[i] >= 0.2f && this.points4post[i] <= 0.6f) {
                    canvas.drawCircle(getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) points4postNum)), getYCoordinate(this.points4post[i], axis4y), 5.0f, paint4point);
                }
                i++;
            }
            paint4point.setColor(Color.argb(220, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK));
            paint4point.setStyle(Style.STROKE);
            paint4point.setStrokeWidth((float) Utils.dip2px(this.context, 0.5f));
            i = 0;
            while (i < this.points4pre.length) {
                if (this.points4pre[i] >= 0.2f && this.points4pre[i] <= 0.6f) {
                    canvas.drawCircle(getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) points4preNum)), getYCoordinate(this.points4pre[i], axis4y), 4.0f, paint4point);
                }
                i++;
            }
            i = 0;
            while (i < this.points4post.length) {
                if (this.points4post[i] >= 0.2f && this.points4post[i] <= 0.6f) {
                    canvas.drawCircle(getStartPointX(axis4x) + ((((float) i) * getValidAxis4Xlength(axis4x)) / ((float) points4postNum)), getYCoordinate(this.points4post[i], axis4y), 4.0f, paint4point);
                }
                i++;
            }
            return;
        }
        this.array4point.recycle();
    }

    private void draw4floatTip(float tipX, float tipY) {
        int axis4x = getMeasuredWidth();
        int axis4y = getMeasuredHeight();
        Bitmap bitmap4pre = BitmapFactory.decodeResource(getResources(), R.drawable.ic_tip4pre);
        Bitmap bitmap4post = BitmapFactory.decodeResource(getResources(), R.drawable.ic_tip4post);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) Utils.dip2px(this.context, 8.0f));
        int index4pre = getXLabel4preIndexByXCoodinate(tipX, axis4x);
        int index4post = getXLabel4postIndexByXCoodinate(tipX, axis4x);
        float pointYCood = getPointValueByYCoordinate(tipY, axis4y);
        if (this.points4pre.length > 0) {
            int length = this.points4pre.length;
            if (index4pre >= 0) {
                index4pre = this.points4pre.length - 1;
            }
            if (index4pre < 0) {
                index4pre = 0;
            }
            float pointY4pre = this.points4pre[index4pre];
            float tipYCood4pre = getYCoordinate(pointY4pre, axis4y);
            float tipXCood4pre = getX4preCoordinate(index4pre, axis4x);
            if (((double) Math.abs(pointYCood - pointY4pre)) <= 0.02d) {
                this.canvas.drawBitmap(bitmap4pre, null, new RectF(tipXCood4pre - Constants.BODY_PART_FACE_MORMAL_MAX, tipYCood4pre - 100.0f, Constants.BODY_PART_FACE_MORMAL_MAX + tipXCood4pre, tipYCood4pre), paint);
                paint.setStrokeWidth((float) Utils.dip2px(this.context, 10.0f));
                paint.setStyle(Style.FILL);
                String tipText4numPre = new BigDecimal((double) (100.0f * pointY4pre)).setScale(2, 4).floatValue() + "%";
                this.canvas.drawText(tipText4numPre, tipXCood4pre - (paint.measureText(tipText4numPre) * 0.5f), tipYCood4pre - 50.0f, paint);
            }
        }
        if (this.points4post.length > 0) {

            if (index4post >= 0) {
                index4post = this.points4post.length - 1;
            }
            if (index4post <= 0) {
                index4post = 1;
            }
            float pointY4post = this.points4post[index4post];
            float tipYCood4post = getYCoordinate(pointY4post, axis4y);
            float tipXCood4post = getX4postCoordinate(index4post, axis4x);
            if (((double) Math.abs(pointYCood - pointY4post)) <= 0.02d) {
                this.canvas.drawBitmap(bitmap4post, null, new RectF(tipXCood4post - Constants.BODY_PART_FACE_MORMAL_MAX, tipYCood4post - 100.0f, Constants.BODY_PART_FACE_MORMAL_MAX + tipXCood4post, tipYCood4post), paint);
                paint.setStrokeWidth((float) Utils.dip2px(this.context, 10.0f));
                paint.setStyle(Style.FILL);
                String tipText4numPost = String.valueOf(new StringBuilder(String.valueOf(new BigDecimal((double) (100.0f * pointY4post)).setScale(2, 4).floatValue())).append("%").toString());
                this.canvas.drawText(tipText4numPost, tipXCood4post - (paint.measureText(tipText4numPost) * 0.5f), tipYCood4post - 50.0f, paint);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        draw4axis(canvas);
        draw4scale(canvas);
        draw4line(canvas);
        draw4point(canvas);
    }

    private float getYCoordinate(float pointValue, int axis4y) {
        return (1.22f - (1.6f * pointValue)) * ((float) axis4y);
    }

    private float getPointValueByYCoordinate(float pointYCoordinate, int axis4y) {
        return 0.7625f - ((0.625f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) axis4y))) * pointYCoordinate);
    }

    private int getXLabel4preIndexByXCoodinate(float pointXCoordinate, int axis4x) {
        return new BigDecimal((double) (((pointXCoordinate - (0.1f * ((float) axis4x))) * ((float) (this.xLabel4pre.length - 1))) / (0.82f * ((float) axis4x)))).setScale(0, 4).intValue();
    }

    private int getXLabel4postIndexByXCoodinate(float pointXCoordinate, int axis4x) {
        return new BigDecimal((double) (((pointXCoordinate - (0.1f * ((float) axis4x))) * ((float) (this.xLabel4post.length - 1))) / (0.82f * ((float) axis4x)))).setScale(0, 4).intValue();
    }

    private float getX4preCoordinate(int index, int axis4x) {
        return (((float) axis4x) * 0.1f) + ((((float) (index * axis4x)) * 0.82f) / ((float) (this.xLabel4pre.length - 1)));
    }

    private float getX4postCoordinate(int index, int axis4x) {
        return (((float) axis4x) * 0.1f) + ((((float) (index * axis4x)) * 0.82f) / ((float) (this.xLabel4post.length - 1)));
    }

    private float getStartPointX(int axis4x) {
        return ((float) axis4x) * 0.1f;
    }

    private float getValidAxis4Xlength(int axis4x) {
        return ((float) axis4x) * 0.82f;
    }

    public String[] getxLabel4pre() {
        return this.xLabel4pre;
    }

    public void setxLabel4pre(String[] xLabel4pre) {
        this.xLabel4pre = xLabel4pre;
    }

    public String[] getxLabel4post() {
        return this.xLabel4post;
    }

    public void setxLabel4post(String[] xLabel4post) {
        this.xLabel4post = xLabel4post;
    }

    public String[] getyLabel() {
        return this.yLabel;
    }

    public void setyLabel(String[] yLabel) {
        this.yLabel = yLabel;
    }

    public float[] getPoints4pre() {
        return this.points4pre;
    }

    public void setPoints4pre(float[] points4pre) {
        this.points4pre = points4pre;
    }

    public float[] getPoints4post() {
        return this.points4post;
    }

    public void setPoints4post(float[] points4post) {
        this.points4post = points4post;
    }
}
