/* Interceptor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent;
import java.io.Serializable;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.compiler.InMemoryJavaCompiler;

public class Interceptor implements Serializable
{
    private static final long serialVersionUID = 6704262479741301027L;
    private static int numberOfExecutions = 0;
    private String code;
    private String classname;
    private final String SHELL_CODE
	= "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport com.javosize.metrics.MetricCollector;\nimport com.javosize.metrics.MetricType;\nimport java.util.Iterator;\npublic class JavosizeShell {\n\tpublic static void execute(Object params[]){\n       CODE_SNIPPED\n \t}}";
    private String appName;
    
    public String getClassname() {
	return classname;
    }
    
    public void setClassname(String classname) {
	this.classname = classname;
    }
    
    public Interceptor(String code, String appName) throws Throwable {
	this.code = code;
	classname = createInterceptorClass();
	this.appName = appName;
    }
    
    private String getCurrentClassName() {
	return new StringBuilder().append("JavosizeInterceptor").append
		   (numberOfExecutions).toString();
    }
    
    public synchronized String createInterceptorClass() throws Throwable {
	numberOfExecutions++;
	classname = getCurrentClassName();
	String sourceCode = getActualSrc();
	Class javoShell
	    = ((Class)
	       InMemoryJavaCompiler.compileAndLoadClass
		   (classname, sourceCode.toString(),
		    UserThreadSessionTracker.getClassLoaderForApp(appName),
		    null)
		   .get(0));
	return classname;
    }
    
    private String getActualSrc() {
	String sourceCode
	    = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport com.javosize.metrics.MetricCollector;\nimport com.javosize.metrics.MetricType;\nimport java.util.Iterator;\npublic class JavosizeShell {\n\tpublic static void execute(Object params[]){\n       CODE_SNIPPED\n \t}}"
		  .replace("JavosizeShell", getCurrentClassName());
	return sourceCode.replace("CODE_SNIPPED", code);
    }
}
