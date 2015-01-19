package com.sqisland.android.sliding_pane_layout;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;

public class CrossFadePaneActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cross_fade_pane);

    final SlidingPaneLayout layout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
    layout.setSliderFadeColor(Color.TRANSPARENT);
  }
}