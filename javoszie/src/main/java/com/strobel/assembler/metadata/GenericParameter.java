 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class GenericParameter
   extends TypeDefinition
 {
   private int _position;
   private GenericParameterType _type = GenericParameterType.Type;
   private IGenericParameterProvider _owner;
   private TypeReference _extendsBound;
   
   public GenericParameter(String name) {
     this._extendsBound = BuiltinTypes.Object;
     setName(name != null ? name : "");
   }
   
   public GenericParameter(String name, TypeReference extendsBound) {
     this._extendsBound = ((TypeReference)VerifyArgument.notNull(extendsBound, "extendsBound"));
     setName(name != null ? name : "");
   }
   
   protected final void setPosition(int position) {
     this._position = position;
   }
   
   protected final void setOwner(IGenericParameterProvider owner) {
     this._owner = owner;
     
     this._type = ((owner instanceof MethodReference) ? GenericParameterType.Method : GenericParameterType.Type);
   }
   
   protected final void setExtendsBound(TypeReference extendsBound)
   {
     this._extendsBound = extendsBound;
   }
   
   public String getName()
   {
     String name = super.getName();
     
     if (!StringUtilities.isNullOrEmpty(name)) {
       return name;
     }
     
     return "T" + this._position;
   }
   
   public String getFullName()
   {
     return getName();
   }
   
   public String getInternalName()
   {
     return getName();
   }
   
   public TypeReference getUnderlyingType()
   {
     TypeReference extendsBound = getExtendsBound();
     return extendsBound != null ? extendsBound : BuiltinTypes.Object;
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitGenericParameter(this, parameter);
   }
   
   public boolean isUnbounded()
   {
     return !hasExtendsBound();
   }
   
   public boolean isGenericParameter()
   {
     return true;
   }
   
   public boolean containsGenericParameters()
   {
     return true;
   }
   
   public TypeReference getDeclaringType()
   {
     IGenericParameterProvider owner = this._owner;
     
     if ((owner instanceof TypeReference)) {
       return (TypeReference)owner;
     }
     
     return null;
   }
   
   public int getPosition() {
     return this._position;
   }
   
   public GenericParameterType getType() {
     return this._type;
   }
   
   public IGenericParameterProvider getOwner() {
     return this._owner;
   }
   
   public boolean hasExtendsBound()
   {
     return (this._extendsBound != null) && (!MetadataResolver.areEquivalent(this._extendsBound, BuiltinTypes.Object));
   }
   
 
   public TypeReference getExtendsBound()
   {
     return this._extendsBound;
   }
   
   public boolean hasAnnotations()
   {
     return !getAnnotations().isEmpty();
   }
   
   public TypeDefinition resolve()
   {
     if (((this._owner instanceof TypeReference)) && (!(this._owner instanceof TypeDefinition)))
     {
       TypeDefinition resolvedOwner = ((TypeReference)this._owner).resolve();
       
       if (resolvedOwner != null) {
         List<GenericParameter> genericParameters = resolvedOwner.getGenericParameters();
         
         if ((this._position >= 0) && (this._position < genericParameters.size()))
         {
 
           return (TypeDefinition)genericParameters.get(this._position);
         }
       }
     }
     return null;
   }
   
 
 
   protected StringBuilder appendDescription(StringBuilder sb)
   {
     sb.append(getFullName());
     
     TypeReference upperBound = getExtendsBound();
     
     if ((upperBound != null) && (!upperBound.equals(BuiltinTypes.Object))) {
       sb.append(" extends ");
       if ((upperBound.isGenericParameter()) || (upperBound.equals(getDeclaringType()))) {
         return sb.append(upperBound.getFullName());
       }
       return upperBound.appendErasedDescription(sb);
     }
     
     return sb;
   }
   
   protected StringBuilder appendBriefDescription(StringBuilder sb)
   {
     sb.append(getFullName());
     
     TypeReference upperBound = getExtendsBound();
     
     if ((upperBound != null) && (!upperBound.equals(BuiltinTypes.Object))) {
       sb.append(" extends ");
       if ((upperBound.isGenericParameter()) || (upperBound.equals(getDeclaringType()))) {
         return sb.append(upperBound.getName());
       }
       return upperBound.appendErasedDescription(sb);
     }
     
     return sb;
   }
   
   protected StringBuilder appendErasedDescription(StringBuilder sb)
   {
     return getExtendsBound().appendErasedDescription(sb);
   }
   
   protected StringBuilder appendSignature(StringBuilder sb)
   {
     return sb.append('T').append(getName()).append(';');
   }
   
 
 
   protected StringBuilder appendErasedSignature(StringBuilder sb)
   {
     return getExtendsBound().appendErasedSignature(sb);
   }
   
   protected StringBuilder appendSimpleDescription(StringBuilder sb)
   {
     sb.append(getFullName());
     
     TypeReference upperBound = getExtendsBound();
     
     if ((upperBound != null) && (!upperBound.equals(BuiltinTypes.Object))) {
       sb.append(" extends ");
       if ((upperBound.isGenericParameter()) || (upperBound.equals(getOwner()))) {
         return sb.append(upperBound.getSimpleName());
       }
       return upperBound.appendSimpleDescription(sb);
     }
     
     return sb;
   }
 }


