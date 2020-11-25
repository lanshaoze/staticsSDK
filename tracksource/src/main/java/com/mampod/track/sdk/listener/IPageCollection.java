package com.mampod.track.sdk.listener;

import org.json.JSONObject;

/**
 * @package： com.mampod.ergedd.statistics.sdk.ui
 * @Des: 界面方法收集接口
 * @author: Jack-Lu
 * @time: 2020/10/26 下午2:17
 * @change:
 * @changtime:
 * @changelog:
 */
public interface IPageCollection {
    /**
     * 界面描述
     *
     * @return
     */
    String getDes();

    /**
     * 界面额外参数
     *
     * @return
     */
    JSONObject getProps();
}
