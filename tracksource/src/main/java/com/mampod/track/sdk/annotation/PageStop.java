package com.mampod.track.sdk.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Activity生命周期onStop注解标记
 *
 * @package com.mampod.track.sdk.annotation
 * @author: Jack-Lu
 * @date:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageStop {
    String value() default "";
}
