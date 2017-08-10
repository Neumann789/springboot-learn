 package com.javosize.actions;
 
 import com.javosize.agent.ReflectionUtils;
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.compiler.InMemoryJavaCompiler;
 import java.net.URLClassLoader;
 import java.util.List;
 
 public class ShellAction extends Action
 {
   private static final long serialVersionUID = 7012178819397554068L;
   private String code = "return \"IT WORKED!\"";
   private static int numberOfExecutions = 0;
   private String appName = null;
   
   private final String SHELL_CODE = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport java.util.Iterator;\n//import javax.servlet.http.HttpSession;\nimport com.javosize.agent.session.UserThreadSessionTracker;\npublic class JavosizeShell {\n\tpublic static String execute(){\n       CODE_SNIPPED\n \t}}";
   
 
 
 
 
 
 
 
 
 
 
   public ShellAction(String code, String appName)
   {
     this.code = code;
     this.appName = appName;
   }
   
   public String execute()
   {
     try {
       numberOfExecutions += 1;
       String sourceCode = getActualSrc();
       Class<?> javoShell = (Class)InMemoryJavaCompiler.compileAndLoadClass(getCurrentClassName(), sourceCode.toString(), getAppClassLoader(this.appName), null).get(0);
       return (String)ReflectionUtils.invokeStaticMethod(javoShell, "execute", null, null);
     } catch (Throwable th) {
       th.printStackTrace();
       System.out.println("Shell: " + getActualSrc().toString());
       return "ERROR: Executing shell action: " + th;
     }
   }
   
   private ClassLoader getAppClassLoader(String appName)
   {
     if (appName == null) {
       return (URLClassLoader)ClassLoader.getSystemClassLoader();
     }
     return UserThreadSessionTracker.getClassLoaderForApp(appName);
   }
   
   private String getActualSrc() {
     String sourceCode = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport java.util.Iterator;\n//import javax.servlet.http.HttpSession;\nimport com.javosize.agent.session.UserThreadSessionTracker;\npublic class JavosizeShell {\n\tpublic static String execute(){\n       CODE_SNIPPED\n \t}}".replace("JavosizeShell", getCurrentClassName());
     return sourceCode.replace("CODE_SNIPPED", this.code);
   }
   
   private String getCurrentClassName() {
     return "JavosizeShell" + numberOfExecutions;
   }
 }


