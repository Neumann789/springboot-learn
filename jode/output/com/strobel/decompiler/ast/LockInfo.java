/* LockInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.strobel.core.VerifyArgument;

final class LockInfo
{
    public final Label leadingLabel;
    public final Expression lockInit;
    public final Expression lockStore;
    public final Expression lockStoreCopy;
    public final Expression lockAcquire;
    public final Variable lock;
    public final Variable lockCopy;
    public final int operationCount;
    public final boolean isSimpleAcquire;
    
    public final List getLockVariables() {
	if (lockCopy != null)
	    return Arrays.asList(new Variable[] { lock, lockCopy });
	return Collections.singletonList(lock);
    }
    
    LockInfo(Label leadingLabel, Expression lockAcquire) {
	this(leadingLabel, null, null, null, lockAcquire);
    }
    
    LockInfo(Label leadingLabel, Expression lockInit, Expression lockStore,
	     Expression lockStoreCopy, Expression lockAcquire) {
	this.leadingLabel = leadingLabel;
	this.lockInit = lockInit;
	this.lockStore = lockStore;
	this.lockStoreCopy = lockStoreCopy;
	this.lockAcquire
	    = (Expression) VerifyArgument.notNull(lockAcquire, "lockAcquire");
    label_1504:
	{
	    lock = (Variable) ((Expression) lockAcquire.getArguments().get(0))
				  .getOperand();
	    if (lockStoreCopy == null)
		lockCopy = null;
	    else
		lockCopy = (Variable) lockStoreCopy.getOperand();
	    break label_1504;
	}
    label_1505:
	{
	    PUSH this;
	    if (lockInit != null || lockStore != null || lockStoreCopy != null)
		PUSH false;
	    else
		PUSH true;
	    break label_1505;
	}
	((LockInfo) POP).isSimpleAcquire = POP;
    label_1507:
	{
	label_1506:
	    {
		PUSH this;
		if (leadingLabel == null)
		    PUSH false;
		else
		    PUSH true;
		break label_1506;
	    }
	    if (lockStore == null)
		PUSH false;
	    else
		PUSH true;
	    break label_1507;
	}
    label_1508:
	{
	    PUSH POP + POP;
	    if (lockStoreCopy == null)
		PUSH false;
	    else
		PUSH true;
	    break label_1508;
	}
	((LockInfo) POP).operationCount = POP + POP + 1;
    }
}
