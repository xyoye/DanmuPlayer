package com.example.xyy.DanmuPlayer.bean;

public class Directory {
    private int id;
    private String directory_name;
    private String directory_file_number;
    private String directory_file_path;
    private long directory_file_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setdirectory_name(String directory_name){
        this.directory_name = directory_name;
    }
    public String getdirectory_name(){
        return directory_name;
    }

    public void setdirectory_file_number(String directory_file_number){
        this.directory_file_number = directory_file_number;
    }
    public String getdirectory_file_number(){
        return directory_file_number;
    }

    public void setdirectory_file_path(String directory_file_path){
        this.directory_file_path = directory_file_path;
    }
    public String getdirectory_file_path(){
        return directory_file_path;
    }

    public void setdirectory_file_time(long directory_file_time) {
        this.directory_file_time = directory_file_time;
    }

    public long getdirectory_file_time(){
        return directory_file_time;
    }
}
