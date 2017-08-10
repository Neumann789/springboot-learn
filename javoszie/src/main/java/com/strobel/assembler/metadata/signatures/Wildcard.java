 package com.strobel.assembler.metadata.signatures;
 
 import com.strobel.core.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Wildcard
   implements TypeArgument
 {
   private final FieldTypeSignature _superBound;
   private final FieldTypeSignature _extendsBound;
   
   private Wildcard(FieldTypeSignature superBound, FieldTypeSignature extendsBound)
   {
     this._superBound = superBound;
     this._extendsBound = extendsBound;
   }
   
   public static Wildcard make(FieldTypeSignature superBound, FieldTypeSignature extendsBound) {
     return new Wildcard(superBound, extendsBound);
   }
   
   public boolean isUnbounded() {
     return (!hasSuperBound()) && (!hasExtendsBound());
   }
   
   public boolean hasSuperBound() {
     return (this._superBound != null) && (this._superBound != BottomSignature.make());
   }
   
   public boolean hasExtendsBound()
   {
     return (this._extendsBound != null) && ((!(this._extendsBound instanceof SimpleClassTypeSignature)) || (!StringUtilities.equals("java.lang.Object", ((SimpleClassTypeSignature)this._extendsBound).getName())));
   }
   
 
   public FieldTypeSignature getSuperBound()
   {
     return this._superBound;
   }
   
   public FieldTypeSignature getExtendsBound() {
     return this._extendsBound;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitWildcard(this);
   }
 }


