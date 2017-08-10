 package com.strobel.assembler.metadata;
 
 import com.strobel.compilerservices.RuntimeHelpers;
 import com.strobel.core.Fences;
 import com.strobel.core.VerifyArgument;
 import java.util.concurrent.ConcurrentHashMap;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MetadataSystem
   extends MetadataResolver
 {
   private static MetadataSystem _instance;
   private final ConcurrentHashMap<String, TypeDefinition> _types;
   private final ITypeLoader _typeLoader;
   private boolean _isEagerMethodLoadingEnabled;
   
   public static MetadataSystem instance()
   {
     if (_instance == null) {
       synchronized (MetadataSystem.class) {
         if (_instance == null) {
           _instance = (MetadataSystem)Fences.orderWrites(new MetadataSystem());
         }
       }
     }
     return _instance;
   }
   
   public MetadataSystem() {
     this(new ClasspathTypeLoader());
   }
   
   public MetadataSystem(String classPath) {
     this(new ClasspathTypeLoader((String)VerifyArgument.notNull(classPath, "classPath")));
   }
   
   public MetadataSystem(ITypeLoader typeLoader) {
     this._typeLoader = ((ITypeLoader)VerifyArgument.notNull(typeLoader, "typeLoader"));
     this._types = new ConcurrentHashMap();
   }
   
   public final boolean isEagerMethodLoadingEnabled() {
     return this._isEagerMethodLoadingEnabled;
   }
   
   public final void setEagerMethodLoadingEnabled(boolean value) {
     this._isEagerMethodLoadingEnabled = value;
   }
   
   public void addTypeDefinition(TypeDefinition type) {
     VerifyArgument.notNull(type, "type");
     this._types.putIfAbsent(type.getInternalName(), type);
   }
   
   protected TypeDefinition resolveCore(TypeReference type)
   {
     VerifyArgument.notNull(type, "type");
     return resolveType(type.getInternalName(), false);
   }
   
   protected TypeReference lookupTypeCore(String descriptor)
   {
     return resolveType(descriptor, true);
   }
   
   protected TypeDefinition resolveType(String descriptor, boolean mightBePrimitive) {
     VerifyArgument.notNull(descriptor, "descriptor");
     
     if (mightBePrimitive) {
       if (descriptor.length() == 1) {
         int primitiveHash = descriptor.charAt(0) - 'B';
         
         if ((primitiveHash >= 0) && (primitiveHash < PRIMITIVE_TYPES_BY_DESCRIPTOR.length)) {
           TypeDefinition primitiveType = PRIMITIVE_TYPES_BY_DESCRIPTOR[primitiveHash];
           
           if (primitiveType != null) {
             return primitiveType;
           }
         }
       }
       else {
         int primitiveHash = hashPrimitiveName(descriptor);
         
         if ((primitiveHash >= 0) && (primitiveHash < PRIMITIVE_TYPES_BY_NAME.length)) {
           TypeDefinition primitiveType = PRIMITIVE_TYPES_BY_NAME[primitiveHash];
           
           if ((primitiveType != null) && (descriptor.equals(primitiveType.getName()))) {
             return primitiveType;
           }
         }
       }
     }
     
     TypeDefinition cachedDefinition = (TypeDefinition)this._types.get(descriptor);
     
     if (cachedDefinition != null) {
       return cachedDefinition;
     }
     
     Buffer buffer = new Buffer(0);
     
     if (!this._typeLoader.tryLoadType(descriptor, buffer)) {
       return null;
     }
     
     TypeDefinition typeDefinition = ClassFileReader.readClass(this._isEagerMethodLoadingEnabled ? 3 : 1, this, buffer);
     
 
 
 
 
 
     cachedDefinition = (TypeDefinition)this._types.putIfAbsent(descriptor, typeDefinition);
     typeDefinition.setTypeLoader(this._typeLoader);
     
     if (cachedDefinition != null) {
       return cachedDefinition;
     }
     
     return typeDefinition;
   }
   
 
 
   private static final TypeDefinition[] PRIMITIVE_TYPES_BY_NAME = new TypeDefinition[25];
   private static final TypeDefinition[] PRIMITIVE_TYPES_BY_DESCRIPTOR = new TypeDefinition[16];
   
   static {
     RuntimeHelpers.ensureClassInitialized(BuiltinTypes.class);
     
     TypeDefinition[] allPrimitives = { BuiltinTypes.Boolean, BuiltinTypes.Byte, BuiltinTypes.Character, BuiltinTypes.Short, BuiltinTypes.Integer, BuiltinTypes.Long, BuiltinTypes.Float, BuiltinTypes.Double, BuiltinTypes.Void };
     
 
 
 
 
 
 
 
 
 
 
     for (TypeDefinition t : allPrimitives) {
       PRIMITIVE_TYPES_BY_DESCRIPTOR[hashPrimitiveName(t.getName())] = t;
       PRIMITIVE_TYPES_BY_NAME[(t.getInternalName().charAt(0) - 'B')] = t;
     }
   }
   
   private static int hashPrimitiveName(String name) {
     if (name.length() < 3) {
       return 0;
     }
     return (name.charAt(0) + name.charAt(2)) % 16;
   }
 }


