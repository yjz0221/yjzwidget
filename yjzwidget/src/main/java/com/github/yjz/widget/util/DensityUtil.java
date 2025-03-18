package com.github.yjz.widget.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 作者:cl
 * 创建日期：2025/2/24
 * 描述:
 */
public class DensityUtil {

   // dp 转 px
   public static int dpToPx(Context context,float dp) {
      float density = context.getResources().getDisplayMetrics().density;
      return (int) (dp * density + 0.5f);
   }

   // px 转 dp
   public static int pxToDp(Context context,float px) {
      float density = context.getResources().getDisplayMetrics().density;
      return (int) (px / density + 0.5f);
   }
}
