 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class EntityDeclaration
   extends AstNode
 {
   public static final Role<Annotation> ANNOTATION_ROLE = Roles.ANNOTATION;
   public static final Role<Annotation> UNATTACHED_ANNOTATION_ROLE = new Role("UnattachedAnnotation", Annotation.class);
   public static final Role<JavaModifierToken> MODIFIER_ROLE = new Role("Modifier", JavaModifierToken.class);
   public static final Role<AstType> PRIVATE_IMPLEMENTATION_TYPE_ROLE = new Role("PrivateImplementationType", AstType.class, AstType.NULL);
   
 
   private boolean _anyModifiers;
   
 
   public final boolean isAnyModifiers()
   {
     return this._anyModifiers;
   }
   
 
 
   public final void setAnyModifiers(boolean value)
   {
     verifyNotFrozen();
     this._anyModifiers = value;
   }
   
   public NodeType getNodeType()
   {
     return NodeType.MEMBER;
   }
   
   public abstract EntityType getEntityType();
   
   public final AstNodeCollection<Annotation> getAnnotations() {
     return getChildrenByRole(ANNOTATION_ROLE);
   }
   
   public final boolean hasModifier(Modifier modifier) {
     for (JavaModifierToken modifierToken : getModifiers()) {
       if (modifierToken.getModifier() == modifier) {
         return true;
       }
     }
     return false;
   }
   
   public final AstNodeCollection<JavaModifierToken> getModifiers() {
     return getChildrenByRole(MODIFIER_ROLE);
   }
   
   public final String getName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setName(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setNameToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final AstType getReturnType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setReturnType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public EntityDeclaration clone()
   {
     EntityDeclaration copy = (EntityDeclaration)super.clone();
     copy._anyModifiers = this._anyModifiers;
     return copy;
   }
   
   protected final boolean matchAnnotationsAndModifiers(EntityDeclaration other, Match match) {
     VerifyArgument.notNull(other, "other");
     
     return (other != null) && (!other.isNull()) && ((isAnyModifiers()) || (getModifiers().matches(other.getModifiers(), match))) && (getAnnotations().matches(other.getAnnotations(), match));
   }
   
 
 
   static List<Modifier> getModifiers(AstNode node)
   {
     List<Modifier> modifiers = null;
     
     for (JavaModifierToken modifierToken : node.getChildrenByRole(MODIFIER_ROLE)) {
       if (modifiers == null) {
         modifiers = new ArrayList();
       }
       
       modifiers.add(modifierToken.getModifier());
     }
     
     return modifiers != null ? Collections.unmodifiableList(modifiers) : Collections.emptyList();
   }
   
   static void setModifiers(AstNode node, Collection<Modifier> modifiers)
   {
     AstNodeCollection<JavaModifierToken> modifierTokens = node.getChildrenByRole(MODIFIER_ROLE);
     
     modifierTokens.clear();
     
     for (Modifier modifier : modifiers) {
       modifierTokens.add(new JavaModifierToken(TextLocation.EMPTY, modifier));
     }
   }
   
   static void addModifier(AstNode node, Modifier modifier) {
     List<Modifier> modifiers = getModifiers(node);
     
     if (modifiers.contains(modifier)) {
       return;
     }
     
     node.addChild(new JavaModifierToken(TextLocation.EMPTY, modifier), MODIFIER_ROLE);
   }
   
   static boolean removeModifier(AstNode node, Modifier modifier) {
     AstNodeCollection<JavaModifierToken> modifierTokens = node.getChildrenByRole(MODIFIER_ROLE);
     
     for (JavaModifierToken modifierToken : modifierTokens) {
       if (modifierToken.getModifier() == modifier) {
         modifierToken.remove();
         return true;
       }
     }
     
     return false;
   }
 }


