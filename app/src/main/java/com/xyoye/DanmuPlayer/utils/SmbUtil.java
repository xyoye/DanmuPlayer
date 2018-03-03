package com.xyoye.DanmuPlayer.utils;

import android.os.Environment;
import android.os.StatFs;

import com.xyoye.DanmuPlayer.R;
import com.xyoye.DanmuPlayer.bean.SmbInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * Created by xyy on 2018-02-11 上午 9:34
 */

public class SmbUtil {

    public List<SmbInfo> getFileNamesFromSmb(String smbMachine){
        Log.d("connect to url："+smbMachine);
        List<SmbInfo> infoList = new ArrayList<>();
        SmbFile smbFile;
        SmbFile[] files;
        try {
            smbFile = new SmbFile(smbMachine);
            files = smbFile.listFiles();
            for (SmbFile sFile : files) {
                SmbInfo info = new SmbInfo();
                try {
                    InputStream is = new BufferedInputStream(new SmbFileInputStream(sFile));
                    //只加载xml弹幕文件，预防用户内存不足不显示大文件，另压缩文件貌似缓存错误
                    if (sFile.getName().endsWith(".xml")){
                        info.setDirectory(false);
                        info.setName(sFile.getName());
                        info.setImage(R.mipmap.type_file);
                        is.close();
                        infoList.add(info);
                    }
                } catch (Exception e) {
                    info.setDirectory(true);
                    info.setName(sFile.getName().replace("/",""));
                    info.setImage(R.mipmap.type_folder);
                    infoList.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return infoList;
    }

    /**
     * 从smbMachine读取文件并存储到localPath指定的路径
     * @param smbPath 共享文件路径
     */
    public String loadFromSmb(String smbPath){

        String localPath = Environment.getExternalStorageDirectory().getPath()+"/DanmuPlayer/cache";
        File file = new File(localPath);
        if (!file.exists()){
            file.mkdirs();
        }

        InputStream bis;
        BufferedReader reader;
        BufferedWriter bfw;
        try{
            SmbFile smbFile = new SmbFile(smbPath);
            long fileSize = smbFile.length();
            if(fileSize>1024*1024*5){
                localPath = "超过5M";
            }else if (!isEnoughMem()){
                localPath = "内存不足";
            }else {
                String fileName = smbFile.getName();
                localPath = localPath+ File.separator+fileName;

                bis = new BufferedInputStream(new SmbFileInputStream(smbFile));
                reader = new BufferedReader(new InputStreamReader(bis,"utf-8"));
                File localFile = new File(localPath);
                bfw = new BufferedWriter(new FileWriter(localFile, false));
                bfw.write(reader.readLine());
                bfw.newLine();
                bfw.flush();

                bis.close();
                reader.close();
                bfw.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return localPath;
    }

    //内存不能小于5M
    private boolean isEnoughMem() {
        File path = Environment.getDataDirectory();  // Get the path /data, this is internal storage path.
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long memSize = availableBlocks* blockSize;  // free size, unit is byte.

        if (memSize <1024*1024*5) { //If phone available memory is less than 10M , kill activity,it will avoid force when phone low memory.
            return false;
        }
        return true;
    }
}