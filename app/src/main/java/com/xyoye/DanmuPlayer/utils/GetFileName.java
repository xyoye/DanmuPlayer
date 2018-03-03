package com.xyoye.DanmuPlayer.utils;

/**
 * Created by YE on 2017/5/15.
 */
public class GetFileName {

    public String getName(String url){
        try {
            String[] file_name_array = url.split("/");
            String file_name = file_name_array[file_name_array.length-1];
            int Suffix = file_name.lastIndexOf(".");
            file_name = file_name.substring(0,Suffix);
            return file_name;
        }catch (Exception e){
            e.printStackTrace();
            return  url;
        }
    }
}
