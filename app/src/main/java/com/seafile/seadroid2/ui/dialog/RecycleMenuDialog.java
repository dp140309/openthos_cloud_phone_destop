package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.ui.ClickListener.OnMenuClick;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecycleMenuDialog extends Dialog implements AdapterView.OnItemClickListener {

    private static RecycleMenuDialog mRecycleMenuDialog;
    protected static Point mPoint;

    private List<String> mDatas;
    private SeafItem mSeafItem;
    private MenuDialogAdapter mAdapter;
    private int mWidth, mHeight, viewType, mStatusBarHeight;
    private ListView mLeftView;

    private OnMenuClick mOnMenuClick;

    public static RecycleMenuDialog getInstance(Context context) {
        if (mRecycleMenuDialog == null) {
            mRecycleMenuDialog = new RecycleMenuDialog(context);
        }
        return mRecycleMenuDialog;
    }

    public RecycleMenuDialog(@NonNull Context context) {
        super(context);
        create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recycle_item_right_menu);

        if (mPoint == null) {
            Display defaultDisplay = ((WindowManager)
                    getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            mPoint = new Point();
            defaultDisplay.getRealSize(mPoint);
        }
        creatView();
        createDate();
    }

    private void creatView() {
        mLeftView = (ListView) findViewById(R.id.left_view);
        mLeftView.setOnItemClickListener(this);
    }

    private void createDate() {
        mDatas = new ArrayList<>();
        mAdapter = new MenuDialogAdapter();
        mLeftView.setAdapter(mAdapter);
    }

    public void setOnMenuClick(OnMenuClick menuClick) {
        mOnMenuClick = menuClick;
    }

    public void show(int type, int x, int y, SeafItem postion) {
        mSeafItem = postion;
        viewType = type;

        Window dialogWindow = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        else
            dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.format = PixelFormat.TRANSPARENT;
        lp.dimAmount = 0;
        prepareData(type);
        lp.x = x;
        if (y + mHeight + mStatusBarHeight < mPoint.y) lp.y = y;
        else lp.y = y - mHeight;

        dialogWindow.setAttributes(lp);
        show();
    }

    private void prepareData(int type) {
        if (mDatas != null) mDatas.clear();
//        mDatas.clear();
        String[] sArr = null;
        switch (type) {
            case 1:
                sArr = getContext().getResources().getStringArray(R.array.left_item_menu);
                break;
            case 2:
                sArr = getContext().getResources().getStringArray(R.array.right_item_menu);
                break;
            case 3:
                sArr = getContext().getResources().getStringArray(R.array.account_button_menu);
                break;
        }

        mDatas.addAll(Arrays.asList(sArr));
        mAdapter.notifyDataSetChanged();
        mWidth = 0;
        mHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i, null, null);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mWidth = Math.max(view.getMeasuredWidth(), mWidth);
            mHeight = mHeight + view.getMeasuredHeight();
        }

        mLeftView.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class MenuDialogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                Log.i("menudialog", "menu dialog is runing---------------5");
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.recycle_left_menu_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = mDatas.get(position);

            holder.name.setEnabled(true);
            holder.name.setTextColor(
                    getContext().getResources().getColor(android.R.color.black));

            holder.name.setText(name);
            return convertView;

        }


    }

    private class ViewHolder implements View.OnHoverListener, View.OnClickListener {
        private LinearLayout mItemMenuView;
        private TextView name;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.left_text_item);
            mItemMenuView = (LinearLayout) view.findViewById(R.id.item_menu_view);
            name.setOnHoverListener(this);
            name.setOnClickListener(this);
        }

        @Override
        public boolean onHover(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    mItemMenuView.setBackgroundResource(R.drawable.right_view_background);
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    mItemMenuView.setBackgroundResource(0);
                    break;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mOnMenuClick != null) {
                mOnMenuClick.menuClick(v, mRecycleMenuDialog, mSeafItem, ((TextView) v).getText().toString(),viewType);
            }

        }
    }
}
