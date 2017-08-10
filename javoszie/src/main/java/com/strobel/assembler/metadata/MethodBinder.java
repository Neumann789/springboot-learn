 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MethodBinder
 {
   public static class BindResult
   {
     public static final BindResult FAILURE = new BindResult(false, null);
     public static final BindResult AMBIGUOUS = new BindResult(true, null);
     private final boolean _ambiguous;
     private final MethodReference _method;
     
     private BindResult(boolean ambiguous, MethodReference method)
     {
       this._ambiguous = ambiguous;
       this._method = method;
     }
     
     public final boolean isFailure() {
       return this._method == null;
     }
     
     public final boolean isAmbiguous() {
       return this._ambiguous;
     }
     
     public final MethodReference getMethod() {
       return this._method;
     }
   }
   
   public static BindResult selectMethod(List<? extends MethodReference> matches, List<TypeReference> types) {
     VerifyArgument.notNull(matches, "matches");
     VerifyArgument.notNull(types, "types");
     
     if (types.isEmpty()) {
       return null;
     }
     
     int argumentCount = types.size();
     MethodReference[] candidates = (MethodReference[])matches.toArray(new MethodReference[matches.size()]);
     
     for (int i = 0; i < candidates.length; i++) {
       MethodReference candidate = candidates[i];
       
       if (candidate.isGenericMethod()) {
         Map<TypeReference, TypeReference> mappings = new HashMap();
         List<ParameterDefinition> parameters = candidate.getParameters();
         
         int j = 0; for (int n = Math.min(argumentCount, parameters.size()); j < n; j++) {
           ParameterDefinition p = (ParameterDefinition)parameters.get(j);
           TypeReference pType = p.getParameterType();
           
           if (pType.containsGenericParameters()) {
             new AddMappingsForArgumentVisitor((TypeReference)types.get(j)).visit(pType, mappings);
           }
         }
         
         candidates[i] = TypeSubstitutionVisitor.instance().visitMethod(candidate, mappings);
       }
     }
     
 
 
 
 
 
 
     int currentIndex = 0;
     
     int i = 0; for (int n = candidates.length; i < n; i++) {
       MethodReference candidate = candidates[i];
       MethodDefinition resolved = candidate.resolve();
       List<ParameterDefinition> parameters = candidate.getParameters();
       int parameterCount = parameters.size();
       boolean isVarArgs = (resolved != null) && (resolved.isVarArgs());
       
       if ((parameterCount == types.size()) || (isVarArgs))
       {
 
 
         for (int stop = 0; stop < Math.min(parameterCount, types.size()); stop++) {
           TypeReference parameterType = ((ParameterDefinition)parameters.get(stop)).getParameterType();
           
           if ((!MetadataHelper.isSameType(parameterType, (TypeReference)types.get(stop), false)) && (!MetadataHelper.isSameType(parameterType, BuiltinTypes.Object, false)))
           {
 
 
 
 
             if (!MetadataHelper.isAssignableFrom(parameterType, (TypeReference)types.get(stop)))
             {
 
 
               if ((!isVarArgs) || (stop != parameterCount - 1)) {
                 break;
               }
               
               if (!MetadataHelper.isAssignableFrom(parameterType.getElementType(), (TypeReference)types.get(stop)))
                 break;
             }
           }
         }
         if ((stop == parameterCount) || ((stop == parameterCount - 1) && (isVarArgs)))
         {
 
           candidates[(currentIndex++)] = candidate;
         }
       }
     }
     if (currentIndex == 0) {
       return BindResult.FAILURE;
     }
     
     if (currentIndex == 1) {
       return new BindResult(false, candidates[0], null);
     }
     
 
     int currentMin = 0;
     boolean ambiguous = false;
     
     int[] parameterOrder = new int[types.size()];
     
     int i = 0; for (int n = types.size(); i < n; i++) {
       parameterOrder[i] = i;
     }
     
     for (int i = 1; i < currentIndex; i++) {
       MethodReference m1 = candidates[currentMin];
       MethodReference m2 = candidates[i];
       
       MethodDefinition r1 = m1.resolve();
       MethodDefinition r2 = m2.resolve();
       
       TypeReference varArgType1;
       
       TypeReference varArgType1;
       if ((r1 != null) && (r1.isVarArgs())) {
         List<ParameterDefinition> p1 = m1.getParameters();
         varArgType1 = ((ParameterDefinition)p1.get(p1.size() - 1)).getParameterType().getElementType();
       }
       else {
         varArgType1 = null; }
       TypeReference varArgType2;
       TypeReference varArgType2;
       if ((r2 != null) && (r2.isVarArgs())) {
         List<ParameterDefinition> p2 = m2.getParameters();
         varArgType2 = ((ParameterDefinition)p2.get(p2.size() - 1)).getParameterType().getElementType();
       }
       else {
         varArgType2 = null;
       }
       
       int newMin = findMostSpecificMethod(m1, parameterOrder, varArgType1, candidates[i], parameterOrder, varArgType2, types, null);
       
 
 
 
 
 
 
 
 
 
       if (newMin == 0) {
         ambiguous = true;
 
       }
       else if (newMin == 2) {
         ambiguous = false;
         currentMin = i;
       }
     }
     
 
     if (ambiguous) {
       return new BindResult(true, candidates[currentMin], null);
     }
     
     return new BindResult(false, candidates[currentMin], null);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   private static int findMostSpecificMethod(MethodReference m1, int[] varArgOrder1, TypeReference varArgArrayType1, MethodReference m2, int[] varArgOrder2, TypeReference varArgArrayType2, List<TypeReference> types, Object[] args)
   {
     int result = findMostSpecific(m1.getParameters(), varArgOrder1, null, m2.getParameters(), varArgOrder2, null, types, args, false);
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     if (result == 0) {
       result = findMostSpecific(m1.getParameters(), varArgOrder1, null, m2.getParameters(), varArgOrder2, null, types, args, true);
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     if (result == 0) {
       result = findMostSpecific(m1.getParameters(), varArgOrder1, varArgArrayType1, m2.getParameters(), varArgOrder2, varArgArrayType2, types, args, true);
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
     if (result != 0) {
       return result;
     }
     
 
 
 
     if (compareMethodSignatureAndName(m1, m2))
     {
 
 
       int hierarchyDepth1 = getHierarchyDepth(m1.getDeclaringType());
       int hierarchyDepth2 = getHierarchyDepth(m2.getDeclaringType());
       
 
 
 
       if (hierarchyDepth1 == hierarchyDepth2) {
         return 0;
       }
       if (hierarchyDepth1 < hierarchyDepth2) {
         return 2;
       }
       
       return 1;
     }
     
 
 
 
 
     return 0;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   private static int findMostSpecific(List<ParameterDefinition> p1, int[] varArgOrder1, TypeReference varArgArrayType1, List<ParameterDefinition> p2, int[] varArgOrder2, TypeReference varArgArrayType2, List<TypeReference> types, Object[] args, boolean allowAutoBoxing)
   {
     if ((varArgArrayType1 != null) && (varArgArrayType2 == null) && 
       (types.size() != p1.size())) {
       return 2;
     }
     
 
     if ((varArgArrayType2 != null) && (varArgArrayType1 == null) && 
       (types.size() != p2.size())) {
       return 1;
     }
     
 
 
 
 
 
     boolean p1Less = false;
     boolean p2Less = false;
     
     int max = varArgArrayType1 != null ? types.size() : Math.min(p1.size(), p2.size());
     
 
     for (int i = 0; i < max; i++) {
       if (args == null)
       {
         TypeReference c1;
         
 
 
 
 
 
 
 
         TypeReference c1;
         
 
 
 
 
 
 
         if ((varArgArrayType1 != null) && (varArgOrder1[i] >= p1.size() - 1)) {
           c1 = varArgArrayType1;
         }
         else
           c1 = ((ParameterDefinition)p1.get(varArgOrder1[i])).getParameterType();
         TypeReference c2;
         TypeReference c2;
         if ((varArgArrayType2 != null) && (varArgOrder2[i] >= p2.size() - 1)) {
           c2 = varArgArrayType2;
         }
         else {
           c2 = ((ParameterDefinition)p2.get(varArgOrder2[i])).getParameterType();
         }
         
         if (c1 != c2)
         {
 
 
           switch (findMostSpecificType(c1, c2, (TypeReference)types.get(i), allowAutoBoxing))
           {
 
           case 1: 
             p1Less = true;
             break;
           case 2: 
             p2Less = true;
           }
           
         }
       }
     }
     
 
 
     if (p1Less == p2Less)
     {
 
 
 
       if ((!p1Less) && (args != null)) {
         if (p1.size() > p2.size()) {
           return 1;
         }
         if (p2.size() > p1.size()) {
           return 2;
         }
       }
       
       return 0;
     }
     
     return p1Less ? 1 : 2;
   }
   
 
 
 
 
 
 
 
 
   private static int findMostSpecificType(TypeReference c1, TypeReference c2, TypeReference t, boolean allowAutoBoxing)
   {
     if (MetadataHelper.isSameType(c1, c2, false)) {
       return 0;
     }
     
     if (MetadataHelper.isSameType(c1, t, false)) {
       return 1;
     }
     
     if (MetadataHelper.isSameType(c2, t, false)) {
       return 2;
     }
     
     boolean c1FromT = ((allowAutoBoxing) || (c1.isPrimitive() == t.isPrimitive())) && (MetadataHelper.isAssignableFrom(c1, t));
     
 
     boolean c2FromT = ((allowAutoBoxing) || (c2.isPrimitive() == t.isPrimitive())) && (MetadataHelper.isAssignableFrom(c2, t));
     
 
     if (c1FromT != c2FromT) {
       return c1FromT ? 1 : 2;
     }
     
     boolean c2FromC1;
     boolean c1FromC2;
     boolean c2FromC1;
     if ((allowAutoBoxing) || (c1.isPrimitive() == c2.isPrimitive())) {
       boolean c1FromC2 = MetadataHelper.isAssignableFrom(c1, c2);
       c2FromC1 = MetadataHelper.isAssignableFrom(c2, c1);
     }
     else {
       c1FromC2 = false;
       c2FromC1 = false;
     }
     
     if (c1FromC2 == c2FromC1) {
       if ((!t.isPrimitive()) && (c1.isPrimitive() != c2.isPrimitive())) {
         return c1.isPrimitive() ? 2 : 1;
       }
       
       return 0;
     }
     
     return c1FromC2 ? 2 : 1;
   }
   
   private static boolean compareMethodSignatureAndName(MethodReference m1, MethodReference m2) {
     List<ParameterDefinition> p1 = m1.getParameters();
     List<ParameterDefinition> p2 = m2.getParameters();
     
     if (p1.size() != p2.size()) {
       return false;
     }
     
     int i = 0; for (int n = p1.size(); i < n; i++) {
       if (!MetadataHelper.isSameType(((ParameterDefinition)p1.get(i)).getParameterType(), ((ParameterDefinition)p2.get(i)).getParameterType(), false)) {
         return false;
       }
     }
     
     return true;
   }
   
   private static int getHierarchyDepth(TypeReference t) {
     int depth = 0;
     
     TypeReference currentType = t;
     do
     {
       depth++;
       currentType = MetadataHelper.getBaseType(currentType);
     }
     while (currentType != null);
     
     return depth;
   }
   
   private static final class AddMappingsForArgumentVisitor extends DefaultTypeVisitor<Map<TypeReference, TypeReference>, Void> {
     private TypeReference argumentType;
     
     AddMappingsForArgumentVisitor(TypeReference argumentType) {
       this.argumentType = ((TypeReference)VerifyArgument.notNull(argumentType, "argumentType"));
     }
     
     public Void visit(TypeReference t, Map<TypeReference, TypeReference> map) {
       TypeReference a = this.argumentType;
       t.accept(this, map);
       this.argumentType = a;
       return null;
     }
     
     public Void visitArrayType(ArrayType t, Map<TypeReference, TypeReference> map)
     {
       TypeReference a = this.argumentType;
       
       if ((a.isArray()) && (t.isArray())) {
         this.argumentType = a.getElementType();
         visit(t.getElementType(), map);
       }
       
       return null;
     }
     
 
     public Void visitGenericParameter(GenericParameter t, Map<TypeReference, TypeReference> map)
     {
       if (MetadataResolver.areEquivalent(this.argumentType, t)) {
         return null;
       }
       
       TypeReference existingMapping = (TypeReference)map.get(t);
       
       TypeReference mappedType = this.argumentType;
       
       mappedType = ensureReferenceType(mappedType);
       
       if (existingMapping == null) {
         if ((!(mappedType instanceof RawType)) && (MetadataHelper.isRawType(mappedType))) {
           TypeReference bound = MetadataHelper.getUpperBound(t);
           TypeReference asSuper = MetadataHelper.asSuper(mappedType, bound);
           
           if (asSuper != null) {
             if (MetadataHelper.isSameType(MetadataHelper.getUpperBound(t), asSuper)) {
               return null;
             }
             mappedType = asSuper;
           }
           else {
             mappedType = MetadataHelper.erase(mappedType);
           }
         }
         map.put(t, mappedType);
       }
       else if (!MetadataHelper.isSubType(this.argumentType, existingMapping))
       {
 
 
         TypeReference commonSuperType = MetadataHelper.asSuper(mappedType, existingMapping);
         
         if (commonSuperType == null) {
           commonSuperType = MetadataHelper.asSuper(existingMapping, mappedType);
         }
         
         if (commonSuperType == null) {
           commonSuperType = MetadataHelper.findCommonSuperType(existingMapping, mappedType);
         }
         
         map.put(t, commonSuperType);
       }
       
       return null;
     }
     
     public Void visitWildcard(WildcardType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitCompoundType(CompoundTypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitParameterizedType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       TypeReference r = MetadataHelper.asSuper(t.getUnderlyingType(), this.argumentType);
       TypeReference s = MetadataHelper.asSubType(this.argumentType, r != null ? r : t.getUnderlyingType());
       
       if ((s != null) && ((s instanceof IGenericInstance))) {
         List<TypeReference> tArgs = ((IGenericInstance)t).getTypeArguments();
         List<TypeReference> sArgs = ((IGenericInstance)s).getTypeArguments();
         
         if (tArgs.size() == sArgs.size()) {
           int i = 0; for (int n = tArgs.size(); i < n; i++) {
             this.argumentType = ((TypeReference)sArgs.get(i));
             visit((TypeReference)tArgs.get(i), map);
           }
         }
       }
       
       return null;
     }
     
     public Void visitPrimitiveType(PrimitiveType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitClassType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitNullType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitBottomType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitRawType(RawType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     private static TypeReference ensureReferenceType(TypeReference mappedType) {
       if (mappedType == null) {
         return null;
       }
       
       if (mappedType.isPrimitive()) {
         switch (MethodBinder.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[mappedType.getSimpleType().ordinal()]) {
         case 1: 
           return CommonTypeReferences.Boolean;
         case 2: 
           return CommonTypeReferences.Byte;
         case 3: 
           return CommonTypeReferences.Character;
         case 4: 
           return CommonTypeReferences.Short;
         case 5: 
           return CommonTypeReferences.Integer;
         case 6: 
           return CommonTypeReferences.Long;
         case 7: 
           return CommonTypeReferences.Float;
         case 8: 
           return CommonTypeReferences.Double;
         }
         
       }
       return mappedType;
     }
   }
 }


