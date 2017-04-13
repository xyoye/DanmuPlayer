package com.example.xyy.danmuplayer.bean;

public class Video {
    private int id;
    private String video_name;
    private String video_path;
    private String danmu_path;
    private int video_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setvideo_name(String video_name){
        this.video_name = video_name;
    }
    public String getvideo_name(){
        return video_name;
    }

    public void setvideo_path(String video_path){
        this.video_path = video_path;
    }
    public String getvideo_path(){
        return video_path;
    }

    public void setdanmu_path(String danmu_path){
        this.danmu_path = danmu_path;
    }
    public String getdanmu_path(){
        return danmu_path;
    }

    public void setvideo_time(int video_time){
        this.video_time = video_time;
    }
    public int getvideo_time (){
        return video_time;
    }
}
