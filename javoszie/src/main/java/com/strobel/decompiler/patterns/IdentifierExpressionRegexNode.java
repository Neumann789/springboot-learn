 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import java.util.regex.Matcher;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class IdentifierExpressionRegexNode
   extends Pattern
 {
   private final String _groupName;
   private final java.util.regex.Pattern _pattern;
   
   public IdentifierExpressionRegexNode(String pattern)
   {
     this._groupName = null;
     this._pattern = java.util.regex.Pattern.compile((String)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public IdentifierExpressionRegexNode(java.util.regex.Pattern pattern) {
     this._groupName = null;
     this._pattern = ((java.util.regex.Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public IdentifierExpressionRegexNode(String groupName, String pattern) {
     this._groupName = groupName;
     this._pattern = java.util.regex.Pattern.compile((String)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public IdentifierExpressionRegexNode(String groupName, java.util.regex.Pattern pattern) {
     this._groupName = groupName;
     this._pattern = ((java.util.regex.Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof IdentifierExpression)) {
       IdentifierExpression identifier = (IdentifierExpression)other;
       
       if (this._pattern.matcher(identifier.getIdentifier()).matches()) {
         match.add(this._groupName, identifier);
         return true;
       }
     }
     
     return false;
   }
 }


