 package com.strobel.assembler.metadata;
 
 import com.strobel.collections.ListBuffer;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Pair;
 import com.strobel.core.Predicate;
 import com.strobel.core.Predicates;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 public final class MetadataHelper
 {
   public static boolean areGenericsSupported(TypeDefinition t)
   {
     return (t != null) && (t.getCompilerMajorVersion() >= 49);
   }
   
   public static int getArrayRank(TypeReference t) {
     if (t == null) {
       return 0;
     }
     
     int rank = 0;
     TypeReference current = t;
     
     while (current.isArray()) {
       rank++;
       current = current.getElementType();
     }
     
     return rank;
   }
   
   public static boolean isEnclosedBy(TypeReference innerType, TypeReference outerType) {
     if (innerType == null) {
       return false;
     }
     
     for (TypeReference current = innerType; 
         current != null; 
         current = current.getDeclaringType())
     {
       if (isSameType(current, outerType)) {
         return true;
       }
     }
     
     TypeDefinition resolvedInnerType = innerType.resolve();
     
     return (resolvedInnerType != null) && (isEnclosedBy(resolvedInnerType.getBaseType(), outerType));
   }
   
   public static boolean canReferenceTypeVariablesOf(TypeReference declaringType, TypeReference referenceSite)
   {
     if ((declaringType == null) || (referenceSite == null)) {
       return false;
     }
     
     if (declaringType == referenceSite) {
       return declaringType.isGenericType();
     }
     
     TypeReference current = referenceSite.getDeclaringType();
     while (current != null)
     {
       if (isSameType(current, declaringType)) {
         return true;
       }
       
       TypeDefinition resolvedType = current.resolve();
       
       if (resolvedType != null) {
         MethodReference declaringMethod = resolvedType.getDeclaringMethod();
         
         if (declaringMethod != null) {
           current = declaringMethod.getDeclaringType();
           continue;
         }
       }
       
       current = current.getDeclaringType();
     }
     
     return false;
   }
   
   public static TypeReference findCommonSuperType(TypeReference type1, TypeReference type2) {
     VerifyArgument.notNull(type1, "type1");
     VerifyArgument.notNull(type2, "type2");
     
     if (type1 == type2) {
       return type1;
     }
     
     if (type1.isPrimitive()) {
       if (type2.isPrimitive()) {
         if (isAssignableFrom(type1, type2)) {
           return type1;
         }
         if (isAssignableFrom(type2, type1)) {
           return type2;
         }
         return doNumericPromotion(type1, type2);
       }
       return findCommonSuperType(getBoxedTypeOrSelf(type1), type2);
     }
     if (type2.isPrimitive()) {
       return findCommonSuperType(type1, getBoxedTypeOrSelf(type2));
     }
     
     int rank1 = 0;
     int rank2 = 0;
     
     TypeReference elementType1 = type1;
     TypeReference elementType2 = type2;
     
     while (elementType1.isArray()) {
       elementType1 = elementType1.getElementType();
       rank1++;
     }
     
     while (elementType2.isArray()) {
       elementType2 = elementType2.getElementType();
       rank2++;
     }
     
     if (rank1 != rank2) {
       return BuiltinTypes.Object;
     }
     
     if ((rank1 != 0) && ((elementType1.isPrimitive()) || (elementType2.isPrimitive()))) {
       if ((elementType1.isPrimitive()) && (elementType2.isPrimitive())) {
         TypeReference promotedType = doNumericPromotion(elementType1, elementType2);
         
         while (rank1-- > 0) {
           promotedType = promotedType.makeArrayType();
         }
         
         return promotedType;
       }
       return BuiltinTypes.Object;
     }
     
     while (!elementType1.isUnbounded()) {
       elementType1 = elementType1.hasSuperBound() ? elementType1.getSuperBound() : elementType1.getExtendsBound();
     }
     
 
     while (!elementType2.isUnbounded()) {
       elementType2 = elementType2.hasSuperBound() ? elementType2.getSuperBound() : elementType2.getExtendsBound();
     }
     
 
     TypeReference result = findCommonSuperTypeCore(elementType1, elementType2);
     
     while (rank1-- > 0) {
       result = result.makeArrayType();
     }
     
     return result;
   }
   
   private static TypeReference doNumericPromotion(TypeReference leftType, TypeReference rightType) {
     JvmType left = leftType.getSimpleType();
     JvmType right = rightType.getSimpleType();
     
     if (left == right) {
       return leftType;
     }
     
     if ((left == JvmType.Double) || (right == JvmType.Double)) {
       return BuiltinTypes.Double;
     }
     
     if ((left == JvmType.Float) || (right == JvmType.Float)) {
       return BuiltinTypes.Float;
     }
     
     if ((left == JvmType.Long) || (right == JvmType.Long)) {
       return BuiltinTypes.Long;
     }
     
     if (((left.isNumeric()) && (left != JvmType.Boolean)) || ((right.isNumeric()) && (right != JvmType.Boolean))) {
       return BuiltinTypes.Integer;
     }
     
     return leftType;
   }
   
   private static TypeReference findCommonSuperTypeCore(TypeReference type1, TypeReference type2) {
     if (isAssignableFrom(type1, type2)) {
       if ((type2.isGenericType()) && (!type1.isGenericType())) {
         TypeDefinition resolved1 = type1.resolve();
         
         if (resolved1 != null) {
           return substituteGenericArguments(resolved1, type2);
         }
       }
       return substituteGenericArguments(type1, type2);
     }
     
     if (isAssignableFrom(type2, type1)) {
       if ((type1.isGenericType()) && (!type2.isGenericType())) {
         TypeDefinition resolved2 = type2.resolve();
         
         if (resolved2 != null) {
           return substituteGenericArguments(resolved2, type1);
         }
       }
       return substituteGenericArguments(type2, type1);
     }
     
     TypeDefinition c = type1.resolve();
     TypeDefinition d = type2.resolve();
     
     if ((c == null) || (d == null) || (c.isInterface()) || (d.isInterface())) {
       return BuiltinTypes.Object;
     }
     
     TypeReference current = c;
     
     while (current != null) {
       for (TypeReference interfaceType : getInterfaces(current)) {
         if (isAssignableFrom(interfaceType, d)) {
           return interfaceType;
         }
       }
       
       current = getBaseType(current);
       
       if ((current != null) && 
         (isAssignableFrom(current, d))) {
         return current;
       }
     }
     
 
     return BuiltinTypes.Object;
   }
   
 
 
 
 
 
 
 
 
 
 
   public static ConversionType getConversionType(TypeReference target, TypeReference source)
   {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(target, "target");
     
     TypeReference underlyingTarget = getUnderlyingPrimitiveTypeOrSelf(target);
     TypeReference underlyingSource = getUnderlyingPrimitiveTypeOrSelf(source);
     
     if ((underlyingTarget.getSimpleType().isNumeric()) && (underlyingSource.getSimpleType().isNumeric())) {
       return getNumericConversionType(target, source);
     }
     
     if (StringUtilities.equals(target.getInternalName(), "java/lang/Object")) {
       return ConversionType.IMPLICIT;
     }
     
     if (isSameType(target, source, true)) {
       return ConversionType.IDENTITY;
     }
     
     if (isAssignableFrom(target, source, false)) {
       return ConversionType.IMPLICIT;
     }
     
     int targetRank = 0;
     int sourceRank = 0;
     
     TypeReference targetElementType = target;
     TypeReference sourceElementType = source;
     
     while (targetElementType.isArray()) {
       targetRank++;
       targetElementType = targetElementType.getElementType();
     }
     
     while (sourceElementType.isArray()) {
       sourceRank++;
       sourceElementType = sourceElementType.getElementType();
     }
     
     if (sourceRank != targetRank) {
       if (isSameType(sourceElementType, BuiltinTypes.Object)) {
         return ConversionType.EXPLICIT;
       }
       return ConversionType.NONE;
     }
     
     return ConversionType.EXPLICIT;
   }
   
   public static ConversionType getNumericConversionType(TypeReference target, TypeReference source) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(target, "target");
     
     if (isSameType(target, source)) {
       return ConversionType.IDENTITY;
     }
     Object unboxedConversion;
     if (!source.isPrimitive())
     {
       TypeReference unboxedSourceType;
       switch (source.getInternalName()) {
       case "java/lang/Byte": 
         unboxedSourceType = BuiltinTypes.Byte;
         break;
       case "java/lang/Character": 
         unboxedSourceType = BuiltinTypes.Character;
         break;
       case "java/lang/Short": 
         unboxedSourceType = BuiltinTypes.Short;
         break;
       case "java/lang/Integer": 
         unboxedSourceType = BuiltinTypes.Integer;
         break;
       case "java/lang/Long": 
         unboxedSourceType = BuiltinTypes.Long;
         break;
       case "java/lang/Float": 
         unboxedSourceType = BuiltinTypes.Float;
         break;
       case "java/lang/Double": 
         unboxedSourceType = BuiltinTypes.Double;
         break;
       case "java/lang/Boolean": 
         unboxedSourceType = BuiltinTypes.Boolean;
         break;
       default: 
         return ConversionType.NONE;
       }
       
       unboxedConversion = getNumericConversionType(target, unboxedSourceType);
       
       switch (unboxedConversion) {
       case IDENTITY: 
       case IMPLICIT: 
         return ConversionType.IMPLICIT;
       case EXPLICIT: 
         return ConversionType.NONE;
       }
       return (ConversionType)unboxedConversion;
     }
     
 
     if (!target.isPrimitive())
     {
 
       unboxedConversion = target.getInternalName();??? = -1; switch (((String)unboxedConversion).hashCode()) {case 202917116:  if (((String)unboxedConversion).equals("java/lang/Byte")) ??? = 0; break; case 1466314677:  if (((String)unboxedConversion).equals("java/lang/Character")) ??? = 1; break; case 2010652424:  if (((String)unboxedConversion).equals("java/lang/Short")) ??? = 2; break; case -607409974:  if (((String)unboxedConversion).equals("java/lang/Integer")) ??? = 3; break; case 203205232:  if (((String)unboxedConversion).equals("java/lang/Long")) ??? = 4; break; case 1998765288:  if (((String)unboxedConversion).equals("java/lang/Float")) ??? = 5; break; case 1777873605:  if (((String)unboxedConversion).equals("java/lang/Double")) ??? = 6; break; case 1794216884:  if (((String)unboxedConversion).equals("java/lang/Boolean")) ??? = 7; break; } TypeReference unboxedTargetType; switch (???) {
       case 0: 
         unboxedTargetType = BuiltinTypes.Byte;
         break;
       case 1: 
         unboxedTargetType = BuiltinTypes.Character;
         break;
       case 2: 
         unboxedTargetType = BuiltinTypes.Short;
         break;
       case 3: 
         unboxedTargetType = BuiltinTypes.Integer;
         break;
       case 4: 
         unboxedTargetType = BuiltinTypes.Long;
         break;
       case 5: 
         unboxedTargetType = BuiltinTypes.Float;
         break;
       case 6: 
         unboxedTargetType = BuiltinTypes.Double;
         break;
       case 7: 
         unboxedTargetType = BuiltinTypes.Boolean;
         break;
       default: 
         return ConversionType.NONE;
       }
       
       switch (getNumericConversionType(unboxedTargetType, source)) {
       case IDENTITY: 
         return ConversionType.IMPLICIT;
       case IMPLICIT: 
         return ConversionType.EXPLICIT_TO_UNBOXED;
       case EXPLICIT: 
         return ConversionType.EXPLICIT;
       }
       return ConversionType.NONE;
     }
     
 
     JvmType targetJvmType = target.getSimpleType();
     JvmType sourceJvmType = source.getSimpleType();
     
     if (targetJvmType == sourceJvmType) {
       return ConversionType.IDENTITY;
     }
     
     if (sourceJvmType == JvmType.Boolean) {
       return ConversionType.NONE;
     }
     
     switch (targetJvmType) {
     case Float: 
     case Double: 
       if ((sourceJvmType.isIntegral()) || (sourceJvmType.bitWidth() <= targetJvmType.bitWidth())) {
         return ConversionType.IMPLICIT;
       }
       return ConversionType.EXPLICIT;
     
     case Byte: 
     case Short: 
       if (sourceJvmType == JvmType.Character) {
         return ConversionType.EXPLICIT;
       }
     
     case Integer: 
     case Long: 
       if ((sourceJvmType.isIntegral()) && (sourceJvmType.bitWidth() <= targetJvmType.bitWidth()))
       {
 
         return ConversionType.IMPLICIT;
       }
       
       return ConversionType.EXPLICIT;
     
     case Character: 
       return sourceJvmType.isNumeric() ? ConversionType.EXPLICIT : ConversionType.NONE;
     }
     
     
     return ConversionType.NONE;
   }
   
   public static boolean hasImplicitNumericConversion(TypeReference target, TypeReference source) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(target, "target");
     
     if (target == source) {
       return true;
     }
     
     if ((!target.isPrimitive()) || (!source.isPrimitive())) {
       return false;
     }
     
     JvmType targetJvmType = target.getSimpleType();
     JvmType sourceJvmType = source.getSimpleType();
     
     if (targetJvmType == sourceJvmType) {
       return true;
     }
     
     if (sourceJvmType == JvmType.Boolean) {
       return false;
     }
     
     switch (targetJvmType) {
     case Float: 
     case Double: 
       return sourceJvmType.bitWidth() <= targetJvmType.bitWidth();
     
     case Byte: 
     case Short: 
     case Integer: 
     case Long: 
       return (sourceJvmType.isIntegral()) && (sourceJvmType.bitWidth() <= targetJvmType.bitWidth());
     }
     
     
     return false;
   }
   
   public static boolean isConvertible(TypeReference source, TypeReference target) {
     return isConvertible(source, target, true);
   }
   
   public static boolean isConvertible(TypeReference source, TypeReference target, boolean allowUnchecked) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(target, "target");
     
     boolean tPrimitive = target.isPrimitive();
     boolean sPrimitive = source.isPrimitive();
     
     if (source == BuiltinTypes.Null) {
       return !tPrimitive;
     }
     
     if ((target.isWildcardType()) && (target.isUnbounded())) {
       return !sPrimitive;
     }
     
     if (tPrimitive == sPrimitive) {
       return allowUnchecked ? isSubTypeUnchecked(source, target) : isSubType(source, target);
     }
     
 
     if (tPrimitive) {
       switch (getNumericConversionType(target, source)) {
       case IDENTITY: 
       case IMPLICIT: 
         return true;
       }
       return false;
     }
     
 
     return allowUnchecked ? isSubTypeUnchecked(getBoxedTypeOrSelf(source), target) : isSubType(getBoxedTypeOrSelf(source), target);
   }
   
   private static boolean isSubTypeUnchecked(TypeReference t, TypeReference s)
   {
     return isSubtypeUncheckedInternal(t, s);
   }
   
   private static boolean isSubtypeUncheckedInternal(TypeReference t, TypeReference s) {
     if (t == s) {
       return true;
     }
     
     if ((t == null) || (s == null)) {
       return false;
     }
     
     if ((t.isArray()) && (s.isArray())) {
       if (t.getElementType().isPrimitive()) {
         return isSameType(getElementType(t), getElementType(s));
       }
       
       return isSubTypeUnchecked(getElementType(t), getElementType(s));
     }
     
     if (isSubType(t, s)) {
       return true;
     }
     if ((t.isGenericParameter()) && (t.hasExtendsBound())) {
       return isSubTypeUnchecked(getUpperBound(t), s);
     }
     if (!isRawType(s)) {
       TypeReference t2 = asSuper(s, t);
       if ((t2 != null) && (isRawType(t2))) {
         return true;
       }
     }
     
     return false;
   }
   
   public static boolean isAssignableFrom(TypeReference target, TypeReference source) {
     return isConvertible(source, target);
   }
   
   public static boolean isAssignableFrom(TypeReference target, TypeReference source, boolean allowUnchecked) {
     return isConvertible(source, target, allowUnchecked);
   }
   
   public static boolean isSubType(TypeReference type, TypeReference baseType) {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(baseType, "baseType");
     
     return isSubType(type, baseType, true);
   }
   
   public static boolean isPrimitiveBoxType(TypeReference type) {
     VerifyArgument.notNull(type, "type");
     
     switch (type.getInternalName()) {
     case "java/lang/Byte": 
     case "java/lang/Character": 
     case "java/lang/Short": 
     case "java/lang/Integer": 
     case "java/lang/Long": 
     case "java/lang/Float": 
     case "java/lang/Double": 
     case "java/lang/Boolean": 
     case "java/lang/Void": 
       return true;
     }
     
     return false;
   }
   
   public static TypeReference getBoxedTypeOrSelf(TypeReference type)
   {
     VerifyArgument.notNull(type, "type");
     
     if (type.isPrimitive()) {
       switch (type.getSimpleType()) {
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
     return type;
   }
   
   public static TypeReference getUnderlyingPrimitiveTypeOrSelf(TypeReference type) {
     VerifyArgument.notNull(type, "type");
     
     switch (type.getInternalName()) {
     case "java/lang/Void": 
       return BuiltinTypes.Void;
     case "java/lang/Boolean": 
       return BuiltinTypes.Boolean;
     case "java/lang/Byte": 
       return BuiltinTypes.Byte;
     case "java/lang/Character": 
       return BuiltinTypes.Character;
     case "java/lang/Short": 
       return BuiltinTypes.Short;
     case "java/lang/Integer": 
       return BuiltinTypes.Integer;
     case "java/lang/Long": 
       return BuiltinTypes.Long;
     case "java/lang/Float": 
       return BuiltinTypes.Float;
     case "java/lang/Double": 
       return BuiltinTypes.Double;
     }
     return type;
   }
   
   public static TypeReference getDeclaredType(TypeReference type)
   {
     if (type == null) {
       return null;
     }
     
     TypeDefinition resolvedType = type.resolve();
     
     if (resolvedType == null) {
       return type;
     }
     
     if (resolvedType.isAnonymous()) {
       List<TypeReference> interfaces = resolvedType.getExplicitInterfaces();
       TypeReference baseType = interfaces.isEmpty() ? resolvedType.getBaseType() : (TypeReference)interfaces.get(0);
       
       if (baseType != null) {
         TypeReference asSuperType = asSuper(baseType, type);
         
         if (asSuperType != null) {
           return asSuperType;
         }
         
         return baseType.isGenericType() ? new RawType(baseType) : baseType;
       }
     }
     
     return type;
   }
   
   public static TypeReference getBaseType(TypeReference type) {
     if (type == null) {
       return null;
     }
     
     TypeDefinition resolvedType = type.resolve();
     
     if (resolvedType == null) {
       return null;
     }
     
     TypeReference baseType = resolvedType.getBaseType();
     
     if (baseType == null) {
       return null;
     }
     
     return substituteGenericArguments(baseType, type);
   }
   
   public static List<TypeReference> getInterfaces(TypeReference type) {
     List<TypeReference> result = (List)INTERFACES_VISITOR.visit(type);
     return result != null ? result : Collections.emptyList();
   }
   
   public static TypeReference asSubType(TypeReference type, TypeReference baseType) {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(baseType, "baseType");
     
     TypeReference effectiveType = type;
     
     if ((type instanceof RawType)) {
       effectiveType = type.getUnderlyingType();
     }
     else if (isRawType(type)) {
       TypeDefinition resolvedType = type.resolve();
       effectiveType = resolvedType != null ? resolvedType : type;
     }
     
     return (TypeReference)AS_SUBTYPE_VISITOR.visit(baseType, effectiveType);
   }
   
   public static TypeReference asSuper(TypeReference type, TypeReference subType) {
     VerifyArgument.notNull(subType, "t");
     VerifyArgument.notNull(type, "s");
     
     return (TypeReference)AS_SUPER_VISITOR.visit(subType, type);
   }
   
   public static Map<TypeReference, TypeReference> getGenericSubTypeMappings(TypeReference type, TypeReference baseType)
   {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(baseType, "baseType");
     
     if ((type.isArray()) && (baseType.isArray())) {
       TypeReference elementType = type.getElementType();
       TypeReference baseElementType = baseType.getElementType();
       
       while ((elementType.isArray()) && (baseElementType.isArray())) {
         elementType = elementType.getElementType();
         baseElementType = baseElementType.getElementType();
       }
       
       return getGenericSubTypeMappings(elementType, baseElementType);
     }
     
     TypeReference current = type;
     
     List<? extends TypeReference> baseArguments;
     List<? extends TypeReference> baseArguments;
     if (baseType.isGenericDefinition()) {
       baseArguments = baseType.getGenericParameters();
     } else { List<? extends TypeReference> baseArguments;
       if (baseType.isGenericType()) {
         baseArguments = ((IGenericInstance)baseType).getTypeArguments();
       }
       else {
         baseArguments = Collections.emptyList();
       }
     }
     TypeDefinition resolvedBaseType = baseType.resolve();
     
     while (current != null) {
       TypeDefinition resolved = current.resolve();
       
       if ((resolvedBaseType != null) && (resolvedBaseType.isGenericDefinition()) && (isSameType(resolved, resolvedBaseType)))
       {
 
 
         if (((current instanceof IGenericInstance)) && ((baseType instanceof IGenericInstance)))
         {
 
           List<? extends TypeReference> typeArguments = ((IGenericInstance)current).getTypeArguments();
           
           if (baseArguments.size() == typeArguments.size()) {
             Map<TypeReference, TypeReference> map = new HashMap();
             
             for (int i = 0; i < typeArguments.size(); i++) {
               map.put(typeArguments.get(i), baseArguments.get(i));
             }
             
             return map;
           }
         }
         else if (((baseType instanceof IGenericInstance)) && (resolved.isGenericDefinition()))
         {
 
           List<GenericParameter> genericParameters = resolved.getGenericParameters();
           List<? extends TypeReference> typeArguments = ((IGenericInstance)baseType).getTypeArguments();
           
           if (genericParameters.size() == typeArguments.size()) {
             Map<TypeReference, TypeReference> map = new HashMap();
             
             for (int i = 0; i < typeArguments.size(); i++) {
               map.put(genericParameters.get(i), typeArguments.get(i));
             }
             
             return map;
           }
         }
       }
       
       if ((resolvedBaseType != null) && (resolvedBaseType.isInterface())) {
         for (TypeReference interfaceType : getInterfaces(current)) {
           Map<TypeReference, TypeReference> interfaceMap = getGenericSubTypeMappings(interfaceType, baseType);
           
           if (!interfaceMap.isEmpty()) {
             return interfaceMap;
           }
         }
       }
       
       current = getBaseType(current);
     }
     
     return Collections.emptyMap();
   }
   
   public static MethodReference asMemberOf(MethodReference method, TypeReference baseType) {
     VerifyArgument.notNull(method, "method");
     VerifyArgument.notNull(baseType, "baseType");
     
 
 
     TypeReference base = baseType;
     MethodReference asMember;
     MethodReference asMember; if ((baseType instanceof RawType)) {
       asMember = erase(method);
     }
     else {
       while ((base.isGenericParameter()) || (base.isWildcardType())) {
         if (base.hasExtendsBound()) {
           base = getUpperBound(base);
         }
         else {
           base = BuiltinTypes.Object;
         }
       }
       
       TypeReference asSuper = asSuper(method.getDeclaringType(), base);
       
       Map<TypeReference, TypeReference> map;
       try
       {
         map = adapt(method.getDeclaringType(), asSuper != null ? asSuper : base);
       }
       catch (AdaptFailure ignored) {
         map = getGenericSubTypeMappings(method.getDeclaringType(), asSuper != null ? asSuper : base);
       }
       
       asMember = TypeSubstitutionVisitor.instance().visitMethod(method, map);
       
       if ((asMember != method) && ((asMember instanceof GenericMethodInstance))) {
         ((GenericMethodInstance)asMember).setDeclaringType(asSuper != null ? asSuper : base);
       }
     }
     
     MethodReference result = specializeIfNecessary(method, asMember, base);
     
     return result;
   }
   
 
 
 
   private static MethodReference specializeIfNecessary(MethodReference originalMethod, MethodReference asMember, TypeReference baseType)
   {
     if ((baseType.isArray()) && (StringUtilities.equals(asMember.getName(), "clone")) && (asMember.getParameters().isEmpty()))
     {
 
 
       return ensureReturnType(originalMethod, asMember, baseType, baseType);
     }
     if ((StringUtilities.equals(asMember.getName(), "getClass")) && (asMember.getParameters().isEmpty()))
     {
 
 
 
       TypeDefinition resolvedType = baseType.resolve();
       TypeReference classType;
       TypeDefinition resolvedClassType;
       if ((resolvedType == null) || ((classType = resolvedType.getResolver().lookupType("java/lang/Class")) == null) || ((resolvedClassType = classType.resolve()) == null))
       {
 
 
         resolvedType = originalMethod.getDeclaringType().resolve(); }
       TypeReference classType;
       TypeDefinition resolvedClassType;
       if ((resolvedType == null) || ((classType = resolvedType.getResolver().lookupType("java/lang/Class")) == null) || ((resolvedClassType = classType.resolve()) == null))
       {
 
 
         return asMember; }
       TypeDefinition resolvedClassType;
       TypeReference classType;
       if (resolvedClassType.isGenericType()) {
         MethodDefinition resolvedMethod = originalMethod.resolve();
         
         return new GenericMethodInstance(baseType, resolvedMethod != null ? resolvedMethod : asMember, resolvedClassType.makeGenericType(new TypeReference[] { WildcardType.makeExtends(erase(baseType)) }), Collections.emptyList(), Collections.emptyList());
       }
       
 
 
 
 
 
 
       return asMember;
     }
     
     return asMember;
   }
   
 
 
 
 
   private static MethodReference ensureReturnType(MethodReference originalMethod, MethodReference method, TypeReference returnType, TypeReference declaringType)
   {
     if (isSameType(method.getReturnType(), returnType, true)) {
       return method;
     }
     
     MethodDefinition resolvedMethod = originalMethod.resolve();
     List<TypeReference> typeArguments;
     List<TypeReference> typeArguments;
     if (((method instanceof IGenericInstance)) && (method.isGenericMethod())) {
       typeArguments = ((IGenericInstance)method).getTypeArguments();
     }
     else {
       typeArguments = Collections.emptyList();
     }
     
     return new GenericMethodInstance(declaringType, resolvedMethod != null ? resolvedMethod : originalMethod, returnType, copyParameters(method.getParameters()), typeArguments);
   }
   
 
 
 
 
 
   public static FieldReference asMemberOf(FieldReference field, TypeReference baseType)
   {
     VerifyArgument.notNull(field, "field");
     VerifyArgument.notNull(baseType, "baseType");
     
     Map<TypeReference, TypeReference> map = adapt(field.getDeclaringType(), baseType);
     
     return TypeSubstitutionVisitor.instance().visitField(field, map);
   }
   
 
 
   public static TypeReference substituteGenericArguments(TypeReference inputType, TypeReference substitutionsProvider)
   {
     if ((inputType == null) || (substitutionsProvider == null)) {
       return inputType;
     }
     
     return substituteGenericArguments(inputType, adapt(inputType, substitutionsProvider));
   }
   
 
 
   public static TypeReference substituteGenericArguments(TypeReference inputType, MethodReference substitutionsProvider)
   {
     if (inputType == null) {
       return null;
     }
     
     if ((substitutionsProvider == null) || (!isGenericSubstitutionNeeded(inputType))) {
       return inputType;
     }
     
     TypeReference declaringType = substitutionsProvider.getDeclaringType();
     
     assert (declaringType != null);
     
     if ((!substitutionsProvider.isGenericMethod()) && (!declaringType.isGenericType())) {
       return null;
     }
     
 
     List<? extends TypeReference> methodGenericParameters;
     
     List<? extends TypeReference> methodGenericParameters;
     
     if (substitutionsProvider.isGenericMethod()) {
       methodGenericParameters = substitutionsProvider.getGenericParameters();
     }
     else
       methodGenericParameters = Collections.emptyList();
     List<? extends TypeReference> methodTypeArguments;
     List<? extends TypeReference> methodTypeArguments;
     if (substitutionsProvider.isGenericDefinition()) {
       methodTypeArguments = methodGenericParameters;
     }
     else
       methodTypeArguments = ((IGenericInstance)substitutionsProvider).getTypeArguments();
     List<? extends TypeReference> typeArguments;
     List<? extends TypeReference> genericParameters;
     List<? extends TypeReference> typeArguments; if (declaringType.isGenericType()) {
       List<? extends TypeReference> genericParameters = declaringType.getGenericParameters();
       List<? extends TypeReference> typeArguments;
       if (declaringType.isGenericDefinition()) {
         typeArguments = genericParameters;
       }
       else {
         typeArguments = ((IGenericInstance)declaringType).getTypeArguments();
       }
     }
     else {
       genericParameters = Collections.emptyList();
       typeArguments = Collections.emptyList();
     }
     
     if ((methodTypeArguments.isEmpty()) && (typeArguments.isEmpty())) {
       return inputType;
     }
     
     Map<TypeReference, TypeReference> map = new HashMap();
     
     if (methodTypeArguments.size() == methodGenericParameters.size()) {
       for (int i = 0; i < methodTypeArguments.size(); i++) {
         map.put(methodGenericParameters.get(i), methodTypeArguments.get(i));
       }
     }
     
     if (typeArguments.size() == genericParameters.size()) {
       for (int i = 0; i < typeArguments.size(); i++) {
         map.put(genericParameters.get(i), typeArguments.get(i));
       }
     }
     
     return substituteGenericArguments(inputType, map);
   }
   
 
 
   public static TypeReference substituteGenericArguments(TypeReference inputType, Map<TypeReference, TypeReference> substitutionsProvider)
   {
     if (inputType == null) {
       return null;
     }
     
     if ((substitutionsProvider == null) || (substitutionsProvider.isEmpty())) {
       return inputType;
     }
     
     return TypeSubstitutionVisitor.instance().visit(inputType, substitutionsProvider);
   }
   
   private static boolean isGenericSubstitutionNeeded(TypeReference type) {
     if (type == null) {
       return false;
     }
     
     TypeDefinition resolvedType = type.resolve();
     
     return (resolvedType != null) && (resolvedType.containsGenericParameters());
   }
   
   public static List<MethodReference> findMethods(TypeReference type)
   {
     return findMethods(type, Predicates.alwaysTrue());
   }
   
 
 
   public static List<MethodReference> findMethods(TypeReference type, Predicate<? super MethodReference> filter)
   {
     return findMethods(type, filter, false);
   }
   
 
 
 
   public static List<MethodReference> findMethods(TypeReference type, Predicate<? super MethodReference> filter, boolean includeBridgeMethods)
   {
     return findMethods(type, filter, includeBridgeMethods, false);
   }
   
 
 
 
 
   public static List<MethodReference> findMethods(TypeReference type, Predicate<? super MethodReference> filter, boolean includeBridgeMethods, boolean includeOverriddenMethods)
   {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(filter, "filter");
     
     Set<String> descriptors = new HashSet();
     ArrayDeque<TypeReference> agenda = new ArrayDeque();
     
     List<MethodReference> results = null;
     
     agenda.addLast(getUpperBound(type));
     descriptors.add(type.getInternalName());
     
     while (!agenda.isEmpty()) {
       TypeDefinition resolvedType = ((TypeReference)agenda.removeFirst()).resolve();
       
       if (resolvedType == null) {
         break;
       }
       
       TypeReference baseType = resolvedType.getBaseType();
       
       if ((baseType != null) && (descriptors.add(baseType.getInternalName()))) {
         agenda.addLast(baseType);
       }
       
       for (TypeReference interfaceType : resolvedType.getExplicitInterfaces()) {
         if ((interfaceType != null) && (descriptors.add(interfaceType.getInternalName()))) {
           agenda.addLast(interfaceType);
         }
       }
       
       for (MethodDefinition method : resolvedType.getDeclaredMethods()) {
         if ((includeBridgeMethods) || (!method.isBridgeMethod()))
         {
 
 
           if (filter.test(method)) {
             String key = (includeOverriddenMethods ? method.getFullName() : method.getName()) + ":" + method.getErasedSignature();
             
             if (descriptors.add(key)) {
               if (results == null) {
                 results = new ArrayList();
               }
               
               MethodReference asMember = asMemberOf(method, type);
               
               results.add(asMember != null ? asMember : method);
             }
           }
         }
       }
     }
     return results != null ? results : Collections.emptyList();
   }
   
   public static boolean isOverloadCheckingRequired(MethodReference method)
   {
     MethodDefinition resolved = method.resolve();
     boolean isVarArgs = (resolved != null) && (resolved.isVarArgs());
     TypeReference declaringType = (resolved != null ? resolved : method).getDeclaringType();
     final int parameterCount = (resolved != null ? resolved.getParameters() : method.getParameters()).size();
     
     List<MethodReference> methods = findMethods(declaringType, Predicates.and(MetadataFilters.matchName(method.getName()), new Predicate()
     {
 
 
       public boolean test(MethodReference m)
       {
 
         List<ParameterDefinition> p = m.getParameters();
         
         MethodDefinition r = (m instanceof MethodDefinition) ? (MethodDefinition)m : m.resolve();
         
 
         if ((r != null) && (r.isBridgeMethod())) {
           return false;
         }
         
         if (this.val$isVarArgs) {
           if ((r != null) && (r.isVarArgs())) {
             return true;
           }
           return p.size() >= parameterCount;
         }
         
         if (p.size() < parameterCount) {
           return (r != null) && (r.isVarArgs());
         }
         
         return p.size() == parameterCount;
 
       }
       
 
     }));
     return methods.size() > 1;
   }
   
   public static TypeReference getLowerBound(TypeReference t) {
     return (TypeReference)LOWER_BOUND_VISITOR.visit(t);
   }
   
   public static TypeReference getUpperBound(TypeReference t) {
     return (TypeReference)UPPER_BOUND_VISITOR.visit(t);
   }
   
   public static TypeReference getElementType(TypeReference t) {
     if (t.isArray()) {
       return t.getElementType();
     }
     
     if (t.isWildcardType()) {
       return getElementType(getUpperBound(t));
     }
     
     return null;
   }
   
   public static TypeReference getSuperType(TypeReference t) {
     if (t == null) {
       return null;
     }
     
     return (TypeReference)SUPER_VISITOR.visit(t);
   }
   
   public static boolean isSubTypeNoCapture(TypeReference type, TypeReference baseType) {
     return isSubType(type, baseType, false);
   }
   
   public static boolean isSubType(TypeReference type, TypeReference baseType, boolean capture) {
     if (type == baseType) {
       return true;
     }
     
     if ((type == null) || (baseType == null)) {
       return false;
     }
     
     if ((baseType instanceof CompoundTypeReference)) {
       CompoundTypeReference c = (CompoundTypeReference)baseType;
       
       if (!isSubType(type, getSuperType(c), capture)) {
         return false;
       }
       
       for (TypeReference interfaceType : c.getInterfaces()) {
         if (!isSubType(type, interfaceType, capture)) {
           return false;
         }
       }
       
       return true;
     }
     
     TypeReference lower = getLowerBound(baseType);
     
     if (lower != baseType) {
       return isSubType(capture ? capture(type) : type, lower, false);
     }
     
     return ((Boolean)IS_SUBTYPE_VISITOR.visit(capture ? capture(type) : type, baseType)).booleanValue();
   }
   
   private static TypeReference capture(TypeReference type)
   {
     return type;
   }
   
   public static Map<TypeReference, TypeReference> adapt(TypeReference source, TypeReference target) {
     Adapter adapter = new Adapter(null);
     adapter.visit(source, target);
     return adapter.mapping;
   }
   
   private static Map<TypeReference, TypeReference> adaptSelf(TypeReference t) {
     TypeDefinition r = t.resolve();
     
     return r != null ? adapt(r, t) : Collections.emptyMap();
   }
   
   private static TypeReference rewriteSupers(TypeReference t)
   {
     if (!(t instanceof IGenericInstance)) {
       return t;
     }
     
     Map<TypeReference, TypeReference> map = adaptSelf(t);
     
     if (map.isEmpty()) {
       return t;
     }
     
     Map<TypeReference, TypeReference> rewrite = null;
     
     for (TypeReference k : map.keySet()) {
       TypeReference original = (TypeReference)map.get(k);
       
       TypeReference s = rewriteSupers(original);
       
       if ((s.hasSuperBound()) && (!s.hasExtendsBound())) {
         s = WildcardType.unbounded();
         
         if (rewrite == null) {
           rewrite = new HashMap(map);
         }
       }
       else if (s != original) {
         s = WildcardType.makeExtends(getUpperBound(s));
         
         if (rewrite == null) {
           rewrite = new HashMap(map);
         }
       }
       
       if (rewrite != null) {
         map.put(k, s);
       }
     }
     
     if (rewrite != null) {
       return substituteGenericArguments(t, rewrite);
     }
     
 
     return t;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static boolean containsType(TypeReference t, TypeReference s)
   {
     return ((Boolean)CONTAINS_TYPE_VISITOR.visit(t, s)).booleanValue();
   }
   
   public static boolean isSameType(TypeReference t, TypeReference s) {
     return isSameType(t, s, false);
   }
   
   public static boolean isSameType(TypeReference t, TypeReference s, boolean strict) {
     if (t == s) {
       return true;
     }
     
     if ((t == null) || (s == null)) {
       return false;
     }
     
     return (strict ? SAME_TYPE_VISITOR_STRICT.visit(t, s) : SAME_TYPE_VISITOR_LOOSE.visit(t, s)).booleanValue();
   }
   
   public static boolean areSameTypes(List<? extends TypeReference> t, List<? extends TypeReference> s)
   {
     return areSameTypes(t, s, false);
   }
   
 
 
 
   public static boolean areSameTypes(List<? extends TypeReference> t, List<? extends TypeReference> s, boolean strict)
   {
     if (t.size() != s.size()) {
       return false;
     }
     
     int i = 0; for (int n = t.size(); i < n; i++) {
       if (!isSameType((TypeReference)t.get(i), (TypeReference)s.get(i), strict)) {
         return false;
       }
     }
     
     return true;
   }
   
   private static boolean isCaptureOf(TypeReference t, TypeReference s) {
     return isSameWildcard(t, s);
   }
   
   private static boolean isSameWildcard(TypeReference t, TypeReference s) {
     VerifyArgument.notNull(t, "t");
     VerifyArgument.notNull(s, "s");
     
     if ((!t.isWildcardType()) || (!s.isWildcardType())) {
       return false;
     }
     
     if (t.isUnbounded()) {
       return s.isUnbounded();
     }
     
     if (t.hasSuperBound()) {
       return (s.hasSuperBound()) && (isSameType(t.getSuperBound(), s.getSuperBound()));
     }
     
     return (s.hasExtendsBound()) && (isSameType(t.getExtendsBound(), s.getExtendsBound()));
   }
   
   private static List<? extends TypeReference> getTypeArguments(TypeReference t) {
     if ((t instanceof IGenericInstance)) {
       return ((IGenericInstance)t).getTypeArguments();
     }
     
     if (t.isGenericType()) {
       return t.getGenericParameters();
     }
     
     return Collections.emptyList();
   }
   
   private static boolean containsType(List<? extends TypeReference> t, List<? extends TypeReference> s) {
     if (t.size() != s.size()) {
       return false;
     }
     
     if (t.isEmpty()) {
       return true;
     }
     
     int i = 0; for (int n = t.size(); i < n; i++) {
       if (!containsType((TypeReference)t.get(i), (TypeReference)s.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   private static boolean containsTypeEquivalent(TypeReference t, TypeReference s) {
     return (s == t) || ((containsType(t, s)) && (containsType(s, t)));
   }
   
   private static boolean containsTypeEquivalent(List<? extends TypeReference> t, List<? extends TypeReference> s)
   {
     if (t.size() != s.size()) {
       return false;
     }
     
     int i = 0; for (int n = t.size(); i < n; i++) {
       if (!containsTypeEquivalent((TypeReference)t.get(i), (TypeReference)s.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   private static final ThreadLocal<HashSet<Pair<TypeReference, TypeReference>>> CONTAINS_TYPE_CACHE = new ThreadLocal()
   {
     protected final HashSet<Pair<TypeReference, TypeReference>> initialValue()
     {
       return new HashSet();
     }
   };
   
   private static final ThreadLocal<HashSet<Pair<TypeReference, TypeReference>>> ADAPT_CACHE = new ThreadLocal()
   {
     protected final HashSet<Pair<TypeReference, TypeReference>> initialValue()
     {
       return new HashSet();
     }
   };
   
   private static boolean containsTypeRecursive(TypeReference t, TypeReference s) {
     HashSet<Pair<TypeReference, TypeReference>> cache = (HashSet)CONTAINS_TYPE_CACHE.get();
     Pair<TypeReference, TypeReference> pair = new Pair(t, s);
     
     if (cache.add(pair)) {
       try {
         return containsType(getTypeArguments(t), getTypeArguments(s));
       }
       finally {
         cache.remove(pair);
       }
     }
     
     return containsType(getTypeArguments(t), getTypeArguments(rewriteSupers(s)));
   }
   
   private static TypeReference arraySuperType(TypeReference t)
   {
     TypeDefinition resolved = t.resolve();
     
     if (resolved != null) {
       IMetadataResolver resolver = resolved.getResolver();
       TypeReference cloneable = resolver.lookupType("java/lang/Cloneable");
       TypeReference serializable = resolver.lookupType("java/io/Serializable");
       
       if (cloneable != null) {
         if (serializable != null) {
           return new CompoundTypeReference(null, ArrayUtilities.asUnmodifiableList(new TypeReference[] { cloneable, serializable }));
         }
         
 
 
         return cloneable;
       }
       
       if (serializable != null) {
         return serializable;
       }
     }
     
     return BuiltinTypes.Object;
   }
   
   public static boolean isRawType(TypeReference t) {
     if (t == null) {
       return false;
     }
     
     if ((t instanceof RawType)) {
       return true;
     }
     
     if (t.isGenericType()) {
       return false;
     }
     
     TypeReference r = t.resolve();
     
     if ((r != null) && (r.isGenericType())) {
       return true;
     }
     
     return false;
   }
   
   public static int getUnboundGenericParameterCount(TypeReference t) {
     if ((t == null) || ((t instanceof RawType)) || (!t.isGenericType())) {
       return 0;
     }
     
     List<GenericParameter> genericParameters = t.getGenericParameters();
     
     if (t.isGenericDefinition()) {
       return genericParameters.size();
     }
     
     IGenericParameterProvider genericDefinition = ((IGenericInstance)t).getGenericDefinition();
     
     if (!genericDefinition.isGenericDefinition()) {
       return 0;
     }
     
     List<TypeReference> typeArguments = ((IGenericInstance)t).getTypeArguments();
     
     assert (genericParameters.size() == typeArguments.size());
     
     int count = 0;
     
     for (int i = 0; i < genericParameters.size(); i++) {
       GenericParameter genericParameter = (GenericParameter)genericParameters.get(i);
       TypeReference typeArgument = (TypeReference)typeArguments.get(i);
       
       if (isSameType(genericParameter, typeArgument, true)) {
         count++;
       }
     }
     
     return count;
   }
   
   public static List<TypeReference> eraseRecursive(List<TypeReference> types) {
     ArrayList<TypeReference> result = null;
     
     int i = 0; for (int n = types.size(); i < n; i++) {
       TypeReference type = (TypeReference)types.get(i);
       TypeReference erased = eraseRecursive(type);
       
       if (result != null) {
         result.set(i, erased);
       }
       else if (type != erased) {
         result = new ArrayList(types);
         result.set(i, erased);
       }
     }
     
     return result != null ? result : types;
   }
   
   public static TypeReference eraseRecursive(TypeReference type) {
     return erase(type, true);
   }
   
   private static boolean eraseNotNeeded(TypeReference type) {
     return (type == null) || ((type instanceof RawType)) || (type.isPrimitive()) || (StringUtilities.equals(type.getInternalName(), CommonTypeReferences.String.getInternalName()));
   }
   
 
 
   public static TypeReference erase(TypeReference type)
   {
     return erase(type, false);
   }
   
   public static TypeReference erase(TypeReference type, boolean recurse) {
     if (eraseNotNeeded(type)) {
       return type;
     }
     
     return (TypeReference)type.accept(ERASE_VISITOR, Boolean.valueOf(recurse));
   }
   
   public static MethodReference erase(MethodReference method) {
     if (method != null) {
       MethodReference baseMethod = method;
       
       MethodDefinition resolvedMethod = baseMethod.resolve();
       
       if (resolvedMethod != null) {
         baseMethod = resolvedMethod;
       }
       else if ((baseMethod instanceof IGenericInstance)) {
         baseMethod = (MethodReference)((IGenericInstance)baseMethod).getGenericDefinition();
       }
       
       if (baseMethod != null) {
         return new RawMethod(baseMethod);
       }
     }
     return method;
   }
   
 
 
   private static TypeReference classBound(TypeReference t)
   {
     return t;
   }
   
   public static boolean isOverride(MethodDefinition method, MethodReference ancestorMethod) {
     MethodDefinition resolvedAncestor = ancestorMethod.resolve();
     
     if ((resolvedAncestor == null) || (resolvedAncestor.isFinal()) || (resolvedAncestor.isPrivate()) || (resolvedAncestor.isStatic())) {
       return false;
     }
     
     int modifiers = method.getModifiers() & 0x7;
     int ancestorModifiers = resolvedAncestor.getModifiers() & 0x7;
     
     if (modifiers != ancestorModifiers) {
       return false;
     }
     
     if (!StringUtilities.equals(method.getName(), ancestorMethod.getName())) {
       return false;
     }
     
     if (method.getDeclaringType().isInterface()) {
       return false;
     }
     
     MethodDefinition resolved = method.resolve();
     
     TypeReference declaringType = erase(resolved != null ? resolved.getDeclaringType() : method.getDeclaringType());
     
 
 
 
     TypeReference ancestorDeclaringType = erase(resolvedAncestor.getDeclaringType());
     
     if (isSameType(declaringType, ancestorDeclaringType)) {
       return false;
     }
     
     if (StringUtilities.equals(method.getErasedSignature(), ancestorMethod.getErasedSignature())) {
       return true;
     }
     
     if (!isSubType(declaringType, ancestorDeclaringType)) {
       return false;
     }
     
     List<ParameterDefinition> parameters = method.getParameters();
     List<ParameterDefinition> ancestorParameters = ancestorMethod.getParameters();
     
     if (parameters.size() != ancestorParameters.size()) {
       return false;
     }
     
     TypeReference ancestorReturnType = erase(ancestorMethod.getReturnType());
     TypeReference baseReturnType = erase(method.getReturnType());
     
     if (!isAssignableFrom(ancestorReturnType, baseReturnType)) {
       return false;
     }
     
     int i = 0; for (int n = ancestorParameters.size(); i < n; i++) {
       TypeReference parameterType = erase(((ParameterDefinition)parameters.get(i)).getParameterType());
       TypeReference ancestorParameterType = erase(((ParameterDefinition)ancestorParameters.get(i)).getParameterType());
       
       if (!isSameType(parameterType, ancestorParameterType, false)) {
         return false;
       }
     }
     
     return true;
   }
   
 
 
   private static final TypeMapper<Void> UPPER_BOUND_VISITOR = new TypeMapper()
   {
     public TypeReference visitType(TypeReference t, Void ignored) {
       if ((t.isWildcardType()) || (t.isGenericParameter()) || ((t instanceof ICapturedType))) {
         return (t.isUnbounded()) || (t.hasSuperBound()) ? BuiltinTypes.Object : (TypeReference)visit(t.getExtendsBound());
       }
       
       return t;
     }
     
     public TypeReference visitCapturedType(CapturedType t, Void ignored)
     {
       return t.getExtendsBound();
     }
   };
   
   private static final TypeMapper<Void> LOWER_BOUND_VISITOR = new TypeMapper()
   {
     public TypeReference visitWildcard(WildcardType t, Void ignored) {
       return t.hasSuperBound() ? (TypeReference)visit(t.getSuperBound()) : BuiltinTypes.Bottom;
     }
     
 
     public TypeReference visitCapturedType(CapturedType t, Void ignored)
     {
       return t.getSuperBound();
     }
   };
   
   private static final TypeRelation IS_SUBTYPE_VISITOR = new TypeRelation()
   {
     public Boolean visitArrayType(ArrayType t, TypeReference s) {
       if (s.isArray()) {
         TypeReference et = MetadataHelper.getElementType(t);
         TypeReference es = MetadataHelper.getElementType(s);
         
         if (et.isPrimitive()) {
           return Boolean.valueOf(MetadataHelper.isSameType(et, es));
         }
         
         return Boolean.valueOf(MetadataHelper.isSubTypeNoCapture(et, es));
       }
       
       String sName = s.getInternalName();
       
       return Boolean.valueOf((StringUtilities.equals(sName, "java/lang/Object")) || (StringUtilities.equals(sName, "java/lang/Cloneable")) || (StringUtilities.equals(sName, "java/io/Serializable")));
     }
     
 
 
     public Boolean visitBottomType(TypeReference t, TypeReference s)
     {
       switch (MetadataHelper.14.$SwitchMap$com$strobel$assembler$metadata$JvmType[t.getSimpleType().ordinal()]) {
       case 10: 
       case 11: 
       case 12: 
         return Boolean.valueOf(true);
       }
       
       return Boolean.valueOf(false);
     }
     
 
     public Boolean visitClassType(TypeReference t, TypeReference s)
     {
       TypeReference superType = MetadataHelper.asSuper(s, t);
       
       return Boolean.valueOf((superType != null) && (StringUtilities.equals(superType.getInternalName(), s.getInternalName())) && ((!(s instanceof IGenericInstance)) || (MetadataHelper.containsTypeRecursive(s, superType))) && (MetadataHelper.isSubTypeNoCapture(superType.getDeclaringType(), s.getDeclaringType())));
     }
     
 
 
 
 
 
 
 
 
 
     public Boolean visitCompoundType(CompoundTypeReference t, TypeReference s)
     {
       return (Boolean)super.visitCompoundType(t, s);
     }
     
     public Boolean visitGenericParameter(GenericParameter t, TypeReference s)
     {
       return Boolean.valueOf(MetadataHelper.isSubTypeNoCapture(t.hasExtendsBound() ? t.getExtendsBound() : BuiltinTypes.Object, s));
     }
     
 
 
 
     public Boolean visitParameterizedType(TypeReference t, TypeReference s)
     {
       return visitClassType(t, s);
     }
     
     public Boolean visitPrimitiveType(PrimitiveType t, TypeReference s)
     {
       JvmType jt = t.getSimpleType();
       JvmType js = s.getSimpleType();
       
       switch (MetadataHelper.14.$SwitchMap$com$strobel$assembler$metadata$JvmType[js.ordinal()]) {
       case 8: 
         return Boolean.valueOf(jt == JvmType.Boolean);
       
       case 3: 
         return Boolean.valueOf((js != JvmType.Character) && (jt.isIntegral()) && (jt.bitWidth() <= js.bitWidth()));
       
       case 7: 
         return Boolean.valueOf(jt == JvmType.Character);
       
       case 4: 
         if (jt == JvmType.Character) {
           return Boolean.valueOf(false);
         }
       
       case 5: 
       case 6: 
         return Boolean.valueOf((jt.isIntegral()) && (jt.bitWidth() <= js.bitWidth()));
       
       case 1: 
       case 2: 
         return Boolean.valueOf((jt.isIntegral()) || (jt.bitWidth() <= js.bitWidth()));
       
       case 9: 
         return Boolean.valueOf(s.getSimpleType() == JvmType.Void);
       }
       
       return Boolean.FALSE;
     }
     
 
     public Boolean visitRawType(RawType t, TypeReference s)
     {
       return visitClassType(t, s);
     }
     
 
 
 
     public Boolean visitWildcard(WildcardType t, TypeReference s)
     {
       return Boolean.FALSE;
     }
     
     public Boolean visitCapturedType(CapturedType t, TypeReference s)
     {
       return Boolean.valueOf(MetadataHelper.isSubTypeNoCapture(t.hasExtendsBound() ? t.getExtendsBound() : BuiltinTypes.Object, s));
     }
     
 
 
 
     public Boolean visitType(TypeReference t, TypeReference s)
     {
       return Boolean.FALSE;
     }
   };
   
   private static final TypeRelation CONTAINS_TYPE_VISITOR = new TypeRelation() {
     private TypeReference U(TypeReference t) {
       TypeReference current = t;
       
       while (current.isWildcardType()) {
         if (current.isUnbounded()) {
           return BuiltinTypes.Object;
         }
         
         if (current.hasSuperBound()) {
           return current.getSuperBound();
         }
         
         current = current.getExtendsBound();
       }
       
       return current;
     }
     
     private TypeReference L(TypeReference t) {
       TypeReference current = t;
       
       while (current.isWildcardType()) {
         if ((current.isUnbounded()) || (current.hasExtendsBound())) {
           return BuiltinTypes.Bottom;
         }
         
         current = current.getSuperBound();
       }
       
       return current;
     }
     
     public Boolean visitType(TypeReference t, TypeReference s)
     {
       return Boolean.valueOf(MetadataHelper.isSameType(t, s));
     }
     
     public Boolean visitWildcard(WildcardType t, TypeReference s)
     {
       return Boolean.valueOf((MetadataHelper.isSameWildcard(t, s)) || (MetadataHelper.isCaptureOf(s, t)) || (((t.hasExtendsBound()) || (MetadataHelper.isSubTypeNoCapture(L(t), MetadataHelper.getLowerBound(s)))) && ((t.hasSuperBound()) || (MetadataHelper.isSubTypeNoCapture(MetadataHelper.getUpperBound(s), U(t))))));
     }
   };
   
 
 
 
   private static final TypeMapper<TypeReference> AS_SUPER_VISITOR = new TypeMapper()
   {
     public TypeReference visitType(TypeReference t, TypeReference s) {
       return null;
     }
     
     public TypeReference visitArrayType(ArrayType t, TypeReference s)
     {
       return MetadataHelper.isSubType(t, s) ? s : null;
     }
     
     public TypeReference visitClassType(TypeReference t, TypeReference s)
     {
       if (StringUtilities.equals(t.getInternalName(), s.getInternalName())) {
         return t;
       }
       
       TypeReference st = MetadataHelper.getSuperType(t);
       
       if ((st != null) && ((st.getSimpleType() == JvmType.Object) || (st.getSimpleType() == JvmType.TypeVariable)))
       {
 
 
         TypeReference x = MetadataHelper.asSuper(s, st);
         
         if (x != null) {
           return x;
         }
       }
       
       TypeDefinition ds = s.resolve();
       
       if ((ds != null) && (ds.isInterface())) {
         for (TypeReference i : MetadataHelper.getInterfaces(t)) {
           TypeReference x = MetadataHelper.asSuper(s, i);
           
           if (x != null) {
             return x;
           }
         }
       }
       
       return null;
     }
     
     public TypeReference visitGenericParameter(GenericParameter t, TypeReference s)
     {
       if (MetadataHelper.isSameType(t, s)) {
         return t;
       }
       return MetadataHelper.asSuper(s, t.hasExtendsBound() ? t.getExtendsBound() : BuiltinTypes.Object);
     }
     
     public TypeReference visitNullType(TypeReference t, TypeReference s)
     {
       return (TypeReference)super.visitNullType(t, s);
     }
     
 
 
     public TypeReference visitParameterizedType(TypeReference t, TypeReference s)
     {
       return visitClassType(t, s);
     }
     
     public TypeReference visitPrimitiveType(PrimitiveType t, TypeReference s)
     {
       return (TypeReference)super.visitPrimitiveType(t, s);
     }
     
     public TypeReference visitRawType(RawType t, TypeReference s)
     {
       return visitClassType(t, s);
     }
     
     public TypeReference visitWildcard(WildcardType t, TypeReference s)
     {
       return (TypeReference)super.visitWildcard(t, s);
     }
   };
   
   private static final TypeMapper<Void> SUPER_VISITOR = new TypeMapper()
   {
     public TypeReference visitType(TypeReference t, Void ignored) {
       return null;
     }
     
     public TypeReference visitArrayType(ArrayType t, Void ignored)
     {
       TypeReference et = MetadataHelper.getElementType(t);
       
       if ((et.isPrimitive()) || (MetadataHelper.isSameType(et, BuiltinTypes.Object))) {
         return MetadataHelper.arraySuperType(et);
       }
       
       TypeReference superType = MetadataHelper.getSuperType(et);
       
       return superType != null ? superType.makeArrayType() : null;
     }
     
 
 
 
 
 
     public TypeReference visitCompoundType(CompoundTypeReference t, Void ignored)
     {
       TypeReference bt = t.getBaseType();
       
       if (bt != null) {
         return MetadataHelper.getSuperType(bt);
       }
       
       return t;
     }
     
     public TypeReference visitClassType(TypeReference t, Void ignored)
     {
       TypeDefinition resolved = t.resolve();
       
       if (resolved == null) {
         return BuiltinTypes.Object;
       }
       
       TypeReference superType;
       
       if (resolved.isInterface()) {
         TypeReference superType = resolved.getBaseType();
         
         if (superType == null) {
           superType = (TypeReference)CollectionUtilities.firstOrDefault(resolved.getExplicitInterfaces());
         }
       }
       else {
         superType = resolved.getBaseType();
       }
       
       if (superType == null) {
         return null;
       }
       
       if (resolved.isGenericDefinition()) {
         if (!t.isGenericType()) {
           return MetadataHelper.eraseRecursive(superType);
         }
         
         if (t.isGenericDefinition()) {
           return superType;
         }
         
         return MetadataHelper.substituteGenericArguments(superType, MetadataHelper.classBound(t));
       }
       
       return superType;
     }
     
     public TypeReference visitGenericParameter(GenericParameter t, Void ignored)
     {
       return t.hasExtendsBound() ? t.getExtendsBound() : BuiltinTypes.Object;
     }
     
 
     public TypeReference visitNullType(TypeReference t, Void ignored)
     {
       return BuiltinTypes.Object;
     }
     
     public TypeReference visitParameterizedType(TypeReference t, Void ignored)
     {
       return visitClassType(t, ignored);
     }
     
     public TypeReference visitRawType(RawType t, Void ignored)
     {
       TypeReference genericDefinition = t.getUnderlyingType();
       
       if (!genericDefinition.isGenericDefinition()) {
         TypeDefinition resolved = genericDefinition.resolve();
         
         if ((resolved == null) || (!resolved.isGenericDefinition())) {
           return BuiltinTypes.Object;
         }
         
         genericDefinition = resolved;
       }
       
       TypeReference baseType = MetadataHelper.getBaseType(genericDefinition);
       
       return (baseType != null) && (baseType.isGenericType()) ? MetadataHelper.eraseRecursive(baseType) : baseType;
     }
     
 
     public TypeReference visitWildcard(WildcardType t, Void ignored)
     {
       if (t.isUnbounded()) {
         return BuiltinTypes.Object;
       }
       
       if (t.hasExtendsBound()) {
         return t.getExtendsBound();
       }
       
 
 
 
 
       return null;
     }
   };
   
   static List<ParameterDefinition> copyParameters(List<ParameterDefinition> parameters) {
     List<ParameterDefinition> newParameters = new ArrayList();
     
     for (ParameterDefinition p : parameters) {
       if (p.hasName()) {
         newParameters.add(new ParameterDefinition(p.getSlot(), p.getName(), p.getParameterType()));
       }
       else {
         newParameters.add(new ParameterDefinition(p.getSlot(), p.getParameterType()));
       }
     }
     
     return newParameters;
   }
   
   private static final class Adapter extends DefaultTypeVisitor<TypeReference, Void> {
     final ListBuffer<TypeReference> from = ListBuffer.lb();
     final ListBuffer<TypeReference> to = ListBuffer.lb();
     final Map<TypeReference, TypeReference> mapping = new HashMap();
     
 
 
     private void adaptRecursive(List<? extends TypeReference> source, List<? extends TypeReference> target)
     {
       if (source.size() == target.size()) {
         int i = 0; for (int n = source.size(); i < n; i++) {
           adaptRecursive((TypeReference)source.get(i), (TypeReference)target.get(i));
         }
       }
     }
     
     public Void visitClassType(TypeReference source, TypeReference target)
     {
       adaptRecursive(MetadataHelper.getTypeArguments(source), MetadataHelper.getTypeArguments(target));
       return null;
     }
     
     public Void visitParameterizedType(TypeReference source, TypeReference target)
     {
       adaptRecursive(MetadataHelper.getTypeArguments(source), MetadataHelper.getTypeArguments(target));
       return null;
     }
     
     private void adaptRecursive(TypeReference source, TypeReference target) {
       HashSet<Pair<TypeReference, TypeReference>> cache = (HashSet)MetadataHelper.ADAPT_CACHE.get();
       Pair<TypeReference, TypeReference> pair = Pair.create(source, target);
       
       if (cache.add(pair)) {
         try {
           visit(source, target);
         }
         finally {
           cache.remove(pair);
         }
       }
     }
     
     public Void visitArrayType(ArrayType source, TypeReference target)
     {
       if (target.isArray()) {
         adaptRecursive(MetadataHelper.getElementType(source), MetadataHelper.getElementType(target));
       }
       return null;
     }
     
     public Void visitWildcard(WildcardType source, TypeReference target)
     {
       if (source.hasExtendsBound()) {
         adaptRecursive(MetadataHelper.getUpperBound(source), MetadataHelper.getUpperBound(target));
       }
       else if (source.hasSuperBound()) {
         adaptRecursive(MetadataHelper.getLowerBound(source), MetadataHelper.getLowerBound(target));
       }
       return null;
     }
     
     public Void visitGenericParameter(GenericParameter source, TypeReference target)
     {
       TypeReference value = (TypeReference)this.mapping.get(source);
       
       if (value != null) {
         if ((value.hasSuperBound()) && (target.hasSuperBound())) {
           value = MetadataHelper.isSubType(MetadataHelper.getLowerBound(value), MetadataHelper.getLowerBound(target)) ? target : value;
 
         }
         else if ((value.hasExtendsBound()) && (target.hasExtendsBound())) {
           value = MetadataHelper.isSubType(MetadataHelper.getUpperBound(value), MetadataHelper.getUpperBound(target)) ? value : target;
 
         }
         else if ((!value.isWildcardType()) || (!value.isUnbounded()))
         {
 
           if (!MetadataHelper.isSameType(value, target)) {
             throw new MetadataHelper.AdaptFailure();
           }
         }
       } else {
         value = target;
         this.from.append(source);
         this.to.append(target);
       }
       
       this.mapping.put(source, value);
       
       return null;
     }
   }
   
 
 
 
 
   private static final SameTypeVisitor SAME_TYPE_VISITOR_LOOSE = new LooseSameTypeVisitor();
   private static final SameTypeVisitor SAME_TYPE_VISITOR_STRICT = new StrictSameTypeVisitor();
   
   public static class AdaptFailure extends RuntimeException { static final long serialVersionUID = -7490231548272701566L; }
   
   static abstract class SameTypeVisitor extends TypeRelation { abstract boolean areSameGenericParameters(GenericParameter paramGenericParameter1, GenericParameter paramGenericParameter2);
     
     protected abstract boolean containsTypes(List<? extends TypeReference> paramList1, List<? extends TypeReference> paramList2);
     
     public Boolean visit(TypeReference t, TypeReference s) { if (t == null) {
         return Boolean.valueOf(s == null);
       }
       
       if (s == null) {
         return Boolean.valueOf(false);
       }
       
       return (Boolean)t.accept(this, s);
     }
     
     public Boolean visitType(TypeReference t, TypeReference s)
     {
       return Boolean.FALSE;
     }
     
     public Boolean visitArrayType(ArrayType t, TypeReference s)
     {
       return Boolean.valueOf((s.isArray()) && (MetadataHelper.containsTypeEquivalent(MetadataHelper.getElementType(t), MetadataHelper.getElementType(s))));
     }
     
 
     public Boolean visitBottomType(TypeReference t, TypeReference s)
     {
       return Boolean.valueOf(t == s);
     }
     
     public Boolean visitClassType(TypeReference t, TypeReference s)
     {
       if (t == s) {
         return Boolean.valueOf(true);
       }
       
       if ((!(t instanceof RawType)) && (MetadataHelper.isRawType(t))) {
         TypeDefinition tResolved = t.resolve();
         
         if (tResolved != null) {
           return visitClassType(tResolved, s);
         }
       }
       
       if ((!(s instanceof RawType)) && (MetadataHelper.isRawType(s))) {
         TypeDefinition sResolved = s.resolve();
         
         if (sResolved != null) {
           return visitClassType(t, sResolved);
         }
       }
       
       if (t.isGenericDefinition()) {
         if (s.isGenericDefinition()) {
           return Boolean.valueOf((StringUtilities.equals(t.getInternalName(), s.getInternalName())) && (visit(t.getDeclaringType(), s.getDeclaringType()).booleanValue()));
         }
         
         return Boolean.valueOf(false);
       }
       
       if ((s.getSimpleType() == JvmType.Object) && (StringUtilities.equals(t.getInternalName(), s.getInternalName())) && (containsTypes(MetadataHelper.getTypeArguments(t), MetadataHelper.getTypeArguments(s))))
       {
 
 
 
         return Boolean.valueOf(true);
       }
       
       return Boolean.valueOf(false);
     }
     
     public Boolean visitCompoundType(CompoundTypeReference t, TypeReference s)
     {
       if (!s.isCompoundType()) {
         return Boolean.valueOf(false);
       }
       
       if (!visit(MetadataHelper.getSuperType(t), MetadataHelper.getSuperType(s)).booleanValue()) {
         return Boolean.valueOf(false);
       }
       
       HashSet<TypeReference> set = new HashSet();
       
       for (TypeReference i : MetadataHelper.getInterfaces(t)) {
         set.add(i);
       }
       
       for (TypeReference i : MetadataHelper.getInterfaces(s)) {
         if (!set.remove(i)) {
           return Boolean.valueOf(false);
         }
       }
       
       return Boolean.valueOf(set.isEmpty());
     }
     
     public Boolean visitGenericParameter(GenericParameter t, TypeReference s)
     {
       if ((s instanceof GenericParameter))
       {
 
 
 
         return Boolean.valueOf(areSameGenericParameters(t, (GenericParameter)s));
       }
       
 
 
 
 
       return Boolean.valueOf((s.hasSuperBound()) && (!s.hasExtendsBound()) && (visit(t, MetadataHelper.getUpperBound(s)).booleanValue()));
     }
     
 
 
     public Boolean visitNullType(TypeReference t, TypeReference s)
     {
       return Boolean.valueOf(t == s);
     }
     
     public Boolean visitParameterizedType(TypeReference t, TypeReference s)
     {
       return visitClassType(t, s);
     }
     
     public Boolean visitPrimitiveType(PrimitiveType t, TypeReference s)
     {
       return Boolean.valueOf(t.getSimpleType() == s.getSimpleType());
     }
     
     public Boolean visitRawType(RawType t, TypeReference s)
     {
       return Boolean.valueOf((s.getSimpleType() == JvmType.Object) && (!s.isGenericType()) && (StringUtilities.equals(t.getInternalName(), s.getInternalName())));
     }
     
 
 
     public Boolean visitWildcard(WildcardType t, TypeReference s)
     {
       if (s.isWildcardType()) {
         if (t.isUnbounded()) {
           return Boolean.valueOf(s.isUnbounded());
         }
         
         if (t.hasExtendsBound()) {
           return Boolean.valueOf((s.hasExtendsBound()) && (visit(MetadataHelper.getUpperBound(t), MetadataHelper.getUpperBound(s)).booleanValue()));
         }
         
 
         if (t.hasSuperBound()) {
           return Boolean.valueOf((s.hasSuperBound()) && (visit(MetadataHelper.getLowerBound(t), MetadataHelper.getLowerBound(s)).booleanValue()));
         }
       }
       
 
       return Boolean.FALSE;
     }
   }
   
   static final class LooseSameTypeVisitor extends MetadataHelper.SameTypeVisitor
   {
     boolean areSameGenericParameters(GenericParameter gp1, GenericParameter gp2) {
       if (gp1 == gp2) {
         return true;
       }
       
       if ((gp1 == null) || (gp2 == null)) {
         return false;
       }
       
       if (!StringUtilities.equals(gp1.getName(), gp2.getName())) {
         return false;
       }
       
       IGenericParameterProvider owner1 = gp1.getOwner();
       IGenericParameterProvider owner2 = gp2.getOwner();
       
       if (owner1.getGenericParameters().indexOf(gp1) != owner1.getGenericParameters().indexOf(gp2)) {
         return false;
       }
       
       if (owner1 == owner2) {
         return true;
       }
       
       if ((owner1 instanceof TypeReference)) {
         return ((owner2 instanceof TypeReference)) && (StringUtilities.equals(((TypeReference)owner1).getInternalName(), ((TypeReference)owner2).getInternalName()));
       }
       
 
 
 
 
       return ((owner1 instanceof MethodReference)) && ((owner2 instanceof MethodReference)) && (StringUtilities.equals(((MethodReference)owner1).getFullName(), ((MethodReference)owner2).getFullName())) && (StringUtilities.equals(((MethodReference)owner1).getErasedSignature(), ((MethodReference)owner2).getErasedSignature()));
     }
     
 
 
 
 
 
 
 
 
 
     protected boolean containsTypes(List<? extends TypeReference> t1, List<? extends TypeReference> t2)
     {
       return MetadataHelper.containsTypeEquivalent(t1, t2);
     }
   }
   
   static final class StrictSameTypeVisitor extends MetadataHelper.SameTypeVisitor
   {
     boolean areSameGenericParameters(GenericParameter gp1, GenericParameter gp2) {
       if (gp1 == gp2) {
         return true;
       }
       
       if ((gp1 == null) || (gp2 == null)) {
         return false;
       }
       
       if (!StringUtilities.equals(gp1.getName(), gp2.getName())) {
         return false;
       }
       
       IGenericParameterProvider owner1 = gp1.getOwner();
       IGenericParameterProvider owner2 = gp2.getOwner();
       
       if ((owner1 == null) || (owner2 == null)) {
         if (owner1 != owner2) {
           return false;
         }
       }
       else if (CollectionUtilities.indexOfByIdentity(owner1.getGenericParameters(), gp1) != CollectionUtilities.indexOfByIdentity(owner2.getGenericParameters(), gp2)) {
         return false;
       }
       
       if (owner1 == owner2) {
         return true;
       }
       
       if ((owner1 instanceof TypeReference)) {
         return ((owner2 instanceof TypeReference)) && (StringUtilities.equals(gp1.getName(), gp2.getName())) && (StringUtilities.equals(((TypeReference)owner1).getInternalName(), ((TypeReference)owner2).getInternalName()));
       }
       
 
 
 
 
 
       return ((owner1 instanceof MethodReference)) && ((owner2 instanceof MethodReference)) && (StringUtilities.equals(gp1.getName(), gp2.getName())) && (StringUtilities.equals(((MethodReference)owner1).getFullName(), ((MethodReference)owner2).getFullName())) && (StringUtilities.equals(((MethodReference)owner1).getErasedSignature(), ((MethodReference)owner2).getErasedSignature()));
     }
     
 
 
 
 
 
 
 
 
 
 
     protected boolean containsTypes(List<? extends TypeReference> t1, List<? extends TypeReference> t2)
     {
       return MetadataHelper.areSameTypes(t1, t2, true);
     }
     
     public Boolean visitWildcard(WildcardType t, TypeReference s)
     {
       if (s.isWildcardType()) {
         if (t.isUnbounded()) {
           return Boolean.valueOf(s.isUnbounded());
         }
         
         if (t.hasExtendsBound()) {
           return Boolean.valueOf((s.hasExtendsBound()) && (MetadataHelper.isSameType(t.getExtendsBound(), s.getExtendsBound())));
         }
         
 
         return Boolean.valueOf((s.hasSuperBound()) && (MetadataHelper.isSameType(t.getSuperBound(), s.getSuperBound())));
       }
       
 
       return Boolean.valueOf(false);
     }
   }
   
   private static final DefaultTypeVisitor<Void, List<TypeReference>> INTERFACES_VISITOR = new DefaultTypeVisitor()
   {
     public List<TypeReference> visitClassType(TypeReference t, Void ignored)
     {
       TypeDefinition r = t.resolve();
       
       if (r == null) {
         return Collections.emptyList();
       }
       
       List<TypeReference> interfaces = r.getExplicitInterfaces();
       
       if (r.isGenericDefinition()) {
         if (t.isGenericDefinition()) {
           return interfaces;
         }
         
         if (MetadataHelper.isRawType(t)) {
           return MetadataHelper.eraseRecursive(interfaces);
         }
         
         List<? extends TypeReference> formal = MetadataHelper.getTypeArguments(r);
         List<? extends TypeReference> actual = MetadataHelper.getTypeArguments(t);
         
         ArrayList<TypeReference> result = new ArrayList();
         Map<TypeReference, TypeReference> mappings = new HashMap();
         
         int i = 0; for (int n = formal.size(); i < n; i++) {
           mappings.put(formal.get(i), actual.get(i));
         }
         
         int i = 0; for (int n = interfaces.size(); i < n; i++) {
           result.add(MetadataHelper.substituteGenericArguments((TypeReference)interfaces.get(i), mappings));
         }
         
         return result;
       }
       
       return interfaces;
     }
     
     public List<TypeReference> visitWildcard(WildcardType t, Void ignored)
     {
       if (t.hasExtendsBound()) {
         TypeReference bound = t.getExtendsBound();
         TypeDefinition resolvedBound = bound.resolve();
         
         if (resolvedBound != null) {
           if (resolvedBound.isInterface()) {
             return Collections.singletonList(bound);
           }
           if (resolvedBound.isCompoundType()) {
             visit(bound, null);
           }
         }
         
         return (List)visit(bound, null);
       }
       
       return Collections.emptyList();
     }
     
     public List<TypeReference> visitGenericParameter(GenericParameter t, Void ignored)
     {
       if (t.hasExtendsBound()) {
         TypeReference bound = t.getExtendsBound();
         TypeDefinition resolvedBound = bound.resolve();
         
         if (resolvedBound != null) {
           if (resolvedBound.isInterface()) {
             return Collections.singletonList(bound);
           }
           if (resolvedBound.isCompoundType()) {
             visit(bound, null);
           }
         }
         
         return (List)visit(bound, null);
       }
       
       return Collections.emptyList();
     }
   };
   
   private static final TypeMapper<TypeReference> AS_SUBTYPE_VISITOR = new TypeMapper()
   {
     public TypeReference visitClassType(TypeReference t, TypeReference s) {
       if (MetadataHelper.isSameType(t, s)) {
         return t;
       }
       
       TypeReference base = MetadataHelper.asSuper(t, s);
       
       if (base == null) {
         return null;
       }
       
       Map<TypeReference, TypeReference> mappings;
       try
       {
         mappings = MetadataHelper.adapt(base, t);
       }
       catch (MetadataHelper.AdaptFailure ignored) {
         mappings = MetadataHelper.getGenericSubTypeMappings(t, base);
       }
       
       TypeReference result = MetadataHelper.substituteGenericArguments(s, mappings);
       
       if (!MetadataHelper.isSubType(result, t)) {
         return null;
       }
       
       List<? extends TypeReference> tTypeArguments = MetadataHelper.getTypeArguments(t);
       List<? extends TypeReference> sTypeArguments = MetadataHelper.getTypeArguments(s);
       List<? extends TypeReference> resultTypeArguments = MetadataHelper.getTypeArguments(result);
       
       List<TypeReference> openGenericParameters = null;
       
       for (TypeReference a : sTypeArguments) {
         if ((a.isGenericParameter()) && (CollectionUtilities.indexOfByIdentity(resultTypeArguments, a) >= 0) && (CollectionUtilities.indexOfByIdentity(tTypeArguments, a) < 0))
         {
 
 
           if (openGenericParameters == null) {
             openGenericParameters = new ArrayList();
           }
           
           openGenericParameters.add(a);
         }
       }
       
       if (openGenericParameters != null) {
         if (MetadataHelper.isRawType(t))
         {
 
 
           return MetadataHelper.eraseRecursive(result);
         }
         
         Map<TypeReference, TypeReference> unboundMappings = new HashMap();
         
         for (TypeReference p : openGenericParameters) {
           unboundMappings.put(p, WildcardType.unbounded());
         }
         
         return MetadataHelper.substituteGenericArguments(result, unboundMappings);
       }
       
 
       return result;
     }
   };
   
   private static final DefaultTypeVisitor<Boolean, TypeReference> ERASE_VISITOR = new DefaultTypeVisitor()
   {
     public TypeReference visitArrayType(ArrayType t, Boolean recurse) {
       TypeReference elementType = MetadataHelper.getElementType(t);
       TypeReference erasedElementType = MetadataHelper.erase(MetadataHelper.getElementType(t), recurse.booleanValue());
       
       return erasedElementType == elementType ? t : erasedElementType.makeArrayType();
     }
     
     public TypeReference visitBottomType(TypeReference t, Boolean recurse)
     {
       return t;
     }
     
     public TypeReference visitClassType(TypeReference t, Boolean recurse)
     {
       if (t.isGenericType()) {
         return new RawType(t);
       }
       
       TypeDefinition resolved = t.resolve();
       
       if ((resolved != null) && (resolved.isGenericDefinition())) {
         return new RawType(resolved);
       }
       
       return t;
     }
     
     public TypeReference visitCompoundType(CompoundTypeReference t, Boolean recurse)
     {
       TypeReference baseType = t.getBaseType();
       return MetadataHelper.erase(baseType != null ? baseType : (TypeReference)CollectionUtilities.first(t.getInterfaces()), recurse.booleanValue());
     }
     
     public TypeReference visitGenericParameter(GenericParameter t, Boolean recurse)
     {
       return MetadataHelper.erase(MetadataHelper.getUpperBound(t), recurse.booleanValue());
     }
     
     public TypeReference visitNullType(TypeReference t, Boolean recurse)
     {
       return t;
     }
     
     public TypeReference visitPrimitiveType(PrimitiveType t, Boolean recurse)
     {
       return t;
     }
     
     public TypeReference visitRawType(RawType t, Boolean recurse)
     {
       return t;
     }
     
     public TypeReference visitType(TypeReference t, Boolean recurse)
     {
       if (t.isGenericType()) {
         return new RawType(t);
       }
       return t;
     }
     
     public TypeReference visitWildcard(WildcardType t, Boolean recurse)
     {
       return MetadataHelper.erase(MetadataHelper.getUpperBound(t), recurse.booleanValue());
     }
   };
   
   private static final DefaultTypeVisitor<Void, Boolean> IS_DECLARED_TYPE = new DefaultTypeVisitor()
   {
     public Boolean visitWildcard(WildcardType t, Void ignored) {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitArrayType(ArrayType t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitBottomType(TypeReference t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitCapturedType(CapturedType t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitClassType(TypeReference t, Void ignored)
     {
       return Boolean.valueOf(true);
     }
     
     public Boolean visitCompoundType(CompoundTypeReference t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitGenericParameter(GenericParameter t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitNullType(TypeReference t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitParameterizedType(TypeReference t, Void ignored)
     {
       return Boolean.valueOf(true);
     }
     
     public Boolean visitPrimitiveType(PrimitiveType t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
     
     public Boolean visitRawType(RawType t, Void ignored)
     {
       return Boolean.valueOf(true);
     }
     
     public Boolean visitType(TypeReference t, Void ignored)
     {
       return Boolean.valueOf(false);
     }
   };
 }


