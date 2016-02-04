package com.mlizhi.widgets.cursor4water;

import android.content.Context;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.skincare.skincareprototype.R;

import p016u.aly.bq;

public class RangeView extends RelativeLayout {
    private static final float OFFSET_RIGHT = 0.0f;
    private float bgTextSize;
    private float bgViewWidth;
    private int dryColor;
    private TextView dryLabel;
    private TextView maxScaleText;
    private TextView minScaleText;
    private int moistColor;
    private TextView moistLabel;
    private int normalColor;
    private TextView normalLabel;
    private TextView normalMaxScaleText;
    private TextView normalMinScaleText;
    private int textColor;

    public RangeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.bgTextSize = 10.0f;
        this.bgViewWidth = 0.0f;
    }

    public RangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bgTextSize = 10.0f;
        this.bgViewWidth = 0.0f;
    }

    public RangeView(Context context) {
        super(context);
        this.bgTextSize = 10.0f;
        this.bgViewWidth = 0.0f;
    }

    public void initRangeView(Context context) {
        LayoutParams param4dry = new LayoutParams(-2, -2);
        param4dry.addRule(9);
        param4dry.addRule(10);
        this.dryLabel = new TextView(getContext());
        this.dryLabel.setText(R.string.detect_result_dry);
        this.dryLabel.setTextColor(this.textColor);
        this.dryLabel.setGravity(17);
        addView(this.dryLabel, param4dry);
        LayoutParams param4normal = new LayoutParams(-2, -2);
        param4normal.addRule(9);
        param4normal.addRule(10);
        this.normalLabel = new TextView(getContext());
        this.normalLabel.setText(R.string.detect_result_medium);
        this.normalLabel.setTextColor(this.textColor);
        this.normalLabel.setBackgroundColor(this.normalColor);
        addView(this.normalLabel, param4normal);
        LayoutParams param4moist = new LayoutParams(-2, -2);
        param4moist.addRule(9);
        param4moist.addRule(10);
        this.moistLabel = new TextView(getContext());
        this.moistLabel.setText(R.string.detect_result_moisture);
        this.moistLabel.setTextColor(this.textColor);
        this.moistLabel.setBackgroundColor(this.moistColor);
        addView(this.moistLabel, param4moist);
        this.normalMaxScaleText = new TextView(getContext());
        addView(this.normalMaxScaleText);
        this.normalMinScaleText = new TextView(getContext());
        addView(this.normalMinScaleText);
        this.maxScaleText = new TextView(getContext());
        addView(this.maxScaleText);
        this.minScaleText = new TextView(getContext());
        addView(this.minScaleText);
    }

    public void setMeasureWidth(float minScale, float normalMinScale, float normalMaxScale, float maxScale, boolean showScale) {
        if (showScale) {
            this.minScaleText.setText(String.valueOf(new StringBuilder(String.valueOf(minScale)).append("%").toString()));
            this.normalMinScaleText.setText(String.valueOf(new StringBuilder(String.valueOf(normalMinScale)).append("%").toString()));
            this.normalMaxScaleText.setText(String.valueOf(new StringBuilder(String.valueOf(normalMaxScale)).append("%").toString()));
            this.maxScaleText.setText(String.valueOf(new StringBuilder(String.valueOf(maxScale)).append("%").toString()));
        } else {
            this.minScaleText.setText(bq.f888b);
            this.normalMinScaleText.setText(bq.f888b);
            this.normalMaxScaleText.setText(bq.f888b);
            this.maxScaleText.setText(bq.f888b);
        }
        TextPaint tp = this.minScaleText.getPaint();
        FontMetrics fm = tp.getFontMetrics();
        int textWidth = (int) tp.measureText(String.valueOf(new StringBuilder(String.valueOf(minScale)).append("%").toString()));
        int textHeight = (int) (Math.ceil((double) (fm.bottom - fm.top)) + 16.0d);
        ViewGroup.LayoutParams param4dry = this.dryLabel.getLayoutParams();
        this.dryLabel.setTextSize(this.bgTextSize);
        this.dryLabel.setBackgroundColor(this.dryColor);
        if (param4dry != null) {
            ((LayoutParams) param4dry).leftMargin = (int) ((this.bgViewWidth * 0.0f) + 0.0f);
            ((LayoutParams) param4dry).height = textHeight;
            ((LayoutParams) param4dry).width = (int) (this.bgViewWidth * ((normalMinScale - minScale) / (maxScale - minScale)));
        }
        ViewGroup.LayoutParams param4normal = this.normalLabel.getLayoutParams();
        this.normalLabel.setTextSize(this.bgTextSize);
        this.normalLabel.setGravity(17);
        if (param4normal != null) {
            ((LayoutParams) param4normal).leftMargin = (int) ((this.bgViewWidth * ((normalMinScale - minScale) / (maxScale - minScale))) + 0.0f);
            ((LayoutParams) param4normal).height = textHeight;
            ((LayoutParams) param4normal).width = (int) (this.bgViewWidth * ((normalMaxScale - normalMinScale) / (maxScale - minScale)));
        }
        ViewGroup.LayoutParams param4moist = this.moistLabel.getLayoutParams();
        this.moistLabel.setTextSize(this.bgTextSize);
        this.moistLabel.setGravity(17);
        if (param4moist != null) {
            ((LayoutParams) param4moist).leftMargin = (int) ((this.bgViewWidth * ((normalMaxScale - minScale) / (maxScale - minScale))) + 0.0f);
            ((LayoutParams) param4moist).height = textHeight;
            ((LayoutParams) param4moist).width = (int) (this.bgViewWidth * ((maxScale - normalMaxScale) / (maxScale - minScale)));
        }
        ViewGroup.LayoutParams param4minScale = this.minScaleText.getLayoutParams();
        this.minScaleText.setGravity(19);
        if (param4minScale != null) {
            ((LayoutParams) param4minScale).leftMargin = 0;
            ((LayoutParams) param4minScale).topMargin = textHeight;
        }
        ViewGroup.LayoutParams param4normalMinScale = this.normalMinScaleText.getLayoutParams();
        this.normalMinScaleText.setGravity(17);
        if (param4normalMinScale != null) {
            ((LayoutParams) param4normalMinScale).leftMargin = (int) ((this.bgViewWidth * ((normalMinScale - minScale) / (maxScale - minScale))) - (((float) textWidth) * 0.5f));
            ((LayoutParams) param4normalMinScale).topMargin = textHeight;
        }
        ViewGroup.LayoutParams param4normalMaxScale = this.normalMaxScaleText.getLayoutParams();
        this.normalMaxScaleText.setGravity(17);
        if (param4normalMaxScale != null) {
            ((LayoutParams) param4normalMaxScale).leftMargin = (int) ((this.bgViewWidth * ((normalMaxScale - minScale) / (maxScale - minScale))) - (((float) textWidth) * 0.5f));
            ((LayoutParams) param4normalMaxScale).topMargin = textHeight;
        }
        ViewGroup.LayoutParams param4maxScale = this.maxScaleText.getLayoutParams();
        this.maxScaleText.setGravity(21);
        if (param4maxScale != null) {
            ((LayoutParams) param4maxScale).leftMargin = (int) (this.bgViewWidth - (((float) textWidth) * 1.2f));
            ((LayoutParams) param4maxScale).topMargin = textHeight;
        }
    }

    public float getBgTextSize() {
        return this.bgTextSize;
    }

    public void setBgTextSize(float bgTextSize) {
        this.bgTextSize = bgTextSize;
    }

    public float getBgViewWidth() {
        return this.bgViewWidth;
    }

    public void setBgViewWidth(float bgViewWidth) {
        this.bgViewWidth = bgViewWidth;
    }

    public int getDryColor() {
        return this.dryColor;
    }

    public void setDryColor(int dryColor) {
        this.dryColor = dryColor;
    }

    public int getNormalColor() {
        return this.normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getMoistColor() {
        return this.moistColor;
    }

    public void setMoistColor(int moistColor) {
        this.moistColor = moistColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
