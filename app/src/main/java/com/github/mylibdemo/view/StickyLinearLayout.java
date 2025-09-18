package com.github.mylibdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mylibdemo.R;
import com.github.yjz.widget.util.DensityUtil;

/**
 * 作者:cl
 * 创建日期：2025/5/16
 * 描述:基于RecycleView子类实现嵌套滑动联动效果
 */
public class StickyLinearLayout extends ConstraintLayout implements NestedScrollingParent {

    private int minTopHeight;
    private int maxTopHeight;

    private NestedScrollingParentHelper scrollingParentHelper;
    private View stickTopView;


    public StickyLinearLayout(@NonNull Context context) {
        this(context, null);
    }

    public StickyLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();

        if (childCount > 0) {
            stickTopView = getChildAt(0);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        scrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        final View topView = stickTopView;
        if (topView == null) {
            return;
        }


        ViewGroup.LayoutParams lp = topView.getLayoutParams();
        int topHeight = topView.getHeight();
        int topTmpHeight = topHeight - dy;

        boolean canUpScroll = (dy > 0) && (topHeight > minTopHeight);
        boolean canDownScroll = false;

        if (target instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) target;
            if (rv.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                canDownScroll = (dy < 0) && (topView.getHeight() < maxTopHeight) && lm.findFirstVisibleItemPosition() == 0;
            }
        }

        if (canUpScroll || canDownScroll) {
           if (canUpScroll) {
              //往上滑
              lp.height = Math.max(topTmpHeight, minTopHeight);
           } else {
              //往下滑
              lp.height = Math.min(topTmpHeight, maxTopHeight);
           }
            consumed[1] = Math.abs(lp.height - topView.getHeight());
        }

        if (consumed[1] != 0) {
            topView.requestLayout();
        }

        Log.e("ludak", "onNestedPreScroll " + dx + "，" + dy + "，" + consumed[1] + "，" + ViewCompat.canScrollVertically(target, -1));
    }

    @Override
    public void onStopNestedScroll(View child) {
        scrollingParentHelper.onStopNestedScroll(child);
    }

    @Override
    public int getNestedScrollAxes() {
        return scrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return true;
    }

    private void init(Context ctx, AttributeSet attrs) {
        scrollingParentHelper = new NestedScrollingParentHelper(this);

        minTopHeight = DensityUtil.dpToPx(getContext(), 50);
        maxTopHeight = DensityUtil.dpToPx(getContext(), 200);
    }

}
