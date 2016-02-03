package com.mlizhi.widgets.waterfall.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Adapter;

public abstract class PLA_AdapterView<T extends Adapter> extends ViewGroup {
    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID = Long.MIN_VALUE;
    public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
    public static final int ITEM_VIEW_TYPE_IGNORE = -1;
    static final int SYNC_FIRST_POSITION = 1;
    static final int SYNC_MAX_DURATION_MILLIS = 100;
    static final int SYNC_SELECTED_POSITION = 0;
    boolean mBlockLayoutRequests;
    boolean mDataChanged;
    private boolean mDesiredFocusableInTouchModeState;
    private boolean mDesiredFocusableState;
    private View mEmptyView;
    @ExportedProperty
    int mFirstPosition;
    boolean mInLayout;
    @ExportedProperty
    int mItemCount;
    private int mLayoutHeight;
    boolean mNeedSync;
    int mOldItemCount;
    int mOldSelectedPosition;
    long mOldSelectedRowId;
    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    OnItemSelectedListener mOnItemSelectedListener;
    int mSpecificTop;
    long mSyncHeight;
    int mSyncMode;
    int mSyncPosition;
    long mSyncRowId;

    public static class AdapterContextMenuInfo implements ContextMenuInfo {
        public long id;
        public int position;
        public View targetView;

        public AdapterContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }
    }

    class AdapterDataSetObserver extends DataSetObserver {
        private Parcelable mInstanceState;

        AdapterDataSetObserver() {
            this.mInstanceState = null;
        }

        public void onChanged() {
            PLA_AdapterView.this.mDataChanged = true;
            PLA_AdapterView.this.mOldItemCount = PLA_AdapterView.this.mItemCount;
            PLA_AdapterView.this.mItemCount = PLA_AdapterView.this.getAdapter().getCount();
            if (!PLA_AdapterView.this.getAdapter().hasStableIds() || this.mInstanceState == null || PLA_AdapterView.this.mOldItemCount != 0 || PLA_AdapterView.this.mItemCount <= 0) {
                PLA_AdapterView.this.rememberSyncState();
            } else {
                PLA_AdapterView.this.onRestoreInstanceState(this.mInstanceState);
                this.mInstanceState = null;
            }
            PLA_AdapterView.this.checkFocus();
            PLA_AdapterView.this.requestLayout();
        }

        public void onInvalidated() {
            PLA_AdapterView.this.mDataChanged = true;
            if (PLA_AdapterView.this.getAdapter().hasStableIds()) {
                this.mInstanceState = PLA_AdapterView.this.onSaveInstanceState();
            }
            PLA_AdapterView.this.mOldItemCount = PLA_AdapterView.this.mItemCount;
            PLA_AdapterView.this.mItemCount = 0;
            PLA_AdapterView.this.mNeedSync = false;
            PLA_AdapterView.this.checkFocus();
            PLA_AdapterView.this.requestLayout();
        }

        public void clearSavedState() {
            this.mInstanceState = null;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PLA_AdapterView<?> pLA_AdapterView, View view, int i, long j);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(PLA_AdapterView<?> pLA_AdapterView, View view, int i, long j);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(PLA_AdapterView<?> pLA_AdapterView, View view, int i, long j);

        void onNothingSelected(PLA_AdapterView<?> pLA_AdapterView);
    }

    public abstract T getAdapter();

    public abstract View getSelectedView();

    public abstract void setAdapter(T t);

    public abstract void setSelection(int i);

    public PLA_AdapterView(Context context) {
        super(context);
        this.mFirstPosition = 0;
        this.mSyncRowId = INVALID_ROW_ID;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mOldSelectedPosition = ITEM_VIEW_TYPE_IGNORE;
        this.mOldSelectedRowId = INVALID_ROW_ID;
        this.mBlockLayoutRequests = false;
    }

    public PLA_AdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFirstPosition = 0;
        this.mSyncRowId = INVALID_ROW_ID;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mOldSelectedPosition = ITEM_VIEW_TYPE_IGNORE;
        this.mOldSelectedRowId = INVALID_ROW_ID;
        this.mBlockLayoutRequests = false;
    }

    public PLA_AdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFirstPosition = 0;
        this.mSyncRowId = INVALID_ROW_ID;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mOldSelectedPosition = ITEM_VIEW_TYPE_IGNORE;
        this.mOldSelectedRowId = INVALID_ROW_ID;
        this.mBlockLayoutRequests = false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public boolean performItemClick(View view, int position, long id) {
        if (this.mOnItemClickListener == null) {
            return false;
        }
        playSoundEffect(0);
        this.mOnItemClickListener.onItemClick(this, view, position, id);
        return true;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        this.mOnItemLongClickListener = listener;
    }

    public final OnItemLongClickListener getOnItemLongClickListener() {
        return this.mOnItemLongClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return this.mOnItemSelectedListener;
    }

    public void addView(View child) {
        throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
    }

    public void addView(View child, int index) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
    }

    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
    }

    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
    }

    public void removeView(View child) {
        throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
    }

    public void removeViewAt(int index) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
    }

    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutHeight = getHeight();
    }

    @CapturedViewProperty
    public int getSelectedItemPosition() {
        return ITEM_VIEW_TYPE_IGNORE;
    }

    @CapturedViewProperty
    public long getSelectedItemId() {
        return INVALID_ROW_ID;
    }

    public Object getSelectedItem() {
        T adapter = getAdapter();
        int selection = getSelectedItemPosition();
        if (adapter == null || adapter.getCount() <= 0 || selection < 0) {
            return null;
        }
        return adapter.getItem(selection);
    }

    @CapturedViewProperty
    public int getCount() {
        return this.mItemCount;
    }

    public int getPositionForView(View view) {
        View listItem = view;
        while (true) {
            try {
                View v = (View) listItem.getParent();
                if (v.equals(this)) {
                    break;
                }
                listItem = v;
            } catch (ClassCastException e) {
                return ITEM_VIEW_TYPE_IGNORE;
            }
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i += SYNC_FIRST_POSITION) {
            if (getChildAt(i).equals(listItem)) {
                return this.mFirstPosition + i;
            }
        }
        return ITEM_VIEW_TYPE_IGNORE;
    }

    public int getFirstVisiblePosition() {
        return this.mFirstPosition;
    }

    public int getLastVisiblePosition() {
        return (this.mFirstPosition + getChildCount()) + ITEM_VIEW_TYPE_IGNORE;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        T adapter = getAdapter();
        boolean empty = adapter == null || adapter.isEmpty();
        updateEmptyStatus(empty);
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    boolean isInFilterMode() {
        return false;
    }

    public void setFocusable(boolean focusable) {
        boolean z = true;
        T adapter = getAdapter();
        boolean empty;
        if (adapter == null || adapter.getCount() == 0) {
            empty = true;
        } else {
            empty = false;
        }
        this.mDesiredFocusableState = focusable;
        if (!focusable) {
            this.mDesiredFocusableInTouchModeState = false;
        }
        if (!focusable || (empty && !isInFilterMode())) {
            z = false;
        }
        super.setFocusable(z);
    }

    public void setFocusableInTouchMode(boolean focusable) {
        boolean z = true;
        T adapter = getAdapter();
        boolean empty;
        if (adapter == null || adapter.getCount() == 0) {
            empty = true;
        } else {
            empty = false;
        }
        this.mDesiredFocusableInTouchModeState = focusable;
        if (focusable) {
            this.mDesiredFocusableState = true;
        }
        if (!focusable || (empty && !isInFilterMode())) {
            z = false;
        }
        super.setFocusableInTouchMode(z);
    }

    void checkFocus() {
        boolean empty;
        boolean focusable;
        boolean z;
        boolean z2 = false;
        T adapter = getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            empty = true;
        } else {
            empty = false;
        }
        if (!empty || isInFilterMode()) {
            focusable = true;
        } else {
            focusable = false;
        }
        if (focusable && this.mDesiredFocusableInTouchModeState) {
            z = true;
        } else {
            z = false;
        }
        super.setFocusableInTouchMode(z);
        if (focusable && this.mDesiredFocusableState) {
            z = true;
        } else {
            z = false;
        }
        super.setFocusable(z);
        if (this.mEmptyView != null) {
            if (adapter == null || adapter.isEmpty()) {
                z2 = true;
            }
            updateEmptyStatus(z2);
        }
    }

    @SuppressLint({"WrongCall"})
    private void updateEmptyStatus(boolean empty) {
        if (isInFilterMode()) {
            empty = false;
        }
        if (empty) {
            if (this.mEmptyView != null) {
                this.mEmptyView.setVisibility(0);
                setVisibility(8);
            } else {
                setVisibility(0);
            }
            if (this.mDataChanged) {
                onLayout(false, getLeft(), getTop(), getRight(), getBottom());
                return;
            }
            return;
        }
        if (this.mEmptyView != null) {
            this.mEmptyView.setVisibility(8);
        }
        setVisibility(0);
    }

    public Object getItemAtPosition(int position) {
        T adapter = getAdapter();
        return (adapter == null || position < 0) ? null : adapter.getItem(position);
    }

    public long getItemIdAtPosition(int position) {
        T adapter = getAdapter();
        return (adapter == null || position < 0) ? INVALID_ROW_ID : adapter.getItemId(position);
    }

    public void setOnClickListener(OnClickListener l) {
        throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
    }

    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = false;
        if (event.getEventType() == 8) {
            event.setEventType(4);
        }
        View selectedView = getSelectedView();
        if (selectedView != null) {
            populated = selectedView.dispatchPopulateAccessibilityEvent(event);
        }
        if (!populated) {
            if (selectedView != null) {
                event.setEnabled(selectedView.isEnabled());
            }
            event.setItemCount(getCount());
            event.setCurrentItemIndex(getSelectedItemPosition());
        }
        return populated;
    }

    protected boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    void handleDataChanged() {
        if (this.mItemCount > 0 && this.mNeedSync) {
            this.mNeedSync = false;
        }
    }

    int findSyncPosition() {
        int count = this.mItemCount;
        if (count == 0) {
            return ITEM_VIEW_TYPE_IGNORE;
        }
        long idToMatch = this.mSyncRowId;
        int seed = this.mSyncPosition;
        if (idToMatch == INVALID_ROW_ID) {
            return ITEM_VIEW_TYPE_IGNORE;
        }
        seed = Math.min(count + ITEM_VIEW_TYPE_IGNORE, Math.max(0, seed));
        long endTime = SystemClock.uptimeMillis() + 100;
        int first = seed;
        int last = seed;
        boolean next = false;
        T adapter = getAdapter();
        if (adapter == null) {
            return ITEM_VIEW_TYPE_IGNORE;
        }
        while (SystemClock.uptimeMillis() <= endTime) {
            if (adapter.getItemId(seed) != idToMatch) {
                boolean hitLast = last == count + ITEM_VIEW_TYPE_IGNORE;
                boolean hitFirst = first == 0;
                if (hitLast && hitFirst) {
                    break;
                } else if (hitFirst || (next && !hitLast)) {
                    last += SYNC_FIRST_POSITION;
                    seed = last;
                    next = false;
                } else if (hitLast || !(next || hitFirst)) {
                    first += ITEM_VIEW_TYPE_IGNORE;
                    seed = first;
                    next = true;
                }
            } else {
                return seed;
            }
        }
        return ITEM_VIEW_TYPE_IGNORE;
    }

    int lookForSelectablePosition(int position, boolean lookDown) {
        return position;
    }

    void rememberSyncState() {
        if (getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = (long) this.mLayoutHeight;
            View v = getChildAt(0);
            T adapter = getAdapter();
            if (this.mFirstPosition < 0 || this.mFirstPosition >= adapter.getCount()) {
                this.mSyncRowId = -1;
            } else {
                this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
            }
            this.mSyncPosition = this.mFirstPosition;
            if (v != null) {
                this.mSpecificTop = v.getTop();
            }
            this.mSyncMode = SYNC_FIRST_POSITION;
        }
    }
}
