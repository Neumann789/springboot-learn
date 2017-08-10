package com.javosize.decompile;

import com.javosize.actions.CompileClassAction;
import com.javosize.agent.Tools;
import com.javosize.log.Log;
import com.javosize.remote.Controller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Decompiler {
	private static Log log = new Log(Decompiler.class.getName());

	public static enum Tool {
		JODE, PROCYON;

		private Tool() {
		}
	}

	public static DecompilationResult decompileAndValidate(String className, Map<String, byte[]> classes) {
		DecompilationResult result = new DecompilationResult();

		if (classes.size() > 0) {
			try {
				byte[] bytecode = (byte[]) classes.values().iterator().next();
				String version = Tools.getVersionOfCompilation(bytecode);
				result.setTargetVersion(version);
			} catch (Throwable th) {
				log.debug("Unable to recover version of compilation: " + th, th);
			}
		}

		try {
			String decompilation = decompile(className, classes, Tool.PROCYON) + "\n";

			CompileClassAction compiler = new CompileClassAction(decompilation, className);
			String compileResult = Controller.getInstance().execute(compiler);
			if (compileResult.equals("true")) {
				result.setDecompilation(decompilation);
				result.setCompilationOK(true);
				return result;
			}
			result.setDecompilation(decompilation);
			result.setCompilationOK(false);
		} catch (Throwable th) {
			log.trace("Procyon Decompilation error: " + th, th);
			result.setDecompilationErrors(" - Procyon Decompilation Error Message: " + th + "\n");
		}

		try {
			String decompilation = decompile(className, classes, Tool.JODE) + "\n";

			CompileClassAction compiler = new CompileClassAction(decompilation, className);
			String compileResult = Controller.getInstance().execute(compiler);
			if (compileResult.equals("true")) {
				result.setDecompilation(decompilation);
				result.setCompilationOK(true);
				return result;
			}
			result.setDecompilation(decompilation);
			result.setCompilationOK(false);
		} catch (Throwable th) {
			log.trace("Jode Decompilation error: " + th, th);

			if (!result.isDecompilationOK()) {
				result.setDecompilationErrors(
						result.getDecompilationErrors() + " - Jode Decompilation Error Message: " + th + "\n");
			}
		}

		return result;
	}

	public static String decompile(String className, Map<String, byte[]> classes) throws Exception {
		return decompile(className, classes, Tool.PROCYON);
	}

	public static String decompile(String className, Map<String, byte[]> classes, Tool tool) throws Exception {
		String[] param;

		if (tool.equals(Tool.PROCYON)) {
			param = getProcyonDecompilationCommand(className, classes);
		} else {
			param = getJodeDecompilationCommand(className, classes);
		}

		String output = executeDecompilationProcess(param);

		if (output.equals("")) {
			output = "No code available";
		}

		return output;
	}

	private static String[] getJodeDecompilationCommand(String className, Map<String, byte[]> bytecodes)
			throws Exception {
		String[] param = null;

		className = className.trim();
		String fullClassName = className.trim().substring(0, className.lastIndexOf("."));

		File tempFolder = dumpClassFilesToTempFolder(bytecodes);

		String actualDirectory = new File(".").getAbsolutePath();
		actualDirectory = actualDirectory.substring(0, actualDirectory.lastIndexOf(File.separator));

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

		String classpath = actualDirectory.substring(0, actualDirectory.lastIndexOf(File.separator)) + File.separator
				+ "lib" + File.separator + "jode-1.1.2-javosize.jar";
		classpath = classpath + File.pathSeparator + tempFolder + File.pathSeparator
				+ System.getProperty("java.class.path");

		param = new String[] { javaBin, "-Xms64m", "-Xmx64m", "-cp", classpath, "jode.decompiler.Main", "--verify=off",
				"--inner=on", fullClassName };

		return param;
	}

	private static String[] getProcyonDecompilationCommand(String className, Map<String, byte[]> bytecodes)
			throws Exception {
		String[] param = null;

		className = className.trim();
		String fullClassName = className.trim().substring(0, className.lastIndexOf("."));
		fullClassName = fullClassName.replace(".", File.separator) + ".class";

		File tempFolder = dumpClassFilesToTempFolder(bytecodes);

		String actualDirectory = new File(".").getAbsolutePath();
		actualDirectory = actualDirectory.substring(0, actualDirectory.lastIndexOf(File.separator));

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String pathToDecompile = tempFolder + File.separator + fullClassName;

		param = new String[] { javaBin, "-Xms64m", "-Xmx64m", "-cp", System.getProperty("java.class.path"),
				"com.strobel.decompiler.DecompilerDriver", "--disable-foreach", "-ec", pathToDecompile };

		return param;
	}

	private static File dumpClassFilesToTempFolder(Map<String, byte[]> bytecodes) throws IOException {
		File tempFolder = Tools.getTemporaryFolder("javosize-decompilation-" + System.currentTimeMillis());
		tempFolder.deleteOnExit();

		for (Map.Entry<String, byte[]> clazz : bytecodes.entrySet()) {
			String cName = ((String) clazz.getKey()).trim();
			String tmpClassName = cName.substring(0, cName.lastIndexOf("."));
			String tempClass = tmpClassName.replace(".", File.separator);

			int index = tempClass.lastIndexOf(File.separator);
			String dirPath = index > 0 ? tempFolder.getCanonicalPath() + File.separator + tempClass.substring(0, index)
					: tempFolder.getCanonicalPath();
			String name = tempClass.substring(index + 1);

			File test = new File(dirPath);
			test.mkdirs();
			test.deleteOnExit();

			File file = new File(test, name + ".class");
			file.deleteOnExit();
			writeTempClassFile((byte[]) clazz.getValue(), file);
		}

		return tempFolder;
	}

	private static void writeTempClassFile(byte[] bytecodes, File tempFile) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(bytecodes);
		fos.close();
	}

	private static String executeDecompilationProcess(String[] param) throws Exception {
		StringWriter output = new StringWriter();
		StringWriter error = new StringWriter();
		String source = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			Process pid = runtime.exec(param);
			StreamRedirectThread outRedirect = new StreamRedirectThread("output_reader", pid.getInputStream(), output);
			StreamRedirectThread errorRedirect = new StreamRedirectThread("error_reader", pid.getErrorStream(), error);
			errorRedirect.start();
			outRedirect.start();
			pid.waitFor();
			outRedirect.join();
			errorRedirect.join();

			try {
				output.flush();
				output.close();
				error.flush();
				error.close();
			} catch (Throwable e) {
				log.trace("Problem closing buffer while decompiling class: " + e, e);
			}

			source = output.toString();
		} finally {
			try {
				output.flush();
				output.close();
				error.flush();
				error.close();
			} catch (Throwable e) {
				log.trace("Problem closing buffer while decompiling class: " + e, e);
			}
		}

		if (source.trim().length() == 0) {
			throw new Exception("Errors in decompilation: " + error.toString());
		}
		return source;
	}
}
