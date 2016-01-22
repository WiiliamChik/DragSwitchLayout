package com.williamchik.dragswitchlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 详情页专属定制的 ScrollView，滑动到底部时需要再次向上滑动才能拖拽到底部视图
 */
public class CustomScrollView extends ScrollView {

  // 是否允许向上拖拽到底部视图
  boolean mAllowDragToBottom;
  // 手势 down 时的 Y 轴坐标
  float mDownY;

  private ScrollListener mScrollListener;

  public CustomScrollView(Context context) {
    super(context);
  }

  public CustomScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setScrollListener(ScrollListener scrollListener) {
    mScrollListener = scrollListener;
  }

  /**
   * 重写 ScrollView 的 dispatchTouchEvent() 方法，自定义 touch 事件的分发流程。
   * 1. 默认情况下，ScrollView 内部的滚动优先，即由 ScrollView 处理 touch 事件。
   * 2. 当 ScrollView 滚动到底部时，此时再次向上拖拽，则将 touch 事件交给父视图处理，其他情况，都是由 ScrollView 来处理 touch 事件
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      mDownY = ev.getRawY();
      // ACTION_DOWN 的时候判断是否允许向上拖拽到底部视图。
      // ScrollView 滑动到底部，则允许，否则不允许
      mAllowDragToBottom = isAtBottom();
    } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
      if (mAllowDragToBottom && mDownY > ev.getRawY()) {
        // 承接上一个 ACTION_DOWN，如果允许向上拖拽到底部视图，且再次向上拖拽时，就将 touch 事件交给父视图处理
        getParent().requestDisallowInterceptTouchEvent(false);
        return false;
      }
    }

    // 默认情况下父视图不拦截 ScrollView 的 touch 事件
    getParent().requestDisallowInterceptTouchEvent(true);
    return super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mScrollListener != null) {
      mScrollListener.onScrollChanged(this, l, t, oldl, oldt);
    }
  }

  /**
   * @return ScrollView 是否滑动到底部
   */
  private boolean isAtBottom() {
    return getScrollY() + getMeasuredHeight() >= computeVerticalScrollRange();
  }

  /**
   * 当前时刻 ScrollView 滑动到底部需要滑动的Y轴距离
   */
  public int getScrollToBottomY() {
    return computeVerticalScrollRange() - getScrollY() - getHeight();
  }

  public interface ScrollListener {

    void onScrollChanged(CustomScrollView scrollView, int l, int t, int oldl, int oldt);

  }

}
