package com.williamchik.dragswitchlayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基础业务 fragment
 *
 * @author WilliamChik on 2016/01/21 18:57.
 */
public abstract class BaseBizFragment extends Fragment {

  protected View mRootView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mRootView = inflater.inflate(getFragmentResId(), null);
    return mRootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    initUI();
  }

  /**
   * 设置 fragment 对应的 layout id
   */
  protected abstract int getFragmentResId();

  /**
   * 初始化 UI
   */
  protected abstract void initUI();

}
