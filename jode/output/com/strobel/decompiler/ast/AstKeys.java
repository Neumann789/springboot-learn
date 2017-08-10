/* AstKeys - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import com.strobel.componentmodel.Key;
import com.strobel.util.ContractUtils;

public final class AstKeys
{
    public static final Key SWITCH_INFO = Key.create("SwitchInfo");
    public static final Key PARENT_LAMBDA_BINDING
	= Key.create("ParentLambdaBinding");
    public static final Key TYPE_ARGUMENTS = Key.create("TypeArguments");
    
    private AstKeys() {
	throw ContractUtils.unreachable();
    }
}
