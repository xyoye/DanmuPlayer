package com.xyoye.danmuplayer.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.danmuplayer.bean.Video;
import com.xyoye.danmuplayer.R;
import com.xyoye.danmuplayer.database.DirectoryDao;
import com.xyoye.danmuplayer.ui.adpter.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener
 {
     private TextView tvTitle;
     private ListView lvVideo;

     DirectoryDao ddao;
     VideoAdapter adapter;

     String tvTitleText;
     List<String> filePathList;
     List<String> fileNameList;
     List<Video> videoList;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);

         initView();
         
         initData();
         
         initListener();
     }

     /**
      * 初始化数据
      */
     public void initData(){
         Intent intent = getIntent();
         tvTitleText = intent.getStringExtra("title");
         tvTitle.setText(tvTitleText);

         ddao = new DirectoryDao(VideoListActivity.this);
         videoList = new ArrayList<>();
     }

     /**
      * 初始化组件
      */
     public void initView(){
         tvTitle = (TextView)findViewById(R.id.tv_title);
         lvVideo = (ListView)findViewById(R.id.lv_video);
     }
     
     /**
      * 初始化事件
      */
     public void initListener(){
         lvVideo.setOnItemClickListener(this);
         lvVideo.setOnItemLongClickListener(this);
     }

     /**
      * 展现数据到listView
      */
     public void showVideoList(){
         videoList = new ArrayList<>();
         filePathList = ddao.QueryFilePath(tvTitleText);
         fileNameList = ddao.QueryFiles(tvTitleText);

         if(filePathList.size()>0){
             for (int i=0;i<filePathList.size();i++){
                 //此处暂不加入弹幕地址，以备后用
                 Video video = new Video();
                 video.setvideo_name(fileNameList.get(i));
                 video.setvideo_path(filePathList.get(i));
                 video.setvideo_time(ddao.QueryFileTime(filePathList.get(i)));
                 video.setdanmu_path(ddao.QueryFileDanmu(filePathList.get(i)));
                 videoList.add(video);
             }
         }
         adapter = new VideoAdapter(VideoListActivity.this,videoList);
         lvVideo.setAdapter(adapter);
     }

     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Intent intent = new Intent(VideoListActivity.this, PlayActivity.class);
         intent.putExtra("PLAY_URL",videoList.get(position).getvideo_path());
         intent.putExtra("VIDEO_NAME", videoList.get(position).getvideo_name());
         intent.putExtra("DANMU_URL", videoList.get(position).getdanmu_path());
         startActivity(intent);
     }

     @Override
     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
         final String delete_fiel_name  = videoList.get(position).getvideo_path();
         AlertDialog.Builder builder_dalete = new AlertDialog.Builder(this);
         builder_dalete.setTitle("确认删除此记录？").setView(null).setNegativeButton("取消", null);
         builder_dalete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
                 ddao.deleteFile(delete_fiel_name);
                 showVideoList();
                 Toast.makeText(VideoListActivity.this,"删除成功！！！",Toast.LENGTH_LONG).show();
             }
         });
         builder_dalete.show();
         return true;
     }

     @Override
     protected void onResume() {
         super.onResume();
         //显示数据到ListView上
         showVideoList();
     }
 }
