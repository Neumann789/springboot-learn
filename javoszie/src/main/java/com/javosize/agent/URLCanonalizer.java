 package com.javosize.agent;
 
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.net.URLEncoder;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 public class URLCanonalizer
 {
   public static String getCanonicalURL(String url, boolean skipParamValues, boolean pathOnly)
   {
     URL canonicalURL = getCanonicalURL(url, null, skipParamValues);
     if (canonicalURL != null) {
       if (pathOnly) {
         return canonicalURL.getPath();
       }
       return canonicalURL.toExternalForm();
     }
     
     return null;
   }
   
   public static URL getCanonicalURL(String href, String context, boolean skipParamValues)
   {
     try {
       URL canonicalURL;
       if (context == null) {
         canonicalURL = new URL(href);
       } else {
         canonicalURL = new URL(new URL(context), href);
       }
       
       String path = canonicalURL.getPath();
       
 
 
 
 
 
       path = new URI(path).normalize().toString();
       
 
 
 
       int idx = path.indexOf("//");
       while (idx >= 0) {
         path = path.replace("//", "/");
         idx = path.indexOf("//");
       }
       
 
 
 
       while (path.startsWith("/../")) {
         path = path.substring(3);
       }
       
 
 
 
       path = path.trim();
       
       SortedMap<String, String> params = createParameterMap(canonicalURL.getQuery(), skipParamValues);
       String queryString;
       if ((params != null) && (params.size() > 0)) {
         String canonicalParams = canonicalize(params);
         queryString = "?" + canonicalParams;
       } else {
         queryString = "";
       }
       
 
 
 
       if (path.length() == 0) {
         path = "/" + path;
       }
       
 
 
 
       int port = canonicalURL.getPort();
       if (port == canonicalURL.getDefaultPort()) {
         port = -1;
       }
       
 
 
 
       String protocol = canonicalURL.getProtocol().toLowerCase();
       String host = canonicalURL.getHost().toLowerCase();
       String pathAndQueryString = normalizePath(path) + queryString;
       
       return new URL(protocol, host, port, pathAndQueryString);
     }
     catch (MalformedURLException ex) {
       return null;
     } catch (URISyntaxException ex) {}
     return null;
   }
   
 
 
 
 
 
 
 
   private static SortedMap<String, String> createParameterMap(String queryString, boolean skipParamValues)
   {
     if ((queryString == null) || (queryString.isEmpty())) {
       return null;
     }
     
     String[] pairs = queryString.split("&");
     Map<String, String> params = new java.util.HashMap(pairs.length);
     
     for (String pair : pairs) {
       if (pair.length() != 0)
       {
 
 
         String[] tokens = pair.split("=", 2);
         switch (tokens.length) {
         case 1: 
           if (pair.charAt(0) == '=') {
             params.put("", tokens[0]);
           } else {
             params.put(tokens[0], "");
           }
           break;
         case 2: 
           if (skipParamValues) {
             params.put(tokens[0], "*");
           } else
             params.put(tokens[0], tokens[1]);
           break;
         }
       }
     }
     return new TreeMap(params);
   }
   
 
 
 
 
 
 
   private static String canonicalize(SortedMap<String, String> sortedParamMap)
   {
     if ((sortedParamMap == null) || (sortedParamMap.isEmpty())) {
       return "";
     }
     
     StringBuffer sb = new StringBuffer(100);
     for (Map.Entry<String, String> pair : sortedParamMap.entrySet()) {
       String key = ((String)pair.getKey()).toLowerCase();
       if ((!key.equals("jsessionid")) && (!key.equals("phpsessid")) && (!key.equals("aspsessionid")))
       {
 
         if (sb.length() > 0) {
           sb.append('&');
         }
         sb.append(percentEncodeRfc3986((String)pair.getKey()));
         if (!((String)pair.getValue()).isEmpty()) {
           sb.append('=');
           sb.append(percentEncodeRfc3986((String)pair.getValue()));
         }
       } }
     return sb.toString();
   }
   
 
 
 
 
 
 
 
   private static String percentEncodeRfc3986(String string)
   {
     try
     {
       string = string.replace("+", "%2B");
       string = URLDecoder.decode(string, "UTF-8");
       string = URLEncoder.encode(string, "UTF-8");
       return string.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
     } catch (Exception e) {}
     return string;
   }
   
   private static String normalizePath(String path)
   {
     return path.replace("%7E", "~").replace(" ", "%20");
   }
 }


