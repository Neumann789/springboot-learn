package com.javosize.compiler;

import com.javosize.agent.Utils;
import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.log.Log;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class InMemoryJavaCompiler {
	private static Log log = new Log(InMemoryJavaCompiler.class.getName());

	static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

	public static List<Class> compileAndLoadClass(String className, String sourceCodeInText, ClassLoader orgCl,
			String targetVersion) throws Throwable {
		if (orgCl == null) {
			ClassLoader firstAppClassLoader = UserThreadSessionTracker.getFirtAvailableClassLoader();
			if (firstAppClassLoader != null) {
				orgCl = firstAppClassLoader.getParent();
			} else {
				orgCl = ClassLoader.getSystemClassLoader();
			}
		}
		DynamicClassLoader cl = new DynamicClassLoader(orgCl);
		Map<String, byte[]> byteCodes = compileAndReturnBytecode(className, sourceCodeInText, orgCl, cl, targetVersion);
		return ClassLoaderInjector.injectClassesInClassLoader(byteCodes, orgCl);
	}

	public static Map<String, byte[]> compileAndReturnBytecode(String className, String sourceCodeInText,
			ClassLoader origClassCl, DynamicClassLoader dynamicCl, String targetVersion) throws Throwable {
		if (javac == null) {
			throw new Exception(
					"Can not complete action. Your monitored app needs to be running with JDK and not JRE.");
		}
		if (origClassCl == null) {
			origClassCl = ClassLoader.getSystemClassLoader();
		}
		if (dynamicCl == null) {
			dynamicCl = new DynamicClassLoader(origClassCl);
		}

		StringBuffer classpath = new StringBuffer();
		addClassForClassLoader(origClassCl, classpath);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector();
		try {
			return compile(className, sourceCodeInText, classpath, diagnostics, targetVersion);
		} catch (Exception e) {
			log.warn("Error compiling in first attempt. Exception: " + e, e);
			log.warn("Error compiling in first attempt. Diagnostics: " + getDiagnosticsMessages(diagnostics));
			addMissingSymbolsToCp(className, diagnostics, origClassCl, classpath);
			diagnostics = new DiagnosticCollector();
			try {
				return compile(className, sourceCodeInText, classpath, diagnostics, targetVersion);
			} catch (Throwable th) {
				log.warn("Error compiling in second attempt. Exception: " + th, th);
				log.warn("Error compiling in second attempt. Diagnostics: " + getDiagnosticsMessages(diagnostics));
				log.debug("Error compiling class " + className + " with classpath: " + classpath);
				String compileMessage = "Errors compiling class " + className + ".\n - Exception: " + th
						+ "\n - Diagnose: " + getDiagnosticsMessages(diagnostics);
				throw new Exception(compileMessage, th);
			}
		}
	}

	private static Map<String, byte[]> compile(String className, String sourceCodeInText, StringBuffer classpath,
			DiagnosticCollector<JavaFileObject> diagnostics, String targetVersion) throws Exception {
		SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(new SourceCode[] { sourceCode });

		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(
				javac.getStandardFileManager(diagnostics, null, null));
		List<String> optionList = new ArrayList();
		if (targetVersion != null) {
			String sourceVersion = targetVersion;

			if ((sourceVersion.equals("1.1")) || (sourceVersion.equals("1.2"))) {
				sourceVersion = "1.3";
			}
			log.debug("Compiling class " + className + " using Java version " + targetVersion);
			optionList.addAll(Arrays.asList(new String[] { "-classpath", classpath.toString(), "-target", targetVersion,
					"-source", sourceVersion }));
		} else {
			optionList.addAll(Arrays.asList(new String[] { "-classpath", classpath.toString() }));
		}
		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnits);
		if (!task.call().booleanValue()) {
			throw new Exception("Error compiling, check diagnose info.");
		}

		return fileManager.getAllBuffers();
	}

	private static void addClassForClassLoader(ClassLoader cl, StringBuffer sb) {
		while ((cl != null) && (cl != cl.getParent())) {
			String[] urls = getAllUrlsForCL(cl);
			for (String url : urls) {
				checkAndAddPathSeparator(sb);
				sb.append(fixForWindows(url));
			}
			cl = cl.getParent();
		}
	}

	private static String[] getAllUrlsForCL(ClassLoader targetCl) {
		Set<String> result = new HashSet();

		if ((targetCl instanceof URLClassLoader)) {
			URLClassLoader urlc = (URLClassLoader) targetCl;

			for (URL url : urlc.getURLs()) {
				result.add(Utils.getFileFromURL(url));
			}
		} else {
			Instrumentation instrumentation = com.javosize.agent.Agent.getInstrumentation();
			Class[] classes = instrumentation.getAllLoadedClasses();

			for (int i = 0; i < classes.length; i++) {
				ClassLoader cl = classes[i].getClassLoader();
				if ((cl != null) && (cl.equals(targetCl))) {
					String url = com.javosize.actions.ListClassesAction.obtainJarFileName(classes[i]);
					if ((url != null) && (!"NULL".equals(url))) {
						result.add(url);
					}
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	private static void checkAndAddPathSeparator(StringBuffer sb) {
		if (sb.toString().equals("")) {
			return;
		}

		if (!sb.toString().endsWith(File.pathSeparator)) {
			sb.append(File.pathSeparator);
		}
	}

	private static String fixForWindows(String file) {
		if (!"\\".equals(File.separator)) {
			return file;
		}

		if (file.startsWith("file:/")) {
			file = file.substring("file:/".length());
		}

		String result = file.replace("%20", " ");

		result = result.replace("/", "\\");
		return result;
	}

	private static void addMissingSymbolsToCp(String className, DiagnosticCollector<JavaFileObject> diagnostics, ClassLoader origCl, StringBuffer classpath) {
     Set<String> packages = new HashSet();
     Set<String> symbols = new HashSet();
     
     if (diagnostics == null) {
       return;
     }
     
 
 
 
     packages.add("");
     
 
     packages.add(Utils.getPackageName(className));
     String msg;
     for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
       msg = diagnostic.getMessage(null);
       
       log.debug("Processing diagnostic message: " + msg);
       if ((msg != null) && (msg.contains("package ")) && (msg.contains("does not exist"))) {
         String pack = msg.split("package ")[1];
         pack = pack.split(" does not exist")[0];
         log.debug("Detected missing package: " + pack);
         packages.add(pack);
       } else if ((msg != null) && (msg.contains("cannot find symbol")) && (msg.contains("class "))) {
         String symbol = msg.split("class ")[1];
         symbol = symbol.split("\n")[0].trim();
         log.debug("Detected missing class: " + symbol);
         symbols.add(symbol);
       }
     }
     
     String symbol;
     
     for (Iterator it = symbols.iterator(); it.hasNext();) {
    	 symbol = (String)it.next();
       log.debug("Trying to find symbol " + symbol + " at any of the missing packages....");
       for (String pack : packages) {
         log.debug("Is " + symbol + " at package " + pack + "? [CL: " + origCl.toString() + "]");
         URL resPath = getResource(pack + "." + symbol, origCl);
         if (resPath != null) {
           log.debug("Class " + symbol + " belongs to package " + pack + ". Adding the resource " + resPath + " to compile classpath.");
           checkAndAddPathSeparator(classpath);
           classpath.append(getFileFromUrl(resPath));
         }
       }
     }
   
   }

	private static String getFileFromUrl(URL resPath) {
		try {
			int idx = resPath.toString().indexOf('!');
			String fileName = resPath.getFile();
			if (idx != -1) {
				return URLDecoder.decode(resPath.toString().substring("jar:file:".length(), idx),
						Charset.defaultCharset().name());
			}
			return resPath.getFile();
		} catch (UnsupportedEncodingException e) {
		}
		return resPath.getFile();
	}

	private static URL getResource(String classname, ClassLoader origCl) {
		Class c;
		try {
			c = origCl.loadClass(classname);
		} catch (ClassNotFoundException e) {
			log.debug("ClassNotFoundException trying to load class " + classname + " from class loader " + origCl);
			return null;
		} catch (NoClassDefFoundError e) {
			log.debug("NoClassDefFoundError trying to load class " + classname + " from class loader " + origCl);
			return null;
		}
		
		URL result = null;

		if ((c.getProtectionDomain() != null) && (c.getProtectionDomain().getCodeSource() != null)) {
			result = c.getProtectionDomain().getCodeSource().getLocation();
			if (result != null) {
				return result;
			}
		} else {
			log.debug("Unable to get location for class " + classname + " at classloader " + origCl);
		}

		return null;
	}

	private static String getDiagnosticsMessages(DiagnosticCollector<JavaFileObject> diagnostics) {
		String message = "";
		try {
			if (diagnostics != null) {
				for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {

					message = message + "\n\t -  [Line:" + diagnostic.getLineNumber() + "]"
							+ diagnostic.getMessage(null);
				}
			}
		} catch (Throwable th) {
			message = "Unable to obtain diagnostics info: " + th;
			log.error(message, th);
		}
		return message;
	}
}
