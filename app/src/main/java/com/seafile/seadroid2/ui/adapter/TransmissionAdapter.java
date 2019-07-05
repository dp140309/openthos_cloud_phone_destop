package com.seafile.seadroid2.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;

import java.util.ArrayList;
import java.util.List;

public class TransmissionAdapter extends BaseAdapter {

    private List<SeafDirent> mList = new ArrayList<>();
    private LayoutInflater Inflater;


    public  TransmissionAdapter(List<SeafDirent> list, Context context) {
        Inflater = LayoutInflater.from(context);
        this.mList = list;
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

        ViewHolder holder = (ViewHolder) convertView.getTag();

        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mTransIcon.setImageResource(mList.get(position).getIcon());
        holder.mTransName.setText(mList.get(position).name);
        holder.mTransime.setText(mList.get(position).size + "M/S");

        return convertView;
    }

    private class ViewHolder {
        private ImageView mTransIcon, mTransDelete, mTransPause, mTransFile;
        private TextView mTransName, mTransime;

        public ViewHolder(View view) {
            mTransIcon = (ImageView) view.findViewById(R.id.transmission_icon);
            mTransDelete = (ImageView) view.findViewById(R.id.transmission_delete);
            mTransPause = (ImageView) view.findViewById(R.id.transmission_cancel);
            mTransFile = (ImageView) view.findViewById(R.id.transmission_file);
            mTransName = (TextView) view.findViewById(R.id.transmission_name);
            mTransime = (TextView) view.findViewById(R.id.transmission_time);
        }
    }


}
