package com.mampod.track.sdk.delegate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.mampod.track.sdk.annotation.PageResume;
import com.mampod.track.sdk.annotation.PageStop;
import com.mampod.track.sdk.listener.IPageCollection;
import com.mampod.track.sdk.annotation.PageExit;
import com.mampod.track.sdk.annotation.PageOpen;
import com.mampod.track.sdk.tool.AutoTrackUtil;

import org.json.JSONObject;

/**
 * @package： com.mampod.ergedd.statistics.sdk.delegate
 * @Des: Activity界面代理类
 * @author: Jack-Lu
 * @time: 2020/10/26 下午3:06
 * @change:
 * @changtime:
 * @changelog:
 */
public class HookActivityDelegate extends FragmentActivity implements IPageCollection {


    @Override
    @PageOpen
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @PageExit
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    @PageStop
    protected void onStop() {
        super.onStop();
    }

    @Override
    @PageResume
    protected void onResume() {
        super.onResume();
    }

    @Override
    public String getDes() {
        return "unknow";
    }

    @Override
    public JSONObject getProps() {
        return null;
    }
}
