package com.mampod.track.sdk.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by dk on 16/11/15.
 */

public class ChannelUtil {
    public static final String GOOGLE_PLAY = "googleplay";

    public static final String API_KEY_ERGEDD = "ergedd_android";
    public static final String API_SECRET_ERGEDD = "a05e8c34718b3f272e2164a9c1d963f1";

    public static final String API_KEY_BBVIDEO = "bberge_android";
    public static final String API_SECRET_BBVIDEO = "0416406022e02aab6ba769189a4c35e1";

    public static final String API_KEY_ENGLISH = "dd_eng_android";
    public static final String API_SECRET_ENGLISH = "227b1a754a717c945b7208328b2fcc0c";

    public static final String UMENG_APP_KEY_ERGEDD = "5593c9c067e58e0a6e001a28";
    public static final String UMENG_APP_KEY_BBVIDEO = "57b6b98467e58eb9930019d5";
    public static final String UMENG_APP_KEY_GOOGLEPLAY = "587ba9c25312dd75d9001a8a";
    public static final String UMENG_APP_KEY_ENGLISH = "58ca3a81310c931d1b0015d9";
    public static final String UMENG_APP_KEY_MAMPOD_JISU = "5c7e0c7761f564e717000f99";

    public static final String TALKINGDATA_APP_KEY_ERGEDD = "3D30180881BE4641836A3228D78EB6FF";
    public static final String TALKINGDATA_APP_KEY_BBVIDEO = "8BFACBCD614443D79BAC0028B1323FEB";
    public static final String TALKINGDATA_APP_KEY_MAMPOD_JISU = "1CC896FAECC34D26BCF77E1037E28F12";
    // 萌宝指定广告位
    public static final String TALKINGDATA_APP_KEY_BBVIDEO_TEST = "BD12DB718CE74567921B8238E803F246";

    public static final String CHANNEL_KEY = "cztchannel";
    private static final String CHANNEL_VERSION_KEY = "cztchannel_version";
    private static String mChannel;

    private static final String XIAOMI_APP_ID_ERGEDD = "2882303761517362910";
    private static final String XIAOMI_BANNER_ID_ERGEDD = "02ce0c3859ab8fb6d50eb61cd32dc848";

    private static final String XIAOMI_APP_ID_BBVIDEO = "2882303761517305070";
    private static final String XIAOMI_BANNER_ID_BBVIDEO = "1f50ae8bc9843de59e1f7bda47ca477f";

    private static final String GDT_APP_ID_ERGEDD = "1105981748";
    private static final String GDT_BANNER_ID_ERGEDD = "8080210983901426";

    private static final String GDT_APP_ID_BBVIDEO = "1106024020";
    private static final String GDT_BANNER_ID_BBVIDEO = "6050626076713529";

    /**
     * 广点通原生广告
     */
    private static final String GDT_NATIVE_BANNER_ID_BBVIDEO = "5040777375381547";
    private static final String GDT_NATIVE_BANNER_ID_ERGEDD = "7050485245327467";

    /**
     * 穿山甲原生广告
     */

    private static final String CSJ_NATIVE_BANNER_ID_BBVIDEO = "911418747";
    private static final String CSJ_NATIVE_BANNER_ID_ERGEDD = "912714669";


    public static HashMap<String, Integer> UNION_DEBUT = new HashMap<>();
    public static HashMap<String, Integer> UNION_DEBUT_BBVIDEO = new HashMap<>();


    //新老用户标识
    private static final String USER_TAG = "user_tag";

    static {
//        UNION_DEBUT.put("qihu", R.drawable.debut360);
//        UNION_DEBUT.put("qihu", R.drawable.debut_360_union);
//        UNION_DEBUT.put("huawei", R.drawable.debut_huawei);
//        UNION_DEBUT.put("lenovo", R.drawable.debut_lenovo);
//        UNION_DEBUT.put("taobao", R.drawable.debut_ali);
//        UNION_DEBUT.put("qq", R.drawable.debut_qq);

//        UNION_DEBUT_BBVIDEO.put("qihu", R.drawable.debut360);
//        UNION_DEBUT_BBVIDEO.put("qihu", R.drawable.debut_360_union);
//        UNION_DEBUT_BBVIDEO.put("huawei", R.drawable.debut_huawei);
//        UNION_DEBUT_BBVIDEO.put("taobao", R.drawable.debut_ali);
    }

    /**
     * 返回市场。  如果获取失败返回""
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        //加固后会丢失渠道信息,由于只有360渠道需要加固,所以默认渠道为qihu
        return getChannel(context, "qihu");
    }

    /**
     * 返回市场。  如果获取失败返回defaultChannel
     *
     * @param context
     * @param defaultChannel
     * @return
     */
    public static String getChannel(Context context, String defaultChannel) {
        //内存中获取
        if (!TextUtils.isEmpty(mChannel)) {
            return mChannel;
        }
        //sp中获取
        mChannel = getChannelBySharedPreferences(context);
        if (!TextUtils.isEmpty(mChannel)) {
            return mChannel;
        }
        //从apk中获取
        mChannel = getChannelFromApk(context, CHANNEL_KEY);
        if (!TextUtils.isEmpty(mChannel)) {
            //保存sp中备用
            saveChannelBySharedPreferences(context, mChannel);
            return mChannel;
        } else {
            mChannel = defaultChannel;
        }
        //全部获取失败
        return defaultChannel;
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @param channelKey
     * @return
     */
    public static String getChannelFromApk(Context context, String channelKey) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }

    /**
     * 本地保存channel & 对应版本号
     *
     * @param context
     * @param channel
     */
    private static void saveChannelBySharedPreferences(Context context, String channel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY, channel);
        editor.putInt(CHANNEL_VERSION_KEY, getVersionCode(context));
        editor.apply();
    }

    /**
     * 从sp中获取channel
     *
     * @param context
     * @return 为空表示获取异常、sp中的值已经失效、sp中没有此值
     */
    private static String getChannelBySharedPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersionCode = getVersionCode(context);
        if (currentVersionCode == -1) {
            //获取错误
            saveUserTag(context, false);
            return "";
        }
        int versionCodeSaved = sp.getInt(CHANNEL_VERSION_KEY, -1);
        if (versionCodeSaved == -1) {
            //本地没有存储的channel对应的版本号
            //第一次使用  或者 原先存储版本号异常
            saveUserTag(context, false);
            return "";
        }
        if (currentVersionCode != versionCodeSaved) {
            saveUserTag(context, true);
            return "";
        }
        return sp.getString(CHANNEL_KEY, "");
    }

    /**
     * 从包信息中获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 从包信息中获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPackageName(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return packageInfo.packageName;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }



    /**
     * 本地保存channel & 对应版本号 A B测试
     *
     * @param context
     * @param channel
     */
    public static void saveChannelBySPForAB(Context context, String channel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY, channel);
        editor.apply();
    }


    /**
     * 从sp中获取channel A B测试
     *
     * @param context
     * @return 为空表示获取异常、sp中的值已经失效、sp中没有此值
     */
    public static String getChannelBySPForAB(Context context) {
        String bRet = "";
        try {
            if (null == context) {
                return "";
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (null == sp) {
                return "";
            }

            bRet = sp.getString(CHANNEL_KEY, "");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bRet;
    }


    /**
     * 保存新老用户标识
     *
     * @param context
     * @param isOldUser
     */
    private static void saveUserTag(Context context, boolean isOldUser) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(USER_TAG, isOldUser);
        editor.apply();
    }

    /**
     * 获取新老用户身份
     *
     * @param context
     * @return
     */
    public static boolean isOldUser(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(USER_TAG, true);
    }
}
