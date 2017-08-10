 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class UnixNewLine
   extends NewLineNode
 {
   public NewLineType getNewLineType()
   {
     return NewLineType.UNIX;
   }
   
 
   public UnixNewLine() {}
   
   public UnixNewLine(TextLocation startLocation)
   {
     super(startLocation);
   }
   
   public boolean matches(INode other, Match match)
   {
     return other instanceof UnixNewLine;
   }
 }


