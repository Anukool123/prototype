package com.mlizhi.widgets.waterfall;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import com.mlizhi.C0111R;
import com.mlizhi.widgets.waterfall.internal.PLA_ListView;

public class MultiColumnListView extends PLA_ListView {
    private static final int DEFAULT_COLUMN_NUMBER = 2;
    private static final String TAG = "MultiColumnListView";
    private int mColumnNumber;
    private int mColumnPaddingLeft;
    private int mColumnPaddingRight;
    private Column[] mColumns;
    private Column mFixedColumn;
    private Rect mFrameRect;
    private SparseIntArray mItems;

    private class Column {
        private int mColumnLeft;
        private int mColumnWidth;
        private int mIndex;
        private int mSynchedBottom;
        private int mSynchedTop;

        public Column(int index) {
            this.mSynchedTop = 0;
            this.mSynchedBottom = 0;
            this.mIndex = index;
        }

        public int getColumnLeft() {
            return this.mColumnLeft;
        }

        public int getColumnWidth() {
            return this.mColumnWidth;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public int getBottom() {
            int bottom = ExploreByTouchHelper.INVALID_ID;
            int childCount = MultiColumnListView.this.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View v = MultiColumnListView.this.getChildAt(index);
                if (v.getLeft() == this.mColumnLeft || MultiColumnListView.this.isFixedView(v)) {
                    if (bottom < v.getBottom()) {
                        bottom = v.getBottom();
                    }
                }
            }
            if (bottom == ExploreByTouchHelper.INVALID_ID) {
                return this.mSynchedBottom;
            }
            return bottom;
        }

        public void offsetTopAndBottom(int offset) {
            if (offset != 0) {
                int childCount = MultiColumnListView.this.getChildCount();
                for (int index = 0; index < childCount; index++) {
                    View v = MultiColumnListView.this.getChildAt(index);
                    if (v.getLeft() == this.mColumnLeft || MultiColumnListView.this.isFixedView(v)) {
                        v.offsetTopAndBottom(offset);
                    }
                }
            }
        }

        public int getTop() {
            int top = Integer.MAX_VALUE;
            int childCount = MultiColumnListView.this.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View v = MultiColumnListView.this.getChildAt(index);
                if (v.getLeft() == this.mColumnLeft || MultiColumnListView.this.isFixedView(v)) {
                    if (top > v.getTop()) {
                        top = v.getTop();
                    }
                }
            }
            if (top == Integer.MAX_VALUE) {
                return this.mSynchedTop;
            }
            return top;
        }

        public void save() {
            this.mSynchedTop = 0;
            this.mSynchedBottom = getTop();
        }

        public void clear() {
            this.mSynchedTop = 0;
            this.mSynchedBottom = 0;
        }
    }

    private class FixedColumn extends Column {
        public FixedColumn() {
            super(Integer.MAX_VALUE);
        }

        public int getBottom() {
            return MultiColumnListView.this.getScrollChildBottom();
        }

        public int getTop() {
            return MultiColumnListView.this.getScrollChildTop();
        }
    }

    public MultiColumnListView(Context context) {
        super(context);
        this.mColumnNumber = DEFAULT_COLUMN_NUMBER;
        this.mColumns = null;
        this.mFixedColumn = null;
        this.mItems = new SparseIntArray();
        this.mColumnPaddingLeft = 0;
        this.mColumnPaddingRight = 0;
        this.mFrameRect = new Rect();
        init(null);
    }

    public MultiColumnListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mColumnNumber = DEFAULT_COLUMN_NUMBER;
        this.mColumns = null;
        this.mFixedColumn = null;
        this.mItems = new SparseIntArray();
        this.mColumnPaddingLeft = 0;
        this.mColumnPaddingRight = 0;
        this.mFrameRect = new Rect();
        init(attrs);
    }

    public MultiColumnListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mColumnNumber = DEFAULT_COLUMN_NUMBER;
        this.mColumns = null;
        this.mFixedColumn = null;
        this.mItems = new SparseIntArray();
        this.mColumnPaddingLeft = 0;
        this.mColumnPaddingRight = 0;
        this.mFrameRect = new Rect();
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        getWindowVisibleDisplayFrame(this.mFrameRect);
        if (attrs == null) {
            this.mColumnNumber = DEFAULT_COLUMN_NUMBER;
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, C0111R.styleable.PinterestLikeAdapterView);
            int landColNumber = a.getInteger(1, -1);
            int defColNumber = a.getInteger(0, -1);
            if (this.mFrameRect.width() > this.mFrameRect.height() && landColNumber != -1) {
                this.mColumnNumber = landColNumber;
            } else if (defColNumber != -1) {
                this.mColumnNumber = defColNumber;
            } else {
                this.mColumnNumber = DEFAULT_COLUMN_NUMBER;
            }
            this.mColumnPaddingLeft = a.getDimensionPixelSize(DEFAULT_COLUMN_NUMBER, 0);
            this.mColumnPaddingRight = a.getDimensionPixelSize(3, 0);
            a.recycle();
        }
        this.mColumns = new Column[this.mColumnNumber];
        for (int i = 0; i < this.mColumnNumber; i++) {
            this.mColumns[i] = new Column(i);
        }
        this.mFixedColumn = new FixedColumn();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = ((((getMeasuredWidth() - this.mListPadding.left) - this.mListPadding.right) - this.mColumnPaddingLeft) - this.mColumnPaddingRight) / this.mColumnNumber;
        for (int index = 0; index < this.mColumnNumber; index++) {
            this.mColumns[index].mColumnWidth = width;
            this.mColumns[index].mColumnLeft = (this.mListPadding.left + this.mColumnPaddingLeft) + (width * index);
        }
        this.mFixedColumn.mColumnLeft = this.mListPadding.left;
        this.mFixedColumn.mColumnWidth = getMeasuredWidth();
    }

    protected void onMeasureChild(View child, int position, int widthMeasureSpec, int heightMeasureSpec) {
        if (isFixedView(child)) {
            child.measure(widthMeasureSpec, heightMeasureSpec);
        } else {
            child.measure(1073741824 | getColumnWidth(position), heightMeasureSpec);
        }
    }

    protected int modifyFlingInitialVelocity(int initialVelocity) {
        return initialVelocity / this.mColumnNumber;
    }

    protected void onItemAddedToList(int position, boolean flow) {
        super.onItemAddedToList(position, flow);
        if (!isHeaderOrFooterPosition(position)) {
            this.mItems.append(position, getNextColumn(flow, position).getIndex());
        }
    }

    protected void onLayoutSync(int syncPos) {
        for (Column c : this.mColumns) {
            c.save();
        }
    }

    protected void onLayoutSyncFinished(int syncPos) {
        for (Column c : this.mColumns) {
            c.clear();
        }
    }

    protected void onAdjustChildViews(boolean down) {
        int i = 0;
        int firstItem = getFirstVisiblePosition();
        if (!down && firstItem == 0) {
            int firstColumnTop = this.mColumns[0].getTop();
            Column[] columnArr = this.mColumns;
            int length = columnArr.length;
            while (i < length) {
                Column c = columnArr[i];
                c.offsetTopAndBottom(firstColumnTop - c.getTop());
                i++;
            }
        }
        super.onAdjustChildViews(down);
    }

    protected int getFillChildBottom() {
        int result = Integer.MAX_VALUE;
        for (Column c : this.mColumns) {
            int bottom = c.getBottom();
            if (result > bottom) {
                result = bottom;
            }
        }
        return result;
    }

    protected int getFillChildTop() {
        int result = ExploreByTouchHelper.INVALID_ID;
        for (Column c : this.mColumns) {
            int top = c.getTop();
            if (result < top) {
                result = top;
            }
        }
        return result;
    }

    protected int getScrollChildBottom() {
        int result = ExploreByTouchHelper.INVALID_ID;
        for (Column c : this.mColumns) {
            int bottom = c.getBottom();
            if (result < bottom) {
                result = bottom;
            }
        }
        return result;
    }

    protected int getScrollChildTop() {
        int result = Integer.MAX_VALUE;
        for (Column c : this.mColumns) {
            int top = c.getTop();
            if (result > top) {
                result = top;
            }
        }
        return result;
    }

    protected int getItemLeft(int pos) {
        if (isHeaderOrFooterPosition(pos)) {
            return this.mFixedColumn.getColumnLeft();
        }
        return getColumnLeft(pos);
    }

    protected int getItemTop(int pos) {
        if (isHeaderOrFooterPosition(pos)) {
            return this.mFixedColumn.getBottom();
        }
        int colIndex = this.mItems.get(pos, -1);
        if (colIndex == -1) {
            return getFillChildBottom();
        }
        return this.mColumns[colIndex].getBottom();
    }

    protected int getItemBottom(int pos) {
        if (isHeaderOrFooterPosition(pos)) {
            return this.mFixedColumn.getTop();
        }
        int colIndex = this.mItems.get(pos, -1);
        if (colIndex == -1) {
            return getFillChildTop();
        }
        return this.mColumns[colIndex].getTop();
    }

    private Column getNextColumn(boolean flow, int position) {
        int colIndex = this.mItems.get(position, -1);
        if (colIndex != -1) {
            return this.mColumns[colIndex];
        }
        int lastVisiblePos = Math.max(0, Math.max(0, position - getHeaderViewsCount()));
        if (lastVisiblePos < this.mColumnNumber) {
            return this.mColumns[lastVisiblePos];
        }
        if (flow) {
            return gettBottomColumn();
        }
        return getTopColumn();
    }

    private boolean isHeaderOrFooterPosition(int pos) {
        return this.mAdapter.getItemViewType(pos) == -2;
    }

    private Column getTopColumn() {
        int i = 0;
        Column result = this.mColumns[0];
        Column[] columnArr = this.mColumns;
        int length = columnArr.length;
        while (i < length) {
            Column c = columnArr[i];
            if (result.getTop() > c.getTop()) {
                result = c;
            }
            i++;
        }
        return result;
    }

    private Column gettBottomColumn() {
        int i = 0;
        Column result = this.mColumns[0];
        Column[] columnArr = this.mColumns;
        int length = columnArr.length;
        while (i < length) {
            Column c = columnArr[i];
            if (result.getBottom() > c.getBottom()) {
                result = c;
            }
            i++;
        }
        return result;
    }

    private int getColumnLeft(int pos) {
        int colIndex = this.mItems.get(pos, -1);
        if (colIndex == -1) {
            return 0;
        }
        return this.mColumns[colIndex].getColumnLeft();
    }

    private int getColumnWidth(int pos) {
        int colIndex = this.mItems.get(pos, -1);
        if (colIndex == -1) {
            return 0;
        }
        return this.mColumns[colIndex].getColumnWidth();
    }
}
