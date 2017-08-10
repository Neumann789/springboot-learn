 package com.strobel.assembler.metadata.signatures;
 
 import java.io.PrintStream;
 import java.lang.reflect.GenericSignatureFormatError;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SignatureParser
 {
   private static final boolean DEBUG = Boolean.getBoolean("DEBUG");
   private static final TypeArgument[] EMPTY_TYPE_ARGUMENTS = new TypeArgument[0];
   
   private static final char EOI = ':';
   private char[] input;
   private int index = 0;
   
 
 
   public static SignatureParser make()
   {
     return new SignatureParser();
   }
   
   private char current() {
     assert (this.index <= this.input.length);
     try {
       return this.input[this.index];
     }
     catch (ArrayIndexOutOfBoundsException e) {}
     return ':';
   }
   
   private void advance()
   {
     assert (this.index <= this.input.length);
     this.index += 1;
   }
   
   private Error error(String errorMsg) {
     if (DEBUG) {
       System.out.println("Parse error:" + errorMsg);
     }
     return new GenericSignatureFormatError();
   }
   
   public ClassSignature parseClassSignature(String s) {
     if (DEBUG) {
       System.out.println("Parsing class sig:" + s);
     }
     this.input = s.toCharArray();
     this.index = 0;
     return parseClassSignature();
   }
   
   public MethodTypeSignature parseMethodSignature(String s) {
     if (DEBUG) {
       System.out.println("Parsing method sig:" + s);
     }
     this.input = s.toCharArray();
     this.index = 0;
     return parseMethodTypeSignature();
   }
   
   public TypeSignature parseTypeSignature(String s) {
     if (DEBUG) {
       System.out.println("Parsing type sig:" + s);
     }
     this.input = s.toCharArray();
     this.index = 0;
     return parseTypeSignature();
   }
   
   private ClassSignature parseClassSignature() {
     assert (this.index == 0);
     
     return ClassSignature.make(parseZeroOrMoreFormalTypeParameters(), parseClassTypeSignature(), parseSuperInterfaces());
   }
   
 
 
 
   private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters()
   {
     if (current() == '<') {
       return parseFormalTypeParameters();
     }
     
     return new FormalTypeParameter[0];
   }
   
   private FormalTypeParameter[] parseFormalTypeParameters()
   {
     Collection<FormalTypeParameter> ftps = new ArrayList(3);
     
     assert (current() == '<');
     
     if (current() != '<') {
       throw error("expected <");
     }
     
     advance();
     ftps.add(parseFormalTypeParameter());
     
     while (current() != '>') {
       ftps.add(parseFormalTypeParameter());
     }
     
     advance();
     
     FormalTypeParameter[] formalTypeParameters = new FormalTypeParameter[ftps.size()];
     
     return (FormalTypeParameter[])ftps.toArray(formalTypeParameters);
   }
   
   private FormalTypeParameter parseFormalTypeParameter() {
     return FormalTypeParameter.make(parseIdentifier(), parseZeroOrMoreBounds());
   }
   
 
 
   private String parseIdentifier()
   {
     StringBuilder result = new StringBuilder();
     while (!Character.isWhitespace(current())) {
       char c = current();
       switch (c) {
       case '.': 
       case '/': 
       case ':': 
       case ';': 
       case '<': 
       case '>': 
         return result.toString();
       }
       result.append(c);
       advance();
     }
     
 
     return result.toString();
   }
   
   private FieldTypeSignature parseFieldTypeSignature() {
     switch (current()) {
     case 'L': 
       return parseClassTypeSignature();
     case 'T': 
       return parseTypeVariableSignature();
     case '[': 
       return parseArrayTypeSignature();
     }
     throw error("Expected Field Type Signature");
   }
   
   private ClassTypeSignature parseClassTypeSignature()
   {
     assert (current() == 'L');
     
     if (current() != 'L') {
       throw error("expected a class type");
     }
     
     advance();
     
     List<SimpleClassTypeSignature> typeSignatures = new ArrayList(5);
     
     typeSignatures.add(parseSimpleClassTypeSignature(false));
     parseClassTypeSignatureSuffix(typeSignatures);
     
     if (current() != ';') {
       throw error("expected ';' got '" + current() + "'");
     }
     
     advance();
     
     return ClassTypeSignature.make(typeSignatures);
   }
   
   private SimpleClassTypeSignature parseSimpleClassTypeSignature(boolean dollar) {
     String id = parseIdentifier();
     int position = this.index;
     char c = current();
     
     switch (c) {
     case '$': 
     case '.': 
     case '/': 
     case ';': 
       return SimpleClassTypeSignature.make(id, dollar, new TypeArgument[0]);
     
 
     case '<': 
       return SimpleClassTypeSignature.make(id, dollar, parseTypeArguments());
     }
     
     
     throw error(position + ": expected < or ; or /");
   }
   
 
   private void parseClassTypeSignatureSuffix(List<SimpleClassTypeSignature> typeSignatures)
   {
     while ((current() == '/') || (current() == '.')) {
       boolean dollar = current() == '.';
       advance();
       typeSignatures.add(parseSimpleClassTypeSignature(dollar));
     }
   }
   
   private TypeArgument[] parseTypeArguments() {
     Collection<TypeArgument> tas = new ArrayList(3);
     assert (current() == '<');
     if (current() != '<') {
       throw error("expected <");
     }
     advance();
     tas.add(parseTypeArgument());
     while (current() != '>')
     {
       tas.add(parseTypeArgument());
     }
     advance();
     TypeArgument[] taa = new TypeArgument[tas.size()];
     return (TypeArgument[])tas.toArray(taa);
   }
   
   private TypeArgument parseTypeArgument() {
     char c = current();
     
     switch (c) {
     case '+': 
       advance();
       return Wildcard.make(BottomSignature.make(), parseFieldTypeSignature());
     
     case '*': 
       advance();
       return Wildcard.make(BottomSignature.make(), SimpleClassTypeSignature.make("java.lang.Object", false, EMPTY_TYPE_ARGUMENTS));
     
 
 
 
     case '-': 
       advance();
       return Wildcard.make(parseFieldTypeSignature(), SimpleClassTypeSignature.make("java.lang.Object", false, EMPTY_TYPE_ARGUMENTS));
     }
     
     
 
 
     return parseFieldTypeSignature();
   }
   
 
 
 
   private TypeVariableSignature parseTypeVariableSignature()
   {
     assert (current() == 'T');
     if (current() != 'T') {
       throw error("expected a type variable usage");
     }
     advance();
     TypeVariableSignature ts = TypeVariableSignature.make(parseIdentifier());
     
     if (current() != ';') {
       throw error("; expected in signature of type variable named" + ts.getName());
     }
     
 
 
     advance();
     return ts;
   }
   
 
   private ArrayTypeSignature parseArrayTypeSignature()
   {
     if (current() != '[') {
       throw error("expected array type signature");
     }
     advance();
     return ArrayTypeSignature.make(parseTypeSignature());
   }
   
 
   private TypeSignature parseTypeSignature()
   {
     switch (current()) {
     case 'B': 
     case 'C': 
     case 'D': 
     case 'F': 
     case 'I': 
     case 'J': 
     case 'S': 
     case 'V': 
     case 'Z': 
       return parseBaseType();
     }
     return parseFieldTypeSignature();
   }
   
   private BaseType parseBaseType()
   {
     switch (current()) {
     case 'B': 
       advance();
       return ByteSignature.make();
     case 'C': 
       advance();
       return CharSignature.make();
     case 'D': 
       advance();
       return DoubleSignature.make();
     case 'F': 
       advance();
       return FloatSignature.make();
     case 'I': 
       advance();
       return IntSignature.make();
     case 'J': 
       advance();
       return LongSignature.make();
     case 'S': 
       advance();
       return ShortSignature.make();
     case 'Z': 
       advance();
       return BooleanSignature.make();
     case 'V': 
       advance();
       return VoidSignature.make();
     }
     throw error("expected primitive type");
   }
   
 
   private FieldTypeSignature[] parseZeroOrMoreBounds()
   {
     List<FieldTypeSignature> fts = new ArrayList(3);
     
     if (current() == ':') {
       advance();
       switch (current()) {
       case ':': 
         fts.add(BottomSignature.make());
         break;
       
       default: 
         fts.add(parseFieldTypeSignature());
       }
       
       
 
       while (current() == ':') {
         advance();
         fts.add(parseFieldTypeSignature());
       }
     }
     
     return (FieldTypeSignature[])fts.toArray(new FieldTypeSignature[fts.size()]);
   }
   
   private ClassTypeSignature[] parseSuperInterfaces() {
     Collection<ClassTypeSignature> cts = new ArrayList(5);
     
     while (current() == 'L') {
       cts.add(parseClassTypeSignature());
     }
     ClassTypeSignature[] cta = new ClassTypeSignature[cts.size()];
     return (ClassTypeSignature[])cts.toArray(cta);
   }
   
   private MethodTypeSignature parseMethodTypeSignature()
   {
     assert (this.index == 0);
     
     return MethodTypeSignature.make(parseZeroOrMoreFormalTypeParameters(), parseFormalParameters(), parseReturnType(), parseZeroOrMoreThrowsSignatures());
   }
   
 
 
 
 
 
   private TypeSignature[] parseFormalParameters()
   {
     if (current() != '(') {
       throw error("expected (");
     }
     advance();
     TypeSignature[] pts = parseZeroOrMoreTypeSignatures();
     if (current() != ')') {
       throw error("expected )");
     }
     advance();
     return pts;
   }
   
   private TypeSignature[] parseZeroOrMoreTypeSignatures()
   {
     Collection<TypeSignature> ts = new ArrayList();
     boolean stop = false;
     while (!stop) {
       switch (current()) {
       case 'B': 
       case 'C': 
       case 'D': 
       case 'F': 
       case 'I': 
       case 'J': 
       case 'L': 
       case 'S': 
       case 'T': 
       case 'Z': 
       case '[': 
         ts.add(parseTypeSignature());
         break;
       case 'E': case 'G': case 'H': case 'K': case 'M': case 'N': case 'O': case 'P': 
       case 'Q': case 'R': case 'U': case 'V': case 'W': case 'X': case 'Y': default: 
         stop = true;
       }
       
     }
     
 
 
 
     TypeSignature[] ta = new TypeSignature[ts.size()];
     return (TypeSignature[])ts.toArray(ta);
   }
   
 
   private ReturnType parseReturnType()
   {
     if (current() == 'V') {
       advance();
       return VoidSignature.make();
     }
     
     return parseTypeSignature();
   }
   
 
   private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures()
   {
     Collection<FieldTypeSignature> ets = new ArrayList(3);
     
     while (current() == '^') {
       ets.add(parseThrowsSignature());
     }
     FieldTypeSignature[] eta = new FieldTypeSignature[ets.size()];
     return (FieldTypeSignature[])ets.toArray(eta);
   }
   
 
   private FieldTypeSignature parseThrowsSignature()
   {
     assert (current() == '^');
     if (current() != '^') {
       throw error("expected throws signature");
     }
     advance();
     return parseFieldTypeSignature();
   }
 }


