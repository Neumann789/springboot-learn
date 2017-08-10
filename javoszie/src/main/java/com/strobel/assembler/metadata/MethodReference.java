 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.util.ContractUtils;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class MethodReference
   extends MemberReference
   implements IMethodSignature, IGenericParameterProvider, IGenericContext
 {
   protected static final String CONSTRUCTOR_NAME = "<init>";
   protected static final String STATIC_INITIALIZER_NAME = "<clinit>";
   
   public abstract TypeReference getReturnType();
   
   public boolean hasParameters()
   {
     return !getParameters().isEmpty();
   }
   
   public abstract List<ParameterDefinition> getParameters();
   
   public List<TypeReference> getThrownTypes() {
     return Collections.emptyList();
   }
   
 
 
 
 
   public boolean isSpecialName()
   {
     return ("<init>".equals(getName())) || ("<clinit>".equals(getName()));
   }
   
 
   public boolean containsGenericParameters()
   {
     if ((super.containsGenericParameters()) || (hasGenericParameters())) {
       return true;
     }
     
     if (getReturnType().containsGenericParameters()) {
       return true;
     }
     
     if (hasParameters()) {
       List<ParameterDefinition> parameters = getParameters();
       
       int i = 0; for (int n = parameters.size(); i < n; i++) {
         if (((ParameterDefinition)parameters.get(i)).getParameterType().containsGenericParameters()) {
           return true;
         }
       }
     }
     
     return false;
   }
   
   public boolean isEquivalentTo(MemberReference member)
   {
     if (super.isEquivalentTo(member)) {
       return true;
     }
     
     if ((member instanceof MethodReference)) {
       MethodReference method = (MethodReference)member;
       
       return (StringUtilities.equals(method.getName(), getName())) && (StringUtilities.equals(method.getErasedSignature(), getErasedSignature())) && (MetadataResolver.areEquivalent(method.getDeclaringType(), getDeclaringType()));
     }
     
 
 
     return false;
   }
   
 
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     if (fullName) {
       TypeReference declaringType = getDeclaringType();
       
       if (declaringType != null) {
         return declaringType.appendName(sb, true, false).append('.').append(getName());
       }
     }
     
     return sb.append(getName());
   }
   
   public boolean isConstructor() {
     return "<init>".equals(getName());
   }
   
   public boolean isTypeInitializer() {
     return "<clinit>".equals(getName());
   }
   
 
 
 
   public boolean isGenericMethod()
   {
     return hasGenericParameters();
   }
   
   public boolean hasGenericParameters()
   {
     return !getGenericParameters().isEmpty();
   }
   
   public boolean isGenericDefinition()
   {
     return (hasGenericParameters()) && (isDefinition());
   }
   
   public List<GenericParameter> getGenericParameters()
   {
     return Collections.emptyList();
   }
   
   public GenericParameter findTypeVariable(String name)
   {
     for (GenericParameter genericParameter : getGenericParameters()) {
       if (StringUtilities.equals(genericParameter.getName(), name)) {
         return genericParameter;
       }
     }
     
     TypeReference declaringType = getDeclaringType();
     
     if (declaringType != null) {
       return declaringType.findTypeVariable(name);
     }
     
     return null;
   }
   
 
 
 
   public MethodDefinition resolve()
   {
     TypeReference declaringType = getDeclaringType();
     
     if (declaringType == null) {
       throw ContractUtils.unsupported();
     }
     return declaringType.resolve(this);
   }
   
 
 
 
 
   public StringBuilder appendSignature(StringBuilder sb)
   {
     List<ParameterDefinition> parameters = getParameters();
     
     StringBuilder s = sb;
     s.append('(');
     
     int i = 0; for (int n = parameters.size(); i < n; i++) {
       ParameterDefinition p = (ParameterDefinition)parameters.get(i);
       s = p.getParameterType().appendSignature(s);
     }
     
     s.append(')');
     s = getReturnType().appendSignature(s);
     
     return s;
   }
   
   public StringBuilder appendErasedSignature(StringBuilder sb)
   {
     StringBuilder s = sb;
     s.append('(');
     
     List<ParameterDefinition> parameterTypes = getParameters();
     
     int i = 0; for (int n = parameterTypes.size(); i < n; i++) {
       s = ((ParameterDefinition)parameterTypes.get(i)).getParameterType().appendErasedSignature(s);
     }
     
     s.append(')');
     s = getReturnType().appendErasedSignature(s);
     
     return s;
   }
 }


