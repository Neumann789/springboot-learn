 package com.javosize.actions;
 
 import com.javosize.log.Log;
 import java.math.BigInteger;
 import java.net.InetAddress;
 import java.security.MessageDigest;
 
 
 
 public class GetJavaInfoAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(GetJavaInfoAction.class.getName());
   
   private String hostname = "NULL"; private String jvmName = "NULL"; private String javaVersion = "NULL"; private String javaVendor = "NULL"; private String os = "NULL"; private String id = "NULL";
   
 
 
 
 
 
 
 
 
   public String execute()
   {
     log.debug("Java info requested.");
     return getJavaInfo();
   }
   
   public String getJavaInfo()
   {
     try {
       this.hostname = InetAddress.getLocalHost().getHostName();
       this.jvmName = System.getProperty("java.vm.name");
       this.javaVersion = System.getProperty("java.version");
       this.javaVendor = System.getProperty("java.vendor");
       
       this.os = System.getProperty("os.name");
       if ((this.os != null) && (System.getProperty("os.arch") != null)) {
         this.os = (this.os + " (Arch=" + System.getProperty("os.arch") + ")");
       }
       if ((this.os != null) && (System.getProperty("os.version") != null)) {
         this.os = (this.os + " (Ver=" + System.getProperty("os.version") + ")");
       }
       
       String classpath = System.getProperty("java.class.path");
       this.id = getMD5(this.hostname + "-" + this.javaVersion + "-" + this.jvmName + "-" + this.javaVendor + "-" + classpath);
       
       log.info("Java Agent info [HostName=" + this.hostname + "][JavaVersion=" + this.javaVersion + "][JVM Name=" + this.jvmName + "][JavaVendor=" + this.javaVendor + "][OS=" + this.os + "][ID=" + this.id + "]");
     } catch (Throwable th) {
       log.error("Error obtaining agent info: " + th, th);
     }
     
 
     return "{\"hostname\": \"" + this.hostname + "\", " + "\"jvmName\": \"" + this.jvmName + "\", " + "\"javaVersion\": \"" + this.javaVersion + "\", " + "\"javaVendor\": \"" + this.javaVendor + "\", " + "\"os\": \"" + this.os + "\", " + "\"id\": \"" + this.id + "\"" + "}";
   }
   
 
 
 
 
 
 
   private String getMD5(String text)
     throws Exception
   {
     MessageDigest m = MessageDigest.getInstance("MD5");
     m.update(text.getBytes(), 0, text.length());
     return new BigInteger(1, m.digest()).toString(16);
   }
   
   public String getHostname() {
     return this.hostname;
   }
   
   public String getJvmName() {
     return this.jvmName;
   }
   
   public String getJavaVersion() {
     return this.javaVersion;
   }
   
   public String getJavaVendor() {
     return this.javaVendor;
   }
   
   public String getOs() {
     return this.os;
   }
   
   public String getId() {
     return this.id;
   }
 }


