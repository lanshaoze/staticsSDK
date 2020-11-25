package com.mampod.track.sdk.config;

import android.content.Context;
import android.text.TextUtils;

import com.mampod.track.hook.TrackLog;
import com.mampod.track.sdk.TrackAgent;
import com.mampod.track.sdk.model.CommonInfo;

import java.lang.ref.WeakReference;

/**
 * @package： com.mampod.ergedd.statistics
 * @Des: 统计SDK
 * @author: Jack-Lu
 * @time: 2018/9/26 下午5:13
 * @change:
 * @changtime:
 * @changelog:
 */
public class TrackSDK {

    /**
     * 采用弱引用来回收单例对象
     */
    private static WeakReference<TrackSDK> WeakReferenceInstance;

    /**
     * 增加弱引用管理
     *
     * @return
     */
    private static TrackSDK getManager() {
        if (WeakReferenceInstance == null || WeakReferenceInstance.get() == null) {
            WeakReferenceInstance = new WeakReference<>(new TrackSDK());
        }
        return WeakReferenceInstance.get();
    }

    private ConfigParam P;
    private static volatile TrackSDK instance;

    //标记是否在前台运行
    private boolean isForground = true;

    public boolean isForground() {
        return isForground;
    }

    public void setForground(boolean forground) {
        isForground = forground;
    }

    public void setP(ConfigParam p) {
        P = p;
    }

    public ConfigParam getP() {
        return P;
    }

    public static TrackSDK getInstance() {
        if (instance == null) {
            synchronized (TrackSDK.class) {
                if (instance == null) {
                    instance = getManager();
                }
            }
        }
        return instance;
    }

    private TrackSDK() {
    }


    /**
     * 初始化操作
     */
    public void init(String appId) {

        try {
            if (P == null || TextUtils.isEmpty(appId)) return;
            new TrackAgent.EventConfBuilder(P.context)
                    .configUrl(P.proxUrl)
                    .configAppid(appId)
                    .create();
            //统计初始化
            TrackLog.staticsInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 增加配置
     */
    public static class ConfigBuilder {
        private ConfigParam p;

        public ConfigBuilder(Context context) {
            this.p = new ConfigParam();
            p.context = context;
        }

        public ConfigBuilder configUrl(String url) {
            p.proxUrl = url;
            return this;
        }

        public ConfigParam getP() {
            return p;
        }

//        public TrackSDK create() {
//            TrackSDK sdk = getInstance();
//            sdk.setP(p);
//            return sdk;
//        }
    }

    public TrackSDK setBuilder(ConfigBuilder builder) {
        if (builder != null) {
            getInstance().setP(builder.getP());
        }

        return getInstance();
    }

    /**
     * 可扩展参数类
     */

    public static class ConfigParam {
        public Context context;
        public String proxUrl;
        public CommonInfo info; // 设备公共参数
        public long initTime; //初始化时间

        public ConfigParam() {
            initTime = System.currentTimeMillis();
        }
    }
}
