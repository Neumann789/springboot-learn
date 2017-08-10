 package com.javosize.actions;
 
 import java.lang.management.ManagementFactory;
 import java.util.StringTokenizer;
 import javax.management.Attribute;
 import javax.management.MBeanInfo;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.MBeanServer;
 import javax.management.MalformedObjectNameException;
 import javax.management.ObjectName;
 
 
 
 
 public class MbeanOperationExecutionAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static final String STRING_TYPE = "java.lang.String";
   private static final String BOOLEAN_TYPE = "boolean";
   private static final String INT_TYPE = "int";
   private static final String LONG_TYPE = "long";
   private static final String FLOAT_TYPE = "float";
   private static final String DOUBLE_TYPE = "double";
   private String command = "";
   private String mBeanName = "";
   private String operationName = "";
   private Object[] parameters;
   private String[] signature;
   
   private String executeSet()
   {
     try {
       if ((this.mBeanName == "") || (this.command == "")) {
         return "Please, check the sintax of the command to be executed\n";
       }
       
       String setter = this.command.substring(this.command.lastIndexOf(".") + 1);
       String attValue = setter.substring(setter.indexOf("(") + 1, setter.lastIndexOf(")"));
       if ((attValue.startsWith("\"")) && (attValue.endsWith("\""))) {
         attValue = attValue.substring(1, attValue.length() - 1);
       }
       String attribute = setter.substring(3, setter.indexOf("("));
       MBeanServer server = ManagementFactory.getPlatformMBeanServer();
       Object attType = server.getAttribute(new ObjectName(this.mBeanName), attribute);
       String attClass = attType.getClass().getName();
       
       if (attClass.contains("String")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, attValue));
       } else if (attClass.contains("Bool")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, Boolean.valueOf(new Boolean(attValue).booleanValue())));
       } else if (attClass.contains("Int")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, Integer.valueOf(new Integer(attValue).intValue())));
       } else if (attClass.contains("Long")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, Long.valueOf(new Long(attValue).longValue())));
       } else if (attClass.contains("Float")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, Float.valueOf(new Float(attValue).floatValue())));
       } else if (attClass.contains("Double")) {
         server.setAttribute(new ObjectName(this.mBeanName), new Attribute(attribute, Double.valueOf(new Double(attValue).doubleValue())));
       }
     }
     catch (Exception e)
     {
       return "Remote ERROR executing operation to mbean: " + e.toString() + "\n";
     }
     return "";
   }
   
   private String executeOperation()
   {
     try {
       if ((this.mBeanName == "") || (this.operationName == "")) {
         return "Please, check the sintax of the command to be executed\n";
       }
       
       MBeanServer server = ManagementFactory.getPlatformMBeanServer();
       MBeanInfo mbi = server.getMBeanInfo(new ObjectName(this.mBeanName));
       MBeanOperationInfo[] mbois = mbi.getOperations();
       
       boolean operationExists = false;
       
       for (MBeanOperationInfo mboi : mbois) {
         if (mboi.getName().equals(this.operationName)) {
           operationExists = true;
           MBeanParameterInfo[] mbpis = mboi.getSignature();
           setParams(mbpis);
         }
       }
       
       if (!operationExists) {
         return "Please, check the name of the operation\n";
       }
       
       if ((this.parameters == null) || (this.signature == null)) {
         return "Please, check the sintax of the command to be executed. Something is wrong with the parameters\n";
       }
       
       Object result = server.invoke(new ObjectName(this.mBeanName), this.operationName, this.parameters, this.signature);
       
       if (result != null) {
         return result.toString() + "\n";
       }
       
       return "";
     } catch (MalformedObjectNameException mone) {
       return "Please, check the name of the mBean\n";
     } catch (Throwable th) {
       if (th.toString().contains("File exists"))
         return "The output file alreay exists. Please, change the name\n";
       if (th.toString().contains("Permission denied")) {
         return "Permission denied. I can't create the output file\n";
       }
       return "Remote ERROR executing operation to mbean: " + th.toString() + "\n";
     }
   }
   
   private void setMbeanAndOperationName() {
     try {
       String signature = this.command.substring(0, this.command.indexOf("("));
       this.mBeanName = signature.substring(0, signature.lastIndexOf("."));
       this.operationName = signature.substring(signature.lastIndexOf(".") + 1);
     } catch (Exception e) {
       this.mBeanName = "";
       this.operationName = "";
     }
   }
   
   private void setParams(MBeanParameterInfo[] mbpis) {
     try {
       Object[] paramsArray = new Object[mbpis.length];
       String[] sign = new String[mbpis.length];
       String params = this.command.substring(this.command.indexOf("(") + 1, this.command.lastIndexOf(")"));
       StringTokenizer strTok = new StringTokenizer(params, ",");
       String parameterType = "";
       String param = "";
       int paramsCount = 0;
       while (strTok.hasMoreTokens()) {
         param = strTok.nextToken();
         if ((param.startsWith("\"")) && (param.endsWith("\""))) {
           param = param.substring(1, param.length() - 1);
         }
         parameterType = mbpis[paramsCount].getType();
         if (parameterType == "java.lang.String") {
           paramsArray[paramsCount] = param;
           sign[paramsCount] = String.class.getName();
         } else if (parameterType == "boolean") {
           paramsArray[paramsCount] = Boolean.valueOf(param);
           sign[paramsCount] = Boolean.TYPE.getName();
         } else if (parameterType == "int") {
           paramsArray[paramsCount] = Integer.valueOf(param);
           sign[paramsCount] = Integer.TYPE.getName();
         } else if (parameterType == "long") {
           paramsArray[paramsCount] = Long.valueOf(param);
           sign[paramsCount] = Long.TYPE.getName();
         } else if (parameterType == "float") {
           paramsArray[paramsCount] = Float.valueOf(param);
           sign[paramsCount] = Float.TYPE.getName();
         } else if (parameterType == "double") {
           paramsArray[paramsCount] = Double.valueOf(param);
           sign[paramsCount] = Double.TYPE.getName();
         }
         paramsCount++;
       }
       if (paramsCount != mbpis.length) {
         this.parameters = null;
         this.signature = null;
       } else {
         this.parameters = paramsArray;
         this.signature = sign;
       }
     } catch (Exception e) {
       this.parameters = null;
       this.signature = null;
     }
   }
   
   public String execute()
   {
     setMbeanAndOperationName();
     if ((this.operationName.length() >= 3) && (this.operationName.substring(0, 3).trim().equals("set"))) {
       return executeSet();
     }
     return executeOperation();
   }
   
   public void setCommand(String command)
   {
     this.command = command;
   }
 }


