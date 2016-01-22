package com.williamchik.dragswitchlayout.fragment;

import android.webkit.WebView;

import williamchik.com.dragswitchlayout.R;

/**
 * 图文详情 fragment
 *
 * @author WilliamChik on 2016/01/22 14:08.
 */
public class ImgDeatailFragment extends BaseBizFragment {

  // 图文详情 WebView
  private WebView mWebView;

  @Override
  protected int getFragmentResId() {
    return R.layout.img_detail_fragment;
  }

  @Override
  protected void initUI() {
    mWebView = (WebView) mRootView.findViewById(R.id.wv_good_detail_desc_webview);
    mWebView.loadUrl("https://app.bishe.com//?fenlicore_c=goods&fenlicore_a=goodsDesc&m=wap&goodsId=64");
  }
}
