package com.example.xyy.DanmuPlayer.utils.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.vov.vitamio.ThumbnailUtils;

/**
 * Created by YE on 2017/12/3.
 */

public class ImageLoader {
    Map<String, Bitmap> imgCache = Collections.synchronizedMap(new HashMap<String, Bitmap>());
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    Handler handler;
    Context context;

    public ImageLoader() {
        handler = new Handler();
    }

    public void displayImage(String url, ImageView imageView, Context context){
        this.context = context;
        imageViews.put(imageView, url);
        Bitmap map = imgCache.get(url);
        if(map != null){
            imageView.setImageBitmap(map);
            return;
        }
        createThreadAndDownloadImage(url, imageView);
    }

    public void createThreadAndDownloadImage(String url, ImageView imageView){
        Thread th = new DownloadImageThread(url, imageView);
        th.start();
    }

    //从网络获取图片
    private Bitmap getBitmap(String url){
        //Bitmap bitmap =  android.media.ThumbnailUtils.createVideoThumbnail (url, MediaStore.Images.Thumbnails.MICRO_KIND);
       /* MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(url);
        Bitmap bitmap = media.getFrameAtTime();*/

        // 缓存缩略图
        Bitmap bitmap = android.media.ThumbnailUtils.createVideoThumbnail(
                url, MediaStore.Images.Thumbnails.MICRO_KIND);
        if (bitmap == null) {// vitamio取不到，用原生方法取
            bitmap = android.media.ThumbnailUtils.createVideoThumbnail(
                    url, MediaStore.Images.Thumbnails.MICRO_KIND);
        }
        return bitmap;
    }


    class DownloadImageThread extends Thread{
        String url;
        ImageView img;
        public DownloadImageThread(String u, ImageView imageView){
            url = u;
            img = imageView;
        }

        public void run(){
            Bitmap bmp = getBitmap(url);
            imgCache.put(url, bmp);
            handler.post(new SetImageViewRunnable(img, bmp, url));

        }
    }

    boolean imageViewReused(String url, ImageView img){
        String tag = imageViews.get(img);
        if(tag == null || !tag.equals(url))
            return true;
        return false;
    }

    class SetImageViewRunnable implements Runnable{

        private ImageView image;
        private Bitmap bmp;
        private String url;
        public SetImageViewRunnable(ImageView img, Bitmap bp, String u){
            image = img;
            bmp = bp;
            url = u;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(imageViewReused(url, image))
                return;
            if(bmp != null)
                image.setImageBitmap(bmp);
        }

    }
}