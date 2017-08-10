 package com.javosize.communication.client;
 
 import com.javosize.log.Log;
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.net.URLEncoder;
 import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
 
 
 
 
 
 
 
 
 public class RestAPIClient
 {
   private static Log log = new Log(RestAPIClient.class.getName());
   
   private static String collectorURL = "https://www.javosize.com/javosize";
   
 
 
 
 
 
 
 
 
 
 
 
   public static String executeRestApiCall(String method, String path, HashMap<String, String> params)
     throws Exception
   {
     log.trace("Starting API call [Method=" + method + "][Path=" + path + "]");
     
     String urlString = collectorURL + path;
     String paramsString = getParamsString(params);
     
     log.trace("Generated API call params [Method=" + method + "][Path=" + path + "][Params=" + paramsString + "]");
     
 
     if ((method.equals("GET")) && (paramsString != null) && (paramsString.length() > 0)) {
       urlString = urlString + "?" + paramsString;
     }
     
     URL url = new URL(urlString);
     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
     conn.setRequestMethod(method);
     conn.setRequestProperty("Accept", "application/json");
     
 
     if ((method.equals("POST")) && (paramsString != null) && (paramsString.length() > 0)) {
       conn.setDoOutput(true);
       
       OutputStream out = conn.getOutputStream();
       out.write(paramsString.getBytes("UTF-8"));
       out.flush();
       out.close();
     }
     
 
 
     if (conn.getResponseCode() != 200) {
       throw new Exception("Connection error - [Status=" + conn.getResponseCode() + "][Error=" + readInputStream(conn.getErrorStream()) + "]");
     }
     
     String result = readInputStream(conn.getInputStream());
     
     conn.disconnect();
     
     log.trace("API call finished properly [Method=" + method + "][Path=" + path + "]");
     
     return result;
   }
   
   private static String getParamsString(HashMap<String, String> params)
     throws UnsupportedEncodingException
   {
     if ((params == null) || (params.size() == 0)) {
       return null;
     }
     
     StringBuilder result = new StringBuilder();
     boolean first = true;
     for (Map.Entry<String, String> entry : params.entrySet()) {
       if (first) {
         first = false;
       } else {
         result.append("&");
       }
       result.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
       result.append("=");
       result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
     }
     
     return result.toString();
   }
   
 
 
 
 
 
 
 
   private static String readInputStream(InputStream input)
     throws IOException
   {
     String ret = null;
     
     if (input != null) {
       BufferedReader br = new BufferedReader(new InputStreamReader(input));
       StringBuffer result = new StringBuffer();
       String line;
       while ((line = br.readLine()) != null) {
         result.append(line);
       }
       br.close();
       input.close();
       
       ret = result.toString();
     }
     
     return ret;
   }
 }


