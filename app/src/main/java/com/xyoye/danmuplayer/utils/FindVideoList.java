package com.xyoye.danmuplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.xyoye.danmuplayer.bean.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        File file = new File(Path);
        File[] files = file.listFiles();
        if(files!=null){
            for (int i = 0; i < files.length; i++)
            {
                File f = files[i];
                if(f == null)continue;
                if (f.isFile())
                {
                    String rmvb = f.getPath().substring(f.getPath().length() - "rmvb".length());
                    String _3gp = f.getPath().substring(f.getPath().length() - "3gp".length());
                    String flv = f.getPath().substring(f.getPath().length() - "flv".length());
                    String mov = f.getPath().substring(f.getPath().length() - "mov".length());
                    String m4a = f.getPath().substring(f.getPath().length() - "m4a".length());
                    String _3g2 = f.getPath().substring(f.getPath().length() - "3g2".length());
                    String mj2 = f.getPath().substring(f.getPath().length() - "mj2".length());
                    String wmv = f.getPath().substring(f.getPath().length() - "wmv".length());
                    String ts = f.getPath().substring(f.getPath().length() - "ts".length());
                    String mpeg = f.getPath().substring(f.getPath().length() - "mpeg".length());
                    String mpe = f.getPath().substring(f.getPath().length() - "mpe".length());
                    String m1v = f.getPath().substring(f.getPath().length() - "m1v".length());
                    String mp2 = f.getPath().substring(f.getPath().length() - "mp2".length());
                    String mp3 = f.getPath().substring(f.getPath().length() - "mp3".length());
                    String mod = f.getPath().substring(f.getPath().length() - "mod".length());
                    String wma = f.getPath().substring(f.getPath().length() - "wma".length());
                    String rm = f.getPath().substring(f.getPath().length() - "rm".length());
                    String vob = f.getPath().substring(f.getPath().length() - "vob".length());
                    String ogg = f.getPath().substring(f.getPath().length() - "ogg".length());
                    String divx = f.getPath().substring(f.getPath().length() - "divx".length());
                    String qt = f.getPath().substring(f.getPath().length() - "qt".length());
                    String mpg = f.getPath().substring(f.getPath().length() - "mpg".length());
                    String pfv = f.getPath().substring(f.getPath().length() - "pfv".length());
                    String mkv = f.getPath().substring(f.getPath().length() - "mkv".length());
                    String avi = f.getPath().substring(f.getPath().length() - "avi".length());
                    String asf = f.getPath().substring(f.getPath().length() - "asf".length());
                    String m4v = f.getPath().substring(f.getPath().length() - "m4v".length());
                    String mp4 = f.getPath().substring(f.getPath().length() - "mp4".length());

                    //判断扩展名
                    if(rmvb.equals("rmvb")||rmvb.equals("RMVB"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(_3gp.equals("3gp")||_3gp.equals("3GP"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(flv.equals("flv")||flv.equals("FLV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mov.equals("mov")||mov.equals("MOV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(m4a.equals("m4a")||m4a.equals("M4a"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(_3g2.equals("3g2")||_3g2.equals("3G2"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mj2.equals("mj2")||mj2.equals("MJ2"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(wmv.equals("wmv")||wmv.equals("WMV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(ts.equals("ts")||ts.equals("TS"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mpeg.equals("mpeg")||mpeg.equals("MPEG"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mpe.equals("mpe")||mpe.equals("MPE"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(m1v.equals("m1v")||m1v.equals("m1v"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mp2.equals("mp2")||mp2.equals("MP2"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mp3.equals("mp3")||mp3.equals("MP3"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mod.equals("mod")||mod.equals("Mod"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(wma.equals("wma")||wma.equals("WMA"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(rm.equals("rm")||rm.equals("RM"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(vob.equals("vob")||vob.equals("VOB"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(ogg.equals("ogg")||ogg.equals("OGG"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(divx.equals("divx")||divx.equals("DIVX"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(qt.equals("qt")||qt.equals("QT"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mpg.equals("mpg")||mpg.equals("MPG"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(pfv.equals("pfv")||pfv.equals("PFV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mkv.equals("mkv")||mkv.equals("MKV"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(avi.equals("avi")||avi.equals("AVI"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(asf.equals("asf")||asf.equals("ASF"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(m4v.equals("m4v")||m4v.equals("M4V"))
                    {
                        lstFile.add(getDirectoryInfo(context,f.getPath()));
                    }else if(mp4.equals("mp4")||mp4.equals("MP4"))
                    {
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
