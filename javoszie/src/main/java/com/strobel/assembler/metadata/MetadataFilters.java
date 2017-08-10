 package com.strobel.assembler.metadata;
 
 import com.strobel.core.Predicate;
 import com.strobel.core.Predicates;
 import com.strobel.core.StringUtilities;
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MetadataFilters
 {
   private MetadataFilters()
   {
     throw ContractUtils.unreachable();
   }
   
   public static <T extends TypeReference> Predicate<T> isSubType(TypeReference anchor) {
     new Predicate()
     {
       public final boolean test(T t) {
         return MetadataHelper.isSubType(t, this.val$anchor);
       }
     };
   }
   
   public static <T extends TypeReference> Predicate<T> isSuperType(TypeReference anchor) {
     new Predicate()
     {
       public final boolean test(T t) {
         return MetadataHelper.isSubType(this.val$anchor, t);
       }
     };
   }
   
   public static <T extends TypeReference> Predicate<T> isAssignableFrom(TypeReference sourceType) {
     new Predicate()
     {
       public final boolean test(T t) {
         return MetadataHelper.isAssignableFrom(t, this.val$sourceType);
       }
     };
   }
   
   public static <T extends TypeReference> Predicate<T> isAssignableTo(TypeReference targetType) {
     new Predicate()
     {
       public final boolean test(T t) {
         return MetadataHelper.isAssignableFrom(this.val$targetType, t);
       }
     };
   }
   
   public static <T extends MemberReference> Predicate<T> matchName(String name) {
     new Predicate()
     {
       public final boolean test(T t) {
         return StringUtilities.equals(t.getName(), this.val$name);
       }
     };
   }
   
   public static <T extends MemberReference> Predicate<T> matchDescriptor(String descriptor) {
     new Predicate()
     {
       public final boolean test(T t) {
         return StringUtilities.equals(t.getErasedSignature(), this.val$descriptor);
       }
     };
   }
   
   public static <T extends MemberReference> Predicate<T> matchSignature(String signature) {
     new Predicate()
     {
       public final boolean test(T t) {
         return StringUtilities.equals(t.getSignature(), this.val$signature);
       }
     };
   }
   
   public static <T extends MemberReference> Predicate<T> matchNameAndDescriptor(String name, String descriptor) {
     return Predicates.and(matchName(name), matchDescriptor(descriptor));
   }
   
   public static <T extends MemberReference> Predicate<T> matchNameAndSignature(String name, String signature) {
     return Predicates.and(matchName(name), matchSignature(signature));
   }
 }


