package com.strobel.assembler.metadata;

import java.util.HashMap;
import java.util.Map;

public enum CompilerTarget {
	JDK1_1("1.1", 45, 3), JDK1_2("1.2", 46, 0), JDK1_3("1.3", 47, 0),

	JDK1_4("1.4", 48, 0),

	JDK1_5("1.5", 49, 0),

	JDK1_6("1.6", 50, 0),

	JDK1_7("1.7", 51, 0),

	JDK1_8("1.8", 52, 0);

	private static final CompilerTarget[] VALUES;
	private static final CompilerTarget MIN;
	private static final CompilerTarget MAX;
	
	private static final Map<String, CompilerTarget> tab;
	public final String name;
	public final int majorVersion;
	public final int minorVersion;

	public static CompilerTarget MIN() {
		return MIN;
	}

	public static CompilerTarget MAX() {
		return MAX;
	}

	static {
		VALUES = values();
		MIN = VALUES[0];
		MAX = VALUES[(VALUES.length - 1)];

		tab = new HashMap();

		for (CompilerTarget t : values()) {
			tab.put(t.name, t);
		}
		tab.put("5", JDK1_5);
		tab.put("6", JDK1_6);
		tab.put("7", JDK1_7);
		tab.put("8", JDK1_8);
	}



	private CompilerTarget(String name, int majorVersion, int minorVersion) {
		this.name = name;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	public static final CompilerTarget DEFAULT = JDK1_8;

	public static CompilerTarget lookup(String name) {
		return (CompilerTarget) tab.get(name);
	}

	public static CompilerTarget lookup(int majorVersion, int minorVersion) {
		for (CompilerTarget target : VALUES) {
			if (majorVersion < target.majorVersion) {
				return target;
			}
			if ((minorVersion <= target.minorVersion) && (majorVersion == target.majorVersion)) {
				return target;
			}
		}
		return MAX;
	}

	public boolean requiresIProxy() {
		return compareTo(JDK1_1) <= 0;
	}

	public boolean initializeFieldsBeforeSuper() {
		return compareTo(JDK1_4) >= 0;
	}

	public boolean obeyBinaryCompatibility() {
		return compareTo(JDK1_2) >= 0;
	}

	public boolean arrayBinaryCompatibility() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean interfaceFieldsBinaryCompatibility() {
		return compareTo(JDK1_2) > 0;
	}

	public boolean interfaceObjectOverridesBinaryCompatibility() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean usePrivateSyntheticFields() {
		return compareTo(JDK1_5) < 0;
	}

	public boolean useInnerCacheClass() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean generateCLDCStackMap() {
		return false;
	}

	public boolean generateStackMapTable() {
		return compareTo(JDK1_6) >= 0;
	}

	public boolean isPackageInfoSynthetic() {
		return compareTo(JDK1_6) >= 0;
	}

	public boolean generateEmptyAfterBig() {
		return false;
	}

	public boolean useStringBuilder() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean useSyntheticFlag() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean useEnumFlag() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean useAnnotationFlag() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean useVarargsFlag() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean useBridgeFlag() {
		return compareTo(JDK1_5) >= 0;
	}

	public char syntheticNameChar() {
		return '$';
	}

	public boolean hasClassLiterals() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean hasInvokedynamic() {
		return compareTo(JDK1_7) >= 0;
	}

	public boolean hasMethodHandles() {
		return hasInvokedynamic();
	}

	public boolean classLiteralsNoInit() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean hasInitCause() {
		return compareTo(JDK1_4) >= 0;
	}

	public boolean boxWithConstructors() {
		return compareTo(JDK1_5) < 0;
	}

	public boolean hasIterable() {
		return compareTo(JDK1_5) >= 0;
	}

	public boolean hasEnclosingMethodAttribute() {
		return compareTo(JDK1_5) >= 0;
	}
}
