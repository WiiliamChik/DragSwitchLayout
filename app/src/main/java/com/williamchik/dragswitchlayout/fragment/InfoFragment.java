package com.williamchik.dragswitchlayout.fragment;

import android.util.DisplayMetrics;
import android.widget.ImageView;

import williamchik.com.dragswitchlayout.R;

/**
 * 商品详情信息 fragment
 *
 * @author WilliamChik on 2016/01/21 18:51.
 */
public class InfoFragment extends BaseBizFragment {

  @Override
  protected int getFragmentResId() {
    return R.layout.info_fragment;
  }

  @Override
  protected void initUI() {
    ImageView img = (ImageView) mRootView.findViewById(R.id.img);
    DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
    img.getLayoutParams().height = metrics.widthPixels;
  }
}
