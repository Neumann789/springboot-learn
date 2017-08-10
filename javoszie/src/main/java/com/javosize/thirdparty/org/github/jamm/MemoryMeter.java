package com.javosize.thirdparty.org.github.jamm;

import java.lang.instrument.Instrumentation;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Callable;

public class MemoryMeter {
	private static final String outerClassReference = "this\\$[0-9]+";
	private static Instrumentation instrumentation;
	private final Callable<Set<Object>> trackerProvider;
	private final boolean includeFullBufferSize;
	private final Guess guess;
	private final boolean ignoreOuterClassReference;
	private final boolean ignoreKnownSingletons;
	private final boolean ignoreNonStrongReferences;

	public static void premain(String options, Instrumentation inst) {
		instrumentation = inst;
	}

	public static void agentmain(String options, Instrumentation inst) {
		instrumentation = inst;
	}

	public static boolean hasInstrumentation() {
		return instrumentation != null;
	}

	public static enum Guess {
		NEVER,

		FALLBACK_SPEC,

		FALLBACK_UNSAFE,

		FALLBACK_BEST,

		ALWAYS_SPEC,

		ALWAYS_UNSAFE;

		private Guess() {
		}
	}

	public MemoryMeter() {
		this(new CallableSet(), true, Guess.NEVER, false, false, false);
	}

	private MemoryMeter(Callable<Set<Object>> trackerProvider, boolean includeFullBufferSize, Guess guess,
			boolean ignoreOuterClassReference, boolean ignoreKnownSingletons, boolean ignoreNonStrongReferences) {
		this.trackerProvider = trackerProvider;
		this.includeFullBufferSize = includeFullBufferSize;
		this.guess = guess;
		this.ignoreOuterClassReference = ignoreOuterClassReference;
		this.ignoreKnownSingletons = ignoreKnownSingletons;
		this.ignoreNonStrongReferences = ignoreNonStrongReferences;
	}

	public MemoryMeter withTrackerProvider(Callable<Set<Object>> trackerProvider) {
		return new MemoryMeter(trackerProvider, this.includeFullBufferSize, this.guess, this.ignoreOuterClassReference,
				this.ignoreKnownSingletons, this.ignoreNonStrongReferences);
	}

	public MemoryMeter omitSharedBufferOverhead() {
		return new MemoryMeter(this.trackerProvider, false, this.guess, this.ignoreOuterClassReference,
				this.ignoreKnownSingletons, this.ignoreNonStrongReferences);
	}

	public MemoryMeter withGuessing(Guess guess) {
		return new MemoryMeter(this.trackerProvider, this.includeFullBufferSize, guess, this.ignoreOuterClassReference,
				this.ignoreKnownSingletons, this.ignoreNonStrongReferences);
	}

	public MemoryMeter ignoreOuterClassReference() {
		return new MemoryMeter(this.trackerProvider, this.includeFullBufferSize, this.guess, true,
				this.ignoreKnownSingletons, this.ignoreNonStrongReferences);
	}

	public MemoryMeter ignoreKnownSingletons() {
		return new MemoryMeter(this.trackerProvider, this.includeFullBufferSize, this.guess,
				this.ignoreOuterClassReference, true, this.ignoreNonStrongReferences);
	}

	public MemoryMeter ignoreNonStrongReferences() {
		return new MemoryMeter(this.trackerProvider, this.includeFullBufferSize, this.guess,
				this.ignoreOuterClassReference, this.ignoreKnownSingletons, true);
	}

	public long measure(Object object) {
		if (this.guess.equals(Guess.ALWAYS_UNSAFE))
			return MemoryLayoutSpecification.sizeOfWithUnsafe(object);
		if (this.guess.equals(Guess.ALWAYS_SPEC)) {
			return MemoryLayoutSpecification.sizeOf(object);
		}
		if (instrumentation == null) {
			if (this.guess.equals(Guess.NEVER))
				throw new IllegalStateException("Instrumentation is not set; Jamm must be set as -javaagent");
			if (this.guess.equals(Guess.FALLBACK_UNSAFE)) {
				if (!MemoryLayoutSpecification.hasUnsafe()) {
					throw new IllegalStateException(
							"Instrumentation is not set and sun.misc.Unsafe could not be obtained; Jamm must be set as -javaagent, or the SecurityManager must permit access to sun.misc.Unsafe");
				}
			} else if (this.guess.equals(Guess.FALLBACK_BEST)) {
				if (MemoryLayoutSpecification.hasUnsafe()) {
					return MemoryLayoutSpecification.sizeOfWithUnsafe(object);
				}
			} else if (this.guess.equals(Guess.FALLBACK_SPEC)) {
				return MemoryLayoutSpecification.sizeOf(object);
			}
		}
		return instrumentation.getObjectSize(object);
	}

	public long measureDeep(Object object) {
		MemoryMeterListener listener = NoopMemoryMeterListener.FACTORY.newInstance();
		return measureDeep(object, listener);
	}

	public String expandBranch(Object object, long minSize) {
		return expandBranch(object, new TreePrinter(500), minSize);
	}

	public String expandBranch(Object object, TreePrinter listener, long minSize) {
		measureDeep(object, listener);
		TreePrinter.ObjectInfo nodeRoot = (TreePrinter.ObjectInfo) listener.mapping.get(listener.root);
		long rootSize = nodeRoot.totalSize();
		if (rootSize < minSize) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		return expandRelevantNode(nodeRoot, rootSize, sb, minSize);
	}

	public long measureDeep(Object object, MemoryMeterListener listener) {
		if (object == null) {
			throw new NullPointerException();
		}

		if (ignoreClass(object.getClass())) {
			return 0L;
		}
		Set<Object> tracker;
		try {
			tracker = (Set) this.trackerProvider.call();
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
		tracker.add(object);
		listener.started(object);

		Deque<Object> stack = new java.util.ArrayDeque();
		stack.push(object);

		long total = 0L;
		while (!stack.isEmpty()) {
			Object current = stack.pop();
			assert (current != null);
			long size = measure(current);
			listener.objectMeasured(current, size);
			total += size;

			if ((current instanceof Object[])) {
				addArrayChildren((Object[]) current, stack, tracker, listener);
			} else if (((current instanceof ByteBuffer)) && (!this.includeFullBufferSize)) {
				total += ((ByteBuffer) current).remaining();
			} else {
				Object referent = (this.ignoreNonStrongReferences) && ((current instanceof Reference))
						? ((Reference) current).get() : null;
				addFieldChildren(current, stack, tracker, referent, listener);
			}
		}

		listener.done(total);
		return total;
	}

	private String expandRelevantNode(TreePrinter.ObjectInfo node, long rootSize, StringBuilder sb, long minSize) {
		long maxSize = 0L;
		TreePrinter.ObjectInfo maxObject = null;

		for (int i = 0; i < node.depth; i++) {
			sb.append(" ");
		}
		sb.append(node.name + "[" + node.className + "] size: " + node.totalSize() + "\n");

		for (TreePrinter.ObjectInfo currentNode : node.children) {
			if (currentNode.totalSize() > maxSize) {
				maxSize = currentNode.totalSize();
				maxObject = currentNode;
			}
		}

		if (maxSize > minSize) {
			return expandRelevantNode(maxObject, rootSize, sb, minSize);
		}
		return sb.toString();
	}

	public long measureStaticElementsOfClass(Class clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}

		if (ignoreClass(clazz)) {
			return 0L;
		}
		Set<Object> tracker;
		try {
			tracker = (Set) this.trackerProvider.call();
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
		MemoryMeterListener listener = NoopMemoryMeterListener.FACTORY.newInstance();
		listener.started(clazz);

		Deque<Object> stack = new java.util.ArrayDeque();

		for (Field field : clazz.getDeclaredFields()) {
			if ((!field.getType().isPrimitive()) && (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
					&& (!field.isAnnotationPresent(Unmetered.class))) {

				if ((!this.ignoreOuterClassReference) || (!field.getName().matches("this\\$[0-9]+"))) {

					if (!ignoreClass(field.getType())) {

						field.setAccessible(true);
						Object child;
						try {
							child = field.get(null);
						} catch (IllegalAccessException e) {
							
							throw new RuntimeException(e);
						}
						if ((child != null) && (!tracker.contains(child))) {
							stack.push(child);
							tracker.add(child);
							listener.fieldAdded(clazz, field.getName(), child);
						}
					}
				}
			}
		}
		long total = 0L;
		while (!stack.isEmpty()) {
			Object current = stack.pop();
			assert (current != null);
			long size = measure(current);
			listener.objectMeasured(current, size);
			total += size;

			if ((current instanceof Object[])) {
				addArrayChildren((Object[]) current, stack, tracker, listener);
			} else if (((current instanceof ByteBuffer)) && (!this.includeFullBufferSize)) {
				total += ((ByteBuffer) current).remaining();
			} else {
				Object referent = (this.ignoreNonStrongReferences) && ((current instanceof Reference))
						? ((Reference) current).get() : null;
				addFieldChildren(current, stack, tracker, referent, listener);
			}
		}

		listener.done(total);
		return total;
	}

	public long countChildren(Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		MemoryMeterListener listener = NoopMemoryMeterListener.FACTORY.newInstance();
		Set<Object> tracker = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap());
		tracker.add(object);
		listener.started(object);
		Deque<Object> stack = new java.util.ArrayDeque();
		stack.push(object);

		long total = 0L;
		while (!stack.isEmpty()) {
			Object current = stack.pop();
			assert (current != null);
			total += 1L;
			listener.objectCounted(current);

			if ((current instanceof Object[])) {
				addArrayChildren((Object[]) current, stack, tracker, listener);
			} else {
				Object referent = (this.ignoreNonStrongReferences) && ((current instanceof Reference))
						? ((Reference) current).get() : null;
				addFieldChildren(current, stack, tracker, referent, listener);
			}
		}

		listener.done(total);
		return total;
	}

	private void addFieldChildren(Object current, Deque<Object> stack, Set<Object> tracker, Object ignorableChild,
			MemoryMeterListener listener) {
		Class<?> cls = current.getClass();
		while (cls != null) {
			for (Field field : cls.getDeclaredFields()) {
				if ((!field.getType().isPrimitive()) && (!java.lang.reflect.Modifier.isStatic(field.getModifiers()))
						&& (!field.isAnnotationPresent(Unmetered.class))) {

					if ((!this.ignoreOuterClassReference) || (!field.getName().matches("this\\$[0-9]+"))) {

						if (!ignoreClass(field.getType())) {

							field.setAccessible(true);
							Object child;
							try {
								child = field.get(current);
							} catch (IllegalAccessException e) {
								
								throw new RuntimeException(e);
							}
							if ((child != ignorableChild) && (child != null) && (!tracker.contains(child))) {
								stack.push(child);
								tracker.add(child);
								listener.fieldAdded(current, field.getName(), child);
							}
						}
					}
				}
			}
			cls = cls.getSuperclass();
		}
	}

	private boolean ignoreClass(Class<?> cls) {
		return ((this.ignoreKnownSingletons) && ((cls.equals(Class.class)) || (Enum.class.isAssignableFrom(cls))))
				|| (isAnnotationPresent(cls));
	}

	private boolean isAnnotationPresent(Class<?> cls) {
		if (cls == null) {
			return false;
		}
		if (cls.isAnnotationPresent(Unmetered.class)) {
			return true;
		}
		Class<?>[] interfaces = cls.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (isAnnotationPresent(cls.getInterfaces()[i])) {
				return true;
			}
		}
		return isAnnotationPresent(cls.getSuperclass());
	}

	private void addArrayChildren(Object[] current, Deque<Object> stack, Set<Object> tracker,
			MemoryMeterListener listener) {
		for (int i = 0; i < current.length; i++) {
			Object child = current[i];
			if ((child != null) && (!tracker.contains(child))) {
				Class<?> childCls = child.getClass();
				if (!ignoreClass(childCls)) {

					stack.push(child);
					tracker.add(child);
					listener.fieldAdded(current, Integer.toString(i), child);
				}
			}
		}
	}
}
