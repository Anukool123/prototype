package com.mlizhi.widgets.cursor4water;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.mlizhi.utils.Constants;
import com.philips.skincare.skincareprototype.R;

public class WaterRangeView extends RelativeLayout {
    private final float DEFAULT_BG_VIEW_WIDTH;
    private final int DEFAULT_CURSOR_TEXT_COLOR;
    private final int DEFAULT_DRY_COLOR;
    private final int DEFAULT_MOIST_COLOR;
    private final int DEFAULT_NORMAL_COLOR;
    private final int DEFAULT_PROGRESS;
    private final int DEFAULT_TEXT_COLOR;
    private final int DEFAULT_TEXT_SIZE;
    private float bgTextSize;
    private float bgViewWidth;
    private int cursorTextColor;
    private CursorView cursorView;
    private int dryColor;
    private float mProgress;
    private float maxScale;
    private float minScale;
    private int moistColor;
    private int normalColor;
    private float normalMaxScale;
    private float normalMinScale;
    private RangeView rangeView;
    private boolean showScale;
    private int textColor;

    public WaterRangeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
        this.cursorView = null;
        this.rangeView = null;
        this.DEFAULT_DRY_COLOR = SupportMenu.CATEGORY_MASK;
        this.DEFAULT_NORMAL_COLOR = -16776961;
        this.DEFAULT_MOIST_COLOR = -16711936;
        this.DEFAULT_TEXT_COLOR = -1;
        this.DEFAULT_CURSOR_TEXT_COLOR = -7829368;
        this.DEFAULT_BG_VIEW_WIDTH = 500.0f;
        this.DEFAULT_PROGRESS = 80;
        this.DEFAULT_TEXT_SIZE = 18;
    }

    public WaterRangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
        this.cursorView = null;
        this.rangeView = null;
        this.DEFAULT_DRY_COLOR = SupportMenu.CATEGORY_MASK;
        this.DEFAULT_NORMAL_COLOR = -16776961;
        this.DEFAULT_MOIST_COLOR = -16711936;
        this.DEFAULT_TEXT_COLOR = -1;
        this.DEFAULT_CURSOR_TEXT_COLOR = -7829368;
        this.DEFAULT_BG_VIEW_WIDTH = 500.0f;
        this.DEFAULT_PROGRESS = 80;
        this.DEFAULT_TEXT_SIZE = 18;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.WaterRangeView);
        if (attributes != null) {
            try {
                this.dryColor = attributes.getColor(0, SupportMenu.CATEGORY_MASK);
                this.normalColor = attributes.getColor(1,getResources().getColor(R.color.normal_primary_color));
                this.moistColor = attributes.getColor(2, getResources().getColor(R.color.moist_primary_color));
                this.textColor = attributes.getColor(5, getResources().getColor(R.color.com_facebook_likeview_text_color));
                this.cursorTextColor = attributes.getColor(6, getResources().getColor(R.color.com_facebook_share_button_text_color));
                this.mProgress = attributes.getFloat(3, 80.0f);
                this.bgViewWidth = attributes.getDimension(7, 500.0f);
                this.bgTextSize = attributes.getDimension(4, 18.0f);
            } finally {
                attributes.recycle();
            }
        }
        initView(context);
    }

    public WaterRangeView(Context context) {
        super(context);
        this.bgViewWidth = 0.0f;
        this.bgTextSize = Constants.BODY_PART_HAND_MORMAL_MIN;
        this.cursorView = null;
        this.rangeView = null;
        this.DEFAULT_DRY_COLOR = SupportMenu.CATEGORY_MASK;
        this.DEFAULT_NORMAL_COLOR = -16776961;
        this.DEFAULT_MOIST_COLOR = -16711936;
        this.DEFAULT_TEXT_COLOR = -1;
        this.DEFAULT_CURSOR_TEXT_COLOR = -7829368;
        this.DEFAULT_BG_VIEW_WIDTH = 500.0f;
        this.DEFAULT_PROGRESS = 80;
        this.DEFAULT_TEXT_SIZE = 18;
    }

    private void initView(Context context) {
        LayoutParams params4cursor = new LayoutParams(-1, -2);
        params4cursor.addRule(9, -1);
        params4cursor.addRule(10);
        this.cursorView = new CursorView(context);
        this.cursorView.setBgTextSize(400.0f);
        this.cursorView.setCursorTextColor(this.cursorTextColor);
        this.cursorView.setBgViewWidth(this.bgViewWidth);
        addView(this.cursorView, params4cursor);
        int height = this.cursorView.getCursorHeight() + 38;
        LayoutParams params4range = new LayoutParams(-1, -2);
        params4range.addRule(14, -1);
        params4range.addRule(9);
        params4range.addRule(10);
        params4range.leftMargin = 0;
        params4range.rightMargin = 0;
        params4range.topMargin = height;
        this.rangeView = new RangeView(context);
        this.rangeView.setBgTextSize(this.bgTextSize);
        this.rangeView.setBgViewWidth(this.bgViewWidth);
        this.rangeView.setDryColor(this.dryColor);
        this.rangeView.setNormalColor(this.normalColor);
        this.rangeView.setMoistColor(this.moistColor);
        this.rangeView.setTextColor(this.textColor);
        this.rangeView.initRangeView(context);
        addView(this.rangeView, params4range);
    }

    public void setProgress(float mProgress, float minScale, float maxScale) {
        this.cursorView.setmProgress(mProgress, minScale, maxScale);
    }

    public void setMeasureWidth(float minScale, float normalMinScale, float normalMaxScale, float maxScale, boolean showScale) {
        this.rangeView.setMeasureWidth(minScale, normalMinScale, normalMaxScale, maxScale, showScale);
    }

    public boolean isShowScale() {
        return this.showScale;
    }

    public void setShowScale(boolean showScale) {
        this.showScale = showScale;
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

    public float getMinScale() {
        return this.minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getNormalMinScale() {
        return this.normalMinScale;
    }

    public void setNormalMinScale(float normalMinScale) {
        this.normalMinScale = normalMinScale;
    }

    public float getNormalMaxScale() {
        return this.normalMaxScale;
    }

    public void setNormalMaxScale(float normalMaxScale) {
        this.normalMaxScale = normalMaxScale;
    }

    public float getMaxScale() {
        return this.maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }
}
