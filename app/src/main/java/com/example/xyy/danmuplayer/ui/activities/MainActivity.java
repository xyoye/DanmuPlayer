package com.example.xyy.DanmuPlayer.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyy.DanmuPlayer.R;
import com.example.xyy.DanmuPlayer.bean.Directory;
import com.example.xyy.DanmuPlayer.utils.listvew.DirectoryAdapter;
import com.example.xyy.DanmuPlayer.utils.database.DirectoryDao;
import com.example.xyy.DanmuPlayer.utils.others.FindVideoList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 *author：叶子
 */

public class MainActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{
    final static int ADD_DIRECTORY = 1;

    DirectoryDao ddao;
    ListView dirctory_listview;    List<Directory> system_directory_list;

    List<Directory> database_directory_list;
    DirectoryAdapter directoryAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean search_over = false;
    boolean search_success = true;
    int wait_time;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 100:
                    //更新数据库
                    FlashDatabase();
                    //显示数据库中video信息
                    ShowDirectory();
                    //关闭刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "更新文件列表成功！！！", Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    //关闭刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "更新文件列表失败！！！", Toast.LENGTH_LONG).show();
                    break;
                case 102:
                    //显示数据库中video信息
                    ShowDirectory();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置通知栏为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //初始化
        init();

        //判断程序是否第一次运行
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstRun", false);
        editor.commit();
        if (isFirstRun)
        {
            new Thread(){
                @Override
                public void run(){
                    //遍历系统文件,并录入数据库
                    FlashFileList();
                    //确保数据录入数据库后，更新列表
                    wait_time = 0;
                    search_success = true;
                    while (!search_over) {
                        try {
                            Thread.sleep(1000);
                            wait_time++;
                            search_success = true;
                            Log.i("FLASH","正在更新");
                            //五秒内未获取到文件列表，判断为获取失败
                            if (wait_time>5){
                                search_success = false;
                                break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (search_success){
                        Message message  = new Message();
                        message.what = 100;
                        handler.sendMessage(message);
                    }else {
                        Message message  = new Message();
                        message.what = 101;
                        handler.sendMessage(message);
                    }
                }
            }.start();
        }
    }

    public void init(){
        ddao = new DirectoryDao(MainActivity.this);
        database_directory_list = new ArrayList<>();

        dirctory_listview = (ListView)findViewById(R.id.dirctory_listview);
        dirctory_listview.setOnItemClickListener(this);
        dirctory_listview.setOnItemLongClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressViewEndTarget(true, 100);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新数据库
        FlashDatabase();
        //显示数据库中video信息
        ShowDirectory();
    }

    //获取系统中video文件信息并更新到数据库中
    public void FlashFileList()
    {
        search_over = false;
        system_directory_list = new ArrayList<>();
        //获取系统中video文件地址以及video时长
        FindVideoList findVideoList = new FindVideoList();
        findVideoList.setQueryListener(new FindVideoList.QueryListener() {
            @Override
            public void onResult(List<Directory> mediaInfoList) {
                system_directory_list = mediaInfoList;

                if (system_directory_list.size()>0)
                {
                    for (int onefile=0;onefile<system_directory_list.size();onefile++)
                    {
                        //得到video时间
                        String[] get_video_name_array = system_directory_list.get(onefile).getdirectory_file_path().split("/");
                        String video_name = get_video_name_array[get_video_name_array.length-1];
                        int Suffix = video_name.lastIndexOf(".");
                        video_name = video_name.substring(0,Suffix);
                        //得到video所属文件夹
                        String video_directory = get_video_name_array[get_video_name_array.length-2];
                        //得到video地址
                        String video_path = system_directory_list.get(onefile).getdirectory_file_path();
                        //得到video时长
                        int video_time = (int)system_directory_list.get(onefile).getdirectory_file_time();

                        //判断数据库中是否已经存在该视频地址
                        boolean file_had = ddao.QueryFileHad(video_path);
                        if (!file_had){
                            //添加到数据库
                            ddao.insert(video_directory,video_name,video_path,video_time,"null");
                        }
                    }
                    search_over = true;
                }
                else {
                    search_over = true;
                }
            }
        });
        findVideoList.execute(MainActivity.this);
    }

    //更新数据库信息
    public void FlashDatabase()
    {
        List<String> directory_file_list = ddao.QueryAllFile();
        if (directory_file_list!=null&&directory_file_list.size()>0)
        {
            for (int i=0;i<directory_file_list.size();i++)
            {
                try{
                    File f=new File(directory_file_list.get(i));
                    if(!f.exists()){
                        ddao.deleteFile(directory_file_list.get(i));
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //显示数据库中所有文件夹信息
    public void ShowDirectory()
    {
        //查询数据库中所有文件夹，以及其中包含的视频数量
        List<String> directory_list =  ddao.QueryDirectorys();
        database_directory_list = new ArrayList<>();
        if (directory_list!=null&&directory_list.size()>0)
        {
            for (int i=0;i<directory_list.size();i++)
            {
                List<String> directory_file_number_list = ddao.QueryFiles(directory_list.get(i));
                Directory directory = new Directory();
                directory.setdirectory_name(directory_list.get(i));
                directory.setdirectory_file_number(directory_file_number_list.size()+"");
                database_directory_list.add(directory);
            }
        }

        directoryAdapter = new DirectoryAdapter(MainActivity.this,database_directory_list);
        dirctory_listview.setAdapter(directoryAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent open_video_list_activity = new Intent(MainActivity.this, VideoListActivity.class);
        open_video_list_activity.putExtra("actionbar_title",database_directory_list.get(position).getdirectory_name());
        startActivity(open_video_list_activity);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final String directory_name  = database_directory_list.get(position).getdirectory_name();
        AlertDialog.Builder builder_dalete = new AlertDialog.Builder(this);
        builder_dalete.setTitle("确认删除此文件夹？").setView(null).setNegativeButton("取消", null);
        builder_dalete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ddao.deleteDirectory(directory_name);
                ShowDirectory();
                Toast.makeText(MainActivity.this,"删除成功！！！",Toast.LENGTH_LONG).show();
            }
        });
        builder_dalete.show();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_directory:
                Intent intent = new Intent(MainActivity.this, FolderChooserActivity.class);
                intent.putExtra("isFolderChooser", true);
                startActivityForResult(intent,ADD_DIRECTORY);
                break;
            case R.id.about:
                showAbout();
                break;
        }
    }

    public void showAbout(){
        View about_dialog = View.inflate(MainActivity.this,R.layout.about_more,null);
        AlertDialog.Builder about_builder = new AlertDialog.Builder(MainActivity.this).setView(about_dialog);

        TextView bt_aboutPlayer = (TextView) about_dialog.findViewById(R.id.about_player);
        bt_aboutPlayer.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(View v) {
                View dialog = View.inflate(MainActivity.this,R.layout.about_player,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setView(dialog);
                builder.show();
            }
        });

        TextView bt_aboutUse = (TextView) about_dialog.findViewById(R.id.about_use);
        bt_aboutUse.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(View v) {
                View dialog = View.inflate(MainActivity.this,R.layout.about_use,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setView(dialog);
                builder.show();
            }
        });

        about_builder.show();
    }

    @Override
    public void onRefresh() {
        new Thread(){
            @Override
            public void run(){
                //遍历系统文件,并录入数据库
                FlashFileList();
                //确保数据录入数据库后，更新列表
                wait_time = 0;
                search_success = true;
                while (!search_over) {
                    try {
                        Thread.sleep(1000);
                        wait_time++;
                        search_success = true;
                        Log.i("FLASH","正在更新");
                        //五秒内未获取到文件列表，判断为获取失败
                        if (wait_time>5){
                            search_success = false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (search_success){
                    Message message  = new Message();
                    message.what = 100;
                    handler.sendMessage(message);
                }else {
                    Message message  = new Message();
                    message.what = 101;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    //连续点击两次退出
    private boolean waitExit = true;
    private Toast toast;
    private Handler mHandler = new Handler();
    private Runnable cancelExit = new Runnable() {
        @Override
        public void run() {
            waitExit = true;
        }
    };
    @Override
    public void onBackPressed() {
        if (waitExit) {
            waitExit = false;
            toast = Toast.makeText(MainActivity.this,getString(R.string.press_to_exit), Toast.LENGTH_SHORT);
            toast.show();
            mHandler.postDelayed(cancelExit, 2000);
        } else {
            toast.cancel();
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case ADD_DIRECTORY:
                    File folder = (File) data.getSerializableExtra("file_path");
                    String directory_path = folder.getAbsolutePath();
                    List<Directory> addFileList = new ArrayList<>();
                    addFileList = new FindVideoList().getfilelist(MainActivity.this,directory_path);
                    if (addFileList.size()>0) {
                        for (int onefile = 0; onefile < addFileList.size(); onefile++) {
                            //得到video时间
                            String[] get_video_name_array = addFileList.get(onefile).getdirectory_file_path().split("/");
                            String video_name = get_video_name_array[get_video_name_array.length - 1];
                            int Suffix = video_name.lastIndexOf(".");
                            video_name = video_name.substring(0, Suffix);
                            //得到video所属文件夹
                            String video_directory = get_video_name_array[get_video_name_array.length - 2];
                            //得到video地址
                            String video_path = addFileList.get(onefile).getdirectory_file_path();
                            //得到video时长
                            int video_time = (int) addFileList.get(onefile).getdirectory_file_time();

                            //判断数据库中是否已经存在该视频地址
                            boolean file_had = ddao.QueryFileHad(video_path);
                            if (!file_had) {
                                //添加到数据库
                                ddao.insert(video_directory, video_name, video_path, video_time, "null");
                            }
                        }
                    }
                    Message add_file_over_message = new Message();
                    add_file_over_message.what = 102;
                    handler.sendMessage(add_file_over_message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}