package com.example.xyy.danmuplayer.utils.listvew;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xyy.danmuplayer.bean.Directory;
import com.example.xyy.danmuplayer.R;

import java.util.List;

public class DirectoryAdapter extends BaseAdapter {
    Context context;
    List<Directory> data;

    public DirectoryAdapter(Context context, List<Directory> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView != null)
            v = convertView;
        else
            v = View.inflate(context, R.layout.list_item_directory, null);

        Directory directory = data.get(position);
        String directory_name_text = directory.getdirectory_name();
        String directory_file_number_text = directory.getdirectory_file_number();

        TextView directory_name = (TextView)v.findViewById(R.id.directory_name);
        TextView directory_file_number = (TextView)v.findViewById(R.id.directory_file_number);
        directory_name.setText(directory_name_text);
        directory_file_number.setText(directory_file_number_text+" 视频");

        return v;
    }

}

