package com.mampod.track.sdk.config;

import android.text.TextUtils;
import android.view.View;

import com.mampod.track.sdk.TrackAgent;
import com.mampod.track.sdk.config.TrackSDK;
import com.mampod.track.sdk.constants.StatisBusiness;
import com.mampod.track.sdk.model.AutoTrackModel;
import com.mampod.track.sdk.model.Flag;
import com.mampod.track.sdk.tool.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志统计工具类
 *
 * @package com.mampod.track.sdk.config
 * @author: Jack-Lu
 * @date:
 */
public class TrackLog {
    //访问序号
    private static AtomicInteger mOrderNum = new AtomicInteger();

    /**
     * 统计app启动
     */
    protected static void staticsInit() {
//        TrackAgent.getInstance().onEvent(StatisBusiness.Scene.sys, StatisBusiness.Event.init, null, null);
    }

    /**
     * 统计app使用时长
     */
    protected static void staticsAppDelay() {
//        try {
//            Long delayTime = System.currentTimeMillis() - (TrackSDK.getInstance().getP().initTime);
//            StringBuilder e = new StringBuilder();
//            e.append("v=").append(delayTime / 1000);
//            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.sys, StatisBusiness.Event.duration, StatisBusiness.Action.d, e.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * 统计UI形式事件
     *
     * @param event
     * @param extra
     */
    protected synchronized static void staticsUiEvent(AutoTrackModel event, String extra) {
        if (event == null) return;
        try {

            String screen = event.getTrack_screen_name();
            if (TextUtils.isEmpty(screen) || screen.contains("unknow")) {
                return;
            }
            String subScreen = event.getTrack_sub_screen_name();
            if (!TextUtils.isEmpty(subScreen) && subScreen.contains("unknow")) {
                return;
            }

            //6位随机数
            event.setRandom_id(TrackAgent.getInstance().getRandomId());
            //增加顺序号
            event.setOrder_num(mOrderNum.incrementAndGet());

            StringBuilder l2 = new StringBuilder();
            String mContent = event.getTrack_element_content();
            //限制按钮文本长度
            if (!TextUtils.isEmpty(mContent) && mContent.length() > 20) {
                mContent = mContent.substring(0, 20);
            }

            String content = event.getFlag() == Flag.p ? event.getM() :
                    (event.getFlag() == Flag.l ? (TextUtils.isEmpty(event.getTrack_element_id()) ? "" : event.getTrack_element_id()) :
                            (!TextUtils.isEmpty(event.getTrack_element_id()) ? event.getTrack_element_id() : "") +
                                    (TextUtils.isEmpty(mContent) ? "" : ((!TextUtils.isEmpty(event.getTrack_element_id()) ? "|" : "") + mContent)));


            l2.append(event.getRandom_id()).append("#")
                    .append(event.getOrder_num()).append("#")
                    .append(event.getFlag()).append("#")
                    .append(!TextUtils.isEmpty(event.getTrack_screen_name()) ? event.getTrack_screen_name() : "").append("#")
                    .append(!TextUtils.isEmpty(event.getTrack_sub_screen_name()) ? event.getTrack_sub_screen_name() : "").append("#")
                    .append(!TextUtils.isEmpty(content) ? content : "").append("#")
                    .append(event.getFlag() == Flag.l ? event.getTrack_element_position() : "");

//            if (event.getFlag() != null) {
//                l2.append(event.getFlag());
//            }
//
//            if (!TextUtils.isEmpty(event.getM())) {
//                l2.append("#").append(event.getM());
//            }
//
//            l2.append("#").append(screen);
//
//            if (!TextUtils.isEmpty(event.getTrack_element_id())) {
//                l2.append("#").append(event.getTrack_element_id());
//            }
//
////            if (!TextUtils.isEmpty(event.getTrack_element_type())) {
////                l2.append("#").append(event.getTrack_element_type());
////            }
//
//            if (!TextUtils.isEmpty(event.getTrack_element_content())) {
//                l2.append("#").append(event.getTrack_element_content());
//            }


            StringBuilder e = new StringBuilder();
            e.append("t=").append(l2);
//            if (!TextUtils.isEmpty(extra)) {
//                e.append("&l3=").append(extra);
//            }

            String rl = e.toString().toLowerCase();
            if (rl.length() > 500) {
                rl = rl.substring(0, 500);
            }


            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.ui, StatisBusiness.Event.at, null, rl);

//            Map properties = new HashMap();
//            properties.put(LogConstants.Autotrack.SCREEN_NAME, event.getTrack_screen_name());
//            properties.put(LogConstants.Autotrack.ELEMENT_ID, event.getTrack_element_id());
//            properties.put(LogConstants.Autotrack.ELEMENT_TYPE, event.getTrack_element_type());
//            properties.put(LogConstants.Autotrack.ELEMENT_CONTENT, event.getTrack_element_content());
//            properties.put(LogConstants.Autotrack.ELEMENT_POSITION, event.getTrack_element_position());
//
//            String md5 = MD5Util.getMd5Value(URLEncoder.encode(properties.toString(), "UTF-8"));


            //上报统计
//            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.ui, StatisBusiness.Event.v1, m, md5);

//            properties.put(LogConstants.Autotrack.ELEMENT_VIEWPATH, event.getTrack_element_viewpath());
//            properties.put(LogConstants.Autotrack.EVENT_SCAN_PAGE_TITLE, event.getTrack_title());
//
//            JSONObject origin = new JSONObject(properties);
//            JSONObject table = new JSONObject();
//            table.put(md5, origin);
//            //上报统计映射表
//            TrackAgent.getInstance().onVertifyData(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义通用计数
     *
     * @param event
     * @param extra
     */
    public static void staticsCommonEvent(String event, String extra) {
        if (TextUtils.isEmpty(event)) return;
        try {

            StringBuilder e = new StringBuilder();
            e.append("l1=").append("at")
                    .append("&l2=").append(event);
            if (!TextUtils.isEmpty(extra)) {
                e.append("&l3=").append(extra);
            }

            String rl = e.toString().toLowerCase();
            if (rl.length() > 300) {
                rl = rl.substring(0, 300);
            }
            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.point, StatisBusiness.Event.v1, StatisBusiness.Action.v, rl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于区分列表复用导致的无法区分点击事件问题，通过修改页面路径区分
     *
     * @param screen
     * @param view
     */
    public static void modifyScreenEvent(String screen, View view) {
        AutoTrackHelper.modifyClick(screen, view);
    }
}
