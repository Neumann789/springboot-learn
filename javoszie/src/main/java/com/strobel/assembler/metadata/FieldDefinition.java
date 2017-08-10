 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.HashUtilities;
 import com.strobel.core.StringUtilities;
 import java.util.Collections;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class FieldDefinition
   extends FieldReference
   implements IMemberDefinition, IConstantValueProvider
 {
   private final Collection<CustomAnnotation> _customAnnotations;
   private final Collection<SourceAttribute> _sourceAttributes;
   private final List<CustomAnnotation> _customAnnotationsView;
   private final List<SourceAttribute> _sourceAttributesView;
   private final IMetadataResolver _resolver;
   private String _name;
   private Object _fieldType;
   private TypeDefinition _declaringType;
   private Object _constantValue;
   private long _flags;
   
   protected FieldDefinition(IMetadataResolver resolver)
   {
     this._resolver = resolver;
     this._customAnnotations = new Collection();
     this._customAnnotationsView = Collections.unmodifiableList(this._customAnnotations);
     this._sourceAttributes = new Collection();
     this._sourceAttributesView = Collections.unmodifiableList(this._sourceAttributes);
   }
   
   public final List<CustomAnnotation> getAnnotations()
   {
     return this._customAnnotationsView;
   }
   
   protected final Collection<CustomAnnotation> getAnnotationsInternal() {
     return this._customAnnotations;
   }
   
   public final List<SourceAttribute> getSourceAttributes() {
     return this._sourceAttributesView;
   }
   
   protected final Collection<SourceAttribute> getSourceAttributesInternal() {
     return this._sourceAttributes;
   }
   
   public int hashCode()
   {
     return HashUtilities.hashCode(getFullName());
   }
   
   public boolean equals(Object obj)
   {
     if ((obj instanceof FieldDefinition)) {
       FieldDefinition other = (FieldDefinition)obj;
       
       return (StringUtilities.equals(getName(), other.getName())) && (typeNamesMatch(getDeclaringType(), other.getDeclaringType()));
     }
     
 
     return false;
   }
   
   private boolean typeNamesMatch(TypeReference t1, TypeReference t2) {
     return (t1 != null) && (t2 != null) && (StringUtilities.equals(t1.getFullName(), t2.getFullName()));
   }
   
 
 
 
   public final boolean isEnumConstant()
   {
     return Flags.testAny(getFlags(), 16384L);
   }
   
   public final boolean hasConstantValue()
   {
     return this._constantValue != null;
   }
   
   public final Object getConstantValue()
   {
     return this._constantValue;
   }
   
   public final TypeReference getFieldType() {
     if ((this._fieldType instanceof TypeReference)) {
       return (TypeReference)this._fieldType;
     }
     
     if (((this._fieldType instanceof String)) && (this._resolver != null))
     {
 
       TypeReference fieldType = this._resolver.lookupType((String)this._fieldType);
       
       if (fieldType != null) {
         this._fieldType = fieldType;
         return fieldType;
       }
     }
     
     return null;
   }
   
   protected final void setFieldType(TypeReference fieldType) {
     this._fieldType = fieldType;
   }
   
   protected final void setConstantValue(Object constantValue) {
     this._constantValue = constantValue;
   }
   
 
 
 
   public final String getName()
   {
     return this._name;
   }
   
   protected final void setName(String name) {
     this._name = name;
   }
   
   public final boolean isDefinition()
   {
     return true;
   }
   
   public final TypeDefinition getDeclaringType() {
     return this._declaringType;
   }
   
   protected final void setDeclaringType(TypeDefinition declaringType) {
     this._declaringType = declaringType;
   }
   
   public final long getFlags() {
     return this._flags;
   }
   
   protected final void setFlags(long flags) {
     this._flags = flags;
   }
   
   public final int getModifiers() {
     return Flags.toModifiers(getFlags());
   }
   
   public final boolean isFinal() {
     return Flags.testAny(getFlags(), 16L);
   }
   
   public final boolean isNonPublic() {
     return !Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isPrivate() {
     return Flags.testAny(getFlags(), 2L);
   }
   
   public final boolean isProtected() {
     return Flags.testAny(getFlags(), 4L);
   }
   
   public final boolean isPublic() {
     return Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isStatic() {
     return Flags.testAny(getFlags(), 8L);
   }
   
   public final boolean isSynthetic() {
     return Flags.testAny(getFlags(), 4096L);
   }
   
   public final boolean isDeprecated() {
     return Flags.testAny(getFlags(), 131072L);
   }
   
   public final boolean isPackagePrivate() {
     return !Flags.testAny(getFlags(), 7L);
   }
   
 
 
 
 
 
 
 
   public String getBriefDescription()
   {
     return appendBriefDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getDescription()
   {
     return appendDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getErasedDescription()
   {
     return appendErasedDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getSimpleDescription()
   {
     return appendSimpleDescription(new StringBuilder()).toString();
   }
   
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     if (fullName) {
       TypeDefinition declaringType = getDeclaringType();
       
       if (declaringType != null) {
         return declaringType.appendName(sb, true, false).append('.').append(getName());
       }
     }
     
     return sb.append(this._name);
   }
   
   protected StringBuilder appendDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers())) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     TypeReference fieldType = getFieldType();
     
     if (fieldType.isGenericParameter()) {
       s.append(fieldType.getName());
     }
     else {
       s = fieldType.appendBriefDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     
     return s;
   }
   
   protected StringBuilder appendBriefDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers())) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     TypeReference fieldType = getFieldType();
     
     if (fieldType.isGenericParameter()) {
       s.append(fieldType.getName());
     }
     else {
       s = fieldType.appendBriefDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     
     return s;
   }
   
   protected StringBuilder appendErasedDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers())) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     s = getFieldType().getRawType().appendErasedDescription(s);
     s.append(' ');
     s.append(getName());
     
     return s;
   }
   
   protected StringBuilder appendSimpleDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers())) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     TypeReference fieldType = getFieldType();
     
     if (fieldType.isGenericParameter()) {
       s.append(fieldType.getName());
     }
     else {
       s = fieldType.appendSimpleDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     
     return s;
   }
   
   public String toString()
   {
     return getSimpleDescription();
   }
 }


