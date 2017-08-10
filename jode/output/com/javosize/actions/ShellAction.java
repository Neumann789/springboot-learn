/* ShellAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.net.URLClassLoader;

import com.javosize.agent.ReflectionUtils;
import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.compiler.InMemoryJavaCompiler;

public class ShellAction extends Action
{
    private static final long serialVersionUID = 7012178819397554068L;
    private String code = "return \"IT WORKED!\"";
    private static int numberOfExecutions = 0;
    private String appName = null;
    private final String SHELL_CODE
	= "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport java.util.Iterator;\n//import javax.servlet.http.HttpSession;\nimport com.javosize.agent.session.UserThreadSessionTracker;\npublic class JavosizeShell {\n\tpublic static String execute(){\n       CODE_SNIPPED\n \t}}";
    
    public ShellAction(String code, String appName) {
	this.code = code;
	this.appName = appName;
    }
    
    public String execute() {
	String string;
	try {
	    numberOfExecutions++;
	    String sourceCode = getActualSrc();
	    Class javoShell
		= (Class) InMemoryJavaCompiler.compileAndLoadClass
			      (getCurrentClassName(), sourceCode.toString(),
			       getAppClassLoader(appName), null)
			      .get(0);
	    string = (String) ReflectionUtils.invokeStaticMethod(javoShell,
								 "execute",
								 null, null);
	} catch (Throwable th) {
	    th.printStackTrace();
	    System.out.println(new StringBuilder().append("Shell: ").append
				   (getActualSrc().toString()).toString());
	    return new StringBuilder().append
		       ("ERROR: Executing shell action: ").append
		       (th).toString();
	}
	return string;
    }
    
    private ClassLoader getAppClassLoader(String appName) {
	if (appName == null)
	    return (URLClassLoader) ClassLoader.getSystemClassLoader();
	return UserThreadSessionTracker.getClassLoaderForApp(appName);
    }
    
    private String getActualSrc() {
	String sourceCode
	    = "import java.lang.reflect.Method;\nimport java.util.Collection;\nimport java.util.Iterator;\n//import javax.servlet.http.HttpSession;\nimport com.javosize.agent.session.UserThreadSessionTracker;\npublic class JavosizeShell {\n\tpublic static String execute(){\n       CODE_SNIPPED\n \t}}"
		  .replace("JavosizeShell", getCurrentClassName());
	return sourceCode.replace("CODE_SNIPPED", code);
    }
    
    private String getCurrentClassName() {
	return new StringBuilder().append("JavosizeShell").append
		   (numberOfExecutions).toString();
    }
}
