package com.xyoye.DanmuPlayer.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.DanmuPlayer.R;
import com.xyoye.DanmuPlayer.ui.adpter.SmbAdapter;
import com.xyoye.DanmuPlayer.bean.SmbInfo;
import com.xyoye.DanmuPlayer.utils.SmbUtil;
import com.xyoye.DanmuPlayer.utils.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmbActivity extends AppCompatActivity {
    private final static int ERROR_CODE = 101;

    ImageView titleLeft;        //标题栏左边按钮
    TextView titleText;         // 标题栏title
    TextView savePath;
    RecyclerView recyclerView;
    LinearLayout loading_view;
    SmbInfo smbInfo;

    private String smbUrl = "";
    private String baseUrl = "";

    private SmbAdapter mAdapter;
    private List<SmbInfo> mData;

    private List<SmbInfo> parentContents;
    private boolean canGoUp = false;

    private ExecutorService singleThreadExecutor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    savePath.setText(smbUrl);
                    mData.clear();
                    mData.addAll(getContentsArray());
                    mAdapter.notifyDataSetChanged();

                    loading_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                case ERROR_CODE:
                    String mess = (String)msg.obj;
                    Toast.makeText(SmbActivity.this, mess, Toast.LENGTH_SHORT).show();
                    loading_view.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smb_folder_chooser);

        initData();
        
        initView();
        
        setData();
    }


    private void initData(){
        mData = new ArrayList<>();

        baseUrl = getIntent().getStringExtra("smbUrl");
        smbUrl = baseUrl;
        singleThreadExecutor = Executors.newSingleThreadExecutor();

        smbInfo = new SmbInfo();
    }
    
    private void initView() {
        titleLeft = (ImageView) this.findViewById(R.id.title_left);
        titleText = (TextView) this.findViewById(R.id.title_text);
        savePath = (TextView)this.findViewById(R.id.save_path);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        loading_view = (LinearLayout) this.findViewById(R.id.loading_view);
        
        titleText.setText("请选择文件");
        titleLeft.setVisibility(View.VISIBLE);
        titleLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmbActivity.this.finish();
            }
        });
        
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(0xFFE9E7E8)
                .size(1)
                .build());
        mAdapter = new SmbAdapter(this, mData, new ItemClickCallback() {
            @Override
            public void onClick(View view, int position, SmbInfo info) {
                onSelection(view, position, info);
            }
        });
        recyclerView.setAdapter(mAdapter);
        savePath.setText(smbUrl);
    }

    private void setData(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                parentContents = listFiles();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private List<SmbInfo> getContentsArray() {
        List<SmbInfo> results = new ArrayList<>();
        if (parentContents == null) {
            if (canGoUp){
                SmbInfo info = new SmbInfo();
                info.setName("...");
                info.setDirectory(false);
                info.setImage(R.mipmap.back);
                results.add(info);
            }
            return results;
        }
        if (canGoUp){
            SmbInfo info = new SmbInfo();
            info.setName("...");
            info.setDirectory(false);
            info.setImage(R.mipmap.back);
            results.add(info);
        }
        results.addAll(parentContents);
        return results;
    }

    public void onSelection(View view, int position, SmbInfo info) {
        if (canGoUp && position == 0) {
            if (!info.isDirectory()) {
                smbUrl = smbUrl.substring(0,smbUrl.length()-1);
                smbUrl = smbUrl.substring(0,smbUrl.lastIndexOf("/")+1);
            }
            if (smbUrl.equals(baseUrl)){
                canGoUp = false;
            }
            setData();
        }else if (!info.isDirectory()) {
            ChooserEnd(position);
        }else{
            smbUrl = smbUrl + info.getName()+"/";
            canGoUp = true;
            loading_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setData();
        }
    }

    private List<SmbInfo> listFiles() {
        List<SmbInfo> results = new SmbUtil().getFileNamesFromSmb(smbUrl);
        Message message = new Message();
        message.what = ERROR_CODE;
        if (results == null) {
            results = new ArrayList<>();
            message.obj = "连接失败...";
            handler.sendMessage(message);
        } else if (results.size() == 0) {
            message.obj = "空文件夹";
            handler.sendMessage(message);
        }else {
            Collections.sort(results, new FileSorter());
        }
        return results;
    }

    private static class FileSorter implements Comparator<SmbInfo> {
        @Override
        public int compare(SmbInfo lhs, SmbInfo rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else {
                return lhs.getName().compareTo(rhs.getName());
            }
        }
    }

    public interface ItemClickCallback{
        void onClick(View view, int position, SmbInfo info);
    }

    /**
     * 选择完毕，返回本地缓存文件路径
     */
    private void ChooserEnd(int position){
        final String selectUrl = smbUrl + mData.get(position).getName() + "/";
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = ERROR_CODE;

                String localPath = new SmbUtil().loadFromSmb(selectUrl);
                if ("超过5M".equals(localPath)){
                    message.obj = "错误，文件大小超过5M";
                    handler.sendMessage(message);
                }else if ("内存不足".equals(localPath)){
                    message.obj = "错误，可用内存小于5M";
                    handler.sendMessage(message);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("file_path", localPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
