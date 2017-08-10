 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import java.util.ArrayDeque;
 
 public class AssignmentChain extends Pattern
 {
   private final INode _valuePattern;
   private final INode _targetPattern;
   
   public AssignmentChain(INode targetPattern, INode valuePattern)
   {
     this._targetPattern = ((INode)VerifyArgument.notNull(targetPattern, "targetPattern"));
     this._valuePattern = ((INode)VerifyArgument.notNull(valuePattern, "valuePattern"));
   }
   
   public final INode getTargetPattern() {
     return this._targetPattern;
   }
   
   public final INode getValuePattern() {
     return this._valuePattern;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof AssignmentExpression)) {
       ArrayDeque<AssignmentExpression> assignments = new ArrayDeque();
       
       INode current = other;
       
       int checkPoint = match.getCheckPoint();
       
       while (((current instanceof AssignmentExpression)) && (((AssignmentExpression)current).getOperator() == AssignmentOperatorType.ASSIGN))
       {
 
         AssignmentExpression assignment = (AssignmentExpression)current;
         Expression target = assignment.getLeft();
         
         if (!this._targetPattern.matches(target, match)) {
           assignments.clear();
           match.restoreCheckPoint(checkPoint);
           break;
         }
         
         assignments.addLast(assignment);
         current = assignment.getRight();
       }
       
       if ((assignments.isEmpty()) || (!this._valuePattern.matches(((AssignmentExpression)assignments.getLast()).getRight(), match))) {
         match.restoreCheckPoint(checkPoint);
         return false;
       }
       
       return true;
     }
     
     return false;
   }
 }


