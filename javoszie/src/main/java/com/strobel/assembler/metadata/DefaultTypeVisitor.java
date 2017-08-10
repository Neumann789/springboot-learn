 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class DefaultTypeVisitor<P, R>
   implements TypeMetadataVisitor<P, R>
 {
   public R visit(TypeReference t)
   {
     return (R)visit(t, null);
   }
   
   public R visit(TypeReference t, P p) {
     return (R)t.accept(this, p);
   }
   
   public R visitType(TypeReference t, P p)
   {
     return null;
   }
   
   public R visitArrayType(ArrayType t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitBottomType(TypeReference t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitClassType(TypeReference t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitCompoundType(CompoundTypeReference t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitGenericParameter(GenericParameter t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitNullType(TypeReference t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitParameterizedType(TypeReference t, P p)
   {
     return (R)visitClassType(t, p);
   }
   
   public R visitPrimitiveType(PrimitiveType t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitRawType(RawType t, P p)
   {
     return (R)visitClassType(t, p);
   }
   
   public R visitWildcard(WildcardType t, P p)
   {
     return (R)visitType(t, p);
   }
   
   public R visitCapturedType(CapturedType t, P p)
   {
     return (R)visitType(t, p);
   }
 }


