package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LocalVariableAnnotationRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$LocalVariableAnnotationRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    String str = paramAttributes.getValue("desc");
    boolean bool = Boolean.valueOf(paramAttributes.getValue("visible")).booleanValue();
    int i = Integer.parseInt(paramAttributes.getValue("typeRef"));
    TypePath localTypePath = TypePath.fromString(paramAttributes.getValue("typePath"));
    String[] arrayOfString1 = paramAttributes.getValue("start").split(" ");
    Label[] arrayOfLabel1 = new Label[arrayOfString1.length];
    for (int j = 0; j < arrayOfLabel1.length; j++) {
      arrayOfLabel1[j] = getLabel(arrayOfString1[j]);
    }
    String[] arrayOfString2 = paramAttributes.getValue("end").split(" ");
    Label[] arrayOfLabel2 = new Label[arrayOfString2.length];
    for (int k = 0; k < arrayOfLabel2.length; k++) {
      arrayOfLabel2[k] = getLabel(arrayOfString2[k]);
    }
    String[] arrayOfString3 = paramAttributes.getValue("index").split(" ");
    int[] arrayOfInt = new int[arrayOfString3.length];
    for (int m = 0; m < arrayOfInt.length; m++) {
      arrayOfInt[m] = Integer.parseInt(arrayOfString3[m]);
    }
    this.this$0.push(((MethodVisitor)this.this$0.peek()).visitLocalVariableAnnotation(i, localTypePath, arrayOfLabel1, arrayOfLabel2, arrayOfInt, str, bool));
  }
  
  public void end(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.pop();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visitEnd();
    }
  }
}


