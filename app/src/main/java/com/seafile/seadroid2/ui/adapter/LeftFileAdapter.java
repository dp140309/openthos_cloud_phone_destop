package com.seafile.seadroid2.ui.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.ui.activity.BrowserActivity;

import java.util.ArrayList;
import java.util.List;

public class LeftFileAdapter extends BaseAdapter implements View.OnTouchListener, View.OnHoverListener {

    private BrowserActivity mActivity;
    private ArrayList<SeafItem> items;
    private AdapterCallback adapterCallback;
    private SparseBooleanArray mLeftFileSelectedItemsIds;

    private LayoutInflater Inflater;

    public void add(SeafItem entry) {
        items.add(entry);
    }

    public void notifyChanged() {
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
    }

    public LeftFileAdapter(BrowserActivity context) {
        this.mActivity = context;
        if (items == null) items = new ArrayList<>();
        Inflater = LayoutInflater.from(context);
        mLeftFileSelectedItemsIds = new SparseBooleanArray();
    }

    private String countFilesNumber(int position) {
        SeafRepo name = (SeafRepo) items.get(position);
        List<SeafDirent> dirents = mActivity.getDataManager().getCachedDirents(name.id, "/");
        if (dirents == null) return "";
        return dirents.size() + "";
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftFileHolder holder;
        if (convertView == null) {
            convertView = Inflater.inflate(R.layout.left_file_list_layout, parent, false);
            RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.item_list_left);
            TextView textView = (TextView) convertView.findViewById(R.id.left_text_item);
            TextView textViewSize = (TextView) convertView.findViewById(R.id.recycler_text_size);
            holder = new LeftFileHolder(relativeLayout, textView, textViewSize);
            convertView.setTag(holder);
        }
        holder = (LeftFileHolder) convertView.getTag();
        holder.mTextView.setTag(position);
        holder.mTextView.setText(items.get(position).getTitle());
        holder.mTextViewSize.setText(countFilesNumber(position));
        holder.mRelativeLayout.setOnTouchListener(this);
        holder.mRelativeLayout.setOnHoverListener(this);

        if (mLeftFileSelectedItemsIds.get(position)) {
            holder.mRelativeLayout.setBackgroundResource(R.drawable.left_list_item_background);
            holder.mTextView.setTextColor(mActivity
                    .getResources().getColor(R.color.fancy_purple));
        } else {
            holder.mRelativeLayout.setBackgroundResource(0);
            holder.mTextView.setTextColor(mActivity
                    .getResources().getColor(R.color.fancy_left_gray));
        }

        return convertView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LeftFileHolder holder = (LeftFileHolder) v.getTag();
        int postion = (int) holder.mTextView.getTag();
        SeafItem mi = items.get(postion);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLeftFileSelectedItemsIds.clear();
            toggleSelection(postion);
            adapterCallback.onTunchListener(mi);
        }

        if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
            adapterCallback.onRecycleRightMouseClick(
                    (int) event.getRawX(),
                    (int) event.getRawY(),
                    mi);
        }
        return false;
    }


    @Override
    public boolean onHover(View v, MotionEvent event) {
        LeftFileHolder holder = (LeftFileHolder) v.getTag();
        int postion = (int) holder.mTextView.getTag();
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                holder.mRelativeLayout.setBackgroundResource(R.drawable.left_list_item_background);
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                changeItemState(postion, holder);
                break;
        }
        return false;
    }

    private void changeItemState(int postion, LeftFileHolder viewHolder) {
        if (mLeftFileSelectedItemsIds.get(postion)) {
            viewHolder.mRelativeLayout.setBackgroundResource(R.drawable.left_list_item_background);
        } else {
            viewHolder.mRelativeLayout.setBackgroundResource(0);
        }
    }

    public void toggleSelection(int position) {
        if (mLeftFileSelectedItemsIds.get(position)) {
            mLeftFileSelectedItemsIds.delete(position);
        } else {
            mLeftFileSelectedItemsIds.put(position, true);
        }
        notifyDataSetChanged();
    }

    public interface AdapterCallback {
        void onRecycleRightMouseClick(int x, int y, SeafItem position);

        void onTunchListener(SeafItem position);
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = (AdapterCallback) adapterCallback;
    }

    public class LeftFileHolder {
        RelativeLayout mRelativeLayout;
        TextView mTextView;
        TextView mTextViewSize;

        public LeftFileHolder(RelativeLayout relativeLayout, TextView textView, TextView textViewSize) {
            super();
            this.mRelativeLayout = relativeLayout;
            this.mTextView = textView;
            this.mTextViewSize = textViewSize;
        }
    }
}
