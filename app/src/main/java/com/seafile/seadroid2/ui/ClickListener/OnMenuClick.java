package com.seafile.seadroid2.ui.ClickListener;

import android.app.Dialog;
import android.view.View;

import com.seafile.seadroid2.data.SeafItem;

public interface OnMenuClick {

    void menuClick(View view, Dialog dialog, SeafItem postion, String menu);

}
