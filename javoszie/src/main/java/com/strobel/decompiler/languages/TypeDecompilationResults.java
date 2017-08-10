 package com.strobel.decompiler.languages;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypeDecompilationResults
 {
   private final List<LineNumberPosition> _lineNumberPositions;
   
   public TypeDecompilationResults(@Nullable List<LineNumberPosition> lineNumberPositions)
   {
     this._lineNumberPositions = lineNumberPositions;
   }
   
 
 
 
 
 
 
   @NotNull
   public List<LineNumberPosition> getLineNumberPositions()
   {
     if (this._lineNumberPositions == null) {
       return Collections.emptyList();
     }
     return Collections.unmodifiableList(this._lineNumberPositions);
   }
 }


