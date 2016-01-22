package com.williamchik.dragswitchlayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 商品详情专属定制的布局容器，实现拖动时上下两个布局自动切换，通过 ViewDragHelper 来实现。
 *
 * @author WilliaRelativeLayout16/01/12 14:20.
 */
public class DragSwitchLayout extends RelativeLayout {

  // 滑动速度的阈值，超过这个绝对值认为是可以上下切换视图
  private static final int VEL_THRESHOLD = 150;
  // 单位是像素，当上下滑动速度不够时，通过这个阈值来判定应该切换到顶部还是底部
  private static final int DISTANCE_THRESHOLD = 120;

  // 拖拽工具类
  private ViewDragHelper mDragHelper;
  // 顶部视图
  private CustomScrollView mTopView;
  // 底部视图
  private View mBottomView;
  // 顶部视图的高度
  private int mTopViewHeight;
  // 上下两个布局切换时的监听器
  private DragSwitchListener mDragSwitchListener;
  // 是否已经初始化
  private boolean mHasInit;

  public DragSwitchLayout(Context context) {
    super(context);
    init();
  }

  public DragSwitchLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DragSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mDragHelper = ViewDragHelper.create(this, 10f, new DragHelperCallback());
  }

  /**
   * 设置顶部视图的滑动监听
   */
  public void setTopViewScrollListener(final CustomScrollView.ScrollListener scrollListener) {
    post(new Runnable() {
      @Override
      public void run() {
        mTopView.setScrollListener(scrollListener);
      }
    });
  }

  /**
   * 设置布局切换时的监听
   */
  public void setDragSwitchListener(DragSwitchListener dragSwitchListener) {
    mDragSwitchListener = dragSwitchListener;
  }

  /**
   * DragSwitchLayout 回顶
   */
  public void scrollToTop() {
    mTopView.scrollTo(0, 0);
    if (mDragHelper.smoothSlideViewTo(mTopView, 0, 0)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (!mHasInit) {
      // 只在初始化的时候调用，一些参数作为全局变量保存起来
      mHasInit = true;
      mTopView.layout(l, 0, r, b - t);
      mBottomView.layout(l, 0, r, b - t);

      mTopViewHeight = mTopView.getMeasuredHeight();
      mBottomView.offsetTopAndBottom(mTopViewHeight);
    } else {
      // 如果已经初始化，这次 onLayout 只需要将之前的状态存入即可
      mTopView.layout(l, mTopView.getTop(), r, mTopView.getBottom());
      mBottomView.layout(l, mBottomView.getTop(), r, mBottomView.getBottom());
    }
  }

  @Override
  protected void onFinishInflate() {
    if (getChildCount() != 2) {
      throw new IllegalStateException("DragSwitchLayout only can host 2 elements");
    }
    // 初始化上下两个布局
    mTopView = (CustomScrollView) getChildAt(0);
    mBottomView = getChildAt(1);
  }

  /**
   * touch 事件的拦截统一交给 mDragHelper 处理
   */
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mTopView.getBottom() > 0 && mTopView.getTop() < 0) {
      // DragSwitchLayout 粘到顶部或底部，正在动画中的时候，DragSwitchLayout 不拦截 touch 事件
      return false;
    }

    return mDragHelper.shouldInterceptTouchEvent(ev);
  }

  /**
   * touch 事件的响应统一交给 mDragHelper 处理，由 DragHelperCallback 实现拖动效果
   */
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    mDragHelper.processTouchEvent(e);
    return true;
  }

  /**
   * mDragHelper 内部使用了 Scroller 来实现滑动，因此 mDragHelper 要配合主布局的 computeScroll() 一起使用
   */
  @Override
  public void computeScroll() {
    if (mDragHelper.continueSettling(true)) {
      invalidate();
    }
  }

  /**
   * 这是拖拽效果的主要逻辑
   */
  private class DragHelperCallback extends ViewDragHelper.Callback {

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      // 一个 view 位置改变，另一个 view 的位置要跟进
      changeViewPosition(changedView, dy);
    }

    @Override
    public boolean tryCaptureView(View child, int pointerId) {
      // 两个子布局都需要跟踪，返回true
      return true;
    }

    @Override
    public int getViewVerticalDragRange(View child) {
      // 这个用来控制拖拽过程中松手后，自动滑行的速度，暂时给一个随意的数值
      return 1;
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      // 滑动松开后，需要向上或者向下滑动到特定位置
      animTopOrBottom(releasedChild, yvel);
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      // 设定两个子布局垂直滑动的范围
      // dy 代表的是下一帧到来时子 View 应该移动的位置增量，top 代表的是下一帧到来时子 View 应该到达的位置。即有：child.getTop() + dy = top。
      int finalDy = dy;
      if (child == mTopView) {
        // 拖动的是顶部视图
        if (top > 0) {
          // 不让顶部视图往下拖，因为顶部会白板
          finalDy = 0;
        }
      } else if (child == mBottomView) {
        // 拖动的是底部视图
        if (top < 0) {
          // 不让底部视图往上拖，因为底部会白板
          finalDy = 0;
        }
      }
      // finalDy 代表的是下一帧到来时子 View 应该移动的位置增量。此处把移动的位置增量除以一个参数(4)，是为了让切换布局时的滑动速度变慢，制造一种阻力的效果
      // 而 (child.getTop() + finalDy / 4) 代表的是子 View 在下一帧到来时应该到达的位置
      return child.getTop() + finalDy / 4;
    }
  }

  /**
   * 滑动时 view 位置改变协调处理
   *
   * @param changedView 当前位置改变的视图
   * @param dy          changedView 的 Y 轴增量
   */
  private void changeViewPosition(View changedView, int dy) {
    if (changedView == mTopView) {
      // 顶部视图向上滑动时，需要动态地给底部视图设置一个 offset，让底部视图跟随顶部视图的向上滑动而向上滑动
      mBottomView.offsetTopAndBottom(dy);
    } else if (changedView == mBottomView) {
      // 底部视图向下滑动时，需要动态地给顶部视图设置一个 offset，让顶部视图跟随底部视图的向下滑动而向下滑动
      mTopView.offsetTopAndBottom(dy);
    }

    // 有的时候会默认白板，这个很恶心。后面有时间再优化
    invalidate();
  }

  /**
   * 布局容器向上或向下滑动到指定的布局（顶部或底部）
   *
   * @param releasedChild 滑动松开的子视图，也是需要滑动的子视图
   * @param yvel          y 轴速度
   */
  private void animTopOrBottom(View releasedChild, float yvel) {
    // 当前松开的子视图最后应该到达的位置，如果下述的条件都不满足，默认是滑回到原来的位置
    int finalTop = 0;

    if (releasedChild == mTopView) {
      // 拖动顶部视图松手
      if (yvel < -VEL_THRESHOLD || releasedChild.getTop() < -DISTANCE_THRESHOLD) {
        // 向上的速度足够大，或者向上滑动的距离超过某个阈值，就滑动到底部视图
        finalTop = -mTopViewHeight;
        // 底部视图开始显示
        if (null != mDragSwitchListener) {
          mDragSwitchListener.onDragToBottomView();
        }
      }
    } else if (releasedChild == mBottomView) {
      // 拖动底部视图松手
      if (yvel > VEL_THRESHOLD || releasedChild.getTop() > DISTANCE_THRESHOLD) {
        // 向下的速度足够大，或者向下滑动的距离超过某个阈值，就滑动到顶部视图
        finalTop = mTopViewHeight;
        // 顶部视图开始显示
        if (null != mDragSwitchListener) {
          mDragSwitchListener.onDragToTopView();
        }
      }
    }

    // 当前松开的子视图滑动到指定位置
    if (mDragHelper.smoothSlideViewTo(releasedChild, 0, finalTop)) {
      invalidate();
    }
  }

  /**
   * 布局切换时的监听器
   */
  public interface DragSwitchListener {

    /**
     * 切换到底部视图的回调
     */
    void onDragToBottomView();

    /**
     * 切换到顶部视图时的回调
     */
    void onDragToTopView();
  }
}
