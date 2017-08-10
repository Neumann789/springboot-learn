 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 
 
 public class InsertConstantReferencesTransform
   extends ContextTrackingVisitor<Void>
 {
   public InsertConstantReferencesTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitPrimitiveExpression(PrimitiveExpression node, Void data)
   {
     Object value = node.getValue();
     
     if ((value instanceof Number)) {
       tryRewriteConstant(node, value);
     }
     
     return null;
   }
   
 
   private void tryRewriteConstant(PrimitiveExpression node, Object value)
   {
     String fieldName;
     if ((value instanceof Double)) {
       double d = ((Double)value).doubleValue();
       
       JvmType jvmType = JvmType.Double;
       String fieldName;
       if (d == Double.POSITIVE_INFINITY) {
         fieldName = "POSITIVE_INFINITY";
       } else { String fieldName;
         if (d == Double.NEGATIVE_INFINITY) {
           fieldName = "NEGATIVE_INFINITY";
         } else { String fieldName;
           if (Double.isNaN(d)) {
             fieldName = "NaN";
           } else { String fieldName;
             if (d == Double.MIN_VALUE) {
               fieldName = "MIN_VALUE";
             } else { String fieldName;
               if (d == Double.MAX_VALUE) {
                 fieldName = "MAX_VALUE";
               } else { String fieldName;
                 if (d == 2.2250738585072014E-308D)
                   fieldName = "MIN_NORMAL"; else return;
               }
             }
           }
         }
       } } else { String fieldName;
       if ((value instanceof Float)) {
         float f = ((Float)value).floatValue();
         
         JvmType jvmType = JvmType.Float;
         String fieldName;
         if (f == Float.POSITIVE_INFINITY) {
           fieldName = "POSITIVE_INFINITY";
         } else { String fieldName;
           if (f == Float.NEGATIVE_INFINITY) {
             fieldName = "NEGATIVE_INFINITY";
           } else { String fieldName;
             if (Float.isNaN(f)) {
               fieldName = "NaN";
             } else { String fieldName;
               if (f == Float.MIN_VALUE) {
                 fieldName = "MIN_VALUE";
               } else { String fieldName;
                 if (f == Float.MAX_VALUE) {
                   fieldName = "MAX_VALUE";
                 } else { String fieldName;
                   if (f == 1.17549435E-38F)
                     fieldName = "MIN_NORMAL"; else return;
                 }
               }
             }
           }
         } } else { String fieldName;
         if ((value instanceof Long)) {
           long l = ((Long)value).longValue();
           
           JvmType jvmType = JvmType.Long;
           String fieldName;
           if (l == Long.MIN_VALUE) {
             fieldName = "MIN_VALUE";
           } else { String fieldName;
             if (l == Long.MAX_VALUE)
               fieldName = "MAX_VALUE"; else {
               return;
             }
           }
         } else {
           String fieldName;
           if ((value instanceof Integer)) {
             int i = ((Integer)value).intValue();
             
             JvmType jvmType = JvmType.Integer;
             String fieldName;
             if (i == Integer.MIN_VALUE) {
               fieldName = "MIN_VALUE";
             } else { String fieldName;
               if (i == Integer.MAX_VALUE)
                 fieldName = "MAX_VALUE"; else {
                 return;
               }
             }
           } else {
             String fieldName;
             if ((value instanceof Short)) {
               short s = ((Short)value).shortValue();
               
               JvmType jvmType = JvmType.Short;
               String fieldName;
               if (s == Short.MIN_VALUE) {
                 fieldName = "MIN_VALUE";
               } else { String fieldName;
                 if (s == Short.MAX_VALUE)
                   fieldName = "MAX_VALUE"; else {
                   return;
                 }
               }
             } else {
               String fieldName;
               if ((value instanceof Byte)) {
                 byte b = ((Byte)value).byteValue();
                 
                 JvmType jvmType = JvmType.Byte;
                 String fieldName;
                 if (b == Byte.MIN_VALUE) {
                   fieldName = "MIN_VALUE";
                 } else { String fieldName;
                   if (b == Byte.MAX_VALUE)
                     fieldName = "MAX_VALUE"; else return;
                 }
               } else { return;
               }
             }
           }
         }
       }
     }
     String fieldName;
     JvmType jvmType;
     TypeDefinition currentType = this.context.getCurrentType();
     MetadataParser parser;
     MetadataParser parser; if (currentType != null) {
       parser = new MetadataParser(currentType);
     }
     else {
       parser = new MetadataParser(IMetadataResolver.EMPTY);
     }
     
     TypeReference declaringType = parser.parseTypeDescriptor("java/lang/" + jvmType.name());
     FieldReference field = parser.parseField(declaringType, fieldName, jvmType.getDescriptorPrefix());
     
     if ((currentType != null) && ((node.getParent() instanceof VariableInitializer)) && ((node.getParent().getParent() instanceof FieldDeclaration)) && (StringUtilities.equals(currentType.getInternalName(), declaringType.getInternalName())))
     {
 
 
 
       FieldDeclaration declaration = (FieldDeclaration)node.getParent().getParent();
       FieldDefinition actualField = (FieldDefinition)declaration.getUserData(Keys.FIELD_DEFINITION);
       
       if ((actualField == null) || (StringUtilities.equals(actualField.getName(), fieldName))) {
         switch (fieldName) {
         case "POSITIVE_INFINITY": 
           node.replaceWith(new BinaryOperatorExpression(new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? 1.0D : 1.0D)), BinaryOperatorType.DIVIDE, new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? 0.0D : 0.0D))));
           
 
 
 
 
 
           return;
         
 
         case "NEGATIVE_INFINITY": 
           node.replaceWith(new BinaryOperatorExpression(new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? -1.0D : -1.0D)), BinaryOperatorType.DIVIDE, new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? 0.0D : 0.0D))));
           
 
 
 
 
 
           return;
         
 
         case "NaN": 
           node.replaceWith(new BinaryOperatorExpression(new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? 0.0D : 0.0D)), BinaryOperatorType.DIVIDE, new PrimitiveExpression(node.getOffset(), Double.valueOf(jvmType == JvmType.Double ? 0.0D : 0.0D))));
           
 
 
 
 
 
           return;
         }
         
         
         return;
       }
     }
     
 
 
 
     AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
     AstType astType;
     AstType astType; if (astBuilder != null) {
       astType = astBuilder.convertType(declaringType);
     }
     else {
       astType = new SimpleType(declaringType.getName());
       astType.putUserData(Keys.TYPE_REFERENCE, declaringType);
     }
     
     MemberReferenceExpression memberReference = new MemberReferenceExpression(node.getOffset(), new TypeReferenceExpression(node.getOffset(), astType), fieldName, new AstType[0]);
     
 
 
 
     memberReference.putUserData(Keys.MEMBER_REFERENCE, field);
     memberReference.putUserData(Keys.CONSTANT_VALUE, value);
     
     node.replaceWith(memberReference);
   }
 }


