package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.WindowManager;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.ui.activity.BrowserActivity;

public class RecycleMenuDialog extends Dialog {

    protected static Point mPoint;

    private BrowserActivity mContext = null;

    public RecycleMenuDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_DayNight_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_item_right_menu);
        if (mPoint == null) {
            Display defaultDisplay = ((WindowManager)
                    getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            mPoint = new Point();
            defaultDisplay.getRealSize(mPoint);
        }
    }





}
