 package com.javosize.recipes;
 
 import javax.xml.bind.annotation.adapters.XmlAdapter;
 
 public class XMLAdapterCDATA extends XmlAdapter<String, String>
 {
   private static final String INDENT = "          ";
   
   public String marshal(String arg0) throws Exception
   {
     return "<![CDATA[\n          " + arg0.replace("\n", "\n          ") + "]]>";
   }
   
   public String unmarshal(String arg0) throws Exception
   {
     return arg0;
   }
 }


