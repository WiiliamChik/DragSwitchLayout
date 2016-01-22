package com.williamchik.dragswitchlayout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.williamchik.dragswitchlayout.fragment.BottomBarFragment;
import com.williamchik.dragswitchlayout.fragment.ImgDeatailFragment;
import com.williamchik.dragswitchlayout.fragment.TitleFragment;

import williamchik.com.dragswitchlayout.R;

public class MainActivity extends AppCompatActivity {

  // 拖拽自动切换布局的容器
  private DragSwitchLayout mMainContainer;
  // 标题模块
  private TitleFragment mTitleFragment;
  // 图文详情模块
  private ImgDeatailFragment mImgDeatailFragment;
  // 是否初次加载
  private boolean mIsFirstLoad = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initUI();
  }

  private void initUI() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    mTitleFragment = (TitleFragment) fragmentManager.findFragmentById(R.id.fragment_good_detail_title);
    mImgDeatailFragment = new ImgDeatailFragment();
    mMainContainer = (DragSwitchLayout) findViewById(R.id.svc_good_detail_main_container);
    mMainContainer.setTopViewScrollListener(new CustomScrollView.ScrollListener() {

      @Override
      public void onScrollChanged(CustomScrollView scrollView, int l, int t, int oldl, int oldt) {
        // 主布局的顶部视图上下滑动时，顶部 fragment 的透明度动态变化
        mTitleFragment.setAlpha((float) t / (t + scrollView.getScrollToBottomY()));
      }
    });
    mMainContainer.setDragSwitchListener(new DragSwitchLayout.DragSwitchListener() {

      @Override
      public void onDragToBottomView() {
        // 初次滑动到图文详情，开始加载图文详情模块
        if (mIsFirstLoad) {
          fragmentManager.beginTransaction().replace(R.id.sv_good_detail_img_detail_container, mImgDeatailFragment).commit();
          mIsFirstLoad = false;
        }
        // 头部标题切换
        mTitleFragment.switchTitle();
      }

      @Override
      public void onDragToTopView() {
        // 头部标题切换
        mTitleFragment.switchTitle();
      }
    });
  }
}
