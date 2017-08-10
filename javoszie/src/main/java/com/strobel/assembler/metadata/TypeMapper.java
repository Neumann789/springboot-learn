 package com.strobel.assembler.metadata;
 
 import com.strobel.core.ArrayUtilities;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class TypeMapper<T>
   extends DefaultTypeVisitor<T, TypeReference>
 {
   public TypeReference visitType(TypeReference type, T parameter)
   {
     return type;
   }
   
   public List<? extends TypeReference> visit(List<? extends TypeReference> types, T parameter) {
     TypeReference[] newTypes = null;
     
     int i = 0; for (int n = types.size(); i < n; i++) {
       TypeReference oldType = (TypeReference)types.get(i);
       TypeReference newType = (TypeReference)visit(oldType, parameter);
       
       if (newType != oldType) {
         if (newTypes == null) {
           newTypes = (TypeReference[])types.toArray(new TypeReference[types.size()]);
         }
         newTypes[i] = newType;
       }
     }
     
     if (newTypes != null) {
       return ArrayUtilities.asUnmodifiableList(newTypes);
     }
     
     return types;
   }
   
   public List<? extends TypeReference> visit(List<? extends TypeReference> types) {
     return visit(types, null);
   }
 }


