 package com.strobel.core;
 
 import com.strobel.collections.Cache;
 import com.strobel.util.ContractUtils;
 import com.strobel.util.EmptyArrayCache;
 import java.lang.reflect.Array;
 import java.util.AbstractList;
 import java.util.Arrays;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ArrayUtilities
 {
   private static final Cache<Class<?>, Class<?>> GLOBAL_ARRAY_TYPE_CACHE = ;
   private static final Cache<Class<?>, Class<?>> ARRAY_TYPE_CACHE = Cache.createThreadLocalCache(GLOBAL_ARRAY_TYPE_CACHE);
   
   private ArrayUtilities() {
     throw ContractUtils.unreachable();
   }
   
   public static boolean isArray(Object value) {
     return (value != null) && (value.getClass().isArray());
   }
   
   public static <T> T[] create(Class<T> elementType, int length)
   {
     return (Object[])Array.newInstance(elementType, length);
   }
   
   public static Object createAny(Class<?> elementType, int length) {
     return Array.newInstance(elementType, length);
   }
   
   public static int[] range(int start, int count) {
     VerifyArgument.isNonNegative(count, "count");
     
     if (count == 0) {
       return EmptyArrayCache.EMPTY_INT_ARRAY;
     }
     
     int[] array = new int[count];
     
     int i = 0; for (int j = start; i < array.length; i++) {
       array[i] = (j++);
     }
     
     return array;
   }
   
   public static Object copyOf(Object array, int newLength) {
     return copyOf(VerifyArgument.notNull(array, "array"), newLength, array.getClass());
   }
   
   public static Object copyOf(Object array, int newLength, Class<?> newType) {
     Object copy = newType == Object[].class ? new Object[newLength] : Array.newInstance(newType.getComponentType(), newLength);
     
 
 
 
     System.arraycopy(array, 0, copy, 0, Math.min(Array.getLength(array), newLength));
     
     return copy;
   }
   
   public static Object copyOfRange(Object array, int from, int to) {
     return copyOfRange(VerifyArgument.notNull(array, "array"), from, to, array.getClass());
   }
   
   public static Object copyOfRange(Object array, int from, int to, Class<?> newType) {
     int newLength = to - from;
     
     if (newLength < 0) {
       throw new IllegalArgumentException(from + " > " + to);
     }
     
     Object copy = newType == Object[].class ? new Object[newLength] : Array.newInstance(newType.getComponentType(), newLength);
     
 
 
 
     System.arraycopy(array, from, copy, 0, Math.min(Array.getLength(array) - from, newLength));
     
 
 
 
 
 
     return copy;
   }
   
   public static <T> Class<T[]> makeArrayType(Class<T> elementType) {
     Class<?> arrayType = (Class)ARRAY_TYPE_CACHE.get(elementType);
     
     if (arrayType != null) {
       return arrayType;
     }
     
     return (Class)ARRAY_TYPE_CACHE.cache(elementType, Array.newInstance(elementType, 0).getClass());
   }
   
 
 
   public static <T> T[] copy(T[] source, T[] target)
   {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static <T> T[] copy(T[] source, int offset, T[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     
 
     int requiredLength = targetOffset + length;
     T[] actualTarget;
     T[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOf(source, source.length);
       }
       actualTarget = (Object[])Array.newInstance(source.getClass().getComponentType(), requiredLength);
     } else { T[] actualTarget;
       if (requiredLength > target.length) { T[] actualTarget;
         if (targetOffset == 0) {
           actualTarget = (Object[])Array.newInstance(target.getClass().getComponentType(), length);
         }
         else {
           actualTarget = Arrays.copyOf(target, requiredLength);
         }
       }
       else {
         actualTarget = target;
       }
     }
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static <T> boolean rangeEquals(T[] first, T[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (!Comparer.equals(first[i], second[i])) {
         return false;
       }
     }
     
     return true;
   }
   
   public static <T> boolean contains(T[] array, T value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static <T> int indexOf(T[] array, T value) {
     VerifyArgument.notNull(array, "array");
     if (value == null) {
       int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
         if (array[i] == null) {
           return i;
         }
       }
     }
     else {
       int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
         if (value.equals(array[i])) {
           return i;
         }
       }
     }
     return -1;
   }
   
   public static <T> int lastIndexOf(T[] array, T value) {
     VerifyArgument.notNull(array, "array");
     if (value == null) {
       for (int i = array.length - 1; i >= 0; i--) {
         if (array[i] == null) {
           return i;
         }
         
       }
     } else {
       for (int i = array.length - 1; i >= 0; i--) {
         if (value.equals(array[i])) {
           return i;
         }
       }
     }
     return -1;
   }
   
   public static <T> T[] insert(T[] array, int index, T value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     T[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
     
 
 
 
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   @SafeVarargs
   public static <T> T[] insert(T[] array, int index, T... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     VerifyArgument.elementsOfType(array.getClass().getComponentType(), values, "values");
     
     int newItemCount = values.length;
     
     T[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + newItemCount);
     
 
 
 
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static <T> T[] append(T[] array, T value) {
     if (array == null) {
       if (value == null) {
         throw new IllegalArgumentException("At least one value must be specified if 'array' is null.");
       }
       T[] newArray = (Object[])Array.newInstance(value.getClass(), 1);
       newArray[0] = value;
       return newArray;
     }
     return insert(array, ((Object[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   @SafeVarargs
   public static <T> T[] append(T[] array, T... values) {
     if (array == null) {
       if ((values == null) || (values.length == 0)) {
         throw new IllegalArgumentException("At least one value must be specified if 'array' is null.");
       }
       T[] newArray = (Object[])Array.newInstance(values.getClass().getComponentType(), values.length);
       System.arraycopy(values, 0, newArray, 0, values.length);
       return newArray;
     }
     return insert(array, ((Object[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static <T> T[] prepend(T[] array, T value) {
     if (array == null) {
       if (value == null) {
         throw new IllegalArgumentException("At least one value must be specified if 'array' is null.");
       }
       T[] newArray = (Object[])Array.newInstance(value.getClass(), 1);
       newArray[0] = value;
       return newArray;
     }
     return insert(array, 0, value);
   }
   
   @SafeVarargs
   public static <T> T[] prepend(T[] array, T... values) {
     if (array == null) {
       if ((values == null) || (values.length == 0)) {
         throw new IllegalArgumentException("At least one value must be specified if 'array' is null.");
       }
       T[] newArray = (Object[])Array.newInstance(values.getClass().getComponentType(), values.length);
       System.arraycopy(values, 0, newArray, 0, values.length);
       return newArray;
     }
     return insert(array, 0, values);
   }
   
   public static <T> T[] remove(T[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return (Object[])EmptyArrayCache.fromArrayType(array.getClass());
     }
     
     T[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
     
 
 
 
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static <T> boolean isNullOrEmpty(T[] array) {
     return (array == null) || (array.length == 0);
   }
   
   @SafeVarargs
   public static <T> T[] removeAll(T[] array, T... values) {
     VerifyArgument.notNull(array, "array");
     
     if (isNullOrEmpty(array)) {
       return array;
     }
     
     int count = values.length;
     
     int matchCount = 0;
     
     int[] matchIndices = new int[count];
     
     for (int i = 0; i < count; i++) {
       T value = values[i];
       int index = indexOf(array, value);
       
       if (index == -1) {
         matchIndices[i] = Integer.MAX_VALUE;
       }
       else
       {
         matchIndices[i] = index;
         matchCount++;
       }
     }
     if (matchCount == 0) {
       return array;
     }
     
     Arrays.sort(matchIndices);
     
     T[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - matchCount);
     
 
 
 
     int sourcePosition = 0;
     
     for (int i = 0; i < matchCount; i++) {
       int matchIndex = matchIndices[i];
       
       if (matchIndex == Integer.MAX_VALUE) {
         break;
       }
       
       System.arraycopy(array, sourcePosition, newArray, sourcePosition - i, matchIndex);
       
       sourcePosition = matchIndex + 1;
     }
     
     int remaining = array.length - sourcePosition;
     
     if (remaining > 0) {
       System.arraycopy(array, sourcePosition, newArray, newArray.length - remaining, remaining);
     }
     
     return newArray;
   }
   
   public static <T> T[] removeFirst(T[] array, T value) {
     int index = indexOf((Object[])VerifyArgument.notNull(array, "array"), value);
     
     if (index == -1) {
       return array;
     }
     
     return remove(array, index);
   }
   
   public static <T> T[] removeLast(T[] array, T value) {
     int index = lastIndexOf((Object[])VerifyArgument.notNull(array, "array"), value);
     
     if (index == -1) {
       return array;
     }
     
     return remove(array, index);
   }
   
   @SafeVarargs
   public static <T> T[] retainAll(T[] array, T... values) {
     VerifyArgument.notNull(array, "array");
     
     if (isNullOrEmpty(values)) {
       return array;
     }
     
     int count = values.length;
     
     int matchCount = 0;
     
     int[] matchIndices = new int[count];
     
     for (int i = 0; i < count; i++) {
       T value = values[i];
       int index = indexOf(array, value);
       
       if (index == -1) {
         matchIndices[i] = Integer.MAX_VALUE;
       }
       else
       {
         matchIndices[i] = index;
         matchCount++;
       }
     }
     if (matchCount == 0) {
       return (Object[])EmptyArrayCache.fromArrayType(array.getClass());
     }
     
     Arrays.sort(matchIndices);
     
     T[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), matchCount);
     
 
 
 
     int i = 0; for (int j = 0; i < count; i++) {
       int matchIndex = matchIndices[i];
       
       if (matchIndex != Integer.MAX_VALUE)
       {
 
 
         newArray[(j++)] = array[matchIndex];
       }
     }
     return newArray;
   }
   
   @SafeVarargs
   public static <T> T[] union(T[] array, T... values) {
     VerifyArgument.notNull(array, "array");
     
     if (isNullOrEmpty(values)) {
       return array;
     }
     
     int count = values.length;
     
     int matchCount = 0;
     
     int[] matchIndices = new int[count];
     
     for (int i = 0; i < count; i++) {
       T value = values[i];
       int index = indexOf(array, value);
       
       if (index == -1) {
         matchIndices[i] = Integer.MAX_VALUE;
       }
       else
       {
         matchIndices[i] = index;
         matchCount++;
       }
     }
     if (matchCount == 0) {
       return append(array, values);
     }
     
     T[] newArray = Arrays.copyOf(array, array.length + values.length - matchCount);
     
     int i = 0; for (int j = array.length; i < count; i++) {
       int matchIndex = matchIndices[i];
       
       if (matchIndex == Integer.MAX_VALUE) {
         newArray[(j++)] = values[i];
       }
     }
     
     return newArray;
   }
   
 
 
 
   public static boolean isNullOrEmpty(boolean[] array)
   {
     return (array == null) || (array.length == 0);
   }
   
   public static boolean[] copy(boolean[] source, boolean[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static boolean[] copy(boolean[] source, int offset, boolean[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     boolean[] actualTarget;
     boolean[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new boolean[requiredLength];
     }
     else if (requiredLength > target.length) {
       boolean[] actualTarget = new boolean[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(boolean[] first, boolean[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(boolean[] array, boolean value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(boolean[] array, boolean value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(boolean[] array, boolean value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(char[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static char[] copy(char[] source, char[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static char[] copy(char[] source, int offset, char[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     char[] actualTarget;
     char[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new char[requiredLength];
     }
     else if (requiredLength > target.length) {
       char[] actualTarget = new char[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(char[] first, char[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(char[] array, char value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(char[] array, char value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(char[] array, char value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(byte[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static byte[] copy(byte[] source, byte[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static byte[] copy(byte[] source, int offset, byte[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     byte[] actualTarget;
     byte[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new byte[requiredLength];
     }
     else if (requiredLength > target.length) {
       byte[] actualTarget = new byte[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(byte[] first, byte[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(byte[] array, byte value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(byte[] array, byte value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(byte[] array, byte value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(short[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static short[] copy(short[] source, short[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static short[] copy(short[] source, int offset, short[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     short[] actualTarget;
     short[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new short[requiredLength];
     }
     else if (requiredLength > target.length) {
       short[] actualTarget = new short[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(short[] first, short[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(short[] array, short value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(short[] array, short value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(short[] array, short value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(int[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static int[] copy(int[] source, int[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static int[] copy(int[] source, int offset, int[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     int[] actualTarget;
     int[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new int[requiredLength];
     }
     else if (requiredLength > target.length) {
       int[] actualTarget = new int[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(int[] first, int[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(int[] array, int value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(int[] array, int value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(int[] array, int value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(long[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static long[] copy(long[] source, long[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static long[] copy(long[] source, int offset, long[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     long[] actualTarget;
     long[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new long[requiredLength];
     }
     else if (requiredLength > target.length) {
       long[] actualTarget = new long[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(long[] first, long[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(long[] array, long value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(long[] array, long value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(long[] array, long value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(float[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static float[] copy(float[] source, float[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static float[] copy(float[] source, int offset, float[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     float[] actualTarget;
     float[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new float[requiredLength];
     }
     else if (requiredLength > target.length) {
       float[] actualTarget = new float[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(float[] first, float[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(float[] array, float value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(float[] array, float value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(float[] array, float value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static boolean isNullOrEmpty(double[] array) {
     return (array == null) || (array.length == 0);
   }
   
   public static double[] copy(double[] source, double[] target) {
     VerifyArgument.notNull(source, "source");
     return copy(source, 0, target, 0, source.length);
   }
   
   public static double[] copy(double[] source, int offset, double[] target, int targetOffset, int length) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.validElementRange(source.length, offset, offset + length);
     VerifyArgument.isNonNegative(targetOffset, "targetOffset");
     
 
     int requiredLength = targetOffset + length;
     double[] actualTarget;
     double[] actualTarget; if (target == null) {
       if (targetOffset == 0) {
         return Arrays.copyOfRange(source, offset, offset + length);
       }
       actualTarget = new double[requiredLength];
     }
     else if (requiredLength > target.length) {
       double[] actualTarget = new double[requiredLength];
       if (targetOffset != 0) {
         System.arraycopy(target, 0, actualTarget, 0, targetOffset);
       }
     }
     else {
       actualTarget = target;
     }
     
     System.arraycopy(source, offset, actualTarget, targetOffset, length);
     
     return actualTarget;
   }
   
   public static boolean rangeEquals(double[] first, double[] second, int offset, int length) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     int end = offset + length;
     
     if ((offset < 0) || (end < offset) || (end > first.length) || (end > second.length)) {
       return false;
     }
     
     if (first == second) {
       return true;
     }
     
     for (int i = offset; i < end; i++) {
       if (first[i] != second[i]) {
         return false;
       }
     }
     
     return true;
   }
   
   public static boolean contains(double[] array, double value) {
     VerifyArgument.notNull(array, "array");
     return indexOf(array, value) != -1;
   }
   
   public static int indexOf(double[] array, double value) {
     VerifyArgument.notNull(array, "array");
     int i = 0; for (int arrayLength = array.length; i < arrayLength; i++) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
   public static int lastIndexOf(double[] array, double value) {
     VerifyArgument.notNull(array, "array");
     for (int i = array.length - 1; i >= 0; i--) {
       if (value == array[i]) {
         return i;
       }
     }
     return -1;
   }
   
 
 
 
   public static boolean[] append(boolean[] array, boolean value)
   {
     if (isNullOrEmpty(array)) {
       return new boolean[] { value };
     }
     return insert(array, ((boolean[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static boolean[] append(boolean[] array, boolean... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((boolean[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static boolean[] prepend(boolean[] array, boolean value) {
     if (isNullOrEmpty(array)) {
       return new boolean[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static boolean[] prepend(boolean[] array, boolean... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static boolean[] remove(boolean[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_BOOLEAN_ARRAY;
     }
     
     boolean[] newArray = new boolean[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static boolean[] insert(boolean[] array, int index, boolean value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     boolean[] newArray = new boolean[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static boolean[] insert(boolean[] array, int index, boolean... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     boolean[] newArray = new boolean[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static char[] append(char[] array, char value) {
     if (isNullOrEmpty(array)) {
       return new char[] { value };
     }
     return insert(array, ((char[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static char[] append(char[] array, char... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((char[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static char[] prepend(char[] array, char value) {
     if (isNullOrEmpty(array)) {
       return new char[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static char[] prepend(char[] array, char... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static char[] remove(char[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_CHAR_ARRAY;
     }
     
     char[] newArray = new char[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static char[] insert(char[] array, int index, char value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     char[] newArray = new char[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static char[] insert(char[] array, int index, char... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     char[] newArray = new char[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static byte[] append(byte[] array, byte value) {
     if (isNullOrEmpty(array)) {
       return new byte[] { value };
     }
     return insert(array, ((byte[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static byte[] append(byte[] array, byte... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((byte[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static byte[] prepend(byte[] array, byte value) {
     if (isNullOrEmpty(array)) {
       return new byte[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static byte[] prepend(byte[] array, byte... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static byte[] remove(byte[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_BYTE_ARRAY;
     }
     
     byte[] newArray = new byte[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static byte[] insert(byte[] array, int index, byte value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     byte[] newArray = new byte[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static byte[] insert(byte[] array, int index, byte... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     byte[] newArray = new byte[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static short[] append(short[] array, short value) {
     if (isNullOrEmpty(array)) {
       return new short[] { value };
     }
     return insert(array, ((short[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static short[] append(short[] array, short... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((short[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static short[] prepend(short[] array, short value) {
     if (isNullOrEmpty(array)) {
       return new short[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static short[] prepend(short[] array, short... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static short[] remove(short[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_SHORT_ARRAY;
     }
     
     short[] newArray = new short[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static short[] insert(short[] array, int index, short value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     short[] newArray = new short[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static short[] insert(short[] array, int index, short... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     short[] newArray = new short[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static int[] append(int[] array, int value) {
     if (isNullOrEmpty(array)) {
       return new int[] { value };
     }
     return insert(array, ((int[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static int[] append(int[] array, int... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((int[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static int[] prepend(int[] array, int value) {
     if (isNullOrEmpty(array)) {
       return new int[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static int[] prepend(int[] array, int... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static int[] remove(int[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_INT_ARRAY;
     }
     
     int[] newArray = new int[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static int[] insert(int[] array, int index, int value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     int[] newArray = new int[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static int[] insert(int[] array, int index, int... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     int[] newArray = new int[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static long[] append(long[] array, long value) {
     if (isNullOrEmpty(array)) {
       return new long[] { value };
     }
     return insert(array, ((long[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static long[] append(long[] array, long... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((long[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static long[] prepend(long[] array, long value) {
     if (isNullOrEmpty(array)) {
       return new long[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static long[] prepend(long[] array, long... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static long[] remove(long[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_LONG_ARRAY;
     }
     
     long[] newArray = new long[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static long[] insert(long[] array, int index, long value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     long[] newArray = new long[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static long[] insert(long[] array, int index, long... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     long[] newArray = new long[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static float[] append(float[] array, float value) {
     if (isNullOrEmpty(array)) {
       return new float[] { value };
     }
     return insert(array, ((float[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static float[] append(float[] array, float... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((float[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static float[] prepend(float[] array, float value) {
     if (isNullOrEmpty(array)) {
       return new float[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static float[] prepend(float[] array, float... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static float[] remove(float[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_FLOAT_ARRAY;
     }
     
     float[] newArray = new float[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static float[] insert(float[] array, int index, float value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     float[] newArray = new float[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static float[] insert(float[] array, int index, float... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     float[] newArray = new float[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   public static double[] append(double[] array, double value) {
     if (isNullOrEmpty(array)) {
       return new double[] { value };
     }
     return insert(array, ((double[])VerifyArgument.notNull(array, "array")).length, value);
   }
   
   public static double[] append(double[] array, double... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, ((double[])VerifyArgument.notNull(array, "array")).length, values);
   }
   
   public static double[] prepend(double[] array, double value) {
     if (isNullOrEmpty(array)) {
       return new double[] { value };
     }
     return insert(array, 0, value);
   }
   
   public static double[] prepend(double[] array, double... values) {
     if (isNullOrEmpty(array)) {
       if (isNullOrEmpty(values)) {
         return values;
       }
       return Arrays.copyOf(values, values.length);
     }
     return insert(array, 0, values);
   }
   
   public static double[] remove(double[] array, int index) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length - 1, index, "index");
     
     if (array.length == 1) {
       return EmptyArrayCache.EMPTY_DOUBLE_ARRAY;
     }
     
     double[] newArray = new double[array.length - 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index - 1;
     
     if (remaining > 0) {
       System.arraycopy(array, index + 1, newArray, index, remaining);
     }
     
     return newArray;
   }
   
   public static double[] insert(double[] array, int index, double value) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     double[] newArray = new double[array.length + 1];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + 1, remaining);
     }
     
     newArray[index] = value;
     
     return newArray;
   }
   
   public static double[] insert(double[] array, int index, double... values) {
     VerifyArgument.notNull(array, "array");
     VerifyArgument.inRange(0, array.length, index, "index");
     
     if ((values == null) || (values.length == 0)) {
       return array;
     }
     
     int newItemCount = values.length;
     double[] newArray = new double[array.length + newItemCount];
     
     System.arraycopy(array, 0, newArray, 0, index);
     
     int remaining = array.length - index;
     
     if (remaining > 0) {
       System.arraycopy(array, index, newArray, index + newItemCount, remaining);
     }
     
     System.arraycopy(values, 0, newArray, index, newItemCount);
     
     return newArray;
   }
   
   @SafeVarargs
   public static <T> List<T> asUnmodifiableList(T... items) {
     return new UnmodifiableArrayList(items, null);
   }
   
   private static final class UnmodifiableArrayList<T> extends AbstractList<T> {
     private final T[] _array;
     
     private UnmodifiableArrayList(T[] array) {
       this._array = ((Object[])VerifyArgument.notNull(array, "array"));
     }
     
     public T get(int index)
     {
       return (T)this._array[index];
     }
     
     public int size()
     {
       return this._array.length;
     }
   }
 }


