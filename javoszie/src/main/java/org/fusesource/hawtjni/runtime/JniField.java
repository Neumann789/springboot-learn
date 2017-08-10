package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JniField
{
  String cast() default "";
  
  String accessor() default "";
  
  String conditional() default "";
  
  FieldFlag[] flags() default {};
}


