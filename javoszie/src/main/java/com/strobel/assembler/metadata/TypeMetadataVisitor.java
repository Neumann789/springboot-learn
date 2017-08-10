package com.strobel.assembler.metadata;

public abstract interface TypeMetadataVisitor<P, R>
{
  public abstract R visitType(TypeReference paramTypeReference, P paramP);
  
  public abstract R visitArrayType(ArrayType paramArrayType, P paramP);
  
  public abstract R visitGenericParameter(GenericParameter paramGenericParameter, P paramP);
  
  public abstract R visitWildcard(WildcardType paramWildcardType, P paramP);
  
  public abstract R visitCapturedType(CapturedType paramCapturedType, P paramP);
  
  public abstract R visitCompoundType(CompoundTypeReference paramCompoundTypeReference, P paramP);
  
  public abstract R visitParameterizedType(TypeReference paramTypeReference, P paramP);
  
  public abstract R visitPrimitiveType(PrimitiveType paramPrimitiveType, P paramP);
  
  public abstract R visitClassType(TypeReference paramTypeReference, P paramP);
  
  public abstract R visitNullType(TypeReference paramTypeReference, P paramP);
  
  public abstract R visitBottomType(TypeReference paramTypeReference, P paramP);
  
  public abstract R visitRawType(RawType paramRawType, P paramP);
}


