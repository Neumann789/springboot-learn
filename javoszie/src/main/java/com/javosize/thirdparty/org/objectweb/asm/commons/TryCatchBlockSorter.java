package com.javosize.thirdparty.org.objectweb.asm.commons;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TryCatchBlockSorter
  extends MethodNode
{
  public TryCatchBlockSorter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this(327680, paramMethodVisitor, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  protected TryCatchBlockSorter(int paramInt1, MethodVisitor paramMethodVisitor, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    this.mv = paramMethodVisitor;
  }
  
  public void visitEnd()
  {
    Comparator<TryCatchBlockNode> comp = new Comparator<TryCatchBlockNode>() {

        public int compare(TryCatchBlockNode t1, TryCatchBlockNode t2) {
            int len1 = blockLength(t1);
            int len2 = blockLength(t2);
            return len1 - len2;
        }

        private int blockLength(TryCatchBlockNode block) {
            int startidx = instructions.indexOf(block.start);
            int endidx = instructions.indexOf(block.end);
            return endidx - startidx;
        }
    };
    Collections.sort(this.tryCatchBlocks, comp);
    for (int i = 0; i < this.tryCatchBlocks.size(); i++) {
      ((TryCatchBlockNode)this.tryCatchBlocks.get(i)).updateIndex(i);
    }
    if (this.mv != null) {
      accept(this.mv);
    }
  }
  
  
}


