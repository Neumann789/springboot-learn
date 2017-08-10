 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class CommentStatement
   extends Statement
 {
   private final String _comment;
   
   CommentStatement(String comment)
   {
     super(-34);
     this._comment = comment;
   }
   
   final String getComment() {
     return this._comment;
   }
   
   public static void replaceAll(AstNode tree) {
     for (AstNode node : tree.getDescendants()) {
       if ((node instanceof CommentStatement)) {
         node.getParent().insertChildBefore(node, new Comment(((CommentStatement)node).getComment()), Roles.COMMENT);
         
 
 
 
         node.remove();
       }
     }
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return null;
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof CommentStatement)) && (matchString(this._comment, ((CommentStatement)other)._comment));
   }
 }


