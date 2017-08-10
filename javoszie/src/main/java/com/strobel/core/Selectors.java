 package com.strobel.core;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Selectors
 {
   private static final Selector<?, ?> IDENTITY_SELECTOR = new Selector()
   {
     public Object select(Object source) {
       return source;
     }
   };
   
   private static final Selector<String, String> TO_UPPERCASE = new Selector()
   {
     public String select(String source) {
       if (source == null) {
         return null;
       }
       return source.toUpperCase();
     }
   };
   
   private static final Selector<String, String> TO_LOWERCASE = new Selector()
   {
     public String select(String source) {
       if (source == null) {
         return null;
       }
       return source.toUpperCase();
     }
   };
   
   private static final Selector<?, String> TO_STRING = new Selector()
   {
     public String select(Object source) {
       if (source == null) {
         return null;
       }
       return source.toString();
     }
   };
   
   private Selectors() {
     throw ContractUtils.unreachable();
   }
   
   public static <T> Selector<T, T> identity() {
     return IDENTITY_SELECTOR;
   }
   
   public static Selector<String, String> toUpperCase() {
     return TO_UPPERCASE;
   }
   
   public static Selector<String, String> toLowerCase() {
     return TO_LOWERCASE;
   }
   
   public static <T> Selector<T, String> asString() {
     return TO_STRING;
   }
   
   public static <T, R> Selector<T, R> cast(Class<R> destinationType) {
     new Selector()
     {
       public R select(T source) {
         return (R)this.val$destinationType.cast(source);
       }
     };
   }
   
 
 
   public static <T, U, R> Selector<T, R> combine(final Selector<? super T, ? extends U> first, Selector<? super U, ? extends R> second)
   {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     new Selector()
     {
       public R select(T source) {
         return (R)this.val$second.select(first.select(source));
       }
     };
   }
 }


