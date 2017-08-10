 package com.strobel.assembler.metadata;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BuiltinTypes
 {
   public static final TypeDefinition Boolean = new PrimitiveType(JvmType.Boolean);
   public static final TypeDefinition Byte = new PrimitiveType(JvmType.Byte);
   public static final TypeDefinition Character = new PrimitiveType(JvmType.Character);
   public static final TypeDefinition Short = new PrimitiveType(JvmType.Short);
   public static final TypeDefinition Integer = new PrimitiveType(JvmType.Integer);
   public static final TypeDefinition Long = new PrimitiveType(JvmType.Long);
   public static final TypeDefinition Float = new PrimitiveType(JvmType.Float);
   public static final TypeDefinition Double = new PrimitiveType(JvmType.Double);
   public static final TypeDefinition Void = new PrimitiveType(JvmType.Void);
   public static final TypeDefinition Object; public static final TypeDefinition Bottom = BottomType.INSTANCE;
   public static final TypeDefinition Null = NullType.INSTANCE;
   
   static { Buffer buffer = new Buffer();
     ITypeLoader typeLoader = new ClasspathTypeLoader();
     
     if (!typeLoader.tryLoadType("java/lang/Object", buffer)) {
       throw Error.couldNotLoadObjectType();
     }
     
     MetadataSystem metadataSystem = MetadataSystem.instance();
     
     Object = ClassFileReader.readClass(metadataSystem, buffer);
     
     buffer.reset();
     
     if (!typeLoader.tryLoadType("java/lang/Class", buffer)) {
       throw Error.couldNotLoadClassType();
     }
     
     Class = ClassFileReader.readClass(metadataSystem, buffer);
   }
   
   public static final TypeDefinition Class;
   public static TypeDefinition fromPrimitiveTypeCode(int code) { switch (code) {
     case 4: 
       return Boolean;
     case 8: 
       return Byte;
     case 9: 
       return Short;
     case 10: 
       return Integer;
     case 11: 
       return Long;
     case 5: 
       return Character;
     case 6: 
       return Float;
     case 7: 
       return Double;
     }
     throw ContractUtils.unreachable();
   }
 }


