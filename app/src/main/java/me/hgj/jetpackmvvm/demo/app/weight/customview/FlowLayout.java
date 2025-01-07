package me.hgj.jetpackmvvm.demo.app.weight.customview;

/**
 * @Author : sunjikang
 * @Date : 2023/5/18
 * @Description:
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MarginLayoutParamsCompat;

import com.google.android.material.R;

/**
 * Horizontally lay out children until the row is filled and then moved to the next line. Call
 * {@link FlowLayout#setSingleLine(boolean)} to disable reflow and lay all children out in one line.
 *
 * @hide
 */
public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private int lineSpacing;
    private int itemSpacing;
    private boolean singleLine;

    public FlowLayout(@NonNull Context context) {
        this(context, null);
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        singleLine = false;
        loadFromAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlowLayout(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        singleLine = false;
        loadFromAttributes(context, attrs);
    }

    private void loadFromAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        final TypedArray array =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlowLayout, 0, 0);
        lineSpacing = array.getDimensionPixelSize(R.styleable.FlowLayout_lineSpacing, 0);
        itemSpacing = array.getDimensionPixelSize(R.styleable.FlowLayout_itemSpacing, 0);
        array.recycle();
    }

    protected int getLineSpacing() {
        return lineSpacing;
    }

    protected void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    protected int getItemSpacing() {
        return itemSpacing;
    }

    protected void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    /**
     * Returns whether this chip group is single line or reflowed multiline.
     */
    public boolean isSingleLine() {
        return singleLine;
    }

    /**
     * Sets whether this chip group is single line, or reflowed multiline.
     */
    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    /**
     * 封装了父ViewGroup传递过来的布局的期望, 每个MeasureSpec代表了一组宽度和高度的mode和size.
     * 一个MeasureSpec由大小和模式组成。它有三种模式:
     * 1. UNSPECIFIED(未指定)( 0 << 30)，父ViewGroup不对子View施加任何束缚，子View可以得到任意想要的大小；
     * 2. EXACTLY(完全)(1 << 30)，父ViewGroup决定子View的确切大小，子View将被限定在给定的边界里而忽略它本身大小；
     * 3. AT_MOST(至多)(2 << 30)，子View至多达到指定大小的值。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取widthMeasureSpec的低30位
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        //取widthMeasureSpec的高2位、低30位置0的值
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        //取heightMeasureSpec的低30位
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        //取heightMeasureSpec的高2位、低30位置0的值
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //如果是AT_MOST或者EXACTLY, 则maxWidth为width。
        final int maxWidth = widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY
                ? width
                : Integer.MAX_VALUE;

        int childLeft = 0;
        int childTop = 0;
        int childBottom = childTop;
        int childRight = childLeft;
        int maxChildRight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            if ((childLeft + child.getMeasuredWidth()) > maxWidth && !isSingleLine()) {
                childLeft = 0;
                childTop = childBottom;
            }

            childRight = childLeft + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            if (childRight > maxChildRight) {
                maxChildRight = childRight;
            }

            childLeft += child.getMeasuredWidth();
        }

        //根据父ViewGroup期望的mode和size, 结合当前View实际测量的大小，确定自身实际的大小。
        int finalWidth = getMeasuredDimension(width, widthMode, maxChildRight);
        int finalHeight = getMeasuredDimension(height, heightMode, childBottom);
        //测量完所有子View后调用setMeasuredDimension设置自身大小。
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private static int getMeasuredDimension(int size, int mode, int childrenEdge) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(childrenEdge, size);
            default: // UNSPECIFIED:
                return childrenEdge;
        }
    }

    /**
     * 当前View的尺寸或者位置是否发生了变化, 其中left/top/right/bottom是指当前View相对于父ViewGroup的位置.
     * 在onLayout()中需要调用所有子View的layout(left, top, right, bottom)方法，其中left/top/right/bottom为子View
     * 相当于当前View左上角的距离, 从而为每个子View摆放合适的位置.
     * @param sizeChanged
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean sizeChanged, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            // Do not re-layout when there are no children.
            return;
        }

        int paddingStart =  getPaddingLeft();
        int paddingEnd = getPaddingRight();
        int childStart = paddingStart;
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int childEnd;

        final int maxChildEnd = right - left - paddingEnd;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams lp = child.getLayoutParams();
            int startMargin = 0;
            int endMargin = 0;
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp);
                endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp);
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();

            if (!singleLine && (childEnd > maxChildEnd)) {
                childStart = paddingStart;
                childTop = childBottom;
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            child.layout(childStart + startMargin, childTop, childEnd, childBottom);

            //更新childStart供下次使用
            childStart += (startMargin + endMargin + child.getMeasuredWidth());
        }
    }
}
