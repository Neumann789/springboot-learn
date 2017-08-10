 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TypedPrimitiveValueNode
   extends Pattern
 {
   private final String _groupName;
   private final Class<?> _primitiveType;
   
   public TypedPrimitiveValueNode(Class<?> primitiveType)
   {
     this._groupName = null;
     this._primitiveType = ((Class)VerifyArgument.notNull(primitiveType, "primitiveType"));
   }
   
   public TypedPrimitiveValueNode(String groupName, Class<?> primitiveType) {
     this._groupName = groupName;
     this._primitiveType = ((Class)VerifyArgument.notNull(primitiveType, "primitiveType"));
   }
   
   public final boolean matches(INode other, Match match)
   {
     if ((other instanceof PrimitiveExpression)) {
       PrimitiveExpression primitive = (PrimitiveExpression)other;
       
       if (this._primitiveType.isInstance(primitive.getValue())) {
         match.add(this._groupName, other);
         return true;
       }
     }
     return false;
   }
 }


