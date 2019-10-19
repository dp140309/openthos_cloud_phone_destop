package com.seafile.seadroid2.ui.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafCachedFile;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafGroup;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.ui.AnimateFirstDisplayListener;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileListAdapter extends BaseAdapter implements View.OnTouchListener {
    private LayoutInflater mInflater;
    private BrowserActivity mActivity;
    private ArrayList<SeafItem> items;
    private List<Integer> selectFileInfoListIndex = new ArrayList<>();
//    private View.OnTouchListener mOntouchListener;
    private AdapterCallback adapterCallback;
    /**
     * 点击的次数
     */
    private int mClickcount, mDownX, mDownY, mUpX, mUpY, viewType, mItemPostion = -1;
    private long mLastDownTime, mLastUpTime, mFirstTime, mLastTime;
    private long MAX_LONG_PRESS_TIME = 3000;
    private String mFirstName = null;


    public FileListAdapter(BrowserActivity activity) {
        mInflater = LayoutInflater.from(activity);
        if (items == null) items = new ArrayList<>();
        mActivity = activity;
//        initIconHolder();
    }

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

    /**
     * get left(1) and right(2) layout
     */
//    private int getLayout() { return R.layout.recycler_right_item; }
//    private boolean isLeftRecycle(int type) { return type == 1 ? true : false; }
    public void add(SeafItem entry) {
        items.add(entry);
    }

    public void notifyChanged() {
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
    }

    public List<SeafItem> getFileInfoList() {
        return items;
    }

    public List<Integer> getSelectFileInfoList() {
        return selectFileInfoListIndex;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recycler_right_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTextView.setTag(position);
        holder.mTextView.setText(items.get(position).getTitle());
//        holder.mTextViewSize.setText(countFilesNumber(position));
        holder.mRelativeLayout.setOnTouchListener(this);
        if (position == mItemPostion) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(true);
            holder.mRelativeLayout.setBackgroundResource(R.drawable.right_view_background);
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
            holder.mCheckBox.setChecked(false);
            holder.mRelativeLayout.setBackgroundResource(0);
        }

        SeafDirent dirent = (SeafDirent) items.get(position);
        if (dirent.isDir()) {
            holder.mViewIcon.setImageResource(items.get(position).getIcon());
        } else {
            updateRightItemPicture(holder, dirent);
        }

//        holder.mRelativeLayout.setOnTouchListener(mOntouchListener);
        return convertView;
    }

    private String countFilesNumber(int position) {
        SeafRepo name = (SeafRepo) items.get(position);
        List<SeafDirent> dirents = mActivity.getDataManager().getCachedDirents(name.id, "/");
        if (dirents == null) return "";
        return dirents.size() + "";
    }

    private void updateRightItemPicture(ViewHolder holder, SeafDirent dirent) {
        DataManager dataManager = mActivity.getDataManager();
        String repoName = mActivity.getNavContext().getRepoName();
        String repoID = mActivity.getNavContext().getRepoID();
        String filePath = Utils.pathJoin(mActivity.getNavContext().getDirPath(), dirent.name);
        File file = null;
        try {
            file = mActivity.getDataManager().getLocalRepoFile(repoName, repoID, filePath);
        } catch (RuntimeException e) {
            mActivity.showShortToast(mActivity, mActivity.getResources().getString(R.string.storage_space_insufficient));
            e.printStackTrace();
            return;
        }

        if (Utils.isViewableImage(file.getName())) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .extraForDownloader(dataManager.getAccount())
                    .delayBeforeLoading(500)
                    .resetViewBeforeLoading(true)
                    .showImageOnLoading(R.drawable.file_image)
                    .showImageForEmptyUri(R.drawable.file_image)
                    .showImageOnFail(R.drawable.file_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
            String url = dataManager.getThumbnailLink(repoName, repoID, filePath, getThumbnailWidth());
            if (url == null) {
                holder.mViewIcon.setImageResource(dirent.getIcon());
            } else {
                ImageLoader.getInstance().displayImage(url, holder.mViewIcon, options, animateFirstListener);
            }
        } else {
            holder.mViewIcon.setImageResource(dirent.getIcon());
        }
    }

    private int getThumbnailWidth() {
        return (int) SeadroidApplication.getAppContext().getResources().getDimension(R.dimen.lv_icon_width);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ViewHolder mHolder = new ViewHolder(v);
        int postion = (int) mHolder.mTextView.getTag();
        SeafItem mi = items.get(postion);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastDownTime = System.currentTimeMillis();
            mDownX = (int) event.getX();
            mDownY = (int) event.getY();
            mClickcount++;
            ArrayList<SeafDirent> sd = mActivity.getSeafDirent();
            SeafDirent dirent = (SeafDirent) mi;
            if (sd.size() == 0) {
                sd.add(dirent);
            } else {

                if (mActivity.KEYBOARD_CTRL) {
                    sd.add(dirent);
                } else {
                    sd.clear();
                    sd.add(dirent);
                }
            }

            setSelectPosition(postion);
            if (mClickcount == 1) {
                mFirstTime = System.currentTimeMillis();
            }

            if (mFirstName == null) {
                mFirstName = mi.getTitle();
            } else {
                if (!mFirstName.equals(mi.getTitle())) {
                    mFirstName = mi.getTitle();
                    mClickcount = 1;
                }
            }

            if (mClickcount == 2 && mFirstName.equals(mi.getTitle())) {
                mLastTime = System.currentTimeMillis();
                long time = mLastTime - mFirstTime;
                if (time <= MAX_LONG_PRESS_TIME) {
                    if(adapterCallback != null){
                        adapterCallback.onTunchListener(mi);
                    }
                    mItemPostion = -1;
                    mActivity.getSeafDirent().clear();
                    notifyDataSetChanged();
                }
                mClickcount = 0;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mLastUpTime = System.currentTimeMillis();
            mUpX = (int) event.getX();
            mUpY = (int) event.getY();
            int mx = Math.abs(mUpX - mDownX);
            int my = Math.abs(mUpY - mDownY);
        }

        if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
            if(adapterCallback != null){
                adapterCallback.onRecycleRightMouseClick(
                        (int) event.getRawX(),
                        (int) event.getRawY(),
                        mi);
            }
        }
        return false;
    }

    public class ViewHolder {
        RelativeLayout mRelativeLayout;
        TextView mTextView;
        ImageView mViewIcon;
        CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_list_right);
            mTextView = (TextView) itemView.findViewById(R.id.right_text_item);
            mViewIcon = (ImageView) itemView.findViewById(R.id.recycler_image_item);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.right_view_checkbox);
        }
    }

    public void setDownloadTaskList(List<DownloadTaskInfo> newList) {
//        if (!equalLists(newList, mDownloadTaskInfos)) {
//            this.mDownloadTaskInfos = newList;
//            notifyDataSetChanged();
//        }
    }

    public interface AdapterCallback {
        void onRecycleRightMouseClick(int x, int y, SeafItem position);
        void onTunchListener(SeafItem position);
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = (AdapterCallback) adapterCallback;
    }

    public void setSelectPosition(int position) {
        if (!(position < 0 || position > items.size())) {
            mItemPostion = position;
            notifyDataSetChanged();
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
//    public void dispose() {
//        if (mIconHolder != null) {
//            mIconHolder.cleanup();
//            mIconHolder = null;
//        }
//    }
}
