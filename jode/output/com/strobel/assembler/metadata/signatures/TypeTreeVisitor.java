/* TypeTreeVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public interface TypeTreeVisitor
{
    public Object getResult();
    
    public void visitFormalTypeParameter
	(FormalTypeParameter formaltypeparameter);
    
    public void visitClassTypeSignature(ClassTypeSignature classtypesignature);
    
    public void visitArrayTypeSignature(ArrayTypeSignature arraytypesignature);
    
    public void visitTypeVariableSignature
	(TypeVariableSignature typevariablesignature);
    
    public void visitWildcard(Wildcard wildcard);
    
    public void visitSimpleClassTypeSignature
	(SimpleClassTypeSignature simpleclasstypesignature);
    
    public void visitBottomSignature(BottomSignature bottomsignature);
    
    public void visitByteSignature(ByteSignature bytesignature);
    
    public void visitBooleanSignature(BooleanSignature booleansignature);
    
    public void visitShortSignature(ShortSignature shortsignature);
    
    public void visitCharSignature(CharSignature charsignature);
    
    public void visitIntSignature(IntSignature intsignature);
    
    public void visitLongSignature(LongSignature longsignature);
    
    public void visitFloatSignature(FloatSignature floatsignature);
    
    public void visitDoubleSignature(DoubleSignature doublesignature);
    
    public void visitVoidSignature(VoidSignature voidsignature);
}
