package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.ui.activity.BrowserActivity;

public class RecycleMenuDialog extends Dialog {

    private BrowserActivity mContext = null ;

    public RecycleMenuDialog(@NonNull Context context) {
        super(context,R.style.Theme_AppCompat_DayNight_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_item_right_menu);

    }
}
