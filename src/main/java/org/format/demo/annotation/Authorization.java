package org.format.demo.annotation;

import org.format.demo.model.AuthMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorization {

    String[] roles() default {};

    String[] auth() default {};

    AuthMode mode() default AuthMode.AND;

}
