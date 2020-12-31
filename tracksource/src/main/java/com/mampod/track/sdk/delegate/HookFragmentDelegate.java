package com.mampod.track.sdk.delegate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.gyf.immersionbar.components.ImmersionFragment;
import com.mampod.track.sdk.listener.IPageCollection;
import com.mampod.track.sdk.annotation.SubPageOpen;
import com.mampod.track.sdk.tool.AutoTrackUtil;

import org.json.JSONObject;

/**
 * Fragment代理类
 *
 * @package com.mampod.track.sdk.delegate
 * @author: Jack-Lu
 * @date:
 */
public abstract class HookFragmentDelegate extends ImmersionFragment implements IPageCollection {

    /**
     * 如果有使用viewpager等复用fragment的情况  需要重写该方法，传入不同的值，以便能够进行aop区分统计
     */
    @Override
    public String getDes() {
        return "unknow";
    }

    @Override
    public JSONObject getProps() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    @Override
//    public void onResume() {
//        if (!isShow && mIsActivityCreated) {
//            onFragmentShow(true, getParentValue(), getRoutDes());
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        isShow = getUserVisibleHint();
//        if (mIsActivityCreated) {
//            onFragmentShow(isVisibleToUser, getParentValue(), getRoutDes());
//        }
//    }

    @Override
    public void onVisible() {
        super.onVisible();
        onFragmentShow(true, null, AutoTrackUtil.getScreenName(this));
    }

    /**
     * aop统计检查的方法，通过该方法的进行统计
     */
    @SubPageOpen
    public void onFragmentShow(boolean isVisibleToUser, String parent, String des) {

    }
}
