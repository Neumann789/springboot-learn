 package com.javosize.print;
 
 import java.util.ArrayList;
 import java.util.List;
 
 public class TextReport
 {
   private static final String BULLET = "*";
   public static final String INDENT = "    ";
   private List<Section> sections = new ArrayList();
   
 
 
 
   public void addSection(String title, String[] content)
   {
     this.sections.add(new Section(title, content));
   }
   
   public String toString() {
     StringBuffer sb = new StringBuffer();
     for (Section s : this.sections) {
       sb.append("* " + s.getTitle() + "\n");
       String[] content = s.getContent();
       for (int i = 0; i < content.length; i++) {
         if (content[i] != null)
           sb.append("    " + content[i].replace("\n", "\n    ") + "\n");
       }
     }
     return sb.toString();
   }
 }


