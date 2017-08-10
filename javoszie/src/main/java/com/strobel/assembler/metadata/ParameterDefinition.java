 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ParameterDefinition
   extends ParameterReference
   implements IAnnotationsProvider
 {
   private final Collection<CustomAnnotation> _customAnnotations = new Collection();
   private final List<CustomAnnotation> _customAnnotationsView = Collections.unmodifiableList(this._customAnnotations);
   private final int _size;
   private int _slot;
   private IMethodSignature _method;
   private TypeReference _declaringType;
   private long _flags;
   
   public ParameterDefinition(int slot, TypeReference parameterType)
   {
     super("", parameterType);
     this._slot = slot;
     this._size = (parameterType.getSimpleType().isDoubleWord() ? 2 : 1);
   }
   
   public ParameterDefinition(int slot, String name, TypeReference parameterType) {
     super(name, parameterType);
     this._slot = slot;
     this._size = (parameterType.getSimpleType().isDoubleWord() ? 2 : 1);
   }
   
   public final int getSize() {
     return this._size;
   }
   
   public final int getSlot() {
     return this._slot;
   }
   
   public final long getFlags() {
     return this._flags;
   }
   
   final void setFlags(long flags) {
     this._flags = flags;
   }
   
   final void setSlot(int slot) {
     this._slot = slot;
   }
   
   public final IMethodSignature getMethod() {
     return this._method;
   }
   
   final void setMethod(IMethodSignature method) {
     this._method = method;
   }
   
   public final boolean isFinal() {
     return Flags.testAny(this._flags, 16L);
   }
   
   public final boolean isMandated() {
     return Flags.testAny(this._flags, 32768L);
   }
   
   public final boolean isSynthetic() {
     return Flags.testAny(this._flags, 4096L);
   }
   
   public boolean hasAnnotations()
   {
     return !getAnnotations().isEmpty();
   }
   
   public List<CustomAnnotation> getAnnotations()
   {
     return this._customAnnotationsView;
   }
   
   protected final Collection<CustomAnnotation> getAnnotationsInternal() {
     return this._customAnnotations;
   }
   
   public final TypeReference getDeclaringType()
   {
     return this._declaringType;
   }
   
   final void setDeclaringType(TypeReference declaringType) {
     this._declaringType = declaringType;
   }
   
   public ParameterDefinition resolve()
   {
     TypeReference resolvedParameterType = super.getParameterType().resolve();
     
     if (resolvedParameterType != null) {
       setParameterType(resolvedParameterType);
     }
     
     return this;
   }
   
 
   private List<CustomAnnotation> populateCustomAnnotations()
   {
     return Collections.emptyList();
   }
 }


