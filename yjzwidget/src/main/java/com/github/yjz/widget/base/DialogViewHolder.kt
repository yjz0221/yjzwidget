package com.github.yjz.widget.base

import android.util.SparseArray
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 对话框ViewHolder
 */
class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViewContainer: SparseArray<View> = SparseArray()

    fun <T : View> getView(@IdRes id: Int): T {
        var view = mViewContainer.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            mViewContainer.put(id, view)
        }

        return view as T
    }

    fun setGone(@IdRes id: Int, isGone: Boolean): DialogViewHolder {
        val view = getView<View>(id)

        view.visibility = if (isGone) View.GONE else View.VISIBLE

        return this
    }

    fun setText(@IdRes id: Int, charSequence: CharSequence?): DialogViewHolder {
        val view = getView<View>(id)

        if (view is EditText) {
            view.setText(charSequence)
        } else if (view is TextView) {
            view.text = charSequence
        }

        return this
    }

    fun setTextColor(@IdRes id: Int, color: Int): DialogViewHolder {
        val view = getView<View>(id)
        if (view is TextView) {
            view.setTextColor(color)
        }

        return this
    }

    fun setBackgroundColor(@IdRes id: Int, color: Int): DialogViewHolder {
        val view = getView<View>(id)
        view.setBackgroundColor(color)

        return this
    }

    fun setBackgroundResource(@IdRes id: Int, @DrawableRes resId: Int): DialogViewHolder {
        val view = getView<View>(id)
        view.setBackgroundResource(resId)

        return this
    }

    fun setImageResource(@IdRes id: Int, @DrawableRes resId: Int): DialogViewHolder {
        val view = getView<View>(id)
        if (view is ImageView) {
            view.setImageResource(resId)
        }

        return this
    }

    fun setOnClickListener(@IdRes id: Int, listener: View.OnClickListener): DialogViewHolder {
        val view = getView<View>(id)
        view.setOnClickListener(listener)

        return this
    }

    fun setOnLongClickListener(
        @IdRes id: Int,
        listener: View.OnLongClickListener
    ): DialogViewHolder {
        val view = getView<View>(id)
        view.setOnLongClickListener(listener)

        return this
    }

    fun setTag(@IdRes id: Int, tag: Any): DialogViewHolder {
        val view = getView<View>(id)
        view.tag = tag

        return this
    }

    fun getTag(@IdRes id: Int): Any {
        val view = getView<View>(id)
        return view.tag
    }
}