package com.sqisland.android.sliding_pane_layout;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CrossFadeSlidingPaneLayout extends SlidingPaneLayout {
  private View partialView = null;
  private View fullView = null;

  public CrossFadeSlidingPaneLayout(Context context) {
    super(context);
  }

  public CrossFadeSlidingPaneLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CrossFadeSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    if (getChildCount() < 1) {
      return;
    }

    View panel = getChildAt(0);
    if (!(panel instanceof ViewGroup)) {
      return;
    }

    ViewGroup viewGroup = (ViewGroup) panel;
    if (viewGroup.getChildCount() != 2) {
      return;
    }
    fullView = viewGroup.getChildAt(0);
    partialView = viewGroup.getChildAt(1);

    super.setPanelSlideListener(crossFadeListener);
  }

  @Override
  public void setPanelSlideListener(final PanelSlideListener listener) {
    if (listener == null) {
      super.setPanelSlideListener(crossFadeListener);
      return;
    }

    super.setPanelSlideListener(new PanelSlideListener() {
      @Override
      public void onPanelSlide(View panel, float slideOffset) {
        crossFadeListener.onPanelSlide(panel, slideOffset);
        listener.onPanelSlide(panel, slideOffset);
      }

      @Override
      public void onPanelOpened(View panel) {
        listener.onPanelOpened(panel);
      }

      @Override
      public void onPanelClosed(View panel) {
        listener.onPanelClosed(panel);
      }
    });
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    if (partialView != null) {
      partialView.setVisibility(isOpen() ? View.GONE : VISIBLE);
    }
  }

  private SimplePanelSlideListener crossFadeListener = new SimplePanelSlideListener() {
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
      super.onPanelSlide(panel, slideOffset);
      if (partialView == null || fullView == null) {
        return;
      }

      partialView.setVisibility(isOpen() ? View.GONE : VISIBLE);
      partialView.setAlpha(1 - slideOffset);
      fullView.setAlpha(slideOffset);
    }
  };
}