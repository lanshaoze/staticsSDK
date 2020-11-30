package com.mampod.track.sdk.model;

/**
 * 自定义log数据模型
 *
 * @package com.mampod.track.sdk.model
 * @author: Jack-Lu
 * @date:
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
