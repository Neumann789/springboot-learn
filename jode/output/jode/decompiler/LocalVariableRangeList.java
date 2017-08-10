/* LocalVariableRangeList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.GlobalOptions;
import jode.type.Type;

public class LocalVariableRangeList
{
    LocalVarEntry list = null;
    
    LocalVariableRangeList() {
	/* empty */
    }
    
    private void add(LocalVarEntry localvarentry) {
	LocalVarEntry localvarentry_0_ = null;
	LocalVarEntry localvarentry_1_ = list;
    label_867:
	{
	    for (;;) {
		if (localvarentry_1_ == null
		    || localvarentry_1_.endAddr >= localvarentry.startAddr) {
		    if (localvarentry_1_ != null
			&& (localvarentry.endAddr
			    >= localvarentry_1_.startAddr)) {
			if (!localvarentry_1_.type.equals(localvarentry.type)
			    || !localvarentry_1_.name
				    .equals(localvarentry.name))
			    GlobalOptions.err
				.println("warning: non disjoint locals");
			else {
			    localvarentry_1_.startAddr
				= Math.min(localvarentry_1_.startAddr,
					   localvarentry.startAddr);
			    localvarentry_1_.endAddr
				= Math.max(localvarentry_1_.endAddr,
					   localvarentry.endAddr);
			    return;
			}
		    }
		    break;
		}
		localvarentry_0_ = localvarentry_1_;
		localvarentry_1_ = localvarentry_1_.next;
	    }
	}
	localvarentry.next = localvarentry_1_;
	if (localvarentry_0_ != null)
	    localvarentry_0_.next = localvarentry;
	else
	    list = localvarentry;
	break label_867;
    }
    
    private LocalVarEntry find(int i) {
	LocalVarEntry localvarentry = list;
	for (;;) {
	    if (localvarentry == null || localvarentry.endAddr >= i) {
		if (localvarentry != null && localvarentry.startAddr <= i)
		    return localvarentry;
		return null;
	    }
	    localvarentry = localvarentry.next;
	}
    }
    
    public void addLocal(int i, int i_2_, String string, Type type) {
	LocalVarEntry localvarentry = new LocalVarEntry(i, i_2_, string, type);
	add(localvarentry);
    }
    
    public LocalVarEntry getInfo(int i) {
	return find(i);
    }
}
