package com.seafile.seadroid2.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafCachedFile;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafGroup;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.ui.activity.BrowserActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.RightHolder> implements View.OnTouchListener {

    private Context mContext;
    private LayoutInflater Inflater;
    private ArrayList<SeafItem> items;
    private List<DownloadTaskInfo> mDownloadTaskInfos;
    private BrowserActivity mActivity;
    private AdapterCallback adapterCallback;

    /* 点击的次数 */
    private int mClickcount, mDownX, mDownY, mUpX, mUpY, viewType;
    private long mLastDownTime, mLastUpTime, mFirstTime, mLastTime;

    private boolean isDoubleClick = false;
    private long MAX_LONG_PRESS_TIME = 3000;
    private int mItemPostion = -1;
    private int MAX_MOVE_FOR_CLICK = 50;

    private String mFirstName = null;

    /**
     * sort files type
     */
    public static final int SORT_BY_NAME = 9;
    /**
     * sort files type
     */
    public static final int SORT_BY_LAST_MODIFIED_TIME = 10;
    /**
     * sort files order
     */
    public static final int SORT_ORDER_ASCENDING = 11;
    /**
     * sort files order
     */
    public static final int SORT_ORDER_DESCENDING = 12;


    public RecycleViewAdapter(BrowserActivity context, int type) {
        this.mActivity = context;
        this.viewType = type;
        if (items == null) items = new ArrayList<>();
        Inflater = LayoutInflater.from(context);
    }

    public void add(SeafItem entry) {
        items.add(entry);
    }

    public int getItemPostion(int number){
        mItemPostion = number;
        notifyDataSetChanged();
        return mItemPostion;
    }

    public void removePersonalData() {
        if (items.get(0).getTitle().equals("个人")) {
            items.remove(0);
            notifyItemRemoved(0);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @NonNull
    @Override
    public RightHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = Inflater.inflate(getLayout(), parent, false);
        RightHolder holder = new RightHolder(view);
        view.setOnFocusChangeListener(new RecycleFocusChange());
        holder.mRelativeLayout.setOnTouchListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RightHolder holder, int position) {

        holder.mTextView.setText(items.get(position).getTitle());
        SeafItem seafile = items.get(position);
        holder.mRelativeLayout.setTag(seafile);
        holder.mTextView.setTag(position);

        if (isLeftRecycle(viewType)) {
            if (position == mItemPostion) {
                holder.mRelativeLayout.setBackgroundResource(R.drawable.recycle_item_backgroud);
                holder.mTextView.setTextColor(mActivity
                        .getResources().getColor(R.color.fancy_purple));
            } else {
                holder.mRelativeLayout.setBackgroundResource(0);
                holder.mTextView.setTextColor(mActivity
                        .getResources().getColor(R.color.fancy_left_gray));
            }
        } else {
            if (position == mItemPostion) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
                holder.mCheckBox.setChecked(true);
                holder.mRelativeLayout.setBackgroundResource(R.drawable.right_view_background);
            } else {
                holder.mCheckBox.setVisibility(View.GONE);
                holder.mCheckBox.setChecked(false);
                holder.mRelativeLayout.setBackgroundResource(0);
            }
            holder.mViewIcon.setImageResource(items.get(position).getIcon());
        }
    }

    class RightHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mTextViewSize;
        ImageView mViewIcon;
        RelativeLayout mRelativeLayout;
        CheckBox mCheckBox;

        public RightHolder(View itemView) {

            super(itemView);
            if (isLeftRecycle(viewType)) {
                mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_list_left);
                mTextView = (TextView) itemView.findViewById(R.id.left_text_item);
                mTextViewSize = (TextView) itemView.findViewById(R.id.recycler_text_size);
                mRelativeLayout.setOnHoverListener(new View.OnHoverListener() {
                    @Override
                    public boolean onHover(View v, MotionEvent event) {
                        if (mItemPostion != getPosition()) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_HOVER_ENTER:
                                    mRelativeLayout.setBackgroundResource(R.drawable.recycle_item_backgroud);
                                    mTextView.setTextColor(mActivity
                                            .getResources().getColor(R.color.fancy_purple));
                                    break;
                                case MotionEvent.ACTION_HOVER_EXIT:
                                    mRelativeLayout.setBackgroundResource(0);
                                    mTextView.setTextColor(mActivity
                                            .getResources().getColor(R.color.fancy_left_gray));
                                    break;
                            }
                        }
                        return false;
                    }
                });
            } else {
                mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_list_right);
                mTextView = (TextView) itemView.findViewById(R.id.right_text_item);
                mViewIcon = (ImageView) itemView.findViewById(R.id.recycler_image_item);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.right_view_checkbox);
            }
        }
    }


    /**
     * view type 1 return true
     */
    private boolean isLeftRecycle(int type) {
        return type == 1 ? true : false;
    }

    /**
     * view type 1 or 2
     * 1 is left item
     * 2 is right item
     */
    private int getLayout() {
        return isLeftRecycle(viewType) ? R.layout.recycler_left_item : R.layout.recycler_right_item;
    }

    private boolean equalLists(List<DownloadTaskInfo> newList, List<DownloadTaskInfo> oldList) {
        if (newList == null && oldList == null) return true;
        if ((newList == null && oldList != null)
                || newList != null && oldList == null
                || newList.size() != oldList.size()) return false;
        return newList.equals(oldList);
    }
    public interface AdapterCallback {
        void onRecycleRightMouseClick(int x, int y, SeafItem position);

        void onTunchListener(SeafItem position);
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = (AdapterCallback) adapterCallback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        RightHolder mHolder = new RightHolder(v);
        int postion = (int) mHolder.mTextView.getTag();
        SeafItem mi = (SeafItem) v.getTag();
        ArrayList<SeafDirent> sd = mActivity.getSeafDirent();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastDownTime = System.currentTimeMillis();
            mDownX = (int) event.getX();
            mDownY = (int) event.getY();
            mClickcount++;

            if (isLeftRecycle(viewType)) {
                setSelectPosition(postion);
                adapterCallback.onTunchListener((SeafItem) v.getTag());
            } else {
                SeafDirent dirent = (SeafDirent) mi;
                if (sd.size() == 0){
                    sd.add(dirent);
                    Log.i("i--","---sd is null----"+sd.get(0).id);
                }else {
                    sd.clear();
                    sd.add(dirent);
                    Log.i("i--","---sd is ----"+sd.get(0).id);
                }
//                String currentPath = mActivity.getNavContext().getDirPath();
//                String newPath = currentPath.endsWith("/") ?
//                        currentPath + dirent.name : currentPath + "/" + dirent.name;

                setSelectPosition(postion);

                if (mClickcount == 1) {
                    mFirstTime = System.currentTimeMillis();
                }

                if (mFirstName == null){
                    mFirstName = mi.getTitle();
                }else {
                    if (!mFirstName .equals(mi.getTitle())) {
                        mFirstName = mi.getTitle();
                        mClickcount = 1;
                    }
                }

                if (mClickcount == 2 && mFirstName.equals(mi.getTitle()) ) {
                    mLastTime = System.currentTimeMillis();
                    long time =mLastTime - mFirstTime;
                    if (time <= MAX_LONG_PRESS_TIME) {
                        adapterCallback.onTunchListener((SeafItem) v.getTag());
                        mItemPostion = -1;
                        mActivity.getSeafDirent().clear();
                        notifyDataSetChanged();
                    }
                    mClickcount = 0 ;
                }

            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mLastUpTime = System.currentTimeMillis();
            mUpX = (int) event.getX();
            mUpY = (int) event.getY();
            int mx = Math.abs(mUpX - mDownX);
            int my = Math.abs(mUpY - mDownY);

//            if (mx <= MAX_MOVE_FOR_CLICK && my <= MAX_MOVE_FOR_CLICK) {
//
//            } else { // 移动
//                mClickcount = 0;
//            }
        }

        if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
            adapterCallback.onRecycleRightMouseClick(
                    (int) event.getRawX(),
                    (int) event.getRawY(),
                    (SeafItem) v.getTag());
        }
        return false;
    }

    public void setSelectPosition(int position) {
        if (!(position < 0 || position > items.size())) {
            mItemPostion = position;
            notifyDataSetChanged();
        }
    }

    class RecycleFocusChange implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.left_list_item_background);
            } else {
                v.setBackgroundResource(0);
            }
        }
    }


    /**
     * Sorts the given list by type of {@link #SORT_BY_NAME} or {@link #SORT_BY_LAST_MODIFIED_TIME},
     * and by order of {@link #SORT_ORDER_ASCENDING} or {@link #SORT_ORDER_DESCENDING}
     */
    public void sortFiles(int type, int order) {
        List<SeafGroup> groups = Lists.newArrayList();
        List<SeafCachedFile> cachedFiles = Lists.newArrayList();
        List<SeafDirent> folders = Lists.newArrayList();
        List<SeafDirent> files = Lists.newArrayList();

        SeafGroup group = null;

        for (SeafItem item : items) {
            if (item instanceof SeafGroup) {
                group = (SeafGroup) item;
                groups.add(group);
            } else if (item instanceof SeafRepo) {
                if (group == null)
                    continue;
                group.addIfAbsent((SeafRepo) item);
            } else if (item instanceof SeafCachedFile) {
                cachedFiles.add(((SeafCachedFile) item));
            } else {
                if (((SeafDirent) item).isDir())
                    folders.add(((SeafDirent) item));
                else
                    files.add(((SeafDirent) item));
            }
        }
        items.clear();

        // sort SeafGroups and SeafRepos
        for (SeafGroup sg : groups) {
            sg.sortByType(type, order);
            items.add(sg);
            items.addAll(sg.getRepos());
        }

        // sort SeafDirents
        if (type == SORT_BY_NAME) {
            // sort by name, in ascending order
            Collections.sort(folders, new SeafDirent.DirentNameComparator());
            Collections.sort(files, new SeafDirent.DirentNameComparator());
            if (order == SORT_ORDER_DESCENDING) {
                Collections.reverse(folders);
                Collections.reverse(files);
            }
        } else if (type == SORT_BY_LAST_MODIFIED_TIME) {
            // sort by last modified time, in ascending order
            Collections.sort(folders, new SeafDirent.DirentLastMTimeComparator());
            Collections.sort(files, new SeafDirent.DirentLastMTimeComparator());
            if (order == SORT_ORDER_DESCENDING) {
                Collections.reverse(folders);
                Collections.reverse(files);
            }
        }
        // Adds the objects in the specified collection to this ArrayList
        items.addAll(cachedFiles);
        items.addAll(folders);
        items.addAll(files);
    }

    public void setDownloadTaskList(List<DownloadTaskInfo> newList) {
        if (!equalLists(newList, mDownloadTaskInfos)) {
            this.mDownloadTaskInfos = newList;
            // redraw the list
            notifyDataSetChanged();
        }
    }

}
