 package com.strobel.decompiler.languages.java;
 
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.PackageReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.LineNumberPosition;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.Comment;
 import com.strobel.decompiler.languages.java.ast.CommentType;
 import com.strobel.decompiler.languages.java.ast.EntityDeclaration;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.ImportDeclaration;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LabelStatement;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Stack;
 
 public class TextOutputFormatter implements IOutputFormatter
 {
   private final ITextOutput output;
   private final Stack<AstNode> nodeStack = new Stack();
   private int braceLevelWithinType = -1;
   private boolean inDocumentationComment = false;
   
   private boolean firstUsingDeclaration;
   
   private boolean lastUsingDeclaration;
   private LineNumberMode lineNumberMode;
   
   public static enum LineNumberMode
   {
     WITH_DEBUG_LINE_NUMBERS, 
     WITHOUT_DEBUG_LINE_NUMBERS;
     
     private LineNumberMode() {} }
   
   private int lastObservedLineNumber = -100;
   
 
   private OffsetToLineNumberConverter offset2LineNumber = OffsetToLineNumberConverter.NOOP_CONVERTER;
   
 
   private final List<LineNumberPosition> lineNumberPositions = new ArrayList();
   
   private final Stack<TextLocation> startLocations = new Stack();
   
   public TextOutputFormatter(ITextOutput output, LineNumberMode lineNumberMode)
   {
     this.output = ((ITextOutput)VerifyArgument.notNull(output, "output"));
     this.lineNumberMode = lineNumberMode;
   }
   
   public void startNode(AstNode node)
   {
     if (this.nodeStack.isEmpty()) {
       if (isImportDeclaration(node)) {
         this.firstUsingDeclaration = (!isImportDeclaration(node.getPreviousSibling()));
         this.lastUsingDeclaration = (!isImportDeclaration(node.getNextSibling()));
       }
       else {
         this.firstUsingDeclaration = false;
         this.lastUsingDeclaration = false;
       }
     }
     
     this.nodeStack.push(node);
     
 
     int offset = -34;
     String prefix = null;
     if ((node instanceof Expression)) {
       offset = ((Expression)node).getOffset();
       prefix = "/*EL:";
     } else if ((node instanceof Statement)) {
       offset = ((Statement)node).getOffset();
       prefix = "/*SL:";
     }
     if (offset != -34)
     {
       int lineNumber = this.offset2LineNumber.getLineForOffset(offset);
       if (lineNumber > this.lastObservedLineNumber)
       {
         int lineOfComment = this.output.getRow();
         int columnOfComment = this.output.getColumn();
         LineNumberPosition pos = new LineNumberPosition(lineNumber, lineOfComment, columnOfComment);
         this.lineNumberPositions.add(pos);
         this.lastObservedLineNumber = lineNumber;
         if (this.lineNumberMode == LineNumberMode.WITH_DEBUG_LINE_NUMBERS)
         {
           String commentStr = prefix + lineNumber + "*/";
           this.output.writeComment(commentStr);
         }
       }
     }
     
     this.startLocations.push(new TextLocation(this.output.getRow(), this.output.getColumn()));
     
     if (((node instanceof EntityDeclaration)) && (node.getUserData(Keys.MEMBER_REFERENCE) != null) && (((Identifier)node.getChildByRole(Roles.IDENTIFIER)).isNull()))
     {
 
 
       this.output.writeDefinition("", node.getUserData(Keys.MEMBER_REFERENCE), false);
     }
   }
   
   public void endNode(AstNode node)
   {
     if (this.nodeStack.pop() != node) {
       throw new IllegalStateException();
     }
     
     this.startLocations.pop();
   }
   
   public void writeLabel(String label)
   {
     this.output.writeLabel(label);
   }
   
 
 
   public void writeIdentifier(String identifier)
   {
     Object reference = getCurrentLocalReference();
     
     if (reference != null) {
       this.output.writeReference(identifier, reference, true);
       return;
     }
     
     reference = getCurrentMemberReference();
     
     if (reference != null) {
       this.output.writeReference(identifier, reference);
       return;
     }
     
     reference = getCurrentTypeReference();
     
     if (reference != null) {
       this.output.writeReference(identifier, reference);
       return;
     }
     
     reference = getCurrentPackageReference();
     
     if (reference != null) {
       this.output.writeReference(identifier, reference);
       return;
     }
     
     Object definition = getCurrentDefinition();
     
     if (definition != null) {
       this.output.writeDefinition(identifier, definition, false);
       return;
     }
     
     definition = getCurrentLocalDefinition();
     
     if (definition != null) {
       this.output.writeDefinition(identifier, definition);
       return;
     }
     
     if (this.firstUsingDeclaration) {
       this.output.markFoldStart("", true);
       this.firstUsingDeclaration = false;
     }
     
     this.output.write(identifier);
   }
   
   public void writeKeyword(String keyword)
   {
     this.output.writeKeyword(keyword);
   }
   
   public void writeOperator(String token)
   {
     this.output.writeOperator(token);
   }
   
   public void writeDelimiter(String token)
   {
     this.output.writeDelimiter(token);
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public void writeToken(String token)
   {
     this.output.write(token);
   }
   
   public void writeLiteral(String value)
   {
     this.output.writeLiteral(value);
   }
   
   public void writeTextLiteral(String value)
   {
     this.output.writeTextLiteral(value);
   }
   
   public void space()
   {
     this.output.write(' ');
   }
   
   public void openBrace(BraceStyle style)
   {
     if ((this.braceLevelWithinType >= 0) || ((this.nodeStack.peek() instanceof TypeDeclaration))) {
       this.braceLevelWithinType += 1;
     }
     
     int blockDepth = 0;
     
     for (AstNode node : this.nodeStack) {
       if ((node instanceof BlockStatement)) {
         blockDepth++;
       }
     }
     
     if (blockDepth <= 1) {
       this.output.markFoldStart("", this.braceLevelWithinType == 1);
     }
     
     switch (style) {
     case EndOfLine: 
     case EndOfLineWithoutSpace: 
       break;
     case NextLine: 
       this.output.writeLine();
       break;
     case NextLineShifted: 
       this.output.writeLine();
       this.output.indent();
       break;
     case NextLineShifted2: 
       this.output.writeLine();
       this.output.indent();
       this.output.indent();
       break;
     }
     
     
 
     this.output.writeDelimiter("{");
     
     if (style != BraceStyle.BannerStyle) {
       this.output.writeLine();
     }
     
     this.output.indent();
   }
   
   public void closeBrace(BraceStyle style)
   {
     this.output.unindent();
     this.output.writeDelimiter("}");
     
     switch (style) {
     case NextLineShifted: 
       this.output.unindent();
       break;
     case NextLineShifted2: 
       this.output.unindent();
       this.output.unindent();
     }
     
     
     int blockDepth = 0;
     
     for (AstNode node : this.nodeStack) {
       if ((node instanceof BlockStatement)) {
         blockDepth++;
       }
     }
     
     if (blockDepth <= 1) {
       this.output.markFoldEnd();
     }
     
     if (this.braceLevelWithinType >= 0) {
       this.braceLevelWithinType -= 1;
     }
   }
   
   public void indent()
   {
     this.output.indent();
   }
   
   public void unindent()
   {
     this.output.unindent();
   }
   
   public void newLine()
   {
     if (this.lastUsingDeclaration) {
       this.output.markFoldEnd();
       this.lastUsingDeclaration = false;
     }
     this.output.writeLine();
   }
   
   public void writeComment(CommentType commentType, String content)
   {
     switch (commentType) {
     case SingleLine: 
       this.output.writeComment("//");
       this.output.writeComment(content);
       this.output.writeLine();
       break;
     
 
     case MultiLine: 
       this.output.writeComment("/*");
       this.output.writeComment(content);
       this.output.writeComment("*/");
       break;
     
 
     case Documentation: 
       boolean isFirstLine = !(((AstNode)this.nodeStack.peek()).getPreviousSibling() instanceof Comment);
       boolean isLastLine = !(((AstNode)this.nodeStack.peek()).getNextSibling() instanceof Comment);
       
       if ((!this.inDocumentationComment) && (isFirstLine)) {
         this.inDocumentationComment = true;
         
         String foldedContent = content.replace("\r|\n", " ").trim();
         
         if (foldedContent.length() > 80) {
           foldedContent = foldedContent.substring(0, 80) + " (...)";
         }
         else if (!isLastLine) {
           foldedContent = foldedContent + " (...)";
         }
         
         this.output.markFoldStart("/** " + foldedContent + " */", true);
         this.output.writeComment("/**");
         this.output.writeLine();
       }
       
       this.output.writeComment(" * ");
       this.output.writeComment(content);
       this.output.writeLine();
       
       if ((this.inDocumentationComment) && (isLastLine)) {
         this.inDocumentationComment = false;
         this.output.writeComment(" */");
         this.output.markFoldEnd();
         this.output.writeLine();
       }
       
 
 
       break;
     default: 
       this.output.write(content);
     }
     
   }
   
   private Object getCurrentDefinition()
   {
     if (this.nodeStack.isEmpty()) {
       return null;
     }
     
 
 
     AstNode node = (AstNode)this.nodeStack.peek();
     
     if (isDefinition(node)) {
       Object definition = node.getUserData(Keys.TYPE_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
       
       definition = node.getUserData(Keys.METHOD_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
       
       definition = node.getUserData(Keys.FIELD_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
     }
     
     if (node.getRole() == Roles.IDENTIFIER) {
       AstNode parent = node.getParent();
       
       if (parent == null) {
         return null;
       }
       
       if ((parent instanceof VariableInitializer)) {
         parent = parent.getParent();
       }
       
       Object definition = parent.getUserData(Keys.TYPE_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
       
       definition = parent.getUserData(Keys.METHOD_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
       
       definition = parent.getUserData(Keys.FIELD_DEFINITION);
       
       if (definition != null) {
         return definition;
       }
     }
     
     return null;
   }
   
   private MemberReference getCurrentTypeReference() {
     AstNode node = (AstNode)this.nodeStack.peek();
     TypeReference typeReference = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
     
     if (typeReference != null) {
       return typeReference;
     }
     
     if ((node instanceof Identifier)) {
       AstNode parent = node.getParent();
       
       if (((parent instanceof com.strobel.decompiler.languages.java.ast.AstType)) || ((parent instanceof com.strobel.decompiler.languages.java.ast.TypeParameterDeclaration)) || ((parent instanceof ImportDeclaration)))
       {
 
 
         return (MemberReference)parent.getUserData(Keys.TYPE_REFERENCE);
       }
     }
     
     return null;
   }
   
   private PackageReference getCurrentPackageReference() {
     AstNode node = (AstNode)this.nodeStack.peek();
     
     PackageReference pkg = (PackageReference)node.getUserData(Keys.PACKAGE_REFERENCE);
     
     if ((pkg == null) && ((node.getParent() instanceof ImportDeclaration)))
     {
 
       pkg = (PackageReference)node.getParent().getUserData(Keys.PACKAGE_REFERENCE);
     }
     
     return pkg;
   }
   
   private MemberReference getCurrentMemberReference() {
     AstNode node = (AstNode)this.nodeStack.peek();
     
     MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     if ((member == null) && (node.getRole() == Roles.TARGET_EXPRESSION) && (((node.getParent() instanceof InvocationExpression)) || ((node.getParent() instanceof com.strobel.decompiler.languages.java.ast.ObjectCreationExpression))))
     {
 
 
       member = (MemberReference)node.getParent().getUserData(Keys.MEMBER_REFERENCE);
     }
     
     return member;
   }
   
   private Object getCurrentLocalReference() {
     AstNode node = (AstNode)this.nodeStack.peek();
     
     Variable variable = (Variable)node.getUserData(Keys.VARIABLE);
     
     if ((variable == null) && ((node instanceof Identifier)) && (node.getParent() != null)) {
       variable = (Variable)node.getParent().getUserData(Keys.VARIABLE);
     }
     
     if (variable != null) {
       if (variable.isParameter()) {
         return variable.getOriginalParameter();
       }
       return variable.getOriginalVariable();
     }
     
     return null;
   }
   
   private Object getCurrentLocalDefinition() {
     AstNode node = (AstNode)this.nodeStack.peek();
     
     if (((node instanceof Identifier)) && (node.getParent() != null)) {
       node = node.getParent();
     }
     
     ParameterDefinition parameter = (ParameterDefinition)node.getUserData(Keys.PARAMETER_DEFINITION);
     
     if (parameter != null) {
       return parameter;
     }
     
     if (((node instanceof VariableInitializer)) || ((node instanceof CatchClause))) {
       Variable variable = (Variable)node.getUserData(Keys.VARIABLE);
       
       if ((variable == null) && ((node.getParent() instanceof VariableDeclarationStatement))) {
         variable = (Variable)node.getParent().getUserData(Keys.VARIABLE);
       }
       
       if (variable != null) {
         if (variable.getOriginalParameter() != null) {
           return variable.getOriginalParameter();
         }
         
         return variable.getOriginalVariable();
       }
     }
     
     if ((node instanceof LabelStatement)) {
       LabelStatement label = (LabelStatement)node;
       
       for (int i = this.nodeStack.size() - 1; i >= 0; i--) {
         AstNode n = (AstNode)this.nodeStack.get(i);
         MemberReference methodReference = (MemberReference)n.getUserData(Keys.MEMBER_REFERENCE);
         
         if ((methodReference instanceof MethodReference)) {
           return methodReference + label.getLabel();
         }
       }
     }
     
     return null;
   }
   
   private static boolean isDefinition(AstNode node) {
     return node instanceof EntityDeclaration;
   }
   
   private boolean isImportDeclaration(AstNode node) {
     return node instanceof ImportDeclaration;
   }
   
 
 
   public void resetLineNumberOffsets(OffsetToLineNumberConverter offset2LineNumber)
   {
     this.lastObservedLineNumber = -100;
     this.offset2LineNumber = offset2LineNumber;
   }
   
 
 
   public List<LineNumberPosition> getLineNumberPositions()
   {
     return this.lineNumberPositions;
   }
 }


