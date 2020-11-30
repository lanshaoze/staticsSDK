package com.mampod.track.sdk.model;

import android.text.TextUtils;


import com.mampod.track.sdk.tool.MD5Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 公共参数类
 *
 * @package com.mampod.track.sdk.model
 * @author: Jack-Lu
 * @date:
 */
public class CommonInfo {
    private String d; //用户UUID
    private String v; //客户端版本
    private String c; //渠道
    private String n; //网络类型
    private String o; //设备型号
    private String pk; //包名MD5后四位

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        try {
            this.o = URLEncoder.encode(o, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String p) {
        try {
            if (TextUtils.isEmpty(p)) return;
            String md5 = MD5Util.getMd5Value(p);
            if (md5.length() >= 4) {
                this.pk = md5.substring(md5.length() - 4);
            } else {
                this.pk = md5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "CommonInfo{" +
                "d='" + d + '\'' +
                ", v='" + v + '\'' +
                ", c='" + c + '\'' +
                ", n='" + n + '\'' +
                ", o='" + o + '\'' +
                ", pk='" + pk + '\'' +
                '}';
    }
}
