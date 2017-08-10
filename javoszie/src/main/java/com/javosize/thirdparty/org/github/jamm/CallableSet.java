 package com.javosize.thirdparty.org.github.jamm;
 
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.Set;
 import java.util.concurrent.Callable;
 
 public class CallableSet
   implements Callable<Set<Object>>
 {
   public Set<Object> call()
     throws Exception
   {
     return Collections.newSetFromMap(new IdentityHashMap());
   }
 }


