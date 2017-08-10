 package com.javosize.agent;
 
 import java.io.Serializable;
 
 public class Interception implements Serializable {
   private static final long serialVersionUID = -3796655954293000226L;
   private Type type;
   
   public static enum Type { begin,  end;
     
     private Type() {}
   }
   
   private String id;
   private Interceptor interceptor;
   private String classNameRegexp;
   private String methodNameRegexp;
   private String interceptorCode;
   private String appName;
   public boolean isBeginInterception() {
     return this.type.equals(Type.begin);
   }
   
   public boolean isEndInterception() {
     return this.type.equals(Type.end);
   }
   
   public String getInterceptorCode() {
     return this.interceptorCode;
   }
   
   public void setInterceptorCode(String interceptorCode) { this.interceptorCode = interceptorCode; }
   
   public String getMethodNameRegexp() {
     return this.methodNameRegexp;
   }
   
   public void setMethodNameRegexp(String methodNameRegexp) { this.methodNameRegexp = methodNameRegexp; }
   
   public String getClassNameRegexp() {
     return this.classNameRegexp;
   }
   
   public void setClassNameRegexp(String classNameRegexp) { this.classNameRegexp = classNameRegexp; }
   
   public Type getType() {
     return this.type;
   }
   
   public void setType(String type) { this.type = Type.valueOf(type); }
   
   public String getId() {
     return this.id;
   }
   
   public void setId(String id) { this.id = id; }
   
   public Interceptor getInterceptor() {
     return this.interceptor;
   }
   
   public void setInterceptor(Interceptor interceptor) { this.interceptor = interceptor; }
   
   public boolean matchesClassName(String className)
   {
     return className.matches(this.classNameRegexp);
   }
   
   public void initInterceptor() throws Throwable { this.interceptor = new Interceptor(getInterceptorCode(), this.appName); }
   
   public String toString()
   {
     StringBuffer sb = new StringBuffer();
     sb.append(this.id);
     sb.append(" ");
     sb.append(this.type);
     sb.append(" ");
     sb.append(this.classNameRegexp);
     sb.append(" ");
     sb.append(this.methodNameRegexp);
     return sb.toString();
   }
   
   public boolean matchesMethodName(String name) {
     return name.matches(getMethodNameRegexp());
   }
   
   public void setAppName(String appName) {
     this.appName = appName;
   }
 }


