package com.beust.jcommander;

public abstract interface IStringConverter<T>
{
  public abstract T convert(String paramString);
}


