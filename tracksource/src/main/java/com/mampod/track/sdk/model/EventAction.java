package com.mampod.track.sdk.model;

/**
 * 事件类
 *
 * @package
 * @author:
 * @date:
 */
public class EventAction {
    private String eventId;
    private int eventType;
    private Object props;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Object getProps() {
        return props;
    }

    public void setProps(Object props) {
        this.props = props;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "EventAction{" +
                "eventId='" + eventId + '\'' +
                ", eventType=" + eventType +
                ", props=" + props +
                '}';
    }
}
