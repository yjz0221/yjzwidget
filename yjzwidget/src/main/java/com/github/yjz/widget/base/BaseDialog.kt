package com.github.yjz.widget.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.github.yjz.widget.R
import com.github.yjz.widget.util.KtScreenUtils
import com.github.yjz.widget.util.ScreenUtils


/**
 * 对话框基类
 */
open class BaseDialog(context: Context, styleId: Int?) :
    Dialog(context, styleId ?: R.style.YjzBaseDialog) {

    private lateinit var dialogViewHolder: DialogViewHolder

    private var width: Int = 0
    private var height: Int = 0
    private var gravity: Int = Gravity.CENTER
    private var dimAmount: Float = 0.6F

    @StyleRes
    private var animationRes: Int = 0

    constructor(
        context: Context,
        @StyleRes styleId: Int? = null,
        @LayoutRes layoutId: Int,
        listener: OnBindViewListener
    ) : this(context, styleId) {
        val contentView = LayoutInflater.from(context).inflate(layoutId, null)
        dialogViewHolder = DialogViewHolder(contentView)

        setContentView(contentView)
        listener.bindView(dialogViewHolder, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.decorView?.setPadding(0, 0, 0, 0)
    }

    override fun onStart() {
        super.onStart()

        window?.let {
            val lp = it.attributes
            lp.width = if (width == WindowManager.LayoutParams.WRAP_CONTENT) {
                WindowManager.LayoutParams.WRAP_CONTENT
            } else if (width <= 0) {
                (KtScreenUtils.getRealScreenWidth(context) * 0.8).toInt()
            } else {
                width
            }

            lp.height = if (height <= 0) {
                WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                height
            }

            lp.dimAmount = if (dimAmount < 0) {
                0.6F
            } else {
                dimAmount
            }

            lp.gravity = gravity

            if (animationRes != 0) {
                lp.windowAnimations = animationRes
            }
        }
    }

    fun setWidth(width: Int): BaseDialog {
        this.width = width
        return this
    }

    fun setHeight(height: Int): BaseDialog {
        this.height = height
        return this
    }

    fun setDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float): BaseDialog {
        this.dimAmount = dimAmount
        return this
    }

    fun setGravity(gravity: Int): BaseDialog {
        this.gravity = gravity
        return this
    }

    fun setAnimation(@StyleRes animationRes: Int): BaseDialog {
        this.animationRes = animationRes
        return this
    }

    fun cancelable(flag: Boolean): BaseDialog {
        setCancelable(flag)
        return this
    }

    fun canceledOnTouchOutside(cancel: Boolean): BaseDialog {
        setCanceledOnTouchOutside(cancel)
        return this
    }
}