package com.example.xyy.DanmuPlayer.utils.others;

import android.app.Application;
import android.graphics.Bitmap;

import com.example.xyy.DanmuPlayer.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by YE on 2017/8/25.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                //保存的每个缓存文件的最大长宽
                .memoryCacheExtraOptions(264, 165)
                // 设置缓存的详细信息，最好不要设置这个
                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
                // 线程池内加载的数量
                .threadPoolSize(3)
                // 线程优先级
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                //你可以通过自己的内存缓存实现
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                //硬盘缓存50MB
                .diskCacheSize(50 * 1024 * 1024)
                //将保存的时候的URI名称用MD5
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                //将保存的时候的URI名称用HASHCODE加密
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //缓存的File数量
                .diskCacheFileCount(100)
                //自定义缓存路径
                //.diskCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory()+"/myApp/imgCache")))// 自定义缓存路径
                .defaultDisplayImageOptions(getDisplayOption())
                // connectTimeout (5 s), readTimeout (30 s)超时时间
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000,30 * 1000))
                //输出日志 观察LOGCAT中日志
                .writeDebugLogs()
                .build();
        // 全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

    private DisplayImageOptions getDisplayOption(){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                //设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.video_img)
                //设置图片URI为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.video_img)
                //设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.drawable.video_img)
                //设置下载图片是否缓存在内存中
                .cacheInMemory(true)
                //设置图片是否缓存在sd卡中
                .cacheOnDisc(true)
                //是否考虑JPG图像EXIF参数（旋转，翻转）
                .considerExifParams(true)
                //设置图片以如何的编码方式显示
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                //设置图片的解码类型
                .bitmapConfig(Bitmap.Config.RGB_565)
                //设置下载前的延迟时间
                //.delayBeforeLoading(int delaynMillis)
                //设置图片加入缓存前，对bitmap进行设置
                //.preProcessor(BitmapProcessor preProcessor)
                //设置图片在下载前是否重置、复位
                .resetViewBeforeLoading(true)
                //是否设置圆角，弧度为多少
                .displayer(new RoundedBitmapDisplayer(20))
                //是否图片加载好后渐入动画时间
                .displayer(new FadeInBitmapDisplayer(100))
                .build();
        return options;
    }
}
