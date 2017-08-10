package com.strobel.assembler.metadata.signatures;

import com.strobel.assembler.metadata.GenericParameter;
import com.strobel.assembler.metadata.IClassSignature;
import com.strobel.assembler.metadata.IMethodSignature;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.WildcardType;
import java.util.List;

public abstract interface MetadataFactory
{
  public abstract GenericParameter makeTypeVariable(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature);
  
  public abstract TypeReference makeParameterizedType(TypeReference paramTypeReference1, TypeReference paramTypeReference2, TypeReference... paramVarArgs);
  
  public abstract GenericParameter findTypeVariable(String paramString);
  
  public abstract WildcardType makeWildcard(FieldTypeSignature paramFieldTypeSignature1, FieldTypeSignature paramFieldTypeSignature2);
  
  public abstract TypeReference makeNamedType(String paramString);
  
  public abstract TypeReference makeArrayType(TypeReference paramTypeReference);
  
  public abstract TypeReference makeByte();
  
  public abstract TypeReference makeBoolean();
  
  public abstract TypeReference makeShort();
  
  public abstract TypeReference makeChar();
  
  public abstract TypeReference makeInt();
  
  public abstract TypeReference makeLong();
  
  public abstract TypeReference makeFloat();
  
  public abstract TypeReference makeDouble();
  
  public abstract TypeReference makeVoid();
  
  public abstract IMethodSignature makeMethodSignature(TypeReference paramTypeReference, List<TypeReference> paramList1, List<GenericParameter> paramList, List<TypeReference> paramList2);
  
  public abstract IClassSignature makeClassSignature(TypeReference paramTypeReference, List<TypeReference> paramList, List<GenericParameter> paramList1);
}


