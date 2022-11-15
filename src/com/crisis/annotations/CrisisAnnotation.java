package com.crisis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface CrisisAnnotation {

    enum PRIORITY {
        NONE, LOW, MEDIUM, HIGH
    }

    PRIORITY priority_order() default PRIORITY.MEDIUM;

    PRIORITY priority_only() default PRIORITY.NONE;

    boolean suppress_warnings() default false;

    String createdBy() default "";

    String lastModified() default "15/11/2022";

    String createdDate() default "13/11/2022";

    boolean silent() default false;

    boolean colorize() default true;

}
