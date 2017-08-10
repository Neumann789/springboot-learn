 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class WildcardType
   extends TypeReference
 {
   private static final WildcardType UNBOUNDED = new WildcardType(BuiltinTypes.Object, BuiltinTypes.Bottom);
   
   private final TypeReference _bound;
   private final boolean _hasSuperBound;
   private String _name;
   
   private WildcardType(TypeReference extendsBound, TypeReference superBound)
   {
     this._hasSuperBound = (superBound != BuiltinTypes.Bottom);
     this._bound = (this._hasSuperBound ? superBound : extendsBound);
   }
   
   public TypeReference getDeclaringType()
   {
     return null;
   }
   
   public String getSimpleName()
   {
     return this._name;
   }
   
   public JvmType getSimpleType()
   {
     return JvmType.Wildcard;
   }
   
 
 
   public boolean containsGenericParameters()
   {
     if (hasSuperBound()) {
       return getSuperBound().containsGenericParameters();
     }
     if (hasExtendsBound()) {
       return getExtendsBound().containsGenericParameters();
     }
     return false;
   }
   
   public String getName()
   {
     if (this._name == null) {
       this._name = appendSimpleDescription(new StringBuilder()).toString();
     }
     return this._name;
   }
   
   public String getFullName()
   {
     return getName();
   }
   
   public String getInternalName()
   {
     return getName();
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitWildcard(this, parameter);
   }
   
   public boolean isWildcardType()
   {
     return true;
   }
   
   public boolean isBoundedType()
   {
     return true;
   }
   
   public boolean isUnbounded()
   {
     return (this._bound == null) || ((!this._hasSuperBound) && (BuiltinTypes.Object.equals(this._bound)));
   }
   
 
   public boolean hasExtendsBound()
   {
     return (!this._hasSuperBound) && (this._bound != null) && (!BuiltinTypes.Object.equals(this._bound));
   }
   
 
 
   public boolean hasSuperBound()
   {
     return this._hasSuperBound;
   }
   
   public TypeReference getSuperBound()
   {
     return this._hasSuperBound ? this._bound : BuiltinTypes.Bottom;
   }
   
   public TypeReference getExtendsBound()
   {
     return this._hasSuperBound ? BuiltinTypes.Object : this._bound;
   }
   
 
 
 
 
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     return appendSimpleDescription(sb);
   }
   
   public StringBuilder appendSignature(StringBuilder sb)
   {
     if (isUnbounded()) {
       return sb.append('*');
     }
     
     if (hasSuperBound()) {
       return this._bound.appendSignature(sb.append('-'));
     }
     
     return this._bound.appendSignature(sb.append('+'));
   }
   
   public StringBuilder appendBriefDescription(StringBuilder sb)
   {
     if (isUnbounded()) {
       return sb.append("?");
     }
     
     if (hasSuperBound()) {
       sb.append("? super ");
       if (this._bound.isGenericParameter()) {
         return sb.append(this._bound.getFullName());
       }
       return this._bound.appendErasedDescription(sb);
     }
     
     sb.append("? extends ");
     
     if (this._bound.isGenericParameter()) {
       return sb.append(this._bound.getFullName());
     }
     
     return this._bound.appendErasedDescription(sb);
   }
   
   public StringBuilder appendSimpleDescription(StringBuilder sb)
   {
     if (isUnbounded()) {
       return sb.append("?");
     }
     
     if (hasSuperBound()) {
       sb.append("? super ");
       if ((this._bound.isGenericParameter()) || (this._bound.isWildcardType())) {
         return sb.append(this._bound.getSimpleName());
       }
       return this._bound.appendSimpleDescription(sb);
     }
     
     sb.append("? extends ");
     
     if ((this._bound.isGenericParameter()) || (this._bound.isWildcardType())) {
       return sb.append(this._bound.getSimpleName());
     }
     
     return this._bound.appendSimpleDescription(sb);
   }
   
   public StringBuilder appendErasedDescription(StringBuilder sb)
   {
     return appendBriefDescription(sb);
   }
   
   public StringBuilder appendDescription(StringBuilder sb)
   {
     return appendBriefDescription(sb);
   }
   
 
 
 
   public static WildcardType unbounded()
   {
     return UNBOUNDED;
   }
   
   public static WildcardType makeSuper(TypeReference superBound) {
     return new WildcardType(BuiltinTypes.Object, superBound);
   }
   
   public static WildcardType makeExtends(TypeReference extendsBound) {
     return new WildcardType(extendsBound, BuiltinTypes.Bottom);
   }
 }


