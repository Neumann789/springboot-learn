package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

public final class SAXFieldAdapter
  extends FieldVisitor
{
  SAXAdapter sa;
  
  public SAXFieldAdapter(SAXAdapter paramSAXAdapter, Attributes paramAttributes)
  {
    super(327680);
    this.sa = paramSAXAdapter;
    paramSAXAdapter.addStart("field", paramAttributes);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    return new SAXAnnotationAdapter(this.sa, "annotation", paramBoolean ? 1 : -1, null, paramString);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    return new SAXAnnotationAdapter(this.sa, "typeAnnotation", paramBoolean ? 1 : -1, null, paramString, paramInt, paramTypePath);
  }
  
  public void visitEnd()
  {
    this.sa.addEnd("field");
  }
}


