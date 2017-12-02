package com.example.xyy.DanmuPlayer.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyy.DanmuPlayer.bean.Video;
import com.example.xyy.DanmuPlayer.R;
import com.example.xyy.DanmuPlayer.utils.database.DirectoryDao;
import com.example.xyy.DanmuPlayer.utils.listvew.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener
 {
     private TextView actionbar_title;
     private ListView video_listview;
     DirectoryDao ddao;
     VideoAdapter adapter;
     String actionbar_title_text;
     List<String> file_path_list;
     List<String> file_name_list;
     List<Video> video_list;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        //设置通知栏为透明
        View decorView=getWindow().getDecorView();
        int option=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //实例化组件
        findviews();
        //实例化变量
        ddao = new DirectoryDao(VideoListActivity.this);
        video_list = new ArrayList<>();
        //获得打开的文件夹名
        Intent intent = getIntent();
        actionbar_title_text = intent.getStringExtra("actionbar_title");
        actionbar_title.setText(actionbar_title_text);
     }

     @Override
     protected void onResume() {
         super.onResume();
         //显示数据到ListView上
         ShowVideoList();
     }

     public void findviews(){
         actionbar_title = (TextView)findViewById(R.id.videolist_activity_action_bar);
         video_listview = (ListView)findViewById(R.id.video_listview);

         video_listview.setOnItemClickListener(this);
         video_listview.setOnItemLongClickListener(this);
     }

     public void ShowVideoList(){
         video_list = new ArrayList<>();
         file_path_list = ddao.QueryFilePath(actionbar_title_text);
         file_name_list = ddao.QueryFiles(actionbar_title_text);

         if(file_path_list.size()>0){
             for (int i=0;i<file_path_list.size();i++){
                 //此处暂不加入弹幕地址，以备后用
                 Video video = new Video();
                 video.setvideo_name(file_name_list.get(i));
                 video.setvideo_path(file_path_list.get(i));
                 video.setvideo_time(ddao.QueryFileTime(file_path_list.get(i)));
                 video.setdanmu_path(ddao.QueryFileDanmu(file_path_list.get(i)));
                 video_list.add(video);
             }
         }
         adapter = new VideoAdapter(VideoListActivity.this,video_list);
         video_listview.setAdapter(adapter);
     }

     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Intent intent = new Intent(VideoListActivity.this, PlayActivity.class);
         intent.putExtra("PLAY_URL",video_list.get(position).getvideo_path());
         intent.putExtra("VIDEO_NAME", video_list.get(position).getvideo_name());
         intent.putExtra("DANMU_URL", video_list.get(position).getdanmu_path());
         startActivity(intent);
     }

     @Override
     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
         final String delete_fiel_name  = video_list.get(position).getvideo_path();
         AlertDialog.Builder builder_dalete = new AlertDialog.Builder(this);
         builder_dalete.setTitle("确认删除此记录？").setView(null).setNegativeButton("取消", null);
         builder_dalete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
                 ddao.deleteFile(delete_fiel_name);
                 ShowVideoList();
                 Toast.makeText(VideoListActivity.this,"删除成功！！！",Toast.LENGTH_LONG).show();
             }
         });
         builder_dalete.show();
         return true;
     }
 }
