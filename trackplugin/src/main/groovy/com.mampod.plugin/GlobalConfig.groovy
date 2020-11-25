package com.mampod.plugin

import com.mampod.plugin.bean.AutoClassFilter
import com.mampod.plugin.bean.AutoSettingParams
import org.gradle.api.Project

/**
 * @package： com.mampod.plugin.GlobalConfig
 * @Des:  全局配置类
 * @author: Jack-Lu
 * @time:
 * @change:
 * @changtime:
 * @changelog:
 */
public class GlobalConfig {
    private static Project project
    private static List<AutoClassFilter> autoClassFilter
    private static HashSet<String> exclude
    private static HashSet<String> include

    public static void setProject(Project project) {
        GlobalConfig.@project = project
    }

    public static Project getProject() {
        return project
    }

    static AutoSettingParams getParams() {
        return project.noTracePoint
    }

    static void setAutoClassFilter(List<AutoClassFilter> filter) {
        autoClassFilter = filter
    }

    static List<AutoClassFilter> getAutoClassFilter() {
        return autoClassFilter
    }


    static boolean getIsOpenLogTrack() {
        return getParams().isOpenLogTrack
    }

    static boolean getIsOutputModifyFile() {
        return getParams().isOutputModifyFile
    }

    static HashSet<String> getExclude() {
        return exclude
    }

    static void setExclude(HashSet<String> exclude) {
        this.exclude = exclude
    }

    static HashSet<String> getInclude() {
        return include
    }

    static void setInclude(HashSet<String> include) {
        this.include = include
    }
}