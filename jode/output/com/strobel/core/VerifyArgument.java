/* VerifyArgument - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class VerifyArgument
{
    private VerifyArgument() {
	/* empty */
    }
    
    public static Object notNull(Object value, String parameterName) {
	if (value == null)
	    throw new IllegalArgumentException
		      (String.format("Argument '%s' cannot be null.",
				     new Object[] { parameterName }));
	return value;
    }
    
    public static Object[] notEmpty(Object[] array, String parameterName) {
	notNull(array, parameterName);
	if (array.length != 0)
	    return array;
	throw new IllegalArgumentException
		  (String.format
		   ("Argument '%s' must be a non-empty collection.",
		    new Object[] { parameterName }));
    }
    
    public static Iterable notEmpty(Iterable collection,
				    String parameterName) {
    label_1414:
	{
	    notNull(collection, parameterName);
	    if (!(collection instanceof Collection)) {
		Iterator iterator = collection.iterator();
		if (iterator.hasNext())
		    return collection;
	    } else if (!((Collection) collection).isEmpty())
		return collection;
	    break label_1414;
	}
	throw new IllegalArgumentException
		  (String.format
		   ("Argument '%s' must be a non-empty collection.",
		    new Object[] { parameterName }));
    }
    
    public static Object[] noNullElements(Object[] array,
					  String parameterName) {
	notNull(array, parameterName);
	Object[] arr$ = array;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return array;
	    Object item = arr$[i$];
	    if (item != null)
		i$++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not have any null elements.",
			new Object[] { parameterName }));
	}
    }
    
    public static Object[] noNullElements(Object[] array, int offset,
					  int length, String parameterName) {
	notNull(array, parameterName);
	int i = offset;
	int end = offset + length;
	for (;;) {
	    if (i >= end)
		return array;
	    Object item = array[i];
	    if (item != null)
		i++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not have any null elements in the range (%s, %s].",
			new Object[] { parameterName, Integer.valueOf(offset),
				       Integer.valueOf(offset + length) }));
	}
    }
    
    public static Iterable noNullElements(Iterable collection,
					  String parameterName) {
	notNull(collection, parameterName);
	if (!(collection instanceof List)
	    || !(collection instanceof RandomAccess)) {
	    Iterator i$ = collection.iterator();
	    for (;;) {
		if (!i$.hasNext())
		    return collection;
		Object item = i$.next();
		IF (item != null)
		    /* empty */
		throw new IllegalArgumentException
			  (String.format
			   ("Argument '%s' must not have any null elements.",
			    new Object[] { parameterName }));
	    }
	}
	List list = (List) collection;
	int i = 0;
	int n = list.size();
	for (;;) {
	    if (i >= n)
		return collection;
	    if (list.get(i) != null)
		i++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not have any null elements.",
			new Object[] { parameterName }));
	}
    }
    
    public static Object[] noNullElementsAndNotEmpty(Object[] array,
						     String parameterName) {
	notEmpty(array, parameterName);
	Object[] arr$ = array;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return array;
	    Object item = arr$[i$];
	    if (item != null)
		i$++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not have any null elements.",
			new Object[] { parameterName }));
	}
    }
    
    public static Object[] noNullElementsAndNotEmpty(Object[] array,
						     int offset, int length,
						     String parameterName) {
	notEmpty(array, parameterName);
	int i = offset;
	int end = offset + length;
	for (;;) {
	    if (i >= end)
		return array;
	    Object item = array[i];
	    if (item != null)
		i++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not have any null elements in the range (%s, %s].",
			new Object[] { parameterName, Integer.valueOf(offset),
				       Integer.valueOf(offset + length) }));
	}
    }
    
    public static Iterable noNullElementsAndNotEmpty(Iterable collection,
						     String parameterName) {
	notNull(collection, parameterName);
	if (!(collection instanceof List)
	    || !(collection instanceof RandomAccess)) {
	    Iterator iterator = collection.iterator();
	    if (iterator.hasNext()) {
		for (;;) {
		    Object item = iterator.next();
		    if (item != null) {
			if (!iterator.hasNext())
			    return collection;
		    }
		    throw new IllegalArgumentException
			      (String.format
			       ("Argument '%s' must not have any null elements.",
				new Object[] { parameterName }));
		}
	    }
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be a non-empty collection.",
			new Object[] { parameterName }));
	}
	List list = (List) collection;
	if (!list.isEmpty()) {
	    int i = 0;
	    int n = list.size();
	    for (;;) {
		if (i >= n)
		    return collection;
		if (list.get(i) != null)
		    i++;
		throw new IllegalArgumentException
			  (String.format
			   ("Argument '%s' must not have any null elements.",
			    new Object[] { parameterName }));
	    }
	}
	throw new IllegalArgumentException
		  (String.format
		   ("Argument '%s' must be a non-empty collection.",
		    new Object[] { parameterName }));
    }
    
    public static Object[] elementsOfType(Class elementType, Object[] values,
					  String parameterName) {
	notNull(elementType, "elementType");
	notNull(values, "values");
	Object[] arr$ = values;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return values;
	    Object value = arr$[i$];
	    if (elementType.isInstance(value))
		i$++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must only contain elements of type '%s'.",
			new Object[] { parameterName, elementType }));
	}
    }
    
    public static Object[] elementsOfTypeOrNull(Class elementType,
						Object[] values,
						String parameterName) {
	notNull(elementType, "elementType");
	notNull(values, "values");
	Object[] arr$ = values;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return values;
	    Object value = arr$[i$];
	    if (value == null || elementType.isInstance(value))
		i$++;
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must only contain elements of type '%s'.",
			new Object[] { parameterName, elementType }));
	}
    }
    
    public static int validElementRange(int size, int startInclusive,
					int endExclusive) {
	if (startInclusive < 0 || endExclusive > size
	    || endExclusive < startInclusive)
	    throw new IllegalArgumentException
		      (String.format
		       ("The specified element range is not valid: range=(%d, %d], length=%d",
			new Object[] { Integer.valueOf(startInclusive),
				       Integer.valueOf(endExclusive),
				       Integer.valueOf(size) }));
	return endExclusive - startInclusive;
    }
    
    public static String notNullOrEmpty(String value, String parameterName) {
	if (StringUtilities.isNullOrEmpty(value))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be a non-null, non-empty string.",
			new Object[] { parameterName }));
	return value;
    }
    
    public static String notNullOrWhitespace(String value,
					     String parameterName) {
	if (StringUtilities.isNullOrWhitespace(value))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be a non-null, non-empty string.",
			new Object[] { parameterName }));
	return value;
    }
    
    public static int isNonZero(int value, String parameterName) {
	if (value == 0)
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be non-zero, but value was: %d.",
			new Object[] { parameterName,
				       Integer.valueOf(value) }));
	return value;
    }
    
    public static int isPositive(int value, String parameterName) {
	if (value <= 0)
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be positive, but value was: %d.",
			new Object[] { parameterName,
				       Integer.valueOf(value) }));
	return value;
    }
    
    public static int isNonNegative(int value, String parameterName) {
	if (value < 0)
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be non-negative, but value was: %d.",
			new Object[] { parameterName,
				       Integer.valueOf(value) }));
	return value;
    }
    
    public static int isNegative(int value, String parameterName) {
	if (value >= 0)
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be negative, but value was: %d.",
			new Object[] { parameterName,
				       Integer.valueOf(value) }));
	return value;
    }
    
    public static int inRange(int minInclusive, int maxInclusive, int value,
			      String parameterName) {
	if (maxInclusive >= minInclusive) {
	    if (value < minInclusive || value > maxInclusive)
		throw new IllegalArgumentException
			  (String.format
			   ("Argument '%s' must be in the range [%s, %s], but value was: %d.",
			    new Object[] { parameterName,
					   Integer.valueOf(minInclusive),
					   Integer.valueOf(maxInclusive),
					   Integer.valueOf(value) }));
	    return value;
	}
	throw new IllegalArgumentException
		  (String.format
		   ("The specified maximum value (%d) is less than the specified minimum value (%d).",
		    new Object[] { Integer.valueOf(maxInclusive),
				   Integer.valueOf(minInclusive) }));
    }
    
    public static double isNonZero(double value, String parameterName) {
	if (value == 0.0)
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be non-zero, but value was: %s.",
			new Object[] { parameterName,
				       Double.valueOf(value) }));
	return value;
    }
    
    public static double isPositive(double value, String parameterName) {
	if (!(value > 0.0))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be positive, but value was: %s.",
			new Object[] { parameterName,
				       Double.valueOf(value) }));
	return value;
    }
    
    public static double isNonNegative(double value, String parameterName) {
	if (!(value >= 0.0))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be non-negative, but value was: %s.",
			new Object[] { parameterName,
				       Double.valueOf(value) }));
	return value;
    }
    
    public static double isNegative(double value, String parameterName) {
	if (!(value < 0.0))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be negative, but value was: %s.",
			new Object[] { parameterName,
				       Double.valueOf(value) }));
	return value;
    }
    
    public static double inRange(double minInclusive, double maxInclusive,
				 double value, String parameterName) {
	if (!Double.isNaN(minInclusive)) {
	    if (!Double.isNaN(maxInclusive)) {
		if (!(maxInclusive < minInclusive)) {
		    if (!(value >= minInclusive) || !(value <= maxInclusive))
			throw new IllegalArgumentException
				  (String.format
				   ("Argument '%s' must be in the range [%s, %s], but value was: %s.",
				    (new Object[]
				     { parameterName,
				       Double.valueOf(minInclusive),
				       Double.valueOf(maxInclusive),
				       Double.valueOf(value) })));
		    return value;
		}
		throw new IllegalArgumentException
			  (String.format
			   ("The specified maximum value (%s) is less than the specified minimum value (%s).",
			    new Object[] { Double.valueOf(maxInclusive),
					   Double.valueOf(minInclusive) }));
	    }
	    throw new IllegalArgumentException
		      ("The maximum value cannot be NaN.");
	}
	throw new IllegalArgumentException("The minimum value cannot be NaN.");
    }
    
    public static Object instanceOf(Class type, Object value,
				    String parameterName) {
	Class actualType = getBoxedType((Class) notNull(type, "type"));
	if (!actualType.isInstance(value))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must be an instance of type %s.",
			new Object[] { parameterName,
				       type.getCanonicalName() }));
	return value;
    }
    
    public static Object notInstanceOf(Class type, Object value,
				       String parameterName) {
	Class actualType = getBoxedType((Class) notNull(type, "type"));
	if (actualType.isInstance(value))
	    throw new IllegalArgumentException
		      (String.format
		       ("Argument '%s' must not be an instance of type %s.",
			new Object[] { parameterName,
				       type.getCanonicalName() }));
	return value;
    }
    
    private static Class getBoxedType(Class type) {
	if (type.isPrimitive()) {
	    if (type != Boolean.TYPE) {
		if (type != Character.TYPE) {
		    if (type != Byte.TYPE) {
			if (type != Short.TYPE) {
			    if (type != Integer.TYPE) {
				if (type != Long.TYPE) {
				    if (type != Float.TYPE) {
					if (type != Double.TYPE)
					    return type;
					return Double.class;
				    }
				    return Float.class;
				}
				return Long.class;
			    }
			    return Integer.class;
			}
			return Short.class;
		    }
		    return Byte.class;
		}
		return Character.class;
	    }
	    return Boolean.class;
	}
	return type;
    }
}
