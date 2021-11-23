package com.mampod.track.sdk.delegate;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mampod.track.sdk.annotation.PageResume;
import com.mampod.track.sdk.annotation.PageStop;
import com.mampod.track.sdk.listener.IPageCollection;
import com.mampod.track.sdk.annotation.PageExit;
import com.mampod.track.sdk.annotation.PageOpen;

import org.json.JSONObject;
/**
 * Activity界面代理类
 *
 * @package com.mampod.track.sdk.delegate
 * @author: Jack-Lu
 * @date:
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
