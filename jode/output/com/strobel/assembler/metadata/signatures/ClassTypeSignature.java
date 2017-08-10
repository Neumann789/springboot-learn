/* ClassTypeSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;
import java.util.List;

public final class ClassTypeSignature implements FieldTypeSignature
{
    private final List _path;
    
    private ClassTypeSignature(List path) {
	_path = path;
    }
    
    public static ClassTypeSignature make(List p) {
	return new ClassTypeSignature(p);
    }
    
    public List getPath() {
	return _path;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitClassTypeSignature(this);
    }
}
