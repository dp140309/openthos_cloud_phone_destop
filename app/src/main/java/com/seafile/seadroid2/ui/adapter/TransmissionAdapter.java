package com.seafile.seadroid2.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransmissionAdapter extends BaseAdapter implements View.OnClickListener {

    private List<SeafDirent> mList = new ArrayList<>();
    private LayoutInflater Inflater;
    private ViewHolder holder;
    private Context mContext;


    public  TransmissionAdapter(List<SeafDirent> list, Context context) {
        Inflater = LayoutInflater.from(context);
        this.mList = list;
        this.mContext = context;
    }

    public void add(SeafDirent entry) {
        mList.add(entry);
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

        if (convertView == null)
            convertView = Inflater.inflate(R.layout.transmission_item_list, null);

        holder = (ViewHolder) convertView.getTag();

        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mTransIcon.setImageResource(mList.get(position).getIcon());
        holder.mTransName.setText(mList.get(position).name);
        holder.mTransime.setText(mList.get(position).size + "M/S");
        holder.mProgressBar.setMax((int)mList.get(position).size);
        holder.mProgressBar.setProgress(1000);

        holder.mTransDelete.setOnClickListener(this);
        holder.mTransPause.setOnClickListener(this);
        holder.mTransFile.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transmission_pause:
                Toast.makeText(mContext, "COMING SONG PAUSE", Toast.LENGTH_LONG).show();
                break;

            case R.id.transmission_delete:
                deleteProgressBar((Integer) v.getTag());
                Toast.makeText(mContext, "COMING SONG DELETE", Toast.LENGTH_LONG).show();
                break;

            case R.id.transmission_file:
                try {
                    URL uri = new URL("www.baidu.com");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                FilePath();
                Toast.makeText(mContext, "COMING SONG FILE", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private class ViewHolder {
        private ImageView mTransIcon, mTransDelete, mTransPause, mTransFile;
        private TextView mTransName, mTransime;
        private ProgressBar mProgressBar;

        public ViewHolder(View view) {
            mProgressBar = view.findViewById(R.id.transmission_progress);
            mTransIcon =  view.findViewById(R.id.transmission_icon);
            mTransDelete =  view.findViewById(R.id.transmission_delete);
            mTransPause = view.findViewById(R.id.transmission_pause);
            mTransFile =  view.findViewById(R.id.transmission_file);
            mTransName =  view.findViewById(R.id.transmission_name);
            mTransime =  view.findViewById(R.id.transmission_time);
        }
    }

    public void updateProgressBar(int a){
        holder.mProgressBar.setProgress(a);
        notifyDataSetChanged();
    }

    public void ProgressBarPause(){}

    public void deleteProgressBar(int a){
        mList.remove(a);
        notifyDataSetChanged();
    }

    public void FilePath(){}

    public void onTaskStart(){}

    public void onTaskStop(){}
}
