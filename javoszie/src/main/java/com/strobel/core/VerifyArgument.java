 package com.strobel.core;
 
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.RandomAccess;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class VerifyArgument
 {
   public static <T> T notNull(T value, String parameterName)
   {
     if (value != null) {
       return value;
     }
     throw new IllegalArgumentException(String.format("Argument '%s' cannot be null.", new Object[] { parameterName }));
   }
   
 
 
 
 
 
   public static <T> T[] notEmpty(T[] array, String parameterName)
   {
     notNull(array, parameterName);
     
     if (array.length == 0) {
       throw new IllegalArgumentException(String.format("Argument '%s' must be a non-empty collection.", new Object[] { parameterName }));
     }
     
 
 
     return array;
   }
   
   public static <T extends Iterable<?>> T notEmpty(T collection, String parameterName) {
     notNull(collection, parameterName);
     
     if ((collection instanceof Collection)) {
       if (!((Collection)collection).isEmpty()) {
         return collection;
       }
     }
     else {
       Iterator<?> iterator = collection.iterator();
       if (iterator.hasNext()) {
         return collection;
       }
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be a non-empty collection.", new Object[] { parameterName }));
   }
   
 
   public static <T> T[] noNullElements(T[] array, String parameterName)
   {
     notNull(array, parameterName);
     
     for (T item : array) {
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
       }
     }
     
 
 
     return array;
   }
   
   public static <T> T[] noNullElements(T[] array, int offset, int length, String parameterName) {
     notNull(array, parameterName);
     
     int i = offset; for (int end = offset + length; i < end; i++) {
       T item = array[i];
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements in the range (%s, %s].", new Object[] { parameterName, Integer.valueOf(offset), Integer.valueOf(offset + length) }));
       }
     }
     
 
 
 
 
 
 
 
     return array;
   }
   
   public static <T extends Iterable<?>> T noNullElements(T collection, String parameterName) {
     notNull(collection, parameterName);
     
     if (((collection instanceof List)) && ((collection instanceof RandomAccess))) {
       List<?> list = (List)collection;
       
       int i = 0; for (int n = list.size(); i < n; i++) {
         if (list.get(i) == null) {
           throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
         }
       }
       
 
 
       return collection;
     }
     
     for (Object item : collection) {
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
       }
     }
     
 
 
     return collection;
   }
   
   public static <T> T[] noNullElementsAndNotEmpty(T[] array, String parameterName) {
     notEmpty(array, parameterName);
     
     for (T item : array) {
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
       }
     }
     
 
 
     return array;
   }
   
   public static <T> T[] noNullElementsAndNotEmpty(T[] array, int offset, int length, String parameterName) {
     notEmpty(array, parameterName);
     
     int i = offset; for (int end = offset + length; i < end; i++) {
       T item = array[i];
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements in the range (%s, %s].", new Object[] { parameterName, Integer.valueOf(offset), Integer.valueOf(offset + length) }));
       }
     }
     
 
 
 
 
 
 
 
     return array;
   }
   
   public static <T extends Iterable<?>> T noNullElementsAndNotEmpty(T collection, String parameterName) {
     notNull(collection, parameterName);
     
     if (((collection instanceof List)) && ((collection instanceof RandomAccess))) {
       List<?> list = (List)collection;
       
       if (list.isEmpty()) {
         throw new IllegalArgumentException(String.format("Argument '%s' must be a non-empty collection.", new Object[] { parameterName }));
       }
       
 
 
 
       int i = 0; for (int n = list.size(); i < n; i++) {
         if (list.get(i) == null) {
           throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
         }
       }
       
 
 
       return collection;
     }
     
     Iterator iterator = collection.iterator();
     
     if (!iterator.hasNext()) {
       throw new IllegalArgumentException(String.format("Argument '%s' must be a non-empty collection.", new Object[] { parameterName }));
     }
     
 
     do
     {
       Object item = iterator.next();
       
       if (item == null) {
         throw new IllegalArgumentException(String.format("Argument '%s' must not have any null elements.", new Object[] { parameterName }));
 
       }
       
     }
     while (iterator.hasNext());
     
     return collection;
   }
   
   public static <T> T[] elementsOfType(Class<?> elementType, T[] values, String parameterName) {
     notNull(elementType, "elementType");
     notNull(values, "values");
     
     for (T value : values) {
       if (!elementType.isInstance(value)) {
         throw new IllegalArgumentException(String.format("Argument '%s' must only contain elements of type '%s'.", new Object[] { parameterName, elementType }));
       }
     }
     
 
 
 
 
 
 
     return values;
   }
   
   public static <T> T[] elementsOfTypeOrNull(Class<T> elementType, T[] values, String parameterName) {
     notNull(elementType, "elementType");
     notNull(values, "values");
     
     for (T value : values) {
       if ((value != null) && (!elementType.isInstance(value))) {
         throw new IllegalArgumentException(String.format("Argument '%s' must only contain elements of type '%s'.", new Object[] { parameterName, elementType }));
       }
     }
     
 
 
 
 
 
 
     return values;
   }
   
   public static int validElementRange(int size, int startInclusive, int endExclusive) {
     if ((startInclusive >= 0) && (endExclusive <= size) && (endExclusive >= startInclusive)) {
       return endExclusive - startInclusive;
     }
     
     throw new IllegalArgumentException(String.format("The specified element range is not valid: range=(%d, %d], length=%d", new Object[] { Integer.valueOf(startInclusive), Integer.valueOf(endExclusive), Integer.valueOf(size) }));
   }
   
 
 
 
 
 
 
 
 
 
 
   public static String notNullOrEmpty(String value, String parameterName)
   {
     if (!StringUtilities.isNullOrEmpty(value)) {
       return value;
     }
     throw new IllegalArgumentException(String.format("Argument '%s' must be a non-null, non-empty string.", new Object[] { parameterName }));
   }
   
 
   public static String notNullOrWhitespace(String value, String parameterName)
   {
     if (!StringUtilities.isNullOrWhitespace(value)) {
       return value;
     }
     throw new IllegalArgumentException(String.format("Argument '%s' must be a non-null, non-empty string.", new Object[] { parameterName }));
   }
   
 
 
 
 
 
   public static int isNonZero(int value, String parameterName)
   {
     if (value != 0) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be non-zero, but value was: %d.", new Object[] { parameterName, Integer.valueOf(value) }));
   }
   
   public static int isPositive(int value, String parameterName) {
     if (value > 0) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be positive, but value was: %d.", new Object[] { parameterName, Integer.valueOf(value) }));
   }
   
   public static int isNonNegative(int value, String parameterName) {
     if (value >= 0) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be non-negative, but value was: %d.", new Object[] { parameterName, Integer.valueOf(value) }));
   }
   
   public static int isNegative(int value, String parameterName) {
     if (value < 0) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be negative, but value was: %d.", new Object[] { parameterName, Integer.valueOf(value) }));
   }
   
   public static int inRange(int minInclusive, int maxInclusive, int value, String parameterName) {
     if (maxInclusive < minInclusive) {
       throw new IllegalArgumentException(String.format("The specified maximum value (%d) is less than the specified minimum value (%d).", new Object[] { Integer.valueOf(maxInclusive), Integer.valueOf(minInclusive) }));
     }
     
 
 
 
 
 
 
     if ((value >= minInclusive) && (value <= maxInclusive)) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be in the range [%s, %s], but value was: %d.", new Object[] { parameterName, Integer.valueOf(minInclusive), Integer.valueOf(maxInclusive), Integer.valueOf(value) }));
   }
   
 
 
 
 
 
 
 
   public static double isNonZero(double value, String parameterName)
   {
     if (value != 0.0D) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be non-zero, but value was: %s.", new Object[] { parameterName, Double.valueOf(value) }));
   }
   
   public static double isPositive(double value, String parameterName) {
     if (value > 0.0D) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be positive, but value was: %s.", new Object[] { parameterName, Double.valueOf(value) }));
   }
   
   public static double isNonNegative(double value, String parameterName) {
     if (value >= 0.0D) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be non-negative, but value was: %s.", new Object[] { parameterName, Double.valueOf(value) }));
   }
   
   public static double isNegative(double value, String parameterName) {
     if (value < 0.0D) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be negative, but value was: %s.", new Object[] { parameterName, Double.valueOf(value) }));
   }
   
 
 
 
 
   public static double inRange(double minInclusive, double maxInclusive, double value, String parameterName)
   {
     if (Double.isNaN(minInclusive)) {
       throw new IllegalArgumentException("The minimum value cannot be NaN.");
     }
     
     if (Double.isNaN(maxInclusive)) {
       throw new IllegalArgumentException("The maximum value cannot be NaN.");
     }
     
     if (maxInclusive < minInclusive) {
       throw new IllegalArgumentException(String.format("The specified maximum value (%s) is less than the specified minimum value (%s).", new Object[] { Double.valueOf(maxInclusive), Double.valueOf(minInclusive) }));
     }
     
 
 
 
 
 
 
     if ((value >= minInclusive) && (value <= maxInclusive)) {
       return value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be in the range [%s, %s], but value was: %s.", new Object[] { parameterName, Double.valueOf(minInclusive), Double.valueOf(maxInclusive), Double.valueOf(value) }));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static <T> T instanceOf(Class<T> type, Object value, String parameterName)
   {
     Class<?> actualType = getBoxedType((Class)notNull(type, "type"));
     
     if (actualType.isInstance(value)) {
       return (T)value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must be an instance of type %s.", new Object[] { parameterName, type.getCanonicalName() }));
   }
   
 
 
 
 
 
   public static <T> T notInstanceOf(Class<T> type, Object value, String parameterName)
   {
     Class<?> actualType = getBoxedType((Class)notNull(type, "type"));
     
     if (!actualType.isInstance(value)) {
       return (T)value;
     }
     
     throw new IllegalArgumentException(String.format("Argument '%s' must not be an instance of type %s.", new Object[] { parameterName, type.getCanonicalName() }));
   }
   
 
 
 
 
 
   private static Class<?> getBoxedType(Class<?> type)
   {
     if (!type.isPrimitive()) {
       return type;
     }
     if (type == Boolean.TYPE) {
       return Boolean.class;
     }
     if (type == Character.TYPE) {
       return Character.class;
     }
     if (type == Byte.TYPE) {
       return Byte.class;
     }
     if (type == Short.TYPE) {
       return Short.class;
     }
     if (type == Integer.TYPE) {
       return Integer.class;
     }
     if (type == Long.TYPE) {
       return Long.class;
     }
     if (type == Float.TYPE) {
       return Float.class;
     }
     if (type == Double.TYPE) {
       return Double.class;
     }
     return type;
   }
 }


