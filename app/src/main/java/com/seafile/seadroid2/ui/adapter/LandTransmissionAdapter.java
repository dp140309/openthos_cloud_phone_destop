package com.seafile.seadroid2.ui.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.transfer.TransferTaskInfo;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.util.Utils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.LongFunction;

public class LandTransmissionAdapter extends BaseAdapter {

    private static final String DEBUG_TAG = "LandTransmissionAdapter";

    private List<? extends TransferTaskInfo> mTransferTaskInfos;
    private List<Integer> mSelectedItemsPositions = Lists.newArrayList();
    private BrowserActivity mActivity;
    private int flag = 0;

    public LandTransmissionAdapter(BrowserActivity activity, List<? extends TransferTaskInfo> transferTaskInfos){
        this.mActivity = activity;
        this.mTransferTaskInfos = transferTaskInfos;

    }

    @Override
    public int getCount() {
        return mTransferTaskInfos.size();
    }

    @Override
    public TransferTaskInfo getItem(int position) {
        return mTransferTaskInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transmission_item_list, null);
            ImageView mTransIcon =  (ImageView) view.findViewById(R.id.transmission_icon);
            ImageView mTransDelete =(ImageView)  view.findViewById(R.id.transmission_delete);
            ImageView mTransPause = (ImageView) view.findViewById(R.id.transmission_pause);
            ImageView mTransFile = (ImageView)  view.findViewById(R.id.transmission_file);
            TextView mTransName = (TextView) view.findViewById(R.id.transmission_name);
            TextView mTransime = (TextView) view.findViewById(R.id.transmission_time);
            TextView state = (TextView) view.findViewById(R.id.transfer_land_state);
            ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.transmission_progress);
            viewHolder = new ViewHolder(mTransIcon, mTransDelete,mTransPause, mTransFile,
                    mTransName, mTransime, mProgressBar, state);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final DownloadTaskInfo taskInfo = (DownloadTaskInfo) mTransferTaskInfos.get(position);
        viewHolder.mTransIcon.setImageResource(Utils.getFileIcon(taskInfo.pathInRepo));
        viewHolder.mTransName.setText(Utils.fileNameFromPath(taskInfo.pathInRepo));
        updateTaskView(taskInfo,viewHolder);
        viewHolder.mTransDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> ids = Lists.newArrayList();
                TransferTaskInfo tti = getItem(position);
                ids.add(tti.taskID);
                if (mActivity.getTransferService() != null) {
                    mActivity.getTransferService().removeDownloadTasksByIds(ids);
                }

                notifyDataSetChanged();
            }
        });

        viewHolder.mTransFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parentFlie = new File(mActivity.getExternalCacheDir() + mActivity.getNavContext().getDirPath());
                Log.i("----",mActivity.getExternalCacheDir()+"---"+mActivity.getNavContext().getDirPath()+"<<---0-0-0-0--->>"+mTransferTaskInfos.get(position).localFilePath);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(Uri.fromFile(parentFlie), "*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                mActivity.startActivity(intent);
            }
        });

        viewHolder.mTransPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag){
                    case 0:
                        v.setActivated(true);
                        flag = 1;
                        transferPause(position);
                        break;
                    case 1:
                        v.setActivated(false);
                        flag = 0;
                        transferPlay(position);
                        break;
                }
            }
        });

        return view;
    }

    private void transferPause(int postion){
        mActivity.showShortToast(mActivity,"COMING SONG PAUSE");

    }

    private void transferPlay(int postion){
        mActivity.showShortToast(mActivity,"COMING SONG play");

    }

    public List<Integer> getSelectedIds() {
        mTransferTaskInfos = mActivity.getTransferService().getAllDownloadTaskInfos();
        if (mSelectedItemsPositions != null) mSelectedItemsPositions.clear();
        for (int i = 0; i < mTransferTaskInfos.size(); i++) {
//            mSelectedItemsIds.put(i, true);
            mSelectedItemsPositions.add(i);
        }
        Log.i("--set--",mSelectedItemsPositions+"---0-0-0-0-0-0-");
        return mSelectedItemsPositions;
    }

    private List<Integer> convertToTaskIds(List<Integer> positions) {
        List<Integer> taskIds = Lists.newArrayList();
        for (int position : positions) {
            TransferTaskInfo tti = getItem(position);
            taskIds.add(tti.taskID);
        }

        return taskIds;
    }

    private void updateTaskView(TransferTaskInfo info, ViewHolder viewHolder) {
        String stateStr = "";
        int stateColor = R.color.light_black;
        long totalSize = 0l;
        long transferedSize = 0l;

        DownloadTaskInfo dti = (DownloadTaskInfo) info;
        totalSize = dti.fileSize;
        transferedSize = dti.finished;

        String sizeStr = Utils.readableFileSize(totalSize).toString();

        switch (info.state) {
            case INIT:
                stateStr = mActivity.getString(R.string.download_waiting);

                viewHolder.mTransime.setVisibility(View.INVISIBLE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                break;
            case TRANSFERRING:
                int percent;
                if (totalSize == 0)
                    percent = 0;
                else
                    percent = (int) (transferedSize * 100 / totalSize);

                viewHolder.mProgressBar.setProgress(percent);
                sizeStr = String.format("%s / %s",
                        Utils.readableFileSize(transferedSize),
                        Utils.readableFileSize(totalSize));
                viewHolder.mTransime.setVisibility(View.VISIBLE);
                viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                break;
            case FINISHED:
//                if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.DOWNLOAD_TASK))
                    stateStr = mActivity.getString(R.string.download_finished);
//                else if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.UPLOAD_TASK))
//                    stateStr = mActivity.getString(R.string.upload_finished);
                stateColor = Color.BLACK;
                viewHolder.mTransime.setVisibility(View.VISIBLE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                break;
            case CANCELLED:
//                if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.DOWNLOAD_TASK))
                    stateStr = mActivity.getString(R.string.download_cancelled);
//                else if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.UPLOAD_TASK))
//                    stateStr = mActivity.getString(R.string.upload_cancelled);
                stateColor = Color.RED;
                viewHolder.mTransime.setVisibility(View.INVISIBLE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                break;
            case FAILED:
//                if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.DOWNLOAD_TASK))
                    stateStr = mActivity.getString(R.string.download_failed);
//                else if (mTransferTaskType.equals(TransferTaskAdapter.TaskType.UPLOAD_TASK))
//                    stateStr = mActivity.getString(R.string.upload_failed);
                stateColor = Color.RED;
                viewHolder.mTransime.setVisibility(View.INVISIBLE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                break;
        }
        viewHolder.mTransime.setText(sizeStr);
        viewHolder.state.setText(stateStr);
        viewHolder.state.setTextColor(stateColor);
    }

    public void setTransferTaskInfos(List<? extends TransferTaskInfo> infos) {
        mTransferTaskInfos = infos;
        Collections.sort(mTransferTaskInfos, new TaskInfoComparator());
    }

    private class TaskInfoComparator implements Comparator<TransferTaskInfo> {
        private int taskStateToInteger(TransferTaskInfo info) {
            switch (info.state) {
                case TRANSFERRING:
                    return 0;
                case INIT:
                    return 1;
                case CANCELLED:
                    return 2;
                case FAILED:
                    return 3;
                case FINISHED:
                    return 4;
            }

            return 0;
        }

        @Override
        public int compare(TransferTaskInfo infoA, TransferTaskInfo infoB) {
            // sort task list, transferring < init < cancelled < failed <  finished
            return taskStateToInteger(infoA) - taskStateToInteger(infoB);
        }
    }

    private class ViewHolder {
        private ImageView mTransIcon, mTransDelete, mTransPause, mTransFile;
        private TextView mTransName, mTransime, state;
        private ProgressBar mProgressBar;

        public ViewHolder(ImageView mTransIcon, ImageView mTransDelete, ImageView mTransPause,
                          ImageView mTransFile, TextView mTransName, TextView mTransime,
                          ProgressBar mProgressBar, TextView state){
            super();
            this.mTransIcon = mTransIcon;
            this.mTransDelete = mTransDelete;
            this.mTransPause = mTransPause;
            this.mTransFile = mTransFile;
            this.mTransName = mTransName;
            this.mTransime = mTransime;
            this.mProgressBar = mProgressBar;
            this.state = state;
        }
    }


//    public void onTaskStop(){
//        if (mTransferTaskInfos.size() != 0 && mTransferTaskInfos.size() > 0){
//            for (int i= 0; i<= mTransferTaskInfos.size()-1; i++ ){
//                String filePath = Utils.pathJoin(mActivity.getNavContext().getDirPath(), mTransferTaskInfos.get(i).name);
//                taskID = mActivity.getTransferService().addDownloadTask(mActivity.getAccount(),
//                        mActivity.getNavContext().getRepoName(),
//                        mActivity.getNavContext().getRepoID(),filePath);
//                mActivity.getTransferService().cancelDownloadTask(taskID);
//                mActivity.getTransferService().cancelNotification();
//                notifyDataSetChanged();
//            }
//        }else {
//            mActivity.getTransferService().cancelDownloadTask(taskID);
//            mActivity.getTransferService().cancelNotification();
//            notifyDataSetChanged();
//        }
//    }
}
