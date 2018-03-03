package com.example.xyy.DanmuPlayer.ui.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xyy.DanmuPlayer.R;
import com.example.xyy.DanmuPlayer.bean.Video;
import com.example.xyy.DanmuPlayer.utils.ImageTask;

import java.util.List;

import io.vov.vitamio.utils.StringUtils;

public class VideoAdapter extends BaseAdapter{

    private Context context;
    private List<Video> data;
    private ImageTask it;

    public VideoAdapter(Context context, List<Video> data){
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

        it = new ImageTask(parent);

        View v;
        if (convertView != null)
            v = convertView;
        else
            v = View.inflate(context, R.layout.list_item_video, null);

        Video video = data.get(position);
        String video_name_text = video.getvideo_name();
        String video_path = video.getvideo_path();
        int video_time_text = video.getvideo_time();

        ImageView video_iv = (ImageView) v.findViewById(R.id.video_iv);
        TextView video_name = (TextView)v.findViewById(R.id.video_name);
        TextView video_time = (TextView)v.findViewById(R.id.video_time);
        RelativeLayout time_rl = (RelativeLayout)v.findViewById(R.id.time_rl);

        video_name.setText(video_name_text);
        video_iv.setTag(video_path);
        it.execute(video_path);

        if (video_time_text == 0){
            time_rl.setVisibility(View.GONE);
        }else {
            video_time.setText(StringUtils.generateTime(video_time_text));
        }
        return v;
    }

}
