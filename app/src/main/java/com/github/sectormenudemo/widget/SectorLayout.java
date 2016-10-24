package com.github.sectormenudemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 10/24 0024.
 */
public class SectorLayout extends FrameLayout {

    private Context mContext;

    private ListAdapter mAdapter;

    private int mRadius;

    private float mAlpha;

    private float mScale;

    private float mRotation;

    private View mMenuView;

    private boolean mOpenEnable;

    private float mStartValue, mEndValue;

    private ValueAnimator mOpenAnimator, mCloseAnimator;

    private static final long MOVE_TIME = 2000;

    private static final int CHILD_PERCENT = 6;

    private static final int ANGLE_90 = 90;

    private static final int ANGLE_360 = 360;

    private static final int DEFAULT_SIZE = 200;

    public SectorLayout(Context context) {
        this(context, null);
    }

    public SectorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * init base date
     *
     * @param context
     */
    private void init(Context context) {
        mStartValue = 0f;
        mEndValue = 1.0f;
        mContext = context;
        mRadius = 0;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //处理 wrap_content问题
        int defaultDimension = dip2px(DEFAULT_SIZE);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, defaultDimension);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultDimension);
        }

        measureChildViews();
    }


    private void measureChildViews() {
        final int childCount = getChildCount();
        final int childMode = MeasureSpec.EXACTLY;
        final int childSize = Math.min(getMeasuredWidth(), getMeasuredHeight()) / CHILD_PERCENT;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            int measureSpec = -1;
            measureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            childView.measure(measureSpec, measureSpec);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onChildLayout();
    }

    private void onChildLayout() {
        final int childCount = getChildCount();
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        int angle = 0;
        if (childCount > 2) {
            angle = ANGLE_90 / (childCount - 2);
        }
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (mMenuView == null && i == (childCount - 1)) {
                mMenuView = childView;
                menuClickListener();
            }
            int childSize = childView.getMeasuredWidth();
            if (childView.getVisibility() == GONE) {
                continue;
            }
            if (i == (childCount - 1)) {
                childView.layout(width - childSize, height - childSize, width, height);
            } else {
                childView.layout(width - (int) (mRadius * Math.sin(Math.toRadians(angle * i))) - childSize,
                        height - (int) (mRadius * Math.cos(Math.toRadians(angle * i))) - childSize,
                        width - (int) (mRadius * Math.sin(Math.toRadians(angle * i))),
                        height - (int) (mRadius * Math.cos(Math.toRadians(angle * i))));
                childView.setAlpha(mAlpha);
                childView.setScaleX(mScale);
                childView.setScaleY(mScale);
                childView.setRotation(mRotation);
            }
        }
    }

    private void menuClickListener() {
        mMenuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mOpenEnable) {
                    startAnimator();
                } else {
                    endAnimator();
                }
                mOpenEnable = !mOpenEnable;
            }
        });
    }

    public void endAnimator() {
        mCloseAnimator = initAnimator(false);
        if (mCloseAnimator != null)
            mCloseAnimator.start();
    }

    public void startAnimator() {
        mOpenAnimator = initAnimator(true);
        if (mOpenAnimator != null) {
            mOpenAnimator.start();
        }
    }

    public void cancelAnimator() {
        if (mOpenAnimator != null) {
            mOpenAnimator.cancel();
        }
        if (mCloseAnimator != null) {
            mCloseAnimator.cancel();
        }
    }

    private ValueAnimator initAnimator(final boolean openAnimatorEnable) {
        ValueAnimator animator = null;
        if (getChildAt(0) == null) {
            return animator;
        }
        if (openAnimatorEnable) {
            animator = ValueAnimator.ofFloat(mStartValue, mEndValue);
            animator.setInterpolator(new BounceInterpolator());
        } else {
            animator = ValueAnimator.ofFloat(mEndValue, mStartValue);
            animator.setInterpolator(new LinearInterpolator());
        }
        animator.setDuration(MOVE_TIME);
        animator.addUpdateListener(new MyAnimatorUpdateListener(this) {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRadius = (int) ((float) valueAnimator.getAnimatedValue() * (getHeight() - getChildAt(0).getWidth()));
                mAlpha = (float) valueAnimator.getAnimatedValue();
                mScale = (float) valueAnimator.getAnimatedValue();
                mRotation = (float) valueAnimator.getAnimatedValue() * ANGLE_360;
                if (openAnimatorEnable) {
                    mStartValue = 0f;
                    mEndValue = (float) valueAnimator.getAnimatedValue();
                } else {
                    mEndValue = 1.0f;
                    mStartValue = (float) valueAnimator.getAnimatedValue();
                }
                requestLayout();
            }
        });
        return animator;
    }

    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }

    private void buildItems() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final View itemView = mAdapter.getView(i, null, this);
            addView(itemView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null && !mAdapter.isEmpty()) {
            buildItems();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimator();
    }


    /**
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private WeakReference<SectorLayout> mWeakReference;

        public MyAnimatorUpdateListener(SectorLayout layout) {
            mWeakReference = new WeakReference<SectorLayout>(layout);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            SectorLayout layout = mWeakReference.get();
            if (layout == null) {
                return;
            }
        }
    }

}
