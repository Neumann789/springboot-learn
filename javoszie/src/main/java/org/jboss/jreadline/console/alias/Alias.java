 package org.jboss.jreadline.console.alias;
 
 
 
 
 public class Alias
   implements Comparable
 {
   private String name;
   
 
 
   private String value;
   
 
 
 
   public Alias(String name, String value)
   {
     this.name = name;
     this.value = value;
   }
   
   public String getName() {
     return this.name;
   }
   
   public String getValue() {
     return this.value;
   }
   
   public boolean equals(Object o)
   {
     return ((o instanceof Alias)) && (((Alias)o).getName().equals(getName()));
   }
   
   public int hashCode()
   {
     return 9320012;
   }
   
 
   public String toString()
   {
     return getName() + "='" + getValue() + "'";
   }
   
   public int compareTo(Object o)
   {
     return getName().compareTo(((Alias)o).getName());
   }
 }


