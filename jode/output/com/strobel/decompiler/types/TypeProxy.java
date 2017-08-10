/* TypeProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.strobel.collections.ImmutableList;
import com.strobel.core.VerifyArgument;

final class TypeProxy implements ITypeInfo
{
    private static final List EMPTY_LISTENERS = Collections.emptyList();
    private final ITypeListener _listener;
    private List _listeners;
    private ITypeInfo _delegate;
    
    private final class DelegateListener implements ITypeListener
    {
	private DelegateListener() {
	    super();
	}
	
	public final void onChanged() {
	    notifyChanged();
	}
    }
    
    TypeProxy(ITypeInfo delegate) {
	VerifyArgument.notNull(delegate, "delegate");
	_listeners = EMPTY_LISTENERS;
	_listener = new DelegateListener();
	setDelegate(delegate);
    }
    
    final void setDelegate(ITypeInfo delegate) {
    label_1866:
	{
	    VerifyArgument.notNull(delegate, "delegate");
	    if (_delegate != null)
		_delegate.removeListener(_listener);
	    break label_1866;
	}
	_delegate = delegate;
	_delegate.addListener(_listener);
    }
    
    public final String getName() {
	return _delegate.getName();
    }
    
    public final String getPackageName() {
	return _delegate.getPackageName();
    }
    
    public final String getFullName() {
	return _delegate.getFullName();
    }
    
    public final String getCanonicalName() {
	return _delegate.getCanonicalName();
    }
    
    public final String getInternalName() {
	return _delegate.getInternalName();
    }
    
    public final String getSignature() {
	return _delegate.getSignature();
    }
    
    public final boolean isArray() {
	return _delegate.isArray();
    }
    
    public final boolean isPrimitive() {
	return _delegate.isPrimitive();
    }
    
    public final boolean isPrimitiveOrVoid() {
	return _delegate.isPrimitiveOrVoid();
    }
    
    public final boolean isVoid() {
	return _delegate.isVoid();
    }
    
    public final boolean isRawType() {
	return _delegate.isRawType();
    }
    
    public final boolean isGenericType() {
	return _delegate.isGenericType();
    }
    
    public final boolean isGenericTypeInstance() {
	return _delegate.isGenericTypeInstance();
    }
    
    public final boolean isGenericTypeDefinition() {
	return _delegate.isGenericTypeDefinition();
    }
    
    public final boolean isGenericParameter() {
	return _delegate.isGenericParameter();
    }
    
    public final boolean isWildcard() {
	return _delegate.isWildcard();
    }
    
    public final boolean isUnknownType() {
	return _delegate.isUnknownType();
    }
    
    public final boolean isBound() {
	return _delegate.isBound();
    }
    
    public final boolean isLocal() {
	return _delegate.isLocal();
    }
    
    public final boolean isAnonymous() {
	return _delegate.isAnonymous();
    }
    
    public final ITypeInfo getDeclaringType() {
	return _delegate.getDeclaringType();
    }
    
    public final boolean hasConstraints() {
	return _delegate.hasConstraints();
    }
    
    public final boolean hasSuperConstraint() {
	return _delegate.hasSuperConstraint();
    }
    
    public final boolean hasExtendsConstraint() {
	return _delegate.hasExtendsConstraint();
    }
    
    public final ITypeInfo getElementType() {
	return _delegate.getElementType();
    }
    
    public final ITypeInfo getSuperConstraint() {
	return _delegate.getSuperConstraint();
    }
    
    public final ITypeInfo getExtendsConstraint() {
	return _delegate.getExtendsConstraint();
    }
    
    public final ITypeInfo getSuperClass() {
	return _delegate.getSuperClass();
    }
    
    public final ImmutableList getSuperInterfaces() {
	return _delegate.getSuperInterfaces();
    }
    
    public final ImmutableList getGenericParameters() {
	return _delegate.getGenericParameters();
    }
    
    public final ImmutableList getTypeArguments() {
	return _delegate.getTypeArguments();
    }
    
    public final ITypeInfo getGenericDefinition() {
	return _delegate.getGenericDefinition();
    }
    
    public final void removeListener(ITypeListener listener) {
	VerifyArgument.notNull(listener, "listener");
	if (_listeners != EMPTY_LISTENERS)
	    _listeners.remove(listener);
	return;
    }
    
    public final void addListener(ITypeListener listener) {
    label_1867:
	{
	    VerifyArgument.notNull(listener, "listener");
	    if (_listeners == EMPTY_LISTENERS)
		_listeners = new ArrayList();
	    break label_1867;
	}
	_listeners.add(listener);
    }
    
    final void notifyChanged() {
	List listeners = _listeners;
	if (listeners != EMPTY_LISTENERS) {
	    Iterator i$ = listeners.iterator();
	    while (i$.hasNext()) {
		ITypeListener listener = (ITypeListener) i$.next();
		listener.onChanged();
	    }
	}
	return;
    }
}
