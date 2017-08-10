 package com.javosize.recipes;
 
 import com.javosize.cli.Main;
 import com.javosize.cli.preferences.JavOSizePreferences;
 import com.javosize.log.Log;
 import java.io.BufferedReader;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.security.CodeSource;
 import java.security.ProtectionDomain;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;
 
 public class Repository implements java.io.Serializable
 {
   private static final long serialVersionUID = -9053088003607376440L;
   private Map<String, Recipe> recipes = new ConcurrentHashMap();
   private static volatile transient Repository repository = null;
   
   private static Log log = new Log(Repository.class.getName());
   
   static {
     try {
       repository = JavOSizePreferences.loadRepo();
       loadDefaultRecipes();
     } catch (Throwable th) {
       log.error("ERROR loading Recipes Repository: " + th, th);
     }
   }
   
 
 
   public static synchronized Repository getRepository()
     throws IOException
   {
     return getRepository(false);
   }
   
   public static synchronized Repository getRepository(boolean forceReload) throws IOException {
     if ((repository != null) && (!forceReload)) {
       return repository;
     }
     repository = JavOSizePreferences.loadRepo();
     return repository;
   }
   
   public static synchronized boolean addRecipe(Recipe r) throws IOException {
     return addRecipe(r, false);
   }
   
   public static synchronized boolean addRecipe(Recipe r, boolean overwrite) throws IOException {
     Map<String, Recipe> recipes = getRepository(true).getRecipes();
     if ((overwrite) || (!recipes.containsKey(r.getName()))) {
       getRepository().getRecipes().put(r.getName(), r);
       JavOSizePreferences.persistRepo(getRepository());
       return true;
     }
     log.trace("Recipe " + r.getName() + " not loaded because it already exists at repository.");
     return false;
   }
   
   public static synchronized void removeRecipe(Recipe r) throws IOException
   {
     getRepository(true).getRecipes().remove(r.getName());
     JavOSizePreferences.persistRepo(getRepository());
   }
   
   public static Recipe getRecipe(String name) throws IOException {
     return (Recipe)getRepository(true).getRecipes().get(name);
   }
   
   public static String importFromCloud(String url) throws MalformedURLException, IOException {
     if (importRecipe(url, false))
       return "Recipe loaded properly from " + url + ".\n";
     if (Main.askForConfirmation("Recipe already exists. Overwrite? [y/n]")) {
       importRecipe(url, true);
       return "Recipe updated properly from " + url + ".\n";
     }
     return "\nRecipe not updated.\n";
   }
   
   private static InputStream getRecipeISfromURL(String url) throws IOException
   {
     InputStream is = null;
     try {
       is = new URL(url).openStream();
     } catch (Throwable th) {
       log.trace("Failed to find file at URL " + url + ". Trying again assuming that it is file path. Error: " + th);
       try {
         is = new FileInputStream(url);
       } catch (Throwable th2) {
         log.trace("Failed to find file " + url + ". Error: " + th2);
         throw new IOException("Unable to find: " + url + ". Please, check that it is a valid URL or a correct file path.\n");
       }
     }
     return is;
   }
   
   public static boolean importRecipe(String url, boolean overwrite) throws MalformedURLException, IOException {
     if ((url == null) || (url.isEmpty())) {
       throw new IOException("Empty URL!");
     }
     
     if (url.trim().toLowerCase().endsWith(".xml")) {
       return importXmlRecipe(getRecipeISfromURL(url), overwrite);
     }
     return importJsonRecipe(getRecipeISfromURL(url), overwrite);
   }
   
   public static boolean importJsonRecipe(InputStream is, boolean overwrite) throws IOException
   {
     BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
     String jsonText = readAll(rd);
     Recipe r = Recipe.fromJSON(jsonText);
     return addRecipe(r, overwrite);
   }
   
   public static boolean importXmlRecipe(InputStream is, boolean overwrite) throws IOException {
     BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
     String xml = readAll(rd);
     Recipe r = Recipe.fromXML(xml);
     return addRecipe(r, overwrite);
   }
   
   private static String readAll(Reader rd) throws IOException {
     StringBuilder sb = new StringBuilder();
     int cp;
     while ((cp = rd.read()) != -1) {
       sb.append((char)cp);
     }
     return sb.toString();
   }
   
   public Map<String, Recipe> getRecipes() {
     return this.recipes;
   }
   
   private static synchronized void loadDefaultRecipes() throws IOException {
     log.trace("Loading default javOSize Recipes...");
     CodeSource src = Repository.class.getProtectionDomain().getCodeSource();
     if (src != null) {
       URL jar = src.getLocation();
       ZipInputStream zip = new ZipInputStream(jar.openStream());
       ZipEntry e;
       while ((e = zip.getNextEntry()) != null) {
         String name = e.getName();
         if ((name.startsWith("recipes/")) && ((name.endsWith(".json")) || (name.endsWith("xml")))) {
           try {
             boolean loaded = false;
             if (name.endsWith(".json")) {
               loaded = importJsonRecipe(Repository.class.getResourceAsStream("/" + name), true);
             } else {
               loaded = importXmlRecipe(Repository.class.getResourceAsStream("/" + name), true);
             }
             
             if (loaded) {
               log.trace("Loaded javOSize default recipe " + name.substring(8) + ". [" + name + "]");
             }
           } catch (Throwable th) {
             log.warn("Unable to load javOSize default recipe " + name + ": " + th);
           }
         }
       }
       log.trace("Loaded ok");
     }
     else {
       log.error("Unable to load recipes from Jar file");
     }
   }
 }


