package com.williamchik.dragswitchlayout.fragment;

import android.view.View;

import com.williamchik.dragswitchlayout.AutoSwitchLayout;

import williamchik.com.dragswitchlayout.R;

/**
 * 标题栏 fragment
 *
 * @author WilliamChik on 2016/01/22 09:26.
 */
public class TitleFragment extends BaseBizFragment {

  private View mMainContainer;
  private AutoSwitchLayout mAutoSwitchLayout;
  private View mOvalBackBtn;
  private View mDivider;

  @Override
  protected int getFragmentResId() {
    return R.layout.title_fragment;
  }

  @Override
  protected void initUI() {
    mMainContainer = mRootView.findViewById(R.id.ll_good_detail_header_main_container);
    mMainContainer.setAlpha(0);
    mDivider = mRootView.findViewById(R.id.v_good_detail_header_divider);
    mDivider.setAlpha(0);
    mOvalBackBtn = mRootView.findViewById(R.id.iv_good_detail_oval_back_btn);
    mAutoSwitchLayout = (AutoSwitchLayout) mRootView.findViewById(R.id.good_detail_auto_switch_layout);
  }

  /**
   * 根据详情页顶部视图的上下滑动来动态设置头部 fragment 的透明度
   *
   * @param alpha alpha 值
   */
  public void setAlpha(float alpha) {
    mMainContainer.setAlpha(alpha);
    mDivider.setAlpha(alpha);
    mOvalBackBtn.setAlpha(1 - alpha);
  }

  /**
   * 头部 title 切换
   */
  public void switchTitle() {
    mAutoSwitchLayout.switchView();
  }
}
