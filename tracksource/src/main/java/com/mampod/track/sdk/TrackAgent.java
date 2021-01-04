package com.mampod.track.sdk;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mampod.track.sdk.constants.StatisBusiness;
import com.mampod.track.sdk.io.LogReport;
import com.mampod.track.sdk.model.CommonInfo;
import com.mampod.track.sdk.tool.AppUtils;
import com.mampod.track.sdk.tool.ChannelUtil;
import com.mampod.track.sdk.tool.DeviceUtils;
import com.mampod.track.sdk.tool.NetStateUtils;
import com.mampod.track.sdk.tool.PropertyUtil;
import com.mampod.track.sdk.annotation.Transient;
import com.mampod.track.sdk.tool.RandomUtil;
import com.mampod.track.sdk.tool.SecurityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 事件上报代理类
 *
 * @package com.mampod.track.sdk
 * @author: Jack-Lu
 * @date: 2020/11/10 下午4:04
 */
public class TrackAgent {

    private static final String TAG = TrackAgent.class.getSimpleName();

    /**
     * 采用弱引用来回收单例对象
     */
    private static WeakReference<TrackAgent> WeakReferenceInstance;

    /**
     * 增加弱引用管理
     *
     * @return
     */
    private static TrackAgent getManager() {
        if (WeakReferenceInstance == null || WeakReferenceInstance.get() == null) {
            WeakReferenceInstance = new WeakReference<>(new TrackAgent());
        }
        return WeakReferenceInstance.get();
    }

    //上报接口
    private String baseUrl;
    //参数配置
    private EventParam P;

    public static class Inner {
        public static TrackAgent agent = getManager();
    }

    public static TrackAgent getInstance() {
        return Inner.agent;
    }

    public TrackAgent() {
//        baseUrl = "http://tc.ergedd.com/app.gif?";
    }

    public EventParam getP() {
        return P;
    }

    public void setP(EventParam p) {
        P = p;
        if (!TextUtils.isEmpty(P.proxyUrl)) {
            baseUrl = P.proxyUrl;
        }
    }

    /**
     * 初始化AppId
     *
     * @param appId
     */
    public void init(String appId) {
        if (P != null) {
            P.setA(appId);
        }
    }

    /**
     * 具体的上报事件
     *
     * @param p
     * @param k
     * @param m
     * @param e
     */
    public synchronized void onEvent(StatisBusiness.Scene p, StatisBusiness.Event k, StatisBusiness.Action m, String e) {

        try {
            if (TextUtils.isEmpty(P.getA())) {
                Log.d(TAG, "no appid");
                return;
            }
            if (TextUtils.isEmpty(baseUrl)) {
                Log.d(TAG, "no server url");
                return;
            }
            // 1. 装配数据
            P.p = p;
            P.k = k;
            P.m = m;
            P.setE(e);
            P.setT(System.currentTimeMillis());
            P.setG(String.valueOf(SecurityUtils.getCrc32Value(P.getA() + P.getCommonInfo().getPk() + P.getR() + P.getT())));
            //修正app网络情况
            P.getCommonInfo().setN(NetStateUtils.getNetWorkStatusInfo(P.context).getmType());
            String proty = baseUrl + P.splitProperty();
//            String proty = P.splitProperty();
            // 2. 上报服务器
            Log.e(TAG, "log:\n" + "p=" + p + "\n&k=" + k + "\n&m=" + m + "\n&e=" + e + "\n&a=" + P.a + "\n&g=" + P.g + "\n&t=" + P.t + "\n&r=" + P.r);
            Log.e(TAG, "最终拼接：" + proty);
            pushServer(proty);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    /**
     * 统计事件上报
     */
    private void pushServer(String url) {
        if (!AppUtils.isConnected(P.context)) return;
        try {
            LogReport.getInstance().getData(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "上报失败");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    Log.d(TAG, "上报成功");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 采用构造器模式，开放配置
     */
    public static class EventConfBuilder {
        private EventParam parm;

        public EventConfBuilder(Context context) {
            parm = new EventParam();
            parm.context = context;
        }

        public EventConfBuilder configUrl(String url) {
            parm.proxyUrl = url;
            return this;
        }

        public EventConfBuilder configAppid(String appId) {
            parm.setA(appId);
            return this;
        }

        public EventConfBuilder setP(StatisBusiness.Scene p) {
            parm.p = p;
            return this;
        }

        public TrackAgent create() {
            TrackAgent agent = getInstance();
            //初始化数据
            initParam();
            //装配参数
            agent.setP(parm);
            return agent;
        }

        /**
         * 用于公共参数装配
         */
        private void initParam() {
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setD(DeviceUtils.getDeviceId(parm.context));
            commonInfo.setV(ChannelUtil.getVersionName(parm.context));
            commonInfo.setC(ChannelUtil.getChannel(parm.context));
            commonInfo.setO(DeviceUtils.getDeviceModel());
            commonInfo.setN(NetStateUtils.getNetWorkStatusInfo(parm.context).getmType());
            commonInfo.setPk(ChannelUtil.getPackageName(parm.context));
            parm.setCommonInfo(commonInfo);
            //动态随机8位
            parm.setR(RandomUtil.generateNumber2(8));
            Log.i(TAG, "统计上报公共参数装配成功:" + commonInfo.toString());
        }
    }


    /**
     * 配置参数，可扩展
     */
    public static class EventParam {
        @Transient
        private Context context; //上下文
        @Transient
        private String proxyUrl;//url
        private StatisBusiness.Scene p; //场景
        private StatisBusiness.Event k; //事件
        private StatisBusiness.Action m; //触发类型
        private String e; //触发值扩展字段
        private CommonInfo commonInfo; //公共参数
        private Long t; //随机时间戳，避免url缓存

        private String a;// appid

        private String g; // 签名

        private String r; // 随机8位数

        public void setT(Long t) {
            this.t = t;
        }

        public Long getT() {
            return t;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public void setCommonInfo(CommonInfo commonInfo) {
            this.commonInfo = commonInfo;
        }

        public CommonInfo getCommonInfo() {
            return commonInfo;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public void setG(String g) {
            this.g = g;
        }

        /**
         * 扩展参数需要进行URLEncode
         *
         * @param e
         */
        public void setE(String e) {
            try {
                this.e = e;
                if (TextUtils.isEmpty(e)) return;
                this.e = URLEncoder.encode(e, "UTF-8");
                Log.d(TAG, "e未编码值：" + e.toString());
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }

        /**
         * 利用反射+注解自身属性拼接上报参数
         *
         * @return
         */
        public String splitProperty() {
            TreeMap<String, Object> map = PropertyUtil.sensorDataList(this);
            String property = "";
            for (Map.Entry<String, Object> ss : map.entrySet()) {
                if (ss.getValue() != null) {
                    property += ss.getKey() + "=" + ss.getValue() + "&";
                    Log.d(TAG, ss.getKey() + "=" + ss.getValue() + "\n");
                }
            }
            if (!TextUtils.isEmpty(property)) {
                property = property.substring(0, property.length() - 1);
            }
            //释放无用内存
            if (map != null) {
                map.clear();
                map = null;
            }
            return property;
        }
    }
}
