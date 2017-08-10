package com.javosize.compiler;

import com.javosize.agent.Agent;
import com.javosize.agent.Tools;
import com.javosize.log.Log;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HotSwapper {
	private static Log log = new Log(HotSwapper.class.getName());

	public static void redefineClasses(ClassDefinition... definition)
			throws ClassNotFoundException, UnmodifiableClassException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < definition.length; i++) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(definition[i].getDefinitionClass().getName());
		}

		log.debug("Trying to redefine class" + (definition.length > 1 ? "es " : " ") + sb);
		if (Agent.getInstrumentation().isRedefineClassesSupported()) {
			Agent.getInstrumentation().redefineClasses(definition);
		} else {
			throw new UnmodifiableClassException("Running application does not support class redefinition");
		}
	}

	public static void hotSwappClassFromJavaCode(String className, String javaCode) throws Throwable {
		Class origClass = getClassForHotSwap(className);

		String targetVersion = Tools.getVersionOfCompilation(origClass);

		Map<String, byte[]> result = InMemoryJavaCompiler.compileAndReturnBytecode(className, javaCode,
				origClass.getClassLoader(), null, targetVersion);
		if ((result == null) || (result.size() == 0)) {
			log.debug("Compilation failed for " + className + ". Null value received from compiler. ");
			throw new Exception("Compilation failed for " + className + ". Null value received from compiler. ");
		}
		log.debug("Class " + className + " compiled properly. Trying to replace the bytecode in memory.");
		ClassDefinition[] classesToRedefine = new ClassDefinition[result.size()];
		try {
			int i = 0;
			for (Iterator localIterator = result.entrySet().iterator(); localIterator.hasNext();) {
				Map.Entry entry = (Map.Entry) localIterator.next();
				classesToRedefine[(i++)] = new ClassDefinition(getClassForHotSwap((String) entry.getKey()),
						(byte[]) entry.getValue());
			}
			redefineClasses(classesToRedefine);
		} catch (Throwable th) {
			Map.Entry<String, byte[]> entry;
			if ((targetVersion.equals("1.8")) && ((th instanceof UnsupportedOperationException))) {
				log.warn(
						"First attempt of redefition has failed. Trying again refactoring lambda expressions names. Detail: "
								+ th);
				int i = 0;
				for (Map.Entry<String, byte[]> e : result.entrySet()) {
					byte[] newVersion = MethodNameModifier.refactorLambdaExpressions((byte[]) e.getValue());
					classesToRedefine[(i++)] = new ClassDefinition(getClassForHotSwap((String) e.getKey()),
							newVersion);
				}
				redefineClasses(classesToRedefine);
			} else {
				throw th;
			}
		}
	}

	public static void hotSwapMultipleClasses(String[]... classInfo) throws Throwable {
		List<ClassDefinition> classesToRedefineArray = new ArrayList();
		for (int i = 0; i < classInfo.length; i++) {
			String className = classInfo[i][0];
			String javaCode = classInfo[i][1];
			Class origClass = getClassForHotSwap(className);

			String targetVersion = Tools.getVersionOfCompilation(origClass);

			Map<String, byte[]> result = InMemoryJavaCompiler.compileAndReturnBytecode(className, javaCode,
					origClass.getClassLoader(), null, targetVersion);
			if ((result == null) || (result.size() == 0)) {
				log.debug("Compilation failed for " + className + ". Null value received from compiler. ");
				throw new Exception("Compilation failed for " + className + ". Null value received from compiler. ");
			}
			log.debug("Class " + className + " compiled properly. Added bytecode to the list of classes to replace.");
			for (Map.Entry<String, byte[]> entry : result.entrySet()) {
				classesToRedefineArray.add(
						new ClassDefinition(getClassForHotSwap((String) entry.getKey()), (byte[]) entry.getValue()));
			}
		}

		redefineClasses((ClassDefinition[]) classesToRedefineArray.toArray());
	}

	public static boolean validateJavaCode(String className, String javaCode) throws Throwable {
		Class origClass = getClassForHotSwap(className);

		String targetVersion = Tools.getVersionOfCompilation(origClass);

		Map<String, byte[]> result = InMemoryJavaCompiler.compileAndReturnBytecode(className, javaCode,
				origClass.getClassLoader(), null, targetVersion);
		if ((result == null) || (result.size() == 0)) {
			log.debug("Compilation failed for " + className + ". Null value received from compiler. ");
			return false;
		}
		log.debug("Class " + className + " compiled properly.");
		return true;
	}

	public static void hotSwappClassFromByteCode(String className, byte[] byteCode) throws Exception {
		log.debug("Trying to redefine class " + className + " from new bytecode.");
		Class origClass = getClassForHotSwap(className);
		ClassDefinition cd = new ClassDefinition(origClass, byteCode);

		Class newClass = cd.getDefinitionClass();
		Class[] nestedClasses = newClass.getDeclaredClasses();
		if (nestedClasses.length > 0) {
			ClassDefinition[] classes = new ClassDefinition[nestedClasses.length + 1];
			classes[0] = cd;
			for (int i = 0; i < nestedClasses.length; i++) {
				String nestedClassName = nestedClasses[i].getName().replace('.', '/');
				byte[] nestedBytecode = Tools.getClassBytes(nestedClassName);
				if (nestedBytecode == null) {
					throw new Exception("Nested class " + nestedClassName
							+ " nof found!! That class is required for compiling class " + className);
				}
				classes[i] = new ClassDefinition(nestedClasses[i], nestedBytecode);
			}

			log.debug("Trying to replace the bytecode of class " + className
					+ " in memory [Including its nested classes].");
			redefineClasses(classes);
		} else {
			log.debug("Trying to replace the bytecode of class " + className + " in memory.");
			redefineClasses(new ClassDefinition[] { cd });
		}
	}

	public static void hotSwapMultipleClassesFromByteCode(String[] classNames, byte[]... byteCode) throws Exception {
		StringBuffer sb = new StringBuffer();
		ClassDefinition[] classesToRedefine = new ClassDefinition[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(classNames[i]);

			String className = classNames[i];
			Class origClass = getClassForHotSwap(className);
			ClassDefinition cd = new ClassDefinition(origClass, byteCode[i]);
			classesToRedefine[i] = cd;
		}

		log.debug("Trying to redefine class" + (classNames.length > 1 ? "es " : " ") + sb + " from new bytecode.");
		redefineClasses(classesToRedefine);
	}

	private static Class getClassForHotSwap(String className) throws Exception {
		Class origClass = Tools.getClassFromInstrumentation(className);
		if (origClass == null) {
			log.warn("Class " + className + " not found from while hotSwapping code");
			throw new Exception("Class " + className + " not found from while hotSwapping code");
		}

		return origClass;
	}
}
