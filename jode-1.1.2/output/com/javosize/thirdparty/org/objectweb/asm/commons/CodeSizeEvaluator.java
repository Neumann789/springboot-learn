/* CodeSizeEvaluator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator extends MethodVisitor implements Opcodes
{
    private int minSize;
    private int maxSize;
    
    public CodeSizeEvaluator(MethodVisitor methodvisitor) {
	this(327680, methodvisitor);
    }
    
    protected CodeSizeEvaluator(int i, MethodVisitor methodvisitor) {
	super(i, methodvisitor);
    }
    
    public int getMinSize() {
	return minSize;
    }
    
    public int getMaxSize() {
	return maxSize;
    }
    
    public void visitInsn(int i) {
	minSize++;
	maxSize++;
	if (mv != null)
	    mv.visitInsn(i);
    }
    
    public void visitIntInsn(int i, int i_0_) {
	if (i == 17) {
	    minSize += 3;
	    maxSize += 3;
	} else {
	    minSize += 2;
	    maxSize += 2;
	}
	if (mv != null)
	    mv.visitIntInsn(i, i_0_);
    }
    
    public void visitVarInsn(int i, int i_1_) {
	if (i_1_ < 4 && i != 169) {
	    minSize++;
	    maxSize++;
	} else if (i_1_ >= 256) {
	    minSize += 4;
	    maxSize += 4;
	} else {
	    minSize += 2;
	    maxSize += 2;
	}
	if (mv != null)
	    mv.visitVarInsn(i, i_1_);
    }
    
    public void visitTypeInsn(int i, String string) {
	minSize += 3;
	maxSize += 3;
	if (mv != null)
	    mv.visitTypeInsn(i, string);
    }
    
    public void visitFieldInsn(int i, String string, String string_2_,
			       String string_3_) {
	minSize += 3;
	maxSize += 3;
	if (mv != null)
	    mv.visitFieldInsn(i, string, string_2_, string_3_);
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_4_,
				String string_5_) {
	if (api >= 327680)
	    super.visitMethodInsn(i, string, string_4_, string_5_);
	else
	    doVisitMethodInsn(i, string, string_4_, string_5_, i == 185);
    }
    
    public void visitMethodInsn(int i, String string, String string_6_,
				String string_7_, boolean bool) {
	if (api < 327680)
	    super.visitMethodInsn(i, string, string_6_, string_7_, bool);
	else
	    doVisitMethodInsn(i, string, string_6_, string_7_, bool);
    }
    
    private void doVisitMethodInsn(int i, String string, String string_8_,
				   String string_9_, boolean bool) {
	if (i == 185) {
	    minSize += 5;
	    maxSize += 5;
	} else {
	    minSize += 3;
	    maxSize += 3;
	}
	if (mv != null)
	    mv.visitMethodInsn(i, string, string_8_, string_9_, bool);
    }
    
    public transient void visitInvokeDynamicInsn
	(String string, String string_10_, Handle handle, Object[] objects) {
	minSize += 5;
	maxSize += 5;
	if (mv != null)
	    mv.visitInvokeDynamicInsn(string, string_10_, handle, objects);
    }
    
    public void visitJumpInsn(int i, Label label) {
	minSize += 3;
	if (i == 167 || i == 168)
	    maxSize += 5;
	else
	    maxSize += 8;
	if (mv != null)
	    mv.visitJumpInsn(i, label);
    }
    
    public void visitLdcInsn(Object object) {
	if (object instanceof Long || object instanceof Double) {
	    minSize += 3;
	    maxSize += 3;
	} else {
	    minSize += 2;
	    maxSize += 3;
	}
	if (mv != null)
	    mv.visitLdcInsn(object);
    }
    
    public void visitIincInsn(int i, int i_11_) {
	if (i > 255 || i_11_ > 127 || i_11_ < -128) {
	    minSize += 6;
	    maxSize += 6;
	} else {
	    minSize += 3;
	    maxSize += 3;
	}
	if (mv != null)
	    mv.visitIincInsn(i, i_11_);
    }
    
    public transient void visitTableSwitchInsn(int i, int i_12_, Label label,
					       Label[] labels) {
	minSize += 13 + labels.length * 4;
	maxSize += 16 + labels.length * 4;
	if (mv != null)
	    mv.visitTableSwitchInsn(i, i_12_, label, labels);
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	minSize += 9 + is.length * 8;
	maxSize += 12 + is.length * 8;
	if (mv != null)
	    mv.visitLookupSwitchInsn(label, is, labels);
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	minSize += 4;
	maxSize += 4;
	if (mv != null)
	    mv.visitMultiANewArrayInsn(string, i);
    }
}
