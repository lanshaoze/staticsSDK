package com.mampod.track.sdk.tool;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.mampod.track.sdk.delegate.HookActivityDelegate;
import com.mampod.track.sdk.delegate.HookFragmentDelegate;
import com.mampod.track.sdk.model.MethodCell;
import com.mampod.tracksource.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * view唯一ID生成器
 *
 * @package com.mampod.track.sdk.tool
 * @author: Jack-Lu
 * @date:
 */
public class ViewPathUtil {

    /**
     * 获取view的页面唯一值
     *
     * @return
     */
    public static String getViewPath(Activity activity, View view) {
        String idName = "";
        if (view.getId() != View.NO_ID) {
            idName = view.getResources().getResourceEntryName(view.getId());
        }
        String pageName = getRoutersData(activity);
        String route = getViewId(view);
        Object subPage = view.getTag(R.id.fragment_tag);
        if (subPage != null && subPage instanceof HookFragmentDelegate) {
            pageName = AutoTrackUtil.getRoutersData(subPage);
        }

        int position = -1;
        if (view.getTag(R.id.sub_view_tag) != null) {
            position = (int) view.getTag(R.id.sub_view_tag);
        }

        String type = "btn";
        if (view.getTag(R.id.sub_view_type) != null) {
            type = (String) view.getTag(R.id.sub_view_type);
        }
        MethodCell methodCell = new MethodCell(pageName, idName, route, position, type);
        Log.d("SDK--->", methodCell.toString());
        //pageName + "_" + MD5Util.md5(route) + ((!TextUtils.isEmpty(idName) ? "_[" + idName + "]" : "") + (position != -1 ? "_[" + position + "]" : ""))
        return methodCell.toString();
    }

    /**
     * 获取页面名称
     *
     * @param view
     * @return
     */
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return ((Activity) context);
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 获取view唯一id,根据xml文件内容计算
     *
     * @param view1
     * @return
     */
    private static String getViewId(View view1) {

        StringBuilder sb = new StringBuilder();

        //当前需要计算位置的view
        View view = view1;
        ViewParent viewParent = view.getParent();

        while (viewParent != null && viewParent instanceof ViewGroup) {
            ViewGroup tview = (ViewGroup) viewParent;
            if (((View) view.getParent()).getId() == android.R.id.content) {
//                sb.insert(0, view.getClass().getSimpleName());
                break;
            } else {
                int index = getChildIndex(tview, view);
                sb.insert(0, view.getClass().getSimpleName() + "[" + (index == -1 ? "_" : index) + "]");
            }
            viewParent = tview.getParent();
            view = tview;
        }
        return sb.toString();
    }

    /**
     * 计算当前 view在父容器中相对于同类型view的位置
     */
    private static int getChildIndex(ViewGroup viewGroup, View view) {
        if (viewGroup == null || view == null) {
            return -1;
        }
        String viewName = view.getClass().getSimpleName();
        int index = 0;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View el = viewGroup.getChildAt(i);
            String elName = el.getClass().getSimpleName();
            if (elName.equals(viewName)) {
                //表示同类型的view
                if (el == view) {
                    return index;
                } else {
                    index++;
                }
            }
        }
        return -1;
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
                fs.add(getClassName(o));
            } else {
                fs.add(((HookFragmentDelegate) o).getDes());
            }
            StringBuilder e = new StringBuilder();
            while (((HookFragmentDelegate) o).getParentFragment() != null) {
                o = ((HookFragmentDelegate) o).getParentFragment();
                if (!TextUtils.isEmpty(((HookFragmentDelegate) o).getDes())) {
                    fs.add(((HookFragmentDelegate) o).getDes());
                } else {
                    fs.add(getClassName(o));
                }
            }

            if (((HookFragmentDelegate) o).getActivity() != null && ((HookFragmentDelegate) o).getActivity() instanceof HookActivityDelegate) {
                HookActivityDelegate avActivity = (HookActivityDelegate) ((HookFragmentDelegate) o).getActivity();
                if (!TextUtils.isEmpty(avActivity.getDes())) {
                    fs.add(avActivity.getDes());
                } else {
                    fs.add(getClassName(avActivity));
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
            des = getClassName(o);
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
     * 获取当前对应页面的扩展参数
     */
    public static JSONObject getNowProps(Object o) {
        JSONObject props = null;
        if (o instanceof HookFragmentDelegate) {
            HookFragmentDelegate baseFragment = (HookFragmentDelegate) o;
            props = baseFragment.getProps();
        } else if (o instanceof HookActivityDelegate) {
            HookActivityDelegate avActivity = (HookActivityDelegate) o;
            props = avActivity.getProps();
        }
        return props;
    }
}
