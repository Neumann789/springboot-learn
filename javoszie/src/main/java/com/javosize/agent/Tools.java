 package com.javosize.agent;
 
 import com.javosize.log.Log;
 import java.io.BufferedOutputStream;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.lang.instrument.Instrumentation;
 import java.lang.management.ManagementFactory;
 import java.lang.management.RuntimeMXBean;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.net.URL;
 
 
 
 
 
 
 
 public class Tools
 {
   private static Log log = new Log(Tools.class.getName());
   
   private static final String REV = "1";
   
   private static final String NATIVE_DIR = "natives/";
   
   private static final String WIN_DIR = "windows/";
   private static final String NIX_DIR = "linux/";
   private static final String MAC_DIR = "mac/";
   private static final String SOLARIS_DIR = "solaris/";
   private static final String CACHE_SUBFOLDER = "agentcache.0.0_1";
   private static volatile String tempFolder = System.getProperty("java.io.tmpdir");
   private static volatile boolean tempFolderUpdatedToHome = false;
   
 
 
 
 
 
 
 
 
   public static File getTemporaryFile(String subfolder, String name, String extension)
     throws IOException
   {
     try
     {
       String folder = tempFolder;
       if ((subfolder != null) && (!subfolder.isEmpty())) {
         if (!tempFolder.endsWith(File.separator)) {
           folder = folder + File.separator;
         }
         folder = folder + subfolder;
       }
       
       File tmp = new File(folder, name + "." + extension);
       
       FileOutputStream fos = new FileOutputStream(tmp);
       fos.close();
       
       return tmp;
     } catch (Throwable th) {
       if (!tempFolderUpdatedToHome) {
         useHomeFolderForTempFiles();
         return getTemporaryFile(subfolder, name, extension);
       }
       
       throw new IOException("Unable to get temporary file: " + th, th);
     }
   }
   
   public static File getTemporaryFolder(String subfolder) throws IOException
   {
     try {
       String folder = tempFolder;
       if ((subfolder != null) && (!subfolder.isEmpty())) {
         if (!tempFolder.endsWith(File.separator)) {
           folder = folder + File.separator;
         }
         folder = folder + subfolder;
       }
       
       File tmp = new File(folder);
       tmp.mkdirs();
       
       return tmp;
     } catch (Throwable th) {
       if (!tempFolderUpdatedToHome) {
         useHomeFolderForTempFiles();
         return getTemporaryFolder(subfolder);
       }
       
       throw new IOException("Unable to get temporary folder: " + th, th);
     }
   }
   
 
 
 
   private static void useHomeFolderForTempFiles()
   {
     tempFolderUpdatedToHome = true;
     String baseFolder = System.getProperty("user.home") + File.separator + ".javosize" + File.separator;
     if (System.getProperty("javosize.preferences.folder") != null) {
       baseFolder = System.getProperty("javosize.preferences.folder");
       if (!baseFolder.endsWith(File.separator)) {
         baseFolder = baseFolder + File.separator;
       }
     }
     tempFolder = baseFolder + "tmp";
     File directory = new File(tempFolder);
     directory.mkdirs();
   }
   
   public static String getCurrentPID() {
     String jvm = ManagementFactory.getRuntimeMXBean().getName();
     return jvm.substring(0, jvm.indexOf('@'));
   }
   
   public static Class getClassFromInstrumentation(String classname)
   {
     classname = classname.trim();
     Instrumentation ins = Agent.getInstrumentation();
     if (classname.endsWith(".class")) {
       classname = classname.substring(0, classname.lastIndexOf("."));
     }
     
     Class[] classes = ins.getAllLoadedClasses();
     for (int i = 0; i < classes.length; i++) {
       if (classes[i].getName().equals(classname)) {
         return classes[i];
       }
     }
     
     return null;
   }
   
   public static byte[] getBytesFromStream(InputStream stream) throws IOException {
     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
     
     byte[] data = new byte[65536];
     int nRead; while ((nRead = stream.read(data, 0, data.length)) != -1) {
       buffer.write(data, 0, nRead);
     }
     buffer.flush();
     return buffer.toByteArray();
   }
   
   public static byte[] getBytesFromClass(String className, ClassLoader cl)
     throws IOException
   {
     return getBytesFromStream(cl.getResourceAsStream(className.replace('.', '/') + ".class"));
   }
   
   public static byte[] getBytesFromClass(Class<?> clazz) throws IOException {
     ClassLoader cl = clazz.getClassLoader();
     if (cl == null) {
       cl = ClassLoader.getSystemClassLoader();
     }
     return getBytesFromStream(cl.getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
   }
   
   public static byte[] getBytesFromResource(ClassLoader clazzLoader, String resource) throws IOException {
     return getBytesFromStream(clazzLoader.getResourceAsStream(resource));
   }
   
   public static void addToLibPath(String path) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
     if (System.getProperty("java.library.path") != null)
     {
 
 
       System.setProperty("java.library.path", path + System.getProperty("path.separator") + System.getProperty("java.library.path"));
     } else {
       System.setProperty("java.library.path", path);
     }
     
 
 
     Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
     fieldSysPath.setAccessible(true);
     fieldSysPath.set(null, null);
   }
   
   public static void extractResourceToDirectory(ClassLoader loader, String resourceName, String targetName, String targetDir) throws IOException
   {
     InputStream source = loader.getResourceAsStream(resourceName);
     File tmpdir = new File(targetDir);
     File target = new File(tmpdir, targetName);
     target.createNewFile();
     
     FileOutputStream stream = new FileOutputStream(target);
     byte[] buf = new byte[65536];
     int read;
     while ((read = source.read(buf)) != -1) {
       stream.write(buf, 0, read);
     }
     stream.close();
     source.close();
   }
   
   public static void loadAgentLibrary() {
     switch (Platform.getPlatform()) {
     case WINDOWS: 
       unpack("windows/attach.dll");
       break;
     case LINUX: 
       unpack("linux/libattach.so");
       break;
     case MAC: 
       unpack("mac/libattach.dylib");
       break;
     case SOLARIS: 
       unpack("solaris/libattach.so");
       break;
     default: 
       throw new UnsupportedOperationException("unsupported platform");
     }
   }
   
   private static void unpack(String path) {
     try {
       URL url = ClassLoader.getSystemResource("natives/" + ((Platform.is64Bit()) || (Platform.getPlatform() == Platform.MAC) ? "64/" : "32/") + path);
       
       File pathDir = getTemporaryFolder("agentcache.0.0_1");
       File libfile = new File(pathDir, path.substring(path.lastIndexOf("/"), path.length()));
       
       if (!libfile.exists()) {
         libfile.deleteOnExit();
         InputStream in = url.openStream();
         OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));
         
 
         byte[] buffer = new byte[' '];
         int len; while ((len = in.read(buffer)) > -1) {
           out.write(buffer, 0, len);
         }
         out.flush();
         out.close();
         in.close();
       }
     } catch (IOException x) {
       if (!tempFolderUpdatedToHome) {
         useHomeFolderForTempFiles();
         unpack(path);
       }
       
       if (x.getMessage().contains("Permission denied")) {
         log.error("Unable to unpack binaries because javOSize doesn't have write permissions at default Java temp folder.We recommend you to start javOSize indicating temporary folder with write permissions.\nFor example: java -Djava.io.tmpdir=/MyPath/MyNewTmpFolder -jar javosize.jar <PID>");
         
 
         log.error("Unable to unpack binaries. Detail: " + x, x);
       } else {
         log.fatal("Unable to unpack binaries. Detail: " + x, x);
       }
       throw new RuntimeException("Could not unpack binaries. Detail: " + x, x);
     }
   }
   
 
 
 
 
 
 
   public static byte[] getClassBytes(String name)
   {
     Class c = getClassFromInstrumentation(name);
     if (c == null) {
       log.warn("Class not found. ");
       return null;
     }
     try {
       log.debug("Class " + name + " found. Trying to obtain bytecode...");
       return getClassBytes(c);
     } catch (Throwable e) {
       log.error("Problem loading class for hotdecompilation: " + name + " error: " + e, e); }
     return null;
   }
   
 
 
 
 
 
 
 
   public static byte[] getClassBytes(Class clazz)
     throws IOException
   {
     String name = clazz.getName().replace('.', '/') + ".class";
     ClassLoader cl = clazz.getClassLoader();
     if (cl == null) {
       cl = ClassLoader.getSystemClassLoader();
     }
     InputStream iStream = cl.getResourceAsStream(name);
     
 
 
 
 
 
     if (iStream == null)
     {
       log.warn("JavOSize hasn't found the class " + name + " using an standard ClassLoader method. Trying to use alternative methods to recover the class file. ");
       
 
       Class play = getClassFromInstrumentation("play.Play");
       
       if (play != null) {
         log.info("Play framework detected. Trying to recover the class " + name + " using the Play ClassLoader.");
         
 
 
 
 
 
         try
         {
           Method m = play.getMethod("getFile", new Class[] { String.class });
           File file = (File)m.invoke(null, new Object[] { "precompiled/java/" + name });
           
           if (file.exists()) {
             log.info("File of class " + name + " found at \"precompiled/java/\" folder of Play Application.");
             iStream = new FileInputStream(file);
           }
           else
           {
             log.info("File of class " + name + " doesn't exist at \"precompiled/java/\" folder of Play Application.");
           }
         } catch (Exception e) {
           log.error("Error trying to recover class " + name + ": " + e, e);
         }
       }
     }
     
 
 
     byte[] buff;
     try
     {
       ByteArrayOutputStream oStream = new ByteArrayOutputStream();
       buff = new byte[1024];
       int len = 0;
       while ((len = iStream.read(buff)) != -1) {
         oStream.write(buff, 0, len);
       }
       
       log.debug("Bytes found for class " + name + " [" + oStream.size() + "bytes]");
       
       return oStream.toByteArray();
     } catch (Exception e) { 
       log.error("Exception trying to recover the class " + name + ": " + e, e);
       return null;
     } finally {
       iStream.close();
     }
   }
   
 
 
 
 
   public static String getVersionOfCompilation(Class clazz)
     throws Exception
   {
     return getVersionOfCompilation(getClassBytes(clazz));
   }
   
 
 
 
 
 
   public static String getVersionOfCompilation(byte[] bytecode)
     throws Exception
   {
     String result = null;
     
 
 
 
 
 
 
 
 
 
 
     try
     {
       DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytecode));
       
       int magic = in.readInt();
       if (magic != -889275714) {
         throw new Exception("Unable to recover Java version from bytecode. Magic number not present at bytecode!");
       }
       
       int minor = in.readUnsignedShort();
       int major = in.readUnsignedShort();
       in.close();
       
       switch (major) {
       case 45: 
         result = "1.1";
         break;
       case 46: 
         result = "1.2";
         break;
       case 47: 
         result = "1.3";
         break;
       case 48: 
         result = "1.4";
         break;
       case 49: 
         result = "1.5";
         break;
       case 50: 
         result = "1.6";
         break;
       case 51: 
         result = "1.7";
         break;
       case 52: 
         result = "1.8";
       }
       
     }
     catch (Throwable th)
     {
       throw new Exception("Error recovering Java compilation version: " + th, th);
     }
     return result;
   }
   
   public static enum Platform
   {
     LINUX,  WINDOWS,  MAC,  SOLARIS;
     
     private Platform() {}
     public static Platform getPlatform() { String os = System.getProperty("os.name").toLowerCase();
       if (os.indexOf("win") >= 0) {
         return WINDOWS;
       }
       if ((os.indexOf("nix") >= 0) || (os.indexOf("nux") >= 0) || (os.indexOf("aix") >= 0)) {
         return LINUX;
       }
       if (os.indexOf("mac") >= 0) {
         return MAC;
       }
       if (os.indexOf("sunos") >= 0)
         return SOLARIS;
       return null;
     }
     
     public static boolean is64Bit() {
       String osArch = System.getProperty("os.arch");
       return ("amd64".equals(osArch)) || ("x86_64".equals(osArch));
     }
   }
 }


