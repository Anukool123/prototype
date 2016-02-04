package com.mlizhi.widgets.waterfall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.skincare.skincareprototype.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MultiColumnPullToRefreshListView extends MultiColumnListView {
    private static /* synthetic */ int[] f2319x6d826fb6 = null;
    private static final int BOUNCE_ANIMATION_DELAY = 0;
    private static final int BOUNCE_ANIMATION_DURATION = 200;
    private static final float PULL_RESISTANCE = 1.7f;
    private static final int ROTATE_ARROW_ANIMATION_DURATION = 250;
    private static int measuredHeaderHeight;
    private TranslateAnimation bounceAnimation;
    private boolean bounceBackHeader;
    private RotateAnimation flipAnimation;
    private boolean hasResetHeader;
    private RelativeLayout header;
    private LinearLayout headerContainer;
    private int headerPadding;
    private ImageView image;
    private boolean isPulling;
    private long lastUpdated;
    private SimpleDateFormat lastUpdatedDateFormat;
    private String lastUpdatedText;
    private TextView lastUpdatedTextView;
    private boolean lockScrollWhileRefreshing;
    private OnRefreshListener onRefreshListener;
    private float previousY;
    private String pullToRefreshText;
    private String refreshingText;
    private String releaseToRefreshText;
    private RotateAnimation reverseFlipAnimation;
    private boolean scrollbarEnabled;
    private boolean showLastUpdatedText;
    private ProgressBar spinner;
    private State state;
    private TextView text;

    private class HeaderAnimationListener implements AnimationListener {
        private int height;
        private State stateAtAnimationStart;
        private int translation;

        /* renamed from: com.mlizhi.widgets.waterfall.MultiColumnPullToRefreshListView.HeaderAnimationListener.1 */
        class C01711 implements Runnable {
            C01711() {
            }

            public void run() {
                MultiColumnPullToRefreshListView.this.resetHeader();
            }
        }

        public HeaderAnimationListener(int translation) {
            this.translation = translation;
        }

        public void onAnimationStart(Animation animation) {
            this.stateAtAnimationStart = MultiColumnPullToRefreshListView.this.state;
            ViewGroup.LayoutParams lp = MultiColumnPullToRefreshListView.this.getLayoutParams();
            this.height = lp.height;
            lp.height = MultiColumnPullToRefreshListView.this.getHeight() - this.translation;
            MultiColumnPullToRefreshListView.this.setLayoutParams(lp);
            if (MultiColumnPullToRefreshListView.this.scrollbarEnabled) {
                MultiColumnPullToRefreshListView.this.setVerticalScrollBarEnabled(false);
            }
        }

        public void onAnimationEnd(Animation animation) {
            int i;
            MultiColumnPullToRefreshListView multiColumnPullToRefreshListView = MultiColumnPullToRefreshListView.this;
            if (this.stateAtAnimationStart == State.REFRESHING) {
                i = MultiColumnPullToRefreshListView.BOUNCE_ANIMATION_DELAY;
            } else {
                i = (-MultiColumnPullToRefreshListView.measuredHeaderHeight) - MultiColumnPullToRefreshListView.this.headerContainer.getTop();
            }
            multiColumnPullToRefreshListView.setHeaderPadding(i);
            ViewGroup.LayoutParams lp = MultiColumnPullToRefreshListView.this.getLayoutParams();
            lp.height = this.height;
            MultiColumnPullToRefreshListView.this.setLayoutParams(lp);
            if (MultiColumnPullToRefreshListView.this.scrollbarEnabled) {
                MultiColumnPullToRefreshListView.this.setVerticalScrollBarEnabled(true);
            }
            if (MultiColumnPullToRefreshListView.this.bounceBackHeader) {
                MultiColumnPullToRefreshListView.this.bounceBackHeader = false;
                MultiColumnPullToRefreshListView.this.postDelayed(new C01711(), 0);
            } else if (this.stateAtAnimationStart != State.REFRESHING) {
                MultiColumnPullToRefreshListView.this.setState(State.PULL_TO_REFRESH);
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private class PTROnGlobalLayoutListener implements OnGlobalLayoutListener {
        private PTROnGlobalLayoutListener() {
        }

        public void onGlobalLayout() {
            int initialHeaderHeight = MultiColumnPullToRefreshListView.this.header.getHeight();
            if (initialHeaderHeight > 0) {
                MultiColumnPullToRefreshListView.measuredHeaderHeight = initialHeaderHeight;
                if (MultiColumnPullToRefreshListView.measuredHeaderHeight > 0 && MultiColumnPullToRefreshListView.this.state != State.REFRESHING) {
                    MultiColumnPullToRefreshListView.this.setHeaderPadding(-MultiColumnPullToRefreshListView.measuredHeaderHeight);
                    MultiColumnPullToRefreshListView.this.requestLayout();
                }
            }
            MultiColumnPullToRefreshListView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private enum State {
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING
    }

    static /* synthetic */ int[] m3477x6d826fb6() {
        int[] iArr = f2319x6d826fb6;
        if (iArr == null) {
            iArr = new int[State.values().length];
            try {
                iArr[State.PULL_TO_REFRESH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[State.REFRESHING.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[State.RELEASE_TO_REFRESH.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            f2319x6d826fb6 = iArr;
        }
        return iArr;
    }

    public MultiColumnPullToRefreshListView(Context context) {
        super(context);
        this.lastUpdatedDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        this.lastUpdated = -1;
        this.isPulling = false;
        init();
    }

    public MultiColumnPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.lastUpdatedDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        this.lastUpdated = -1;
        this.isPulling = false;
        init();
    }

    public MultiColumnPullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.lastUpdatedDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        this.lastUpdated = -1;
        this.isPulling = false;
        init();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public boolean isRefreshing() {
        return this.state == State.REFRESHING;
    }

    public void setLockScrollWhileRefreshing(boolean lockScrollWhileRefreshing) {
        this.lockScrollWhileRefreshing = lockScrollWhileRefreshing;
    }

    public void setShowLastUpdatedText(boolean showLastUpdatedText) {
        this.showLastUpdatedText = showLastUpdatedText;
        if (!showLastUpdatedText) {
            this.lastUpdatedTextView.setVisibility(View.GONE);
        }
    }

    public void setLastUpdatedDateFormat(SimpleDateFormat lastUpdatedDateFormat) {
        this.lastUpdatedDateFormat = lastUpdatedDateFormat;
    }

    public void setRefreshing() {
        this.state = State.REFRESHING;
        setUiRefreshing();
    }

    public void onRefreshComplete() {
        this.state = State.PULL_TO_REFRESH;
        resetHeader();
        this.lastUpdated = System.currentTimeMillis();
    }

    public void setTextPullToRefresh(String pullToRefreshText) {
        this.pullToRefreshText = pullToRefreshText;
        if (this.state == State.PULL_TO_REFRESH) {
            this.text.setText(pullToRefreshText);
        }
    }

    public void setTextReleaseToRefresh(String releaseToRefreshText) {
        this.releaseToRefreshText = releaseToRefreshText;
        if (this.state == State.RELEASE_TO_REFRESH) {
            this.text.setText(releaseToRefreshText);
        }
    }

    public void setTextRefreshing(String refreshingText) {
        this.refreshingText = refreshingText;
        if (this.state == State.REFRESHING) {
            this.text.setText(refreshingText);
        }
    }

    private void init() {
        setVerticalFadingEdgeEnabled(false);
        this.headerContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.ptr_header, null);
        this.header = (RelativeLayout) this.headerContainer.findViewById(R.id.ptr_id_header);
        this.text = (TextView) this.header.findViewById(R.id.ptr_id_text);
        this.lastUpdatedTextView = (TextView) this.header.findViewById(R.id.ptr_id_last_updated);
        this.image = (ImageView) this.header.findViewById(R.id.ptr_id_image);
        this.spinner = (ProgressBar) this.header.findViewById(R.id.ptr_id_spinner);
        this.pullToRefreshText = getContext().getString(R.string.ptr_pull_to_refresh);
        this.releaseToRefreshText = getContext().getString(R.string.ptr_release_to_refresh);
        this.refreshingText = getContext().getString(R.string.ptr_refreshing);
        this.lastUpdatedText = getContext().getString(R.string.ptr_last_updated);
        this.flipAnimation = new RotateAnimation(0.0f, -180.0f, 1, 0.5f, 1, 0.5f);
        this.flipAnimation.setInterpolator(new LinearInterpolator());
        this.flipAnimation.setDuration(250);
        this.flipAnimation.setFillAfter(true);
        this.reverseFlipAnimation = new RotateAnimation(-180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        this.reverseFlipAnimation.setInterpolator(new LinearInterpolator());
        this.reverseFlipAnimation.setDuration(250);
        this.reverseFlipAnimation.setFillAfter(true);
        addHeaderView(this.headerContainer);
        setState(State.PULL_TO_REFRESH);
        this.scrollbarEnabled = isVerticalScrollBarEnabled();
        this.header.getViewTreeObserver().addOnGlobalLayoutListener(new PTROnGlobalLayoutListener());
    }

    private void setHeaderPadding(int padding) {
        this.headerPadding = padding;
        MarginLayoutParams mlp = (MarginLayoutParams) this.header.getLayoutParams();
        mlp.setMargins(BOUNCE_ANIMATION_DELAY, Math.round((float) padding), BOUNCE_ANIMATION_DELAY, BOUNCE_ANIMATION_DELAY);
        this.header.setLayoutParams(mlp);
    }

    private boolean isPull(MotionEvent event) {
        return this.isPulling;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.lockScrollWhileRefreshing) {
            if (this.state == State.REFRESHING) {
                return true;
            }
            if (!(getAnimation() == null || getAnimation().hasEnded())) {
                return true;
            }
        }
        switch (event.getAction()) {
            case BOUNCE_ANIMATION_DELAY /*0*/:
                if (getFirstVisiblePosition() == 0) {
                    this.previousY = event.getY();
                    break;
                }
                break;
            case 1 /*1*/:
            case 3 /*3*/:
                this.isPulling = false;
                break;
            case 2 /*2*/:
                if (getFirstVisiblePosition() != 0 || event.getY() - this.previousY <= 0.0f) {
                    this.isPulling = false;
                    break;
                }
                this.isPulling = true;
                return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.lockScrollWhileRefreshing && (this.state == State.REFRESHING || (getAnimation() != null && !getAnimation().hasEnded()))) {
            return true;
        }
        switch (event.getAction()) {
            case 1 /*1*/:
                if (isPull(event) && (this.state == State.RELEASE_TO_REFRESH || getFirstVisiblePosition() == 0)) {
                    switch (m3477x6d826fb6()[this.state.ordinal()]) {
                        case 1 /*1*/:
                            resetHeader();
                            break;
                        case 2 /*2*/:
                            setState(State.REFRESHING);
                            bounceBackHeader();
                            break;
                        default:
                            break;
                    }
                }
            case 2 /*2*/:
                if (isPull(event)) {
                    float y = event.getY();
                    float diff = y - this.previousY;
                    if (diff > 0.0f) {
                        diff /= PULL_RESISTANCE;
                    }
                    this.previousY = y;
                    int newHeaderPadding = Math.max(Math.round(((float) this.headerPadding) + diff), -this.header.getHeight());
                    if (!(newHeaderPadding == this.headerPadding || this.state == State.REFRESHING)) {
                        setHeaderPadding(newHeaderPadding);
                        if (this.state != State.PULL_TO_REFRESH || this.headerPadding <= 0) {
                            if (this.state == State.RELEASE_TO_REFRESH && this.headerPadding < 0) {
                                setState(State.PULL_TO_REFRESH);
                                this.image.clearAnimation();
                                this.image.startAnimation(this.reverseFlipAnimation);
                                break;
                            }
                        }
                        setState(State.RELEASE_TO_REFRESH);
                        this.image.clearAnimation();
                        this.image.startAnimation(this.flipAnimation);
                        break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void bounceBackHeader() {
        int yTranslate;
        if (this.state == State.REFRESHING) {
            yTranslate = this.header.getHeight() - this.headerContainer.getHeight();
        } else {
            yTranslate = (-this.headerContainer.getHeight()) - this.headerContainer.getTop();
        }
        this.bounceAnimation = new TranslateAnimation(BOUNCE_ANIMATION_DELAY, 0.0f, BOUNCE_ANIMATION_DELAY, 0.0f, BOUNCE_ANIMATION_DELAY, 0.0f, BOUNCE_ANIMATION_DELAY, (float) yTranslate);
        this.bounceAnimation.setDuration(200);
        this.bounceAnimation.setFillEnabled(true);
        this.bounceAnimation.setFillAfter(false);
        this.bounceAnimation.setFillBefore(true);
        this.bounceAnimation.setAnimationListener(new HeaderAnimationListener(yTranslate));
        startAnimation(this.bounceAnimation);
    }

    private void resetHeader() {
        if (getFirstVisiblePosition() > 0) {
            setHeaderPadding(-this.header.getHeight());
            setState(State.PULL_TO_REFRESH);
        } else if (getAnimation() == null || getAnimation().hasEnded()) {
            bounceBackHeader();
        } else {
            this.bounceBackHeader = true;
        }
    }

    private void setUiRefreshing() {
        this.spinner.setVisibility(View.VISIBLE);
        this.image.clearAnimation();
        this.image.setVisibility(View.INVISIBLE);
        this.text.setText(this.refreshingText);
    }

    private void setState(State state) {
        this.state = state;
        switch (m3477x6d826fb6()[state.ordinal()]) {
            case 1 /*1*/:
                this.spinner.setVisibility(View.INVISIBLE);
                this.image.setVisibility(View.VISIBLE);
                this.text.setText(this.pullToRefreshText);
                if (this.showLastUpdatedText && this.lastUpdated != -1) {
                    this.lastUpdatedTextView.setVisibility(View.VISIBLE);
                    this.lastUpdatedTextView.setText(String.format(this.lastUpdatedText, new Object[]{this.lastUpdatedDateFormat.format(new Date(this.lastUpdated))}));
                }
            case 2 /*2*/:
                this.spinner.setVisibility(View.INVISIBLE);
                this.image.setVisibility(View.VISIBLE);
                this.text.setText(this.releaseToRefreshText);
            case 3 /*3*/:
                setUiRefreshing();
                this.lastUpdated = System.currentTimeMillis();
                if (this.onRefreshListener == null) {
                    setState(State.PULL_TO_REFRESH);
                } else {
                    this.onRefreshListener.onRefresh();
                }
            default:
        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!this.hasResetHeader) {
            if (measuredHeaderHeight > 0 && this.state != State.REFRESHING) {
                setHeaderPadding(-measuredHeaderHeight);
            }
            this.hasResetHeader = true;
        }
    }
}
