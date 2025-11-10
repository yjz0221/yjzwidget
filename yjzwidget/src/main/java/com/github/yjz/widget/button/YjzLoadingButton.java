package com.github.yjz.widget.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.yjz.widget.R;

/**
 * 作者:yjz
 * 创建日期：2025/4/25
 * 描述: ProgressBar + TextView加载按钮（堆叠排列）
 */
public class YjzLoadingButton extends FrameLayout {

    private ProgressBar pbView;
    private TextView tvContent;

    //控件属性
    private CharSequence text = "";
    private int textSize;
    private ColorStateList textColor;
    private int loadingColor;
    private float progressPercent = 0.65f;


    public YjzLoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public YjzLoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YjzLoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //重新设置ProgressBar的宽高大小
        setProgressSize(getMeasuredWidth(),getMeasuredHeight());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //重新设置ProgressBar的宽高大小
        setProgressSize(w,h);

    }

    public boolean isLoading() {
        return pbView.getVisibility() == View.VISIBLE;
    }


    public void loading() {
        loading(false);
    }


    /**
     * 设置加载状态
     *
     * @param openDarkenFilter 是否将控件背景亮度调暗，以提示用户此时不可点击
     */
    public void loading(boolean openDarkenFilter) {
        invisible(tvContent);
        visible(pbView);

        //调低背景亮度
        if (openDarkenFilter) {
            applyDarkenFilter(getBackground());
        }

        setEnabled(false);
    }


    public void normal() {
        normal(text);
    }


    public void normal(CharSequence text) {
        this.text = text;
        tvContent.setText(text);

        invisible(pbView);
        visible(tvContent);
        removeColorFilter(getBackground());
        setEnabled(true);
    }


    public void setTextColor(int color) {
        textColor = new ColorStateList(new int[][]{
                new int[]{} // default state
        }, new int[]{
                color
        });

        tvContent.setTextColor(textColor);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }


    private void setProgressSize(int width,int height){
        FrameLayout.LayoutParams lp = (LayoutParams) pbView.getLayoutParams();
        if (lp != null){
            int minSize = (int) (Math.min(width, height)*progressPercent);

            lp.gravity = Gravity.CENTER;
            lp.width = minSize;
            lp.height = minSize;

            pbView.setLayoutParams(lp);
        }
    }

    private void setLoadingColor(int colorValue) {
        // 获取 ProgressBar 的不确定模式 Drawable
        Drawable indeterminateDrawable = pbView.getIndeterminateDrawable();

        // 检查 Drawable 是否存在
        if (indeterminateDrawable != null) {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(colorValue, PorterDuff.Mode.SRC_IN);
            // 对于 API 29 及以上，可以使用 BlendModeColorFilter
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // BlendMode.SRC_IN 类似于 PorterDuff.Mode.SRC_IN
                BlendModeColorFilter blendModeColorFilter = new BlendModeColorFilter(colorValue, BlendMode.SRC_IN);
                indeterminateDrawable.setColorFilter(blendModeColorFilter);
            } else {
                indeterminateDrawable.setColorFilter(colorFilter);
            }
        }
    }


    private void init(Context ctx, AttributeSet attrs) {
        initAttr(ctx, attrs);
        initView(ctx);

        Drawable bgDrawable = getBackground();
        if (bgDrawable == null) {
            //默认添加一个白色的背景
            setBackground(new ColorDrawable(Color.parseColor("#ffffff")));
        }
    }

    private void initAttr(Context ctx, AttributeSet attrs) {
        TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.YjzLoadingButton);

        text = typedArray.getText(R.styleable.YjzLoadingButton_android_text);
        textColor = typedArray.getColorStateList(R.styleable.YjzLoadingButton_android_textColor);
        //获取尺寸值
        textSize = typedArray.getDimensionPixelOffset(R.styleable.YjzLoadingButton_android_textSize, 14);
        loadingColor = typedArray.getColor(R.styleable.YjzLoadingButton_yjz_loadingColor, Color.parseColor("#FF0000"));
        progressPercent = typedArray.getFloat(R.styleable.YjzLoadingButton_yjz_progressSizePrecent,progressPercent);

        typedArray.recycle();

        if (textColor == null) {
            //创建一个默认白色文字颜色
            textColor = new ColorStateList(new int[][]{
                    new int[]{} // default state
            }, new int[]{
                    Color.WHITE
            });
        }
    }


    private void initView(Context context) {
        pbView = (ProgressBar) LayoutInflater.from(context).inflate(R.layout.layout_yjz_progress_bar, this, false);
        tvContent = (TextView) LayoutInflater.from(context).inflate(R.layout.layout_yjz_text_view, this, false);


        //设置自定义属性
        tvContent.setText(text);
        setTextSize(textSize);
        tvContent.setTextColor(textColor);
        setLoadingColor(loadingColor);

        addView(pbView);
        addView(tvContent);

        invisible(pbView);
        visible(tvContent);
    }


    private void invisible(View... views) {
        if (views == null) return;

        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }


    private void visible(View... views) {
        if (views == null) return;

        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }


    private void applyDarkenFilter(Drawable drawable) {
        if (drawable == null) {
            return;
        }

        // 1. 创建 ColorMatrix
        ColorMatrix cm = new ColorMatrix();

        // 2. 设置 ColorMatrix 来降低亮度
        // setScale(rScale, gScale, bScale, alphaScale)
        // 将红、绿、蓝分量都乘以一个小于 1 的值，可以降低亮度
        // 例如，0.75f 表示将亮度降低到原来的 75%
        float scale = 0.75f; // 调整这个值来控制变暗的程度 (0.0f 到 1.0f)
        cm.setScale(scale, scale, scale, 1.0f); // 最后一个参数 1.0f 保持 Alpha 不变

        // 3. 从 ColorMatrix 创建 ColorMatrixColorFilter
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(cm);

        // 4. 应用 ColorFilter 到 Drawable
        // 使用 mutate() 获取 Drawable 的可变副本，避免影响共享的 Drawable 状态
        drawable.mutate();
        drawable.setColorFilter(colorFilter);
    }

    /**
     * 移除 Drawable 的 ColorFilter 恢复原始颜色
     *
     * @param drawable 需要移除过滤器的 Drawable
     */
    private void removeColorFilter(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        drawable.mutate();
        drawable.setColorFilter(null); // 将 ColorFilter 设置为 null 即可移除
    }
}
