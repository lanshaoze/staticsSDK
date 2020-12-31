package com.mampod.track.sdk.model;

/**
 * 统计上报数据模型
 *
 * @package com.mampod.track.sdk.model
 * @author: Jack-Lu
 * @date: 2020/11/18 下午2:43
 */
public class AutoTrackModel {
    private String random_id;  //6位随机数
    private int order_num;  //顺序数
    private Flag flag;  //消息类型
    private String track_screen_name; //大页面
    private String track_sub_screen_name; // 小界面
    private String track_element_content; //控件内容
    private String track_element_position; //索引（仅list）


    private String track_title;
    private String track_element_id;
    private String track_element_type;
    private String track_element_viewpath;
    private String m;  //标记类型{i:页面进入;o：页面退出;c：点击}


    public String getRandom_id() {
        return random_id;
    }

    public void setRandom_id(String random_id) {
        this.random_id = random_id;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public String getTrack_sub_screen_name() {
        return track_sub_screen_name;
    }

    public void setTrack_sub_screen_name(String track_sub_screen_name) {
        this.track_sub_screen_name = track_sub_screen_name;
    }

    public String getTrack_screen_name() {
        return track_screen_name;
    }

    public void setTrack_screen_name(String track_screen_name) {
        this.track_screen_name = track_screen_name;
    }

    public String getTrack_element_id() {
        return track_element_id;
    }

    public void setTrack_element_id(String track_element_id) {
        this.track_element_id = track_element_id;
    }

    public String getTrack_element_type() {
        return track_element_type;
    }

    public void setTrack_element_type(String track_element_type) {
        this.track_element_type = track_element_type;
    }

    public String getTrack_element_content() {
        return track_element_content;
    }

    public void setTrack_element_content(String track_element_content) {
        this.track_element_content = track_element_content;
    }

    public String getTrack_element_position() {
        return track_element_position;
    }

    public void setTrack_element_position(String track_element_position) {
        this.track_element_position = track_element_position;
    }

    public String getTrack_element_viewpath() {
        return track_element_viewpath;
    }

    public void setTrack_element_viewpath(String track_element_viewpath) {
        this.track_element_viewpath = track_element_viewpath;
    }

    public String getTrack_title() {
        return track_title;
    }

    public void setTrack_title(String track_title) {
        this.track_title = track_title;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}
