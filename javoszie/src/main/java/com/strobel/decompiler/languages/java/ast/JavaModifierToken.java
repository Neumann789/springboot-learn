 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JavaModifierToken
   extends JavaTokenNode
 {
   private static final List<Modifier> ALL_MODIFIERS = ArrayUtilities.asUnmodifiableList(Modifier.values());
   private Modifier _modifier;
   
   public static List<Modifier> allModifiers() { return ALL_MODIFIERS; }
   
 
 
   public JavaModifierToken(Modifier modifier)
   {
     this(TextLocation.EMPTY, modifier);
   }
   
   public JavaModifierToken(TextLocation startLocation, Modifier modifier) {
     super(startLocation);
     this._modifier = modifier;
   }
   
   public final Modifier getModifier() {
     return this._modifier;
   }
   
   public final void setModifier(Modifier modifier) {
     verifyNotFrozen();
     this._modifier = modifier;
   }
   
   public static String getModifierName(Modifier modifier) {
     return String.valueOf(modifier);
   }
   
   public String getText(JavaFormattingOptions options)
   {
     return getModifierName(this._modifier);
   }
   
   protected int getTokenLength()
   {
     return getModifierName(this._modifier).length();
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof JavaModifierToken)) && (((JavaModifierToken)other)._modifier == this._modifier);
   }
 }


