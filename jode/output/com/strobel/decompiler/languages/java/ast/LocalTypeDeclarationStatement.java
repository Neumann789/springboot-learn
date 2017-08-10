/* LocalTypeDeclarationStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class LocalTypeDeclarationStatement extends Statement
{
    public LocalTypeDeclarationStatement(int offset, TypeDeclaration type) {
	super(offset);
	setChildByRole(Roles.LOCAL_TYPE_DECLARATION, type);
    }
    
    public final TypeDeclaration getTypeDeclaration() {
	return (TypeDeclaration) getChildByRole(Roles.LOCAL_TYPE_DECLARATION);
    }
    
    public final void setTypeDeclaration(TypeDeclaration type) {
	setChildByRole(Roles.LOCAL_TYPE_DECLARATION, type);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitLocalTypeDeclarationStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	return false;
    }
}
