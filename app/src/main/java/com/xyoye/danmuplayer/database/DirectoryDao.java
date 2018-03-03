package com.xyoye.danmuplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DirectoryDao {
    private DirectoryHelper helper;
    public DirectoryDao(Context context) {
        helper = new DirectoryHelper(context, "directory_database.db", null, 1); // 创建Dao时, 创建Helper
    }

    public void insert(String directory_name,String directory_file_name,String directory_file_path,long directory_file_time,String directory_file_danmu_path) {
        SQLiteDatabase db = helper.getWritableDatabase(); // 获取数据库对象
        ContentValues values = new ContentValues();
        values.put("directory_name",directory_name);
        values.put("directory_file_name",directory_file_name);
        values.put("directory_file_path",directory_file_path);
        values.put("directory_file_time",directory_file_time);
        values.put("directory_file_danmu_path",directory_file_danmu_path);
        db.insert("directory", null, values);
        db.close();         // 关闭数据库
    }

    //删除文件夹信息
    public void deleteDirectory(String delete_directory_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // 按条件删除指定表中的数据, 返回受影响的行数
        db.delete("directory", "directory_name=?", new String[] { delete_directory_name });
        db.close();
    }

    //删除文件信息
    public void deleteFile(String delete_file_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // 按条件删除指定表中的数据, 返回受影响的行数
        db.delete("directory", "directory_file_path=?", new String[] { delete_file_name });
        db.close();
    }

    //查询某文件夹下所有video文件名字
    public List<String> QueryFiles(String directory_name) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory", null,"directory_name=?",new String[]{directory_name}, null, null,"id DESC");
        List<String> list = new ArrayList<String>();
        while (c.moveToNext()) {
            String directory_file_name = c.getString(c.getColumnIndex("directory_file_name"));
            list.add(directory_file_name);
        }
        c.close();
        db.close();
        return list;
    }

    //查询某文件夹下所有video文件地址
    public List<String> QueryFilePath(String directory_name) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory", null,"directory_name=?",new String[]{directory_name}, null, null,"id DESC");
        List<String> list = new ArrayList<String>();
        while (c.moveToNext()) {
            String directory_file_name = c.getString(c.getColumnIndex("directory_file_path"));
            list.add(directory_file_name);
        }
        c.close();
        db.close();
        return list;
    }

    //查询所有含有video文件的文件夹
    public List<String> QueryDirectorys() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory",new String[]{"directory_name"},null,null,"directory_name",null,"id DESC");
        List<String> list = new ArrayList<String>();
        while (c.moveToNext()) {
            String result_name = c.getString(c.getColumnIndex("directory_name"));
            list.add(result_name);
        }
        c.close();
        db.close();
        return list;
    }

    //查询数据库中所有video文件地址
    public List<String> QueryAllFile() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory",null,null,null,null,null,"id DESC");
        List<String> list = new ArrayList<String>();
        while (c.moveToNext()) {
            String result_path = c.getString(c.getColumnIndex("directory_file_path"));
            list.add(result_path);
        }
        c.close();
        db.close();
        return list;
    }

    //查询数据库中文件的时长
    public int QueryFileTime(String query_file_path){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory",null,"directory_file_path=?",new String[]{query_file_path},null,null,"id DESC");
        int result_time = 0;
        while (c.moveToNext()) {
            result_time = c.getInt(c.getColumnIndex("directory_file_time"));
        }
        c.close();
        db.close();
        return  result_time;
    }

    //查询数据库中文件的弹幕
    public String QueryFileDanmu(String query_file_path){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory",null,"directory_file_path=?",new String[]{query_file_path},null,null,"id DESC");
        String result_danmu = null;
        while (c.moveToNext()) {
            result_danmu= c.getString(c.getColumnIndex("directory_file_danmu_path"));
        }
        c.close();
        db.close();
        return  result_danmu;
    }

    //判断数据库中某文件是否存在
    public boolean QueryFileHad(String query_file_path){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("directory",null,"directory_file_path=?",new String[]{query_file_path},null,null,"id DESC");
        List<String> list = new ArrayList<String>();
        while (c.moveToNext()) {
            String result_path = c.getString(c.getColumnIndex("directory_file_path"));
            list.add(result_path);
        }
        c.close();
        db.close();
        if(list.size()>0){
            return true;
        }else {
            return false;
        }
    }

    //更新视频对应的弹幕地址
    public void UpdateFileDanmu(String file_path,String danmu_path){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("directory_file_danmu_path", danmu_path);
        db.update("directory",updatedValues,"directory_file_path=?",new String[]{file_path});
    }
}
