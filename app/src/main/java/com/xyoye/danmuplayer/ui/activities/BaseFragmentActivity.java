package com.xyoye.danmuplayer.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

/**
 * activity父类
 * Created by xyy on 2017/2/6
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentViewId());
        ButterKnife.bind(this);
    }

    protected abstract int getContentViewId();
}
