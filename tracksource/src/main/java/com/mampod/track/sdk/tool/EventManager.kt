package com.mampod.track.sdk.tool

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * Description.
 * @package com.mampod.track.sdk.tool
 * @author: jack
 * @date: 2021/4/8 下午4:40
 */
object EventManager {
    val maxCount = 3
    var actionEvents: BlockingQueue<String> = LinkedBlockingQueue<String>(maxCount)

    fun offerActionEVent(action: String?) {
        if (actionEvents.size == maxCount) {
            removeFirstActionEvent()
        }
        actionEvents.offer(action)
    }

    fun removeFirstActionEvent() {
        actionEvents.poll()
    }


    fun getActions(): Array<String> {
        return actionEvents.toTypedArray()
    }
}