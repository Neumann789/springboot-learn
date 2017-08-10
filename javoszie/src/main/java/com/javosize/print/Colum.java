 package com.javosize.print;
 
 public class Colum
 {
   private String title;
   private int percentage;
   
   public Colum(String title, int percentage) {
     this.title = title;
     this.percentage = percentage;
   }
   
   public String getTitle() {
     return this.title;
   }
   
   public void setTitle(String title) { this.title = title; }
   
   public int getPercentage()
   {
     return this.percentage;
   }
   
   public void setPercentage(int percentage) {
     this.percentage = percentage;
   }
 }


