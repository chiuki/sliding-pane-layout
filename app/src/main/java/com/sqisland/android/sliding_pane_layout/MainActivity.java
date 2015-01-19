package com.sqisland.android.sliding_pane_layout;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends ListActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final ArrayList<Demo> demos = new ArrayList<>();
    demos.add(new Demo(this, TwoPaneActivity.class, R.string.title_two_pane));
    demos.add(new Demo(this, PartiallyShownPaneActivity.class, R.string.title_partially_shown_pane));
    demos.add(new Demo(this, CrossFadePaneActivity.class, R.string.title_cross_fade_pane));

    ArrayAdapter<Demo> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_list_item_1,
        demos);
    getListView().setAdapter(adapter);

    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Demo demo = demos.get(position);
        startActivity(new Intent(MainActivity.this, demo.activityClass));
      }
    });
  }

  public static class Demo {
    public final Class<?> activityClass;
    public final String title;

    public Demo(Context context, Class<?> activityClass, int titleId) {
      this.activityClass = activityClass;
      this.title = context.getString(titleId);
    }

    @Override
    public String toString() {
      return this.title;
    }
  }
}
