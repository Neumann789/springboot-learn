/* IMemberDefinition - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public interface IMemberDefinition
{
    public String getName();
    
    public String getFullName();
    
    public boolean isSpecialName();
    
    public TypeReference getDeclaringType();
    
    public long getFlags();
    
    public int getModifiers();
    
    public boolean isFinal();
    
    public boolean isNonPublic();
    
    public boolean isPrivate();
    
    public boolean isProtected();
    
    public boolean isPublic();
    
    public boolean isStatic();
    
    public boolean isSynthetic();
    
    public boolean isDeprecated();
    
    public boolean isPackagePrivate();
    
    public String getBriefDescription();
    
    public String getDescription();
    
    public String getErasedDescription();
    
    public String getSimpleDescription();
}
