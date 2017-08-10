package com.beust.jcommander;

public abstract interface IParameterValidator2
  extends IParameterValidator
{
  public abstract void validate(String paramString1, String paramString2, ParameterDescription paramParameterDescription)
    throws ParameterException;
}


