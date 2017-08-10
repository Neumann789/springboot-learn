 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class PrimitiveType
   extends TypeDefinition
 {
   private final JvmType _jvmType;
   
   PrimitiveType(JvmType jvmType)
   {
     super(MetadataSystem.instance());
     this._jvmType = ((JvmType)VerifyArgument.notNull(jvmType, "jvmType"));
     setFlags(1L);
     setName(this._jvmType.getPrimitiveName());
   }
   
   public String getInternalName()
   {
     return this._jvmType.getDescriptorPrefix();
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitPrimitiveType(this, parameter);
   }
   
   public String getSimpleName()
   {
     return this._jvmType.getPrimitiveName();
   }
   
   public String getFullName()
   {
     return this._jvmType.getPrimitiveName();
   }
   
   public final boolean isPrimitive()
   {
     return true;
   }
   
   public final boolean isVoid()
   {
     return this._jvmType == JvmType.Void;
   }
   
   public final JvmType getSimpleType()
   {
     return this._jvmType;
   }
   
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     return sb.append(this._jvmType.getPrimitiveName());
   }
   
   protected StringBuilder appendBriefDescription(StringBuilder sb)
   {
     return sb.append(this._jvmType.getPrimitiveName());
   }
   
   protected StringBuilder appendSimpleDescription(StringBuilder sb)
   {
     return sb.append(this._jvmType.getPrimitiveName());
   }
   
   protected StringBuilder appendErasedDescription(StringBuilder sb)
   {
     return sb.append(this._jvmType.getPrimitiveName());
   }
   
   protected StringBuilder appendClassDescription(StringBuilder sb)
   {
     return sb.append(this._jvmType.getPrimitiveName());
   }
   
   protected StringBuilder appendSignature(StringBuilder sb)
   {
     return sb.append(this._jvmType.getDescriptorPrefix());
   }
   
   protected StringBuilder appendErasedSignature(StringBuilder sb)
   {
     return sb.append(this._jvmType.getDescriptorPrefix());
   }
   
   protected StringBuilder appendClassSignature(StringBuilder sb)
   {
     return sb.append(this._jvmType.getDescriptorPrefix());
   }
   
   protected StringBuilder appendErasedClassSignature(StringBuilder sb)
   {
     return sb.append(this._jvmType.getDescriptorPrefix());
   }
   
   public StringBuilder appendGenericSignature(StringBuilder sb)
   {
     return sb.append(this._jvmType.getDescriptorPrefix());
   }
 }


