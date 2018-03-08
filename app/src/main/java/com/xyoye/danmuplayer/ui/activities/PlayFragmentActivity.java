package com.xyoye.danmuplayer.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.danmuplayer.R;
import com.xyoye.danmuplayer.database.DirectoryDao;
import com.xyoye.danmuplayer.ui.view.BatteryView;
import com.xyoye.danmuplayer.utils.BiliDanmukuParser;
import com.xyoye.danmuplayer.utils.GetFileName;
import com.xyoye.danmuplayer.utils.ListDataSave;
import com.xyoye.danmuplayer.utils.PlayerGesture;
import com.xyoye.danmuplayer.utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.VideoView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;

/**
 * 播放页
 */
public class PlayFragmentActivity extends BaseFragmentActivity implements View.OnClickListener{
    private final int HIDE_INTERVAL = 5000;// 隐藏控制View时间间隔
    private final int CHANGE_DANMU_PATH = 1;// 打开文件选择界面的返回码

    @BindView(R.id.vitamio_videoview)
    VideoView mVideoView;
    @BindView(R.id.player_loading_layout)
    View mLoadingView;
    @BindView(R.id.loading_text)
    TextView mLoadingInfo;
    @BindView(R.id.player_center_iv)
    View mVideoCenter;
    @BindView(R.id.player_bottom_layout)
    View mVideoBottomBar;
    @BindView(R.id.player_top_bar)
    View mVideoTopBar;
    @BindView(R.id.player_play_iv)
    ImageView mVideoPlayPause;
    @BindView(R.id.player_play)
    ImageView mVideoPlayPauseView;
    @BindView(R.id.player_current_time)
    TextView mCurrentTime;
    @BindView(R.id.player_total_time)
    TextView mTotalTime;
    @BindView(R.id.player_seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.orientation_change)
    ImageView mOrientationChange;
    @BindView(R.id.player_back)
    View mBack;
    @BindView(R.id.player_name)
    TextView mName;
    @BindView(R.id.battery_view)
    BatteryView batteryView;

    @BindView(R.id.danmu_setting)
    TextView danmu_setting;
    @BindView(R.id.danmu_switch)
    ImageView danmu_switch;
    @BindView(R.id.danmu_switch_text)
    TextView danmu_switch_text;
    @BindView(R.id.danmu_setting_layout)
    RelativeLayout danmu_setting_layout;
    @BindView(R.id.danmu_setting_list)
    LinearLayout danmu_setting_list;
    @BindView(R.id.danmu_block_setting)
    LinearLayout danmu_block_setting;
    @BindView(R.id.danmu_size_setting)
    LinearLayout danmu_size_setting;
    @BindView(R.id.danmu_speed_setting)
    LinearLayout danmu_speed_setting;
    @BindView(R.id.keyWord_group)
    LinearLayout keyword_group;
    @BindView(R.id.block_setting_display)
    TextView block_setting_display;
    @BindView(R.id.size_setting_display)
    TextView size_setting_display;
    @BindView(R.id.speed_setting_display)
    TextView speed_setting_display;
    @BindView(R.id.danmu_setting_close)
    ImageView danmu_setting_close;
    @BindView(R.id.open_close_danmu)
    ImageView open_close_danmu;
    @BindView(R.id.danmu_path)
    TextView danmu_path;
    @BindView(R.id.change_danmu_path)
    Button change_danmu_path;
    @BindView(R.id.add_block_keyWord_et)
    EditText add_keyword_et;
    @BindView(R.id.add_block_keyWord_bt)
    Button add_keyword_bt;
    @BindView(R.id.mobile_danmu)
    ImageView mobile_danmu;
    @BindView(R.id.botton_danmu)
    ImageView botton_danmu;
    @BindView(R.id.top_danmu)
    ImageView top_danmu;
    @BindView(R.id.text_size_big)
    TextView text_big;
    @BindView(R.id.text_size_middle)
    TextView text_middle;
    @BindView(R.id.text_size_small)
    TextView text_small;
    @BindView(R.id.text_move_fast)
    TextView move_fast;
    @BindView(R.id.text_move_middle)
    TextView move_middle;
    @BindView(R.id.text_move_slow)
    TextView move_slow;

    private String VIDEO_PATH;
    private String DANMU_PATH;

    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmukuContext;
    boolean first_start_danme = true;       //弹幕文件第一次启动
    boolean danmu_is_open = false;          //弹幕是否开启
    boolean danmu_view_hide = false;        //弹幕是否隐藏
    boolean moblie_danmu_hide = false;     //滚动弹幕是否隐藏
    boolean botton_danmu_hide = false;     //底部弹幕是否隐藏
    boolean top_danmu_hide = false;         //顶部弹幕是否隐藏
    boolean seekbar_is_change = false;
    private List<String> block_keyword_list;

    private GestureDetector mDetector;// 手势
    private PlayerGesture mPlayerGesture;
    private boolean player_check;
    InputStream danmu;

    private String name;// 视频名称
    private Uri playUri;// 播放地址
    private boolean isPlayComplete = false;// 是否播放完成
    private boolean isPlayError = false;// 是否播放出错
    private long currentPosition = 0;// 播放位置

    //存储屏蔽列表信息
    ListDataSave blockListSave;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected int getContentViewId() {
        return R.layout.activity_player;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPlayerUrl();

        initDanmuView();

        initVideoView();

        initSeekBar();

        initOther();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mVideoView.isPlaying()) {
            currentPosition = mVideoView.getCurrentPosition();
            onVideoPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(hiddenViewThread);
        mHandler.removeCallbacks(updateSeekBarThread);
        mVideoView.stopPlayback();
        new DirectoryDao(PlayFragmentActivity.this).UpdateFileDanmu(VIDEO_PATH,DANMU_PATH);
        super.onDestroy();
    }

    /**
     * 初始化播放地址
     */
    private void initPlayerUrl() {
        Intent intent = getIntent();
        DANMU_PATH = intent.getStringExtra("DANMU_URL");
        VIDEO_PATH = intent.getStringExtra("PLAY_URL");
        name = intent.getStringExtra("VIDEO_NAME");
        if (VIDEO_PATH == null) {
            Log.e("error","play url is null");
        } else {
            playUri = Uri.parse(VIDEO_PATH);
        }
    }

    /**
     * 初始化VideoView
     */
    private void initVideoView() {
        Vitamio.isInitialized(mContext);
        mVideoView.setMediaBufferingIndicator(mLoadingView);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mLoadingView.setVisibility(View.GONE);
                mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
                onVideoPlay();
                if (currentPosition != 0 && currentPosition < mVideoView.getDuration()) {
                    // 若不调用 VideoView.getDuration() 就进行 seekTo() 那么 seekTo() 无效！
                    // vitamio5.0 的问题？
                    mVideoView.seekTo(currentPosition);
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlayComplete = true;
                onPlaySRCChange(false);
                mLoadingView.setVisibility(View.GONE);
                mVideoCenter.setVisibility(View.VISIBLE);
                mHandler.removeCallbacks(updateSeekBarThread);
                mCurrentTime.setText(StringUtils.generateTime(mVideoView.getDuration()));
                mSeekBar.setProgress(mSeekBar.getMax());
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Utility.showToast(mContext, R.string.play_error);
                isPlayError = true;
                Log.i("VideoView on error!"," what:" + what + " extra:" + extra);
                return true;
            }
        });
        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent > 0 && percent < 100) {
                    String percentText = percent + "%";
                    mLoadingInfo.setText(percentText);
                } else {
                    mLoadingInfo.setText("");
                }
            }
        });
        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                onVideoPlay();
            }
        });
    }

    /**
     * 初始化DanmuView
     */
    private void initDanmuView(){
        // 设置最大显示行数
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        // 设置是否禁止重叠
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuView = findViewById(R.id.sv_danmaku);
        mDanmukuContext = DanmakuContext.create();
        mDanmukuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.0f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                    float percent = mSeekBar.getProgress() / (float) mSeekBar.getMax();
                    mDanmakuView.seekTo((long) (mVideoView.getDuration() * percent));
                    mDanmakuView.show();
                    Log.i("PREPARED","解析完成,弹幕已启动！！！");
                }
            });
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }
    /**
     * 初始化进度条
     */
    private void initSeekBar() {
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                float percent = seekbar.getProgress() / (float) seekbar.getMax();
                mVideoView.seekTo((int) (mVideoView.getDuration() * percent));
                if(danmu_is_open){
                    mDanmakuView.seekTo((long) (mVideoView.getDuration() * percent));
                }
                mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
                onVideoPlay();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                mHandler.removeCallbacks(updateSeekBarThread);
                mHandler.removeCallbacks(hiddenViewThread);
            }

            @Override
            public void onProgressChanged(SeekBar seekbar, int position, boolean arg2) {
                float percent = position / (float) seekbar.getMax() ;
                mCurrentTime.setText(StringUtils.generateTime(
                        (int) (mVideoView.getDuration() * percent)));
                seekbar_is_change = true;
            }
        });
    }

    /**
     * 初始化其他组件
     */
    private void initOther() {
        blockListSave = new ListDataSave(this,"DanmuBlock");

        mVideoBottomBar.setOnClickListener(this);
        mVideoCenter.setOnClickListener(this);
        mVideoPlayPause.setOnClickListener(this);
        mOrientationChange.setOnClickListener(this);
        mBack.setOnClickListener(this);

        if (name != null) {
            mName.setText(name);
        }

        // 添加手势监听
        player_check = true;
        mPlayerGesture = new PlayerGesture(this, mVideoView,
                (RelativeLayout) findViewById(R.id.player_root));
        mDetector = new GestureDetector(this, mPlayerGesture);
        mDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (player_check){
                    if (mVideoBottomBar.getVisibility() == View.VISIBLE) {
                        mHandler.post(hiddenViewThread);
                    } else {
                        showControllerBar();
                    }
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });


        if (playUri == null) {
            Utility.showToast(mContext, R.string.play_url_error);
        } else {
            mVideoView.setVideoURI(playUri);
            mLoadingView.setVisibility(View.VISIBLE);
        }

        if ("null".equals(DANMU_PATH)){
            danmu_path.setText("");
        }else {
            danmu_path.setText(new GetFileName().getName(DANMU_PATH));
        }
        danmu_switch.setOnClickListener(this);
        danmu_setting.setOnClickListener(this);
        danmu_setting_close.setOnClickListener(this);
        block_setting_display.setOnClickListener(this);
        size_setting_display.setOnClickListener(this);
        speed_setting_display.setOnClickListener(this);
        open_close_danmu.setOnClickListener(this);
        change_danmu_path.setOnClickListener(this);
        add_keyword_bt.setOnClickListener(this);
        mobile_danmu.setOnClickListener(this);
        botton_danmu.setOnClickListener(this);
        top_danmu.setOnClickListener(this);
        text_big.setOnClickListener(this);
        text_middle.setOnClickListener(this);
        text_small.setOnClickListener(this);
        move_fast.setOnClickListener(this);
        move_middle.setOnClickListener(this);
        move_slow.setOnClickListener(this);

        text_middle.setTextColor(Color.parseColor("#28d9f6"));
        move_middle.setTextColor(Color.parseColor("#28d9f6"));
        block_keyword_list = new ArrayList<>();
    }

    /**
     * 初始化屏蔽列表
     */
    private void initBlock(){
        block_keyword_list = blockListSave.getDataList("blockList");
        display_block_keyword();
    }

    /**
     * 更新进度条
     */
    private Runnable updateSeekBarThread = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(updateSeekBarThread, 1000);
            if (mVideoView.getDuration() != 0) {
                mSeekBar.setProgress((int) (mVideoView.getCurrentPosition() /
                        (float) mVideoView.getDuration() * 1000));
                mCurrentTime.setText(StringUtils.generateTime(
                        mVideoView.getCurrentPosition()));
                mTotalTime.setText(StringUtils.generateTime(
                        mVideoView.getDuration()));
            }
        }
    };

    /**
     * 隐藏控制视图
     */
    private Runnable hiddenViewThread = new Runnable() {
        @Override
        public void run() {
            Utility.translateAnimation(mVideoBottomBar, 0f, 0f, 0f, 1.0f, 400);
            mVideoBottomBar.setVisibility(View.GONE);
            Utility.translateAnimation(mVideoTopBar, 0f, 0f, 0f, -1.0f, 400);
            mVideoTopBar.setVisibility(View.GONE);
        }
    };

    /**
     * 显示控制视图
     */
    private void showControllerBar() {
        mHandler.removeCallbacks(hiddenViewThread);
        if (mVideoView.isPlaying()) {
            onPlaySRCChange(true);
        } else {
            onPlaySRCChange(false);
        }
        Utility.translateAnimation(mVideoBottomBar,
                0f, 0f, 1.0f, 0f, 300);
        mVideoBottomBar.setVisibility(View.VISIBLE);
        Utility.translateAnimation(mVideoTopBar,
                0f, 0f, -1.0f, 0f, 300);
        mVideoTopBar.setVisibility(View.VISIBLE);

        mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
    }

    /**
     * 重新倒计时 隐藏控制视图
     */
    private void removeHideControllerBar() {
        mHandler.removeCallbacks(hiddenViewThread);
        mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
    }

    /**
     * 根据播放状态改变 切换播放图标
     */
    private void onPlaySRCChange(boolean isPlaying) {
        if (isPlaying) {
            mVideoPlayPauseView.setImageResource(R.drawable.play_pause_icon);
        } else {
            mVideoPlayPauseView.setImageResource(R.drawable.play_start_icon);
        }
    }

    /**
     * 暂停
     */
    private void onVideoPause() {
        mVideoView.pause();
        mDanmakuView.pause();
        onPlaySRCChange(false);
        mVideoCenter.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(updateSeekBarThread);
    }

    /**
     * 播放
     */
    private void onVideoPlay() {
        mVideoView.start();
        mDanmakuView.resume();
        onPlaySRCChange(true);
        mVideoCenter.setVisibility(View.GONE);
        mHandler.post(updateSeekBarThread);
    }

    /**
     * 点击播放按钮
     */
    private void onClickPlayButton() {
        removeHideControllerBar();
        if (isPlayComplete) {
            currentPosition = 0;
            mVideoView.setVideoURI(playUri);
            isPlayComplete = false;
        } else {
            if (mVideoView.isValid()) {
                if (mVideoView.isPlaying()) {
                    onVideoPause();
                } else {
                    onVideoPlay();
                }
            }
        }
    }

    /**
     * 解析弹幕
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            assert loader != null;
            loader.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_bottom_layout:
                removeHideControllerBar();
                break;
            case R.id.player_center_iv:
            case R.id.player_play_iv:
                onClickPlayButton();
                break;
            case R.id.orientation_change:
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.player_back:
                finish();
                break;
            case R.id.danmu_setting:
                openDanmuSetting();
                break;
            case R.id.danmu_switch:
                changeDanmuSwitch();
                break;
            case R.id.danmu_setting_close:
                closeDanmuSetting();
                break;
            case R.id.open_close_danmu:
                danmuOpenClose();
                break;
            case R.id.change_danmu_path:
                changeDanmuPath();
                break;
            case R.id.add_block_keyWord_bt:
                add_block_keyword();
                break;
            case R.id.block_setting_display:
                displaySetting(1);
                break;
            case R.id.size_setting_display:
                displaySetting(2);
                break;
            case R.id.speed_setting_display:
                displaySetting(3);
                break;
            case R.id.mobile_danmu:
                mobileDanmuSetting();
                break;
            case R.id.botton_danmu:
                bottonDanmuSetting();
                break;
            case R.id.top_danmu:
                topDanmuSetting();
                break;
            case R.id.text_size_big:
                setDanmuTextSize(1.2f,Color.GRAY,Color.GRAY,Color.parseColor("#28d9f6"));
                break;
            case R.id.text_size_middle:
                setDanmuTextSize(1.0f,Color.GRAY,Color.parseColor("#28d9f6"),Color.GRAY);
                break;
            case R.id.text_size_small:
                setDanmuTextSize(0.8f,Color.parseColor("#28d9f6"),Color.GRAY,Color.GRAY);
                break;
            case R.id.text_move_fast:
                setDanmuTextSpeed(1.0f,Color.GRAY,Color.GRAY,Color.parseColor("#28d9f6"));
                break;
            case R.id.text_move_middle:
                setDanmuTextSpeed(1.2f,Color.GRAY,Color.parseColor("#28d9f6"),Color.GRAY);
                break;
            case R.id.text_move_slow:
                setDanmuTextSpeed(1.4f,Color.parseColor("#28d9f6"),Color.GRAY,Color.GRAY);
                break;
            default:
                break;
        }
    }
    /*
   *改变播放面板中的弹幕开关
   */
   public void changeDanmuSwitch(){
       if (danmu_is_open){
           if (danmu_view_hide){
               mDanmakuView.show();
               danmu_switch.setImageResource(R.drawable.bili_player_danmaku_is_open);
               danmu_switch_text.setText("弹幕开");
               danmu_view_hide = false;
           }else{
               mDanmakuView.hide();
               danmu_switch.setImageResource(R.drawable.bili_player_danmaku_is_closed);
               danmu_switch_text.setText("弹幕关");
               danmu_view_hide = true;
           }
       }
   }

    /*
    *开启弹幕设置面板
     */
    public void openDanmuSetting(){
        danmu_setting_layout.setVisibility(View.VISIBLE);
        Utility.translateAnimation(danmu_setting_layout,
                30f, 0f, 0f, 0f, 300);
        player_check = false;
        if (mVideoBottomBar.getVisibility() == View.VISIBLE) {
            mHandler.post(hiddenViewThread);
        }
    }

   /*
   *关闭弹幕设置面板
    */
    public void closeDanmuSetting(){
        danmu_setting_layout.setVisibility(View.INVISIBLE);
        player_check = true;
    }

    /*
    *弹幕设置面板中开启or关闭弹幕
     */
    public void danmuOpenClose(){
        if(!isPlayError){
            if (danmu_is_open){
                danmu_setting_list.setVisibility(View.GONE);
                displaySetting(0);
                open_close_danmu.setImageResource(R.drawable.ic_live_switch_off);
                danmu_switch.setImageResource(R.drawable.bili_player_danmaku_is_closed);
                danmu_switch_text.setText("弹幕关");
                mDanmakuView.hide();
                danmu_is_open = false;
            }else {
                if (!"".equals(DANMU_PATH)){
                    try {
                        if (first_start_danme){
                            mDanmakuView.release();
                            danmu = new FileInputStream(DANMU_PATH);
                            BaseDanmakuParser mParser = createParser(danmu);
                            mDanmakuView.prepare(mParser, mDanmukuContext);
                            first_start_danme = false;
                        }else {
                            mDanmakuView.resume();
                            mDanmakuView.show();
                            danmu_view_hide = false;
                        }
                        danmu_setting_list.setVisibility(View.VISIBLE);
                        danmu_switch.setImageResource(R.drawable.bili_player_danmaku_is_open);
                        danmu_switch_text.setText("弹幕开");
                        open_close_danmu.setImageResource(R.drawable.ic_live_switch_on);
                        danmu_is_open = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(PlayFragmentActivity.this,"请选择弹幕地址",Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Utility.showToast(mContext, R.string.play_error);
        }

    }

    /**
     * 改变弹幕地址
     */
    public void changeDanmuPath(){
        first_start_danme = true;
        danmu_setting_list.setVisibility(View.GONE);
        displaySetting(0);
        open_close_danmu.setImageResource(R.drawable.ic_live_switch_off);
        danmu_switch.setImageResource(R.drawable.bili_player_danmaku_is_closed);
        danmu_switch_text.setText("弹幕关");
        mDanmakuView.hide();
        danmu_is_open = false;
        Intent intent = new Intent(PlayFragmentActivity.this, FolderChooserActivity.class);
        intent.putExtra("isFolderChooser", false);
        intent.putExtra("mimeType", "text/*");
        startActivityForResult(intent,CHANGE_DANMU_PATH);
    }

    /*
    *添加删除屏蔽关键词
     */
    public void add_block_keyword(){
        String keyword = add_keyword_et.getText().toString();
        if ((!block_keyword_list.contains(keyword)) && !"".equals(keyword.trim())){
            block_keyword_list.add(keyword.trim());
            blockListSave.setDataList("blockList",block_keyword_list);
            display_block_keyword();
            Toast.makeText(this,"已屏蔽："+keyword,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 展示屏蔽关键词
     */
    public void display_block_keyword(){
        keyword_group.removeAllViews();
        for (int i=0;i<block_keyword_list.size();i++){
            String keyword = block_keyword_list.get(i);
            final View view = View.inflate(PlayFragmentActivity.this, R.layout.keyword_layout, null);
            TextView tv = view.findViewById(R.id.key_word);
            tv.setText(keyword);
            view.setTag(i);
            //添加屏蔽词
            mDanmukuContext.addKeyWordBlackList(keyword);
            //保存屏蔽列表
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int number = (Integer) v.getTag();
                    mDanmukuContext.removeKeyWordBlackList(block_keyword_list.get(number));
                    block_keyword_list.remove(number);
                    blockListSave.setDataList("blockList",block_keyword_list);
                    keyword_group.removeView(v);
                }
            });
            keyword_group.addView(view);
        }
    }

    /**
     * 显示设置列表
     */
    public void displaySetting(int s){
        switch (s){
            case 0:
                block_setting_display.setTextColor(Color.WHITE);
                size_setting_display.setTextColor(Color.WHITE);
                speed_setting_display.setTextColor(Color.WHITE);
                danmu_block_setting.setVisibility(View.GONE);
                danmu_size_setting.setVisibility(View.GONE);
                danmu_speed_setting.setVisibility(View.GONE);
                break;
            case 1:
                if(danmu_block_setting.getVisibility() == View.VISIBLE){
                    block_setting_display.setTextColor(Color.WHITE);
                    danmu_block_setting.setVisibility(View.GONE);
                }else{
                    block_setting_display.setTextColor(Color.parseColor("#28d9f6"));
                    size_setting_display.setTextColor(Color.WHITE);
                    speed_setting_display.setTextColor(Color.WHITE);
                    danmu_block_setting.setVisibility(View.VISIBLE);
                    danmu_size_setting.setVisibility(View.GONE);
                    danmu_speed_setting.setVisibility(View.GONE);

                    //初始化屏蔽列表
                    initBlock();
                }
                break;
            case 2:
                if (danmu_size_setting.getVisibility() == View.VISIBLE){
                    size_setting_display.setTextColor(Color.WHITE);
                    danmu_size_setting.setVisibility(View.GONE);
                }else{
                    block_setting_display.setTextColor(Color.WHITE);
                    size_setting_display.setTextColor(Color.parseColor("#28d9f6"));
                    speed_setting_display.setTextColor(Color.WHITE);
                    danmu_block_setting.setVisibility(View.GONE);
                    danmu_size_setting.setVisibility(View.VISIBLE);
                    danmu_speed_setting.setVisibility(View.GONE);
                }
                break;
            case 3:
                if(danmu_speed_setting.getVisibility() == View.VISIBLE){
                    speed_setting_display.setTextColor(Color.WHITE);
                    danmu_speed_setting.setVisibility(View.GONE);
                }else{
                    block_setting_display.setTextColor(Color.WHITE);
                    size_setting_display.setTextColor(Color.WHITE);
                    speed_setting_display.setTextColor(Color.parseColor("#28d9f6"));
                    danmu_block_setting.setVisibility(View.GONE);
                    danmu_size_setting.setVisibility(View.GONE);
                    danmu_speed_setting.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * 开启or关闭滚动弹幕
     */
    public void mobileDanmuSetting(){
        if (moblie_danmu_hide){
            mDanmukuContext.setR2LDanmakuVisibility(true);
            mDanmukuContext.setL2RDanmakuVisibility(true);
            moblie_danmu_hide = false;
            mobile_danmu.setImageResource(R.drawable.moblie_danmu_unchecked);
        }else{
            mDanmukuContext.setR2LDanmakuVisibility(false);
            mDanmukuContext.setL2RDanmakuVisibility(false);
            moblie_danmu_hide = true;
            mobile_danmu.setImageResource(R.drawable.moblie_danmu_checked);
        }
    }

    /**
     * 开启or关闭底部弹幕
     */
    public void bottonDanmuSetting(){
        if (botton_danmu_hide){
            mDanmukuContext.setFBDanmakuVisibility(true);
            botton_danmu_hide = false;
            botton_danmu.setImageResource(R.drawable.botton_danmu_unchecked);
        }else{
            mDanmukuContext.setFBDanmakuVisibility(false);
            botton_danmu_hide = true;
            botton_danmu.setImageResource(R.drawable.botton_danmu_checked);
        }
    }

    /**
     * 开启or关闭顶部弹幕
     */
    public void topDanmuSetting(){
        if (top_danmu_hide){
            mDanmukuContext.setFTDanmakuVisibility(true);
            top_danmu_hide = false;
            top_danmu.setImageResource(R.drawable.top_danmu_unchecked);
        }else{
            mDanmukuContext.setFTDanmakuVisibility(false);
            top_danmu_hide = true;
            top_danmu.setImageResource(R.drawable.top_danmu_checked);
        }
    }

    /**
     * 设置弹幕字体大小
     */
    public void setDanmuTextSize(float p,int small,int middle,int big){
        mDanmukuContext.setScaleTextSize(p);
        text_small.setTextColor(small);
        text_middle.setTextColor(middle);
        text_big.setTextColor(big);
    }

    /**
     * 设置弹幕速度快慢
     */
    public void setDanmuTextSpeed(float p,int slow,int middle,int fast){
        mDanmukuContext.setScrollSpeedFactor(p);
        move_slow.setTextColor(slow);
        move_middle.setTextColor(middle);
        move_fast.setTextColor(fast);
    }

    /**
     * 获取选取的弹幕文件路径
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case CHANGE_DANMU_PATH:
                    File file = (File) data.getSerializableExtra("file_path");
                    DANMU_PATH = file.getAbsolutePath();
                    danmu_path.setText(new GetFileName().getName(DANMU_PATH));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //用于判断是点击还是滑动
    private float DownX = 0;
    private float DownY = 0;
    private float moveX = 0;
    private float moveY = 0;
    long currentMS = 0;

    /**
     * 拉动屏幕更新进度，更新弹幕
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int action = event.getAction();

        if (pointerCount == 1){
            if (danmu_setting_layout.getVisibility() != View.VISIBLE){
                if (action == MotionEvent.ACTION_DOWN) {
                    mPlayerGesture.onFingerDown();
                    currentMS = System.currentTimeMillis();     //获取系统时间
                    DownX = event.getX();//float DownX
                    DownY = event.getY();//float DownY
                    moveX = 0;
                    moveY = 0;
                } else if(action == MotionEvent.ACTION_MOVE){
                    moveX += Math.abs(event.getX() - DownX);//X轴距离
                    moveY += Math.abs(event.getY() - DownY);//y轴距离
                    DownX = event.getX();
                    DownY = event.getY();
                }else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    mPlayerGesture.onFingerUp();
                    long moveTime = System.currentTimeMillis()-currentMS;
                    seekbar_is_change = false;
                    if(danmu_is_open){
                        if(moveTime>100&&(moveX>10||moveY>10)){
                            new Thread(){
                                @Override
                                public void run(){
                                    //如果进度条更新，则跳出循环，启动弹幕
                                    while(!seekbar_is_change){
                                        System.out.println("waiting for seekbar change");
                                    }
                                    float danmu_percent = mSeekBar.getProgress() / (float) mSeekBar.getMax();
                                    mDanmakuView.seekTo((long) (mVideoView.getDuration() * danmu_percent));
                                }
                            }.start();
                        }
                    }
                }
                return mDetector.onTouchEvent(event);
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

}
