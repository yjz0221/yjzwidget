package com.github.yjz.widget.button;

import android.animation.ValueAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 描述:绘制叉和勾实现
 */
class StatusIndicatorView extends View {

   public enum Status {
      NONE,
      SUCCESS,
      FAILURE
   }

   private Status status = Status.NONE;

   private Paint successPaint;
   private Paint failurePaint;

   private Path successPath;
   private Path failurePathLine1; //  叉号的第一条线段 Path
   private Path failurePathLine2; //  叉号的第二条线段 Path

   private float successPathLength; //  保存 √ 的 Path 长度
   private float failurePathLengthLine1; // 叉号第一条线段的长度
   private float failurePathLengthLine2; // 叉号第二条线段的长度

   private ValueAnimator animator;
   private float animationProgress = 0f;



   public StatusIndicatorView(Context context) {
      super(context);
      init();
   }

   public StatusIndicatorView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public StatusIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }


   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      initPaths();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      switch (status) {
         case SUCCESS:
            drawSuccess(canvas);
            break;
         case FAILURE:
            drawFailure(canvas);
            break;
         case NONE:
         default:
            //无状态，什么都不绘制
            break;
      }
   }


   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (animator != null){
         animator.cancel();
      }
   }

   private void init() {
      initPaints();
      initPaths();

      // 初始化 ValueAnimator
      animator = ValueAnimator.ofFloat(0f, 1f);
      animator.setDuration(300);
      animator.setInterpolator(new LinearInterpolator());
      animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
         @Override
         public void onAnimationUpdate(ValueAnimator animation) {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
         }
      });
      animator.addListener(new AnimatorListenerAdapter() {
         @Override
         public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animationProgress = 1f;
            invalidate();
         }
      });
   }

   private void initPaints() {
      // 初始化成功状态画笔
      successPaint = new Paint();
      successPaint.setColor(Color.WHITE);
      successPaint.setStyle(Paint.Style.STROKE);
      successPaint.setStrokeWidth(5);
      successPaint.setAntiAlias(true);

      // 初始化失败状态画笔
      failurePaint = new Paint();
      failurePaint.setColor(Color.WHITE);
      failurePaint.setStyle(Paint.Style.STROKE);
      failurePaint.setStrokeWidth(5);
      failurePaint.setAntiAlias(true);
   }

   private void initPaths() {
      initCheckPath();
      initCrossMark();
   }


   /**
    * 绘制 ×
    */
   private void initCrossMark() {
      int centerX = getWidth() / 2;
      int centerY = getHeight() / 2;

      // 计算按钮尺寸的 1/4作为叉号的目标长度 (修改为 1:4 比例)
      float targetLength = Math.min(getWidth(), getHeight()) / 4f; //修改为除以 4

      // 初始化 × 的两条线段的 Path
      failurePathLine1 = new Path(); // 第一条线段 Path
      failurePathLine1.moveTo(centerX - targetLength / 2, centerY - targetLength / 2);
      failurePathLine1.lineTo(centerX + targetLength / 2, centerY + targetLength / 2);

      failurePathLine2 = new Path(); // 第二条线段 Path
      failurePathLine2.moveTo(centerX + targetLength / 2, centerY - targetLength / 2);
      failurePathLine2.lineTo(centerX - targetLength / 2, centerY + targetLength / 2);


      // 测量 × 的 Path 长度 (分别测量两条线段的长度)
      PathMeasure failureMeasure1 = new PathMeasure(failurePathLine1, false);
      failurePathLengthLine1 = failureMeasure1.getLength();

      PathMeasure failureMeasure2 = new PathMeasure(failurePathLine2, false);
      failurePathLengthLine2 = failureMeasure2.getLength();
   }


   /**
    * 绘制 √
    */
   private void initCheckPath() {
      int centerX = getWidth() / 2;
      int centerY = getHeight() / 2;

      // 初始化 √ 的路径
      successPath = new Path();
      successPath.moveTo(centerX - (float) getWidth() / 15f, centerY);         // 起点 P1:  水平偏移，垂直居中
      successPath.lineTo(centerX, centerY + (float) getHeight() / 10f);  // 第二个点 P2 (转折点): 微调水平偏移，向下偏移
      successPath.lineTo(centerX + (float) getWidth() / 12f, centerY - (float) getHeight() / 6f);   // 第三个点 P3 (终点):  水平偏移，向上偏移

      // 测量 √ 的 Path 长度
      PathMeasure successMeasure = new PathMeasure(successPath, false);
      successPathLength = successMeasure.getLength();
   }




   private void drawSuccess(Canvas canvas) {
      Path animatedPath = new Path();
      PathMeasure measure = new PathMeasure(successPath, false);
      float currentLength = successPathLength * animationProgress;

      if (currentLength > 0) {
         measure.getSegment(0, currentLength, animatedPath, true);
         canvas.drawPath(animatedPath, successPaint);
      }
   }

   private void drawFailure(Canvas canvas) {
      // 绘制第一条线段的动画
      Path animatedPathLine1 = new Path();
      PathMeasure measureLine1 = new PathMeasure(failurePathLine1, false);
      float currentLengthLine1 = failurePathLengthLine1 * animationProgress;

      if (currentLengthLine1 > 0) {
         measureLine1.getSegment(0, currentLengthLine1, animatedPathLine1, true);
         canvas.drawPath(animatedPathLine1, failurePaint); // 绘制第一条线段动画
      }

      // 绘制第二条线段的动画 (注意动画起始进度控制)
      Path animatedPathLine2 = new Path();
      PathMeasure measureLine2 = new PathMeasure(failurePathLine2, false);
      //  为了让两条线段动画同时进行，这里仍然使用相同的 animationProgress。
      //  如果需要更复杂的动画效果 (例如先绘制一条线，再绘制另一条线)，可以调整动画进度控制逻辑
      float currentLengthLine2 = failurePathLengthLine2 * animationProgress;

      if (currentLengthLine2 > 0) {
         measureLine2.getSegment(0, currentLengthLine2, animatedPathLine2, true);
         canvas.drawPath(animatedPathLine2, failurePaint); // 绘制第二条线段动画
      }
   }


   public void setStatus(Status status) {
      this.status = status;

      if (status == Status.SUCCESS || status == Status.FAILURE) {
         animationProgress = 0f;
         animator.start();
      } else {
         animationProgress = 1f;
         animator.cancel();
      }

      invalidate();
   }

   public Status getStatus() {
      return status;
   }

}