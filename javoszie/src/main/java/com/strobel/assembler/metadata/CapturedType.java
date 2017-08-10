 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CapturedType
   extends TypeReference
   implements ICapturedType
 {
   private final TypeReference _superBound;
   private final TypeReference _extendsBound;
   private final WildcardType _wildcard;
   
   CapturedType(TypeReference superBound, TypeReference extendsBound, WildcardType wildcard)
   {
     this._superBound = (superBound != null ? superBound : BuiltinTypes.Bottom);
     this._extendsBound = (extendsBound != null ? extendsBound : BuiltinTypes.Object);
     this._wildcard = ((WildcardType)VerifyArgument.notNull(wildcard, "wildcard"));
   }
   
   public final WildcardType getWildcard()
   {
     return this._wildcard;
   }
   
   public final TypeReference getExtendsBound()
   {
     return this._extendsBound;
   }
   
   public final TypeReference getSuperBound()
   {
     return this._superBound;
   }
   
   public final boolean hasExtendsBound()
   {
     return (this._extendsBound != null) && (!MetadataHelper.isSameType(this._extendsBound, BuiltinTypes.Object));
   }
   
 
   public final boolean hasSuperBound()
   {
     return this._superBound != BuiltinTypes.Bottom;
   }
   
   public final boolean isBoundedType()
   {
     return true;
   }
   
   public String getSimpleName()
   {
     return "capture of " + this._wildcard.getSimpleName();
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitCapturedType(this, parameter);
   }
   
   protected final StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     return this._wildcard.appendName(sb.append("capture of "), fullName, dottedName);
   }
 }


