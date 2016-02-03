package com.mlizhi.widgets.waterfall.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListAdapter;

import com.philips.skincare.skincareprototype.R;

import java.util.ArrayList;

public class PLA_ListView extends PLA_AbsListView {
    private static final float MAX_SCROLL_FACTOR = 0.33f;
    static final int NO_POSITION = -1;
    private boolean mAreAllItemsSelectable;
    private boolean mClipDivider;
    Drawable mDivider;
    int mDividerHeight;
    private boolean mDividerIsOpaque;
    private Paint mDividerPaint;
    private boolean mFooterDividersEnabled;
    private ArrayList<FixedViewInfo> mFooterViewInfos;
    private boolean mHeaderDividersEnabled;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private boolean mIsCacheColorOpaque;
    private boolean mItemsCanFocus;
    Drawable mOverScrollFooter;
    Drawable mOverScrollHeader;
    private final Rect mTempRect;

    public class FixedViewInfo {
        public Object data;
        public boolean isSelectable;
        public View view;
    }

    public PLA_ListView(Context context) {
        this(context, null);
    }

    public PLA_ListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listViewStyle);
    }

    public PLA_ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHeaderViewInfos = new ArrayList();
        this.mFooterViewInfos = new ArrayList();
        this.mAreAllItemsSelectable = true;
        this.mItemsCanFocus = false;
        this.mTempRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.ListView, defStyle, 0);
        Drawable osHeader = a.getDrawable(3);
        if (osHeader != null) {
            setOverscrollHeader(osHeader);
        }
        Drawable osFooter = a.getDrawable(4);
        if (osFooter != null) {
            setOverscrollFooter(osFooter);
        }
        int dividerHeight = a.getDimensionPixelSize(0, 0);
        if (dividerHeight != 0) {
            setDividerHeight(dividerHeight);
        }
        this.mHeaderDividersEnabled = a.getBoolean(1, true);
        this.mFooterDividersEnabled = a.getBoolean(2, true);
        a.recycle();
    }

    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * ((float) (getBottom() - getTop())));
    }

    private void adjustViewsUpOrDown() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int delta;
            if (this.mStackFromBottom) {
                delta = getScrollChildBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mDividerHeight;
                }
                if (delta > 0) {
                    delta = 0;
                }
            } else {
                delta = getScrollChildTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    delta -= this.mDividerHeight;
                }
                if (delta < 0) {
                    delta = 0;
                }
            }
            if (delta != 0) {
                tryOffsetChildrenTopAndBottom(-delta);
            }
        }
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (this.mAdapter != null) {
            throw new IllegalStateException("Cannot add header view to list -- setAdapter has already been called.");
        }
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mHeaderViewInfos.add(info);
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewsCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean isFixedView(View v) {
        int i;
        ArrayList<FixedViewInfo> where = this.mHeaderViewInfos;
        int len = where.size();
        for (i = 0; i < len; i++) {
            if (((FixedViewInfo) where.get(i)).view == v) {
                return true;
            }
        }
        where = this.mFooterViewInfos;
        len = where.size();
        for (i = 0; i < len; i++) {
            if (((FixedViewInfo) where.get(i)).view == v) {
                return true;
            }
        }
        return false;
    }

    public boolean removeHeaderView(View v) {
        if (this.mHeaderViewInfos.size() <= 0) {
            return false;
        }
        boolean result = false;
        if (((PLA_HeaderViewListAdapter) this.mAdapter).removeHeader(v)) {
            this.mDataSetObserver.onChanged();
            result = true;
        }
        removeFixedViewInfo(v, this.mHeaderViewInfos);
        return result;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; i++) {
            if (((FixedViewInfo) where.get(i)).view == v) {
                where.remove(i);
                return;
            }
        }
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mFooterViewInfos.add(info);
        if (this.mDataSetObserver != null) {
            this.mDataSetObserver.onChanged();
        }
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public int getFooterViewsCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeFooterView(View v) {
        if (this.mFooterViewInfos.size() <= 0) {
            return false;
        }
        boolean result = false;
        if (((PLA_HeaderViewListAdapter) this.mAdapter).removeFooter(v)) {
            this.mDataSetObserver.onChanged();
            result = true;
        }
        removeFixedViewInfo(v, this.mFooterViewInfos);
        return result;
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        resetList();
        this.mRecycler.clear();
        if (this.mHeaderViewInfos.size() > 0 || this.mFooterViewInfos.size() > 0) {
            this.mAdapter = new PLA_HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, adapter);
        } else {
            this.mAdapter = adapter;
        }
        this.mOldSelectedPosition = NO_POSITION;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        if (this.mAdapter != null) {
            this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            checkFocus();
            this.mDataSetObserver = new AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
        } else {
            this.mAreAllItemsSelectable = true;
            checkFocus();
        }
        requestLayout();
    }

    public int getFirstVisiblePosition() {
        return Math.max(0, this.mFirstPosition - getHeaderViewsCount());
    }

    public int getLastVisiblePosition() {
        return Math.min((this.mFirstPosition + getChildCount()) + NO_POSITION, this.mAdapter.getCount() + NO_POSITION);
    }

    void resetList() {
        clearRecycledState(this.mHeaderViewInfos);
        clearRecycledState(this.mFooterViewInfos);
        super.resetList();
        this.mLayoutMode = 0;
    }

    private void clearRecycledState(ArrayList<FixedViewInfo> infos) {
        if (infos != null) {
            int count = infos.size();
            for (int i = 0; i < count; i++) {
                LayoutParams p = (LayoutParams) ((FixedViewInfo) infos.get(i)).view.getLayoutParams();
                if (p != null) {
                    p.recycledHeaderFooter = false;
                }
            }
        }
    }

    private boolean showingTopFadingEdge() {
        int listTop = getScrollY() + this.mListPadding.top;
        if (this.mFirstPosition > 0 || getChildAt(0).getTop() > listTop) {
            return true;
        }
        return false;
    }

    private boolean showingBottomFadingEdge() {
        int childCount = getChildCount();
        return (this.mFirstPosition + childCount) + NO_POSITION < this.mItemCount + NO_POSITION || getChildAt(childCount + NO_POSITION).getBottom() < (getScrollY() + getHeight()) - this.mListPadding.bottom;
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        int rectTopWithinChild = rect.top;
        rect.offset(child.getLeft(), child.getTop());
        rect.offset(-child.getScrollX(), -child.getScrollY());
        int height = getHeight();
        int listUnfadedTop = getScrollY();
        int listUnfadedBottom = listUnfadedTop + height;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (showingTopFadingEdge() && rectTopWithinChild > fadingEdge) {
            listUnfadedTop += fadingEdge;
        }
        int bottomOfBottomChild = getChildAt(getChildCount() + NO_POSITION).getBottom();
        if (showingBottomFadingEdge() && rect.bottom < bottomOfBottomChild - fadingEdge) {
            listUnfadedBottom -= fadingEdge;
        }
        int scrollYDelta = 0;
        if (rect.bottom > listUnfadedBottom && rect.top > listUnfadedTop) {
            if (rect.height() > height) {
                scrollYDelta = 0 + (rect.top - listUnfadedTop);
            } else {
                scrollYDelta = 0 + (rect.bottom - listUnfadedBottom);
            }
            scrollYDelta = Math.min(scrollYDelta, bottomOfBottomChild - listUnfadedBottom);
        } else if (rect.top < listUnfadedTop && rect.bottom < listUnfadedBottom) {
            if (rect.height() > height) {
                scrollYDelta = 0 - (listUnfadedBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (listUnfadedTop - rect.top);
            }
            scrollYDelta = Math.max(scrollYDelta, getChildAt(0).getTop() - listUnfadedTop);
        }
        boolean scroll = scrollYDelta != 0;
        if (scroll) {
            scrollListItemsBy(-scrollYDelta);
            positionSelector(child);
            this.mSelectedTop = child.getTop();
            invalidate();
        }
        return scroll;
    }

    protected int getItemLeft(int pos) {
        return this.mListPadding.left;
    }

    protected int getItemTop(int pos) {
        int count = getChildCount();
        return count > 0 ? getChildAt(count + NO_POSITION).getBottom() + this.mDividerHeight : getListPaddingTop();
    }

    protected int getItemBottom(int pos) {
        return getChildCount() > 0 ? getChildAt(0).getTop() - this.mDividerHeight : getHeight() - getListPaddingBottom();
    }

    protected void fillGap(boolean down) {
        int count = getChildCount();
        if (down) {
            fillDown(this.mFirstPosition + count, getItemTop(this.mFirstPosition + count));
            onAdjustChildViews(down);
            return;
        }
        fillUp(this.mFirstPosition + NO_POSITION, getItemBottom(this.mFirstPosition + NO_POSITION));
        onAdjustChildViews(down);
    }

    private View fillDown(int pos, int top) {
        int end = (getBottom() - getTop()) - this.mListPadding.bottom;
        int childTop = getFillChildBottom() + this.mDividerHeight;
        while (childTop < end && pos < this.mItemCount) {
            makeAndAddView(pos, getItemTop(pos), true, false);
            pos++;
            childTop = getFillChildBottom() + this.mDividerHeight;
        }
        return null;
    }

    private View fillUp(int pos, int bottom) {
        int end = this.mListPadding.top;
        int childBottom = getFillChildTop();
        while (childBottom > end && pos >= 0) {
            makeAndAddView(pos, getItemBottom(pos), false, false);
            pos += NO_POSITION;
            childBottom = getItemBottom(pos);
        }
        this.mFirstPosition = pos + 1;
        return null;
    }

    private View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, NO_POSITION);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount + NO_POSITION);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        return fillDown(this.mFirstPosition, nextTop);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = 0;
        int childHeight = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (this.mItemCount > 0 && (widthMode == 0 || heightMode == 0)) {
            View child = obtainView(0, this.mIsScrap);
            measureScrapChild(child, 0, widthMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            if (recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(child);
            }
        }
        if (widthMode == 0) {
            widthSize = ((this.mListPadding.left + this.mListPadding.right) + childWidth) + getVerticalScrollbarWidth();
        }
        if (heightMode == 0) {
            heightSize = ((this.mListPadding.top + this.mListPadding.bottom) + childHeight) + (getVerticalFadingEdgeLength() * 2);
        }
        if (heightMode == ExploreByTouchHelper.INVALID_ID) {
            heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, NO_POSITION);
        }
        setMeasuredDimension(widthSize, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        int childHeightSpec;
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = new LayoutParams(NO_POSITION, -2, 0);
            child.setLayoutParams(p);
        }
        p.viewType = this.mAdapter.getItemViewType(position);
        p.forceAdd = true;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY );
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED );
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @ExportedProperty(category = "list")
    protected boolean recycleOnMeasure() {
        return true;
    }

    final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition, int maxHeight, int disallowPartialChildPosition) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return this.mListPadding.top + this.mListPadding.bottom;
        }
        int returnedHeight = this.mListPadding.top + this.mListPadding.bottom;
        int dividerHeight = (this.mDividerHeight <= 0 || this.mDivider == null) ? 0 : this.mDividerHeight;
        int prevHeightWithoutPartialChild = 0;
        if (endPosition == NO_POSITION) {
            endPosition = adapter.getCount() + NO_POSITION;
        }
        RecycleBin recycleBin = this.mRecycler;
        boolean recyle = recycleOnMeasure();
        boolean[] isScrap = this.mIsScrap;
        int i = startPosition;
        while (i <= endPosition) {
            View child = obtainView(i, isScrap);
            measureScrapChild(child, i, widthMeasureSpec);
            if (i > 0) {
                returnedHeight += dividerHeight;
            }
            if (recyle && recycleBin.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(child);
            }
            returnedHeight += child.getMeasuredHeight();
            if (returnedHeight < maxHeight) {
                if (disallowPartialChildPosition >= 0 && i >= disallowPartialChildPosition) {
                    prevHeightWithoutPartialChild = returnedHeight;
                }
                i++;
            } else if (disallowPartialChildPosition < 0 || i <= disallowPartialChildPosition || prevHeightWithoutPartialChild <= 0 || returnedHeight == maxHeight) {
                return maxHeight;
            } else {
                return prevHeightWithoutPartialChild;
            }
        }
        return returnedHeight;
    }

    int findMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i;
            if (this.mStackFromBottom) {
                for (i = childCount + NO_POSITION; i >= 0; i += NO_POSITION) {
                    if (y >= getChildAt(i).getTop()) {
                        return this.mFirstPosition + i;
                    }
                }
            } else {
                for (i = 0; i < childCount; i++) {
                    if (y <= getChildAt(i).getBottom()) {
                        return this.mFirstPosition + i;
                    }
                }
            }
        }
        return NO_POSITION;
    }

    private View fillSpecific(int position, int top) {
        View temp = makeAndAddView(position, top, true, false);
        this.mFirstPosition = position;
        int dividerHeight = this.mDividerHeight;
        int childCount;
        if (this.mStackFromBottom) {
            fillDown(position + 1, temp.getBottom() + dividerHeight);
            adjustViewsUpOrDown();
            fillUp(position + NO_POSITION, temp.getTop() - dividerHeight);
            childCount = getChildCount();
            if (childCount > 0) {
                correctTooLow(childCount);
            }
        } else {
            fillUp(position + NO_POSITION, temp.getTop() - dividerHeight);
            adjustViewsUpOrDown();
            fillDown(position + 1, temp.getBottom() + dividerHeight);
            childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(childCount);
            }
        }
        return null;
    }

    private void correctTooHigh(int childCount) {
        if ((this.mFirstPosition + childCount) + NO_POSITION == this.mItemCount + NO_POSITION && childCount > 0) {
            int bottomOffset = ((getBottom() - getTop()) - this.mListPadding.bottom) - getScrollChildBottom();
            int firstTop = getScrollChildTop();
            if (bottomOffset <= 0) {
                return;
            }
            if (this.mFirstPosition > 0 || firstTop < this.mListPadding.top) {
                if (this.mFirstPosition == 0) {
                    bottomOffset = Math.min(bottomOffset, this.mListPadding.top - firstTop);
                }
                tryOffsetChildrenTopAndBottom(bottomOffset);
                if (this.mFirstPosition > 0) {
                    fillUp(this.mFirstPosition + NO_POSITION, getScrollChildTop() - this.mDividerHeight);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int childCount) {
        if (this.mFirstPosition == 0 && childCount > 0) {
            int end = (getBottom() - getTop()) - this.mListPadding.bottom;
            int topOffset = getScrollChildTop() - this.mListPadding.top;
            int lastBottom = getScrollChildBottom();
            int lastPosition = (this.mFirstPosition + childCount) + NO_POSITION;
            if (topOffset <= 0) {
                return;
            }
            if (lastPosition < this.mItemCount + NO_POSITION || lastBottom > end) {
                if (lastPosition == this.mItemCount + NO_POSITION) {
                    topOffset = Math.min(topOffset, lastBottom - end);
                }
                tryOffsetChildrenTopAndBottom(-topOffset);
                if (lastPosition < this.mItemCount + NO_POSITION) {
                    fillDown(lastPosition + 1, getFillChildTop() + this.mDividerHeight);
                    adjustViewsUpOrDown();
                }
            } else if (lastPosition == this.mItemCount + NO_POSITION) {
                adjustViewsUpOrDown();
            }
        }
    }

    protected void layoutChildren() {
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            this.mBlockLayoutRequests = true;
            try {
                super.layoutChildren();
                invalidate();
                if (this.mAdapter == null) {
                    resetList();
                    invokeOnItemScrollListener();
                    if (!blockLayoutRequests) {
                        this.mBlockLayoutRequests = false;
                        return;
                    }
                    return;
                }
                int childrenTop = this.mListPadding.top;
                int childrenBottom = (getBottom() - getTop()) - this.mListPadding.bottom;
                int childCount = getChildCount();
                View oldFirst = null;
                View focusLayoutRestoreView = null;
                switch (this.mLayoutMode) {
                    case 1 /*1*/:
                    case 3 /*3*/:
                    case 4 /*4*/:
                    case 5 /*5*/:
                        break;
                    default:
                        oldFirst = getChildAt(0);
                        break;
                }
                boolean dataChanged = this.mDataChanged;
                if (dataChanged) {
                    handleDataChanged();
                }
                if (this.mItemCount == 0) {
                    resetList();
                    invokeOnItemScrollListener();
                    if (!blockLayoutRequests) {
                        this.mBlockLayoutRequests = false;
                    }
                } else if (this.mItemCount != this.mAdapter.getCount()) {
                    throw new IllegalStateException("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. [in ListView(" + getId() + ", " + getClass() + ") with Adapter(" + this.mAdapter.getClass() + ")]");
                } else {
                    int firstPosition = this.mFirstPosition;
                    RecycleBin recycleBin = this.mRecycler;
                    if (dataChanged) {
                        for (int i = 0; i < childCount; i++) {
                            recycleBin.addScrapView(getChildAt(i));
                        }
                    } else {
                        recycleBin.fillActiveViews(childCount, firstPosition);
                    }
                    View focusedChild = getFocusedChild();
                    if (focusedChild != null) {
                        if (!dataChanged || isDirectChildHeaderOrFooter(focusedChild)) {
                            focusLayoutRestoreView = findFocus();
                            if (focusLayoutRestoreView != null) {
                                focusLayoutRestoreView.onStartTemporaryDetach();
                            }
                        }
                        requestFocus();
                    }
                    switch (this.mLayoutMode) {
                        case 1 /*1*/:
                            detachAllViewsFromParent();
                            this.mFirstPosition = 0;
                            fillFromTop(childrenTop);
                            adjustViewsUpOrDown();
                            break;
                        case 3 /*3*/:
                            detachAllViewsFromParent();
                            fillUp(this.mItemCount + NO_POSITION, childrenBottom);
                            adjustViewsUpOrDown();
                            break;
                        case 5 /*5*/:
                            onLayoutSync(this.mSyncPosition);
                            detachAllViewsFromParent();
                            fillSpecific(this.mSyncPosition, this.mSpecificTop);
                            onLayoutSyncFinished(this.mSyncPosition);
                            break;
                        default:
                            if (childCount != 0) {
                                if (this.mFirstPosition >= this.mItemCount) {
                                    onLayoutSync(0);
                                    detachAllViewsFromParent();
                                    fillSpecific(0, childrenTop);
                                    onLayoutSyncFinished(0);
                                    break;
                                }
                                onLayoutSync(this.mFirstPosition);
                                detachAllViewsFromParent();
                                int i2 = this.mFirstPosition;
                                if (oldFirst != null) {
                                    childrenTop = oldFirst.getTop();
                                }
                                fillSpecific(i2, childrenTop);
                                onLayoutSyncFinished(this.mFirstPosition);
                                break;
                            }
                            detachAllViewsFromParent();
                            if (!this.mStackFromBottom) {
                                fillFromTop(childrenTop);
                                break;
                            } else {
                                fillUp(this.mItemCount + NO_POSITION, childrenBottom);
                                break;
                            }
                    }
                    recycleBin.scrapActiveViews();
                    if (this.mTouchMode <= 0 || this.mTouchMode >= 3) {
                        this.mSelectedTop = 0;
                        this.mSelectorRect.setEmpty();
                    } else {
                        View child = getChildAt(this.mMotionPosition - this.mFirstPosition);
                        if (child != null) {
                            positionSelector(child);
                        }
                    }
                    if (hasFocus() && focusLayoutRestoreView != null) {
                        focusLayoutRestoreView.requestFocus();
                    }
                    if (!(focusLayoutRestoreView == null || focusLayoutRestoreView.getWindowToken() == null)) {
                        focusLayoutRestoreView.onFinishTemporaryDetach();
                    }
                    this.mLayoutMode = 0;
                    this.mDataChanged = false;
                    this.mNeedSync = false;
                    invokeOnItemScrollListener();
                    if (!blockLayoutRequests) {
                        this.mBlockLayoutRequests = false;
                    }
                }
            } catch (Throwable th) {
                if (!blockLayoutRequests) {
                    this.mBlockLayoutRequests = false;
                }
            }
        }
    }

    private boolean isDirectChildHeaderOrFooter(View child) {
        int i;
        ArrayList<FixedViewInfo> headers = this.mHeaderViewInfos;
        int numHeaders = headers.size();
        for (i = 0; i < numHeaders; i++) {
            if (child == ((FixedViewInfo) headers.get(i)).view) {
                return true;
            }
        }
        ArrayList<FixedViewInfo> footers = this.mFooterViewInfos;
        int numFooters = footers.size();
        for (i = 0; i < numFooters; i++) {
            if (child == ((FixedViewInfo) footers.get(i)).view) {
                return true;
            }
        }
        return false;
    }

    private View makeAndAddView(int position, int childrenBottomOrTop, boolean flow, boolean selected) {
        View child;
        if (!this.mDataChanged) {
            child = this.mRecycler.getActiveView(position);
            if (child != null) {
                setupChild(child, position, childrenBottomOrTop, flow, getItemLeft(position), selected, true);
                return child;
            }
        }
        onItemAddedToList(position, flow);
        int childrenLeft = getItemLeft(position);
        child = obtainView(position, this.mIsScrap);
        setupChild(child, position, childrenBottomOrTop, flow, childrenLeft, selected, this.mIsScrap[0]);
        return child;
    }

    protected void onItemAddedToList(int position, boolean flow) {
    }

    private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
        boolean isSelected = selected && shouldShowSelector();
        boolean updateChildSelected = isSelected ^ child.isSelected();
        int mode = this.mTouchMode;
        boolean isPressed = mode > 0 && mode < 3 && this.mMotionPosition == position;
        boolean updateChildPressed = isPressed ^ child.isPressed();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            LayoutParams layoutParams = new LayoutParams(NO_POSITION, -2, 0);
        }
        p.viewType = this.mAdapter.getItemViewType(position);
        if ((!recycled || p.forceAdd) && !(p.recycledHeaderFooter && p.viewType == -2)) {
            p.forceAdd = false;
            if (p.viewType == -2) {
                p.recycledHeaderFooter = true;
            }
            addViewInLayout(child, flowDown ? NO_POSITION : 0, p, true);
        } else {
            attachViewToParent(child, flowDown ? NO_POSITION : 0, p);
        }
        if (updateChildSelected) {
            child.setSelected(isSelected);
        }
        if (updateChildPressed) {
            child.setPressed(isPressed);
        }
        if (needToMeasure) {
            int childHeightSpec;
            int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
            int lpHeight = p.height;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            onMeasureChild(child, position, childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childTop = flowDown ? y : y - h;
        if (needToMeasure) {
            onLayoutChild(child, position, childrenLeft, childTop, childrenLeft + w, childTop + h);
        } else {
            onOffsetChild(child, position, childrenLeft - child.getLeft(), childTop - child.getTop());
        }
        if (this.mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }
    }

    protected void onOffsetChild(View child, int position, int offsetLeft, int offsetTop) {
        child.offsetLeftAndRight(offsetLeft);
        child.offsetTopAndBottom(offsetTop);
    }

    protected void onLayoutChild(View child, int position, int l, int t, int r, int b) {
        child.layout(l, t, r, b);
    }

    protected void onMeasureChild(View child, int position, int widthMeasureSpec, int heightMeasureSpec) {
        child.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onAdjustChildViews(boolean down) {
        if (down) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }
    }

    protected boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    public void setSelection(int position) {
    }

    int lookForSelectablePosition(int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode()) {
            return NO_POSITION;
        }
        int count = adapter.getCount();
        if (!this.mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count + NO_POSITION);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position += NO_POSITION;
                }
            }
            if (position < 0 || position >= count) {
                return NO_POSITION;
            }
            return position;
        } else if (position < 0 || position >= count) {
            return NO_POSITION;
        } else {
            return position;
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            int itemCount = 0;
            int currentItemIndex = getSelectedItemPosition();
            ListAdapter adapter = getAdapter();
            if (adapter != null) {
                int count = adapter.getCount();
                if (count < 15) {
                    for (int i = 0; i < count; i++) {
                        if (adapter.isEnabled(i)) {
                            itemCount++;
                        } else if (i <= currentItemIndex) {
                            currentItemIndex += NO_POSITION;
                        }
                    }
                } else {
                    itemCount = count;
                }
            }
            event.setItemCount(itemCount);
            event.setCurrentItemIndex(currentItemIndex);
        }
        return populated;
    }

    public boolean fullScroll(int direction) {
        boolean moved = false;
        if (direction == 33) {
            if (lookForSelectablePosition(0, true) >= 0) {
                this.mLayoutMode = 1;
                invokeOnItemScrollListener();
                moved = true;
            }
        } else if (direction == TransportMediator.KEYCODE_MEDIA_RECORD) {
            if (lookForSelectablePosition(this.mItemCount + NO_POSITION, true) >= 0) {
                this.mLayoutMode = 3;
                invokeOnItemScrollListener();
            }
            moved = true;
        }
        if (moved && !awakenScrollBars()) {
            awakenScrollBars();
            invalidate();
        }
        return moved;
    }

    private void scrollListItemsBy(int amount) {
        tryOffsetChildrenTopAndBottom(amount);
        int listBottom = getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        RecycleBin recycleBin = this.mRecycler;
        View last;
        View first;
        if (amount < 0) {
            last = getLastChild();
            int numChildren = getChildCount();
            while (last.getBottom() < listBottom) {
                int lastVisiblePosition = (this.mFirstPosition + numChildren) + NO_POSITION;
                if (lastVisiblePosition >= this.mItemCount + NO_POSITION) {
                    break;
                }
                addViewBelow(last, lastVisiblePosition);
                last = getLastChild();
                numChildren++;
            }
            if (last.getBottom() < listBottom) {
                tryOffsetChildrenTopAndBottom(listBottom - last.getBottom());
            }
            first = getChildAt(0);
            while (first.getBottom() < listTop) {
                if (recycleBin.shouldRecycleViewType(((LayoutParams) first.getLayoutParams()).viewType)) {
                    detachViewFromParent(first);
                    recycleBin.addScrapView(first);
                } else {
                    removeViewInLayout(first);
                }
                first = getChildAt(0);
                this.mFirstPosition++;
            }
            return;
        }
        first = getChildAt(0);
        while (first.getTop() > listTop && this.mFirstPosition > 0) {
            first = addViewAbove(first, this.mFirstPosition);
            this.mFirstPosition += NO_POSITION;
        }
        if (first.getTop() > listTop) {
            tryOffsetChildrenTopAndBottom(listTop - first.getTop());
        }
        int lastIndex = getChildCount() + NO_POSITION;
        last = getChildAt(lastIndex);
        while (last.getTop() > listBottom) {
            if (recycleBin.shouldRecycleViewType(((LayoutParams) last.getLayoutParams()).viewType)) {
                detachViewFromParent(last);
                recycleBin.addScrapView(last);
            } else {
                removeViewInLayout(last);
            }
            lastIndex += NO_POSITION;
            last = getChildAt(lastIndex);
        }
    }

    protected View getLastChild() {
        return getChildAt(getChildCount() + NO_POSITION);
    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position + NO_POSITION;
        View view = obtainView(abovePosition, this.mIsScrap);
        setupChild(view, abovePosition, theView.getTop() - this.mDividerHeight, false, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1;
        View view = obtainView(belowPosition, this.mIsScrap);
        setupChild(view, belowPosition, theView.getBottom() + this.mDividerHeight, true, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    public void setItemsCanFocus(boolean itemsCanFocus) {
        this.mItemsCanFocus = itemsCanFocus;
        if (!itemsCanFocus) {
            setDescendantFocusability(393216);
        }
    }

    public boolean getItemsCanFocus() {
        return this.mItemsCanFocus;
    }

    public boolean isOpaque() {
        return (this.mCachingStarted && this.mIsCacheColorOpaque && this.mDividerIsOpaque) || super.isOpaque();
    }

    public void setCacheColorHint(int color) {
        boolean opaque = (color >>> 24) == MotionEventCompat.ACTION_MASK;
        this.mIsCacheColorOpaque = opaque;
        if (opaque) {
            if (this.mDividerPaint == null) {
                this.mDividerPaint = new Paint();
            }
            this.mDividerPaint.setColor(color);
        }
        super.setCacheColorHint(color);
    }

    void drawOverscrollHeader(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        if (bounds.bottom - bounds.top < height) {
            bounds.top = bounds.bottom - height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    void drawOverscrollFooter(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        if (bounds.bottom - bounds.top < height) {
            bounds.bottom = bounds.top + height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void dispatchDraw(Canvas r34) {
        /*
        r33 = this;
        r0 = r33;
        r10 = r0.mDividerHeight;
        r0 = r33;
        r0 = r0.mOverScrollHeader;
        r26 = r0;
        r0 = r33;
        r0 = r0.mOverScrollFooter;
        r25 = r0;
        if (r26 == 0) goto L_0x0135;
    L_0x0012:
        r13 = 1;
    L_0x0013:
        if (r25 == 0) goto L_0x0138;
    L_0x0015:
        r12 = 1;
    L_0x0016:
        if (r10 <= 0) goto L_0x013b;
    L_0x0018:
        r0 = r33;
        r0 = r0.mDivider;
        r31 = r0;
        if (r31 == 0) goto L_0x013b;
    L_0x0020:
        r11 = 1;
    L_0x0021:
        if (r11 != 0) goto L_0x0027;
    L_0x0023:
        if (r13 != 0) goto L_0x0027;
    L_0x0025:
        if (r12 == 0) goto L_0x0131;
    L_0x0027:
        r0 = r33;
        r7 = r0.mTempRect;
        r31 = r33.getPaddingLeft();
        r0 = r31;
        r7.left = r0;
        r31 = r33.getRight();
        r32 = r33.getLeft();
        r31 = r31 - r32;
        r32 = r33.getPaddingRight();
        r31 = r31 - r32;
        r0 = r31;
        r7.right = r0;
        r9 = r33.getChildCount();
        r0 = r33;
        r0 = r0.mHeaderViewInfos;
        r31 = r0;
        r18 = r31.size();
        r0 = r33;
        r0 = r0.mItemCount;
        r21 = r0;
        r0 = r33;
        r0 = r0.mFooterViewInfos;
        r31 = r0;
        r31 = r31.size();
        r31 = r21 - r31;
        r17 = r31 + -1;
        r0 = r33;
        r0 = r0.mHeaderDividersEnabled;
        r19 = r0;
        r0 = r33;
        r0 = r0.mFooterDividersEnabled;
        r16 = r0;
        r0 = r33;
        r15 = r0.mFirstPosition;
        r0 = r33;
        r5 = r0.mAreAllItemsSelectable;
        r0 = r33;
        r4 = r0.mAdapter;
        if (r11 == 0) goto L_0x013e;
    L_0x0083:
        r31 = r33.isOpaque();
        if (r31 == 0) goto L_0x013e;
    L_0x0089:
        r31 = super.isOpaque();
        if (r31 != 0) goto L_0x013e;
    L_0x008f:
        r14 = 1;
    L_0x0090:
        if (r14 == 0) goto L_0x00ba;
    L_0x0092:
        r0 = r33;
        r0 = r0.mDividerPaint;
        r31 = r0;
        if (r31 != 0) goto L_0x00ba;
    L_0x009a:
        r0 = r33;
        r0 = r0.mIsCacheColorOpaque;
        r31 = r0;
        if (r31 == 0) goto L_0x00ba;
    L_0x00a2:
        r31 = new android.graphics.Paint;
        r31.<init>();
        r0 = r31;
        r1 = r33;
        r1.mDividerPaint = r0;
        r0 = r33;
        r0 = r0.mDividerPaint;
        r31 = r0;
        r32 = r33.getCacheColorHint();
        r31.setColor(r32);
    L_0x00ba:
        r0 = r33;
        r0 = r0.mDividerPaint;
        r27 = r0;
        r31 = r33.getBottom();
        r32 = r33.getTop();
        r31 = r31 - r32;
        r0 = r33;
        r0 = r0.mListPadding;
        r32 = r0;
        r0 = r32;
        r0 = r0.bottom;
        r32 = r0;
        r31 = r31 - r32;
        r32 = r33.getScrollY();
        r22 = r31 + r32;
        r0 = r33;
        r0 = r0.mStackFromBottom;
        r31 = r0;
        if (r31 != 0) goto L_0x01d3;
    L_0x00e6:
        r6 = 0;
        r28 = r33.getScrollY();
        if (r9 <= 0) goto L_0x0104;
    L_0x00ed:
        if (r28 >= 0) goto L_0x0104;
    L_0x00ef:
        if (r13 == 0) goto L_0x0141;
    L_0x00f1:
        r31 = 0;
        r0 = r31;
        r7.bottom = r0;
        r0 = r28;
        r7.top = r0;
        r0 = r33;
        r1 = r34;
        r2 = r26;
        r0.drawOverscrollHeader(r1, r2, r7);
    L_0x0104:
        r20 = 0;
    L_0x0106:
        r0 = r20;
        if (r0 < r9) goto L_0x015c;
    L_0x010a:
        r31 = r33.getBottom();
        r32 = r33.getScrollY();
        r24 = r31 + r32;
        if (r12 == 0) goto L_0x0131;
    L_0x0116:
        r31 = r15 + r9;
        r0 = r31;
        r1 = r21;
        if (r0 != r1) goto L_0x0131;
    L_0x011e:
        r0 = r24;
        if (r0 <= r6) goto L_0x0131;
    L_0x0122:
        r7.top = r6;
        r0 = r24;
        r7.bottom = r0;
        r0 = r33;
        r1 = r34;
        r2 = r25;
        r0.drawOverscrollFooter(r1, r2, r7);
    L_0x0131:
        super.dispatchDraw(r34);
        return;
    L_0x0135:
        r13 = 0;
        goto L_0x0013;
    L_0x0138:
        r12 = 0;
        goto L_0x0016;
    L_0x013b:
        r11 = 0;
        goto L_0x0021;
    L_0x013e:
        r14 = 0;
        goto L_0x0090;
    L_0x0141:
        if (r11 == 0) goto L_0x0104;
    L_0x0143:
        r31 = 0;
        r0 = r31;
        r7.bottom = r0;
        r0 = -r10;
        r31 = r0;
        r0 = r31;
        r7.top = r0;
        r31 = -1;
        r0 = r33;
        r1 = r34;
        r2 = r31;
        r0.drawDivider(r1, r7, r2);
        goto L_0x0104;
    L_0x015c:
        if (r19 != 0) goto L_0x0166;
    L_0x015e:
        r31 = r15 + r20;
        r0 = r31;
        r1 = r18;
        if (r0 < r1) goto L_0x01bd;
    L_0x0166:
        if (r16 != 0) goto L_0x0170;
    L_0x0168:
        r31 = r15 + r20;
        r0 = r31;
        r1 = r17;
        if (r0 >= r1) goto L_0x01bd;
    L_0x0170:
        r0 = r33;
        r1 = r20;
        r8 = r0.getChildAt(r1);
        r6 = r8.getBottom();
        if (r11 == 0) goto L_0x01bd;
    L_0x017e:
        r0 = r22;
        if (r6 >= r0) goto L_0x01bd;
    L_0x0182:
        if (r12 == 0) goto L_0x018c;
    L_0x0184:
        r31 = r9 + -1;
        r0 = r20;
        r1 = r31;
        if (r0 == r1) goto L_0x01bd;
    L_0x018c:
        if (r5 != 0) goto L_0x01ac;
    L_0x018e:
        r31 = r15 + r20;
        r0 = r31;
        r31 = r4.isEnabled(r0);
        if (r31 == 0) goto L_0x01c1;
    L_0x0198:
        r31 = r9 + -1;
        r0 = r20;
        r1 = r31;
        if (r0 == r1) goto L_0x01ac;
    L_0x01a0:
        r31 = r15 + r20;
        r31 = r31 + 1;
        r0 = r31;
        r31 = r4.isEnabled(r0);
        if (r31 == 0) goto L_0x01c1;
    L_0x01ac:
        r7.top = r6;
        r31 = r6 + r10;
        r0 = r31;
        r7.bottom = r0;
        r0 = r33;
        r1 = r34;
        r2 = r20;
        r0.drawDivider(r1, r7, r2);
    L_0x01bd:
        r20 = r20 + 1;
        goto L_0x0106;
    L_0x01c1:
        if (r14 == 0) goto L_0x01bd;
    L_0x01c3:
        r7.top = r6;
        r31 = r6 + r10;
        r0 = r31;
        r7.bottom = r0;
        r0 = r34;
        r1 = r27;
        r0.drawRect(r7, r1);
        goto L_0x01bd;
    L_0x01d3:
        r0 = r33;
        r0 = r0.mListPadding;
        r31 = r0;
        r0 = r31;
        r0 = r0.top;
        r23 = r0;
        r28 = r33.getScrollY();
        if (r9 <= 0) goto L_0x0206;
    L_0x01e5:
        if (r13 == 0) goto L_0x0206;
    L_0x01e7:
        r0 = r28;
        r7.top = r0;
        r31 = 0;
        r0 = r33;
        r1 = r31;
        r31 = r0.getChildAt(r1);
        r31 = r31.getTop();
        r0 = r31;
        r7.bottom = r0;
        r0 = r33;
        r1 = r34;
        r2 = r26;
        r0.drawOverscrollHeader(r1, r2, r7);
    L_0x0206:
        if (r13 == 0) goto L_0x022d;
    L_0x0208:
        r29 = 1;
    L_0x020a:
        r20 = r29;
    L_0x020c:
        r0 = r20;
        if (r0 < r9) goto L_0x0230;
    L_0x0210:
        if (r9 <= 0) goto L_0x0131;
    L_0x0212:
        if (r28 <= 0) goto L_0x0131;
    L_0x0214:
        if (r12 == 0) goto L_0x02a5;
    L_0x0216:
        r3 = r33.getBottom();
        r7.top = r3;
        r31 = r3 + r28;
        r0 = r31;
        r7.bottom = r0;
        r0 = r33;
        r1 = r34;
        r2 = r25;
        r0.drawOverscrollFooter(r1, r2, r7);
        goto L_0x0131;
    L_0x022d:
        r29 = 0;
        goto L_0x020a;
    L_0x0230:
        if (r19 != 0) goto L_0x023a;
    L_0x0232:
        r31 = r15 + r20;
        r0 = r31;
        r1 = r18;
        if (r0 < r1) goto L_0x028d;
    L_0x023a:
        if (r16 != 0) goto L_0x0244;
    L_0x023c:
        r31 = r15 + r20;
        r0 = r31;
        r1 = r17;
        if (r0 >= r1) goto L_0x028d;
    L_0x0244:
        r0 = r33;
        r1 = r20;
        r8 = r0.getChildAt(r1);
        r30 = r8.getTop();
        if (r11 == 0) goto L_0x028d;
    L_0x0252:
        r0 = r30;
        r1 = r23;
        if (r0 <= r1) goto L_0x028d;
    L_0x0258:
        if (r5 != 0) goto L_0x0278;
    L_0x025a:
        r31 = r15 + r20;
        r0 = r31;
        r31 = r4.isEnabled(r0);
        if (r31 == 0) goto L_0x0291;
    L_0x0264:
        r31 = r9 + -1;
        r0 = r20;
        r1 = r31;
        if (r0 == r1) goto L_0x0278;
    L_0x026c:
        r31 = r15 + r20;
        r31 = r31 + 1;
        r0 = r31;
        r31 = r4.isEnabled(r0);
        if (r31 == 0) goto L_0x0291;
    L_0x0278:
        r31 = r30 - r10;
        r0 = r31;
        r7.top = r0;
        r0 = r30;
        r7.bottom = r0;
        r31 = r20 + -1;
        r0 = r33;
        r1 = r34;
        r2 = r31;
        r0.drawDivider(r1, r7, r2);
    L_0x028d:
        r20 = r20 + 1;
        goto L_0x020c;
    L_0x0291:
        if (r14 == 0) goto L_0x028d;
    L_0x0293:
        r31 = r30 - r10;
        r0 = r31;
        r7.top = r0;
        r0 = r30;
        r7.bottom = r0;
        r0 = r34;
        r1 = r27;
        r0.drawRect(r7, r1);
        goto L_0x028d;
    L_0x02a5:
        if (r11 == 0) goto L_0x0131;
    L_0x02a7:
        r0 = r22;
        r7.top = r0;
        r31 = r22 + r10;
        r0 = r31;
        r7.bottom = r0;
        r31 = -1;
        r0 = r33;
        r1 = r34;
        r2 = r31;
        r0.drawDivider(r1, r7, r2);
        goto L_0x0131;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.widgets.waterfall.internal.PLA_ListView.dispatchDraw(android.graphics.Canvas):void");
    }

    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        Drawable divider = this.mDivider;
        boolean clipDivider = this.mClipDivider;
        if (clipDivider) {
            canvas.save();
            canvas.clipRect(bounds);
        } else {
            divider.setBounds(bounds);
        }
        divider.draw(canvas);
        if (clipDivider) {
            canvas.restore();
        }
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public void setDivider(Drawable divider) {
        boolean z = false;
        if (divider != null) {
            this.mDividerHeight = divider.getIntrinsicHeight();
            this.mClipDivider = divider instanceof ColorDrawable;
        } else {
            this.mDividerHeight = 0;
            this.mClipDivider = false;
        }
        this.mDivider = divider;
        if (divider == null || divider.getOpacity() == NO_POSITION) {
            z = true;
        }
        this.mDividerIsOpaque = z;
        requestLayoutIfNecessary();
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = height;
        requestLayoutIfNecessary();
    }

    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        this.mHeaderDividersEnabled = headerDividersEnabled;
        invalidate();
    }

    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        this.mFooterDividersEnabled = footerDividersEnabled;
        invalidate();
    }

    public void setOverscrollHeader(Drawable header) {
        this.mOverScrollHeader = header;
        if (getScrollY() < 0) {
            invalidate();
        }
    }

    public Drawable getOverscrollHeader() {
        return this.mOverScrollHeader;
    }

    public void setOverscrollFooter(Drawable footer) {
        this.mOverScrollFooter = footer;
        invalidate();
    }

    public Drawable getOverscrollFooter() {
        return this.mOverScrollFooter;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        int closetChildIndex = NO_POSITION;
        if (gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(getScrollX(), getScrollY());
            ListAdapter adapter = this.mAdapter;
            if (adapter.getCount() < getChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                layoutChildren();
            }
            Rect otherRect = this.mTempRect;
            int minDistance = Integer.MAX_VALUE;
            int childCount = getChildCount();
            int firstPosition = this.mFirstPosition;
            for (int i = 0; i < childCount; i++) {
                if (adapter.isEnabled(firstPosition + i)) {
                    View other = getChildAt(i);
                    other.getDrawingRect(otherRect);
                    offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = PLA_AbsListView.getDistance(previouslyFocusedRect, otherRect, direction);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closetChildIndex = i;
                    }
                }
            }
        }
        if (closetChildIndex >= 0) {
            setSelection(this.mFirstPosition + closetChildIndex);
        } else {
            requestLayout();
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                addHeaderView(getChildAt(i));
            }
            removeAllViews();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mItemsCanFocus && ev.getAction() == 0 && ev.getEdgeFlags() != 0) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    public boolean performItemClick(View view, int position, long id) {
        return false | super.performItemClick(view, position, id);
    }

    public void setItemChecked(int position, boolean value) {
    }

    public boolean isItemChecked(int position) {
        return false;
    }

    public int getCheckedItemPosition() {
        return NO_POSITION;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        return null;
    }

    @Deprecated
    public long[] getCheckItemIds() {
        if (this.mAdapter == null || !this.mAdapter.hasStableIds()) {
            return new long[0];
        }
        return getCheckedItemIds();
    }

    public long[] getCheckedItemIds() {
        return new long[0];
    }

    public void clearChoices() {
    }
}
