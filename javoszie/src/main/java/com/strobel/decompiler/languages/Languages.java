 package com.strobel.decompiler.languages;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.decompiler.languages.java.JavaLanguage;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Languages
 {
   private static final List<Language> ALL_LANGUAGES;
   private static final List<Language> DEBUG_LANGUAGES;
   private static final JavaLanguage JAVA;
   private static final Language BYTECODE_AST_UNOPTIMIZED;
   private static final Language BYTECODE_AST;
   private static final Language BYTECODE;
   
   static
   {
     List<BytecodeAstLanguage> bytecodeAstLanguages = BytecodeAstLanguage.getDebugLanguages();
     
     JAVA = new JavaLanguage();
     BYTECODE = new BytecodeLanguage();
     BYTECODE_AST_UNOPTIMIZED = (Language)bytecodeAstLanguages.get(0);
     BYTECODE_AST = new BytecodeAstLanguage();
     
     Language[] languages = new Language[bytecodeAstLanguages.size()];
     
     for (int i = 0; i < languages.length; i++) {
       languages[i] = ((Language)bytecodeAstLanguages.get(i));
     }
     
     ALL_LANGUAGES = ArrayUtilities.asUnmodifiableList(new Language[] { JAVA, BYTECODE_AST, BYTECODE_AST_UNOPTIMIZED });
     DEBUG_LANGUAGES = ArrayUtilities.asUnmodifiableList(languages);
   }
   
   public static List<Language> all() {
     return ALL_LANGUAGES;
   }
   
   public static List<Language> debug() {
     return DEBUG_LANGUAGES;
   }
   
   public static JavaLanguage java() {
     return JAVA;
   }
   
   public static Language bytecode() {
     return BYTECODE;
   }
   
   public static Language bytecodeAst() {
     return BYTECODE_AST;
   }
   
   public static Language bytecodeAstUnoptimized() {
     return BYTECODE_AST_UNOPTIMIZED;
   }
 }


