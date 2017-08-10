 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Role<T>
 {
   public static final int ROLE_INDEX_BITS = 9;
   static final Role[] ROLES = new Role['Ȁ'];
   static final AtomicInteger NEXT_ROLE_INDEX = new AtomicInteger();
   final int index;
   final String name;
   final Class<T> nodeType;
   final T nullObject;
   
   public Role(String name, Class<T> nodeType)
   {
     this(name, nodeType, null);
   }
   
   public Role(String name, Class<T> nodeType, T nullObject) {
     VerifyArgument.notNull(nodeType, "nodeType");
     
     this.index = NEXT_ROLE_INDEX.getAndIncrement();
     
     if (this.index >= ROLES.length) {
       throw new IllegalStateException("Too many roles created!");
     }
     
     this.name = name;
     this.nodeType = nodeType;
     this.nullObject = nullObject;
     
     ROLES[this.index] = this;
   }
   
   public final T getNullObject() {
     return (T)this.nullObject;
   }
   
   public final Class<T> getNodeType() {
     return this.nodeType;
   }
   
   public final int getIndex() {
     return this.index;
   }
   
   public boolean isValid(Object node) {
     return this.nodeType.isInstance(node);
   }
   
   public static Role get(int index) {
     return ROLES[index];
   }
   
   public String toString()
   {
     return this.name;
   }
 }


