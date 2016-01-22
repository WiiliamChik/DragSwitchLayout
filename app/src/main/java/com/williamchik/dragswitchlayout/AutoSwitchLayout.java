package com.williamchik.dragswitchlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 带垂直滚动效果的视图切换容器，使用 Scroller 实现。暂时只支持在两种视图间切换，暂时只支持垂直切换效果。
 *
 * TODO 后续扩展加入支持水平切换的效果和多视图间的切换
 *
 * @author WilliamChik on 2016/01/12 16:30.
 */
public class AutoSwitchLayout extends RelativeLayout {

  // 视图切换时的滚动时长
  private static final int SWITCH_DURATION = 600;

  protected View mFormerView;
  protected View mNextView;

  // 滚动工具
  private Scroller mScroller;
  // 当前显示视图
  protected View mCurrentView;
  // 是否已经初始化
  private boolean mHasInit;

  public AutoSwitchLayout(Context context) {
    super(context);
    init(context);
  }

  public AutoSwitchLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    mScroller = new Scroller(context, new DecelerateInterpolator(2.0f));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (!mHasInit) {
      // 只在初始化的时候调用，一些参数作为全局变量保存起来
      mHasInit = true;
      mFormerView.layout(l, 0, r, b - t);
      mNextView.layout(l, 0, r, b - t);

      mNextView.offsetTopAndBottom(mFormerView.getMeasuredHeight());
      mCurrentView = mFormerView;
    } else {
      // 如果已经初始化，这次 onLayout 只需要将之前的状态存入即可
      mFormerView.layout(l, mFormerView.getTop(), r, mFormerView.getBottom());
      mNextView.layout(l, mNextView.getTop(), r, mNextView.getBottom());
    }
  }

  @Override
  protected void onFinishInflate() {
    if (getChildCount() != 2) {
      throw new IllegalStateException("AutoSwitchLayout only can host 2 elements");
    }
    mFormerView = getChildAt(0);
    mNextView = getChildAt(1);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    // 一个 view 位置改变，另一个 view 的位置要跟进
    changeViewPosition();
  }

  /**
   * 随着 Scroller 调起的滑动，子 View 的位置需要协调处理
   */
  private void changeViewPosition() {
    int offsetTopBottom = 0;
    if (mCurrentView == mFormerView) {
      offsetTopBottom = mFormerView.getBottom() - mNextView.getTop();
    } else if (mCurrentView == mNextView) {
      offsetTopBottom = -(mFormerView.getBottom() - mNextView.getTop());
    }

    mCurrentView.offsetTopAndBottom(offsetTopBottom);
    // 有的时候会默认白板，这个很恶心。后面有时间再优化
    invalidate();
  }

  @Override
  public void computeScroll() {
    // 当本实例正在执行滚动操作时，mScroller.computeScrollOffset() 返回 true，滚动结束，返回 false
    if (mScroller.computeScrollOffset()) {
      // 在 Scroller 正在执行滚动计算时就调用 View 的 scrollTo()，而 scrollTo() 会重绘 View，进而又触发了 View 的 computeScroll()，
      // 从而循环地改变 View 的偏移量，实现了平滑滚动的效果。
      // 注意，因为调用 computeScroll() 函数的是本实例，所以调用 scrollTo() 移动的将是该实例的子 View，即两个 TextView 实例
      scrollTo(0, mScroller.getCurrY());
    }
  }

  /**
   * 切换显示的视图，通过调用 mScroller 的 startScroll() 方法实现
   */
  public void switchView() {
    if (mFormerView.getBottom() > 0 && mFormerView.getTop() < 0) {
      // Scroller 正在滚动切换视图的时候，不处理新的切换视图操作
      return;
    }

    if (mCurrentView == mFormerView) {
      // 当前显示 mFormerView，则 mNextView 向上滚动，切换到 mNextView
      mCurrentView = mNextView;
      mScroller.startScroll(0, 0, 0, getHeight(), SWITCH_DURATION);
    } else if (mCurrentView == mNextView) {
      // 当前显示 mNextView，则 mFormerView 向下滚动，切换到 mFormerView
      mCurrentView = mFormerView;
      mScroller.startScroll(0, getHeight(), 0, -getHeight(), SWITCH_DURATION);
    }

    // mScroller.startScroll() 并不会启动滚动操作，需要通过 invalidate() 的方式来触发父类的 drawChild() 方法，再而触发子 View 的
    // computeScroll() 方法，从而真正启动滚动操作
    invalidate();
  }
}
