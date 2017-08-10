 package com.javosize.agent;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.compiler.InMemoryJavaCompiler;
 import java.util.List;
 
 public class Interceptor implements java.io.Serializable
 {
   private static final long serialVersionUID = 6704262479741301027L;
   private static int numberOfExecutions = 0;
   
   private String code;
   private String classname;
   private final String SHELL_CODE = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport com.javosize.metrics.MetricCollector;\nimport com.javosize.metrics.MetricType;\nimport java.util.Iterator;\npublic class JavosizeShell {\n\tpublic static void execute(Object params[]){\n       CODE_SNIPPED\n \t}}";
   
 
 
 
 
   private String appName;
   
 
 
 
 
   public String getClassname()
   {
     return this.classname;
   }
   
   public void setClassname(String classname) {
     this.classname = classname;
   }
   
   public Interceptor(String code, String appName) throws Throwable {
     this.code = code;
     this.classname = createInterceptorClass();
     this.appName = appName;
   }
   
   private String getCurrentClassName() {
     return "JavosizeInterceptor" + numberOfExecutions;
   }
   
   public synchronized String createInterceptorClass() throws Throwable {
     numberOfExecutions += 1;
     this.classname = getCurrentClassName();
     String sourceCode = getActualSrc();
     Class<?> javoShell = (Class)InMemoryJavaCompiler.compileAndLoadClass(this.classname, sourceCode.toString(), UserThreadSessionTracker.getClassLoaderForApp(this.appName), null).get(0);
     return this.classname;
   }
   
   private String getActualSrc()
   {
     String sourceCode = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport com.javosize.metrics.MetricCollector;\nimport com.javosize.metrics.MetricType;\nimport java.util.Iterator;\npublic class JavosizeShell {\n\tpublic static void execute(Object params[]){\n       CODE_SNIPPED\n \t}}".replace("JavosizeShell", getCurrentClassName());
     return sourceCode.replace("CODE_SNIPPED", this.code);
   }
 }


