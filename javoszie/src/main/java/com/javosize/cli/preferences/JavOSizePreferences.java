 package com.javosize.cli.preferences;
 
 import com.javosize.cli.Environment;
 import com.javosize.log.Log;
 import com.javosize.recipes.Repository;
 import com.javosize.scheduler.Scheduler;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.OutputStream;
 import java.util.UUID;
 
 
 
 
 
 
 
 
 
 
 
 public class JavOSizePreferences
 {
   private static Log log = new Log(JavOSizePreferences.class.getSimpleName());
   
   public static final String JVM_PREFERENCES_FOLDER_PROPERTY = "javosize.preferences.folder";
   
   private static final long LOCK_MAX_WAIT = 1000L;
   
   private static final long LOCK_RETRY_SLEEP = 50L;
   private static final String LOCK_NOT_LOCKED_STRING = "NL";
   private static final String REPOSITORY_INFO_FILE = "javosize_repository.bin";
   private static final String REPOSITORY_INFO_BAK_FILE = "javosize_repository.bak";
   private static final String REPOSITORY_INFO_LOCK_FILE = "javosize_repository.lock";
   private static boolean repositoryNotFound = false;
   
   private static final String SCHEDULER_INFO_FILE = "javosize_scheduler.bin";
   private static final String SCHEDULER_INFO_BAK_FILE = "javosize_scheduler.bak";
   private static final String SCHEDULER_INFO_LOCK_FILE = "javosize_scheduler.lock";
   private static boolean schedulerNotFound = false;
   
   private static final String ENVIRONMENT_INFO_FILE = "javosize_environment.bin";
   private static final String ENVIRONMENT_INFO_BAK_FILE = "javosize_environment.bak";
   private static final String ENVIRONMENT_INFO_LOCK_FILE = "javosize_environment.lock";
   private static boolean environmentNotFound = false;
   
   private static String preferencesFolder = null;
   
   public static String getUserDataDirectory() {
     if (preferencesFolder == null) {
       loadPreferencesFolder();
     }
     return preferencesFolder;
   }
   
   private static void loadPreferencesFolder() {
     preferencesFolder = System.getProperty("user.home") + File.separator + ".javosize" + File.separator;
     if (System.getProperty("javosize.preferences.folder") != null) {
       preferencesFolder = System.getProperty("javosize.preferences.folder");
       if (!preferencesFolder.endsWith(File.separator)) {
         preferencesFolder += File.separator;
       }
     }
     
     File directory = new File(preferencesFolder);
     directory.mkdirs();
   }
   
   public static synchronized Repository loadRepo() {
     log.trace("Loading Recipes Repository...");
     Repository repository = (Repository)readObjectFromFile(
       getUserDataDirectory() + "javosize_repository.bin", 
       getUserDataDirectory() + "javosize_repository.lock", 
       getUserDataDirectory() + "javosize_repository.bak");
     
     if (repository == null) {
       if (repositoryNotFound) {
         log.warn("No previous repository info found at " + getUserDataDirectory() + "javosize_repository.bin" + ".");
       }
       log.trace("No previous repository info found. Created a new one.");
       repositoryNotFound = true;
       repository = new Repository();
     } else {
       repositoryNotFound = false;
       log.trace("Loaded OK!");
     }
     return repository;
   }
   
   public static synchronized void persistRepo(Repository repository) {
     log.trace("Storing Recipes Repository...");
     if (repository != null) {
       if (writeObjectToFile(repository, 
         getUserDataDirectory() + "javosize_repository.bin", 
         getUserDataDirectory() + "javosize_repository.lock", 
         getUserDataDirectory() + "javosize_repository.bak"))
       {
 
         log.trace("Stored ok!"); }
     } else if (repository == null) {
       log.trace("No repository info to store.");
     }
   }
   
   public static synchronized Scheduler loadScheduler()
   {
     log.trace("Loading Scheduler info...");
     Scheduler scheduler = (Scheduler)readObjectFromFile(
       getUserDataDirectory() + "javosize_scheduler.bin", 
       getUserDataDirectory() + "javosize_scheduler.lock", 
       getUserDataDirectory() + "javosize_scheduler.bak");
     
     if (scheduler == null) {
       if (schedulerNotFound) {
         log.warn("No previous scheduler info found at " + getUserDataDirectory() + "javosize_scheduler.bin" + ".");
       }
       log.trace("No previous scheduler info found. Created a new one.");
       scheduler = new Scheduler();
       schedulerNotFound = true;
     } else {
       schedulerNotFound = false;
       log.trace("Loaded OK!");
     }
     return scheduler;
   }
   
   public static synchronized void persistScheduler(Scheduler scheduler) {
     log.trace("Storing Scheduler info...");
     
     if (scheduler != null) {
       if (writeObjectToFile(scheduler, 
         getUserDataDirectory() + "javosize_scheduler.bin", 
         getUserDataDirectory() + "javosize_scheduler.lock", 
         getUserDataDirectory() + "javosize_scheduler.bak"))
       {
 
         log.trace("Stored ok!"); }
     } else if (scheduler == null) {
       log.trace("No scheduler info to store.");
     }
   }
   
   public static synchronized Environment loadEnvironment()
   {
     log.trace("Loading Environment info...");
     Environment environment = (Environment)readObjectFromFile(
       getUserDataDirectory() + "javosize_environment.bin", 
       getUserDataDirectory() + "javosize_environment.lock", 
       getUserDataDirectory() + "javosize_environment.bak");
     
     if (environment == null) {
       if (environmentNotFound) {
         log.trace("No previous scheduler info found at " + getUserDataDirectory() + "javosize_scheduler.bin" + ".");
       }
       log.trace("No previous scheduler info found. Created a new one.");
       environment = new Environment();
       environmentNotFound = true;
     } else {
       environmentNotFound = false;
       log.trace("Loaded OK!");
     }
     return environment;
   }
   
   public static synchronized void persistEnvironment(Environment environment) {
     log.trace("Storing Environment info...");
     
     if (environment != null) {
       if (writeObjectToFile(environment, 
         getUserDataDirectory() + "javosize_environment.bin", 
         getUserDataDirectory() + "javosize_environment.lock", 
         getUserDataDirectory() + "javosize_environment.bak"))
       {
 
         log.trace("Stored ok!"); }
     } else if (environment == null) {
       log.trace("No Environment info to store.");
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   private static boolean writeObjectToFile(Object obj, String fileName, String lockFileName, String backupFileName)
   {
     if (obj == null) {
       return true;
     }
     
     boolean storedOK = false;
     try {
       String lockID = getLockWithRetry(lockFileName, true);
       if (lockID != null) {
         copyFile(fileName, backupFileName);
         storedOK = writeObjectToFile(obj, fileName);
         releaseLock(lockFileName, lockID);
       } else {
         log.warn("Unable to get lock " + lockFileName + ". Information not stored. ");
       }
     } catch (Throwable th) {
       log.error("Error trying to update information at " + fileName + ": " + th, th);
     }
     return storedOK;
   }
   
 
 
 
 
 
 
   private static boolean writeObjectToFile(Object obj, String fileName)
   {
     FileOutputStream fout = null;
     ObjectOutputStream oos = null;
     boolean stored = false;
     try {
       fout = new FileOutputStream(fileName);
       oos = new ObjectOutputStream(fout);
       oos.writeObject(obj);
       oos.flush();
       oos.close();
       return true;
     } catch (Throwable th) {
       log.error("Error storing at file " + fileName + ": " + th, th);
     } finally {
       if (oos != null) {
         try { oos.close();
         }
         catch (Throwable localThrowable3) {}
       }
     }
				return stored;
   }
   
 
 
 
 
 
 
 
 
   private static Object readObjectFromFile(String fileName, String lockFileName, String backupFileName)
   {
     Object result = null;
     try {
       String lockID = getLockWithRetry(lockFileName, true);
       if (lockID != null) {
         result = readObjectFromFile(fileName);
         if (result == null) {
           log.trace("No info recovered from " + fileName + ". Trying again from backup file " + backupFileName + ".");
           result = readObjectFromFile(backupFileName);
           if (result == null) {
             log.trace("No info recovered from backup file " + backupFileName + ".");
           }
         }
         releaseLock(lockFileName, lockID);
       } else {
         log.debug("No info recovered. Unable to get lock " + lockFileName + ".");
       }
     } catch (Throwable th) {
       log.debug("Error loading file " + fileName + ": " + th, th);
     }
     
     return result;
   }
   
 
 
 
 
 
   private static Object readObjectFromFile(String fileName)
   {
     return readObjectFromFile(fileName, 1, null);
   }
   
 
 
 
 
 
 
   private static Object readObjectFromFile(String fileName, int retryOnReadError, Object resultAfterRetry)
   {
     Object obj = null;
     FileInputStream in = null;
     ObjectInputStream ois = null;
     try {
       File f = new File(fileName);
       if (f.exists()) {
         in = new FileInputStream(fileName);
         ois = new ObjectInputStream(in); }
                return ois.readObject();
     }
     catch (FileNotFoundException e) {
       log.trace("Preferences file " + fileName + " not found.");
     } catch (Throwable th) {
       log.error("Error reading object from file " + fileName + ": " + th, th);
       if (retryOnReadError > 0) {
         try {
           long timeToSleep = (long)(50.0D * Math.random());
           Thread.sleep(timeToSleep);
         }
         catch (Throwable localThrowable3) {}
         
         //obj = readObjectFromFile(fileName, retryOnReadError--, resultAfterRetry);
       } else {
         obj = resultAfterRetry;
       }
     } finally {
       if (ois != null) {
         try { ois.close();
         }
         catch (Throwable localThrowable5) {}
       }
     }
			  return obj;
   }
   
 
 
 
 
 
   private static void copyFile(String source, String dest)
   {
     InputStream is = null;
     OutputStream os = null;
     try {
       is = new FileInputStream(source);
       os = new FileOutputStream(dest);
       byte[] buffer = new byte['Ѐ'];
       int length;
       while ((length = is.read(buffer)) > 0) {
         os.write(buffer, 0, length);
       }
       return;
     }
     catch (FileNotFoundException localFileNotFoundException) {}catch (Throwable th) {
       log.debug("Exception creating backup of file " + source + ": " + th, th);
     } finally {
       if (is != null)
         try { is.close();
         } catch (Throwable localThrowable7) {}
       if (os != null) {
         try { os.close();
         }
         catch (Throwable localThrowable8) {}
       }
     }
   }
   
 
 
 
 
 
   private static String getLock(String lockFileName, boolean doubleCheck)
   {
     String currentLockValue = (String)readObjectFromFile(lockFileName, 5, "ReadError");
     String newLockValue = "" + UUID.randomUUID().toString() + "|" + System.currentTimeMillis();
     if ((currentLockValue == null) || 
       (currentLockValue.equals("NL")) || 
       (getLongFromLockString(currentLockValue) + 10000L < System.currentTimeMillis()))
     {
       if (doubleCheck) {
         try {
           long timeToSleep = (long)(50.0D * Math.random());
           Thread.sleep(timeToSleep);
         }
         catch (Throwable localThrowable) {}
         
         currentLockValue = (String)readObjectFromFile(lockFileName, 5, "ReadError");
         if ((currentLockValue != null) && 
           (!currentLockValue.equals("NL")) && 
           (getLongFromLockString(currentLockValue) + 10000L >= System.currentTimeMillis()))
         {
 
           return null;
         }
       }
       
       log.trace("Locking file " + lockFileName + " with id " + newLockValue);
       if (writeObjectToFile("" + newLockValue, lockFileName)) {
         return newLockValue;
       }
     }
     return null;
   }
   
 
 
 
 
 
 
   private static long getLongFromLockString(String lockID)
   {
     try
     {
       if (lockID == null) {
         return -1L;
       }
       String longValue = lockID.substring(lockID.indexOf("|") + 1, lockID.length());
       return Long.valueOf(longValue).longValue();
     }
     catch (Throwable localThrowable) {}
     
     return -1L;
   }
   
 
 
 
 
 
 
   private static String getLockWithRetry(String lockFileName, boolean retry)
   {
     String lockID = getLock(lockFileName, false);
     if ((lockID == null) && (retry)) {
       log.trace("File " + lockFileName + " is locked. Retrying...");
       long firstTS = System.currentTimeMillis();
       while ((lockID == null) && (firstTS + 1000L > System.currentTimeMillis())) {
         try {
           Thread.sleep(50L);
         }
         catch (Throwable localThrowable) {}
         
         lockID = getLock(lockFileName, true);
       }
       if (lockID == null) {
         log.trace("File " + lockFileName + " continues locked. Unable to lock it!!");
       } else {
         log.trace("File " + lockFileName + " locked after waiting for some milliseconds.");
       }
     }
     return lockID;
   }
   
   private static void releaseLock(String lockFileName, String expectedLockValue) {
     String lockID = (String)readObjectFromFile(lockFileName, 5, "ReadError");
     if ((lockID == null) || (!lockID.equals(expectedLockValue))) {
       log.warn("Error releasing lock " + lockFileName + ". Possible file corruption!! [Found=" + lockID + "][Expected=" + expectedLockValue + "]");
     }
     else if (writeObjectToFile("NL", lockFileName)) {
       log.trace("Lock " + lockFileName + " released!");
     } else {
       log.warn("Unable to update " + lockFileName + " file. Lock not released!");
     }
   }
 }


