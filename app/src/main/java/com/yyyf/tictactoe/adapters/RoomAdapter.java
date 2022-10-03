package com.yyyf.tictactoe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yyyf.tictactoe.R;
import com.yyyf.tictactoe.core.net.RoomInfo;

import java.util.List;

public class RoomAdapter extends BaseAdapter {
    Context context;
    List<RoomInfo> list;

    public RoomAdapter(List<RoomInfo> list, Context context){
        this.list    = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getNo();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.room_item,parent,false);

        TextView no = convertView.findViewById(R.id.roomItem_no);
        TextView name = convertView.findViewById(R.id.roomItem_name);
        TextView sock = convertView.findViewById(R.id.roomItem_sock);

        no.setText(list.get(position).getNo() + "");
        name.setText(list.get(position).getName());
        sock.setText(list.get(position).getSocket() + "");

        return convertView;
    }
}
