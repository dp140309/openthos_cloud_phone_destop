package com.seafile.seadroid2.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;

public class OpenthosSettingsActivity extends BaseActivity implements View.OnClickListener, View.OnHoverListener {

    private FrameLayout frameLayout;
    private View accountSettings, generalSettings;
    private TextView accountButton, generalButton;
    private LayoutInflater Inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Inflater = LayoutInflater.from(this);
        initView();
        initData();
    }

    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.settings_item_fragment);
        accountButton = (TextView) findViewById(R.id.account_settings_view);
        generalButton = (TextView) findViewById(R.id.general_settings_view);
        accountSettings = Inflater.inflate(R.layout.account_settings, null, false);
        generalSettings = Inflater.inflate(R.layout.general_settings, null, false);

    }

    private void initData() {
        if (frameLayout != null) frameLayout.removeAllViews();
        frameLayout.addView(accountSettings);
        accountButton.setOnClickListener(this);
        generalButton.setOnClickListener(this);
        accountButton.setOnHoverListener(this);
        generalButton.setOnHoverListener(this);
    }

    @Override
    public void onClick(View v) {
        if (frameLayout != null) frameLayout.removeAllViews();
        switch (v.getId()) {
            case R.id.account_settings_view:
                frameLayout.addView(accountSettings);
                break;
            case R.id.general_settings_view:
                frameLayout.addView(generalSettings);
                break;
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                v.setBackgroundResource(R.drawable.recycle_item_backgroud);
                ((TextView) v).setTextColor(this
                        .getResources().getColor(R.color.fancy_purple));
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                v.setBackgroundResource(0);
                ((TextView) v).setTextColor(this
                        .getResources().getColor(R.color.fancy_left_gray));
                break;
        }
        return false;
    }
}
