package com.example.xyy.danmuplayer.folderchooser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xyy.danmuplayer.R;
import com.example.xyy.danmuplayer.utils.flexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件和文件夹选择器
 * 作者：yedongyang
 * 我的博客：http://blog.csdn.net/sinat_25689603
 * created by ydy on 2016/7/11 11:47
 */
public class FolderChooserActivity extends AppCompatActivity {

    ImageView titleLeft;        //标题栏左边按钮
    TextView titleText;         // 标题栏title
    ImageView titleRight;       //标题栏右边按钮
    TextView savePath;
    RecyclerView recyclerView;
    LinearLayout loading_view;

    //是否为文件夹选择器。true文件夹，false文件
    private boolean isFolderChooser = false;
    private String mimeType = "*/*";
    private String mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private FolderChooserAdapter mAdapter;
    private List<FolderChooserInfo> mData;

    private File parentFolder;
    private List<FolderChooserInfo> parentContents;
    private boolean canGoUp = true;

    private ExecutorService singleThreadExecutor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    savePath.setText(parentFolder.getAbsolutePath());
                    mData.clear();
                    mData.addAll(getContentsArray());
                    mAdapter.notifyDataSetChanged();

                    loading_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_chooser);
        isFolderChooser = getIntent().getBooleanExtra("isFolderChooser", false);
        String mime_Type = getIntent().getStringExtra("mimeType");
        String file_path = getIntent().getStringExtra("file_path");
        mimeType = mime_Type == null ? mimeType : mime_Type;
        singleThreadExecutor = Executors.newSingleThreadExecutor();

        mInitialPath = file_path == null ? mInitialPath : file_path;
        parentFolder = new File(mInitialPath);
        initView();
        setData();
    }

    private void setData(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                parentContents = isFolderChooser ? listFiles() : listFiles(mimeType);
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void initView() {
        findViewById();
        setTitleView();
        setRecyclerView();
        savePath.setText(parentFolder.getAbsolutePath());
    }

    //设置标题栏
    private void setTitleView() {
        titleText.setText("请选择目录");
        titleLeft.setVisibility(View.VISIBLE);
        titleRight.setVisibility(View.VISIBLE);
        titleLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        titleRight.setImageResource(R.mipmap.ic_save_white_24dp);

        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderChooserActivity.this.finish();
            }
        });
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooserEnd();
            }
        });
    }

    private void setRecyclerView() {
        mData = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        //分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(0xFFE9E7E8)
                .size(1)
                .build());

        //设置适配器
        mAdapter = new FolderChooserAdapter(this, mData, new ItemClickCallback() {
            @Override
            public void onClick(View view, int position, FolderChooserInfo info) {
                onSelection(view, position, info);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private List<FolderChooserInfo> getContentsArray() {
        List<FolderChooserInfo> results = new ArrayList<>();
        if (parentContents == null) {
            if (canGoUp){
                FolderChooserInfo info = new FolderChooserInfo();
                info.setName("...");
                info.setFile(null);
                info.setImage(R.mipmap.back);
                results.add(info);
            }
            return results;
        }
        if (canGoUp){
            FolderChooserInfo info = new FolderChooserInfo();
            info.setName("...");
            info.setFile(null);
            info.setImage(R.mipmap.back);
            results.add(info);
        }
        results.addAll(parentContents);
        return results;
    }

    public void onSelection( View view, int position, FolderChooserInfo info) {
        if (canGoUp && position == 0) {
            if (parentFolder.isFile()) {
                parentFolder = parentFolder.getParentFile();
            }
            parentFolder = parentFolder.getParentFile();
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
                parentFolder = parentFolder.getParentFile();
            canGoUp = parentFolder.getParent() != null;
        } else {
            parentFolder = info.getFile();
            canGoUp = true;
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
                parentFolder = Environment.getExternalStorageDirectory();
        }
        if (parentFolder.isFile()) {
            ChooserEnd();
        }else{
            loading_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setData();
        }
    }

    private List<FolderChooserInfo> listFiles() {
        File[] contents = parentFolder.listFiles();
        List<FolderChooserInfo> results = new ArrayList<>();
        if (contents != null) {
            for (File fi : contents) {
                if (fi.isDirectory()){
                    FolderChooserInfo info = new FolderChooserInfo();
                    info.setName(fi.getName());
                    info.setFile(fi);
                    info.setImage(fileType(fi));
                    results.add(info);
                }
            }
            Collections.sort(results, new FolderSorter());
            return results;
        }
        return null;
    }

    private List<FolderChooserInfo> listFiles(String mimeType) {
        File[] contents = parentFolder.listFiles();
        List<FolderChooserInfo> results = new ArrayList<>();
        if (contents != null) {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            for (File fi : contents) {
                if (fi.isDirectory()) {
                    FolderChooserInfo info = new FolderChooserInfo();
                    info.setName(fi.getName());
                    info.setFile(fi);
                    info.setImage(fileType(fi));
                    results.add(info);
                } else {
                    if (fileIsMimeType(fi, mimeType, mimeTypeMap)) {
                        FolderChooserInfo info = new FolderChooserInfo();
                        info.setName(fi.getName());
                        info.setFile(fi);
                        info.setImage(fileType(fi));
                        results.add(info);
                    }
                }
            }
            Collections.sort(results, new FileSorter());
            return results;
        }
        return null;
    }

    boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        } else {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1) {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1);
            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null) {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType)) {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1) {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*")) {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1) {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            if (fileTypeMainType.equals(mimeTypeMainType)) {
                return true;
            }
        }
        return false;
    }

    private static class FileSorter implements Comparator<FolderChooserInfo> {
        @Override
        public int compare(FolderChooserInfo lhs, FolderChooserInfo rhs) {
            if (lhs.getFile().isDirectory() && !rhs.getFile().isDirectory()) {
                return -1;
            } else if (!lhs.getFile().isDirectory() && rhs.getFile().isDirectory()) {
                return 1;
            } else {
                return lhs.getName().compareTo(rhs.getName());
            }
        }
    }

    private static class FolderSorter implements Comparator<FolderChooserInfo> {
        @Override
        public int compare(FolderChooserInfo lhs, FolderChooserInfo rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public interface ItemClickCallback{
        void onClick(View view, int position, FolderChooserInfo info);
    }

    private void findViewById(){
        titleLeft = (ImageView) findViewById(R.id.title_left);
        titleRight = (ImageView) findViewById(R.id.title_right);
        titleText = (TextView) findViewById(R.id.title_text);
        savePath = (TextView) findViewById(R.id.save_path);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loading_view = (LinearLayout) findViewById(R.id.loading_view);
    }

    private void ChooserEnd(){
        File result = parentFolder;
        Intent intent = new Intent();
        intent.putExtra("file_path", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private int fileType(File file){
        int image = R.mipmap.type_file;
        if(file.isDirectory()){
            image = R.mipmap.type_folder;
        }else{
            try {
//            指定文件类型的图标
                String[] token = file.getName().split("\\.");
                String suffix = token[token.length - 1];
                if (suffix.equalsIgnoreCase("txt")) {
                    image = R.mipmap.type_txt;
                } else if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("gif")) {
                    image = R.mipmap.type_image;
                } else if (suffix.equalsIgnoreCase("mp3")) {
                    image = R.mipmap.type_mp3;
                } else if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("rmvb") || suffix.equalsIgnoreCase("avi")) {
                    image = R.mipmap.type_video;
                } else if (suffix.equalsIgnoreCase("apk")) {
                    image = R.mipmap.type_apk;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return image;
    }
}
