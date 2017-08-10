 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract interface IMetadataResolver
 {
   public static final IMetadataResolver EMPTY = new IMetadataResolver()
   {
     public void pushFrame(IResolverFrame frame) {}
     
 
 
     public void popFrame() {}
     
 
     public TypeReference lookupType(String descriptor)
     {
       return null;
     }
     
     public TypeDefinition resolve(TypeReference type)
     {
       return (type instanceof TypeDefinition) ? (TypeDefinition)type : null;
     }
     
 
     public FieldDefinition resolve(FieldReference field)
     {
       return (field instanceof FieldDefinition) ? (FieldDefinition)field : null;
     }
     
 
     public MethodDefinition resolve(MethodReference method)
     {
       return (method instanceof MethodDefinition) ? (MethodDefinition)method : null;
     }
   };
   
   public abstract void pushFrame(IResolverFrame paramIResolverFrame);
   
   public abstract void popFrame();
   
   public abstract TypeReference lookupType(String paramString);
   
   public abstract TypeDefinition resolve(TypeReference paramTypeReference);
   
   public abstract FieldDefinition resolve(FieldReference paramFieldReference);
   
   public abstract MethodDefinition resolve(MethodReference paramMethodReference);
 }


