 package com.strobel.assembler.metadata;
 
 import com.strobel.core.ArrayUtilities;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TypeSubstitutionVisitor
   extends DefaultTypeVisitor<Map<TypeReference, TypeReference>, TypeReference>
   implements MethodMetadataVisitor<Map<TypeReference, TypeReference>, MethodReference>, FieldMetadataVisitor<Map<TypeReference, TypeReference>, FieldReference>
 {
   private static final TypeSubstitutionVisitor INSTANCE = new TypeSubstitutionVisitor();
   
   public static TypeSubstitutionVisitor instance() {
     return INSTANCE;
   }
   
   public TypeReference visit(TypeReference t, Map<TypeReference, TypeReference> map) {
     if (map.isEmpty()) {
       return t;
     }
     return (TypeReference)t.accept(this, map);
   }
   
   public TypeReference visitArrayType(ArrayType t, Map<TypeReference, TypeReference> map)
   {
     TypeReference elementType = visit(t.getElementType(), map);
     
     if ((elementType != null) && (elementType != t.getElementType())) {
       return elementType.makeArrayType();
     }
     
     return t;
   }
   
   public TypeReference visitGenericParameter(GenericParameter t, Map<TypeReference, TypeReference> map)
   {
     TypeReference current = t;
     
     TypeReference mappedType;
     
     while (((mappedType = (TypeReference)map.get(current)) != null) && (mappedType != current) && (map.get(mappedType) != current))
     {
 
       current = mappedType;
     }
     
     if (current == null) {
       return t;
     }
     
     if (current.isPrimitive()) {
       switch (current.getSimpleType()) {
       case Boolean: 
         return CommonTypeReferences.Boolean;
       case Byte: 
         return CommonTypeReferences.Byte;
       case Character: 
         return CommonTypeReferences.Character;
       case Short: 
         return CommonTypeReferences.Short;
       case Integer: 
         return CommonTypeReferences.Integer;
       case Long: 
         return CommonTypeReferences.Long;
       case Float: 
         return CommonTypeReferences.Float;
       case Double: 
         return CommonTypeReferences.Double;
       case Void: 
         return CommonTypeReferences.Void;
       }
       
     }
     return current;
   }
   
   public TypeReference visitWildcard(WildcardType t, Map<TypeReference, TypeReference> map)
   {
     if (t.isUnbounded()) {
       return t;
     }
     
     TypeReference oldBound = t.hasExtendsBound() ? t.getExtendsBound() : t.getSuperBound();
     TypeReference mapping = (TypeReference)map.get(oldBound);
     
     if (MetadataResolver.areEquivalent(mapping, t)) {
       return t;
     }
     
     TypeReference newBound = visit(oldBound, map);
     
     while (newBound.isWildcardType()) {
       if (newBound.isUnbounded()) {
         return newBound;
       }
       newBound = newBound.hasExtendsBound() ? newBound.getExtendsBound() : newBound.getSuperBound();
     }
     
 
     if (oldBound != newBound) {
       return t.hasExtendsBound() ? WildcardType.makeExtends(newBound) : WildcardType.makeSuper(newBound);
     }
     
 
     return t;
   }
   
   public TypeReference visitCompoundType(CompoundTypeReference t, Map<TypeReference, TypeReference> map)
   {
     TypeReference oldBaseType = t.getBaseType();
     TypeReference newBaseType = oldBaseType != null ? visit(oldBaseType, map) : null;
     
     TypeReference[] newInterfaces = null;
     
     boolean changed = newBaseType != oldBaseType;
     
     List<TypeReference> oldInterfaces = t.getInterfaces();
     
     for (int i = 0; i < oldInterfaces.size(); i++) {
       TypeReference oldInterface = (TypeReference)oldInterfaces.get(i);
       TypeReference newInterface = visit(oldInterface, map);
       
       if (newInterfaces != null) {
         newInterfaces[i] = newInterface;
       }
       else if (oldInterface != newInterface) {
         newInterfaces = new TypeReference[oldInterfaces.size()];
         oldInterfaces.toArray(newInterfaces);
         newInterfaces[i] = newInterface;
         changed = true;
       }
     }
     
     if (changed) {
       return new CompoundTypeReference(newBaseType, newInterfaces != null ? ArrayUtilities.asUnmodifiableList(newInterfaces) : t.getInterfaces());
     }
     
 
 
 
 
     return t;
   }
   
   public TypeReference visitParameterizedType(TypeReference t, Map<TypeReference, TypeReference> map)
   {
     List<TypeReference> oldTypeArguments = ((IGenericInstance)t).getTypeArguments();
     
     TypeReference[] newTypeArguments = null;
     
     boolean changed = false;
     
     for (int i = 0; i < oldTypeArguments.size(); i++) {
       TypeReference oldTypeArgument = (TypeReference)oldTypeArguments.get(i);
       TypeReference newTypeArgument = visit(oldTypeArgument, map);
       
       if (newTypeArguments != null) {
         newTypeArguments[i] = newTypeArgument;
       }
       else if (oldTypeArgument != newTypeArgument) {
         newTypeArguments = new TypeReference[oldTypeArguments.size()];
         oldTypeArguments.toArray(newTypeArguments);
         newTypeArguments[i] = newTypeArgument;
         changed = true;
       }
     }
     
     if (changed) {
       return t.makeGenericType(newTypeArguments);
     }
     
     return t;
   }
   
   public TypeReference visitPrimitiveType(PrimitiveType t, Map<TypeReference, TypeReference> map)
   {
     return t;
   }
   
 
   public TypeReference visitClassType(TypeReference t, Map<TypeReference, TypeReference> map)
   {
     TypeReference resolvedType = t.isGenericType() ? t : t.resolve();
     
     if ((resolvedType == null) || (!resolvedType.isGenericDefinition())) {
       return t;
     }
     
     List<TypeReference> oldTypeArguments = (List)resolvedType.getGenericParameters();
     
     TypeReference[] newTypeArguments = null;
     
     boolean changed = false;
     
     for (int i = 0; i < oldTypeArguments.size(); i++) {
       TypeReference oldTypeArgument = (TypeReference)oldTypeArguments.get(i);
       TypeReference newTypeArgument = visit(oldTypeArgument, map);
       
       if (newTypeArguments != null) {
         newTypeArguments[i] = newTypeArgument;
       }
       else if (oldTypeArgument != newTypeArgument) {
         newTypeArguments = new TypeReference[oldTypeArguments.size()];
         oldTypeArguments.toArray(newTypeArguments);
         newTypeArguments[i] = newTypeArgument;
         changed = true;
       }
     }
     
     if (changed) {
       return t.makeGenericType(newTypeArguments);
     }
     
     return t;
   }
   
   public TypeReference visitNullType(TypeReference t, Map<TypeReference, TypeReference> map)
   {
     return t;
   }
   
   public TypeReference visitBottomType(TypeReference t, Map<TypeReference, TypeReference> map)
   {
     return t;
   }
   
   public TypeReference visitRawType(RawType t, Map<TypeReference, TypeReference> map)
   {
     return t;
   }
   
   public MethodReference visitParameterizedMethod(MethodReference m, Map<TypeReference, TypeReference> map)
   {
     return visitMethod(m, map);
   }
   
 
   public MethodReference visitMethod(MethodReference m, Map<TypeReference, TypeReference> map)
   {
     MethodDefinition resolvedMethod = m.resolve();
     
     List<TypeReference> oldTypeArguments;
     
     if ((m instanceof IGenericInstance)) {
       oldTypeArguments = ((IGenericInstance)m).getTypeArguments();
     } else {
       if (m.isGenericDefinition()) {
         oldTypeArguments = (List)m.getGenericParameters();
       }
       else {
         oldTypeArguments = Collections.emptyList();
       }
     }
     List<TypeReference> newTypeArguments = visitTypes(oldTypeArguments, map);
     
     TypeReference oldReturnType = m.getReturnType();
     TypeReference newReturnType = visit(oldReturnType, map);
     
     List<ParameterDefinition> oldParameters = m.getParameters();
     List<ParameterDefinition> newParameters = visitParameters(oldParameters, map);
     
     if ((newTypeArguments != oldTypeArguments) || (newReturnType != oldReturnType) || (newParameters != oldParameters))
     {
 
 
       return new GenericMethodInstance(visit(m.getDeclaringType(), map), resolvedMethod != null ? resolvedMethod : m, newReturnType, newParameters == oldParameters ? MetadataHelper.copyParameters(oldParameters) : newParameters, newTypeArguments);
     }
     
 
 
 
 
 
 
 
     return m;
   }
   
 
 
   protected List<TypeReference> visitTypes(List<TypeReference> types, Map<TypeReference, TypeReference> map)
   {
     TypeReference[] newTypes = null;
     
     boolean changed = false;
     
     for (int i = 0; i < types.size(); i++) {
       TypeReference oldTypeArgument = (TypeReference)types.get(i);
       TypeReference newTypeArgument = visit(oldTypeArgument, map);
       
       if (newTypes != null) {
         newTypes[i] = newTypeArgument;
       }
       else if (oldTypeArgument != newTypeArgument) {
         newTypes = new TypeReference[types.size()];
         types.toArray(newTypes);
         newTypes[i] = newTypeArgument;
         changed = true;
       }
     }
     
     return changed ? ArrayUtilities.asUnmodifiableList(newTypes) : types;
   }
   
 
 
 
   protected List<ParameterDefinition> visitParameters(List<ParameterDefinition> parameters, Map<TypeReference, TypeReference> map)
   {
     if (parameters.isEmpty()) {
       return parameters;
     }
     
     ParameterDefinition[] newParameters = null;
     
     boolean changed = false;
     
     for (int i = 0; i < parameters.size(); i++) {
       ParameterDefinition oldParameter = (ParameterDefinition)parameters.get(i);
       
       TypeReference oldType = oldParameter.getParameterType();
       TypeReference newType = visit(oldType, map);
       
 
 
       ParameterDefinition newParameter = oldType != newType ? new ParameterDefinition(oldParameter.getSlot(), newType) : oldParameter;
       
 
       if (newParameters != null) {
         newParameters[i] = newParameter;
       }
       else if (oldType != newType) {
         newParameters = new ParameterDefinition[parameters.size()];
         parameters.toArray(newParameters);
         newParameters[i] = newParameter;
         changed = true;
       }
     }
     
     return changed ? ArrayUtilities.asUnmodifiableList(newParameters) : parameters;
   }
   
 
   public FieldReference visitField(final FieldReference f, Map<TypeReference, TypeReference> map)
   {
     TypeReference oldFieldType = f.getFieldType();
     final TypeReference newFieldType = visit(oldFieldType, map);
     
     if (newFieldType != oldFieldType) {
       final TypeReference declaringType = f.getDeclaringType();
       
       new FieldReference() {
         private final String _name = f.getName();
         private final TypeReference _type = newFieldType;
         
         public TypeReference getFieldType()
         {
           return this._type;
         }
         
         public TypeReference getDeclaringType()
         {
           return declaringType;
         }
         
         public String getName()
         {
           return this._name;
         }
         
         protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
         {
           if (fullName) {
             TypeReference declaringType = getDeclaringType();
             
             if (declaringType != null) {
               return declaringType.appendName(sb, true, false).append('.').append(getName());
             }
           }
           
           return sb.append(this._name);
         }
       };
     }
     
     return f;
   }
 }


