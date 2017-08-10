 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class DeclaredVariableBackReference
   extends Pattern
 {
   private final String _referencedGroupName;
   
   public DeclaredVariableBackReference(String referencedGroupName)
   {
     this._referencedGroupName = ((String)VerifyArgument.notNull(referencedGroupName, "referencedGroupName"));
   }
   
   public final String getReferencedGroupName() {
     return this._referencedGroupName;
   }
   
   public final boolean matches(INode other, Match match)
   {
     if ((other instanceof AstNode)) {
       INode lastInGroup = (INode)CollectionUtilities.lastOrDefault(match.get(this._referencedGroupName));
       
       if ((lastInGroup instanceof VariableDeclarationStatement)) {
         VariableDeclarationStatement referenced = (VariableDeclarationStatement)lastInGroup;
         AstNodeCollection<VariableInitializer> variables = referenced.getVariables();
         
         return (variables.hasSingleElement()) && (matchString(((VariableInitializer)variables.firstOrNullObject()).getName(), ((Identifier)((AstNode)other).getChildByRole(Roles.IDENTIFIER)).getName()));
       }
     }
     
 
 
 
     return false;
   }
 }


