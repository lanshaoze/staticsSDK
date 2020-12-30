package com.mampod.track.sdk.tool;

import android.util.Log;

import com.mampod.track.sdk.model.EventAction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 事件路径管理类
 *
 * @package
 * @author:
 * @date:
 */
public class EventManager {


    /**
     * 最大事件存储数量
     */
    private static int maxCount = 5;

    private static BlockingQueue<EventAction> events;

    private static class SingletonContainer {
        private static EventManager instance = new EventManager();
    }

    public static EventManager getInstance() {
        return SingletonContainer.instance;
    }

    public EventManager() {
        events = new LinkedBlockingQueue<>(maxCount);
    }


    /**
     * 增加点击事件
     *
     * @param action
     */
    public void offerEvent(EventAction action) {
        if (events.size() == maxCount) {
            deleteFirstEvent();
        }
        events.offer(action);
        Log.d("Event:", "add");
    }


    /**
     * 移除首个曝光事件
     */
    private void deleteFirstEvent() {
        events.poll();
        Log.d("Event:", "delete");
    }


    /**
     * 获取界面曝光事件队列
     *
     * @return
     */
    public EventAction[] getEvents() {
        Log.d("Event:", "getEvents->" + events.toArray().length);
        EventAction[] actions = new EventAction[events.size()];
        EventAction[] result = events.toArray(actions);
        for (int i = 0; i < result.length; i++) {
            Log.d("Event:", result[i].toString());
        }
        return result;
    }
}
