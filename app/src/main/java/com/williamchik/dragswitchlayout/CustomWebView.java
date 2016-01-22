package com.williamchik.dragswitchlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * 详情页专属定制的 WebView，滑动到顶部时需要再次向下滑动才能拖拽到顶部视图
 */
public class CustomWebView extends WebView {

  // 是否允许向下拖拽到顶部视图
  boolean mAllowDragToTop;
  // 手势 down 时的 Y 轴坐标
  float mDownY;

  public CustomWebView(Context context) {
    super(context);
  }

  public CustomWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 重写 WebView 的 dispatchTouchEvent() 方法，自定义 touch 事件的分发流程。
   * 1. 默认情况下，WebView 内部的滚动优先，即由 WebView 处理 touch 事件。
   * 2. 当 WebView 滚动到顶部时，此时再次向下拖拽，则将 touch 事件交给父视图处理，其他情况，都是由 WebView 来处理 touch 事件
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      mDownY = ev.getRawY();
      // ACTION_DOWN 的时候判断是否允许向下拖拽到顶部视图。
      // WebView 滑动到顶部，则允许，否则不允许
      mAllowDragToTop = isAtTop();
    } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
      if (mAllowDragToTop && ev.getRawY() > mDownY) {
        // 承接上一个 ACTION_DOWN，如果允许向下拖拽到顶部视图，且再次向下拖拽时，就将 touch 事件交给父视图处理
        getParent().requestDisallowInterceptTouchEvent(false);
        return false;
      }
    }

    // 默认情况下父视图不拦截 WebView 的 touch 事件
    getParent().requestDisallowInterceptTouchEvent(true);
    return super.dispatchTouchEvent(ev);
  }

  /**
   * @return WebView 是否滑动到顶部
   */
  private boolean isAtTop() {
    return getScrollY() == 0;
  }
}
