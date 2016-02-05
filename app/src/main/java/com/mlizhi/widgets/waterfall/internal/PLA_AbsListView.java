package com.mlizhi.widgets.waterfall.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.android.volley.DefaultRetryPolicy;
import com.philips.skincare.skincareprototype.R;

import org.java_websocket.framing.CloseFrame;

import java.util.ArrayList;
import java.util.List;

public abstract class PLA_AbsListView extends PLA_AdapterView<ListAdapter> implements OnGlobalLayoutListener, OnTouchModeChangeListener {
    protected static final boolean DEBUG = false;
    private static final int INVALID_POINTER = -1;
    static final int LAYOUT_FORCE_BOTTOM = 3;
    static final int LAYOUT_FORCE_TOP = 1;
    static final int LAYOUT_MOVE_SELECTION = 6;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_SET_SELECTION = 2;
    static final int LAYOUT_SPECIFIC = 4;
    static final int LAYOUT_SYNC = 5;
    private static final boolean PROFILE_FLINGING = false;
    private static final boolean PROFILE_SCROLLING = false;
    private static final String TAG = "PLA_AbsListView";
    protected static final int TOUCH_MODE_DONE_WAITING = 2;
    protected static final int TOUCH_MODE_DOWN = 0;
    protected static final int TOUCH_MODE_FLING = 4;
    private static final int TOUCH_MODE_OFF = 1;
    private static final int TOUCH_MODE_ON = 0;
    static final int TOUCH_MODE_REST = -1;
    protected static final int TOUCH_MODE_SCROLL = 3;
    protected static final int TOUCH_MODE_TAP = 1;
    private static final int TOUCH_MODE_UNKNOWN = -1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    private int mActivePointerId;
    protected ListAdapter mAdapter;
    private int mCacheColorHint;
    protected boolean mCachingStarted;
    private Runnable mClearScrollingCache;
    private ContextMenuInfo mContextMenuInfo;
    AdapterDataSetObserver mDataSetObserver;
    boolean mDrawSelectorOnTop;
    private boolean mFlingProfilingStarted;
    private FlingRunnable mFlingRunnable;
    private boolean mIsChildViewEnabled;
    final boolean[] mIsScrap;
    private int mLastScrollState;
    private int mLastTouchMode;
    int mLastY;
    int mLayoutMode;
    protected Rect mListPadding;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    int mMotionCorrection;
    protected int mMotionPosition;
    int mMotionViewNewTop;
    int mMotionViewOriginalTop;
    int mMotionX;
    int mMotionY;
    private OnScrollListener mOnScrollListener;
    private Runnable mPendingCheckForTap;
    private PerformClick mPerformClick;
    private PositionScroller mPositionScroller;
    final RecycleBin mRecycler;
    int mResurrectToPosition;
    private boolean mScrollProfilingStarted;
    boolean mScrollingCacheEnabled;
    int mSelectedTop;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    Drawable mSelector;
    Rect mSelectorRect;
    private boolean mSmoothScrollbarEnabled;
    boolean mStackFromBottom;
    private Rect mTouchFrame;
    protected int mTouchMode;
    private int mTouchSlop;
    private int mTranscriptMode;
    private VelocityTracker mVelocityTracker;
    protected int mWidthMeasureSpec;

    /* renamed from: com.mlizhi.widgets.waterfall.internal.PLA_AbsListView.1 */
    class C01721 implements Runnable {
        private final /* synthetic */ View val$child;
        private final /* synthetic */ PerformClick val$performClick;

        C01721(View view, PerformClick performClick) {
            this.val$child = view;
            this.val$performClick = performClick;
        }

        public void run() {
            this.val$child.setPressed(PLA_AbsListView.PROFILE_SCROLLING);
            PLA_AbsListView.this.setPressed(PLA_AbsListView.PROFILE_SCROLLING);
            if (!PLA_AbsListView.this.mDataChanged) {
                PLA_AbsListView.this.post(this.val$performClick);
            }
            PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TOUCH_MODE_UNKNOWN;
        }
    }

    /* renamed from: com.mlizhi.widgets.waterfall.internal.PLA_AbsListView.2 */
    class C01732 implements Runnable {
        C01732() {
        }

        public void run() {
            if (PLA_AbsListView.this.mCachingStarted) {
                PLA_AbsListView.this.mCachingStarted = PLA_AbsListView.PROFILE_SCROLLING;
                PLA_AbsListView.this.setChildrenDrawnWithCacheEnabled(PLA_AbsListView.PROFILE_SCROLLING);
                if ((PLA_AbsListView.this.getPersistentDrawingCache() & PLA_AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL) == 0) {
                    PLA_AbsListView.this.setChildrenDrawingCacheEnabled(PLA_AbsListView.PROFILE_SCROLLING);
                }
                if (!PLA_AbsListView.this.isAlwaysDrawnWithCacheEnabled()) {
                    PLA_AbsListView.this.invalidate();
                }
            }
        }
    }

    final class CheckForTap implements Runnable {
        CheckForTap() {
        }

        public void run() {
            if (PLA_AbsListView.this.mTouchMode == 0) {
                PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TRANSCRIPT_MODE_NORMAL;
                View child = PLA_AbsListView.this.getChildAt(PLA_AbsListView.this.mMotionPosition - PLA_AbsListView.this.mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    PLA_AbsListView.this.mLayoutMode = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED;
                    if (PLA_AbsListView.this.mDataChanged) {
                        PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL;
                        return;
                    }
                    PLA_AbsListView.this.layoutChildren();
                    child.setPressed(true);
                    PLA_AbsListView.this.positionSelector(child);
                    PLA_AbsListView.this.setPressed(true);
                    int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                    boolean longClickable = PLA_AbsListView.this.isLongClickable();
                    if (PLA_AbsListView.this.mSelector != null) {
                        Drawable d = PLA_AbsListView.this.mSelector.getCurrent();
                        if (d != null && (d instanceof TransitionDrawable)) {
                            if (longClickable) {
                                ((TransitionDrawable) d).startTransition(longPressTimeout);
                            } else {
                                ((TransitionDrawable) d).resetTransition();
                            }
                        }
                    }
                    if (!longClickable) {
                        PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL;
                    }
                }
            }
        }
    }

    private class FlingRunnable implements Runnable {
        private int mLastFlingY;
        private final Scroller mScroller;

        FlingRunnable() {
            this.mScroller = new Scroller(PLA_AbsListView.this.getContext());
        }

        void start(int initialVelocity) {
            int initialY;
            initialVelocity = PLA_AbsListView.this.modifyFlingInitialVelocity(initialVelocity);
            if (initialVelocity < 0) {
                initialY = Integer.MAX_VALUE;
            } else {
                initialY = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED;
            }
            this.mLastFlingY = initialY;
            this.mScroller.fling(PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, initialY, PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, initialVelocity, PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, Integer.MAX_VALUE, PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, Integer.MAX_VALUE);
            PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TOUCH_MODE_FLING;
            PLA_AbsListView.this.post(this);
        }

        void startScroll(int distance, int duration) {
            int initialY;
            if (distance < 0) {
                initialY = Integer.MAX_VALUE;
            } else {
                initialY = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED;
            }
            this.mLastFlingY = initialY;
            this.mScroller.startScroll(PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, initialY, PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, distance, duration);
            PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TOUCH_MODE_FLING;
            PLA_AbsListView.this.post(this);
        }

        private void endFling() {
            this.mLastFlingY = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED;
            PLA_AbsListView.this.mTouchMode = PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            PLA_AbsListView.this.reportScrollStateChange(PLA_AbsListView.TRANSCRIPT_MODE_DISABLED);
            PLA_AbsListView.this.clearScrollingCache();
            PLA_AbsListView.this.removeCallbacks(this);
            if (PLA_AbsListView.this.mPositionScroller != null) {
                PLA_AbsListView.this.removeCallbacks(PLA_AbsListView.this.mPositionScroller);
            }
            this.mScroller.forceFinished(true);
        }

        public void run() {
            switch (PLA_AbsListView.this.mTouchMode) {
                case PLA_AbsListView.TOUCH_MODE_FLING /*4*/:
                    if (PLA_AbsListView.this.mItemCount == 0 || PLA_AbsListView.this.getChildCount() == 0) {
                        endFling();
                        return;
                    }
                    Scroller scroller = this.mScroller;
                    boolean more = scroller.computeScrollOffset();
                    int y = scroller.getCurrY();
                    int delta = this.mLastFlingY - y;
                    if (delta > 0) {
                        PLA_AbsListView.this.mMotionPosition = PLA_AbsListView.this.mFirstPosition;
                        PLA_AbsListView.this.mMotionViewOriginalTop = PLA_AbsListView.this.getScrollChildTop();
                        delta = Math.min(((PLA_AbsListView.this.getHeight() - PLA_AbsListView.this.getPaddingBottom()) - PLA_AbsListView.this.getPaddingTop()) + PLA_AbsListView.TOUCH_MODE_UNKNOWN, delta);
                    } else {
                        int offsetToLast = PLA_AbsListView.this.getChildCount() + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                        PLA_AbsListView.this.mMotionPosition = PLA_AbsListView.this.mFirstPosition + offsetToLast;
                        PLA_AbsListView.this.mMotionViewOriginalTop = PLA_AbsListView.this.getScrollChildBottom();
                        delta = Math.max(-(((PLA_AbsListView.this.getHeight() - PLA_AbsListView.this.getPaddingBottom()) - PLA_AbsListView.this.getPaddingTop()) + PLA_AbsListView.TOUCH_MODE_UNKNOWN), delta);
                    }
                    boolean atEnd = PLA_AbsListView.this.trackMotionScroll(delta, delta);
                    if (!more || atEnd) {
                        endFling();
                        return;
                    }
                    PLA_AbsListView.this.invalidate();
                    this.mLastFlingY = y;
                    PLA_AbsListView.this.post(this);
                default:
            }
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        @ExportedProperty
        public boolean forceAdd;
        @ExportedProperty
        public boolean recycledHeaderFooter;
        @ExportedProperty(mapping = {@IntToString(from = -1, to = "ITEM_VIEW_TYPE_IGNORE"), @IntToString(from = -2, to = "ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
        public int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScroll(PLA_AbsListView pLA_AbsListView, int i, int i2, int i3);

        void onScrollStateChanged(PLA_AbsListView pLA_AbsListView, int i);
    }

    class PositionScroller implements Runnable {
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_UP_POS = 2;
        private static final int SCROLL_DURATION = 400;
        private int mBoundPos;
        private final int mExtraScroll;
        private int mLastSeenPos;
        private int mMode;
        private int mScrollDuration;
        private int mTargetPos;

        PositionScroller() {
            this.mExtraScroll = ViewConfiguration.get(PLA_AbsListView.this.getContext()).getScaledFadingEdgeLength();
        }

        void start(int position) {
            int viewTravelCount;
            int firstPos = PLA_AbsListView.this.mFirstPosition;
            int lastPos = (PLA_AbsListView.this.getChildCount() + firstPos) + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            if (position <= firstPos) {
                viewTravelCount = (firstPos - position) + MOVE_DOWN_POS;
                this.mMode = MOVE_UP_POS;
            } else if (position >= lastPos) {
                viewTravelCount = (position - lastPos) + MOVE_DOWN_POS;
                this.mMode = MOVE_DOWN_POS;
            } else {
                return;
            }
            if (viewTravelCount > 0) {
                this.mScrollDuration = SCROLL_DURATION / viewTravelCount;
            } else {
                this.mScrollDuration = SCROLL_DURATION;
            }
            this.mTargetPos = position;
            this.mBoundPos = PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            this.mLastSeenPos = PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            PLA_AbsListView.this.post(this);
        }

        void start(int position, int boundPosition) {
            if (boundPosition == PLA_AbsListView.TOUCH_MODE_UNKNOWN) {
                start(position);
                return;
            }
            int viewTravelCount;
            int firstPos = PLA_AbsListView.this.mFirstPosition;
            int lastPos = (PLA_AbsListView.this.getChildCount() + firstPos) + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            int posTravel;
            int boundTravel;
            if (position <= firstPos) {
                int boundPosFromLast = lastPos - boundPosition;
                if (boundPosFromLast >= MOVE_DOWN_POS) {
                    posTravel = (firstPos - position) + MOVE_DOWN_POS;
                    boundTravel = boundPosFromLast + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                    if (boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = MOVE_UP_BOUND;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = MOVE_UP_POS;
                    }
                } else {
                    return;
                }
            } else if (position >= lastPos) {
                int boundPosFromFirst = boundPosition - firstPos;
                if (boundPosFromFirst >= MOVE_DOWN_POS) {
                    posTravel = (position - lastPos) + MOVE_DOWN_POS;
                    boundTravel = boundPosFromFirst + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                    if (boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = MOVE_DOWN_BOUND;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = MOVE_DOWN_POS;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
            if (viewTravelCount > 0) {
                this.mScrollDuration = SCROLL_DURATION / viewTravelCount;
            } else {
                this.mScrollDuration = SCROLL_DURATION;
            }
            this.mTargetPos = position;
            this.mBoundPos = boundPosition;
            this.mLastSeenPos = PLA_AbsListView.TOUCH_MODE_UNKNOWN;
            PLA_AbsListView.this.post(this);
        }

        void stop() {
            PLA_AbsListView.this.removeCallbacks(this);
        }

        public void run() {
            int listHeight = PLA_AbsListView.this.getHeight();
            int firstPos = PLA_AbsListView.this.mFirstPosition;
            int lastViewIndex;
            int lastPos;
            int i;
            View lastView;
            int lastViewHeight;
            int lastViewPixelsShowing;
            int extraScroll;
            switch (this.mMode) {
                case MOVE_DOWN_POS /*1*/:
                    lastViewIndex = PLA_AbsListView.this.getChildCount() + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                    lastPos = firstPos + lastViewIndex;
                    if (lastViewIndex >= 0) {
                        i = this.mLastSeenPos;
                        if (lastPos == mLastSeenPos) {
                            PLA_AbsListView.this.post(this);
                            return;
                        }
                        lastView = PLA_AbsListView.this.getChildAt(lastViewIndex);
                        lastViewHeight = lastView.getHeight();
                        lastViewPixelsShowing = listHeight - lastView.getTop();
                        if (lastPos < PLA_AbsListView.this.mItemCount + PLA_AbsListView.TOUCH_MODE_UNKNOWN) {
                            extraScroll = this.mExtraScroll;
                        } else {
                            extraScroll = PLA_AbsListView.this.mListPadding.bottom;
                        }
                        PLA_AbsListView.this.smoothScrollBy((lastViewHeight - lastViewPixelsShowing) + extraScroll, this.mScrollDuration);
                        this.mLastSeenPos = lastPos;
                        i = this.mTargetPos;
                        if (lastPos < mLastSeenPos) {
                            PLA_AbsListView.this.post(this);
                        }
                    }
                case MOVE_UP_POS /*2*/:
                    i = this.mLastSeenPos;
                    if (firstPos == mLastSeenPos) {
                        PLA_AbsListView.this.post(this);
                        return;
                    }
                    View firstView = PLA_AbsListView.this.getChildAt(PLA_AbsListView.TRANSCRIPT_MODE_DISABLED);
                    if (firstView != null) {
                        int firstViewTop = firstView.getTop();
                        if (firstPos > 0) {
                            extraScroll = this.mExtraScroll;
                        } else {
                            extraScroll = PLA_AbsListView.this.mListPadding.top;
                        }
                        PLA_AbsListView.this.smoothScrollBy(firstViewTop - extraScroll, this.mScrollDuration);
                        this.mLastSeenPos = firstPos;
                        i = this.mTargetPos;
                        if (firstPos > mLastSeenPos) {
                            PLA_AbsListView.this.post(this);
                        }
                    }
                case MOVE_DOWN_BOUND /*3*/:
                    int childCount = PLA_AbsListView.this.getChildCount();
                    i = this.mBoundPos;
                    if (firstPos != mLastSeenPos && childCount > MOVE_DOWN_POS && firstPos + childCount < PLA_AbsListView.this.mItemCount) {
                        int nextPos = firstPos + MOVE_DOWN_POS;
                        if (nextPos == this.mLastSeenPos) {
                            PLA_AbsListView.this.post(this);
                            return;
                        }
                        View nextView = PLA_AbsListView.this.getChildAt(MOVE_DOWN_POS);
                        int nextViewHeight = nextView.getHeight();
                        int nextViewTop = nextView.getTop();
                        extraScroll = this.mExtraScroll;
                        if (nextPos < this.mBoundPos) {
                            PLA_AbsListView.this.smoothScrollBy(Math.max(PLA_AbsListView.TRANSCRIPT_MODE_DISABLED, (nextViewHeight + nextViewTop) - extraScroll), this.mScrollDuration);
                            this.mLastSeenPos = nextPos;
                            PLA_AbsListView.this.post(this);
                        } else if (nextViewTop > extraScroll) {
                            PLA_AbsListView.this.smoothScrollBy(nextViewTop - extraScroll, this.mScrollDuration);
                        }
                    }
                case MOVE_UP_BOUND /*4*/:
                    lastViewIndex = PLA_AbsListView.this.getChildCount() - 2;
                    if (lastViewIndex >= 0) {
                        lastPos = firstPos + lastViewIndex;
                        i = this.mLastSeenPos;
                        if (lastPos == mLastSeenPos) {
                            PLA_AbsListView.this.post(this);
                            return;
                        }
                        lastView = PLA_AbsListView.this.getChildAt(lastViewIndex);
                        lastViewHeight = lastView.getHeight();
                        int lastViewTop = lastView.getTop();
                        lastViewPixelsShowing = listHeight - lastViewTop;
                        this.mLastSeenPos = lastPos;
                        i = this.mBoundPos;
                        if (lastPos > mLastSeenPos) {
                            PLA_AbsListView.this.smoothScrollBy(-(lastViewPixelsShowing - this.mExtraScroll), this.mScrollDuration);
                            PLA_AbsListView.this.post(this);
                            return;
                        }
                        int bottom = listHeight - this.mExtraScroll;
                        int lastViewBottom = lastViewTop + lastViewHeight;
                        if (bottom > lastViewBottom) {
                            PLA_AbsListView.this.smoothScrollBy(-(bottom - lastViewBottom), this.mScrollDuration);
                        }
                    }
                default:
            }
        }
    }

    class RecycleBin {
        private View[] mActiveViews;
        private ArrayList<View> mCurrentScrap;
        private int mFirstActivePosition;
        private RecyclerListener mRecyclerListener;
        private ArrayList<View>[] mScrapViews;
        private int mViewTypeCount;

        RecycleBin() {
            this.mActiveViews = new View[PLA_AbsListView.TRANSCRIPT_MODE_DISABLED];
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            ArrayList[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrapViews[i] = new ArrayList();
            }
            this.mViewTypeCount = viewTypeCount;
            this.mCurrentScrap = scrapViews[PLA_AbsListView.TRANSCRIPT_MODE_DISABLED];
            this.mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            ArrayList<View> scrap;
            int scrapCount;
            int i;
            if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mCurrentScrap;
                scrapCount = scrap.size();
                for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < scrapCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) scrap.get(i)).forceLayout();
                }
                return;
            }
            int typeCount = this.mViewTypeCount;
            for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mScrapViews[i];
                scrapCount = scrap.size();
                for (int j = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) scrap.get(j)).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0 ? true : PLA_AbsListView.PROFILE_SCROLLING;
        }

        void clear() {
            ArrayList<View> scrap;
            int scrapCount;
            int i;
            if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mCurrentScrap;
                scrapCount = scrap.size();
                for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < scrapCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    PLA_AbsListView.this.removeDetachedView((View) scrap.remove((scrapCount + PLA_AbsListView.TOUCH_MODE_UNKNOWN) - i), PLA_AbsListView.PROFILE_SCROLLING);
                }
                return;
            }
            int typeCount = this.mViewTypeCount;
            for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mScrapViews[i];
                scrapCount = scrap.size();
                for (int j = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    PLA_AbsListView.this.removeDetachedView((View) scrap.remove((scrapCount + PLA_AbsListView.TOUCH_MODE_UNKNOWN) - j), PLA_AbsListView.PROFILE_SCROLLING);
                }
            }
        }

        void fillActiveViews(int childCount, int firstActivePosition) {
            if (this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }
            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;
            for (int i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < childCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                View child = PLA_AbsListView.this.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!(lp == null || lp.viewType == -2)) {
                    activeViews[i] = child;
                }
            }
        }

        View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index < 0 || index >= activeViews.length) {
                return null;
            }
            View match = activeViews[index];
            activeViews[index] = null;
            return match;
        }

        View getScrapView(int position) {
            ArrayList<View> scrapViews;
            int size;
            if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrapViews = this.mCurrentScrap;
                size = scrapViews.size();
                if (size > 0) {
                    return (View) scrapViews.remove(size + PLA_AbsListView.TOUCH_MODE_UNKNOWN);
                }
                return null;
            }
            int whichScrap = PLA_AbsListView.this.mAdapter.getItemViewType(position);
            if (whichScrap < 0 || whichScrap >= this.mScrapViews.length) {
                return null;
            }
            scrapViews = this.mScrapViews[whichScrap];
            size = scrapViews.size();
            if (size > 0) {
                return (View) scrapViews.remove(size + PLA_AbsListView.TOUCH_MODE_UNKNOWN);
            }
            return null;
        }

        void addScrapView(View scrap) {
            LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp != null) {
                int viewType = lp.viewType;
                if (shouldRecycleViewType(viewType)) {
                    if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                        PLA_AbsListView.this.dispatchFinishTemporaryDetach(scrap);
                        this.mCurrentScrap.add(scrap);
                    } else {
                        PLA_AbsListView.this.dispatchFinishTemporaryDetach(scrap);
                        this.mScrapViews[viewType].add(scrap);
                    }
                    if (this.mRecyclerListener != null) {
                        this.mRecyclerListener.onMovedToScrapHeap(scrap);
                    }
                } else if (viewType != -2) {
                    PLA_AbsListView.this.removeDetachedView(scrap, PLA_AbsListView.PROFILE_SCROLLING);
                }
            }
        }

        void scrapActiveViews() {
            boolean hasListener;
            boolean multipleScraps;
            View[] activeViews = this.mActiveViews;
            if (this.mRecyclerListener != null) {
                hasListener = true;
            } else {
                hasListener = PLA_AbsListView.PROFILE_SCROLLING;
            }
            if (this.mViewTypeCount > PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                multipleScraps = true;
            } else {
                multipleScraps = PLA_AbsListView.PROFILE_SCROLLING;
            }
            ArrayList<View> scrapViews = this.mCurrentScrap;
            for (int i = activeViews.length + PLA_AbsListView.TOUCH_MODE_UNKNOWN; i >= 0; i += PLA_AbsListView.TOUCH_MODE_UNKNOWN) {
                View victim = activeViews[i];
                if (victim != null) {
                    int whichScrap = ((LayoutParams) victim.getLayoutParams()).viewType;
                    activeViews[i] = null;
                    if (shouldRecycleViewType(whichScrap)) {
                        if (multipleScraps) {
                            scrapViews = this.mScrapViews[whichScrap];
                        }
                        PLA_AbsListView.this.dispatchFinishTemporaryDetach(victim);
                        scrapViews.add(victim);
                        if (hasListener) {
                            this.mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    } else if (whichScrap != -2) {
                        PLA_AbsListView.this.removeDetachedView(victim, PLA_AbsListView.PROFILE_SCROLLING);
                    }
                }
            }
            pruneScrapViews();
        }

        private void pruneScrapViews() {
            int maxViews = this.mActiveViews.length;
            int viewTypeCount = this.mViewTypeCount;
            ArrayList[] scrapViews = this.mScrapViews;
            for (int i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                int extras = size - maxViews;
                size += PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                int j = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED;
                int size2 = size;
                while (j < extras) {
                    size = size2 + PLA_AbsListView.TOUCH_MODE_UNKNOWN;
                    PLA_AbsListView.this.removeDetachedView((View) scrapPile.remove(size2), PLA_AbsListView.PROFILE_SCROLLING);
                    j += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL;
                    size2 = size;
                }
            }
        }

        void reclaimScrapViews(List<View> views) {
            if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                views.addAll(this.mCurrentScrap);
                return;
            }
            int viewTypeCount = this.mViewTypeCount;
            ArrayList[] scrapViews = this.mScrapViews;
            for (int i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                views.addAll(scrapViews[i]);
            }
        }

        void setCacheColorHint(int color) {
            int i;
            ArrayList<View> scrap;
            int scrapCount;
            if (this.mViewTypeCount == PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mCurrentScrap;
                scrapCount = scrap.size();
                for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < scrapCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) scrap.get(i)).setDrawingCacheBackgroundColor(color);
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    scrap = this.mScrapViews[i];
                    scrapCount = scrap.size();
                    for (int j = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                        ((View) scrap.get(i)).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            View[] activeViews = this.mActiveViews;
            int count = activeViews.length;
            for (i = PLA_AbsListView.TRANSCRIPT_MODE_DISABLED; i < count; i += PLA_AbsListView.TRANSCRIPT_MODE_NORMAL) {
                View victim = activeViews[i];
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }
    }

    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    private class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = PLA_AbsListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return (PLA_AbsListView.this.hasWindowFocus() && PLA_AbsListView.this.getWindowAttachCount() == this.mOriginalAttachCount) ? true : PLA_AbsListView.PROFILE_SCROLLING;
        }
    }

    private class PerformClick extends WindowRunnnable implements Runnable {
        View mChild;
        int mClickMotionPosition;

        private PerformClick() {
            super();
        }

        public void run() {
            if (!PLA_AbsListView.this.mDataChanged) {
                ListAdapter adapter = PLA_AbsListView.this.mAdapter;
                int motionPosition = this.mClickMotionPosition;
                if (adapter != null && PLA_AbsListView.this.mItemCount > 0 && motionPosition != PLA_AbsListView.TOUCH_MODE_UNKNOWN && motionPosition < adapter.getCount() && sameWindow()) {
                    PLA_AbsListView.this.performItemClick(this.mChild, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }

    abstract void fillGap(boolean z);

    abstract int findMotionRow(int i);

    public PLA_AbsListView(Context context) {
        super(context);
        this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
        this.mDrawSelectorOnTop = PROFILE_SCROLLING;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionTopPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionRightPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionBottomPadding = TRANSCRIPT_MODE_DISABLED;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = TRANSCRIPT_MODE_DISABLED;
        this.mTouchMode = TOUCH_MODE_UNKNOWN;
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = TOUCH_MODE_UNKNOWN;
        this.mScrollProfilingStarted = PROFILE_SCROLLING;
        this.mFlingProfilingStarted = PROFILE_SCROLLING;
        this.mLastScrollState = TRANSCRIPT_MODE_DISABLED;
        this.mIsScrap = new boolean[TRANSCRIPT_MODE_NORMAL];
        this.mActivePointerId = TOUCH_MODE_UNKNOWN;
        initAbsListView();
        setVerticalScrollBarEnabled(true);
       // TypedArray a = context.obtainStyledAttributes(R.styleable.View);
       // initializeScrollbars(a);
       // a.recycle();
    }

    public PLA_AbsListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.absListViewStyle);
    }

    public PLA_AbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
        this.mDrawSelectorOnTop = PROFILE_SCROLLING;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionTopPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionRightPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionBottomPadding = TRANSCRIPT_MODE_DISABLED;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = TRANSCRIPT_MODE_DISABLED;
        this.mTouchMode = TOUCH_MODE_UNKNOWN;
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = TOUCH_MODE_UNKNOWN;
        this.mScrollProfilingStarted = PROFILE_SCROLLING;
        this.mFlingProfilingStarted = PROFILE_SCROLLING;
        this.mLastScrollState = TRANSCRIPT_MODE_DISABLED;
        this.mIsScrap = new boolean[TRANSCRIPT_MODE_NORMAL];
        this.mActivePointerId = TOUCH_MODE_UNKNOWN;
        initAbsListView();
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.AbsListView, defStyle, TRANSCRIPT_MODE_DISABLED);
        Drawable d = a.getDrawable(TRANSCRIPT_MODE_DISABLED);
        if (d != null) {
            setSelector(d);
        }
        this.mDrawSelectorOnTop = a.getBoolean(TRANSCRIPT_MODE_NORMAL, PROFILE_SCROLLING);
        setStackFromBottom(a.getBoolean(TRANSCRIPT_MODE_ALWAYS_SCROLL, PROFILE_SCROLLING));
        setScrollingCacheEnabled(a.getBoolean(TOUCH_MODE_SCROLL, true));
        setTranscriptMode(a.getInt(LAYOUT_SYNC, TRANSCRIPT_MODE_DISABLED));
        setCacheColorHint(a.getColor(LAYOUT_MOVE_SELECTION, TRANSCRIPT_MODE_DISABLED));
        setSmoothScrollbarEnabled(a.getBoolean(8, true));
        a.recycle();
    }

    private void initAbsListView() {
        setClickable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(PROFILE_SCROLLING);
        setAlwaysDrawnWithCacheEnabled(PROFILE_SCROLLING);
        setScrollingCacheEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    @ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    void invokeOnItemScrollListener() {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
        }
    }

    @ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        this.mScrollingCacheEnabled = enabled;
    }

    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view == null || view.getParent() != this) {
            super.getFocusedRect(r);
            return;
        }
        view.getFocusedRect(r);
        offsetDescendantRectToMyCoords(view, r);
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(R.drawable.list_selector_background));
    }

    @ExportedProperty
    public boolean isStackFromBottom() {
        return this.mStackFromBottom;
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        if (this.mStackFromBottom != stackFromBottom) {
            this.mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        this.mDataChanged = true;
        requestLayout();
    }

    public void requestLayout() {
        if (!this.mBlockLayoutRequests && !this.mInLayout) {
            super.requestLayout();
        }
    }

    void resetList() {
        removeAllViewsInLayout();
        this.mFirstPosition = TRANSCRIPT_MODE_DISABLED;
        this.mDataChanged = PROFILE_SCROLLING;
        this.mNeedSync = PROFILE_SCROLLING;
        this.mOldSelectedPosition = TOUCH_MODE_UNKNOWN;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSelectorRect.setEmpty();
        invalidate();
    }

    protected int computeVerticalScrollExtent() {
        int count = getChildCount();
        if (count <= 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        if (!this.mSmoothScrollbarEnabled) {
            return TRANSCRIPT_MODE_NORMAL;
        }
        int extent = count * 100;
        View view = getChildAt(TRANSCRIPT_MODE_DISABLED);
        int top = getFillChildTop();
        int height = view.getHeight();
        if (height > 0) {
            extent += (top * 100) / height;
        }
        view = getChildAt(count + TOUCH_MODE_UNKNOWN);
        int bottom = getScrollChildBottom();
        height = view.getHeight();
        if (height > 0) {
            return extent - (((bottom - getHeight()) * 100) / height);
        }
        return extent;
    }

    protected int computeVerticalScrollOffset() {
        int firstPosition = this.mFirstPosition;
        int childCount = getChildCount();
        if (firstPosition < 0 || childCount <= 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        if (this.mSmoothScrollbarEnabled) {
            View view = getChildAt(TRANSCRIPT_MODE_DISABLED);
            int top = getFillChildTop();
            int height = view.getHeight();
            if (height > 0) {
                return Math.max(((firstPosition * 100) - ((top * 100) / height)) + ((int) (((((float) getScrollY()) / ((float) getHeight())) * ((float) this.mItemCount)) * 100.0f)), TRANSCRIPT_MODE_DISABLED);
            }
            return TRANSCRIPT_MODE_DISABLED;
        }
        int index;
        int count = this.mItemCount;
        if (firstPosition == 0) {
            index = TRANSCRIPT_MODE_DISABLED;
        } else if (firstPosition + childCount == count) {
            index = count;
        } else {
            index = firstPosition + (childCount / TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }
        return (int) (((float) firstPosition) + (((float) childCount) * (((float) index) / ((float) count))));
    }

    protected int computeVerticalScrollRange() {
        if (this.mSmoothScrollbarEnabled) {
            return Math.max(this.mItemCount * 100, TRANSCRIPT_MODE_DISABLED);
        }
        return this.mItemCount;
    }

    protected float getTopFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getTopFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if (this.mFirstPosition > 0) {
            return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        int top = getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
        return top < getPaddingTop() ? ((float) (-(top - getPaddingTop()))) / ((float) getVerticalFadingEdgeLength()) : fadeEdge;
    }

    protected float getBottomFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getBottomFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if ((this.mFirstPosition + count) + TOUCH_MODE_UNKNOWN < this.mItemCount + TOUCH_MODE_UNKNOWN) {
            return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        int bottom = getChildAt(count + TOUCH_MODE_UNKNOWN).getBottom();
        int height = getHeight();
        return bottom > height - getPaddingBottom() ? ((float) ((bottom - height) + getPaddingBottom())) / ((float) getVerticalFadingEdgeLength()) : fadeEdge;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mSelector == null) {
            useDefaultSelector();
        }
        Rect listPadding = this.mListPadding;
        listPadding.left = this.mSelectionLeftPadding + getPaddingLeft();
        listPadding.top = this.mSelectionTopPadding + getPaddingTop();
        listPadding.right = this.mSelectionRightPadding + getPaddingRight();
        listPadding.bottom = this.mSelectionBottomPadding + getPaddingBottom();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        if (changed) {
            int childCount = getChildCount();
            for (int i = TRANSCRIPT_MODE_DISABLED; i < childCount; i += TRANSCRIPT_MODE_NORMAL) {
                getChildAt(i).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
        }
        layoutChildren();
        this.mInLayout = PROFILE_SCROLLING;
    }

    protected void layoutChildren() {
    }

    @ExportedProperty
    public View getSelectedView() {
        return null;
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
    }

    View obtainView(int position, boolean[] isScrap) {
        View child;
        isScrap[TRANSCRIPT_MODE_DISABLED] = PROFILE_SCROLLING;
        View scrapView = this.mRecycler.getScrapView(position);
        if (scrapView != null) {
            child = this.mAdapter.getView(position, scrapView, this);
            if (child != scrapView) {
                this.mRecycler.addScrapView(scrapView);
                if (this.mCacheColorHint != 0) {
                    child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
                }
            } else {
                isScrap[TRANSCRIPT_MODE_DISABLED] = true;
                dispatchFinishTemporaryDetach(child);
            }
        } else {
            child = this.mAdapter.getView(position, null, this);
            if (this.mCacheColorHint != 0) {
                child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
            }
        }
        return child;
    }

    void positionSelector(View sel) {
        Rect selectorRect = this.mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right, selectorRect.bottom);
        boolean isChildViewEnabled = this.mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            this.mIsChildViewEnabled = isChildViewEnabled ? PROFILE_SCROLLING : true;
            refreshDrawableState();
        }
    }

    private void positionSelector(int l, int t, int r, int b) {
        this.mSelectorRect.set(l - this.mSelectionLeftPadding, t - this.mSelectionTopPadding, this.mSelectionRightPadding + r, this.mSelectionBottomPadding + b);
    }

    protected void dispatchDraw(Canvas canvas) {
        boolean drawSelectorOnTop = this.mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }
        super.dispatchDraw(canvas);
        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getChildCount() > 0) {
            this.mDataChanged = true;
            rememberSyncState();
        }
    }

    boolean touchModeDrawsInPressedState() {
        switch (this.mTouchMode) {
            case TRANSCRIPT_MODE_NORMAL /*1*/:
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                return true;
            default:
                return PROFILE_SCROLLING;
        }
    }

    protected boolean shouldShowSelector() {
        return ((!hasFocus() || isInTouchMode()) && !touchModeDrawsInPressedState()) ? PROFILE_SCROLLING : true;
    }

    private void drawSelector(Canvas canvas) {
        if (shouldShowSelector() && this.mSelectorRect != null && !this.mSelectorRect.isEmpty()) {
            Drawable selector = this.mSelector;
            selector.setBounds(this.mSelectorRect);
            selector.draw(canvas);
        }
    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (this.mSelector != null) {
            this.mSelector.setCallback(null);
            unscheduleDrawable(this.mSelector);
        }
        this.mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        this.mSelectionLeftPadding = padding.left;
        this.mSelectionTopPadding = padding.top;
        this.mSelectionRightPadding = padding.right;
        this.mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        sel.setState(getDrawableState());
    }

    public Drawable getSelector() {
        return this.mSelector;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mSelector != null) {
            this.mSelector.setState(getDrawableState());
        }
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        if (this.mIsChildViewEnabled) {
            return super.onCreateDrawableState(extraSpace);
        }
        int enabledState = ENABLED_STATE_SET[TRANSCRIPT_MODE_DISABLED];
        int[] state = super.onCreateDrawableState(extraSpace + TRANSCRIPT_MODE_NORMAL);
        int enabledPos = TOUCH_MODE_UNKNOWN;
        for (int i = state.length + TOUCH_MODE_UNKNOWN; i >= 0; i += TOUCH_MODE_UNKNOWN) {
            if (state[i] == enabledState) {
                enabledPos = i;
                break;
            }
        }
        if (enabledPos < 0) {
            return state;
        }
        System.arraycopy(state, enabledPos + TRANSCRIPT_MODE_NORMAL, state, enabledPos, (state.length - enabledPos) + TOUCH_MODE_UNKNOWN);
        return state;
    }

    public boolean verifyDrawable(Drawable dr) {
        return (this.mSelector == dr || super.verifyDrawable(dr)) ? true : PROFILE_SCROLLING;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.addOnTouchModeChangeListener(this);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRecycler.clear();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.removeOnTouchModeChangeListener(this);
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        int touchMode = isInTouchMode() ? TRANSCRIPT_MODE_DISABLED : TRANSCRIPT_MODE_NORMAL;
        if (!hasWindowFocus) {
            setChildrenDrawingCacheEnabled(PROFILE_SCROLLING);
            if (this.mFlingRunnable != null) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlingRunnable.endFling();
                if (getScrollY() != 0) {
                    scrollTo(getScrollX(), TRANSCRIPT_MODE_DISABLED);
                    invalidate();
                }
            }
        } else if (!(touchMode == this.mLastTouchMode || this.mLastTouchMode == TOUCH_MODE_UNKNOWN)) {
            this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
            layoutChildren();
        }
        this.mLastTouchMode = touchMode;
    }

    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return PROFILE_SCROLLING;
        }
        long longPressId = this.mAdapter.getItemId(longPressPosition);
        boolean handled = PROFILE_SCROLLING;
        if (this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, originalView, longPressPosition, longPressId);
        }
        if (handled) {
            return handled;
        }
        this.mContextMenuInfo = createContextMenuInfo(getChildAt(longPressPosition - this.mFirstPosition), longPressPosition, longPressId);
        return super.showContextMenuForChild(originalView);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return PROFILE_SCROLLING;
    }

    protected void dispatchSetPressed(boolean pressed) {
    }

    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if (frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }
        for (int i = getChildCount() + TOUCH_MODE_UNKNOWN; i >= 0; i += TOUCH_MODE_UNKNOWN) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return TOUCH_MODE_UNKNOWN;
    }

    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return this.mAdapter.getItemId(position);
        }
        return Long.MIN_VALUE;
    }

    private boolean startScrollIfNeeded(int deltaY) {
        if (Math.abs(deltaY) <= this.mTouchSlop) {
            return PROFILE_SCROLLING;
        }
        createScrollingCache();
        this.mTouchMode = TOUCH_MODE_SCROLL;
        this.mMotionCorrection = deltaY;
        setPressed(PROFILE_SCROLLING);
        View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (motionView != null) {
            motionView.setPressed(PROFILE_SCROLLING);
        }
        reportScrollStateChange(TRANSCRIPT_MODE_NORMAL);
        requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public void onTouchModeChanged(boolean isInTouchMode) {
        if (isInTouchMode && getHeight() > 0 && getChildCount() > 0) {
            layoutChildren();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            int action = ev.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(ev);
            int x;
            int y;
            int motionPosition;
            int i;
            switch (action & MotionEventCompat.ACTION_MASK) {
                case TRANSCRIPT_MODE_DISABLED /*0*/:
                    this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
                    x = (int) ev.getX();
                    y = (int) ev.getY();
                    motionPosition = pointToPosition(x, y);
                    if (!this.mDataChanged) {
                        i = this.mTouchMode;
                        if (mTouchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
                            if (((ListAdapter) getAdapter()).isEnabled(motionPosition)) {
                                this.mTouchMode = TRANSCRIPT_MODE_DISABLED;
                                if (this.mPendingCheckForTap == null) {
                                    this.mPendingCheckForTap = new CheckForTap();
                                }
                                postDelayed(this.mPendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
                            }
                        }
                        if (ev.getEdgeFlags() != 0 && motionPosition < 0) {
                            return PROFILE_SCROLLING;
                        }
                        i = this.mTouchMode;
                        if (mTouchMode == TOUCH_MODE_FLING) {
                            createScrollingCache();
                            this.mTouchMode = TOUCH_MODE_SCROLL;
                            this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
                            motionPosition = findMotionRow(y);
                            reportScrollStateChange(TRANSCRIPT_MODE_NORMAL);
                        }
                    }
                    if (motionPosition >= 0) {
                        this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                    }
                    this.mMotionX = x;
                    this.mMotionY = y;
                    this.mMotionPosition = motionPosition;
                    this.mLastY = ExploreByTouchHelper.INVALID_ID;
                    break;
                case TRANSCRIPT_MODE_NORMAL /*1*/:
                    switch (this.mTouchMode) {
                        case TRANSCRIPT_MODE_DISABLED /*0*/:
                        case TRANSCRIPT_MODE_NORMAL /*1*/:
                        case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                            motionPosition = this.mMotionPosition;
                            View child = getChildAt(motionPosition - this.mFirstPosition);
                            if (!(child == null || child.hasFocusable())) {
                                if (this.mTouchMode != 0) {
                                    child.setPressed(PROFILE_SCROLLING);
                                }
                                if (this.mPerformClick == null) {
                                    this.mPerformClick = new PerformClick();
                                }
                                PerformClick performClick = this.mPerformClick;
                                performClick.mChild = child;
                                performClick.mClickMotionPosition = motionPosition;
                                performClick.rememberWindowAttachCount();
                                this.mResurrectToPosition = motionPosition;
                                if (this.mTouchMode != 0) {
                                    i = this.mTouchMode;
                                    if (mTouchMode != TRANSCRIPT_MODE_NORMAL) {
                                        if (!this.mDataChanged) {
                                            if (this.mAdapter.isEnabled(motionPosition)) {
                                                post(performClick);
                                            }
                                        }
                                    }
                                }
                                this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
                                if (!this.mDataChanged) {
                                    if (this.mAdapter.isEnabled(motionPosition)) {
                                        this.mTouchMode = TRANSCRIPT_MODE_NORMAL;
                                        layoutChildren();
                                        child.setPressed(true);
                                        positionSelector(child);
                                        setPressed(true);
                                        if (this.mSelector != null) {
                                            Drawable d = this.mSelector.getCurrent();
                                            if (d != null && (d instanceof TransitionDrawable)) {
                                                ((TransitionDrawable) d).resetTransition();
                                            }
                                        }
                                        postDelayed(new C01721(child, performClick), (long) ViewConfiguration.getPressedStateDuration());
                                        return true;
                                    }
                                }
                                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                                return true;
                            }
                            this.mTouchMode = TOUCH_MODE_UNKNOWN;
                            break;
                        case TOUCH_MODE_SCROLL /*3*/:
                            int childCount = getChildCount();
                            if (childCount <= 0) {
                                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                                reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
                                break;
                            }
                            int top = getFillChildTop();
                            int bottom = getFillChildBottom();
                            if (this.mFirstPosition == 0 && top >= this.mListPadding.top && this.mFirstPosition + childCount < this.mItemCount) {
                                if (bottom <= getHeight() - this.mListPadding.bottom) {
                                    this.mTouchMode = TOUCH_MODE_UNKNOWN;
                                    reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
                                    break;
                                }
                            }
                            VelocityTracker velocityTracker = this.mVelocityTracker;
                            velocityTracker.computeCurrentVelocity(CloseFrame.NORMAL, (float) this.mMaximumVelocity);
                            int initialVelocity = (int) velocityTracker.getYVelocity(this.mActivePointerId);
                            if (Math.abs(initialVelocity) <= this.mMinimumVelocity) {
                                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                                reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
                                break;
                            }
                            if (this.mFlingRunnable == null) {
                                this.mFlingRunnable = new FlingRunnable();
                            }
                            reportScrollStateChange(TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            this.mFlingRunnable.start(-initialVelocity);
                            break;
                    }
                    setPressed(PROFILE_SCROLLING);
                    invalidate();
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mActivePointerId = TOUCH_MODE_UNKNOWN;
                    break;
                case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                    y = (int) ev.getY(ev.findPointerIndex(this.mActivePointerId));
                    int deltaY = y - this.mMotionY;
                    switch (this.mTouchMode) {
                        case TRANSCRIPT_MODE_DISABLED /*0*/:
                        case TRANSCRIPT_MODE_NORMAL /*1*/:
                        case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                            startScrollIfNeeded(deltaY);
                            break;
                        case TOUCH_MODE_SCROLL /*3*/:
                            if (y != this.mLastY) {
                                int incrementalDeltaY;
                                deltaY -= this.mMotionCorrection;
                                i = this.mLastY;
                                if (mTouchMode != Integer.MIN_VALUE) {
                                    incrementalDeltaY = y - this.mLastY;
                                } else {
                                    incrementalDeltaY = deltaY;
                                }
                                boolean atEdge = PROFILE_SCROLLING;
                                if (incrementalDeltaY != 0) {
                                    atEdge = trackMotionScroll(deltaY, incrementalDeltaY);
                                }
                                if (atEdge && getChildCount() > 0) {
                                    motionPosition = findMotionRow(y);
                                    if (motionPosition >= 0) {
                                        this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                                    }
                                    this.mMotionY = y;
                                    this.mMotionPosition = motionPosition;
                                    invalidate();
                                }
                                this.mLastY = y;
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                case TOUCH_MODE_SCROLL /*3*/:
                    this.mTouchMode = TOUCH_MODE_UNKNOWN;
                    setPressed(PROFILE_SCROLLING);
                    View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                    if (motionView != null) {
                        motionView.setPressed(PROFILE_SCROLLING);
                    }
                    clearScrollingCache();
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mActivePointerId = TOUCH_MODE_UNKNOWN;
                    break;
                case LAYOUT_MOVE_SELECTION /*6*/:
                    onSecondaryPointerUp(ev);
                    x = this.mMotionX;
                    y = this.mMotionY;
                    motionPosition = pointToPosition(x, y);
                    if (motionPosition >= 0) {
                        this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                        this.mMotionPosition = motionPosition;
                    }
                    this.mLastY = y;
                    break;
            }
            return true;
        } else if (isClickable() || isLongClickable()) {
            return true;
        } else {
            return PROFILE_SCROLLING;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case TRANSCRIPT_MODE_DISABLED /*0*/:
                int touchMode = this.mTouchMode;
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
                int motionPosition = findMotionRow(y);
                if (touchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
                    this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                    this.mMotionX = x;
                    this.mMotionY = y;
                    this.mMotionPosition = motionPosition;
                    this.mTouchMode = TRANSCRIPT_MODE_DISABLED;
                    clearScrollingCache();
                }
                this.mLastY = ExploreByTouchHelper.INVALID_ID;
                if (touchMode == TOUCH_MODE_FLING) {
                    return true;
                }
                break;
            case TRANSCRIPT_MODE_NORMAL /*1*/:
                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                this.mActivePointerId = TOUCH_MODE_UNKNOWN;
                reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
                break;
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                switch (this.mTouchMode) {
                    case TRANSCRIPT_MODE_DISABLED /*0*/:
                        if (startScrollIfNeeded(((int) ev.getY(ev.findPointerIndex(this.mActivePointerId))) - this.mMotionY)) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            case LAYOUT_MOVE_SELECTION /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        return PROFILE_SCROLLING;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? TRANSCRIPT_MODE_NORMAL : TRANSCRIPT_MODE_DISABLED;
            this.mMotionX = (int) ev.getX(newPointerIndex);
            this.mMotionY = (int) ev.getY(newPointerIndex);
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    public void addTouchables(ArrayList<View> views) {
        int count = getChildCount();
        int firstPosition = this.mFirstPosition;
        ListAdapter adapter = this.mAdapter;
        if (adapter != null) {
            for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                View child = getChildAt(i);
                if (adapter.isEnabled(firstPosition + i)) {
                    views.add(child);
                }
                child.addTouchables(views);
            }
        }
    }

    void reportScrollStateChange(int newState) {
        if (newState != this.mLastScrollState && this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(this, newState);
            this.mLastScrollState = newState;
        }
    }

    public void smoothScrollToPosition(int position) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.start(position);
    }

    public void smoothScrollToPosition(int position, int boundPosition) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.start(position, boundPosition);
    }

    public void smoothScrollBy(int distance, int duration) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        } else {
            this.mFlingRunnable.endFling();
        }
        this.mFlingRunnable.startScroll(distance, duration);
    }

    private void createScrollingCache() {
        if (this.mScrollingCacheEnabled && !this.mCachingStarted) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            this.mCachingStarted = true;
        }
    }

    private void clearScrollingCache() {
        if (this.mClearScrollingCache == null) {
            this.mClearScrollingCache = new C01732();
        }
        post(this.mClearScrollingCache);
    }

    boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        int firstTop = getScrollChildTop();
        int lastBottom = getScrollChildBottom();
        Rect listPadding = this.mListPadding;
        int end = getHeight() - listPadding.bottom;
        int spaceAbove = listPadding.top - getFillChildTop();
        int spaceBelow = getFillChildBottom() - end;
        int height = (getHeight() - getPaddingBottom()) - getPaddingTop();
        if (deltaY < 0) {
            deltaY = Math.max(-(height + TOUCH_MODE_UNKNOWN), deltaY);
        } else {
            deltaY = Math.min(height + TOUCH_MODE_UNKNOWN, deltaY);
        }
        if (incrementalDeltaY < 0) {
            incrementalDeltaY = Math.max(-(height + TOUCH_MODE_UNKNOWN), incrementalDeltaY);
        } else {
            incrementalDeltaY = Math.min(height + TOUCH_MODE_UNKNOWN, incrementalDeltaY);
        }
        int firstPosition = this.mFirstPosition;
        if (firstPosition == 0) {
            int i = listPadding.top;
            if (firstTop >= mTouchMode && deltaY >= 0) {
                return true;
            }
        }
        if (firstPosition + childCount == this.mItemCount && lastBottom <= end && deltaY <= 0) {
            return true;
        }
        boolean down = incrementalDeltaY < 0 ? true : PROFILE_SCROLLING;
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsStart = this.mItemCount - getFooterViewsCount();
        int start = TRANSCRIPT_MODE_DISABLED;
        int count = TRANSCRIPT_MODE_DISABLED;
        int i2;
        View child;
        int position;
        if (!down) {
            int bottom = (getHeight() - listPadding.bottom) - incrementalDeltaY;
            for (i2 = childCount + TOUCH_MODE_UNKNOWN; i2 >= 0; i2 += TOUCH_MODE_UNKNOWN) {
                child = getChildAt(i2);
                if (child.getTop() <= bottom) {
                    break;
                }
                start = i2;
                count += TRANSCRIPT_MODE_NORMAL;
                position = firstPosition + i2;
                if (position >= headerViewsCount && position < footerViewsStart) {
                    this.mRecycler.addScrapView(child);
                }
            }
        } else {
            int top = listPadding.top - incrementalDeltaY;
            for (i2 = TRANSCRIPT_MODE_DISABLED; i2 < childCount; i2 += TRANSCRIPT_MODE_NORMAL) {
                child = getChildAt(i2);
                if (child.getBottom() >= top) {
                    break;
                }
                count += TRANSCRIPT_MODE_NORMAL;
                position = firstPosition + i2;
                if (position >= headerViewsCount && position < footerViewsStart) {
                    this.mRecycler.addScrapView(child);
                }
            }
        }
        this.mMotionViewNewTop = this.mMotionViewOriginalTop + deltaY;
        this.mBlockLayoutRequests = true;
        if (count > 0) {
            detachViewsFromParent(start, count);
        }
        tryOffsetChildrenTopAndBottom(incrementalDeltaY);
        if (down) {
            this.mFirstPosition += count;
        }
        invalidate();
        int absIncrementalDeltaY = Math.abs(incrementalDeltaY);
        if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
            fillGap(down);
        }
        this.mBlockLayoutRequests = PROFILE_SCROLLING;
        invokeOnItemScrollListener();
        awakenScrollBars();
        return PROFILE_SCROLLING;
    }

    protected void tryOffsetChildrenTopAndBottom(int offset) {
        int count = getChildCount();
        for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

    int getHeaderViewsCount() {
        return TRANSCRIPT_MODE_DISABLED;
    }

    int getFooterViewsCount() {
        return TRANSCRIPT_MODE_DISABLED;
    }

    int findClosestMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return TOUCH_MODE_UNKNOWN;
        }
        int motionRow = findMotionRow(y);
        return motionRow == TOUCH_MODE_UNKNOWN ? (this.mFirstPosition + childCount) + TOUCH_MODE_UNKNOWN : motionRow;
    }

    public void invalidateViews() {
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }

    protected void handleDataChanged() {
        int i = TOUCH_MODE_SCROLL;
        int count = this.mItemCount;
        if (count > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = PROFILE_SCROLLING;
                if (this.mTranscriptMode == TRANSCRIPT_MODE_ALWAYS_SCROLL || (this.mTranscriptMode == TRANSCRIPT_MODE_NORMAL && this.mFirstPosition + getChildCount() >= this.mOldItemCount)) {
                    this.mLayoutMode = TOUCH_MODE_SCROLL;
                    return;
                }
                switch (this.mSyncMode) {
                    case TRANSCRIPT_MODE_NORMAL /*1*/:
                        this.mLayoutMode = LAYOUT_SYNC;
                        this.mSyncPosition = Math.min(Math.max(TRANSCRIPT_MODE_DISABLED, this.mSyncPosition), count + TOUCH_MODE_UNKNOWN);
                        return;
                }
            }
            if (!isInTouchMode()) {
                int newPos = getSelectedItemPosition();
                if (newPos >= count) {
                    newPos = count + TOUCH_MODE_UNKNOWN;
                }
                if (newPos < 0) {
                    newPos = TRANSCRIPT_MODE_DISABLED;
                }
                int lookForSelectablePosition = lookForSelectablePosition(newPos, true);
                if (lookForSelectablePosition(newPos, PROFILE_SCROLLING) >= 0) {
                    return;
                }
            } else if (this.mResurrectToPosition >= 0) {
                return;
            }
        }
        if (!this.mStackFromBottom) {
            i = TRANSCRIPT_MODE_NORMAL;
        }
        this.mLayoutMode = i;
        this.mNeedSync = PROFILE_SCROLLING;
    }

    protected void onLayoutSync(int syncPosition) {
    }

    protected void onLayoutSyncFinished(int syncPosition) {
    }

    static int getDistance(Rect source, Rect dest, int direction) {
        int sX;
        int sY;
        int dX;
        int dY;
        switch (direction) {
            case 17 /*17*/:
                sX = source.left;
                sY = source.top + (source.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dX = dest.right;
                dY = dest.top + (dest.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
            case 33 /*33*/:
                sX = source.left + (source.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                sY = source.top;
                dX = dest.left + (dest.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dY = dest.bottom;
                break;
            case 66 /*66*/:
                sX = source.right;
                sY = source.top + (source.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dX = dest.left;
                dY = dest.top + (dest.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
            case 130 /*130*/:
                sX = source.left + (source.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                sY = source.bottom;
                dX = dest.left + (dest.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dY = dest.top;
                break;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return (deltaY * deltaY) + (deltaX * deltaX);
    }

    public void onGlobalLayout() {
    }

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setTranscriptMode(int mode) {
        this.mTranscriptMode = mode;
    }

    public int getTranscriptMode() {
        return this.mTranscriptMode;
    }

    public int getSolidColor() {
        return this.mCacheColorHint;
    }

    public void setCacheColorHint(int color) {
        if (color != this.mCacheColorHint) {
            this.mCacheColorHint = color;
            int count = getChildCount();
            for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                getChildAt(i).setDrawingCacheBackgroundColor(color);
            }
            this.mRecycler.setCacheColorHint(color);
        }
    }

    public int getCacheColorHint() {
        return this.mCacheColorHint;
    }

    public void reclaimViews(List<View> views) {
        int childCount = getChildCount();
        RecyclerListener listener = this.mRecycler.mRecyclerListener;
        for (int i = TRANSCRIPT_MODE_DISABLED; i < childCount; i += TRANSCRIPT_MODE_NORMAL) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp != null && this.mRecycler.shouldRecycleViewType(lp.viewType)) {
                views.add(child);
                if (listener != null) {
                    listener.onMovedToScrapHeap(child);
                }
            }
        }
        this.mRecycler.reclaimScrapViews(views);
        removeAllViewsInLayout();
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecycler.mRecyclerListener = listener;
    }

    private void dispatchFinishTemporaryDetach(View v) {
        if (v != null) {
            v.onFinishTemporaryDetach();
            if (v instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) v;
                int count = group.getChildCount();
                for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                    dispatchFinishTemporaryDetach(group.getChildAt(i));
                }
            }
        }
    }

    protected int modifyFlingInitialVelocity(int initialVelocity) {
        return initialVelocity;
    }

    protected int getScrollChildTop() {
        if (getChildCount() == 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        return getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
    }

    protected int getFirstChildTop() {
        if (getChildCount() == 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        return getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
    }

    protected int getFillChildTop() {
        if (getChildCount() == 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        return getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
    }

    protected int getFillChildBottom() {
        int count = getChildCount();
        if (count == 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        return getChildAt(count + TOUCH_MODE_UNKNOWN).getBottom();
    }

    protected int getScrollChildBottom() {
        int count = getChildCount();
        if (count == 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        return getChildAt(count + TOUCH_MODE_UNKNOWN).getBottom();
    }
}
