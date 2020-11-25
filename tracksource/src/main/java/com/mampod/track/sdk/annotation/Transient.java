package com.mampod.track.sdk.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @package： com.mampod.ergedd.statistics
 * @Des: 上报统计字段标记忽略功能
 * @author: Jack-Lu
 * @time: 2018/9/26 下午6:46
 * @change:
 * @changtime:
 * @changelog:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transient {
}
