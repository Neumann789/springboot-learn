package com.strobel.assembler.metadata;

import com.strobel.assembler.metadata.annotations.CustomAnnotation;
import java.util.List;

public abstract interface IAnnotationsProvider
{
  public abstract boolean hasAnnotations();
  
  public abstract List<CustomAnnotation> getAnnotations();
}


