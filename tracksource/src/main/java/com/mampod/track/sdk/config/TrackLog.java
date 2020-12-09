package com.mampod.track.sdk.config;

import android.text.TextUtils;
import android.view.View;

import com.mampod.track.sdk.TrackAgent;
import com.mampod.track.sdk.config.TrackSDK;
import com.mampod.track.sdk.constants.StatisBusiness;
import com.mampod.track.sdk.model.AutoTrackModel;

/**
 * 日志统计工具类
 *
 * @package com.mampod.track.sdk.config
 * @author: Jack-Lu
 * @date:
 */
public class TrackLog {

    /**
     * 统计app启动
     */
    protected static void staticsInit() {
        TrackAgent.getInstance().onEvent(StatisBusiness.Scene.sys, StatisBusiness.Event.init, null, null);
    }

    /**
     * 统计app使用时长
     */
    protected static void staticsAppDelay() {
        try {
            Long delayTime = System.currentTimeMillis() - (TrackSDK.getInstance().getP().initTime);
            StringBuilder e = new StringBuilder();
            e.append("v=").append(delayTime / 1000);
            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.sys, StatisBusiness.Event.duration, StatisBusiness.Action.d, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 统计UI形式事件
     *
     * @param event
     * @param extra
     */
    protected static void staticsUiEvent(AutoTrackModel event, String extra) {
        if (event == null) return;
        try {

            String screen = event.getTrack_screen_name();
            if (TextUtils.isEmpty(screen) || screen.contains("unknow")) {
                return;
            }

            StringBuilder l2 = new StringBuilder();
            if (event.getFlag() != null) {
                l2.append(event.getFlag());
            }

            if (!TextUtils.isEmpty(event.getM())) {
                l2.append("#").append(event.getM());
            }

            l2.append("#").append(screen);

            if (!TextUtils.isEmpty(event.getTrack_element_id())) {
                l2.append("#").append(event.getTrack_element_id());
            }

//            if (!TextUtils.isEmpty(event.getTrack_element_type())) {
//                l2.append("#").append(event.getTrack_element_type());
//            }

            if (!TextUtils.isEmpty(event.getTrack_element_content())) {
                l2.append("#").append(event.getTrack_element_content());
            }


            StringBuilder e = new StringBuilder();
            e.append("l1=").append("at")
                    .append("&l2=").append(l2);
            if (!TextUtils.isEmpty(extra)) {
                e.append("&l3=").append(extra);
            }

            String rl = e.toString().toLowerCase();
            if (rl.length() > 300) {
                rl = rl.substring(0, 300);
            }


            TrackAgent.getInstance().onEvent(StatisBusiness.Scene.ui, StatisBusiness.Event.v1, StatisBusiness.Action.v, rl);

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
