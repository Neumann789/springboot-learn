 package com.javosize.agent;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.log.Log;
 import java.lang.reflect.InvocationTargetException;
 import java.net.MalformedURLException;
 import java.net.URL;
 
 
 
 
 
 
 public class Aspect
 {
   private static Log log = new Log(Aspect.class.getName());
   
   public static void methodBegin(Object servlet, Object req, Object resp)
   {
     try {
       Thread th = Thread.currentThread();
       Object session = getSession(req);
       String sessionId = getSessionId(session);
       String userId = getUserId(session, req);
       String url = getURL(req);
       String app = getAppName(servlet, req, url);
       UserThreadSessionTracker.startTracking(th, session, sessionId, userId, url, app, servlet);
     } catch (Exception e) {
       log.error("Aspect methodBegin error: " + e, e);
     }
   }
   
 
   public static void methodEnd(Object servlet, Object req, Object resp)
   {
     try
     {
       Thread th = Thread.currentThread();
       Object session = getSession(req);
       String sessionId = getSessionId(session);
       String userId = getUserId(session, req);
       String url = getURL(req);
       String app = getAppName(servlet, req, url);
       UserThreadSessionTracker.stopTracking(th, session, sessionId, userId, url, app);
     } catch (Exception e) {
       log.error("Aspect methodEnd error: " + e, e);
     }
   }
   
 
 
 
   private static String getUserId(Object session, Object req)
   {
     String candidate = (String)ReflectionUtils.invoke(req, "getRemoteUser");
     if (candidate != null) {
       return candidate;
     }
     
     if (session == null) {
       return "";
     }
     
     String[] userCandidates = { "user", "login", "username", "login_id", "userid", "user_id", "usuario", "name", "User", "Login", "UserId", "userId", "userID" };
     for (String candidateName : userCandidates) {
       candidate = (String)ReflectionUtils.invoke(session, "getAttribute", candidateName);
       if (candidate != null) {
         return candidate;
       }
     }
     
     return "";
   }
   
   private static String getSessionId(Object session) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
     if (session == null) {
       return null;
     }
     return (String)ReflectionUtils.invoke(session, "getId");
   }
   
   private static Object getSession(Object req) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
     return ReflectionUtils.invoke(req, "getSession", new Class[] { Boolean.TYPE }, new Object[] { Boolean.valueOf(false) });
   }
   
 
 
 
 
   private static String getAppName(Object servlet, Object request, String url)
   {
     if (servlet != null)
     {
 
       Object context = ReflectionUtils.invoke(servlet, "getServletContext");
       if (context != null) {
         Object aux = ReflectionUtils.invoke(context, "getServletContextName");
         if ((aux != null) && (!"".equals(aux))) {
           return (String)aux;
         }
       }
     }
     
     Object session = ReflectionUtils.invoke(request, "getSession", new Class[] { Boolean.TYPE }, new Object[] { Boolean.FALSE });
     if (session != null) {
       Object context = ReflectionUtils.invoke(session, "getServletContext");
       if (context != null) {
         Object aux = ReflectionUtils.invoke(context, "getServletContextName");
         if ((aux != null) && (!"".equals(aux))) {
           return (String)aux;
         }
       }
     }
     
     String contextName = getContextFromUrl(url);
     if ((contextName != null) && (!"".equals(contextName))) {
       return contextName;
     }
     
     String aux = (String)ReflectionUtils.invoke(request, "getContextPath");
     if (aux != null) {
       int pos = aux.indexOf("/");
       if (pos >= 0) {
         contextName = aux.substring(pos);
       }
     }
     
     return contextName;
   }
   
   private static String getContextFromUrl(String urlString) {
     String path = urlString;
     if (urlString == null) {
       return null;
     }
     if ((urlString != null) && (urlString.toLowerCase().startsWith("http"))) {
       try {
         URL url = new URL(urlString);
         path = url.getPath();
       } catch (MalformedURLException e) {
         log.error("getContextName: Problem trying to get app name from URL: " + urlString + " :" + e, e);
       }
     }
     
     if ((path == null) || (path.equals(""))) {
       return "/";
     }
     
     int index = path.indexOf("/") + 1;
     path = path.substring(index);
     
     index = path.indexOf("/");
     if (index > 0) {
       String contextName = path.substring(0, index);
       return contextName;
     }
     return "/";
   }
   
   private static String getURL(Object request)
   {
     String ret = "NULL";
     if (request == null) {
       return ret;
     }
     
     StringBuffer url = (StringBuffer)ReflectionUtils.invoke(request, "getRequestURL");
     
     if (url == null) {
       String forwardUri = (String)ReflectionUtils.invoke(request, "getAttribute", new Class[] { String.class }, new Object[] { "javax.servlet.forward.request_uri" });
       String includeUri = (String)ReflectionUtils.invoke(request, "getAttribute", new Class[] { String.class }, new Object[] { "javax.servlet.include.request_uri" });
       if (forwardUri != null) {
         ret = forwardUri;
       } else if (includeUri != null) {
         ret = includeUri;
       } else {
         return ret;
       }
     } else {
       ret = url.toString();
     }
     
     return URLCanonalizer.getCanonicalURL(ret.toString(), true, true);
   }
 }


