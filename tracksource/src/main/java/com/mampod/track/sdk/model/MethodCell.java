package com.mampod.track.sdk.model;

/**
 * @package： com.mampod.ergedd.statistics.sdk.model
 * @Des:
 * @author: Jack-Lu
 * @time: 2020/11/2 下午6:37
 * @change:
 * @changtime:
 * @changelog:
 */
public class MethodCell {
    private String pageName; //页面名称
    private String idName; //id名称
    private String route;  //ViewTree路径
    private int index; //索引
    private String type;  //1.btn; 2.list

    public MethodCell(String pageName, String idName, String route, int index, String type) {
        this.pageName = pageName;
        this.idName = idName;
        this.route = route;
        this.index = index;
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "pageName='" + pageName + '\'' +
                ", \nidName='" + idName + '\'' +
                ", \nroute='" + route + '\'' +
                ", \nindex=" + index +
                ", \ntype='" + type + '\'' +
                '}';
    }
}
