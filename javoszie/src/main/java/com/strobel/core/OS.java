 package com.strobel.core;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum OS
 {
   WINDOWS_NT(new String[] { "Windows NT" }), 
   WINDOWS_95(new String[] { "Windows 95" }), 
   WINDOWS_98(new String[] { "Windows 98" }), 
   WINDOWS_2000(new String[] { "Windows 2000" }), 
   WINDOWS_VISTA(new String[] { "Windows Vista" }), 
   WINDOWS_7(new String[] { "Windows 7" }), 
   WINDOWS_OTHER(new String[] { "Windows" }), 
   
   SOLARIS(new String[] { "Solaris" }), 
   LINUX(new String[] { "Linux" }), 
   HP_UX(new String[] { "HP-UX" }), 
   IBM_AIX(new String[] { "AIX" }), 
   SGI_IRIX(new String[] { "Irix" }), 
   SUN_OS(new String[] { "SunOS" }), 
   COMPAQ_TRU64_UNIX(new String[] { "Digital UNIX" }), 
   MAC(new String[] { "Mac OS X", "Darwin" }), 
   FREE_BSD(new String[] { "freebsd" }), 
   
 
   OS2(new String[] { "OS/2" }), 
   COMPAQ_OPEN_VMS(new String[] { "OpenVMS" }), 
   
 
 
 
   OTHER(new String[] { "" });
   
   private final String[] names;
   private static OS current;
   
   private OS(String... names) { this.names = names; }
   
 
 
 
   public boolean isWindows()
   {
     return ordinal() <= WINDOWS_OTHER.ordinal();
   }
   
 
 
   public boolean isUnix()
   {
     return (ordinal() > WINDOWS_OTHER.ordinal()) && (ordinal() < OS2.ordinal());
   }
   
 
 
 
 
   public static OS get(String osName)
   {
     osName = osName.toLowerCase();
     for (OS os : values()) {
       for (String name : os.names) {
         if (osName.contains(name.toLowerCase())) {
           return os;
         }
       }
     }
     throw ContractUtils.unreachable();
   }
   
 
 
 
 
   public static OS get()
   {
     if (current == null) {
       current = get(System.getProperty("os.name"));
     }
     return current;
   }
 }


