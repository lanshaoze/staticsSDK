package com.mampod.track.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解型onClick事件
 *
 * @package com.mampod.track.sdk.annotation
 * @author: Jack-Lu
 * @date:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoTrackDataViewOnClick {
}
