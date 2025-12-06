package com.github.yjz.widget.util

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 屏幕属性工具类
 */
class KtScreenUtils {
    companion object {
        private const val TAG: String = "ScreenUtils"

        /**
         * 获取屏幕宽度
         *
         * @return 宽度像素
         */
        fun getScreenWidth(ctx: Context): Int {
            val screenWidth = ctx.resources.displayMetrics.widthPixels
            return screenWidth
        }

        /**
         * 获取屏幕真正宽度
         *
         * @return 宽度像素
         */
        @Suppress("DEPRECATION")
        fun getRealScreenWidth(ctx: Context): Int {
            val realScreenWidth: Int
            val out = DisplayMetrics()

            val manager = ctx.getSystemService(Context.WINDOW_SERVICE)
            realScreenWidth = if (manager == null) {
                0
            } else {
                (manager as WindowManager).defaultDisplay.getRealMetrics(out)
                out.widthPixels
            }
            return realScreenWidth
        }

        /**
         * 获取内容/屏幕高度（不包含状态栏和导航栏）
         *
         * @return 高度像素
         */
        fun getScreenHeight(ctx: Context): Int {
            val screenHeight = ctx.resources.displayMetrics.heightPixels
            return screenHeight
        }

        /**
         * 获取屏幕真正高度（包含状态栏和导航栏）
         *
         * @return 高度像素
         */
        @Suppress("DEPRECATION")
        fun getRealScreenHeight(ctx: Context): Int {
            val realScreenHeight: Int
            val out = DisplayMetrics()

            val manager = ctx.getSystemService(Context.WINDOW_SERVICE)
            realScreenHeight = if (manager == null) {
                0
            } else {
                (manager as WindowManager).defaultDisplay.getRealMetrics(out)
                out.heightPixels
            }
            return realScreenHeight
        }

        /**
         * 设置屏幕保持常亮
         *
         * @param activity 当前界面
         * @param enable true 保持常亮，false 关闭常亮
         */
        fun keepScreenWakeLock(activity: Activity, enable: Boolean) {
            val window = activity.window

            window?.let {
                if (enable) {
                    it.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    it.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }
}