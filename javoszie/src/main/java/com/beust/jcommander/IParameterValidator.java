package com.beust.jcommander;

public abstract interface IParameterValidator
{
  public abstract void validate(String paramString1, String paramString2)
    throws ParameterException;
}


