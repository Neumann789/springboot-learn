package com.beust.jcommander;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Parameters
{
  public static final String DEFAULT_OPTION_PREFIXES = "-";
  
  String resourceBundle() default "";
  
  String separators() default " ";
  
  String optionPrefixes() default "-";
  
  String commandDescription() default "";
  
  String commandDescriptionKey() default "";
  
  String[] commandNames() default {};
}


