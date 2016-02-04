package com.mlizhi.widgets.cursor4water;

import android.content.Context;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlizhi.utils.Constants;
import com.philips.skincare.skincareprototype.R;

public class CursorView extends RelativeLayout {
    private float bgTextSize;
    private float bgViewWidth;
    private TextView cursorText;
    private int cursorTextColor;
    private float mProgress;
    private TextPaint tp;

    public CursorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
    }

    public CursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
    }

    public CursorView(Context context) {
        super(context);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
        initCursorView();
    }

    public void initCursorView() {
        LayoutParams param4cursor = new LayoutParams(-2, -2);
        param4cursor.addRule(15, -1);
        param4cursor.addRule(9);
        param4cursor.addRule(10);
        this.cursorText = new TextView(getContext());
        this.cursorText.setGravity(17);
        this.cursorText.setText("0.0%");
        this.tp = this.cursorText.getPaint();
        addView(this.cursorText, param4cursor);
    }

    public int getCursorHeight() {
        FontMetrics fm = this.tp.getFontMetrics();
        return (int) (Math.ceil((double) (fm.descent - fm.ascent)) + 10.0d);
    }

    public float getmProgress() {
        return this.mProgress;
    }

    public void setmProgress(float mProgress, float minScale, float maxScale) {
        this.mProgress = mProgress;
        String str = String.valueOf(new StringBuilder(String.valueOf(mProgress)).append("%").toString());
        this.cursorText.setText(str);
        this.cursorText.setTextColor(this.cursorTextColor);
        this.cursorText.setBackgroundResource(R.drawable.ic_cursor_bg);
        ViewGroup.LayoutParams param4cursor = this.cursorText.getLayoutParams();
        int leftMargin = (int) ((this.bgViewWidth * ((mProgress - minScale) / (maxScale - minScale))) - (this.tp.measureText(str) * 0.5f));
        if (param4cursor != null) {
            ((LayoutParams) param4cursor).leftMargin = leftMargin;
        }
        TranslateAnimation translate = new TranslateAnimation((float) (-leftMargin), 0.0f, 0.0f, 0.0f);
        translate.setDuration(1000);
        translate.setInterpolator(new LinearInterpolator());
        translate.setFillAfter(true);
        this.cursorText.startAnimation(translate);
    }

    public float getBgViewWidth() {
        return this.bgViewWidth;
    }

    public void setBgViewWidth(float bgViewWidth) {
        this.bgViewWidth = bgViewWidth;
    }

    public float getBgTextSize() {
        return this.bgTextSize;
    }

    public void setBgTextSize(float bgTextSize) {
        this.bgTextSize = bgTextSize;
    }

    public int getCursorTextColor() {
        return this.cursorTextColor;
    }

    public void setCursorTextColor(int cursorTextColor) {
        this.cursorTextColor = cursorTextColor;
    }
}
