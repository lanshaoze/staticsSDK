package com.mampod.track.sdk.config;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mampod.track.sdk.constants.LogConstants;
import com.mampod.track.sdk.constants.StatisBusiness;
import com.mampod.track.sdk.delegate.HookActivityDelegate;
import com.mampod.track.sdk.delegate.HookFragmentDelegate;
import com.mampod.track.sdk.model.AutoTrackModel;
import com.mampod.track.sdk.model.Flag;
import com.mampod.track.sdk.tool.AppUtils;
import com.mampod.track.sdk.tool.AutoTrackUtil;
import com.mampod.track.sdk.tool.PropertyUtil;
import com.mampod.tracksource.R;

import org.json.JSONObject;

import java.util.Locale;

/**
 * 全埋点的入口类
 *
 * @package com.mampod.track.sdk.config
 * @author: Jack-Lu
 * @date:
 */
public class AutoTrackHelper {

    private static SparseArray<Long> eventTimestamp = new SparseArray<>();

    /**
     * 防抖动，超过一定时间才算数
     */
    private static boolean isDeBounceTrack(Object object) {

        boolean isDeBounceTrack = false;
        long currentOnClickTimestamp = System.currentTimeMillis();
        Object targetObject = eventTimestamp.get(object.hashCode());
        if (targetObject != null) {
            long lastOnClickTimestamp = (long) targetObject;
            if ((currentOnClickTimestamp - lastOnClickTimestamp) < 500) {
                isDeBounceTrack = true;
            }
        }

        eventTimestamp.put(object.hashCode(), currentOnClickTimestamp);
        return isDeBounceTrack;
    }

    /**
     * 防抖动，超过一定时间才算数
     */
    private static boolean isDeBounceTrackForView(View view) {

        boolean isDeBounceTrack = false;

        long currentOnClickTimestamp = System.currentTimeMillis();
        String tag = (String) view.getTag(R.id.auto_track_tag_view_onclick_timestamp);
        if (!TextUtils.isEmpty(tag)) {
            try {
                long lastOnClickTimestamp = Long.parseLong(tag);
                if ((currentOnClickTimestamp - lastOnClickTimestamp) < 500) {
                    isDeBounceTrack = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        view.setTag(R.id.auto_track_tag_view_onclick_timestamp, String.valueOf(currentOnClickTimestamp));

        return isDeBounceTrack;
    }

    /**
     * 对应的埋点方法{@link Fragment#onViewCreated(View, Bundle)}
     *
     * @param object   Fragment对象
     * @param rootView 对应View对象
     * @param bundle   对应Bundle对象
     */
    public static void onFragmentViewCreated(Object object, View rootView, Bundle bundle) {
        try {
//            Log.d("自动埋点", "测试:onFragmentViewCreated");
            if (!(object instanceof HookFragmentDelegate)) {
                return;
            }

            if (rootView instanceof ViewGroup) {
                AutoTrackUtil.traverseViewForFragment(object, (ViewGroup) rootView);
            } else {
                rootView.setTag(R.id.auto_track_tag_view_fragment_name, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对应的埋点方法{@link Fragment#onResume()}
     *
     * @param object Fragment对象
     */
    public static void trackFragmentResume(Object object) {

        try {
            if (!(object instanceof HookFragmentDelegate)) {
                return;
            }
            HookFragmentDelegate fragment = (HookFragmentDelegate) object;
            if (fragment.getParentFragment() == null) {
                if (!fragment.isHidden() && fragment.getUserVisibleHint()) {
                    trackFragmentAppViewScreen(fragment);
                }
            } else {
                if (!fragment.isHidden() && fragment.getUserVisibleHint() && !fragment.getParentFragment().isHidden() && fragment.getParentFragment().getUserVisibleHint()) {
                    trackFragmentAppViewScreen(fragment);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对应的埋点方法{@link Fragment#onResume()}
     *
     * @param object Fragment对象
     */
    public static void trackFragmentShow(Object object, boolean isVisibleToUser, String parent, String des) {

        try {
//            Log.d("自动埋点", "fragment show" + object.getClass().getCanonicalName());
            if (!(object instanceof HookFragmentDelegate)) {
                return;
            }

            HookFragmentDelegate fragment = (HookFragmentDelegate) object;
            if (fragment.getParentFragment() == null) {
                if (isVisibleToUser) {
                    if (fragment.isResumed()) {
                        if (!fragment.isHidden()) {
                            trackFragmentAppViewScreen(fragment);
                        }
                    }
                }
            } else {
                if (isVisibleToUser && fragment.getParentFragment().getUserVisibleHint()) {
                    if (fragment.isResumed() && fragment.getParentFragment().isResumed()) {
                        if (!fragment.isHidden() && !fragment.getParentFragment().isHidden()) {
                            trackFragmentAppViewScreen(fragment);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应的埋点方法{@link Fragment#onDestroy()}
     *
     * @param object Fragment对象
     */
    public static void trackFragmentDestroy(Object object) {

        try {

            if (!(object instanceof HookFragmentDelegate)) {
                return;
            }

            HookFragmentDelegate fragment = (HookFragmentDelegate) object;
            String fragmengName = fragment.getClass().getCanonicalName();
//            Log.d("自动埋点", "测试:" + fragmengName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对应的埋点方法{@link Fragment#setUserVisibleHint(boolean)}
     *
     * @param object          Fragment对象
     * @param isVisibleToUser 对应boolean值
     */
    public static void trackFragmentSetUserVisibleHint(Object object, boolean isVisibleToUser) {
//        Log.d("自动埋点", "测试:trackFragmentSetUserVisibleHint");

        if (!(object instanceof HookFragmentDelegate)) {
            return;
        }

        HookFragmentDelegate fragment = (HookFragmentDelegate) object;

        if (fragment.getParentFragment() == null) {
            if (isVisibleToUser) {
                if (fragment.isResumed()) {
                    if (!fragment.isHidden()) {
                        trackFragmentAppViewScreen(fragment);
                    }
                }
            }
        } else {
            if (isVisibleToUser && fragment.getParentFragment().getUserVisibleHint()) {
                if (fragment.isResumed() && fragment.getParentFragment().isResumed()) {
                    if (!fragment.isHidden() && !fragment.getParentFragment().isHidden()) {
                        trackFragmentAppViewScreen(fragment);
                    }
                }
            }
        }
    }

    /**
     * 对应的埋点方法{@link Fragment#onHiddenChanged(boolean)}
     *
     * @param object Fragment对象
     * @param hidden 对应boolean值
     */
    public static void trackOnHiddenChanged(Object object, boolean hidden) {

        if (!(object instanceof HookFragmentDelegate)) {
            return;
        }

        HookFragmentDelegate fragment = (HookFragmentDelegate) object;
        if (fragment.getParentFragment() == null) {
            if (!hidden) {
                if (fragment.isResumed()) {
                    if (fragment.getUserVisibleHint()) {
                        trackFragmentAppViewScreen(fragment);
                    }
                }
            }
        } else {
            if (!hidden && !fragment.getParentFragment().isHidden()) {
                if (fragment.isResumed() && fragment.getParentFragment().isResumed()) {
                    if (fragment.getUserVisibleHint() && fragment.getParentFragment().getUserVisibleHint()) {
                        trackFragmentAppViewScreen(fragment);
                    }
                }
            }
        }
    }

    /**
     * 对应实现接口的埋点方法{@link ViewPager.OnPageChangeListener#onPageSelected(int)}
     *
     * @param object   this对象
     * @param position 参数选择索引
     */
    public static void trackViewPageSelected(Object object, int position) {

    }


    /**
     * Fragment日志处理
     */
    private static void trackFragmentAppViewScreen(HookFragmentDelegate fragment) {
        try {

            if (fragment == null) return;
            Activity activity = fragment.getActivity();
            AutoTrackModel model = new AutoTrackModel();
            String activityTitle = null;
            if (activity != null) {
                model.setmContext(activity);
                activityTitle = AutoTrackUtil.getActivityTitle(activity);
            }
            if (!TextUtils.isEmpty(activityTitle)) {
                model.setTrack_title(activityTitle);
            }
            //大页面
            String screenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenName(activity));
            model.setTrack_screen_name(screenName);
            //小页面
            String subScreenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenName(fragment));
            model.setTrack_sub_screen_name(subScreenName);
//            Log.d("自动埋点", "trackFragmentAppViewScreen:" + properties.toString());
//            model.setM(StatisBusiness.Action.i.toString());
            //消息类型
            model.setFlag(Flag.p);
            TrackLog.staticsUiEvent(model, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对应实现接口的埋点方法{@link ExpandableListView.OnGroupClickListener#onGroupClick(ExpandableListView, View, int, long)}
     *
     * @param expandableListView 参数对应ExpandableListView
     * @param view               参数对应View
     * @param groupPosition      参数对应int
     */
    public static void trackExpandableListViewOnGroupClick(ExpandableListView expandableListView, View view,
                                                           int groupPosition) {
        try {
            //获取Activity
            Activity activity = AutoTrackUtil.getActivityFromView(expandableListView);
            AutoTrackModel model = new AutoTrackModel();
            // 1、获取当前点击控件的全路径
            String viewPath = AutoTrackUtil.getViewPath(view);
            if (!TextUtils.isEmpty(viewPath)) {
                model.setTrack_element_viewpath(viewPath);
            }

            // 2、获取Activity的标题名
            if (activity != null) {
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }

            //3、获取当前大页面
            String screen_name = AutoTrackUtil.getScreenName(activity);
            model.setTrack_screen_name(screen_name);

            // 4、获取当前页面

            String subName = AutoTrackUtil.getScreenNameFromFragmentView(expandableListView);
            model.setTrack_sub_screen_name(subName);

            // 5、获取ExpandableListView的控件名:ViewId
            String idString = AutoTrackUtil.getViewId(expandableListView);
            if (!TextUtils.isEmpty(idString)) {
                model.setTrack_element_id(idString);
            }

            // 6、获取当前点击控件的索引位置

            model.setTrack_element_position(String.format(Locale.CHINA, "%d", groupPosition));
            model.setTrack_element_type("ExpandableListView");
            model.setFlag(Flag.l);

            // 7、获取当前控件内容
            try {
                String viewText;
                if (view instanceof ViewGroup) {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = AutoTrackUtil.traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length() - 1);
                        model.setTrack_element_content(viewText);
                    }
                } else {
                    CharSequence viewTextOnly = AutoTrackUtil.traverseViewOnly(view);
                    if (!TextUtils.isEmpty(viewTextOnly)) {
                        model.setTrack_element_content(viewTextOnly.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
//            model.setM(StatisBusiness.Action.c.toString());
//            Log.d("自动埋点", "OnGroupClick:" + properties.toString());
            TrackLog.staticsUiEvent(model, model.getTrack_element_position());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link ExpandableListView.OnChildClickListener#onChildClick(ExpandableListView, View, int, int, long)}
     *
     * @param expandableListView 参数对应ExpandableListView
     * @param view               参数对应View
     * @param groupPosition      参数对应groupPosition
     * @param childPosition      参数对应childPosition
     */
    public static void trackExpandableListViewOnChildClick(ExpandableListView expandableListView, View view,
                                                           int groupPosition, int childPosition) {
        try {

            //获取Activity
            Activity activity = AutoTrackUtil.getActivityFromView(expandableListView);

            AutoTrackModel model = new AutoTrackModel();
            // 1、获取当前点击控件的全路径
            String viewPath = AutoTrackUtil.getViewPath(view);
            if (!TextUtils.isEmpty(viewPath)) {
                model.setTrack_element_viewpath(viewPath);
            }

            // 2、获取Activity的标题名
            if (activity != null) {
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }

            //3、获取当前大页面
            String screen_name = AutoTrackUtil.getScreenName(activity);
            model.setTrack_screen_name(screen_name);

            // 4、获取当前页面

            String subName = AutoTrackUtil.getScreenNameFromFragmentView(expandableListView);
            model.setTrack_screen_name(subName);

            // 5、获取ExpandableListView的控件名:ViewId
            String idString = AutoTrackUtil.getViewId(expandableListView);
            if (!TextUtils.isEmpty(idString)) {
                model.setTrack_element_id(idString);
            }

            // 6、获取当前点击控件的索引位置
            model.setTrack_element_position(String.format(Locale.CHINA, "%d:%d", groupPosition, childPosition));
            // 7、控件的类型
            model.setTrack_element_type("ExpandableListView");
            model.setFlag(Flag.l);

            // 7、获取当前控件内容
            try {
                String viewText;
                if (view instanceof ViewGroup) {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = AutoTrackUtil.traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length() - 1);
                        model.setTrack_element_content(viewText);
                    }
                } else {
                    CharSequence viewTextOnly = AutoTrackUtil.traverseViewOnly(view);
                    if (!TextUtils.isEmpty(viewTextOnly)) {
                        model.setTrack_element_content(viewTextOnly.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


//            Log.d("自动埋点", "OnChildClick:" + properties.toString());
//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, model.getTrack_element_position());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 对应实现接口的埋点方法{@link AdapterView.OnItemSelectedListener#onItemSelected(AdapterView, View, int, long)}
     * 和{@link AdapterView.OnItemClickListener#onItemClick(AdapterView, View, int, long)}
     *
     * @param adapterView 参数对应AdapterView
     * @param view        参数对应View
     * @param position    参数对应int
     */
    public static void trackListView(AdapterView<?> adapterView, View view, int position) {
        try {
            //获取Activity
            Activity activity = AutoTrackUtil.getActivityFromView(view);

            AutoTrackModel model = new AutoTrackModel();
            // 1、获取当前点击控件的全路径
            String viewPath = AutoTrackUtil.getViewPath(view);
            if (!TextUtils.isEmpty(viewPath)) {
                model.setTrack_element_viewpath(viewPath);
            }

            // 2、获取Activity的标题名
            if (activity != null) {
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }


            //3、获取当前大页面
            String screen_name = AutoTrackUtil.getScreenName(activity);
            model.setTrack_screen_name(screen_name);
            // 4、获取当前子页面
            String subName = AutoTrackUtil.getScreenNameFromFragmentView(adapterView);
            model.setTrack_sub_screen_name(subName);

            // 5、获取ExpandableListView的控件名:ViewId
            String idString = AutoTrackUtil.getViewId(adapterView);
            if (!TextUtils.isEmpty(idString)) {
                model.setTrack_element_id(idString);
            }

            // 6、获取当前点击控件的索引位置
            model.setTrack_element_position(String.valueOf(position));

            // 7、控件的类型
//            properties.put(LogConstants.Autotrack.ELEMENT_TYPE, adapterView.getClass().getSimpleName());
            model.setTrack_element_type("list");

            model.setFlag(Flag.l);


            // 7、获取当前控件内容
            try {
                String viewText;
                if (view instanceof ViewGroup) {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = AutoTrackUtil.traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length() - 1);
                        model.setTrack_element_content(viewText);
                    }
                } else {
                    CharSequence viewTextOnly = AutoTrackUtil.traverseViewOnly(view);
                    if (!TextUtils.isEmpty(viewTextOnly)) {
                        model.setTrack_element_content(viewTextOnly.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

//            Log.d("自动埋点", "onItemClick:" + properties.toString());
//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, model.getTrack_element_position());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link android.widget.TabHost.OnTabChangeListener#onTabChanged(String)}
     *
     * @param tabName 参数对应String
     */
    public static void trackTabHost(String tabName) {
        try {
            JSONObject properties = new JSONObject();

            properties.put(LogConstants.Autotrack.ELEMENT_CONTENT, tabName);
            properties.put(LogConstants.Autotrack.ELEMENT_TYPE, "TabHost");

//            Log.d("自动埋点", "onTabChanged:" + properties.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link TabLayout.OnTabSelectedListener#onTabSelected(TabLayout.Tab)}
     *
     * @param object this对象
     * @param tab    TabLayout.Tab对象
     */
    public static void trackTabLayoutSelected(Object object, Object tab) {
        try {
            if (isDeBounceTrack(tab)) {
                return;
            }

            Context context = null;

            if (!(tab instanceof TabLayout.Tab)) {
                return;
            }

            AutoTrackModel model = new AutoTrackModel();

            // 1、获取当前控件内容
            TabLayout.Tab tabObject = (TabLayout.Tab) tab;
//            Object text = ReflectUtil.getMethodValue(tab, "getText");
            Object text = tabObject.getText();
            if (text != null) {
                model.setTrack_element_content(text.toString());
            } else {
                // 获取自定义view文本内容
//                Object customViewObject = ReflectUtil.getMethodValue(tab, "getCustomView");
                View customView = tabObject.getCustomView();
                if (customView != null) {
                    try {
                        String viewText;
                        if (customView instanceof ViewGroup) {
                            StringBuilder stringBuilder = new StringBuilder();
                            viewText = AutoTrackUtil.traverseView(stringBuilder, (ViewGroup) customView);
                            if (!TextUtils.isEmpty(viewText)) {
                                viewText = viewText.substring(0, viewText.length() - 1);
                                model.setTrack_element_content(viewText);
                            }
                        } else {
                            CharSequence viewTextOnly = AutoTrackUtil.traverseViewOnly(customView);
                            if (!TextUtils.isEmpty(viewTextOnly)) {
                                model.setTrack_element_content(viewTextOnly.toString());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!(object instanceof Context)) {
                        context = customView.getContext();
                    }
                }


            }

            // 2、获取Activity
            if (object instanceof Context) {
                context = (Context) object;
            } else {
                try {
                    // 反射获取TabLayout.Tab的mParent对象
                    Object mParent = PropertyUtil.getFieldValue(tab, "mParent");
                    TabLayout tabLayout = (TabLayout) mParent;
                    context = tabLayout.getContext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Activity activity = AutoTrackUtil.getActivityFromContext(context);

            // 3、获取当前页面信息，不一定获取得到
            if (activity != null) {
                model.setmContext(activity);
                model.setTrack_screen_name(AutoTrackUtil.getScreenName(activity));
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }

            // 4、控件的类型
            model.setTrack_element_type("TabLayout");

            model.setFlag(Flag.b);

//            Log.d("自动埋点", "onTabSelected:" + properties.toString());
//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, model.getTrack_element_position());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link NavigationView.OnNavigationItemSelectedListener#onNavigationItemSelected(MenuItem)}
     *
     * @param object   this对象
     * @param menuItem 参数对应MenuItem
     */
    public static void trackMenuItem(Object object, MenuItem menuItem) {
        try {

            if (isDeBounceTrack(menuItem)) {
                return;
            }

            // 获取Activity
            Context context = null;
            if (object instanceof Context) {
                context = (Context) object;
            }
            Activity activity = AutoTrackUtil.getActivityFromContext(context);
            AutoTrackModel model = new AutoTrackModel();

            if (activity != null) {
                model.setmContext(activity);
                // 1、获取当前页面
                model.setTrack_screen_name(AutoTrackUtil.getScreenName(activity));
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    // 2、获取Activity的标题名
                    model.setTrack_title(activityTitle);
                }
            }

            // 3、获取MenuItem的控件名
            try {
                if (context != null) {
                    String idString = context.getResources().getResourceEntryName(menuItem.getItemId());
                    if (!TextUtils.isEmpty(idString)) {
                        model.setTrack_element_id(idString);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            // 4、获取当前控件内容
            if (!TextUtils.isEmpty(menuItem.getTitle())) {
                model.setTrack_element_content(menuItem.getTitle().toString());
            }

            // 5、控件的类型
            model.setTrack_element_type("MenuItem");

            model.setFlag(Flag.b);

            Log.d("自动埋点", "onNavigationItemSelected:" + model.toString());

//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link RadioGroup.OnCheckedChangeListener#onCheckedChanged(RadioGroup, int)}
     *
     * @param view      RadioGroup对象
     * @param checkedId 参数对应int
     */
    public static void trackRadioGroup(RadioGroup view, int checkedId) {
        try {

            //获取Activity
            Activity activity = AutoTrackUtil.getActivityFromView(view);

            AutoTrackModel model = new AutoTrackModel();

            // 1、获取Activity的标题名
            if (activity != null) {
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }


            //2、获取当前大页面
            String screen_name = AutoTrackUtil.getScreenName(activity);
            model.setTrack_screen_name(screen_name);
            // 3、获取当前子页面
            String subName = AutoTrackUtil.getScreenNameFromFragmentView(view);
            model.setTrack_sub_screen_name(subName);

            // 4、获取RadioGroup的控件名
            String idString = AutoTrackUtil.getViewId(view);
            if (!TextUtils.isEmpty(idString)) {
                model.setTrack_element_id(idString);
            }

            // 5、控件的类型
            model.setTrack_element_type("RadioButton");

            model.setFlag(Flag.b);

            int checkedRadioButtonId = view.getCheckedRadioButtonId();
            if (activity != null) {
                try {
                    RadioButton radioButton = (RadioButton) activity.findViewById(checkedRadioButtonId);
                    if (radioButton != null) {
                        if (!TextUtils.isEmpty(radioButton.getText())) {
                            String viewText = radioButton.getText().toString();
                            if (!TextUtils.isEmpty(viewText)) {
                                // 5、获取当前控件内容
                                model.setTrack_element_content(viewText);
                            }
                        }
                        // 6、获取当前点击控件的全路径
                        String viewPath = AutoTrackUtil.getViewPath(radioButton);
                        if (!TextUtils.isEmpty(viewPath)) {
                            model.setTrack_element_viewpath(viewPath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //5. 获取点击位置
            Object index = view.getTag(R.id.sub_view_tag);
            if (index != null) {
                model.setTrack_element_position(String.valueOf(index));
            }

//            Log.d("自动埋点", "onNavigationItemSelected:" + properties.toString());

//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对应实现接口的埋点方法{@link DialogInterface.OnClickListener#onClick(DialogInterface, int)}
     *
     * @param dialogInterface DialogInterface对象
     * @param whichButton     参数对应int
     */
    public static void trackDialog(DialogInterface dialogInterface, int whichButton) {
        try {
            //获取所在的Context
            Dialog dialog = null;
            if (dialogInterface instanceof Dialog) {
                dialog = (Dialog) dialogInterface;
            }

            if (dialog == null) {
                return;
            }

            if (isDeBounceTrack(dialog)) {
                return;
            }

            Context context = dialog.getContext();

            //将Context转成Activity
            Activity activity = AutoTrackUtil.getActivityFromContext(context);

            if (activity == null) {
                activity = dialog.getOwnerActivity();
            }

            AutoTrackModel model = new AutoTrackModel();

            if (activity != null) {
                model.setmContext(activity);
                // 1、获取当前页面
                model.setTrack_screen_name(AutoTrackUtil.getScreenName(activity));
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    // 2、获取Activity的标题名
                    model.setTrack_title(activityTitle);
                }
            }

            model.setTrack_element_type("Dialog");
            model.setFlag(Flag.b);
            if (dialog instanceof android.app.AlertDialog) {
                android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialog;
                Button button = alertDialog.getButton(whichButton);
                if (button != null) {
                    if (!TextUtils.isEmpty(button.getText())) {
                        model.setTrack_element_content(button.getText().toString());
                    }
                } else {
                    ListView listView = alertDialog.getListView();
                    if (listView != null) {
                        ListAdapter listAdapter = listView.getAdapter();
                        Object object = listAdapter.getItem(whichButton);
                        if (object != null) {
                            if (object instanceof String) {
                                model.setTrack_element_content((String) object);
                            }
                        }
                    }
                }

            } else if (dialog instanceof androidx.appcompat.app.AlertDialog) {
                androidx.appcompat.app.AlertDialog alertDialog = (androidx.appcompat.app.AlertDialog) dialog;
                Button button = alertDialog.getButton(whichButton);
                if (button != null) {
                    if (!TextUtils.isEmpty(button.getText())) {
                        model.setTrack_element_content(button.getText().toString());
                    }
                } else {
                    ListView listView = alertDialog.getListView();
                    if (listView != null) {
                        ListAdapter listAdapter = listView.getAdapter();
                        Object object = listAdapter.getItem(whichButton);
                        if (object != null) {
                            if (object instanceof String) {
                                model.setTrack_element_content((String) object);
                            }
                        }
                    }
                }
            }

//            Log.d("自动埋点", "trackDialog:" + properties.toString());
//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听 void onDrawerOpened(View)方法
     *
     * @param view 方法中的view参数
     */
    public static void trackDrawerOpened(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(LogConstants.Autotrack.ELEMENT_CONTENT, "Open");

            if (view != null) {
                view.setTag(R.id.auto_track_tag_view_properties, jsonObject);
            }

            trackViewOnClick(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听 void onDrawerClosed(View)方法
     *
     * @param view 方法中的view参数
     */
    public static void trackDrawerClosed(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(LogConstants.Autotrack.ELEMENT_CONTENT, "Close");

            if (view != null) {
                view.setTag(R.id.auto_track_tag_view_properties, jsonObject);
            }

            trackViewOnClick(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trackViewOnClick(View view) {
        try {
            if (isDeBounceTrackForView(view)) {
                return;
            }
            modifyClick(null, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyClick(String screen, View view) {
        try {
            //获取Activity
            Activity activity = AutoTrackUtil.getActivityFromView(view);

            AutoTrackModel model = new AutoTrackModel();


            // 1、获取当前点击控件的全路径
            String viewPath = AutoTrackUtil.getViewPath(view);
            if (!TextUtils.isEmpty(viewPath)) {
                model.setTrack_element_viewpath(viewPath);
            }

            // 2、获取Activity的标题名
            if (activity != null) {
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
            }
            //3、获取当前大页面
            String screen_name = AutoTrackUtil.getScreenName(activity);
            model.setTrack_screen_name(screen_name);
            // 4、获取当前子页面
            if (TextUtils.isEmpty(screen)) {
                String subName = AutoTrackUtil.getScreenNameFromFragmentView(view);
                model.setTrack_sub_screen_name(subName);
            } else {
                model.setTrack_sub_screen_name(screen);
            }

            // 5、获取ExpandableListView的控件名:ViewId
            String idString = AutoTrackUtil.getViewId(view);
            if (!TextUtils.isEmpty(idString)) {
                model.setTrack_element_id(idString);
            }

            // 6、控件的类型
            Object type = view.getTag(R.id.auto_track_tag_type);
            if (type != null && type instanceof String) {
                model.setTrack_element_type((String) view.getTag(R.id.auto_track_tag_type));
                model.setFlag(Flag.l);
            } else {
                model.setTrack_element_type(view.getClass().getSimpleName());
                model.setFlag(Flag.b);
            }

            // 7、获取当前控件内容
            try {
                String viewText;
                if (view.getTag(R.id.auto_track_tag_type) != null) {
                    //标记列表类型，不拿取content值
                } else {
                    if (view instanceof ViewGroup) {
                        StringBuilder stringBuilder = new StringBuilder();
                        viewText = AutoTrackUtil.traverseView(stringBuilder, (ViewGroup) view);
                        if (!TextUtils.isEmpty(viewText)) {
                            viewText = viewText.substring(0, viewText.length() - 1);
                            model.setTrack_element_content(viewText);
                        }
                    } else {
                        CharSequence viewTextOnly = AutoTrackUtil.traverseViewOnly(view);
                        if (!TextUtils.isEmpty(viewTextOnly)) {
                            model.setTrack_element_content(viewTextOnly.toString());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //8. 获取里表中点击位置
            Object index = view.getTag(R.id.sub_view_tag);
            if (index != null) {
                model.setTrack_element_position(String.valueOf(index));
            }

//            // 9、获取 View 自定义属性
//            JSONObject p = (JSONObject) view.getTag(R.id.auto_track_tag_view_properties);
//            if (p != null) {
//                AutoTrackUtil.mergeJSONObject(p, properties);
//            }

//            Log.d("自动埋点", "trackViewOnClick:" + properties.toString());
//            model.setM(StatisBusiness.Action.c.toString());
            TrackLog.staticsUiEvent(model, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trackViewOnClick(Object anything) {
        try {
//            Log.d("自动埋点", "测试:trackViewOnClick");
            if (anything == null) {
                return;
            }

            if (!(anything instanceof View)) {
                return;
            }

            trackViewOnClick((View) anything);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void trackActivityCreate(Activity activity, Bundle bundle) {

        try {
            if (activity == null || !(activity instanceof HookActivityDelegate)) {
                return;
            }
//            if (activity != null) {
//                AutoTrackModel model = new AutoTrackModel();
//                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
//                if (!TextUtils.isEmpty(activityTitle)) {
//                    model.setTrack_title(activityTitle);
//                }
//                String screenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenNameForActivity(activity));
//                model.setTrack_screen_name(screenName);
//                model.setM(StatisBusiness.Action.i.toString());
//                model.setFlag(Flag.p);
//                TrackLog.staticsUiEvent(model, null);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void trackActivityResume(Activity activity) {

        try {
            if (activity == null || !(activity instanceof HookActivityDelegate)) {
                return;
            }
            if (activity != null) {
                AutoTrackModel model = new AutoTrackModel();
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
                String screenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenName(activity));
                model.setTrack_screen_name(screenName);
                model.setM(StatisBusiness.Action.i.toString());
                model.setFlag(Flag.p);
                TrackLog.staticsUiEvent(model, null);
            }

            if (!TrackSDK.getInstance().isForground()) {
                //标记从后台进入前台
                TrackSDK.getInstance().setForground(true);
                Log.d("自动埋点", "进入前台");
                //重置app进入时间
                TrackSDK.getInstance().getP().initTime = System.currentTimeMillis();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trackActivityStop(Activity activity) {

        try {
            if (activity == null || !(activity instanceof HookActivityDelegate)) {
                return;
            }
//            if (activity != null) {
//                AutoTrackModel model = new AutoTrackModel();
//                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
//                if (!TextUtils.isEmpty(activityTitle)) {
//                    model.setTrack_title(activityTitle);
//                }
//                String screenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenNameForActivity(activity));
//                model.setTrack_screen_name(screenName);
////                Log.d("自动埋点", "trackActivityAppViewScreenResume:" + properties.toString());
//            }


            if (!AppUtils.isAppOnForeground(activity)) {
                //标记进入后台
                TrackSDK.getInstance().setForground(false);
                //打点上报，认为一次使用完毕
                Log.d("自动埋点", "进入后台");
                TrackLog.staticsAppDelay();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trackActivityDestroy(Activity activity) {
        try {
            if (activity == null || !(activity instanceof HookActivityDelegate)) return;
            if (activity != null) {
                AutoTrackModel model = new AutoTrackModel();
                model.setmContext(activity);
                String activityTitle = AutoTrackUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    model.setTrack_title(activityTitle);
                }
                String screenName = String.format(Locale.CHINA, "%s", AutoTrackUtil.getScreenName(activity));
                model.setTrack_screen_name(screenName);
//                Log.d("自动埋点", "trackActivityAppViewScreenDestory:" + properties.toString());
                model.setM(StatisBusiness.Action.o.toString());
                model.setFlag(Flag.p);
                TrackLog.staticsUiEvent(model, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void trackAdaperHolder(RecyclerView.ViewHolder holder, int position) {
        try {
//            Log.d("自动埋点", "trackAdaperHolder:" + position);
            if (holder != null && holder.itemView != null) {
                holder.itemView.setTag(R.id.sub_view_tag, position);
                holder.itemView.setTag(R.id.auto_track_tag_type, "list");
                AutoTrackUtil.traverseViewForViewHolder(holder.itemView, position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
