package com.github.yjz.widget.base

import android.app.Dialog

/**
 * 基类对话框ViewHold绑定视图状态接口
 */
interface OnBindViewListener {
    fun bindView(holder: DialogViewHolder, dialog: Dialog)
}