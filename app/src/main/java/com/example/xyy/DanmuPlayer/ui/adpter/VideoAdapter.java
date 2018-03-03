package com.example.xyy.DanmuPlayer.ui.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyy.DanmuPlayer.bean.Video;
import com.example.xyy.DanmuPlayer.R;
import com.example.xyy.DanmuPlayer.ui.activities.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.utils.StringUtils;

public class VideoAdapter extends BaseAdapter{
    Context context;
    List<Video> data;
    Handler handler;

    public VideoAdapter(Context context, List<Video> data){
        this.context = context;
        this.data = data;
        handler = new Handler();
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
            v = View.inflate(context, R.layout.list_item_video, null);

        Video video = data.get(position);
        String video_name_text = video.getvideo_name();
        //String video_path = "file://"+video.getvideo_path();
        String video_path = video.getvideo_path();
        int video_time_text = video.getvideo_time();

        ImageView video_iv = (ImageView) v.findViewById(R.id.video_iv);
        TextView video_name = (TextView)v.findViewById(R.id.video_name);
        TextView video_time = (TextView)v.findViewById(R.id.video_time);
        RelativeLayout time_rl = (RelativeLayout)v.findViewById(R.id.time_rl);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(video_path , video_iv, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        video_name.setText(video_name_text);
        if (video_time_text == 0){
            time_rl.setVisibility(View.GONE);
        }else {
            video_time.setText(StringUtils.generateTime(video_time_text));
        }
        return v;
    }
    
}
