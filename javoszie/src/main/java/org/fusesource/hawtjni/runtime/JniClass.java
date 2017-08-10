package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JniClass
{
  ClassFlag[] flags() default {};
  
  String conditional() default "";
  
  String name() default "";
}


