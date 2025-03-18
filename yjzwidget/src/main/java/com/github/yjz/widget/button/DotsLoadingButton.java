package com.github.yjz.widget.button;

import android.animation.ValueAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatButton;

import com.github.yjz.widget.util.DensityUtil;


/**
 * 作者:cl
 * 创建日期：2025/2/25
 * 描述: 继承Button实现的带有文字和淡入淡出点的加载按钮
 */
public class DotsLoadingButton extends AppCompatButton {

    private enum Status {
        NORMAL,       // 正常状态，显示文本
        LOADING     // 加载状态，显示文本和加载点动画
    }


    private static final float DOT_RADIUS_TEXT_SIZE_RATIO = 0.1f; // 点半径与字体大小的比例系数
    private final float[] dotAlphas = {255, 255, 255}; //三个点的透明度


    private Paint dotPaint; //用于绘制点的画笔
    private float dotRadius; //点的半径，不再是固定值，而是与字体大小关联

    private ValueAnimator animator;
    private Status status = Status.NORMAL; //按钮状态


    public DotsLoadingButton(Context context) {
        this(context, null);
    }

    public DotsLoadingButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public DotsLoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel(); // 页面销毁时取消动画
        }
    }


    private void init() {
        // 初始化画笔，用于绘制点
        dotPaint = new Paint(getPaint());
        //初始化点半径，与字体大小关联
        updateDotRadius(); //初始化点半径
        // 初始化动画
        initAnimator();
    }

    private void initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            dotAlphas[0] = calculateAlpha(animatedValue, 0);
            dotAlphas[1] = calculateAlpha(animatedValue, 1);
            dotAlphas[2] = calculateAlpha(animatedValue, 2);
            postInvalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (status == Status.LOADING) {
                    animator.start();
                }
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (status == Status.LOADING) {
            //更新点半径，确保动态更新
            updateDotRadius();
            //加载状态下绘制动态点
            drawLoadingDots(canvas);
        }
    }

    public void loading() {
        setStatus(Status.LOADING);
    }

    public void normal() {
        setStatus(Status.NORMAL);
    }


    private void drawLoadingDots(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float dotSpacing = DensityUtil.dpToPx(getContext(), 1.5f);
        float centerX = 3f + (width + getPaint().measureText(getText().toString())) / 2f;
        float centerY = (height + getTextHeightPx()) / 2f - dotRadius * 2; //  使用更新后的 dotRadius

        dotPaint.setColor(getCurrentTextColor());
        for (int i = 0; i < 3; i++) {
            dotPaint.setAlpha((int) dotAlphas[i]);
            canvas.drawCircle(centerX + i * (dotRadius * 2 + dotSpacing) + dotRadius, centerY, dotRadius, dotPaint);
        }
    }


    private int calculateAlpha(float animatedValue, int dotIndex) {
        float durationPerDot = 1f / 3f; // 将动画周期分成三份，每份给一个点
        float delay = dotIndex * durationPerDot; // 每个点的延迟启动时间

        float normalizedTime = (animatedValue - delay + 1f) % 1f; // 归一化时间，并加入延迟，保证值在 0~1 循环

        if (normalizedTime < durationPerDot) {
            // 第一个 1/3 周期：点淡入
            return (int) (normalizedTime / durationPerDot * 255); // 线性淡入
        } else if (normalizedTime < 2 * durationPerDot) {
            // 中间 1/3 周期：点保持完全不透明
            return 255;
        } else {
            // 最后 1/3 周期：点淡出
            return (int) ((1 - (normalizedTime - 2 * durationPerDot) / durationPerDot) * 255); // 线性淡出
        }
    }


    // 设置按钮状态
    private void setStatus(Status status) {
        this.status = status;
        if (status == Status.LOADING) {
            if (animator != null && !animator.isRunning()) {
                animator.start(); // 启动动画
            }
        } else {
            if (animator != null && animator.isRunning()) {
                animator.cancel(); //非加载状态停止动画
            }
            postInvalidate(); // 请求重绘
        }
    }


    private float getTextHeightPx() {
        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics(); // 获取 FontMetrics 对象
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private void updateDotRadius() {
        float textSizePx = getPaint().getTextSize(); // 获取当前字体大小 (px)
        dotRadius = textSizePx * DOT_RADIUS_TEXT_SIZE_RATIO; //  根据比例系数计算点半径
    }
}