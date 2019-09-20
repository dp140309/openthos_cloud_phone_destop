package com.seafile.seadroid2.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.account.ui.AccountDetailActivity;
import com.seafile.seadroid2.account.ui.SeafileAuthenticatorActivity;
import com.seafile.seadroid2.gallery.Util;
import com.seafile.seadroid2.util.Utils;

import java.io.File;

public class OpenthosSettingsActivity extends BaseActivity implements View.OnClickListener, View.OnHoverListener {

    private FrameLayout frameLayout;
    private View accountSettings, generalSettings, modifyButton, confirmButton;
    private TextView accountButton, generalButton;
    private LinearLayout mAccountView, mGeneralView;
    private LayoutInflater Inflater;
    private TextView  GSText, mGeneraText;
    private EditText maText;
    private String email,server;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Inflater = LayoutInflater.from(this);
        accountManager = new AccountManager(this);
        email = this.getIntent().getExtras().getString("email");
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
        maText.setText(email);
        modifyButton = accountSettings.findViewById(R.id.account_modify);
        confirmButton = accountSettings.findViewById(R.id.account_confirm);

        accountSettings.findViewById(R.id.account_text).setOnClickListener(this);
        GSText = generalSettings.findViewById(R.id.general_service_text);
        GSText.setText(server);

        generalSettings.findViewById(R.id.general_service_view).setOnClickListener(this);
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
        modifyButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        maText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    maText.setFocusableInTouchMode(false);
                    maText.setBackgroundResource(0);
                    confirmButton.setVisibility(View.GONE);
                    modifyButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private int flag = 0;

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
                modiryAccount();
                break;
            case R.id.account_confirm:
                confirmAccountModify();
                break;
            case R.id.general_service_text:
                break;
            case R.id.general_service_view:
                switch (flag){
                    case 0:
                        v.setActivated(true);
                        flag = 1;
                        showView(v);
                        break;
                    case 1:
                        v.setActivated(false);
                        flag = 0;
                        closeView(v);
                        break;
                }
                break;
            case R.id.general_path_button:
                File parentFlie = new File(getExternalCacheDir() + mGeneraText.getText().toString());
                Intent intent = Utils.selectDifferentVersionIntent(this,parentFlie);
                startActivity(intent);
                break;
        }
    }

    private void modiryAccount(){
        modifyButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        maText.setSelection(email.length());
        maText.setFocusableInTouchMode(true);
        maText.setBackgroundResource(R.drawable.setting_background);
    }
    private void confirmAccountModify(){
        if (maText.getText().toString().equals(email)) {
            showLongToast(this,"未做修改");
            return;
        }
        modifyButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        maText.setText(email);
        maText.setFocusableInTouchMode(false);
        maText.setCursorVisible(false);
        maText.setBackgroundResource(0);
        showLongToast(this,"暂不支持用户名的修改");
    }

    PopupWindow popupWindow;
    private void showView(View v){
        View productListView = LayoutInflater.from(this).inflate(R.layout.setting_service_view, null);
        TextView serviceDev = productListView.findViewById(R.id.service_view_dev);
        TextView service185 = productListView.findViewById(R.id.service_view_185);
        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(GSText.getHeight()+ GSText.getHeight());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setContentView(productListView);
        popupWindow.showAsDropDown(GSText);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                v.setActivated(false);
            }
        });

        serviceDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GSText.setText(serviceDev.getText());
                accountManager.saveServiceUrl(serviceDev.getText().toString());
                Intent intent = new Intent(OpenthosSettingsActivity.this, AccountDetailActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
                OpenthosSettingsActivity.this.finish();
            }
        });

        service185.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GSText.setText(service185.getText());
                accountManager.saveServiceUrl(service185.getText().toString());
                Intent intent = new Intent(OpenthosSettingsActivity.this, AccountDetailActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
                OpenthosSettingsActivity.this.finish();
            }
        });
    }

    private void closeView(View v){
        popupWindow.dismiss();
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
