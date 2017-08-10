 package com.strobel.core;
 
 import com.strobel.util.ContractUtils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class StringUtilities
 {
   public static final String EMPTY = "";
   
   private StringUtilities()
   {
     throw ContractUtils.unreachable();
   }
   
   private static final StringComparator[] _comparators = { StringComparator.Ordinal, StringComparator.OrdinalIgnoreCase };
   
   public static boolean isNullOrEmpty(String s) {
     return (s == null) || (s.length() == 0);
   }
   
   public static boolean equals(String s1, String s2) {
     return StringComparator.Ordinal.equals(s1, s2);
   }
   
   public static boolean equals(String s1, String s2, StringComparison comparison) {
     return _comparators[((StringComparison)VerifyArgument.notNull(comparison, "comparison")).ordinal()].equals(s1, s2);
   }
   
   public static int compare(String s1, String s2) {
     return StringComparator.Ordinal.compare(s1, s2);
   }
   
   public static int compare(String s1, String s2, StringComparison comparison) {
     return _comparators[((StringComparison)VerifyArgument.notNull(comparison, "comparison")).ordinal()].compare(s1, s2);
   }
   
   public static int getHashCode(String s) {
     if (isNullOrEmpty(s)) {
       return 0;
     }
     return s.hashCode();
   }
   
   public static int getHashCodeIgnoreCase(String s) {
     if (isNullOrEmpty(s)) {
       return 0;
     }
     
     int hash = 0;
     
     int i = 0; for (int n = s.length(); i < n; i++) {
       hash = 31 * hash + Character.toLowerCase(s.charAt(i));
     }
     
     return hash;
   }
   
   public static boolean isNullOrWhitespace(String s) {
     if (isNullOrEmpty(s)) {
       return true;
     }
     int i = 0; for (int length = s.length(); i < length; i++) {
       char ch = s.charAt(i);
       if (!Character.isWhitespace(ch)) {
         return false;
       }
     }
     return true;
   }
   
   public static boolean startsWith(CharSequence value, CharSequence prefix) {
     return substringEquals((CharSequence)VerifyArgument.notNull(value, "value"), 0, (CharSequence)VerifyArgument.notNull(prefix, "prefix"), 0, prefix.length(), StringComparison.Ordinal);
   }
   
 
 
 
 
 
 
   public static boolean startsWithIgnoreCase(CharSequence value, String prefix)
   {
     return substringEquals((CharSequence)VerifyArgument.notNull(value, "value"), 0, (CharSequence)VerifyArgument.notNull(prefix, "prefix"), 0, prefix.length(), StringComparison.OrdinalIgnoreCase);
   }
   
 
 
 
 
 
 
   public static boolean endsWith(CharSequence value, CharSequence suffix)
   {
     int valueLength = ((CharSequence)VerifyArgument.notNull(value, "value")).length();
     int suffixLength = ((CharSequence)VerifyArgument.notNull(suffix, "suffix")).length();
     int testOffset = valueLength - suffixLength;
     
     return (testOffset >= 0) && (substringEquals(value, testOffset, suffix, 0, suffixLength, StringComparison.Ordinal));
   }
   
 
 
 
 
 
 
 
   public static boolean endsWithIgnoreCase(CharSequence value, String suffix)
   {
     int valueLength = ((CharSequence)VerifyArgument.notNull(value, "value")).length();
     int suffixLength = ((String)VerifyArgument.notNull(suffix, "suffix")).length();
     int testOffset = valueLength - suffixLength;
     
     return (testOffset >= 0) && (substringEquals(value, testOffset, suffix, 0, suffixLength, StringComparison.OrdinalIgnoreCase));
   }
   
 
 
 
 
 
 
 
   public static String concat(Iterable<String> values)
   {
     return join(null, values);
   }
   
   public static String concat(String... values) {
     return join(null, values);
   }
   
   public static String join(String separator, Iterable<?> values) {
     VerifyArgument.notNull(values, "values");
     
     StringBuilder sb = new StringBuilder();
     
     boolean appendSeparator = false;
     
     for (Object value : values) {
       if (value != null)
       {
 
 
         if (appendSeparator) {
           sb.append(separator);
         }
         
         appendSeparator = true;
         
         sb.append(value);
       }
     }
     return sb.toString();
   }
   
   public static String join(String separator, String... values) {
     if (ArrayUtilities.isNullOrEmpty(values)) {
       return "";
     }
     
     StringBuilder sb = new StringBuilder();
     
     int i = 0; for (int n = values.length; i < n; i++) {
       String value = values[i];
       
       if (value != null)
       {
 
 
         if ((i != 0) && (separator != null)) {
           sb.append(separator);
         }
         
         sb.append(value);
       }
     }
     return sb.toString();
   }
   
 
 
 
 
 
   public static boolean substringEquals(CharSequence value, int offset, CharSequence comparand, int comparandOffset, int substringLength)
   {
     return substringEquals(value, offset, comparand, comparandOffset, substringLength, StringComparison.Ordinal);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static boolean substringEquals(CharSequence value, int offset, CharSequence comparand, int comparandOffset, int substringLength, StringComparison comparison)
   {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.notNull(comparand, "comparand");
     
     VerifyArgument.isNonNegative(offset, "offset");
     VerifyArgument.isNonNegative(comparandOffset, "comparandOffset");
     
     VerifyArgument.isNonNegative(substringLength, "substringLength");
     
     int valueLength = value.length();
     
     if (offset + substringLength > valueLength) {
       return false;
     }
     
     int comparandLength = comparand.length();
     
     if (comparandOffset + substringLength > comparandLength) {
       return false;
     }
     
     boolean ignoreCase = comparison == StringComparison.OrdinalIgnoreCase;
     
     for (int i = 0; i < substringLength; i++) {
       char vc = value.charAt(offset + i);
       char cc = comparand.charAt(comparandOffset + i);
       
       if ((vc != cc) && ((!ignoreCase) || (Character.toLowerCase(vc) != Character.toLowerCase(cc))))
       {
 
 
         return false;
       }
     }
     return true;
   }
   
   public static boolean isTrue(String value) {
     if (isNullOrWhitespace(value)) {
       return false;
     }
     
     String trimmedValue = value.trim();
     
     if (trimmedValue.length() == 1) {
       char ch = Character.toLowerCase(trimmedValue.charAt(0));
       return (ch == 't') || (ch == 'y') || (ch == '1');
     }
     
     return (StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "true")) || (StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "yes"));
   }
   
   public static boolean isFalse(String value)
   {
     if (isNullOrWhitespace(value)) {
       return false;
     }
     
     String trimmedValue = value.trim();
     
     if (trimmedValue.length() == 1) {
       char ch = Character.toLowerCase(trimmedValue.charAt(0));
       return (ch == 'f') || (ch == 'n') || (ch == '0');
     }
     
     return (StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "false")) || (StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "no"));
   }
   
   public static String removeLeft(String value, String prefix)
   {
     return removeLeft(value, prefix, false);
   }
   
   public static String removeLeft(String value, String prefix, boolean ignoreCase) {
     VerifyArgument.notNull(value, "value");
     
     if (isNullOrEmpty(prefix)) {
       return value;
     }
     
     int prefixLength = prefix.length();
     int remaining = value.length() - prefixLength;
     
     if (remaining < 0) {
       return value;
     }
     
     if (remaining == 0) {
       if (ignoreCase) {
         return value.equalsIgnoreCase(prefix) ? "" : value;
       }
       return value.equals(prefix) ? "" : value;
     }
     
     if (ignoreCase) {
       return startsWithIgnoreCase(value, prefix) ? value.substring(prefixLength) : value;
     }
     
 
 
     return value.startsWith(prefix) ? value.substring(prefixLength) : value;
   }
   
 
   public static String removeLeft(String value, char[] removeChars)
   {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.notNull(removeChars, "removeChars");
     
     int totalLength = value.length();
     int start = 0;
     
     while ((start < totalLength) && (ArrayUtilities.contains(removeChars, value.charAt(start)))) {
       start++;
     }
     
     return start > 0 ? value.substring(start) : value;
   }
   
   public static String removeRight(String value, String suffix) {
     return removeRight(value, suffix, false);
   }
   
   public static String removeRight(String value, String suffix, boolean ignoreCase) {
     VerifyArgument.notNull(value, "value");
     
     if (isNullOrEmpty(suffix)) {
       return value;
     }
     
     int valueLength = value.length();
     int suffixLength = suffix.length();
     int end = valueLength - suffixLength;
     
     if (end < 0) {
       return value;
     }
     
     if (end == 0) {
       if (ignoreCase) {
         return value.equalsIgnoreCase(suffix) ? "" : value;
       }
       return value.equals(suffix) ? "" : value;
     }
     
     if (ignoreCase) {
       return endsWithIgnoreCase(value, suffix) ? value.substring(0, end) : value;
     }
     
 
 
     return value.endsWith(suffix) ? value.substring(0, end) : value;
   }
   
 
   public static String removeRight(String value, char[] removeChars)
   {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.notNull(removeChars, "removeChars");
     
     int totalLength = value.length();
     int length = totalLength;
     
     while ((length > 0) && (ArrayUtilities.contains(removeChars, value.charAt(length - 1)))) {
       length--;
     }
     
     return length == totalLength ? value : value.substring(0, length);
   }
   
   public static String padLeft(String value, int length) {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.isNonNegative(length, "length");
     
     if (length == 0) {
       return value;
     }
     
     return String.format("%1$" + length + "s", new Object[] { value });
   }
   
   public static String padRight(String value, int length) {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.isNonNegative(length, "length");
     
     if (length == 0) {
       return value;
     }
     
     return String.format("%1$-" + length + "s", new Object[] { value });
   }
   
   public static String trimLeft(String value) {
     VerifyArgument.notNull(value, "value");
     
     int totalLength = value.length();
     int start = 0;
     
     while ((start < totalLength) && (value.charAt(start) <= ' ')) {
       start++;
     }
     
     return start > 0 ? value.substring(start) : value;
   }
   
   public static String trimRight(String value) {
     VerifyArgument.notNull(value, "value");
     
     int totalLength = value.length();
     int length = totalLength;
     
     while ((length > 0) && (value.charAt(length - 1) <= ' ')) {
       length--;
     }
     
     return length == totalLength ? value : value.substring(0, length);
   }
   
   public static String trimAndRemoveLeft(String value, String prefix) {
     return trimAndRemoveLeft(value, prefix, false);
   }
   
   public static String trimAndRemoveLeft(String value, String prefix, boolean ignoreCase) {
     VerifyArgument.notNull(value, "value");
     
     String trimmedValue = value.trim();
     String result = removeLeft(trimmedValue, prefix, ignoreCase);
     
 
     if (result == trimmedValue) {
       return trimmedValue;
     }
     
     return trimLeft(result);
   }
   
   public static String trimAndRemoveLeft(String value, char[] removeChars) {
     VerifyArgument.notNull(value, "value");
     
     String trimmedValue = value.trim();
     String result = removeLeft(trimmedValue, removeChars);
     
 
     if (result == trimmedValue) {
       return trimmedValue;
     }
     
     return trimLeft(result);
   }
   
   public static String trimAndRemoveRight(String value, String suffix) {
     return trimAndRemoveRight(value, suffix, false);
   }
   
   public static String trimAndRemoveRight(String value, String suffix, boolean ignoreCase) {
     VerifyArgument.notNull(value, "value");
     
     String trimmedValue = value.trim();
     String result = removeRight(trimmedValue, suffix, ignoreCase);
     
 
     if (result == trimmedValue) {
       return trimmedValue;
     }
     
     return trimRight(result);
   }
   
   public static String trimAndRemoveRight(String value, char[] removeChars) {
     VerifyArgument.notNull(value, "value");
     
     String trimmedValue = value.trim();
     String result = removeRight(trimmedValue, removeChars);
     
 
     if (result == trimmedValue) {
       return trimmedValue;
     }
     
     return trimRight(result);
   }
   
   public static int getUtf8ByteCount(String value) {
     VerifyArgument.notNull(value, "value");
     
     if (value.isEmpty()) {
       return 0;
     }
     
     int count = 0;
     
     int i = 0; for (int n = value.length(); i < n; count++) {
       char c = value.charAt(i);
       if (c > '߿') {
         count += 2;
       }
       else if (c > '') {
         count++;
       }
       i++;
     }
     
 
 
 
 
 
 
 
     return count;
   }
   
   public static String escape(char ch) {
     return escapeCharacter(ch, false);
   }
   
   private static String escapeCharacter(char ch, boolean isUnicodeSupported) {
     if (ch == '\'') {
       return "\\'";
     }
     
     if (shouldEscape(ch, false, isUnicodeSupported)) {
       switch (ch) {
       case '\000': 
         return "\\0";
       case '\b': 
         return "\\b";
       case '\f': 
         return "\\f";
       }
       return String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) });
     }
     
 
     return String.valueOf(ch);
   }
   
   public static String escape(char ch, boolean quote) {
     return escape(ch, quote, false);
   }
   
   public static String escape(char ch, boolean quote, boolean isUnicodeSupported) {
     if (quote) {
       if (ch == '\'') {
         return "'\\''";
       }
       
       if (shouldEscape(ch, true, isUnicodeSupported)) {
         switch (ch) {
         case '\000': 
           return "'\\0'";
         case '\t': 
           return "'\\t'";
         case '\b': 
           return "'\\b'";
         case '\n': 
           return "'\\n'";
         case '\r': 
           return "'\\r'";
         case '\f': 
           return "'\\f'";
         case '"': 
           return "'\\\"'";
         case '\\': 
           return "'\\\\'";
         }
         return String.format("'\\u%1$04x'", new Object[] { Integer.valueOf(ch) });
       }
       
 
       return "'" + ch + "'";
     }
     
     return escape(ch);
   }
   
   public static String escape(String value) {
     return escape(value, false);
   }
   
   public static String escape(String value, boolean quote) {
     return escape(value, quote, false);
   }
   
   public static String escape(String value, boolean quote, boolean isUnicodeSupported)
   {
     if (value == null) {
       return null;
     }
     
     StringBuilder sb;
     
     if (quote) {
       StringBuilder sb = new StringBuilder(value.length());
       sb.append('"');
     }
     else {
       sb = null;
     }
     
     int i = 0; for (int n = value.length(); i < n; i++) {
       char ch = value.charAt(i);
       boolean shouldEscape = shouldEscape(ch, quote, isUnicodeSupported);
       
       if (shouldEscape) {
         if (sb == null) {
           sb = new StringBuilder();
           
           if (i != 0) {
             sb.append(value, 0, i);
           }
         }
         
         switch (ch) {
         case '\000': 
           sb.append("\\u0000");
           break;
         case '\t': 
           sb.append('\\');
           sb.append('t');
           break;
         case '\b': 
           sb.append('\\');
           sb.append('b');
           break;
         case '\n': 
           sb.append('\\');
           sb.append('n');
           break;
         case '\r': 
           sb.append('\\');
           sb.append('r');
           break;
         case '\f': 
           sb.append('\\');
           sb.append('f');
           break;
         case '"': 
           sb.append('\\');
           sb.append('"');
           break;
         case '\\': 
           sb.append('\\');
           sb.append('\\');
           break;
         default: 
           sb.append(String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) }));
           break;
         }
       }
       else if (sb != null) {
         sb.append(ch);
       }
     }
     
     if (quote) {
       sb.append('"');
     }
     
     if (sb == null) {
       return value;
     }
     
     return sb.toString();
   }
   
   public static String escapeIdentifier(String value, boolean isUnicodeSupported) {
     if (isNullOrEmpty(value)) {
       return value;
     }
     
     StringBuilder sb = null;
     
     char start = value.charAt(0);
     
     if (!Character.isJavaIdentifierStart(start)) {
       sb = new StringBuilder(value.length() * 2);
       sb.append(start);
     }
     
     int i = 1; for (int n = value.length(); i < n; i++) {
       char ch = value.charAt(i);
       
       boolean valid = (Character.isJavaIdentifierPart(ch)) && ((isUnicodeSupported) || (ch < 'À'));
       
 
       if ((!valid) || (sb != null))
       {
 
 
         if (sb == null) {
           sb = new StringBuilder(value.length() * 2);
         }
         
         if (valid) {
           sb.append(ch);
         }
         else {
           sb.append(String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) }));
         }
       }
     }
     if (sb == null) {
       return value;
     }
     
     return sb.toString();
   }
   
   private static boolean shouldEscape(char ch, boolean quote, boolean isUnicodeSupported) {
     switch (ch) {
     case '\000': 
     case '\b': 
     case '\f': 
       return true;
     
     case '\t': 
     case '\n': 
     case '\r': 
     case '"': 
     case '\\': 
       return quote;
     }
     
     switch (Character.getType(ch)) {
     case 0: 
     case 15: 
     case 16: 
       return true;
     }
     
     return ((!isUnicodeSupported) && (ch >= 'À')) || ((quote) && (Character.isWhitespace(ch)) && (ch != ' '));
   }
   
 
 
 
   public static String repeat(char ch, int length)
   {
     VerifyArgument.isNonNegative(length, "length");
     char[] c = new char[length];
     Arrays.fill(c, 0, length, ch);
     return new String(c);
   }
   
 
 
 
   public static List<String> split(String value, char firstDelimiter, char... additionalDelimiters)
   {
     return split(value, true, firstDelimiter, additionalDelimiters);
   }
   
 
 
 
 
   public static List<String> split(String value, boolean removeEmptyEntries, char firstDelimiter, char... additionalDelimiters)
   {
     VerifyArgument.notNull(value, "value");
     
     int end = value.length();
     ArrayList<String> parts = new ArrayList();
     
     if (end == 0) {
       return parts;
     }
     
     int start = 0;
     int i = start;
     
     while (i < end) {
       char ch = value.charAt(i);
       
       if ((ch == firstDelimiter) || (contains(additionalDelimiters, ch))) {
         if ((i != start) || (!removeEmptyEntries)) {
           parts.add(value.substring(start, i));
         }
         
         start = i + 1;
         
         if ((!removeEmptyEntries) && (start == end)) {
           parts.add("");
         }
       }
       
       i++;
     }
     
     if (start < end) {
       parts.add(value.substring(start, end));
     }
     
     return parts;
   }
   
   public static List<String> split(String value, char[] delimiters) {
     return split(value, true, delimiters);
   }
   
 
 
 
   public static List<String> split(String value, boolean removeEmptyEntries, char[] delimiters)
   {
     VerifyArgument.notNull(value, "value");
     VerifyArgument.notNull(delimiters, "delimiters");
     
     int end = value.length();
     ArrayList<String> parts = new ArrayList();
     
     if (end == 0) {
       return parts;
     }
     
     int start = 0;
     int i = start;
     
     while (i < end) {
       char ch = value.charAt(i);
       
       if (contains(delimiters, ch)) {
         if ((i != start) || (!removeEmptyEntries)) {
           parts.add(value.substring(start, i));
         }
         
         start = i + 1;
       }
       
       i++;
     }
     
     if (start < end) {
       parts.add(value.substring(start, end));
     }
     
     return parts;
   }
   
   private static boolean contains(char[] array, char value) {
     for (char c : array) {
       if (c == value) {
         return true;
       }
     }
     return false;
   }
 }


