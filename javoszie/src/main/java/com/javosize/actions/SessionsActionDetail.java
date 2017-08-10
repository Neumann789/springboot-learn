 package com.javosize.actions;
 
 import com.javosize.agent.HttpSessionHelperFactory;
 import com.javosize.agent.ReflectionUtils;
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.TextReport;
 import java.util.Enumeration;
 
 public class SessionsActionDetail
   extends Action
 {
   private static final long serialVersionUID = 3464512134721516345L;
   private String id;
   
   public SessionsActionDetail(String id)
   {
     this.id = id;
   }
   
   public String execute()
   {
     Class helper = HttpSessionHelperFactory.getSessionHelper();
     if (helper == null) {
       return "";
     }
     TextReport report = new TextReport();
     Object session = UserThreadSessionTracker.getSessionById(this.id);
     
     String numberOfElementString = (String)ReflectionUtils.invokeStaticMethod(helper, "getNumberOfElements", new Class[] { Object.class }, new Object[] { session });
     
     if (numberOfElementString == null) {
       return "";
     }
     
     int eleNum = Integer.valueOf(numberOfElementString).intValue();
     Enumeration<String> values = (Enumeration)ReflectionUtils.invokeStaticMethod(helper, "getAttributes", new Class[] { Object.class }, new Object[] { session });
     String[] results = new String[eleNum];
     int i = 0;
     while (values.hasMoreElements()) {
       String attrName = (String)values.nextElement();
       String value = (String)ReflectionUtils.invokeStaticMethod(helper, "getAttribute", new Class[] { Object.class, String.class }, new Object[] { session, attrName });
       results[(i++)] = (attrName + ":\t" + value);
     }
     
     report.addSection("Attributes", results);
     
     return report.toString() + "\n";
   }
 }


