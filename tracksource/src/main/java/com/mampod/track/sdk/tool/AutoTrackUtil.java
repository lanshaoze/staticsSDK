package com.mampod.track.sdk.tool;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mampod.track.sdk.config.AutoTrackHelper;
import com.mampod.track.sdk.delegate.HookActivityDelegate;
import com.mampod.track.sdk.delegate.HookFragmentDelegate;
import com.mampod.tracksource.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @package： com.mampod.track.sdk.tool.AutoTrackUtil
 * @Des: 自动埋点工具类
 * @author: Jack-Lu
 * @time:
 * @change:
 * @changtime:
 * @changelog:
 */
public class AutoTrackUtil {

    private static final String UNKONW = "unknow";

    /**
     * 获取 Activity 的 title
     */
    public static String getActivityTitle(Activity activity) {
        try {
            if (activity != null) {
                try {
                    // 优先获取Toolbar的标题
                    String activityTitle = null;
                    if (activity instanceof AppCompatActivity) {
                        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                        if (actionBar != null) {
                            if (!TextUtils.isEmpty(actionBar.getTitle())) {
                                activityTitle = actionBar.getTitle().toString();
                                // System.out.println("自动埋点:标题栏标题:" + activityTitle);
                            }
                        }
                    }

                    // 次则获取清单配置标题
                    if (TextUtils.isEmpty(activityTitle)) {
                        activityTitle = activity.getTitle().toString();
                        // System.out.println("自动埋点:配置标题:" + activityTitle);
                    }

                    // 最后没辙了获取全类名
//                    if (TextUtils.isEmpty(activityTitle)) {
//                        activityTitle = activity.getClass().getCanonicalName();
//                    }

                    return activityTitle;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建当前view的全路径
     */
    public static String getViewPath(View view) {
        try {
            if (view == null) {
                return null;
            }

            ViewParent viewParent;
            List<String> viewPath = new ArrayList<>();
            do {
                viewParent = view.getParent();
                int index = getChildIndex(viewParent, view);
                viewPath.add(view.getClass().getCanonicalName() + "[" + index + "]");
                if (viewParent instanceof ViewGroup) {
                    view = (ViewGroup) viewParent;
                }

            } while (viewParent instanceof ViewGroup);

            Collections.reverse(viewPath);
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 1; i < viewPath.size(); i++) {
                stringBuffer.append(viewPath.get(i));
                if (i != (viewPath.size() - 1)) {
                    stringBuffer.append("/");
                }
            }

            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前View在父View中相同控件的索引位置
     *
     * @param parent 父View
     * @param child  子View
     */
    private static int getChildIndex(ViewParent parent, View child) {
        try {
            if (parent == null || !(parent instanceof ViewGroup)) {
                return -1;
            }

            ViewGroup _parent = (ViewGroup) parent;
            final String childIdName = getViewId(child);

            String childClassName = child.getClass().getCanonicalName();
            int index = 0;
            for (int i = 0; i < _parent.getChildCount(); i++) {
                View brother = _parent.getChildAt(i);

                // 是否是相同控件
//                if (!brother.getClass().getCanonicalName().equals(childClassName)) {
//                    continue;
//                }
                if (!hasClassName(brother, childClassName)) {
                    continue;
                }

                String brotherIdName = getViewId(brother);

                if (null != childIdName && !childIdName.equals(brotherIdName)) {
                    index++;
                    continue;
                }

                if (brother == child) {
                    return index;
                }

                index++;
            }

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static String getViewId(View view) {
        String idString = null;
        try {
            if (view.getId() != View.NO_ID) {
                idString = view.getContext().getResources().getResourceEntryName(view.getId());
            }
        } catch (Exception e) {
            //ignore
        }
        return idString;
    }

    /**
     * 遍历ViewGroup获取其所有子View的内容
     */
    public static String traverseView(StringBuilder stringBuilder, ViewGroup root) {
        try {
            if (root == null) {
                return stringBuilder.toString();
            }

            final int childCount = root.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                final View child = root.getChildAt(i);

                if (child.getVisibility() != View.VISIBLE) {
                    continue;
                }

                if (child instanceof ViewGroup) {

                    traverseView(stringBuilder, (ViewGroup) child);
                } else {

                    CharSequence viewText = traverseViewOnly(child);
                    if (!TextUtils.isEmpty(viewText)) {
                        stringBuilder.append(viewText.toString());
                        stringBuilder.append("-");
                    }
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return stringBuilder.toString();
        }
    }


    /**
     * 获取子View的内容
     */
    public static CharSequence traverseViewOnly(View child) {
        try {
            Class<?> switchCompatClass = null;
            try {
                switchCompatClass = Class.forName("android.support.v7.widget.SwitchCompat");
            } catch (Exception e) {
                //ignored
            }

            CharSequence viewText = null;
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                viewText = checkBox.getText();
            } else if (switchCompatClass != null && switchCompatClass.isInstance(child)) {
                CompoundButton switchCompat = (CompoundButton) child;
                Method method;
                if (switchCompat.isChecked()) {
                    method = child.getClass().getMethod("getTextOn");
                } else {
                    method = child.getClass().getMethod("getTextOff");
                }
                viewText = (String) method.invoke(child);
            } else if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                viewText = radioButton.getText();
            } else if (child instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) child;
                boolean isChecked = toggleButton.isChecked();
                if (isChecked) {
                    viewText = toggleButton.getTextOn();
                } else {
                    viewText = toggleButton.getTextOff();
                }
            } else if (child instanceof Button) {
                Button button = (Button) child;
                viewText = button.getText();
            } else if (child instanceof CheckedTextView) {
                CheckedTextView textView = (CheckedTextView) child;
                viewText = textView.getText();
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                viewText = textView.getText();
            } else if (child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                if (!TextUtils.isEmpty(imageView.getContentDescription())) {
                    viewText = imageView.getContentDescription().toString();
                }
            }

            if (!TextUtils.isEmpty(viewText)) {
                return viewText;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 用于在{@link AutoTrackHelper#onFragmentViewCreated(Object, View, Bundle)} 方法中给对应的view打上Fragment名称
     *
     * @param fragment Fragment
     * @param root     参数对应的view
     */
    public static void traverseViewForFragment(Object fragment, ViewGroup root) {
        try {
            try {
                if (fragment == null) {
                    return;
                }

                if (root == null) {
                    return;
                }

                final int childCount = root.getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    final View child = root.getChildAt(i);
                    if (child instanceof ListView ||
                            child instanceof GridView ||
                            child instanceof Spinner ||
                            child instanceof RadioGroup) {
                        child.setTag(R.id.auto_track_tag_view_fragment_name, fragment);
                    } else if (child instanceof ViewGroup) {
                        traverseViewForFragment(fragment, (ViewGroup) child);
                    } else {
                        child.setTag(R.id.auto_track_tag_view_fragment_name, fragment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void traverseViewForViewHolder(View itemview, int position) {
        try {
            try {
                if (itemview == null) {
                    return;
                }
                if (itemview instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) itemview;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        traverseViewForViewHolder(group.getChildAt(i), position);
                    }
                } else {
                    itemview.setTag(R.id.sub_view_tag, position);
                    itemview.setTag(R.id.auto_track_tag_type, "list");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前页面全类名,优先级activity+fragment->fragment->activity
     *
     * @param activity Activity
     * @param view     当前view
     */
    public static String getScreenNameFromView(Activity activity, View view) {
        try {
            if (view != null) {
                String screenName = UNKONW;
                Object fragment = view.getTag(R.id.auto_track_tag_view_fragment_name);
                if (fragment != null && fragment instanceof HookFragmentDelegate) {
                    screenName = AutoTrackUtil.getRoutersData(fragment);
                } else if (activity != null && activity instanceof HookActivityDelegate) {
                    screenName = AutoTrackUtil.getRoutersData(activity);
                } /*else {
                    if (activity != null) {
                        screenName = activity.getClass().getSimpleName();
                        if (TextUtils.isEmpty(screenName)) {
                            screenName = activity.getClass().getName();
                        }
                    }
                }*/
                if (TextUtils.isEmpty(screenName)) {
                    screenName = UNKONW;
                }
                return screenName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return UNKONW;
    }

    public static String getScreenNameForActivity(Activity activity) {
        try {
            String name = UNKONW;
            if (activity != null) {
                if (activity instanceof HookActivityDelegate) {
                    name = AutoTrackUtil.getRoutersData(activity);
                } /*else {
                    name = activity.getClass().getSimpleName();
                    if (TextUtils.isEmpty(name)) {
                        name = activity.getClass().getName();
                    }
                }*/
                if (TextUtils.isEmpty(name)) {
                    name = UNKONW;
                }
                return name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UNKONW;
    }

    public static boolean hasClassName(Object o, String className) {
        Class<?> klass = o.getClass();
        while (klass.getCanonicalName() != null) {
            if (klass.getCanonicalName().equals(className)) {
                return true;
            }

            if (klass == Object.class) {
                break;
            }

            klass = klass.getSuperclass();
        }
        return false;
    }


    /**
     * 从View中利用context获取所属Activity的名字
     */
    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            //context本身是Activity的实例
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            //Activity有可能被系统＂装饰＂，看看context.base是不是Activity
            context = ((ContextWrapper) context).getBaseContext();
            if (context instanceof Activity) {

                return (Activity) context;
            }
        }
        return null;
    }

    /**
     * 从View中利用context获取所属Activity的名字
     */
    public static Activity getActivityFromContext(Context context) {
        if (context instanceof Activity) {
            //context本身是Activity的实例
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            //Activity有可能被系统＂装饰＂，看看context.base是不是Activity
            context = ((ContextWrapper) context).getBaseContext();
            if (context instanceof Activity) {

                return (Activity) context;
            }
        }
        return null;
    }

    /**
     * 获得栈中最顶层的Activity名
     */
    private static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).getClassName();
        } else {
            return null;
        }
    }

    /**
     * 获取路径信息
     *
     * @param o
     * @return
     */
    public static String getRoutersData(Object o) {
        if (o == null) return "";
        String des = "";
        if (o instanceof HookFragmentDelegate) {
            ArrayList<String> fs = new ArrayList<>();
            if (TextUtils.isEmpty(((HookFragmentDelegate) o).getDes())) {
//                fs.add(getClassName(o));
                fs.add(UNKONW);
            } else {
                fs.add(((HookFragmentDelegate) o).getDes());
            }
            StringBuilder e = new StringBuilder();
            while (((HookFragmentDelegate) o).getParentFragment() != null) {
                o = ((HookFragmentDelegate) o).getParentFragment();
                if (!TextUtils.isEmpty(((HookFragmentDelegate) o).getDes())) {
                    fs.add(((HookFragmentDelegate) o).getDes());
                } else {
                    fs.add(UNKONW);
//                    fs.add(getClassName(o));
                }
            }

            if (((HookFragmentDelegate) o).getActivity() != null && ((HookFragmentDelegate) o).getActivity() instanceof HookActivityDelegate) {
                HookActivityDelegate avActivity = (HookActivityDelegate) ((HookFragmentDelegate) o).getActivity();
                if (!TextUtils.isEmpty(avActivity.getDes())) {
                    fs.add(avActivity.getDes());
                } else {
                    fs.add(UNKONW);
//                    fs.add(getClassName(avActivity));
                }
            }

            for (int i = fs.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    e.append(fs.get(i));
                } else {
                    e.append(fs.get(i)).append("|");
                }
            }
            des = e.toString();
        } else if (o instanceof HookActivityDelegate) {
            HookActivityDelegate avActivity = (HookActivityDelegate) o;
            des = avActivity.getDes();
        }

        if (TextUtils.isEmpty(des)) {
//            des = getClassName(o);
            des = UNKONW;
        }

        return des;
    }

    public static String getClassName(Object o) {
        String data = o.getClass().getSimpleName();
        //匿名内部类getSimpleName返回的为空  那么直接去getName()
        if (TextUtils.isEmpty(data)) {
            data = o.getClass().getName();
        }
        return data;
    }


    /**
     * 合并 JSONObject
     *
     * @param source JSONObject
     * @param dest   JSONObject
     * @throws JSONException Exception
     */
    public static void mergeJSONObject(final JSONObject source, JSONObject dest)
            throws JSONException {
        try {
            Iterator<String> superPropertiesIterator = source.keys();
            while (superPropertiesIterator.hasNext()) {
                String key = superPropertiesIterator.next();
                Object value = source.get(key);
                if (value instanceof Date) {
                    synchronized (mDateFormat) {
                        dest.put(key, mDateFormat.format((Date) value));
                    }
                } else {
                    dest.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"
            + ".SSS", Locale.getDefault());

}
