package com.reabyo.crme.interfaces;

import android.widget.LinearLayout;

import com.reabyo.crme.database.SQLite_Builder;

/**
 * Created by user on 12/9/2016.
 */

public interface AutoViewBuilder {
    public void autoViewBuilder();
    public LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    public LinearLayout.LayoutParams edParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    public LinearLayout.LayoutParams vParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    public LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    public int getCurrentVersion();
    public SQLite_Builder getBuilder();
}
