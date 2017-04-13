package com.example.xyy.danmuplayer.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DirectoryHelper extends SQLiteOpenHelper {
    public DirectoryHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table directory (id integer primary key autoincrement, " +
                "directory_name text, directory_file_name text, directory_file_path text, directory_file_time integer, "+
                "directory_file_danmu_path text);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        return;
    }
}
