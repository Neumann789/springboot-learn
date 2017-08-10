/* LocalVariableTable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.bytecode.LocalVariableInfo;
import jode.type.Type;

public class LocalVariableTable
{
    LocalVariableRangeList[] locals;
    
    public LocalVariableTable(int i, LocalVariableInfo[] localvariableinfos) {
	locals = new LocalVariableRangeList[i];
	int i_0_ = 0;
	for (;;) {
	    if (i_0_ >= i) {
		i_0_ = 0;
		for (;;) {
		    IF (i_0_ >= localvariableinfos.length)
			/* empty */
		    locals[localvariableinfos[i_0_].slot].addLocal
			(localvariableinfos[i_0_].start.getAddr(),
			 localvariableinfos[i_0_].end.getAddr(),
			 localvariableinfos[i_0_].name,
			 Type.tType(localvariableinfos[i_0_].type));
		    i_0_++;
		}
	    }
	    locals[i_0_] = new LocalVariableRangeList();
	    i_0_++;
	}
    }
    
    public LocalVarEntry getLocal(int i, int i_1_)
	throws ArrayIndexOutOfBoundsException {
	return locals[i].getInfo(i_1_);
    }
}
