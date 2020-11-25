package com.mampod.track.sdk.tool;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class DeviceUtils {


    // 需要权限方式获取UUID
    final private static String CACHE_KEY_DEVICE_ID = "DeviceUtils.DeviceId";
    final private static String CACHE_NAME = "DeviceUtils";
    private static String DEVICE_ID;

    private static String ip_address = ""; // ip地址
    private static String oaid = ""; // oaid  安卓10及以上使用 移动应用联盟提供设备唯一id

    // 不需要权限方式获取UUID
    final private static String NEW_CACHE_KEY_DEVICE_ID = "New.DeviceUtils.DeviceId";
    private static String NEW_DEVICE_ID;

    /**
     * >=2.2
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * >=2.3
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * >=3.0 LEVEL:11
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * >=3.1
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * >=4.0 14
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * >= 4.1 16
     *
     * @return
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * >= 4.2 17
     */
    public static boolean hasJellyBeanMr1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * >= 4.3 18
     */
    public static boolean hasJellyBeanMr2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * >=4.4 19
     */
    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static int getSDKVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    @SuppressWarnings("deprecation")
    public static String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 获得设备的固件版本号
     */
    public static String getReleaseVersion() {
        return StringUtils.makeSafe(Build.VERSION.RELEASE);
    }

    /**
     * 检测是否是中兴机器
     */
    public static boolean isZte() {
        return getDeviceModel().toLowerCase().contains("zte");
    }

    /**
     * 判断是否是三星的手机
     */
    public static boolean isSamsung() {
        return getManufacturer().toLowerCase().contains("samsung");
    }

    /**
     * 检测是否HTC手机
     */
    public static boolean isHTC() {
        return getManufacturer().toLowerCase().contains("htc");
    }

    /**
     * 检测当前设备是否是特定的设备
     *
     * @param devices
     * @return
     */
    public static boolean isDevice(String... devices) {
        String model = DeviceUtils.getDeviceModel();
        if (devices != null && model != null) {
            for (String device : devices) {
                if (model.contains(device)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获得设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return StringUtils.trim(Build.MODEL);
    }

    /**
     * 获取厂商信息
     */
    public static String getManufacturer() {
        return StringUtils.trim(Build.MANUFACTURER);
    }

    /**
     * 判断是否是平板电脑
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 检测是否是平板电脑
     *
     * @param context
     * @return
     */
    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }

    public static int dipToPX(final Context ctx, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }

    /**
     * 获取CPU的信息
     *
     * @return
     */
    public static String getCpuInfo() {
        String cpuInfo = "";
        try {
            if (new File("/proc/cpuinfo").exists()) {
                FileReader fr = new FileReader("/proc/cpuinfo");
                BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
                cpuInfo = localBufferedReader.readLine();
                localBufferedReader.close();

                if (cpuInfo != null) {
                    cpuInfo = cpuInfo.split(":")[1].trim().split(" ")[0];
                }
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return cpuInfo;
    }

    /**
     * 判断是否支持闪光灯
     */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) //判断设备是否支持闪光灯
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测设备是否支持相机
     */
    public static boolean isSupportCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * 获取屏幕宽度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        String uuid = "";
        if (TextUtils.isEmpty(DEVICE_ID)) {
            DEVICE_ID = EncryptedPreferencesUtil.getInstance(context).getString(CACHE_KEY_DEVICE_ID, "");
        }

        if (!TextUtils.isEmpty(DEVICE_ID)) {
            uuid = DEVICE_ID;
        } else {
            uuid = getNewDeviceId(context);
        }
        return uuid;
    }


    // 此UUID不需要权限
    public static String getNewDeviceId(Context context) {
        if (TextUtils.isEmpty(NEW_DEVICE_ID)) {
            NEW_DEVICE_ID = EncryptedPreferencesUtil.getInstance(context).getString(NEW_CACHE_KEY_DEVICE_ID, "");
        }
        if (TextUtils.isEmpty(NEW_DEVICE_ID)) {
            String serial = "serial";
            String hardwareInfo = null;
            try {
                hardwareInfo = Build.ID + Build.DISPLAY + Build.PRODUCT
                        + Build.DEVICE + Build.BOARD /*+ Build.CPU_ABI*/
                        + Build.MANUFACTURER + Build.BRAND + Build.MODEL
                        + Build.BOOTLOADER + Build.HARDWARE /* + Build.SERIAL */
                        + Build.TYPE + Build.TAGS + Build.FINGERPRINT + Build.HOST
                        + Build.USER;
//                String tmSerial = android.os.Build.class.getField("SERIAL").get(null).toString();
                String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
                final UUID deviceUuid = new UUID(androidId.hashCode(), hardwareInfo.hashCode());
                NEW_DEVICE_ID = deviceUuid.toString();
                if (!TextUtils.isEmpty(NEW_DEVICE_ID)) {
                    EncryptedPreferencesUtil.getInstance(context).edit().putString(NEW_CACHE_KEY_DEVICE_ID, NEW_DEVICE_ID).apply();
                }
                return NEW_DEVICE_ID;
            } catch (Exception e) {
                e.printStackTrace();
            }

            NEW_DEVICE_ID = new UUID(TextUtils.isEmpty(hardwareInfo) ? "hardware".hashCode() : hardwareInfo.hashCode(), serial.hashCode()).toString();
            if (!TextUtils.isEmpty(NEW_DEVICE_ID)) {
                EncryptedPreferencesUtil.getInstance(context).edit().putString(NEW_CACHE_KEY_DEVICE_ID, NEW_DEVICE_ID).apply();
            }
        }

        return NEW_DEVICE_ID;
    }


    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        // 使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        // 最终会得到这样的一串ID：00000000-28ee-3eab-ffff-ffffe9374e72
    }

//    public static boolean couldGetDeviceId(final Context context) {
//        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (TextUtils.isEmpty(tm.getDeviceId())) {
//            return false;
//        }
//        if (TextUtils.isEmpty(tm.getSimSerialNumber())) {
//            return false;
//        }
//        if (TextUtils.isEmpty(android.provider.Settings.Secure.getString(context.getContentResolver(),
//                android.provider.Settings.Secure.ANDROID_ID))) {
//            return false;
//        }
//        return true;
//    }

    public static String getModel() {
        String model = Build.MODEL;
        return TextUtils.isEmpty(model) ? "N/A" : model;
    }

    public static String getBrand() {
        String brand = Build.BRAND;
        return TextUtils.isEmpty(brand) ? "N/A" : brand;
    }

    public static String getProduct() {
        String product = Build.PRODUCT;
        return TextUtils.isEmpty(product) ? "N/A" : product;
    }

    public static String getOSVersion() {
        String version = Build.VERSION.RELEASE;
        return TextUtils.isEmpty(version) ? "N/A" : version;
    }

    public static String getCPUArch() {
        String cpuArch = System.getProperty("os.arch");
        return TextUtils.isEmpty(cpuArch) ? "N/A" : cpuArch;
    }

    public static boolean isSupportTranslucentStatusBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT || isMiui();
    }

    public static boolean isMiui() {
        try {
            Class.forName("android.view.MiuiWindowManager$LayoutParams");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 全屏时隐藏虚拟按键
     *
     * @param context
     */
    public static void hideSystemNavigationBar(Activity context) {
        try {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View view = context.getWindow().getDecorView();
                view.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = context.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查包名是否存在
     *
     * @param packageName
     * @return
     */
    public static boolean checkPackage(Context context,String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Desc: 获取虚拟按键高度 放到工具类里面直接调用即可
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

//    /**
//     * 获取当前设备IP地址
//     *
//     * @param context
//     * @return
//     */
//    public static String getIpAddress(Context context) {
//
//        String ip = "";
//        try {
//            ConnectivityManager conMann = (ConnectivityManager)
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//            if (null != mobileNetworkInfo && mobileNetworkInfo.isConnected()) {
//                ip = getLocalIpAddress();
//            } else if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
//                ip = getLocalIpAddress(context);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ip;
//    }



    /**
     * 获取当前ip地址  wifi 网络局域地址 私网
     *
     * @param context
     * @return
     */
    private static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static String[] platforms = {
            "http://pv.sohu.com/cityjson",
            "http://pv.sohu.com/cityjson?ie=utf-8"
    };



    public interface IPCallback {
        void onSuccess(String ip);

        void onFailed();
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int getSimOperator(Context context) {
        int opeType = 0; // 0：未知 1：中国移动 2：中国联通 3： 中国电信 4：其它
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String opeNum = teleManager.getSimOperator();
        if ("46001".equals(opeNum) || "46006".equals(opeNum) || "46009".equals(opeNum)) {
            // opeType = "中国联通";
            opeType = 2;
        } else if ("46000".equals(opeNum) || "46002".equals(opeNum) || "46004".equals(opeNum) || "46007".equals(opeNum)) {
            // opeType = "中国移动";
            opeType = 1;
        } else if ("46003".equals(opeNum) || "46005".equals(opeNum) || "46011".equals(opeNum)) {
            // opeType = "中国电信";
            opeType = 3;
        } else {
            opeType = 4;
        }
        return opeType;
    }

    // AndroidId
    public static String getAndroidId(Context context) {
        try {
            String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String getIp_address() {
        return ip_address;
    }

    public static void setIp_address(String ip_address) {
        DeviceUtils.ip_address = ip_address;
    }

    public static String getOaid() {
        return oaid;
    }

    public static void setOaid(String oaid) {
        DeviceUtils.oaid = oaid;
    }

    public interface IpResult {
        void onSuccess(String ip);
    }

    public static int getOrientation(Context context) {
        int orientation = 0; // 0:未知;1 竖屏;2、横屏
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            orientation = 2;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            orientation = 1;
        }

        return orientation;
    }
}
