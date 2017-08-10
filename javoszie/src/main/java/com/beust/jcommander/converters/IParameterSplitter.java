package com.beust.jcommander.converters;

import java.util.List;

public abstract interface IParameterSplitter
{
  public abstract List<String> split(String paramString);
}


