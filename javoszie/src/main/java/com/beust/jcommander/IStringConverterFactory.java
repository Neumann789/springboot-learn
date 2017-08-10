package com.beust.jcommander;

public abstract interface IStringConverterFactory
{
  public abstract <T> Class<? extends IStringConverter<T>> getConverter(Class<T> paramClass);
}


