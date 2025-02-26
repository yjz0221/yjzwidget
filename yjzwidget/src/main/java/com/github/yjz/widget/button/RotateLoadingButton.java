package com.github.yjz.widget.button;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.yjz.widget.R;
import com.github.yjz.widget.util.DensityUtil;

/**
 * 描述:继承FrameLayout实现ProgressBar加载按钮
 */
public class RotateLoadingButton extends FrameLayout {

    //定义按钮状态
    private enum ButtonState {
        NORMAL,
        LOADING,
        FINISH,
        ERROR
    }


    private static final float PRESS_SCALE = 0.98f; // 按压时缩放比例
    private static final int PRESS_DURATION = 200; // 按压动画持续时间

    private boolean isPressedEffectEnabled = true; // 是否启用按压效果 (可以根据需要控制是否启用)
    private AnimatorSet pressAnimatorSet;
    private AnimatorSet releaseAnimatorSet;


    private TextView tvContent;
    private ProgressBar loadingView;
    private StatusIndicatorView indicatorView;

    private ButtonState btnState = ButtonState.NORMAL;

    private CharSequence normalText;
    private float normalTextSize;
    private int normalTextColor;
    private int cornerRadius;
    private int normalBgColor;
    private int finishBgColor;
    private int errorBgColor;
    private int loadingColor;
    private int normalPressedColor;
    private int disabledBgColor;


    public RotateLoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public RotateLoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateLoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置ProgressBar宽高
        adjustLoadingViewSize(w, h);
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        OnClickListener wrappedListener = v -> {
            if (btnState == ButtonState.NORMAL) {
                if (l != null) {
                    l.onClick(v);
                }
            }
        };
        super.setOnClickListener(wrappedListener);
    }


    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        setPressedEffectEnabled(clickable);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setBackground(color2Drawable(disabledBgColor));
        } else {
            setBackground(color2Drawable(normalBgColor));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (releaseAnimatorSet != null) {
            releaseAnimatorSet.cancel();
        }

        if (pressAnimatorSet != null) {
            pressAnimatorSet.cancel();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }

        if (!isPressedEffectEnabled || btnState != ButtonState.NORMAL) {
            // 如果按压效果被禁用，直接返回
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下时播放按压动画
                if (releaseAnimatorSet != null && releaseAnimatorSet.isRunning()) {
                    releaseAnimatorSet.cancel(); //如果释放动画还在运行，先取消
                }
                if (pressAnimatorSet != null && !pressAnimatorSet.isRunning()) {
                    pressAnimatorSet.start();
                }
                setBackground(color2Drawable(normalPressedColor));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 松开或取消时播放释放动画
                if (pressAnimatorSet != null && pressAnimatorSet.isRunning()) {
                    pressAnimatorSet.cancel(); // 如果按压动画还在运行，先取消
                }
                if (releaseAnimatorSet != null && !releaseAnimatorSet.isRunning()) {
                    releaseAnimatorSet.start();
                }
                setBackground(color2Drawable(normalBgColor));
                break;
        }
        return super.onTouchEvent(event); //确保调用 super.onTouchEvent(event) 处理其他触摸事件
    }


    // 可以提供方法控制是否启用按压效果
    public void setPressedEffectEnabled(boolean enabled) {
        isPressedEffectEnabled = enabled;
    }


    public boolean isPressedEffectEnabled() {
        return isPressedEffectEnabled;
    }


    public void loading() {
        changeState(ButtonState.LOADING);
    }

    public void finish() {
        changeState(ButtonState.FINISH);
    }


    public void error() {
        changeState(ButtonState.ERROR);
    }


    public void normal() {
        changeState(ButtonState.NORMAL);
    }


    public void setTextSize(float textSize) {
        normalTextSize = textSize;
        tvContent.setTextSize(textSize);
    }


    public void setTextColor(int color) {
        normalTextColor = color;
        tvContent.setTextColor(color);
    }


    public void setText(CharSequence text) {
        normalText = text;
        tvContent.setText(text);
    }


    public void setLoadingColor(int color) {
        loadingView.getIndeterminateDrawable().setColorFilter(
                new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        );
    }


    private void init(Context ctx, AttributeSet attrs) {
        initPress();
        initAttrs(ctx, attrs);
        //添加控件
        initWidget(ctx);
        //初始化背景
        normal();
    }


    private void initAttrs(Context ctx, AttributeSet attrs) {
        cornerRadius = DensityUtil.dpToPx(ctx, 4);
        normalBgColor = Color.parseColor("#FFFFFF");
        finishBgColor = Color.parseColor("#32CD32");
        loadingColor = Color.parseColor("#FF0000");
        errorBgColor = Color.parseColor("#FF0000");
        normalTextColor = Color.parseColor("#000000");

        normalText = "";
        normalTextSize = DensityUtil.dpToPx(ctx, 14);

        //初始化 normalPressedColor，使用 HSV 调整亮度
        final float[] hsv = new float[3]; //  创建一个 float 数组用于存储 HSV 值
        Color.colorToHSV(normalBgColor, hsv); //  将 normalColor 转换为 HSV
        hsv[2] *= 0.5f; //降低亮度 (Value) 值，例如降低到 80%  (0.0f ~ 1.0f) <--  调整此值控制深浅程度
        normalPressedColor = Color.HSVToColor(hsv); //  将调整后的 HSV 值转换回 Color 得到更深色的 normalPressedColor
        //disabledBgColor默认颜色
        disabledBgColor = normalPressedColor;

        if (attrs != null) {
            // 获取自定义属性 (此部分保持不变，允许 XML 属性覆盖代码中的默认值)
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.RotateLoadingButton);
            normalBgColor = typedArray.getColor(R.styleable.RotateLoadingButton_normalBgColor, normalBgColor);
            normalText = typedArray.getString(R.styleable.RotateLoadingButton_normalText);
            normalTextSize = typedArray.getDimensionPixelSize(R.styleable.RotateLoadingButton_normalTextSize, (int) normalTextSize);
            normalTextColor = typedArray.getColor(R.styleable.RotateLoadingButton_normalTextColor, normalTextColor);
            loadingColor = typedArray.getColor(R.styleable.RotateLoadingButton_loadingColor, loadingColor);
            finishBgColor = typedArray.getColor(R.styleable.RotateLoadingButton_finishBgColor, finishBgColor);
            errorBgColor = typedArray.getColor(R.styleable.RotateLoadingButton_errorBgColor, errorBgColor);
            normalPressedColor = typedArray.getColor(R.styleable.RotateLoadingButton_normalPressedColor, normalPressedColor); //  <--  新增：获取 normalPressedColor 属性值
            cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RotateLoadingButton_cornerRadius, cornerRadius);
            disabledBgColor = typedArray.getColor(R.styleable.RotateLoadingButton_disabledBgColor, disabledBgColor);

            if (TextUtils.isEmpty(normalText)) {
                normalText = "";
            }

            typedArray.recycle();
        }
    }

    private void initWidget(Context ctx) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;

        tvContent = new TextView(ctx);
        tvContent.setGravity(Gravity.CENTER);
        tvContent.setLayoutParams(params);
        tvContent.setIncludeFontPadding(false);

        loadingView = new ProgressBar(ctx);
        loadingView.setVisibility(GONE);
        loadingView.setLayoutParams(params);
        setLoadingColor(loadingColor);

        indicatorView = new StatusIndicatorView(ctx);
        indicatorView.setVisibility(View.GONE);
        indicatorView.setLayoutParams(params);

        addView(tvContent);
        addView(loadingView);
        addView(indicatorView);
    }


    private void initPress() {
        setClickable(true);
        setFocusable(true);
        // 初始化按压和释放动画
        initPressAnimators();
    }

    private void initPressAnimators() {
        // 按压动画 (缩小)
        ObjectAnimator pressScaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, PRESS_SCALE);
        ObjectAnimator pressScaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1f, PRESS_SCALE);
        pressAnimatorSet = new AnimatorSet();
        pressAnimatorSet.setDuration(PRESS_DURATION);
        pressAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        pressAnimatorSet.playTogether(pressScaleXAnimator, pressScaleYAnimator);


        // 释放动画 (恢复原大小)
        ObjectAnimator releaseScaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", PRESS_SCALE, 1f);
        ObjectAnimator releaseScaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", PRESS_SCALE, 1f);
        releaseAnimatorSet = new AnimatorSet();
        releaseAnimatorSet.setDuration(PRESS_DURATION);
        releaseAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        releaseAnimatorSet.playTogether(releaseScaleXAnimator, releaseScaleYAnimator);
    }


    private void adjustLoadingViewSize(int width, int height) {
        int min = (int) (Math.min(width, height) * 2 / 3f);
        int margin = DensityUtil.dpToPx(getContext(), 4);
        int loadSize = min - margin * 2;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadSize, loadSize);
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.gravity = Gravity.CENTER;

        loadingView.setLayoutParams(params);
    }


    /**
     * 用于实现圆角效果
     */
    private Drawable color2Drawable(int bgColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE); //设置形状为矩形
        gradientDrawable.setColor(bgColor); // 置背景颜色
        gradientDrawable.setCornerRadius(cornerRadius); // 设置圆角半径

        return gradientDrawable;
    }

    private void changeState(ButtonState state) {
        btnState = state;

        switch (state) {
            case NORMAL:
                gone(indicatorView);
                gone(loadingView);
                visible(tvContent);
                setText(normalText);
                setTextSize(normalTextSize);
                setTextColor(normalTextColor);
                setBackground(color2Drawable(normalBgColor));
                break;
            case LOADING:
                gone(indicatorView);
                gone(tvContent);
                visible(loadingView);
                setBackground(color2Drawable(normalBgColor));
                break;
            case FINISH:
                gone(loadingView);
                gone(tvContent);
                visible(indicatorView);
                setBackground(color2Drawable(finishBgColor));
                indicatorView.setStatus(StatusIndicatorView.Status.SUCCESS);
                break;
            case ERROR:
                gone(loadingView);
                gone(tvContent);
                visible(indicatorView);
                setBackground(color2Drawable(errorBgColor));
                indicatorView.setStatus(StatusIndicatorView.Status.FAILURE);
                break;
        }
    }

    private void gone(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
    }


    private void visible(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
}
