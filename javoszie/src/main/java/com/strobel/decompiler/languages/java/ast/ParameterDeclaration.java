 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ParameterDeclaration
   extends EntityDeclaration
 {
   public static final Role<Annotation> ANNOTATION_ROLE = EntityDeclaration.ANNOTATION_ROLE;
   
   public ParameterDeclaration() {}
   
   public ParameterDeclaration(String name, AstType type)
   {
     setName(name);
     setType(type);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public final AstType getType()
   {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType value) {
     setChildByRole(Roles.TYPE, value);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public EntityType getEntityType()
   {
     return EntityType.PARAMETER;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitParameterDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ParameterDeclaration)) {
       ParameterDeclaration otherDeclaration = (ParameterDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (matchString(getName(), otherDeclaration.getName())) && (getType().matches(otherDeclaration.getType(), match));
     }
     
 
 
 
     return false;
   }
   
 
   public static ParameterDeclaration forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends ParameterDeclaration {
     final Pattern child;
     
     PatternPlaceholder(Pattern child) {
       this.child = child;
     }
     
     public NodeType getNodeType()
     {
       return NodeType.PATTERN;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return (R)visitor.visitPatternPlaceholder(this, this.child, data);
     }
     
     public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
     {
       return this.child.matchesCollection(role, position, match, backtrackingInfo);
     }
     
     public boolean matches(INode other, Match match)
     {
       return this.child.matches(other, match);
     }
   }
 }


