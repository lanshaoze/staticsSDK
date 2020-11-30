package com.mampod.track.sdk.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 网络类型判断
 *
 * @package com.mampod.track.sdk.tool
 * @author: Jack-Lu
 * @date:
 */
public class NetStateUtils {

    public enum NetState {
        NETWORK_CLASS_UNKNOWN(0, "noNetwork"),
        NETWORK_WIFI(1, "wifi"),
        NETWORK_CLASS_2_G(2, "2G"),
        NETWORK_CLASS_3_G(3, "3G"),
        NETWORK_CLASS_4_G(4, "4G"),
        NETWORK_MOBILE(5, "mobile");
        private String mType;

        private int mCode;

        public int getmCode() {
            return mCode;
        }

        public String getmType() {
            return mType;
        }

        NetState(int code, String type) {
            this.mCode = code;
            this.mType = type;
        }
    }

    /**
     * 获取手机网络类型（2G/3G/4G）：
     * 4G为LTE，联通的3G为UMTS或HSDPA，电信的3G为EVDO，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA。
     *
     * @param context
     * @return
     */
    public static NetState getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetState.NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetState.NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetState.NETWORK_CLASS_4_G;

            default:
                return NetState.NETWORK_MOBILE;
        }
    }

    /**
     * 获取手机连接的网络类型（是WIFI还是手机网络[2G/3G/4G]）
     *
     * @param context
     * @return
     */
    public static NetState getNetWorkStatusInfo(Context context) {
        NetState netWorkType = NetState.NETWORK_MOBILE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = NetState.NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
//                netWorkType = getNetWorkClass(context);

                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        netWorkType = NetState.NETWORK_CLASS_2_G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        netWorkType = NetState.NETWORK_CLASS_3_G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        netWorkType = NetState.NETWORK_CLASS_4_G;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (!TextUtils.isEmpty(_strSubTypeName) && (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))) {
                            netWorkType = NetState.NETWORK_CLASS_3_G;
                        } else {
                            netWorkType = NetState.NETWORK_MOBILE;
                        }

                        break;
                }
            }
        }

        return netWorkType;
    }


    /**
     * 获取手机连接的网络类型（是WIFI还是手机网络）
     *
     * @param context
     * @return
     */
    public static NetState getNetWorkStatus(Context context) {
        NetState netWorkType = NetState.NETWORK_CLASS_UNKNOWN;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = NetState.NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = NetState.NETWORK_MOBILE;
            }
        }

        return netWorkType;
    }
}
