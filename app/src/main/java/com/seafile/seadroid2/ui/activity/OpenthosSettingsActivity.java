package com.seafile.seadroid2.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seafile.seadroid2.R;

public class OpenthosSettingsActivity extends BaseActivity implements View.OnClickListener, View.OnHoverListener {

    private FrameLayout frameLayout;
    private View accountSettings, generalSettings;
    private TextView accountButton, generalButton;
    private LinearLayout mAccountView, mGeneralView;
    private LayoutInflater Inflater;
    private TextView maText, GSText, mGeneraText;
    private String account,server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Inflater = LayoutInflater.from(this);
        account = this.getIntent().getExtras().getString("account");
        server = this.getIntent().getExtras().getString("server");
        initView();
        initData();
    }

    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.settings_item_fragment);
        mAccountView = findViewById(R.id.account_settings_view);
        mGeneralView = findViewById(R.id.general_settings_view);
        accountButton = (TextView) findViewById(R.id.account_settings_text);
        generalButton = (TextView) findViewById(R.id.general_settings_text);
        accountSettings = Inflater.inflate(R.layout.account_settings, null, false);
        generalSettings = Inflater.inflate(R.layout.general_settings, null, false);

        maText = accountSettings.findViewById(R.id.account_text);
        maText.setText(account);
        accountSettings.findViewById(R.id.account_modify).setOnClickListener(this);

        accountSettings.findViewById(R.id.account_text).setOnClickListener(this);
        GSText = generalSettings.findViewById(R.id.general_service_text);
        GSText.setText(server);
//        generalSettings.findViewById(R.id.general_service_spinner).setOnClickListener(this);
        mGeneraText = generalSettings.findViewById(R.id.general_path_text);
        mGeneraText.setText(getExternalCacheDir().toString());
        generalSettings.findViewById(R.id.general_path_button).setOnClickListener(this);


    }

    private void initData() {
        if (frameLayout != null) frameLayout.removeAllViews();

        frameLayout.addView(accountSettings);
        mAccountView.setOnClickListener(this);
        mGeneralView.setOnClickListener(this);
        mAccountView.setOnHoverListener(this);
        mGeneralView.setOnHoverListener(this);
        GSText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_settings_view:
                if (frameLayout != null) frameLayout.removeAllViews();
                frameLayout.addView(accountSettings);
                changeViewSelected(1);
                break;
            case R.id.general_settings_view:
                if (frameLayout != null) frameLayout.removeAllViews();
                frameLayout.addView(generalSettings);
                changeViewSelected(2);
                break;

            case R.id.account_text:

                break;

            case R.id.account_modify:
                Toast.makeText(OpenthosSettingsActivity.this, "暂不支持修改", Toast.LENGTH_LONG).show();
                break;

            case R.id.general_service_text:
                break;

//            case R.id.general_service_spinner:
//                break;

            case R.id.general_path_text:
                break;

            case R.id.general_path_button:
                Toast.makeText(OpenthosSettingsActivity.this, "COMING SOON", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                if (v.getId() == R.id.account_settings_view)
                    mAccountView.setBackgroundResource(R.drawable.recycle_item_backgroud);

                if (v.getId() == R.id.general_settings_view)
                    mGeneralView.setBackgroundResource(R.drawable.recycle_item_backgroud);
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                if (v.getId() == R.id.account_settings_view) mAccountView.setBackgroundResource(0);

                if (v.getId() == R.id.general_settings_view) mGeneralView.setBackgroundResource(0);
                break;
        }
        return false;
    }

    private void changeViewSelected(int type){
        if (type == 1){
            mAccountView.setBackgroundResource(R.drawable.recycle_item_backgroud);
            mGeneralView.setBackgroundResource(0);
            accountButton.setTextColor(this
                    .getResources().getColor(R.color.fancy_purple));
            generalButton.setTextColor(this
                    .getResources().getColor(R.color.fancy_left_gray));
        }

        if(type ==2){
            mGeneralView.setBackgroundResource(R.drawable.recycle_item_backgroud);
            mAccountView.setBackgroundResource(0);
            generalButton.setTextColor(this
                    .getResources().getColor(R.color.fancy_purple));
            accountButton.setTextColor(this
                    .getResources().getColor(R.color.fancy_left_gray));
        }
    }

}
