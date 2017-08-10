package com.beust.jcommander;

public abstract interface IValueValidator<T>
{
  public abstract void validate(String paramString, T paramT)
    throws ParameterException;
}


