 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.IntegerBox;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.AstCode;
 import com.strobel.decompiler.ast.Block;
 import com.strobel.decompiler.ast.Expression;
 import com.strobel.decompiler.ast.Loop;
 import com.strobel.decompiler.ast.Node;
 import com.strobel.decompiler.ast.PatternMatching;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.JavaOutputVisitor;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 public class NameVariables
 {
   private static final char MAX_LOOP_VARIABLE_NAME = 'm';
   private static final String[] METHOD_PREFIXES = { "get", "is", "are", "to", "as", "create", "make", "new", "read", "parse", "extract", "find" };
   private static final String[] METHOD_SUFFIXES = { "At", "For", "From", "Of" };
   private static final Map<String, String> BUILT_IN_TYPE_NAMES;
   private static final Map<String, String> METHOD_NAME_MAPPINGS;
   private final ArrayList<String> _fieldNamesInCurrentType;
   
   static { Map<String, String> builtInTypeNames = new LinkedHashMap();
     Map<String, String> methodNameMappings = new LinkedHashMap();
     
     builtInTypeNames.put(BuiltinTypes.Boolean.getInternalName(), "b");
     builtInTypeNames.put("java/lang/Boolean", "b");
     builtInTypeNames.put(BuiltinTypes.Byte.getInternalName(), "b");
     builtInTypeNames.put("java/lang/Byte", "b");
     builtInTypeNames.put(BuiltinTypes.Short.getInternalName(), "n");
     builtInTypeNames.put("java/lang/Short", "n");
     builtInTypeNames.put(BuiltinTypes.Integer.getInternalName(), "n");
     builtInTypeNames.put("java/lang/Integer", "n");
     builtInTypeNames.put(BuiltinTypes.Long.getInternalName(), "n");
     builtInTypeNames.put("java/lang/Long", "n");
     builtInTypeNames.put(BuiltinTypes.Float.getInternalName(), "n");
     builtInTypeNames.put("java/lang/Float", "n");
     builtInTypeNames.put(BuiltinTypes.Double.getInternalName(), "n");
     builtInTypeNames.put("java/lang/Double", "n");
     builtInTypeNames.put(BuiltinTypes.Character.getInternalName(), "c");
     builtInTypeNames.put("java/lang/Number", "n");
     builtInTypeNames.put("java/io/Serializable", "s");
     builtInTypeNames.put("java/lang/Character", "c");
     builtInTypeNames.put("java/lang/Object", "o");
     builtInTypeNames.put("java/lang/String", "s");
     builtInTypeNames.put("java/lang/StringBuilder", "sb");
     builtInTypeNames.put("java/lang/StringBuffer", "sb");
     builtInTypeNames.put("java/lang/Class", "clazz");
     
     BUILT_IN_TYPE_NAMES = Collections.unmodifiableMap(builtInTypeNames);
     
     methodNameMappings.put("get", "value");
     
     METHOD_NAME_MAPPINGS = methodNameMappings;
   }
   
 
   private final Map<String, Integer> _typeNames = new HashMap();
   
   public NameVariables(DecompilerContext context) {
     this._fieldNamesInCurrentType = new ArrayList();
     
     for (FieldDefinition field : context.getCurrentType().getDeclaredFields()) {
       this._fieldNamesInCurrentType.add(field.getName());
     }
   }
   
   public final void addExistingName(String name) {
     if (StringUtilities.isNullOrEmpty(name)) {
       return;
     }
     
     IntegerBox number = new IntegerBox();
     String nameWithoutDigits = splitName(name, number);
     Integer existingNumber = (Integer)this._typeNames.get(nameWithoutDigits);
     
     if (existingNumber != null) {
       this._typeNames.put(nameWithoutDigits, Integer.valueOf(Math.max(number.value, existingNumber.intValue())));
     }
     else {
       this._typeNames.put(nameWithoutDigits, Integer.valueOf(number.value));
     }
   }
   
   final String splitName(String name, IntegerBox number) {
     int position = name.length();
     
     while ((position > 0) && (name.charAt(position - 1) >= '0') && (name.charAt(position - 1) <= '9')) {
       position--;
     }
     
     if (position < name.length()) {
       number.value = Integer.parseInt(name.substring(position));
       return name.substring(0, position);
     }
     
     number.value = 1;
     return name;
   }
   
 
 
 
 
   public static void assignNamesToVariables(DecompilerContext context, Iterable<Variable> parameters, Iterable<Variable> variables, Block methodBody)
   {
     NameVariables nv = new NameVariables(context);
     
     for (String name : context.getReservedVariableNames()) {
       nv.addExistingName(name);
     }
     
     for (Variable p : parameters) {
       nv.addExistingName(p.getName());
     }
     
     if (context.getCurrentMethod().isTypeInitializer())
     {
 
 
 
 
       for (FieldDefinition f : context.getCurrentType().getDeclaredFields()) {
         if ((f.isStatic()) && (f.isFinal()) && (!f.hasConstantValue())) {
           nv.addExistingName(f.getName());
         }
       }
     }
     
     for (Variable v : variables) {
       if (v.isGenerated()) {
         nv.addExistingName(v.getName());
       }
       else {
         VariableDefinition originalVariable = v.getOriginalVariable();
         
         if (originalVariable != null)
         {
 
 
 
 
 
 
           String varName = originalVariable.getName();
           
           if ((StringUtilities.isNullOrEmpty(varName)) || (varName.startsWith("V_")) || (!isValidName(varName))) {
             v.setName(null);
           }
           else {
             v.setName(nv.getAlternativeName(varName));
           }
         }
         else {
           v.setName(null);
         }
       }
     }
     
     for (Variable p : parameters) {
       if (!p.getOriginalParameter().hasName()) {
         p.setName(nv.generateNameForVariable(p, methodBody));
       }
     }
     
     for (Variable varDef : variables) {
       boolean generateName = (StringUtilities.isNullOrEmpty(varDef.getName())) || (varDef.isGenerated()) || ((!varDef.isParameter()) && (!varDef.getOriginalVariable().isFromMetadata()));
       
 
 
       if (generateName) {
         varDef.setName(nv.generateNameForVariable(varDef, methodBody));
       }
     }
   }
   
   static boolean isValidName(String name) {
     if (StringUtilities.isNullOrEmpty(name)) {
       return false;
     }
     
     if (!Character.isJavaIdentifierPart(name.charAt(0))) {
       return false;
     }
     
     for (int i = 1; i < name.length(); i++) {
       if (!Character.isJavaIdentifierPart(name.charAt(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   public String getAlternativeName(String oldVariableName) {
     IntegerBox number = new IntegerBox();
     String nameWithoutDigits = splitName(oldVariableName, number);
     
     if ((!this._typeNames.containsKey(nameWithoutDigits)) && (!JavaOutputVisitor.isKeyword(oldVariableName))) {
       this._typeNames.put(nameWithoutDigits, Integer.valueOf(Math.min(number.value, 1)));
       return oldVariableName;
     }
     
     if ((oldVariableName.length() == 1) && (oldVariableName.charAt(0) >= 'i') && (oldVariableName.charAt(0) <= 'm'))
     {
 
 
       for (char c = 'i'; c <= 'm'; c = (char)(c + '\001')) {
         String cs = String.valueOf(c);
         
         if (!this._typeNames.containsKey(cs)) {
           this._typeNames.put(cs, Integer.valueOf(1));
           return cs;
         }
       }
     }
     
     if (!this._typeNames.containsKey(nameWithoutDigits)) {
       this._typeNames.put(nameWithoutDigits, Integer.valueOf(number.value - 1));
     }
     
     int count = ((Integer)this._typeNames.get(nameWithoutDigits)).intValue() + 1;
     
     this._typeNames.put(nameWithoutDigits, Integer.valueOf(count));
     
     if ((count != 1) || (JavaOutputVisitor.isKeyword(nameWithoutDigits))) {
       return nameWithoutDigits + count;
     }
     
     return nameWithoutDigits;
   }
   
 
   private String generateNameForVariable(Variable variable, Block methodBody)
   {
     String proposedName = null;
     
     if (variable.getType().getSimpleType() == JvmType.Integer) {
       boolean isLoopCounter = false;
       
 
       for (Loop loop : methodBody.getSelfAndChildrenRecursive(Loop.class)) {
         Expression e = loop.getCondition();
         
         while ((e != null) && (e.getCode() == AstCode.LogicalNot)) {
           e = (Expression)e.getArguments().get(0);
         }
         
         if (e != null) {
           switch (e.getCode()) {
           case CmpEq: 
           case CmpNe: 
           case CmpLe: 
           case CmpGt: 
           case CmpGe: 
           case CmpLt: 
             StrongBox<Variable> loadVariable = new StrongBox();
             if ((PatternMatching.matchGetOperand((Node)e.getArguments().get(0), AstCode.Load, loadVariable)) && (loadVariable.get() == variable))
             {
 
               isLoopCounter = true;
               break label204;
             }
             break;
           }
           
         }
       }
       label204:
       if (isLoopCounter) {
         for (char c = 'i'; c < 'm'; c = (char)(c + '\001')) {
           String name = String.valueOf(c);
           
           if (!this._typeNames.containsKey(name)) {
             proposedName = name;
             break;
           }
         }
       }
     }
     
     if (StringUtilities.isNullOrEmpty(proposedName)) {
       String proposedNameForStore = null;
       
       for (Expression e : methodBody.getSelfAndChildrenRecursive(Expression.class)) {
         if ((e.getCode() == AstCode.Store) && (e.getOperand() == variable)) {
           String name = getNameFromExpression((Expression)e.getArguments().get(0));
           
           if (name != null) {
             if (proposedNameForStore != null) {
               proposedNameForStore = null;
               break;
             }
             
             proposedNameForStore = name;
           }
         }
       }
       
       if (proposedNameForStore != null) {
         proposedName = proposedNameForStore;
       }
     }
     
     if (StringUtilities.isNullOrEmpty(proposedName)) {
       String proposedNameForLoad = null;
       
       for (Expression e : methodBody.getSelfAndChildrenRecursive(Expression.class)) {
         List<Expression> arguments = e.getArguments();
         
         for (int i = 0; i < arguments.size(); i++) {
           Expression a = (Expression)arguments.get(i);
           if ((a.getCode() == AstCode.Load) && (a.getOperand() == variable)) {
             String name = getNameForArgument(e, i);
             
             if (name != null) {
               if (proposedNameForLoad != null) {
                 proposedNameForLoad = null;
                 break;
               }
               
               proposedNameForLoad = name;
             }
           }
         }
       }
       
       if (proposedNameForLoad != null) {
         proposedName = proposedNameForLoad;
       }
     }
     
     if (StringUtilities.isNullOrEmpty(proposedName)) {
       proposedName = getNameForType(variable.getType());
     }
     
     return getAlternativeName(proposedName);
   }
   
 
 
 
 
 
 
 
 
   private static String cleanUpVariableName(String s)
   {
     if (s == null) {
       return null;
     }
     
     String name = s;
     
     if ((name.length() > 2) && (name.startsWith("m_"))) {
       name = name.substring(2);
     }
     else if ((name.length() > 1) && (name.startsWith("_"))) {
       name = name.substring(1);
     }
     
     int length = name.length();
     
     if (length == 0) {
       return "obj";
     }
     
 
 
     for (int lowerEnd = 1; 
         (lowerEnd < length) && (Character.isUpperCase(name.charAt(lowerEnd))); 
         lowerEnd++)
     {
       if (lowerEnd < length - 1) {
         char nextChar = name.charAt(lowerEnd + 1);
         
         if (Character.isLowerCase(nextChar)) {
           break;
         }
         
         if (!Character.isAlphabetic(nextChar)) {
           lowerEnd++;
           break;
         }
       }
     }
     
     name = name.substring(0, lowerEnd).toLowerCase() + name.substring(lowerEnd);
     
     if (JavaOutputVisitor.isKeyword(name)) {
       return name + "1";
     }
     
     return name;
   }
   
   private static String getNameFromExpression(Expression e) {
     switch (e.getCode()) {
     case ArrayLength: 
       return cleanUpVariableName("length");
     
 
     case GetField: 
     case GetStatic: 
       return cleanUpVariableName(((FieldReference)e.getOperand()).getName());
     
 
     case InvokeVirtual: 
     case InvokeSpecial: 
     case InvokeStatic: 
     case InvokeInterface: 
       MethodReference method = (MethodReference)e.getOperand();
       
       if (method != null) {
         String methodName = method.getName();
         
         String name = methodName;
         
         String mappedMethodName = (String)METHOD_NAME_MAPPINGS.get(methodName);
         
         if (mappedMethodName != null) {
           return cleanUpVariableName(mappedMethodName);
         }
         
         for (String prefix : METHOD_PREFIXES) {
           if ((methodName.length() > prefix.length()) && (methodName.startsWith(prefix)) && (Character.isUpperCase(methodName.charAt(prefix.length()))))
           {
 
 
             name = methodName.substring(prefix.length());
             break;
           }
         }
         
         for (String suffix : METHOD_SUFFIXES) {
           if ((name.length() > suffix.length()) && (name.endsWith(suffix)) && (Character.isLowerCase(name.charAt(name.length() - suffix.length() - 1))))
           {
 
 
             name = name.substring(0, name.length() - suffix.length());
             break;
           }
         }
         
         return cleanUpVariableName(name);
       }
       
       break;
     }
     
     
     return null;
   }
   
   private static String getNameForArgument(Expression parent, int i) {
     switch (parent.getCode()) {
     case PutField: 
     case PutStatic: 
       if (i == parent.getArguments().size() - 1) {
         return cleanUpVariableName(((FieldReference)parent.getOperand()).getName());
       }
       
 
       break;
     case InvokeVirtual: 
     case InvokeSpecial: 
     case InvokeStatic: 
     case InvokeInterface: 
     case InitObject: 
       MethodReference method = (MethodReference)parent.getOperand();
       
       if (method != null) {
         String methodName = method.getName();
         List<ParameterDefinition> parameters = method.getParameters();
         
         if ((parameters.size() == 1) && (i == parent.getArguments().size() - 1) && 
           (methodName.length() > 3) && (StringUtilities.startsWith(methodName, "set")) && (Character.isUpperCase(methodName.charAt(3))))
         {
 
 
           return cleanUpVariableName(methodName.substring(3));
         }
         
 
         MethodDefinition definition = method.resolve();
         
         if (definition != null) {
           ParameterDefinition p = (ParameterDefinition)CollectionUtilities.getOrDefault(definition.getParameters(), (parent.getCode() != AstCode.InitObject) && (!definition.isStatic()) ? i - 1 : i);
           
 
 
 
           if ((p != null) && (p.hasName()) && (!StringUtilities.isNullOrEmpty(p.getName())))
             return cleanUpVariableName(p.getName());
         }
       }
       break;
     }
     
     
 
 
     return null;
   }
   
   private String getNameForType(TypeReference type)
   {
     TypeReference nameSource = type;
     
     String name;
     String name;
     if (nameSource.isArray()) {
       name = "array";
     } else { String name;
       if (StringUtilities.equals(nameSource.getInternalName(), "java/lang/Throwable")) {
         name = "t";
       } else { String name;
         if (StringUtilities.endsWith(nameSource.getName(), "Exception")) {
           name = "ex";
         } else { String name;
           if (StringUtilities.endsWith(nameSource.getName(), "List")) {
             name = "list";
           } else { String name;
             if (StringUtilities.endsWith(nameSource.getName(), "Set")) {
               name = "set";
             } else { String name;
               if (StringUtilities.endsWith(nameSource.getName(), "Collection")) {
                 name = "collection";
               }
               else {
                 name = (String)BUILT_IN_TYPE_NAMES.get(nameSource.getInternalName());
                 
                 if (name != null) {
                   return name;
                 }
                 
                 nameSource = MetadataHelper.getDeclaredType(nameSource);
                 
                 if (!nameSource.isDefinition()) {
                   TypeDefinition resolvedType = nameSource.resolve();
                   
                   if (resolvedType != null) {
                     nameSource = resolvedType;
                   }
                 }
                 
                 name = nameSource.getSimpleName();
                 
 
 
 
                 if ((name.length() > 2) && ((name.charAt(0) == 'I') || (name.charAt(0) == 'J')) && (Character.isUpperCase(name.charAt(1))) && (Character.isLowerCase(name.charAt(2))))
                 {
 
 
 
                   name = name.substring(1);
                 }
                 
                 name = cleanUpVariableName(name);
               }
             } } } } }
     return name;
   }
 }


