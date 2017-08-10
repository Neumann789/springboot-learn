 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.PackageReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.componentmodel.Key;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.ExceptionUtilities;
 import com.strobel.decompiler.ast.Variable;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 public final class Keys
 {
   public static final Key<Variable> VARIABLE = Key.create("Variable");
   public static final Key<VariableDefinition> VARIABLE_DEFINITION = Key.create("VariableDefinition");
   public static final Key<ParameterDefinition> PARAMETER_DEFINITION = Key.create("ParameterDefinition");
   public static final Key<MemberReference> MEMBER_REFERENCE = Key.create("MemberReference");
   public static final Key<PackageReference> PACKAGE_REFERENCE = Key.create("PackageReference");
   public static final Key<FieldDefinition> FIELD_DEFINITION = Key.create("FieldDefinition");
   public static final Key<MethodDefinition> METHOD_DEFINITION = Key.create("MethodDefinition");
   public static final Key<TypeDefinition> TYPE_DEFINITION = Key.create("TypeDefinition");
   public static final Key<TypeReference> TYPE_REFERENCE = Key.create("TypeReference");
   public static final Key<TypeReference> ANONYMOUS_BASE_TYPE_REFERENCE = Key.create("AnonymousBaseTypeReference");
   public static final Key<DynamicCallSite> DYNAMIC_CALL_SITE = Key.create("DynamicCallSite");
   public static final Key<AstBuilder> AST_BUILDER = Key.create("AstBuilder");
   public static final Key<Object> CONSTANT_VALUE = Key.create("ConstantValue");
   public static final List<Key<?>> ALL_KEYS;
   
   static
   {
     ArrayList<Key<?>> keys = new ArrayList();
     try
     {
       for (Field field : Keys.class.getDeclaredFields()) {
         if (field.getType() == Key.class) {
           keys.add((Key)field.get(null));
         }
       }
       
       ALL_KEYS = ArrayUtilities.asUnmodifiableList(keys.toArray(new Key[keys.size()]));
     }
     catch (Throwable t) {
       throw ExceptionUtilities.asRuntimeException(t);
     }
   }
 }


