 package com.strobel.io;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.StringComparison;
 import com.strobel.core.StringUtilities;
 import com.strobel.util.ContractUtils;
 import java.io.File;
 import java.io.IOException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class PathHelper
 {
   public static final char DirectorySeparator;
   public static final char AlternateDirectorySeparator;
   public static final char VolumeSeparator;
   private static final int maxPath = 260;
   private static final int maxDirectoryLength = 255;
   private static final char[] invalidPathCharacters = { '"', '<', '>', '|', '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037' };
   
 
 
 
 
   private static final char[] invalidFileNameCharacters = { '"', '<', '>', '|', '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', ':', '*', '?', '\\', '/' };
   
 
 
 
 
   private static final char[] trimEndChars = { '\t', '\n', '\013', '\f', '\r', ' ', '', ' ' };
   
   private static final boolean isWindows;
   
 
   static
   {
     String osName = System.getProperty("os.name");
     
     isWindows = (osName != null) && (StringUtilities.startsWithIgnoreCase(osName, "windows"));
     
     if (isWindows) {
       DirectorySeparator = '\\';
       AlternateDirectorySeparator = '/';
       VolumeSeparator = ':';
     }
     else {
       DirectorySeparator = '/';
       AlternateDirectorySeparator = '\\';
       VolumeSeparator = '/';
     }
   }
   
   private PathHelper() {
     throw ContractUtils.unreachable();
   }
   
   public static char[] getInvalidPathCharacters() {
     return (char[])invalidPathCharacters.clone();
   }
   
   public static char[] getInvalidFileNameCharacters() {
     return invalidFileNameCharacters;
   }
   
   public static boolean isPathRooted(String path) {
     if (StringUtilities.isNullOrEmpty(path)) {
       return false;
     }
     
     int length = path.length();
     
     return (path.charAt(0) == DirectorySeparator) || (path.charAt(0) == AlternateDirectorySeparator) || ((isWindows) && (length >= 2) && (path.charAt(1) == VolumeSeparator));
   }
   
 
   public static String combine(String path1, String path2)
   {
     if (path1 == null) {
       return path2 != null ? path2 : "";
     }
     
     if (path2 == null) {
       return path1;
     }
     
     checkInvalidPathChars(path1);
     checkInvalidPathChars(path2);
     
     return combineUnsafe(path1, path2);
   }
   
   public static String combine(String path1, String path2, String path3) {
     return combine(combine(path1, path2), path3);
   }
   
   public static String combine(String... paths) {
     if (ArrayUtilities.isNullOrEmpty(paths)) {
       return "";
     }
     
     int finalSize = 0;
     int firstComponent = 0;
     
 
 
 
 
 
 
     for (int i = 0; i < paths.length; i++) {
       String path = paths[i];
       
       if (!StringUtilities.isNullOrEmpty(path))
       {
 
 
         checkInvalidPathChars(path);
         
         int length = path.length();
         
         if (isPathRooted(path)) {
           firstComponent = i;
           finalSize = length;
         }
         else {
           finalSize += length;
         }
         
         char ch = path.charAt(length - 1);
         
         if ((ch != DirectorySeparator) && (ch != AlternateDirectorySeparator) && (ch != VolumeSeparator))
         {
 
 
           finalSize++;
         }
       }
     }
     if (finalSize == 0) {
       return "";
     }
     
     StringBuilder finalPath = new StringBuilder(finalSize);
     
     for (int i = firstComponent; i < paths.length; i++) {
       String path = paths[i];
       
       if (!StringUtilities.isNullOrEmpty(path))
       {
 
 
         int length = finalPath.length();
         
         if (length == 0) {
           finalPath.append(path);
         }
         else {
           char ch = finalPath.charAt(length - 1);
           
           if ((ch != DirectorySeparator) && (ch != AlternateDirectorySeparator) && (ch != VolumeSeparator))
           {
 
 
             finalPath.append(DirectorySeparator);
           }
           
           finalPath.append(path);
         }
       }
     }
     return finalPath.toString();
   }
   
   public static String getDirectoryName(String path) {
     if (StringUtilities.isNullOrEmpty(path)) {
       return "";
     }
     
     checkInvalidPathChars(path);
     
     String normalizedPath = normalizePath(path, false, 260);
     int root = getRootLength(normalizedPath);
     
     int i = normalizedPath.length();
     
     if (i > root) {
       i = normalizedPath.length();
       
       if (i == root) {
         return null;
       }
       
       while ((i > root) && (!isDirectorySeparator(normalizedPath.charAt(--i)))) {}
       
 
       return normalizedPath.substring(0, i);
     }
     
     return normalizedPath;
   }
   
   public static String getFileName(String path) {
     if (StringUtilities.isNullOrEmpty(path)) {
       return "";
     }
     
     checkInvalidPathChars(path);
     
     int length = path.length();
     
     int i = length; for (;;) { i--; if (i < 0) break;
       char ch = path.charAt(i);
       
       if ((isDirectorySeparator(ch)) || (ch == VolumeSeparator)) {
         return path.substring(i + 1, length);
       }
     }
     
     return path;
   }
   
   public static String getFileNameWithoutExtension(String path) {
     String fileName = getFileName(path);
     
     if (StringUtilities.isNullOrEmpty(fileName)) {
       return fileName;
     }
     
     if (fileName != null) {
       int dotPosition = fileName.lastIndexOf('.');
       
       if (dotPosition == -1) {
         return fileName;
       }
       
       return fileName.substring(0, dotPosition);
     }
     
 
     return null;
   }
   
   public static String getFullPath(String path) {
     if (StringUtilities.isNullOrEmpty(path)) {
       return "";
     }
     
     return normalizePath(path, true, 260);
   }
   
   public static String getTempPath() {
     return getFullPath(System.getProperty("java.io.tmpdir"));
   }
   
   private static String combineUnsafe(String path1, String path2) {
     if (path2.length() == 0) {
       return path1;
     }
     
     if (path1.length() == 0) {
       return path2;
     }
     
     if (isPathRooted(path2)) {
       return path2;
     }
     
     char ch = path1.charAt(path1.length() - 1);
     
     if ((ch != DirectorySeparator) && (ch != AlternateDirectorySeparator) && (ch != VolumeSeparator))
     {
 
 
       return path1 + DirectorySeparator + path2;
     }
     
     return path1 + path2;
   }
   
   private static void checkInvalidPathChars(String path) {
     if ((!isWindows) && (path.length() >= 2) && (path.charAt(0) == '\\') && (path.charAt(1) == '\\')) {
       throw Error.invalidPathCharacters();
     }
     
     for (int i = 0; i < path.length(); i++) {
       int ch = path.charAt(i);
       
       if ((ch == 34) || (ch == 60) || (ch == 62) || (ch == 124) || (ch < 32)) {
         throw Error.invalidPathCharacters();
       }
     }
   }
   
   private static boolean isDirectorySeparator(char ch) {
     return (ch == DirectorySeparator) || (ch == AlternateDirectorySeparator);
   }
   
   private static int getRootLength(String path) {
     checkInvalidPathChars(path);
     
     int i = 0;
     int length = path.length();
     
     if (isWindows) {
       if ((length >= 1) && (isDirectorySeparator(path.charAt(0))))
       {
 
 
         i = 1;
         
         if ((length >= 2) && (isDirectorySeparator(path.charAt(1)))) {
           i = 2;
           int n = 2;
           
           while (i < length) { if (isDirectorySeparator(path.charAt(i))) { n--; if (n <= 0) break; }
             i++;
           }
         }
       }
       else if ((length >= 2) && (path.charAt(1) == VolumeSeparator))
       {
 
 
         i = 2;
         
         if ((length >= 3) && (isDirectorySeparator(path.charAt(2)))) {
           i++;
         }
       }
     }
     else if ((length >= 1) && (isDirectorySeparator(path.charAt(0)))) {
       i = 1;
     }
     
     return i;
   }
   
 
 
 
   private static String normalizePath(String p, boolean fullCheck, int maxPathLength)
   {
     String path;
     
 
     if (fullCheck) {
       String path = StringUtilities.trimAndRemoveRight(p, trimEndChars);
       checkInvalidPathChars(path);
     }
     else {
       path = p;
     }
     
     int index = 0;
     
     StringBuilder newBuffer = new StringBuilder(path.length() + 260);
     
     int spaceCount = 0;
     int dotCount = 0;
     
     boolean fixupDirectorySeparator = false;
     
 
 
 
 
     int significantCharCount = 0;
     int lastSignificantChar = -1;
     
 
 
 
 
     boolean startedWithVolumeSeparator = false;
     boolean firstSegment = true;
     
     int lastSeparatorPosition = 0;
     
     if (isWindows)
     {
 
 
 
 
       if ((path.length() > 0) && ((path.charAt(0) == DirectorySeparator) || (path.charAt(0) == AlternateDirectorySeparator))) {
         newBuffer.append('\\');
         index++;
         lastSignificantChar = 0;
       }
     }
     
 
 
 
     while (index < path.length()) {
       char currentChar = path.charAt(index);
       
 
 
 
 
 
 
 
 
       if ((currentChar == DirectorySeparator) || (currentChar == AlternateDirectorySeparator))
       {
 
 
 
 
 
 
 
 
 
 
 
 
         if (significantCharCount == 0)
         {
 
 
           if (dotCount > 0)
           {
 
 
             int start = lastSignificantChar + 1;
             
             if (path.charAt(start) != '.') {
               throw Error.illegalPath();
             }
             
 
 
 
 
             if (dotCount >= 2)
             {
 
 
               if ((startedWithVolumeSeparator) && (dotCount > 2)) {
                 throw Error.illegalPath();
               }
               
               if (path.charAt(start + 1) == '.')
               {
 
 
                 for (int i = start + 2; i < start + dotCount; i++) {
                   if (path.charAt(i) != '.') {
                     throw Error.illegalPath();
                   }
                 }
                 
                 dotCount = 2;
               }
               else {
                 if (dotCount > 1) {
                   throw Error.illegalPath();
                 }
                 dotCount = 1;
               }
             }
             
             if (dotCount == 2) {
               newBuffer.append('.');
             }
             
             newBuffer.append('.');
             fixupDirectorySeparator = false;
           }
           
 
 
 
 
           if ((spaceCount > 0) && (firstSegment))
           {
 
 
             if ((index + 1 < path.length()) && ((path.charAt(index + 1) == DirectorySeparator) || (path.charAt(index + 1) == AlternateDirectorySeparator)))
             {
 
 
               newBuffer.append(DirectorySeparator);
             }
           }
         }
         
         dotCount = 0;
         
 
 
 
         spaceCount = 0;
         
         if (!fixupDirectorySeparator) {
           fixupDirectorySeparator = true;
           newBuffer.append(DirectorySeparator);
         }
         
         significantCharCount = 0;
         lastSignificantChar = index;
         startedWithVolumeSeparator = false;
         firstSegment = false;
         
         int thisPos = newBuffer.length() - 1;
         
         if (thisPos - lastSeparatorPosition > 255) {
           throw Error.pathTooLong();
         }
         
         lastSeparatorPosition = thisPos;
       }
       else if (currentChar == '.')
       {
 
 
 
         dotCount++;
 
 
 
 
 
 
       }
       else if (currentChar == ' ') {
         spaceCount++;
 
 
       }
       else
       {
 
         fixupDirectorySeparator = false;
         
 
 
 
         if ((isWindows) && (firstSegment) && (currentChar == VolumeSeparator))
         {
 
 
 
           char driveLetter = index > 0 ? path.charAt(index - 1) : ' ';
           boolean validPath = (dotCount == 0) && (significantCharCount >= 1) && (driveLetter != ' ');
           
           if (!validPath) {
             throw Error.illegalPath();
           }
           
           startedWithVolumeSeparator = true;
           
 
 
 
 
           if (significantCharCount > 1)
           {
 
 
 
             int tempSpaceCount = 0;
             
             while ((tempSpaceCount < newBuffer.length()) && (newBuffer.charAt(tempSpaceCount) == ' '))
             {
 
               tempSpaceCount++;
             }
             
             if (significantCharCount - tempSpaceCount == 1)
             {
 
 
 
               newBuffer.setLength(0);
               newBuffer.append(driveLetter);
             }
           }
           
           significantCharCount = 0;
         }
         else {
           significantCharCount += 1 + dotCount + spaceCount;
         }
         
 
 
 
 
 
         if ((dotCount > 0) || (spaceCount > 0)) {
           int copyLength = lastSignificantChar >= 0 ? index - lastSignificantChar - 1 : index;
           
 
 
           if (copyLength > 0) {
             for (int i = 0; i < copyLength; i++) {
               newBuffer.append(path.charAt(lastSignificantChar + 1 + i));
             }
           }
           
           dotCount = 0;
           spaceCount = 0;
         }
         
         newBuffer.append(currentChar);
         lastSignificantChar = index;
       }
       
       index++;
     }
     
     if (newBuffer.length() - 1 - lastSeparatorPosition > 255) {
       throw Error.pathTooLong();
     }
     
 
 
 
 
 
 
     if ((significantCharCount == 0) && 
       (dotCount > 0))
     {
       int start = lastSignificantChar + 1;
       
       if (path.charAt(start) != '.') {
         throw Error.illegalPath();
       }
       
 
 
 
 
       if (dotCount >= 2)
       {
 
 
         if ((startedWithVolumeSeparator) && (dotCount > 2)) {
           throw Error.illegalPath();
         }
         
         if (path.charAt(start + 1) == '.')
         {
 
 
           for (int i = start + 2; i < start + dotCount; i++) {
             if (path.charAt(i) != '.') {
               throw Error.illegalPath();
             }
           }
           
           dotCount = 2;
         }
         else {
           if (dotCount > 1) {
             throw Error.illegalPath();
           }
           dotCount = 1;
         }
       }
       
       if (dotCount == 2) {
         newBuffer.append("..");
       }
       else if (start == 0) {
         newBuffer.append('.');
       }
     }
     
 
 
 
 
     if (newBuffer.length() == 0) {
       throw Error.illegalPath();
     }
     
 
 
 
     if ((fullCheck) && (
       (StringUtilities.startsWith(newBuffer, "http:")) || (StringUtilities.startsWith(newBuffer, "file:"))))
     {
 
       throw Error.pathUriFormatNotSupported();
     }
     
 
     int normalizedLength = newBuffer.length();
     
 
 
 
 
 
     if ((normalizedLength > 1) && (newBuffer.charAt(0) == '\\') && (newBuffer.charAt(1) == '\\'))
     {
 
 
       int startIndex = 2;
       
       while (startIndex < normalizedLength) {
         if (newBuffer.charAt(startIndex) == '\\') {
           startIndex++;
           break;
         }
         
         startIndex++;
       }
       
 
       if (startIndex == normalizedLength) {
         throw Error.illegalUncPath();
       }
     }
     
 
 
 
     if (fullCheck) {
       String temp = newBuffer.toString();
       
       newBuffer.setLength(0);
       try
       {
         newBuffer.append(new File(temp).getCanonicalPath());
       }
       catch (IOException e) {
         throw Error.canonicalizationError(e);
       }
       
       normalizedLength = newBuffer.length();
     }
     
 
     if (newBuffer.length() >= maxPathLength) {
       throw Error.pathTooLong();
     }
     
     if (normalizedLength == 0) {
       return "";
     }
     
     String returnVal = newBuffer.toString();
     
     if (StringUtilities.equals(returnVal, path, StringComparison.Ordinal)) {
       returnVal = path;
     }
     
     return returnVal;
   }
 }


