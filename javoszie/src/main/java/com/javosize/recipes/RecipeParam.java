 package com.javosize.recipes;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 
 
 @XmlRootElement
 public class RecipeParam
   implements Serializable
 {
   private static final long serialVersionUID = 3762571087854456934L;
   private int id;
   private String description;
   
   public RecipeParam() {}
   
   public RecipeParam(int id, String description)
   {
     this.id = id;
     this.description = description;
   }
   
   public int getId() {
     return this.id;
   }
   
   @XmlElement
   public void setId(int id) {
     this.id = id;
   }
   
   public String getDescription() {
     return this.description;
   }
   
   @XmlElement
   public void setDescription(String description) {
     this.description = description;
   }
 }


