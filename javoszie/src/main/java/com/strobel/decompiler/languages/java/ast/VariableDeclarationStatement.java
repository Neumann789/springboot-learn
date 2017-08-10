 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.Predicate;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class VariableDeclarationStatement
   extends Statement
 {
   public static final Role<JavaModifierToken> MODIFIER_ROLE = EntityDeclaration.MODIFIER_ROLE;
   private boolean _anyModifiers;
   
   public VariableDeclarationStatement()
   {
     super(-34);
   }
   
   public VariableDeclarationStatement(AstType type, String name, int offset) {
     this(type, name, offset, null);
   }
   
   public VariableDeclarationStatement(AstType type, String name, Expression initializer) {
     this(type, name, -34, initializer);
   }
   
   public VariableDeclarationStatement(AstType type, String name, int offset, Expression initializer) {
     super(initializer == null ? offset : initializer.getOffset());
     setType(type);
     getVariables().add(new VariableInitializer(name, initializer));
   }
   
 
 
   public final boolean isAnyModifiers()
   {
     return this._anyModifiers;
   }
   
 
 
   public final void setAnyModifiers(boolean value)
   {
     verifyNotFrozen();
     this._anyModifiers = value;
   }
   
   public final List<Modifier> getModifiers() {
     return EntityDeclaration.getModifiers(this);
   }
   
   public final void addModifier(Modifier modifier) {
     EntityDeclaration.addModifier(this, modifier);
   }
   
   public final void removeModifier(Modifier modifier) {
     EntityDeclaration.removeModifier(this, modifier);
   }
   
   public final void setModifiers(List<Modifier> modifiers) {
     EntityDeclaration.setModifiers(this, modifiers);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType value) {
     setChildByRole(Roles.TYPE, value);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public final AstNodeCollection<VariableInitializer> getVariables() {
     return getChildrenByRole(Roles.VARIABLE);
   }
   
   public final VariableInitializer getVariable(final String name) {
     (VariableInitializer)getVariables().firstOrNullObject(new Predicate()
     {
       public boolean test(VariableInitializer variable)
       {
         return StringUtilities.equals(variable.getName(), name);
       }
     });
   }
   
 
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitVariableDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof VariableDeclarationStatement)) {
       VariableDeclarationStatement otherDeclaration = (VariableDeclarationStatement)other;
       
       return (!other.isNull()) && (getType().matches(otherDeclaration.getType(), match)) && ((isAnyModifiers()) || (otherDeclaration.isAnyModifiers()) || (getChildrenByRole(MODIFIER_ROLE).matches(otherDeclaration.getChildrenByRole(MODIFIER_ROLE), match))) && (getVariables().matches(otherDeclaration.getVariables(), match));
     }
     
 
 
 
 
 
     return false;
   }
 }


