 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SwitchSection
   extends AstNode
 {
   public static final Role<CaseLabel> CaseLabelRole = new Role("CaseLabel", CaseLabel.class);
   
   public final AstNodeCollection<Statement> getStatements() {
     return getChildrenByRole(Roles.EMBEDDED_STATEMENT);
   }
   
   public final AstNodeCollection<CaseLabel> getCaseLabels() {
     return getChildrenByRole(CaseLabelRole);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitSwitchSection(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof SwitchSection)) {
       SwitchSection otherSection = (SwitchSection)other;
       
       return (!otherSection.isNull()) && (getCaseLabels().matches(otherSection.getCaseLabels(), match)) && (getStatements().matches(otherSection.getStatements(), match));
     }
     
 
 
     return false;
   }
 }


