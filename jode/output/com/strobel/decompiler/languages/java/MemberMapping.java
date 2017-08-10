/* MemberMapping - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MethodDefinition;

public final class MemberMapping
{
    private MemberReference _memberReference;
    private Iterable _localVariables;
    
    MemberMapping() {
	/* empty */
    }
    
    public MemberMapping(MethodDefinition method) {
	setMemberReference(method);
    }
    
    public MemberReference getMemberReference() {
	return _memberReference;
    }
    
    public void setMemberReference(MemberReference memberReference) {
	_memberReference = memberReference;
    }
    
    public Iterable getLocalVariables() {
	return _localVariables;
    }
    
    public void setLocalVariables(Iterable localVariables) {
	_localVariables = localVariables;
    }
}
