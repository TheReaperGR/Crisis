package com.crisis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAnnotation {

    CrisisAnnotation.PRIORITY priority() default CrisisAnnotation.PRIORITY.MEDIUM;

    boolean enabled() default true;

    boolean result() default true;

    String[] parameters() default " ";

    Class[] types() default String.class;

    boolean array() default false;

}
