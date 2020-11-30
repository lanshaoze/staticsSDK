package com.mampod.track.sdk.listener;

import org.json.JSONObject;

/**
 * 界面方法收集接口
 *
 * @package com.mampod.ergedd.statistics.sdk.ui
 * @author: Jack-Lu
 * @date:
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
