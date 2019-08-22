package com.seafile.seadroid2.ui.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.transfer.TransferTaskInfo;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for land transmission
 */
public class TransmissionAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String DEBUG_TAG = "TransmissionAdapter";

    private BrowserActivity mActivity;
    private List<SeafDirent> mList;
    private List<? extends TransferTaskInfo> mTransferTaskInfos;
    private int progressNumber;
    private int taskID = -1;
    private ViewHolder viewHolder;
    private final Handler mTimer = new Handler();


    public  TransmissionAdapter(BrowserActivity activity) {
        this.mActivity = activity;
        if (mList == null){
            mList = new ArrayList<>();
        }
    }

    public void startTimer() {
        Log.d(DEBUG_TAG, "timer started");
        mTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mActivity.isLandPattern) {
                    mTimer.removeCallbacksAndMessages(null);
                    return;
                }

                if (taskID == 0) {
                    mTimer.removeCallbacksAndMessages(null);
                    return;
                }
                DownloadTaskInfo downloadTaskInfo = mActivity.getTransferService().getDownloadTaskInfo(taskID);
                updateProgress(downloadTaskInfo.fileSize, downloadTaskInfo.finished);
                Log.i(DEBUG_TAG, "timer post refresh signal" + System.currentTimeMillis());
                mTimer.postDelayed(this, 1 * 1000);
            }
        }, 1 * 1000);
    }

    private void updateProgress(long fileSize, long finished) {
        viewHolder.mProgressBar .setIndeterminate(false);
        int percent;
        if (fileSize == 0) {
            percent = 100;
        } else {
            percent = (int)(finished * 100 / fileSize);
        }
        viewHolder.mProgressBar.setProgress(percent);

        String txt = Utils.readableFileSize(finished) + " / " + Utils.readableFileSize(fileSize);
        viewHolder.mTransime.setText(txt);
        notifyDataSetChanged();
    }

    public void add(SeafDirent entry) {
        mList.add(entry);
        String filePath = Utils.pathJoin(mActivity.getNavContext().getDirPath(), mList.get(0).name);
        taskID = mActivity.getTransferService().addDownloadTask(mActivity.getAccount(),
                mActivity.getNavContext().getRepoName(),
                mActivity.getNavContext().getRepoID(),filePath);
        startTimer();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transmission_item_list, null);
            ImageView mTransIcon =  (ImageView) view.findViewById(R.id.transmission_icon);
            ImageView mTransDelete =(ImageView)  view.findViewById(R.id.transmission_delete);
            ImageView mTransPause = (ImageView) view.findViewById(R.id.transmission_pause);
            ImageView mTransFile = (ImageView)  view.findViewById(R.id.transmission_file);
            TextView mTransName = (TextView) view.findViewById(R.id.transmission_name);
            TextView mTransime = (TextView) view.findViewById(R.id.transmission_time);
            ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.transmission_progress);
            viewHolder = new ViewHolder(mTransIcon, mTransDelete,mTransPause, mTransFile,
                                         mTransName, mTransime, mProgressBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTransIcon.setImageResource(mList.get(position).getIcon());
        viewHolder.mTransName.setText(mList.get(position).name);
        viewHolder.mTransDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                mTimer.removeCallbacksAndMessages(null);
                mActivity.getTransferService().cancelDownloadTask(taskID);
                mActivity.getTransferService().cancelNotification();
                notifyDataSetChanged();
            }
        });
        viewHolder.mTransPause.setOnClickListener(this);
        viewHolder.mTransFile.setOnClickListener(this);

        return view;
    }

    private int flag = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transmission_pause:
                switch (flag){
                    case 0:
                        v.setActivated(true);
                        flag = 1;
                        transferPause();
                        break;
                    case 1:
                        v.setActivated(false);
                        flag = 0;
                        transferPlay();
                        break;
                }
                break;

            case R.id.transmission_delete:
//                deleteProgressBar((Integer) v.getTag());
                mActivity.showShortToast(mActivity,"COMING SONG DELETE");
                break;

            case R.id.transmission_file:
                try {
                    URL uri = new URL("www.baidu.com");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                FilePath();
                mActivity.showShortToast(mActivity,"COMING SONG FILE");
                break;
        }
    }

    private void transferPause(){
        mActivity.showShortToast(mActivity,"COMING SONG PAUSE");

    }

    private void transferPlay(){
        mActivity.showShortToast(mActivity,"COMING SONG play");

    }

    private class ViewHolder {
        private ImageView mTransIcon, mTransDelete, mTransPause, mTransFile;
        private TextView mTransName, mTransime;
        private ProgressBar mProgressBar;

        public ViewHolder(ImageView mTransIcon, ImageView mTransDelete, ImageView mTransPause,
                          ImageView mTransFile, TextView mTransName, TextView mTransime,
                          ProgressBar mProgressBar){
            super();
            this.mTransIcon = mTransIcon;
            this.mTransDelete = mTransDelete;
            this.mTransPause = mTransPause;
            this.mTransFile = mTransFile;
            this.mTransName = mTransName;
            this.mTransime = mTransime;
            this.mProgressBar = mProgressBar;
        }
    }

    public void updateProgressBar(int a){
//        ViewHolder.mProgressBar.setProgress(a);
        notifyDataSetChanged();
    }

    public void ProgressBarPause(int a){
        progressNumber = a;
    }

    public void deleteProgressBar(int a){
        mList.remove(a);
        notifyDataSetChanged();
    }

    public void FilePath(){}

    public void onTaskStart(){}

    public void onTaskStop(){
        if (mList.size() != 0 && mList.size() > 0){
            for (int i= 0; i<= mList.size()-1; i++ ){
                String filePath = Utils.pathJoin(mActivity.getNavContext().getDirPath(), mList.get(i).name);
                taskID = mActivity.getTransferService().addDownloadTask(mActivity.getAccount(),
                        mActivity.getNavContext().getRepoName(),
                        mActivity.getNavContext().getRepoID(),filePath);
                mActivity.getTransferService().cancelDownloadTask(taskID);
                mActivity.getTransferService().cancelNotification();
                notifyDataSetChanged();
            }
        }else {
            mActivity.getTransferService().cancelDownloadTask(taskID);
            mActivity.getTransferService().cancelNotification();
            notifyDataSetChanged();
        }
    }
}
