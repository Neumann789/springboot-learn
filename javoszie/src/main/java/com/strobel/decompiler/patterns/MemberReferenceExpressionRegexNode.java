 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import java.util.regex.Matcher;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MemberReferenceExpressionRegexNode
   extends Pattern
 {
   private final String _groupName;
   private final INode _target;
   private final java.util.regex.Pattern _pattern;
   
   public MemberReferenceExpressionRegexNode(INode target, String pattern)
   {
     this._groupName = null;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._pattern = java.util.regex.Pattern.compile((String)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public MemberReferenceExpressionRegexNode(INode target, java.util.regex.Pattern pattern) {
     this._groupName = null;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._pattern = ((java.util.regex.Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public MemberReferenceExpressionRegexNode(String groupName, INode target, String pattern) {
     this._groupName = groupName;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._pattern = java.util.regex.Pattern.compile((String)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public MemberReferenceExpressionRegexNode(String groupName, INode target, java.util.regex.Pattern pattern) {
     this._groupName = groupName;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._pattern = ((java.util.regex.Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof MemberReferenceExpression)) {
       MemberReferenceExpression reference = (MemberReferenceExpression)other;
       
       if ((this._target.matches(reference.getTarget(), match)) && (this._pattern.matcher(reference.getMemberName()).matches()))
       {
 
         match.add(this._groupName, reference);
         return true;
       }
     }
     
     return false;
   }
 }


