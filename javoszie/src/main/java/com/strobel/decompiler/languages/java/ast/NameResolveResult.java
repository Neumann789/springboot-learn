 package com.strobel.decompiler.languages.java.ast;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class NameResolveResult
 {
   public abstract List<Object> getCandidates();
   
   public abstract NameResolveMode getMode();
   
   public boolean hasMatch()
   {
     return !getCandidates().isEmpty();
   }
   
   public boolean isAmbiguous() {
     return getCandidates().size() > 1;
   }
 }


