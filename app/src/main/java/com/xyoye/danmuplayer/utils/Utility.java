package com.xyoye.danmuplayer.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.danmuplayer.R;

/**
 * 工具类
 */
public class Utility {
    /**
     * 位移动画
     */
    public static void translateAnimation(View view, float xFrom, float xTo,
                                          float yFrom, float yTo, long duration) {

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, xFrom, Animation.RELATIVE_TO_SELF, xTo,
                Animation.RELATIVE_TO_SELF, yFrom, Animation.RELATIVE_TO_SELF, yTo);
        translateAnimation.setFillAfter(false);
        translateAnimation.setDuration(duration);
        view.startAnimation(translateAnimation);
        translateAnimation.startNow();
    }

    private static Toast toast;

    public static void showToast(Context context, int content) {
        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View v = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            ((TextView) v.findViewById(R.id.toast_msg)).setText(content);
            toast.setView(v);
        } else {
            ((TextView) toast.getView().findViewById(R.id.toast_msg)).setText(content);
        }
        toast.show();
    }
}
