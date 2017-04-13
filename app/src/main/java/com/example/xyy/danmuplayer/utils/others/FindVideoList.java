package com.example.xyy.danmuplayer.utils.others;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.xyy.danmuplayer.bean.Directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.MediaMetadataRetriever;
import io.vov.vitamio.utils.Log;

/**
 * 视频查找任务
 */
public class FindVideoList extends AsyncTask<Context, Integer, List<Directory>> {

    private ArrayList<Directory> DirectoryList;
    private QueryListener listener;

    public FindVideoList() {
        DirectoryList = new ArrayList<>();
    }

    public void setQueryListener(QueryListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Directory> doInBackground(Context... params) {
        Context context = params[0];

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Cursor exCursor = context.getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            null, null, null, null);
            getDirectory(context, exCursor);
        }

        return DirectoryList;
    }

    @Override
    protected void onPostExecute(List<Directory> DirectoryList) {
        if (listener != null) {
            listener.onResult(DirectoryList);
        }
    }

    private void getDirectory(Context context, Cursor cursor) {
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            File file = new File(path);
            if (!file.exists()) {// 文件不存在
                continue;
            }
            Directory Directory = new Directory();
            Directory.setdirectory_file_path(path);
            Directory.setdirectory_file_time(cursor.getLong(
                    cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
            DirectoryList.add(Directory);
        }
        cursor.close();
    }

    public interface QueryListener {
        void onResult(List<Directory> DirectoryList);
    }

    //此方法将会遍历传入路径中的video文件
    public List<Directory> getfilelist(Context context,String Path)
    {
        List<Directory> lstFile = new ArrayList<>();
        Directory add_file = new Directory();

        File file = new File(Path);
        File[] files = file.listFiles();
        if(files!=null){
            for (int i = 0; i < files.length; i++)
            {
                File f = files[i];
                if(f == null)continue;
                if (f.isFile())
                {
                    String mp4 = f.getPath().substring(f.getPath().length() - "mp4".length());
                    String rmvb = f.getPath().substring(f.getPath().length() - "rmvb".length());
                    String mkv = f.getPath().substring(f.getPath().length() - "mkv".length());
                    String avi = f.getPath().substring(f.getPath().length() - "avi".length());
                    String _3gp = f.getPath().substring(f.getPath().length() - "3gp".length());
                    String wmv = f.getPath().substring(f.getPath().length() - "wmv".length());
                    //判断扩展名
                    if (mp4.equals("mp4")||mp4.equals("MP4"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(rmvb.equals("rmvb")||rmvb.equals("RMVB"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mkv.equals("mkv")||mkv.equals("MKV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(avi.equals("avi")||avi.equals("AVI"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(_3gp.equals("3gp")||_3gp.equals("3GP"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if (wmv.equals("wmv")||wmv.equals("WMV")){
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }
                }
            }
            return lstFile;
        }else {
            return null;
        }
    }

    public Directory getDirectoryInfo(Context context,String video_path){
        Directory directory = new Directory();
        directory.setdirectory_file_path(video_path);
        directory.setdirectory_file_time(00);
        return directory;
    }
}
