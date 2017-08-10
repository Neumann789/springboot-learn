/* IMetadataScope - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public interface IMetadataScope
{
    public TypeReference lookupType(int i);
    
    public FieldReference lookupField(int i);
    
    public MethodReference lookupMethod(int i);
    
    public MethodHandle lookupMethodHandle(int i);
    
    public IMethodSignature lookupMethodType(int i);
    
    public DynamicCallSite lookupDynamicCallSite(int i);
    
    public FieldReference lookupField(int i, int i_0_);
    
    public MethodReference lookupMethod(int i, int i_1_);
    
    public Object lookupConstant(int i);
    
    public Object lookup(int i);
}
