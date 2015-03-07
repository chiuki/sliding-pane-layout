package com.sqisland.android.sliding_pane_layout;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

public class CrossFadeSlidingPaneLayout extends SlidingPaneLayout {
  private View partialView = null;
  private View fullView = null;

  // helper flag pre honeycomb used in visibility and click response handling
  // helps avoid unnecessary layouts
  private boolean wasOpened = false;

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
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        // "changed" means that views were added or removed
        // we need to move the partial view out of the way in any case (if it's supposed to of course)
        updatePartialViewVisibilityPreHoneycomb(isOpen());
      } else {
        partialView.setVisibility(isOpen() ? View.GONE : VISIBLE);
      }
    }
  }

  private SimplePanelSlideListener crossFadeListener = new SimplePanelSlideListener() {
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
      super.onPanelSlide(panel, slideOffset);
      if (partialView == null || fullView == null) {
        return;
      }

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        if (slideOffset == 1 && !wasOpened) {
          // the layout was just opened, move the partial view off screen
          updatePartialViewVisibilityPreHoneycomb(true);
          wasOpened = true;
        } else if (slideOffset < 1 && wasOpened) {
          // the layout just started to close, move the partial view back, so it can be shown animating
          updatePartialViewVisibilityPreHoneycomb(false);
          wasOpened = false;
        }
      } else {
        partialView.setVisibility(isOpen() ? View.GONE : VISIBLE);
      }
      
      ViewHelper.setAlpha(partialView, 1 - slideOffset);
      ViewHelper.setAlpha(fullView, slideOffset);
    }
  };
  
  private void updatePartialViewVisibilityPreHoneycomb(boolean slidingPaneOpened) {
    // below API 11 the top view must be moved so it does not consume clicks intended for the bottom view
    // this applies curiously even when setting its visibility to GONE
    // this might be due to platform limitations or it may have been introduced by NineOldAndroids library
    if (slidingPaneOpened) {
      partialView.layout(-partialView.getWidth(), 0, 0, partialView.getHeight());
    } else {
      partialView.layout(0, 0, partialView.getWidth(), partialView.getHeight());
    }
  }
}
