 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringComparator;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class MetadataResolver
   implements IMetadataResolver, IGenericContext
 {
   private final Stack<IResolverFrame> _frames;
   
   protected MetadataResolver()
   {
     this._frames = new Stack();
   }
   
   public final TypeReference lookupType(String descriptor)
   {
     for (int i = this._frames.size() - 1; i >= 0; i--) {
       TypeReference type = ((IResolverFrame)this._frames.get(i)).findType(descriptor);
       
       if (type != null) {
         return type;
       }
     }
     
     return lookupTypeCore(descriptor);
   }
   
   public final GenericParameter findTypeVariable(String name)
   {
     for (int i = this._frames.size() - 1; i >= 0; i--) {
       GenericParameter type = ((IResolverFrame)this._frames.get(i)).findTypeVariable(name);
       
       if (type != null) {
         return type;
       }
     }
     
     return null;
   }
   
   protected abstract TypeReference lookupTypeCore(String paramString);
   
   public void pushFrame(IResolverFrame frame)
   {
     this._frames.push(VerifyArgument.notNull(frame, "frame"));
   }
   
   public void popFrame()
   {
     this._frames.pop();
   }
   
   public TypeDefinition resolve(TypeReference type)
   {
     TypeReference t = ((TypeReference)VerifyArgument.notNull(type, "type")).getUnderlyingType();
     
     if (!this._frames.isEmpty()) {
       String descriptor = type.getInternalName();
       
       for (int i = this._frames.size() - 1; i >= 0; i--) {
         TypeReference resolved = ((IResolverFrame)this._frames.get(i)).findType(descriptor);
         
         if ((resolved instanceof TypeDefinition)) {
           return (TypeDefinition)resolved;
         }
       }
     }
     
     if (t.isNested()) {
       TypeDefinition declaringType = t.getDeclaringType().resolve();
       
       if (declaringType == null) {
         return null;
       }
       
       TypeDefinition nestedType = getNestedType(declaringType.getDeclaredTypes(), type);
       
       if (nestedType != null) {
         return nestedType;
       }
     }
     
     return resolveCore(t);
   }
   
   protected abstract TypeDefinition resolveCore(TypeReference paramTypeReference);
   
   public FieldDefinition resolve(FieldReference field)
   {
     TypeDefinition declaringType = ((FieldReference)VerifyArgument.notNull(field, "field")).getDeclaringType().resolve();
     
     if (declaringType == null) {
       return null;
     }
     
     return getField(declaringType, field);
   }
   
   public MethodDefinition resolve(MethodReference method)
   {
     TypeReference declaringType = ((MethodReference)VerifyArgument.notNull(method, "method")).getDeclaringType();
     
     if (declaringType.isArray()) {
       declaringType = BuiltinTypes.Object;
     }
     
     TypeDefinition resolvedDeclaringType = declaringType.resolve();
     
     if (resolvedDeclaringType == null) {
       return null;
     }
     
     return getMethod(resolvedDeclaringType, method);
   }
   
 
   final FieldDefinition getField(TypeDefinition declaringType, FieldReference reference)
   {
     TypeDefinition type = declaringType;
     
     while (type != null) {
       FieldDefinition field = getField(type.getDeclaredFields(), reference);
       
       if (field != null) {
         return field;
       }
       
       TypeReference baseType = type.getBaseType();
       
       if (baseType == null) {
         return null;
       }
       
       type = resolve(baseType);
     }
     
     return null;
   }
   
   final MethodDefinition getMethod(TypeDefinition declaringType, MethodReference reference) {
     TypeDefinition type = declaringType;
     
     MethodDefinition method = getMethod(type.getDeclaredMethods(), reference);
     
     if (method != null) {
       return method;
     }
     
     TypeReference baseType = declaringType.getBaseType();
     
     if (baseType != null) {
       type = baseType.resolve();
       
       if (type != null) {
         method = getMethod(type, reference);
         
         if (method != null) {
           return method;
         }
       }
     }
     
     for (TypeReference interfaceType : declaringType.getExplicitInterfaces()) {
       type = interfaceType.resolve();
       
       if (type != null) {
         method = getMethod(type, reference);
         
         if (method != null) {
           return method;
         }
       }
     }
     
     return null;
   }
   
   static TypeDefinition getNestedType(List<TypeDefinition> candidates, TypeReference reference) {
     int i = 0; for (int n = candidates.size(); i < n; i++) {
       TypeDefinition candidate = (TypeDefinition)candidates.get(i);
       
       if (StringComparator.Ordinal.equals(candidate.getName(), reference.getName())) {
         return candidate;
       }
     }
     
     return null;
   }
   
   static FieldDefinition getField(List<FieldDefinition> candidates, FieldReference reference) {
     int i = 0; for (int n = candidates.size(); i < n; i++) {
       FieldDefinition candidate = (FieldDefinition)candidates.get(i);
       
       if (StringComparator.Ordinal.equals(candidate.getName(), reference.getName())) {
         TypeReference referenceType = reference.getFieldType();
         TypeReference candidateType = candidate.getFieldType();
         
         if ((candidateType.isGenericParameter()) && (!referenceType.isGenericParameter())) {
           if (areEquivalent(MetadataHelper.getUpperBound(candidateType), referenceType)) {
             return candidate;
           }
           
         }
         else if (areEquivalent(candidateType, referenceType)) {
           return candidate;
         }
       }
     }
     
 
     return null;
   }
   
   static MethodDefinition getMethod(List<MethodDefinition> candidates, MethodReference reference) {
     String erasedSignature = reference.getErasedSignature();
     
     int i = 0; for (int n = candidates.size(); i < n; i++) {
       MethodDefinition candidate = (MethodDefinition)candidates.get(i);
       
       if (StringComparator.Ordinal.equals(candidate.getName(), reference.getName()))
       {
 
 
         if (StringComparator.Ordinal.equals(candidate.getErasedSignature(), erasedSignature)) {
           return candidate;
         }
         
         if ((!reference.hasGenericParameters()) || (
           (candidate.hasGenericParameters()) && (candidate.getGenericParameters().size() == reference.getGenericParameters().size())))
         {
 
 
 
 
 
           if (StringComparator.Ordinal.equals(candidate.getErasedSignature(), erasedSignature))
           {
 
 
             return candidate; } }
       }
     }
     return null;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static boolean areEquivalent(TypeReference a, TypeReference b)
   {
     return areEquivalent(a, b, true);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static boolean areEquivalent(TypeReference a, TypeReference b, boolean strict)
   {
     if (a == b) {
       return true;
     }
     
     if ((a == null) || (b == null)) {
       return false;
     }
     
     if (a.getSimpleType() != b.getSimpleType()) {
       return false;
     }
     
     if (a.isArray()) {
       return areEquivalent(a.getElementType(), b.getElementType());
     }
     
     if (!StringUtilities.equals(a.getInternalName(), b.getInternalName())) {
       return false;
     }
     
     if ((a instanceof CompoundTypeReference)) {
       if (!(b instanceof CompoundTypeReference)) {
         return false;
       }
       
       CompoundTypeReference cA = (CompoundTypeReference)a;
       CompoundTypeReference cB = (CompoundTypeReference)b;
       
       return (areEquivalent(cA.getBaseType(), cB.getBaseType())) && (areEquivalent(cA.getInterfaces(), cB.getInterfaces()));
     }
     
     if ((b instanceof CompoundTypeReference)) {
       return false;
     }
     
     if (a.isGenericParameter()) {
       if (b.isGenericParameter()) {
         return areEquivalent((GenericParameter)a, (GenericParameter)b);
       }
       
       return areEquivalent(a.getExtendsBound(), b);
     }
     if (b.isGenericParameter()) {
       return false;
     }
     
     if (a.isWildcardType()) {
       return (b.isWildcardType()) && (areEquivalent(a.getExtendsBound(), b.getExtendsBound())) && (areEquivalent(a.getSuperBound(), b.getSuperBound()));
     }
     
 
     if (b.isWildcardType()) {
       return false;
     }
     
     if (b.isGenericType()) {
       if (!a.isGenericType()) {
         return (!strict) || (b.isGenericDefinition());
       }
       
       if (a.isGenericDefinition() != b.isGenericDefinition()) {
         if (a.isGenericDefinition()) {
           return areEquivalent(a.makeGenericType(((IGenericInstance)b).getTypeArguments()), b);
         }
         
         return areEquivalent(a, b.makeGenericType(((IGenericInstance)a).getTypeArguments()));
       }
       
 
       if ((b instanceof IGenericInstance)) {
         return ((a instanceof IGenericInstance)) && (areEquivalent((IGenericInstance)a, (IGenericInstance)b));
       }
     }
     
 
 
 
     return true;
   }
   
   static boolean areParametersEquivalent(List<ParameterDefinition> a, List<ParameterDefinition> b) {
     int count = a.size();
     
     if (b.size() != count) {
       return false;
     }
     
     if (count == 0) {
       return true;
     }
     
     for (int i = 0; i < count; i++) {
       ParameterDefinition pb = (ParameterDefinition)b.get(i);
       ParameterDefinition pa = (ParameterDefinition)a.get(i);
       TypeReference tb = pb.getParameterType();
       
       TypeReference ta = pa.getParameterType();
       
       if ((ta.isGenericParameter()) && (!tb.isGenericParameter()) && (((GenericParameter)ta).getOwner() == pa.getMethod()))
       {
 
 
         ta = ta.getExtendsBound();
       }
       
       if (!areEquivalent(ta, tb)) {
         return false;
       }
     }
     
     return true;
   }
   
   static <T extends TypeReference> boolean areEquivalent(List<T> a, List<T> b) {
     int count = a.size();
     
     if (b.size() != count) {
       return false;
     }
     
     if (count == 0) {
       return true;
     }
     
     for (int i = 0; i < count; i++) {
       if (!areEquivalent((TypeReference)a.get(i), (TypeReference)b.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   private static boolean areEquivalent(IGenericInstance a, IGenericInstance b) {
     List<TypeReference> typeArgumentsA = a.getTypeArguments();
     List<TypeReference> typeArgumentsB = b.getTypeArguments();
     
     int arity = typeArgumentsA.size();
     
     if (arity != typeArgumentsB.size()) {
       return false;
     }
     
     for (int i = 0; i < arity; i++) {
       if (!areEquivalent((TypeReference)typeArgumentsA.get(i), (TypeReference)typeArgumentsB.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   private static boolean areEquivalent(GenericParameter a, GenericParameter b) {
     if (a.getPosition() != b.getPosition()) {
       return false;
     }
     
     IGenericParameterProvider ownerA = a.getOwner();
     IGenericParameterProvider ownerB = b.getOwner();
     
     if ((ownerA instanceof TypeDefinition)) {
       return ((ownerB instanceof TypeDefinition)) && (areEquivalent((TypeDefinition)ownerA, (TypeDefinition)ownerB));
     }
     
 
     if ((ownerA instanceof MethodDefinition)) {
       if (!(ownerB instanceof MethodDefinition)) {
         return false;
       }
       
       MethodDefinition methodA = (MethodDefinition)ownerA;
       MethodDefinition methodB = (MethodDefinition)ownerB;
       
       return (areEquivalent(methodA.getDeclaringType(), methodB.getDeclaringType())) && (StringUtilities.equals(methodA.getErasedSignature(), methodB.getErasedSignature()));
     }
     
 
     return true;
   }
   
 
 
 
   public static IMetadataResolver createLimitedResolver()
   {
     return new LimitedResolver(null);
   }
   
   private static final class LimitedResolver extends MetadataResolver
   {
     protected TypeReference lookupTypeCore(String descriptor) {
       return null;
     }
     
     protected TypeDefinition resolveCore(TypeReference type)
     {
       return (type instanceof TypeDefinition) ? (TypeDefinition)type : null;
     }
   }
 }


