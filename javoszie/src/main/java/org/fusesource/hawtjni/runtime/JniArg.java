package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JniArg
{
  ArgFlag[] flags() default {};
  
  String cast() default "";
}


