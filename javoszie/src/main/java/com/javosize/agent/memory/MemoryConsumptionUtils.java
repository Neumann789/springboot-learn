package com.javosize.agent.memory;

import com.javosize.agent.Agent;
import com.javosize.agent.Tools;
import com.javosize.classutils.ClassNameFilter;
import com.javosize.log.Log;
import com.javosize.thirdparty.org.github.jamm.Unmetered;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryConsumptionUtils {
	private static Log log = new Log(MemoryConsumptionUtils.class.getName());

	public static String findLeakInClass(String name, double factor) throws Exception {
		Class c = Tools.getClassFromInstrumentation(name);
		if (c == null) {
			String error = "Class " + name + " not found. ";
			log.warn(error);
			throw new Exception(error);
		}
		StringBuilder sb = new StringBuilder();
		try {
			long size = Agent.getClassStaticElementsTotalSize(c);
			for (Field field : c.getDeclaredFields()) {
				if ((!field.getType().isPrimitive()) && (Modifier.isStatic(field.getModifiers()))) {

					field.setAccessible(true);
					try {
						Object child = field.get(null);
						if (child != null) {

							String result = Agent.expandMemoryBranch(child, (long) (size * factor));
							if (result != null) {
								sb.append("BranchFound: " + name + "\n");
								sb.append(result.replace("root[", field.getName() + "["));
							}
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (Throwable e) {
			String error = "Problem loading class for obtaining the size [Class=" + name + "]. Error: " + e;
			log.error(error, e);
			throw new Exception(error, e);
		}
		return sb.toString();
	}

	public static List<StaticVariableSize> getClassStaticElementsSize(Class clazz) {
		List<StaticVariableSize> ret = new ArrayList();

		try {
			for (Field field : clazz.getDeclaredFields())
				if ((!field.getType().isPrimitive()) && (Modifier.isStatic(field.getModifiers()))
						&& (!field.isAnnotationPresent(Unmetered.class))) {

					field.setAccessible(true);
					Object child;
					try {
						child = field.get(null);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					try {

						if (child != null) {

							StaticVariableSize var = new StaticVariableSize(clazz.getName(), field.getName(),
									field.getType().getName(), Agent.getObjectDeepSize(child));

							ret.add(var);
						}
					} catch (Throwable th) {
						log.trace("Error obtaining size of variable: " + th, th);
					}
				}
		} catch (Throwable th) {
			log.debug("Unexpected error obtaining variables size: " + th, th);
		}

		return ret;
	}

	public static Long getClassSize(String name) throws Exception {
		Class c = Tools.getClassFromInstrumentation(name);
		if (c == null) {
			String error = "Class " + name + " not found. ";
			log.warn(error);
			throw new Exception(error);
		}
		try {
			log.debug("Class " + name + " found. Trying to obtain size.");
			return Long.valueOf(Agent.getClassStaticElementsTotalSize(c));
		} catch (Throwable e) {
			String error = "Problem loading class for obtaining the size [Class=" + name + "]. Error: " + e;
			log.error(error, e);
			throw new Exception(error);
		}
	}

	public static List<StaticVariableSize> getTopStaticVariablesSize() {
		return getTopStaticVariablesSize(10, null);
	}

	public static List<StaticVariableSize> getTopStaticVariablesSize(int n, String regexPatternClassesToInclude) {
		TopMemoryConsumingVariablesList topList = new TopMemoryConsumingVariablesList(n);

		Instrumentation instrumentation = Agent.getInstrumentation();
		Class[] classes = instrumentation.getAllLoadedClasses();

		for (int i = 0; i < classes.length; i++) {
			String className = classes[i].getName();
			if ((!classes[i].isSynthetic())
					&& (ClassNameFilter.validateClassName(className, true, regexPatternClassesToInclude))) {
				topList.addAll(getClassStaticElementsSize(classes[i]));
			}
		}

		List<StaticVariableSize> list = topList.getTopList();
		Collections.sort(list);
		Collections.reverse(list);

		return list;
	}
}
