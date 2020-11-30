package com.mampod.track.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 插桩成功注解标记
 * @package com.mampod.track.sdk.annotation
 * @author: Jack-Lu
 * @date:
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoDataInstrumented {
}
