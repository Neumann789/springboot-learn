/* ITypeInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.types;
import com.strobel.collections.ImmutableList;

interface ITypeInfo
{
    public String getName();
    
    public String getPackageName();
    
    public String getFullName();
    
    public String getCanonicalName();
    
    public String getInternalName();
    
    public String getSignature();
    
    public boolean isArray();
    
    public boolean isPrimitive();
    
    public boolean isPrimitiveOrVoid();
    
    public boolean isVoid();
    
    public boolean isRawType();
    
    public boolean isGenericType();
    
    public boolean isGenericTypeInstance();
    
    public boolean isGenericTypeDefinition();
    
    public boolean isGenericParameter();
    
    public boolean isWildcard();
    
    public boolean isUnknownType();
    
    public boolean isBound();
    
    public boolean isAnonymous();
    
    public boolean isLocal();
    
    public boolean hasConstraints();
    
    public boolean hasSuperConstraint();
    
    public boolean hasExtendsConstraint();
    
    public ITypeInfo getDeclaringType();
    
    public ITypeInfo getElementType();
    
    public ITypeInfo getSuperConstraint();
    
    public ITypeInfo getExtendsConstraint();
    
    public ITypeInfo getSuperClass();
    
    public ImmutableList getSuperInterfaces();
    
    public ImmutableList getGenericParameters();
    
    public ImmutableList getTypeArguments();
    
    public ITypeInfo getGenericDefinition();
    
    public void addListener(ITypeListener itypelistener);
    
    public void removeListener(ITypeListener itypelistener);
}
