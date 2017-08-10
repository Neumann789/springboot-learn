 package com.strobel.decompiler.types;
 
 import com.strobel.collections.ImmutableList;
 import com.strobel.core.VerifyArgument;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class TypeProxy
   implements ITypeInfo
 {
   private static final List<ITypeListener> EMPTY_LISTENERS = ;
   
   private final ITypeListener _listener;
   private List<ITypeListener> _listeners;
   private ITypeInfo _delegate;
   
   TypeProxy(ITypeInfo delegate)
   {
     VerifyArgument.notNull(delegate, "delegate");
     
     this._listeners = EMPTY_LISTENERS;
     this._listener = new DelegateListener(null);
     
     setDelegate(delegate);
   }
   
   final void setDelegate(ITypeInfo delegate) {
     VerifyArgument.notNull(delegate, "delegate");
     
     if (this._delegate != null) {
       this._delegate.removeListener(this._listener);
     }
     
     this._delegate = delegate;
     this._delegate.addListener(this._listener);
   }
   
   public final String getName()
   {
     return this._delegate.getName();
   }
   
   public final String getPackageName()
   {
     return this._delegate.getPackageName();
   }
   
   public final String getFullName()
   {
     return this._delegate.getFullName();
   }
   
   public final String getCanonicalName()
   {
     return this._delegate.getCanonicalName();
   }
   
   public final String getInternalName()
   {
     return this._delegate.getInternalName();
   }
   
   public final String getSignature()
   {
     return this._delegate.getSignature();
   }
   
   public final boolean isArray()
   {
     return this._delegate.isArray();
   }
   
   public final boolean isPrimitive()
   {
     return this._delegate.isPrimitive();
   }
   
   public final boolean isPrimitiveOrVoid()
   {
     return this._delegate.isPrimitiveOrVoid();
   }
   
   public final boolean isVoid()
   {
     return this._delegate.isVoid();
   }
   
   public final boolean isRawType()
   {
     return this._delegate.isRawType();
   }
   
   public final boolean isGenericType()
   {
     return this._delegate.isGenericType();
   }
   
   public final boolean isGenericTypeInstance()
   {
     return this._delegate.isGenericTypeInstance();
   }
   
   public final boolean isGenericTypeDefinition()
   {
     return this._delegate.isGenericTypeDefinition();
   }
   
   public final boolean isGenericParameter()
   {
     return this._delegate.isGenericParameter();
   }
   
   public final boolean isWildcard()
   {
     return this._delegate.isWildcard();
   }
   
   public final boolean isUnknownType()
   {
     return this._delegate.isUnknownType();
   }
   
   public final boolean isBound()
   {
     return this._delegate.isBound();
   }
   
   public final boolean isLocal()
   {
     return this._delegate.isLocal();
   }
   
   public final boolean isAnonymous()
   {
     return this._delegate.isAnonymous();
   }
   
   public final ITypeInfo getDeclaringType()
   {
     return this._delegate.getDeclaringType();
   }
   
   public final boolean hasConstraints()
   {
     return this._delegate.hasConstraints();
   }
   
   public final boolean hasSuperConstraint()
   {
     return this._delegate.hasSuperConstraint();
   }
   
   public final boolean hasExtendsConstraint()
   {
     return this._delegate.hasExtendsConstraint();
   }
   
   public final ITypeInfo getElementType()
   {
     return this._delegate.getElementType();
   }
   
   public final ITypeInfo getSuperConstraint()
   {
     return this._delegate.getSuperConstraint();
   }
   
   public final ITypeInfo getExtendsConstraint()
   {
     return this._delegate.getExtendsConstraint();
   }
   
   public final ITypeInfo getSuperClass()
   {
     return this._delegate.getSuperClass();
   }
   
   public final ImmutableList<ITypeInfo> getSuperInterfaces()
   {
     return this._delegate.getSuperInterfaces();
   }
   
   public final ImmutableList<ITypeInfo> getGenericParameters()
   {
     return this._delegate.getGenericParameters();
   }
   
   public final ImmutableList<ITypeInfo> getTypeArguments()
   {
     return this._delegate.getTypeArguments();
   }
   
   public final ITypeInfo getGenericDefinition()
   {
     return this._delegate.getGenericDefinition();
   }
   
   public final void removeListener(ITypeListener listener)
   {
     VerifyArgument.notNull(listener, "listener");
     
     if (this._listeners == EMPTY_LISTENERS) {
       return;
     }
     
     this._listeners.remove(listener);
   }
   
   public final void addListener(ITypeListener listener)
   {
     VerifyArgument.notNull(listener, "listener");
     
     if (this._listeners == EMPTY_LISTENERS) {
       this._listeners = new ArrayList();
     }
     
     this._listeners.add(listener);
   }
   
   final void notifyChanged() {
     List<ITypeListener> listeners = this._listeners;
     
     if (listeners == EMPTY_LISTENERS) {
       return;
     }
     
     for (ITypeListener listener : listeners)
       listener.onChanged();
   }
   
   private final class DelegateListener implements ITypeListener {
     private DelegateListener() {}
     
     public final void onChanged() {
       TypeProxy.this.notifyChanged();
     }
   }
 }


