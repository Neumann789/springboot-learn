 package com.strobel.decompiler.patterns;
 
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ParameterReferenceNode
   extends Pattern
 {
   private final int _parameterPosition;
   private final String _groupName;
   
   public ParameterReferenceNode(int parameterPosition)
   {
     this._parameterPosition = parameterPosition;
     this._groupName = null;
   }
   
   public ParameterReferenceNode(int parameterPosition, String groupName) {
     this._parameterPosition = parameterPosition;
     this._groupName = groupName;
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final int getParameterPosition() {
     return this._parameterPosition;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof IdentifierExpression)) {
       IdentifierExpression identifier = (IdentifierExpression)other;
       Variable variable = (Variable)identifier.getUserData(Keys.VARIABLE);
       
       if ((variable != null) && (variable.isParameter()) && (variable.getOriginalParameter().getPosition() == this._parameterPosition))
       {
 
 
         if (this._groupName != null) {
           match.add(this._groupName, identifier);
         }
         
         return true;
       }
     }
     return false;
   }
 }


