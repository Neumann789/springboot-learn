 package com.strobel.decompiler.languages.java;
 
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.languages.java.ast.Annotation;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.ArrayCreationExpression;
 import com.strobel.decompiler.languages.java.ast.ArraySpecifier;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CaseLabel;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.DoWhileStatement;
 import com.strobel.decompiler.languages.java.ast.EntityDeclaration;
 import com.strobel.decompiler.languages.java.ast.EnumValueDeclaration;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.ForEachStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.JavaModifierToken;
 import com.strobel.decompiler.languages.java.ast.LambdaExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.TokenRole;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.TypeParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.WhileStatement;
 import com.strobel.decompiler.patterns.Repeat;
 import com.strobel.decompiler.patterns.Role;
 import java.util.Stack;
 
 public final class JavaOutputVisitor implements com.strobel.decompiler.languages.java.ast.IAstVisitor<Void, Void>
 {
   final TextOutputFormatter formatter;
   final DecompilerSettings settings;
   final JavaFormattingOptions policy;
   final Stack<AstNode> containerStack = new Stack();
   final Stack<AstNode> positionStack = new Stack();
   final ITextOutput output;
   private LastWritten lastWritten;
   
   public JavaOutputVisitor(ITextOutput output, DecompilerSettings settings)
   {
     this.output = output;
     this.settings = ((DecompilerSettings)com.strobel.core.VerifyArgument.notNull(settings, "settings"));
     this.formatter = new TextOutputFormatter(output, settings.getShowDebugLineNumbers() ? TextOutputFormatter.LineNumberMode.WITH_DEBUG_LINE_NUMBERS : TextOutputFormatter.LineNumberMode.WITHOUT_DEBUG_LINE_NUMBERS);
     
 
 
 
     JavaFormattingOptions formattingOptions = settings.getFormattingOptions();
     
     this.policy = (formattingOptions != null ? formattingOptions : JavaFormattingOptions.createDefault());
   }
   
   public java.util.List<com.strobel.decompiler.languages.LineNumberPosition> getLineNumberPositions()
   {
     return this.formatter.getLineNumberPositions();
   }
   
 
 
 
   void startNode(AstNode node)
   {
     assert ((this.containerStack.isEmpty()) || (node.getParent() == this.containerStack.peek()) || (((AstNode)this.containerStack.peek()).getNodeType() == com.strobel.decompiler.languages.java.ast.NodeType.PATTERN));
     
 
 
     if (this.positionStack.size() > 0) {
       writeSpecialsUpToNode(node);
     }
     this.containerStack.push(node);
     this.positionStack.push(node.getFirstChild());
     this.formatter.startNode(node);
   }
   
   void endNode(AstNode node) {
     assert (node == this.containerStack.peek());
     
     AstNode position = (AstNode)this.positionStack.pop();
     
     assert ((position == null) || (position.getParent() == node));
     
     writeSpecials(position, null);
     this.containerStack.pop();
     this.formatter.endNode(node);
   }
   
 
 
 
   private void writeSpecials(AstNode start, AstNode end)
   {
     for (AstNode current = start; current != end; current = current.getNextSibling()) {
       if ((current.getRole() == Roles.COMMENT) || (current.getRole() == Roles.NEW_LINE)) {
         current.acceptVisitor(this, null);
       }
     }
   }
   
   private void writeSpecialsUpToRole(Role<?> role) {
     writeSpecialsUpToRole(role, null);
   }
   
   private void writeSpecialsUpToRole(Role<?> role, AstNode nextNode) {
     if (this.positionStack.isEmpty()) {
       return;
     }
     
     for (AstNode current = (AstNode)this.positionStack.peek(); 
         (current != null) && (current != nextNode); 
         current = current.getNextSibling())
     {
       if (current.getRole() == role) {
         writeSpecials((AstNode)this.positionStack.pop(), current);
         
 
 
 
         this.positionStack.push(current.getNextSibling());
         
 
 
 
         break;
       }
     }
   }
   
   private void writeSpecialsUpToNode(AstNode node) {
     if (this.positionStack.isEmpty()) {
       return;
     }
     
     for (AstNode current = (AstNode)this.positionStack.peek(); current != null; current = current.getNextSibling()) {
       if (current == node) {
         writeSpecials((AstNode)this.positionStack.pop(), current);
         
 
 
 
         this.positionStack.push(current.getNextSibling());
         
 
 
 
         break;
       }
     }
   }
   
 
 
 
   void leftParenthesis()
   {
     writeToken(Roles.LEFT_PARENTHESIS);
   }
   
   void rightParenthesis() {
     writeToken(Roles.RIGHT_PARENTHESIS);
   }
   
   void space() {
     this.formatter.space();
     this.lastWritten = LastWritten.Whitespace;
   }
   
   void space(boolean addSpace) {
     if (addSpace) {
       space();
     }
   }
   
   void newLine() {
     this.formatter.newLine();
     this.lastWritten = LastWritten.Whitespace;
   }
   
   void openBrace(BraceStyle style) {
     writeSpecialsUpToRole(Roles.LEFT_BRACE);
     
     space(((style == BraceStyle.EndOfLine) || (style == BraceStyle.BannerStyle)) && (this.lastWritten != LastWritten.Whitespace) && (this.lastWritten != LastWritten.LeftParenthesis));
     
 
 
 
     this.formatter.openBrace(style);
     
     this.lastWritten = (style == BraceStyle.BannerStyle ? LastWritten.Other : LastWritten.Whitespace);
   }
   
   void closeBrace(BraceStyle style)
   {
     writeSpecialsUpToRole(Roles.RIGHT_BRACE);
     this.formatter.closeBrace(style);
     this.lastWritten = LastWritten.Other;
   }
   
   void writeIdentifier(String identifier) {
     writeIdentifier(identifier, null);
   }
   
   void writeIdentifier(String identifier, Role<Identifier> identifierRole) {
     writeSpecialsUpToRole(identifierRole != null ? identifierRole : Roles.IDENTIFIER);
     
     if (isKeyword(identifier, (AstNode)this.containerStack.peek())) {
       if (this.lastWritten == LastWritten.KeywordOrIdentifier) {
         space();
       }
       
 
     }
     else if (this.lastWritten == LastWritten.KeywordOrIdentifier) {
       this.formatter.space();
     }
     
 
     if (identifierRole == Roles.LABEL) {
       this.formatter.writeLabel(identifier);
     }
     else {
       this.formatter.writeIdentifier(identifier);
     }
     
     this.lastWritten = LastWritten.KeywordOrIdentifier;
   }
   
   void writeToken(TokenRole tokenRole) {
     writeToken(tokenRole.getToken(), tokenRole);
   }
   
   void writeToken(String token, Role role) {
     writeSpecialsUpToRole(role);
     
     if (((this.lastWritten == LastWritten.Plus) && (token.charAt(0) == '+')) || ((this.lastWritten == LastWritten.Minus) && (token.charAt(0) == '-')) || ((this.lastWritten == LastWritten.Ampersand) && (token.charAt(0) == '&')) || ((this.lastWritten == LastWritten.QuestionMark) && (token.charAt(0) == '?')) || ((this.lastWritten == LastWritten.Division) && (token.charAt(0) == '*')))
     {
 
 
 
 
       this.formatter.space();
     }
     
     if ((role instanceof TokenRole)) {
       ??? = (TokenRole)role;
       
       if (???.isKeyword()) {
         this.formatter.writeKeyword(token);
         this.lastWritten = LastWritten.KeywordOrIdentifier;
         return;
       }
       if (???.isOperator()) {
         this.formatter.writeOperator(token);
         this.lastWritten = LastWritten.Operator;
         return;
       }
       if (???.isDelimiter()) {
         this.formatter.writeDelimiter(token);
         this.lastWritten = ("(".equals(token) ? LastWritten.LeftParenthesis : LastWritten.Delimiter);
         return;
       }
     }
     
     this.formatter.writeToken(token);
     
     switch (token) {
     case "+": 
       this.lastWritten = LastWritten.Plus;
       break;
     case "-": 
       this.lastWritten = LastWritten.Minus;
       break;
     case "&": 
       this.lastWritten = LastWritten.Ampersand;
       break;
     case "?": 
       this.lastWritten = LastWritten.QuestionMark;
       break;
     case "/": 
       this.lastWritten = LastWritten.Division;
       break;
     case "(": 
       this.lastWritten = LastWritten.LeftParenthesis;
       break;
     default: 
       this.lastWritten = LastWritten.Other;
     }
     
   }
   
 
 
 
   void comma(AstNode nextNode)
   {
     comma(nextNode, false);
   }
   
   void comma(AstNode nextNode, boolean noSpaceAfterComma) {
     writeSpecialsUpToRole(Roles.COMMA, nextNode);
     space(this.policy.SpaceBeforeBracketComma);
     
     this.formatter.writeDelimiter(",");
     this.lastWritten = LastWritten.Other;
     space((!noSpaceAfterComma) && (this.policy.SpaceAfterBracketComma));
   }
   
   void optionalComma()
   {
     AstNode position = (AstNode)this.positionStack.peek();
     
     while ((position != null) && (position.getNodeType() == com.strobel.decompiler.languages.java.ast.NodeType.WHITESPACE)) {
       position = position.getNextSibling();
     }
     
     if ((position != null) && (position.getRole() == Roles.COMMA)) {
       comma(null, true);
     }
   }
   
   void semicolon() {
     Role role = ((AstNode)this.containerStack.peek()).getRole();
     if ((role != ForStatement.INITIALIZER_ROLE) && (role != ForStatement.ITERATOR_ROLE)) {
       writeToken(Roles.SEMICOLON);
       newLine();
     }
   }
   
   private void optionalSemicolon()
   {
     AstNode pos = (AstNode)this.positionStack.peek();
     while ((pos != null) && (pos.getNodeType() == com.strobel.decompiler.languages.java.ast.NodeType.WHITESPACE)) {
       pos = pos.getNextSibling();
     }
     if ((pos != null) && (pos.getRole() == Roles.SEMICOLON)) {
       semicolon();
     }
   }
   
   private void writeCommaSeparatedList(Iterable<? extends AstNode> list) {
     boolean isFirst = true;
     for (AstNode node : list) {
       if (isFirst) {
         isFirst = false;
       }
       else {
         comma(node);
       }
       node.acceptVisitor(this, null);
     }
   }
   
   private void writePipeSeparatedList(Iterable<? extends AstNode> list) {
     boolean isFirst = true;
     for (AstNode node : list) {
       if (isFirst) {
         isFirst = false;
       }
       else {
         space();
         writeToken(Roles.PIPE);
         space();
       }
       node.acceptVisitor(this, null);
     }
   }
   
 
 
   private void writeCommaSeparatedListInParenthesis(Iterable<? extends AstNode> list, boolean spaceWithin)
   {
     leftParenthesis();
     if (CollectionUtilities.any(list)) {
       space(spaceWithin);
       writeCommaSeparatedList(list);
       space(spaceWithin);
     }
     rightParenthesis();
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private void writeTypeArguments(Iterable<AstType> typeArguments)
   {
     if (CollectionUtilities.any(typeArguments)) {
       writeToken(Roles.LEFT_CHEVRON);
       writeCommaSeparatedList(typeArguments);
       writeToken(Roles.RIGHT_CHEVRON);
     }
   }
   
   public void writeTypeParameters(Iterable<TypeParameterDeclaration> typeParameters) {
     if (CollectionUtilities.any(typeParameters)) {
       writeToken(Roles.LEFT_CHEVRON);
       writeCommaSeparatedList(typeParameters);
       writeToken(Roles.RIGHT_CHEVRON);
     }
   }
   
   private void writeModifiers(Iterable<JavaModifierToken> modifierTokens) {
     for (JavaModifierToken modifier : modifierTokens) {
       modifier.acceptVisitor(this, null);
     }
   }
   
   private void writeQualifiedIdentifier(Iterable<Identifier> identifiers) {
     boolean first = true;
     
     for (Identifier identifier : identifiers) {
       if (first) {
         first = false;
         if (this.lastWritten == LastWritten.KeywordOrIdentifier) {
           this.formatter.space();
         }
       }
       else {
         writeSpecialsUpToRole(Roles.DOT, identifier);
         this.formatter.writeToken(".");
         this.lastWritten = LastWritten.Other;
       }
       
       writeSpecialsUpToNode(identifier);
       
       this.formatter.writeIdentifier(identifier.getName());
       this.lastWritten = LastWritten.KeywordOrIdentifier;
     }
   }
   
   void writeEmbeddedStatement(Statement embeddedStatement) {
     if (embeddedStatement.isNull()) {
       newLine();
       return;
     }
     
     if ((embeddedStatement instanceof BlockStatement)) {
       visitBlockStatement((BlockStatement)embeddedStatement, null);
     }
     else {
       newLine();
       this.formatter.indent();
       embeddedStatement.acceptVisitor(this, null);
       this.formatter.unindent();
     }
   }
   
   void writeMethodBody(AstNodeCollection<TypeDeclaration> declaredTypes, BlockStatement body) {
     if (body.isNull()) {
       semicolon();
       return;
     }
     
     startNode(body);
     
 
 
     AstNode parent = body.getParent();
     BraceEnforcement braceEnforcement;
     BraceStyle style; BraceEnforcement braceEnforcement; if ((parent instanceof ConstructorDeclaration)) {
       BraceStyle style = this.policy.ConstructorBraceStyle;
       braceEnforcement = BraceEnforcement.AddBraces;
     } else { BraceEnforcement braceEnforcement;
       if ((parent instanceof MethodDeclaration)) {
         BraceStyle style = this.policy.MethodBraceStyle;
         braceEnforcement = BraceEnforcement.AddBraces;
 
 
       }
       else
       {
 
         style = this.policy.StatementBraceStyle;
         BraceEnforcement braceEnforcement;
         if ((parent instanceof IfElseStatement)) {
           braceEnforcement = this.policy.IfElseBraceEnforcement;
         } else { BraceEnforcement braceEnforcement;
           if ((parent instanceof WhileStatement)) {
             braceEnforcement = this.policy.WhileBraceEnforcement;
           }
           else {
             braceEnforcement = BraceEnforcement.AddBraces;
           }
         }
       }
     }
     AstNodeCollection<Statement> statements = body.getStatements();
     boolean addBraces;
     switch (braceEnforcement) {
     case RemoveBraces: 
       addBraces = false;
       break;
     default: 
       addBraces = true;
     }
     
     
     if (addBraces) {
       openBrace(style);
     }
     
     boolean needNewLine = false;
     
     if ((declaredTypes != null) && (!declaredTypes.isEmpty())) {
       for (TypeDeclaration declaredType : declaredTypes) {
         if (needNewLine) {
           newLine();
         }
         
         declaredType.acceptVisitor(new JavaOutputVisitor(this.output, this.settings), null);
         needNewLine = true;
       }
     }
     
     if (needNewLine) {
       newLine();
     }
     
     for (AstNode statement : statements) {
       statement.acceptVisitor(this, null);
     }
     
     if (addBraces) {
       closeBrace(style);
     }
     
     if (!(parent instanceof Expression)) {
       newLine();
     }
     
     endNode(body);
   }
   
   void writeAnnotations(Iterable<Annotation> annotations, boolean newLineAfter) {
     for (Annotation annotation : annotations) {
       annotation.acceptVisitor(this, null);
       
       if (newLineAfter) {
         newLine();
       }
       else {
         space();
       }
     }
   }
   
   void writePrivateImplementationType(AstType privateImplementationType) {
     if (!privateImplementationType.isNull()) {
       privateImplementationType.acceptVisitor(this, null);
       writeToken(Roles.DOT);
     }
   }
   
 
 
 
   void writeKeyword(TokenRole tokenRole)
   {
     writeKeyword(tokenRole.getToken(), tokenRole);
   }
   
   void writeKeyword(String token) {
     writeKeyword(token, null);
   }
   
   void writeKeyword(String token, Role tokenRole) {
     if (tokenRole != null) {
       writeSpecialsUpToRole(tokenRole);
     }
     
     if (this.lastWritten == LastWritten.KeywordOrIdentifier) {
       this.formatter.space();
     }
     
     this.formatter.writeKeyword(token);
     this.lastWritten = LastWritten.KeywordOrIdentifier;
   }
   
 
 
 
   void visitNodeInPattern(com.strobel.decompiler.patterns.INode childNode)
   {
     if ((childNode instanceof AstNode)) {
       ((AstNode)childNode).acceptVisitor(this, null);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.IdentifierExpressionBackReference)) {
       visitIdentifierExpressionBackReference((com.strobel.decompiler.patterns.IdentifierExpressionBackReference)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.Choice)) {
       visitChoice((com.strobel.decompiler.patterns.Choice)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.AnyNode)) {
       visitAnyNode((com.strobel.decompiler.patterns.AnyNode)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.BackReference)) {
       visitBackReference((com.strobel.decompiler.patterns.BackReference)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.NamedNode)) {
       visitNamedNode((com.strobel.decompiler.patterns.NamedNode)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.OptionalNode)) {
       visitOptionalNode((com.strobel.decompiler.patterns.OptionalNode)childNode);
     }
     else if ((childNode instanceof Repeat)) {
       visitRepeat((Repeat)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.MemberReferenceTypeNode)) {
       visitMemberReferenceTypeNode((com.strobel.decompiler.patterns.MemberReferenceTypeNode)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.TypedNode)) {
       visitTypedNode((com.strobel.decompiler.patterns.TypedNode)childNode);
     }
     else if ((childNode instanceof com.strobel.decompiler.patterns.ParameterReferenceNode)) {
       visitParameterReferenceNode((com.strobel.decompiler.patterns.ParameterReferenceNode)childNode);
     }
     else {
       writePrimitiveValue(childNode);
     }
   }
   
   private void visitTypedNode(com.strobel.decompiler.patterns.TypedNode node) {
     writeKeyword("anyOf");
     leftParenthesis();
     writeIdentifier(node.getNodeType().getSimpleName());
     rightParenthesis();
   }
   
   private void visitParameterReferenceNode(com.strobel.decompiler.patterns.ParameterReferenceNode node) {
     writeKeyword("parameterAt");
     leftParenthesis();
     writePrimitiveValue(Integer.valueOf(node.getParameterPosition()));
     rightParenthesis();
   }
   
   private void visitIdentifierExpressionBackReference(com.strobel.decompiler.patterns.IdentifierExpressionBackReference node) {
     writeKeyword("identifierBackReference");
     leftParenthesis();
     writeIdentifier(node.getReferencedGroupName());
     rightParenthesis();
   }
   
   private void visitChoice(com.strobel.decompiler.patterns.Choice choice) {
     writeKeyword("choice");
     space();
     leftParenthesis();
     newLine();
     this.formatter.indent();
     
     com.strobel.decompiler.patterns.INode last = (com.strobel.decompiler.patterns.INode)CollectionUtilities.lastOrDefault(choice);
     
     for (com.strobel.decompiler.patterns.INode alternative : choice) {
       visitNodeInPattern(alternative);
       if (alternative != last) {
         writeToken(Roles.COMMA);
       }
       newLine();
     }
     
     this.formatter.unindent();
     rightParenthesis();
   }
   
   private void visitMemberReferenceTypeNode(com.strobel.decompiler.patterns.MemberReferenceTypeNode node) {
     writeKeyword("memberReference");
     writeToken(Roles.LEFT_BRACKET);
     writeIdentifier(node.getReferenceType().getSimpleName());
     writeToken(Roles.RIGHT_BRACKET);
     leftParenthesis();
     visitNodeInPattern(node.getTarget());
     rightParenthesis();
   }
   
   private void visitAnyNode(com.strobel.decompiler.patterns.AnyNode anyNode) {
     if (!StringUtilities.isNullOrEmpty(anyNode.getGroupName())) {
       writeIdentifier(anyNode.getGroupName());
       writeToken(Roles.COLON);
       writeIdentifier("*");
     }
   }
   
   private void visitBackReference(com.strobel.decompiler.patterns.BackReference backReference) {
     writeKeyword("backReference");
     leftParenthesis();
     writeIdentifier(backReference.getReferencedGroupName());
     rightParenthesis();
   }
   
   private void visitNamedNode(com.strobel.decompiler.patterns.NamedNode namedNode) {
     if (!StringUtilities.isNullOrEmpty(namedNode.getGroupName())) {
       writeIdentifier(namedNode.getGroupName());
       writeToken(Roles.COLON);
     }
     visitNodeInPattern(namedNode.getNode());
   }
   
   private void visitOptionalNode(com.strobel.decompiler.patterns.OptionalNode optionalNode) {
     writeKeyword("optional");
     leftParenthesis();
     visitNodeInPattern(optionalNode.getNode());
     rightParenthesis();
   }
   
   private void visitRepeat(Repeat repeat) {
     writeKeyword("repeat");
     leftParenthesis();
     
     if ((repeat.getMinCount() != 0) || (repeat.getMaxCount() != Integer.MAX_VALUE)) {
       writeIdentifier(String.valueOf(repeat.getMinCount()));
       writeToken(Roles.COMMA);
       writeIdentifier(String.valueOf(repeat.getMaxCount()));
       writeToken(Roles.COMMA);
     }
     
     visitNodeInPattern(repeat.getNode());
     rightParenthesis();
   }
   
 
 
   public Void visitComment(com.strobel.decompiler.languages.java.ast.Comment comment, Void ignored)
   {
     if (this.lastWritten == LastWritten.Division) {
       this.formatter.space();
     }
     
     this.formatter.startNode(comment);
     this.formatter.writeComment(comment.getCommentType(), comment.getContent());
     this.formatter.endNode(comment);
     this.lastWritten = LastWritten.Whitespace;
     
     return null;
   }
   
   public Void visitPatternPlaceholder(AstNode node, com.strobel.decompiler.patterns.Pattern pattern, Void ignored)
   {
     startNode(node);
     visitNodeInPattern(pattern);
     endNode(node);
     return null;
   }
   
   public Void visitInvocationExpression(com.strobel.decompiler.languages.java.ast.InvocationExpression node, Void ignored)
   {
     startNode(node);
     node.getTarget().acceptVisitor(this, null);
     space(this.policy.SpaceBeforeMethodCallParentheses);
     writeCommaSeparatedListInParenthesis(node.getArguments(), this.policy.SpaceWithinMethodCallParentheses);
     endNode(node);
     return null;
   }
   
   public Void visitTypeReference(com.strobel.decompiler.languages.java.ast.TypeReferenceExpression node, Void ignored)
   {
     startNode(node);
     node.getType().acceptVisitor(this, null);
     endNode(node);
     return null;
   }
   
   public Void visitJavaTokenNode(com.strobel.decompiler.languages.java.ast.JavaTokenNode node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     if ((node instanceof JavaModifierToken)) {
       JavaModifierToken modifierToken = (JavaModifierToken)node;
       startNode(modifierToken);
       writeKeyword(JavaModifierToken.getModifierName(modifierToken.getModifier()));
       endNode(modifierToken);
     }
     else {
       throw com.strobel.util.ContractUtils.unsupported();
     }
     return null;
   }
   
   public Void visitMemberReferenceExpression(com.strobel.decompiler.languages.java.ast.MemberReferenceExpression node, Void ignored)
   {
     startNode(node);
     
     Expression target = node.getTarget();
     
     if (!target.isNull()) {
       target.acceptVisitor(this, null);
       writeToken(Roles.DOT);
     }
     
     writeTypeArguments(node.getTypeArguments());
     writeIdentifier(node.getMemberName());
     endNode(node);
     return null;
   }
   
   public Void visitIdentifier(Identifier node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     startNode(node);
     writeIdentifier(node.getName());
     endNode(node);
     return null;
   }
   
   public Void visitNullReferenceExpression(com.strobel.decompiler.languages.java.ast.NullReferenceExpression node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     startNode(node);
     writeKeyword("null", node.getRole());
     endNode(node);
     return null;
   }
   
   public Void visitThisReferenceExpression(com.strobel.decompiler.languages.java.ast.ThisReferenceExpression node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     startNode(node);
     
     Expression target = node.getTarget();
     
     if ((target != null) && (!target.isNull())) {
       target.acceptVisitor(this, ignored);
       writeToken(Roles.DOT);
     }
     
     writeKeyword("this", node.getRole());
     endNode(node);
     return null;
   }
   
   public Void visitSuperReferenceExpression(com.strobel.decompiler.languages.java.ast.SuperReferenceExpression node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     startNode(node);
     
     Expression target = node.getTarget();
     
     if ((target != null) && (!target.isNull())) {
       target.acceptVisitor(this, ignored);
       writeToken(Roles.DOT);
     }
     
     writeKeyword("super", node.getRole());
     endNode(node);
     return null;
   }
   
   public Void visitClassOfExpression(com.strobel.decompiler.languages.java.ast.ClassOfExpression node, Void ignored)
   {
     startNode(node);
     node.getType().acceptVisitor(this, ignored);
     writeToken(Roles.DOT);
     writeKeyword("class", node.getRole());
     endNode(node);
     return null;
   }
   
   public Void visitBlockStatement(BlockStatement node, Void ignored)
   {
     startNode(node);
     
 
 
     AstNode parent = node.getParent();
     Iterable<AstNode> children = node.getChildren();
     BraceEnforcement braceEnforcement;
     BraceStyle style; BraceEnforcement braceEnforcement; if ((parent instanceof ConstructorDeclaration)) {
       BraceStyle style = this.policy.ConstructorBraceStyle;
       braceEnforcement = BraceEnforcement.AddBraces;
     } else { BraceEnforcement braceEnforcement;
       if ((parent instanceof MethodDeclaration)) {
         BraceStyle style = this.policy.MethodBraceStyle;
         braceEnforcement = BraceEnforcement.AddBraces;
       } else {
         BraceEnforcement braceEnforcement;
         if ((this.policy.StatementBraceStyle == BraceStyle.EndOfLine) && (!CollectionUtilities.any(children))) {
           BraceStyle style = BraceStyle.BannerStyle;
           braceEnforcement = BraceEnforcement.AddBraces;
         }
         else {
           style = this.policy.StatementBraceStyle;
           BraceEnforcement braceEnforcement;
           if ((parent instanceof IfElseStatement)) {
             braceEnforcement = this.policy.IfElseBraceEnforcement;
           } else { BraceEnforcement braceEnforcement;
             if ((parent instanceof WhileStatement)) {
               braceEnforcement = this.policy.WhileBraceEnforcement;
             }
             else {
               braceEnforcement = BraceEnforcement.AddBraces;
             }
           }
         }
       }
     }
     boolean addBraces;
     switch (braceEnforcement) {
     case RemoveBraces: 
       addBraces = false;
       break;
     default: 
       addBraces = true;
     }
     
     
     if (addBraces) {
       openBrace(style);
     }
     
     for (AstNode child : children) {
       if (((child instanceof Statement)) || ((child instanceof TypeDeclaration))) {
         child.acceptVisitor(this, null);
       }
     }
     
     if (addBraces) {
       closeBrace(style);
     }
     
     if ((!(parent instanceof Expression)) && (!(parent instanceof DoWhileStatement))) {
       newLine();
     }
     
     endNode(node);
     
     return null;
   }
   
   public Void visitExpressionStatement(com.strobel.decompiler.languages.java.ast.ExpressionStatement node, Void ignored)
   {
     startNode(node);
     node.getExpression().acceptVisitor(this, null);
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitBreakStatement(com.strobel.decompiler.languages.java.ast.BreakStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword("break");
     
     String label = node.getLabel();
     
     if (!StringUtilities.isNullOrEmpty(label)) {
       writeIdentifier(label, Roles.LABEL);
     }
     
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitContinueStatement(com.strobel.decompiler.languages.java.ast.ContinueStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword("continue");
     
     String label = node.getLabel();
     
     if (!StringUtilities.isNullOrEmpty(label)) {
       writeIdentifier(label, Roles.LABEL);
     }
     
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitDoWhileStatement(DoWhileStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(DoWhileStatement.DO_KEYWORD_ROLE);
     writeEmbeddedStatement(node.getEmbeddedStatement());
     space(this.lastWritten != LastWritten.Whitespace);
     writeKeyword(DoWhileStatement.WHILE_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeWhileParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinWhileParentheses);
     node.getCondition().acceptVisitor(this, null);
     space(this.policy.SpacesWithinWhileParentheses);
     rightParenthesis();
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitEmptyStatement(com.strobel.decompiler.languages.java.ast.EmptyStatement node, Void ignored)
   {
     startNode(node);
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitIfElseStatement(IfElseStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(IfElseStatement.IF_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeIfParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinIfParentheses);
     node.getCondition().acceptVisitor(this, null);
     space(this.policy.SpacesWithinIfParentheses);
     rightParenthesis();
     writeEmbeddedStatement(node.getTrueStatement());
     
     Statement falseStatement = node.getFalseStatement();
     
     if (!falseStatement.isNull()) {
       writeKeyword(IfElseStatement.ELSE_KEYWORD_ROLE);
       
       if ((falseStatement instanceof IfElseStatement)) {
         falseStatement.acceptVisitor(this, ignored);
       }
       else {
         writeEmbeddedStatement(falseStatement);
       }
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitLabelStatement(com.strobel.decompiler.languages.java.ast.LabelStatement node, Void ignored)
   {
     startNode(node);
     writeIdentifier(node.getLabel(), Roles.LABEL);
     writeToken(Roles.COLON);
     
     boolean foundLabelledStatement = false;
     
     for (AstNode sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
       if (sibling.getRole() == node.getRole()) {
         foundLabelledStatement = true;
       }
     }
     
     if (!foundLabelledStatement)
     {
       writeToken(Roles.SEMICOLON);
     }
     
     newLine();
     endNode(node);
     return null;
   }
   
   public Void visitLabeledStatement(com.strobel.decompiler.languages.java.ast.LabeledStatement node, Void ignored)
   {
     boolean isLoop = AstNode.isLoop(node.getStatement());
     
     startNode(node);
     
     if (isLoop) {
       this.formatter.unindent();
     }
     
     writeIdentifier(node.getLabel(), Roles.LABEL);
     writeToken(Roles.COLON);
     
     if (isLoop) {
       this.formatter.indent();
       newLine();
     }
     
     node.getStatement().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitReturnStatement(com.strobel.decompiler.languages.java.ast.ReturnStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.ReturnStatement.RETURN_KEYWORD_ROLE);
     
     if (!node.getExpression().isNull()) {
       space();
       node.getExpression().acceptVisitor(this, null);
     }
     
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitSwitchStatement(com.strobel.decompiler.languages.java.ast.SwitchStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.SwitchStatement.SWITCH_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeSwitchParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinSwitchParentheses);
     node.getExpression().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinSwitchParentheses);
     rightParenthesis();
     openBrace(this.policy.StatementBraceStyle);
     
     if (this.policy.IndentSwitchBody) {
       this.formatter.indent();
     }
     
     for (SwitchSection section : node.getSwitchSections()) {
       section.acceptVisitor(this, ignored);
     }
     
     if (this.policy.IndentSwitchBody) {
       this.formatter.unindent();
     }
     
     closeBrace(this.policy.StatementBraceStyle);
     newLine();
     endNode(node);
     
     return null;
   }
   
   public Void visitSwitchSection(SwitchSection node, Void ignored)
   {
     startNode(node);
     
     boolean first = true;
     
     for (CaseLabel label : node.getCaseLabels()) {
       if (!first) {
         newLine();
       }
       label.acceptVisitor(this, ignored);
       first = false;
     }
     
     boolean isBlock = (node.getStatements().size() == 1) && ((CollectionUtilities.firstOrDefault(node.getStatements()) instanceof BlockStatement));
     
 
     if ((this.policy.IndentCaseBody) && (!isBlock)) {
       this.formatter.indent();
     }
     
     if (!isBlock) {
       newLine();
     }
     
     for (Statement statement : node.getStatements()) {
       statement.acceptVisitor(this, ignored);
     }
     
     if ((this.policy.IndentCaseBody) && (!isBlock)) {
       this.formatter.unindent();
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitCaseLabel(CaseLabel node, Void ignored)
   {
     startNode(node);
     
     if (node.getExpression().isNull()) {
       writeKeyword(CaseLabel.DEFAULT_KEYWORD_ROLE);
     }
     else {
       writeKeyword(CaseLabel.CASE_KEYWORD_ROLE);
       space();
       node.getExpression().acceptVisitor(this, ignored);
     }
     
     writeToken(Roles.COLON);
     endNode(node);
     return null;
   }
   
   public Void visitThrowStatement(com.strobel.decompiler.languages.java.ast.ThrowStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.ThrowStatement.THROW_KEYWORD_ROLE);
     
     if (!node.getExpression().isNull()) {
       space();
       node.getExpression().acceptVisitor(this, ignored);
     }
     
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitCatchClause(CatchClause node, Void ignored)
   {
     startNode(node);
     writeKeyword(CatchClause.CATCH_KEYWORD_ROLE);
     
     if (!node.getExceptionTypes().isEmpty()) {
       space(this.policy.SpaceBeforeCatchParentheses);
       leftParenthesis();
       space(this.policy.SpacesWithinCatchParentheses);
       writePipeSeparatedList(node.getExceptionTypes());
       
       if (!StringUtilities.isNullOrEmpty(node.getVariableName())) {
         space();
         node.getVariableNameToken().acceptVisitor(this, ignored);
       }
       
       space(this.policy.SpacesWithinCatchParentheses);
       rightParenthesis();
     }
     
     node.getBody().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitAnnotation(Annotation node, Void ignored)
   {
     startNode(node);
     
     startNode(node.getType());
     this.formatter.writeIdentifier("@");
     endNode(node.getType());
     
     node.getType().acceptVisitor(this, ignored);
     
     AstNodeCollection<Expression> arguments = node.getArguments();
     
     if (!arguments.isEmpty()) {
       writeCommaSeparatedListInParenthesis(arguments, false);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitNewLine(com.strobel.decompiler.languages.java.ast.NewLineNode node, Void ignored)
   {
     this.formatter.startNode(node);
     this.formatter.newLine();
     this.formatter.endNode(node);
     return null;
   }
   
   public Void visitVariableDeclaration(VariableDeclarationStatement node, Void ignored)
   {
     return writeVariableDeclaration(node, true);
   }
   
   private Void writeVariableDeclaration(VariableDeclarationStatement node, boolean semicolon) {
     startNode(node);
     writeModifiers(node.getChildrenByRole(VariableDeclarationStatement.MODIFIER_ROLE));
     node.getType().acceptVisitor(this, null);
     space();
     writeCommaSeparatedList(node.getVariables());
     if (semicolon) {
       semicolon();
     }
     endNode(node);
     return null;
   }
   
   public Void visitVariableInitializer(com.strobel.decompiler.languages.java.ast.VariableInitializer node, Void ignored)
   {
     startNode(node);
     node.getNameToken().acceptVisitor(this, ignored);
     
     if (!node.getInitializer().isNull()) {
       space(this.policy.SpaceAroundAssignment);
       writeToken(Roles.ASSIGN);
       space(this.policy.SpaceAroundAssignment);
       node.getInitializer().acceptVisitor(this, ignored);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitText(com.strobel.decompiler.languages.java.ast.TextNode node, Void ignored)
   {
     return null;
   }
   
   public Void visitImportDeclaration(com.strobel.decompiler.languages.java.ast.ImportDeclaration node, Void ignored)
   {
     startNode(node);
     
     writeKeyword(com.strobel.decompiler.languages.java.ast.ImportDeclaration.IMPORT_KEYWORD_RULE);
     node.getImportIdentifier().acceptVisitor(this, ignored);
     semicolon();
     endNode(node);
     
     if (!(node.getNextSibling() instanceof com.strobel.decompiler.languages.java.ast.ImportDeclaration)) {
       newLine();
     }
     
     return null;
   }
   
   public Void visitSimpleType(com.strobel.decompiler.languages.java.ast.SimpleType node, Void ignored)
   {
     startNode(node);
     
     TypeReference typeReference = (TypeReference)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.TYPE_REFERENCE);
     
     if ((typeReference != null) && (typeReference.isPrimitive())) {
       writeKeyword(typeReference.getSimpleName());
     }
     else {
       writeIdentifier(node.getIdentifier());
     }
     
     writeTypeArguments(node.getTypeArguments());
     endNode(node);
     return null;
   }
   
   public Void visitMethodDeclaration(MethodDeclaration node, Void ignored)
   {
     startNode(node);
     this.formatter.resetLineNumberOffsets(OffsetToLineNumberConverter.NOOP_CONVERTER);
     writeAnnotations(node.getAnnotations(), true);
     
     MethodDefinition definition = (MethodDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.METHOD_DEFINITION);
     
     if ((definition != null) && (definition.isDefault())) {
       writeKeyword(Roles.DEFAULT_KEYWORD);
     }
     
     writeModifiers(node.getModifiers());
     
     if ((definition != null) && ((definition.isSynthetic()) || (definition.isBridgeMethod()))) {
       space(this.lastWritten != LastWritten.Whitespace);
       this.formatter.writeComment(com.strobel.decompiler.languages.java.ast.CommentType.MultiLine, definition.isBridgeMethod() ? " bridge " : " synthetic ");
       
 
 
       space();
     }
     
     if ((definition == null) || (!definition.isTypeInitializer())) {
       com.strobel.assembler.ir.attributes.LineNumberTableAttribute lineNumberTable = (com.strobel.assembler.ir.attributes.LineNumberTableAttribute)com.strobel.assembler.ir.attributes.SourceAttribute.find("LineNumberTable", definition != null ? definition.getSourceAttributes() : java.util.Collections.emptyList());
       
 
 
 
 
       if (lineNumberTable != null) {
         this.formatter.resetLineNumberOffsets(new LineNumberTableConverter(lineNumberTable));
       }
       
       AstNodeCollection<TypeParameterDeclaration> typeParameters = node.getTypeParameters();
       
       if (CollectionUtilities.any(typeParameters)) {
         space();
         writeTypeParameters(typeParameters);
         space();
       }
       
       node.getReturnType().acceptVisitor(this, ignored);
       space();
       writePrivateImplementationType(node.getPrivateImplementationType());
       node.getNameToken().acceptVisitor(this, ignored);
       space(this.policy.SpaceBeforeMethodDeclarationParentheses);
       writeCommaSeparatedListInParenthesis(node.getParameters(), this.policy.SpaceWithinMethodDeclarationParentheses);
     }
     
     AstNodeCollection<AstType> thrownTypes = node.getThrownTypes();
     
     if (!thrownTypes.isEmpty()) {
       space();
       writeKeyword(MethodDeclaration.THROWS_KEYWORD);
       writeCommaSeparatedList(thrownTypes);
     }
     
     Expression defaultValue = node.getDefaultValue();
     
     if ((defaultValue != null) && (!defaultValue.isNull())) {
       space();
       writeKeyword(MethodDeclaration.DEFAULT_KEYWORD);
       space();
       defaultValue.acceptVisitor(this, ignored);
     }
     
     AstNodeCollection<TypeDeclaration> declaredTypes = node.getDeclaredTypes();
     
     writeMethodBody(declaredTypes, node.getBody());
     endNode(node);
     return null;
   }
   
   public Void visitInitializerBlock(com.strobel.decompiler.languages.java.ast.InstanceInitializer node, Void ignored)
   {
     startNode(node);
     writeMethodBody(node.getDeclaredTypes(), node.getBody());
     endNode(node);
     return null;
   }
   
   public Void visitConstructorDeclaration(ConstructorDeclaration node, Void ignored)
   {
     startNode(node);
     writeAnnotations(node.getAnnotations(), true);
     writeModifiers(node.getModifiers());
     
     AstNode parent = node.getParent();
     TypeDeclaration type = (parent instanceof TypeDeclaration) ? (TypeDeclaration)parent : null;
     
     startNode(node.getNameToken());
     writeIdentifier(type != null ? type.getName() : node.getName());
     endNode(node.getNameToken());
     space(this.policy.SpaceBeforeConstructorDeclarationParentheses);
     writeCommaSeparatedListInParenthesis(node.getParameters(), this.policy.SpaceWithinMethodDeclarationParentheses);
     
     AstNodeCollection<AstType> thrownTypes = node.getThrownTypes();
     
     if (!thrownTypes.isEmpty()) {
       space();
       writeKeyword(MethodDeclaration.THROWS_KEYWORD);
       writeCommaSeparatedList(thrownTypes);
     }
     
     writeMethodBody(null, node.getBody());
     endNode(node);
     return null;
   }
   
   public Void visitTypeParameterDeclaration(TypeParameterDeclaration node, Void ignored)
   {
     startNode(node);
     writeAnnotations(node.getAnnotations(), false);
     node.getNameToken().acceptVisitor(this, ignored);
     
     AstType extendsBound = node.getExtendsBound();
     
     if ((extendsBound != null) && (!extendsBound.isNull())) {
       writeKeyword(Roles.EXTENDS_KEYWORD);
       extendsBound.acceptVisitor(this, ignored);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitParameterDeclaration(ParameterDeclaration node, Void ignored)
   {
     boolean hasType = !node.getType().isNull();
     
     startNode(node);
     writeAnnotations(node.getAnnotations(), false);
     
     if (hasType) {
       writeModifiers(node.getModifiers());
       node.getType().acceptVisitor(this, ignored);
       
       if (!StringUtilities.isNullOrEmpty(node.getName())) {
         space();
       }
     }
     
     if (!StringUtilities.isNullOrEmpty(node.getName())) {
       node.getNameToken().acceptVisitor(this, ignored);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitFieldDeclaration(FieldDeclaration node, Void ignored)
   {
     startNode(node);
     writeAnnotations(node.getAnnotations(), true);
     writeModifiers(node.getModifiers());
     
     com.strobel.assembler.metadata.FieldDefinition field = (com.strobel.assembler.metadata.FieldDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.FIELD_DEFINITION);
     
     if ((field != null) && (field.isSynthetic())) {
       space(this.lastWritten != LastWritten.Whitespace);
       this.formatter.writeComment(com.strobel.decompiler.languages.java.ast.CommentType.MultiLine, " synthetic ");
       space();
     }
     
     node.getReturnType().acceptVisitor(this, ignored);
     space();
     writeCommaSeparatedList(node.getVariables());
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitLocalTypeDeclarationStatement(com.strobel.decompiler.languages.java.ast.LocalTypeDeclarationStatement node, Void data)
   {
     startNode(node);
     node.getTypeDeclaration().acceptVisitor(this, data);
     endNode(node);
     return data;
   }
   
   public Void visitTypeDeclaration(TypeDeclaration node, Void ignored)
   {
     startNode(node);
     
     TypeDefinition type = (TypeDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.TYPE_DEFINITION);
     
     boolean isTrulyAnonymous = (type != null) && (type.isAnonymous()) && ((node.getParent() instanceof AnonymousObjectCreationExpression));
     
 
 
     if (!isTrulyAnonymous) {
       writeAnnotations(node.getAnnotations(), true);
       writeModifiers(node.getModifiers());
       
       switch (node.getClassType()) {
       case ENUM: 
         writeKeyword(Roles.ENUM_KEYWORD);
         break;
       case INTERFACE: 
         writeKeyword(Roles.INTERFACE_KEYWORD);
         break;
       case ANNOTATION: 
         writeKeyword(Roles.ANNOTATION_KEYWORD);
         break;
       default: 
         writeKeyword(Roles.CLASS_KEYWORD);
       }
       
       
       node.getNameToken().acceptVisitor(this, ignored);
       writeTypeParameters(node.getTypeParameters());
       
       if (!node.getBaseType().isNull()) {
         space();
         writeKeyword(Roles.EXTENDS_KEYWORD);
         space();
         node.getBaseType().acceptVisitor(this, ignored);
       }
       
       if (CollectionUtilities.any(node.getInterfaces())) {
         java.util.Collection<AstType> interfaceTypes;
         java.util.Collection<AstType> interfaceTypes;
         if (node.getClassType() == com.strobel.decompiler.languages.java.ast.ClassType.ANNOTATION) {
           interfaceTypes = new java.util.ArrayList();
           
           for (AstType t : node.getInterfaces()) {
             TypeReference r = (TypeReference)t.getUserData(com.strobel.decompiler.languages.java.ast.Keys.TYPE_REFERENCE);
             
             if ((r == null) || (!"java/lang/annotation/Annotation".equals(r.getInternalName())))
             {
 
 
               interfaceTypes.add(t);
             }
           }
         } else {
           interfaceTypes = node.getInterfaces();
         }
         
         if (CollectionUtilities.any(interfaceTypes)) {
           space();
           
           if ((node.getClassType() == com.strobel.decompiler.languages.java.ast.ClassType.INTERFACE) || (node.getClassType() == com.strobel.decompiler.languages.java.ast.ClassType.ANNOTATION)) {
             writeKeyword(Roles.EXTENDS_KEYWORD);
           }
           else {
             writeKeyword(Roles.IMPLEMENTS_KEYWORD);
           }
           
           space();
           writeCommaSeparatedList(node.getInterfaces());
         }
       }
     }
     
 
     AstNodeCollection<EntityDeclaration> members = node.getMembers();
     BraceStyle braceStyle;
     BraceStyle braceStyle; switch (node.getClassType()) {
     case ENUM: 
       braceStyle = this.policy.EnumBraceStyle;
       break;
     case INTERFACE: 
       braceStyle = this.policy.InterfaceBraceStyle;
       break;
     case ANNOTATION: 
       braceStyle = this.policy.AnnotationBraceStyle;
       break;
     default: 
       if ((type != null) && (type.isAnonymous())) {
         braceStyle = members.isEmpty() ? BraceStyle.BannerStyle : this.policy.AnonymousClassBraceStyle;
       }
       else {
         braceStyle = this.policy.ClassBraceStyle;
       }
       break;
     }
     
     openBrace(braceStyle);
     
     boolean first = true;
     EntityDeclaration lastMember = null;
     
     for (EntityDeclaration member : members) {
       if (first) {
         first = false;
       }
       else {
         int blankLines;
         int blankLines;
         if (((member instanceof FieldDeclaration)) && ((lastMember instanceof FieldDeclaration))) {
           blankLines = this.policy.BlankLinesBetweenFields;
         }
         else {
           blankLines = this.policy.BlankLinesBetweenMembers;
         }
         
         for (int i = 0; i < blankLines; i++) {
           this.formatter.newLine();
         }
       }
       member.acceptVisitor(this, ignored);
       lastMember = member;
     }
     
     closeBrace(braceStyle);
     
     if ((type == null) || (!type.isAnonymous())) {
       optionalSemicolon();
       newLine();
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitCompilationUnit(com.strobel.decompiler.languages.java.ast.CompilationUnit node, Void ignored)
   {
     for (AstNode child : node.getChildren()) {
       child.acceptVisitor(this, ignored);
     }
     return null;
   }
   
   public Void visitPackageDeclaration(com.strobel.decompiler.languages.java.ast.PackageDeclaration node, Void ignored)
   {
     startNode(node);
     writeKeyword(Roles.PACKAGE_KEYWORD);
     writeQualifiedIdentifier(node.getIdentifiers());
     semicolon();
     newLine();
     
     for (int i = 0; i < this.policy.BlankLinesAfterPackageDeclaration; i++) {
       newLine();
     }
     
     endNode(node);
     
     return null;
   }
   
   public Void visitArraySpecifier(ArraySpecifier node, Void ignored)
   {
     startNode(node);
     writeToken(Roles.LEFT_BRACKET);
     
     for (com.strobel.decompiler.languages.java.ast.JavaTokenNode comma : node.getChildrenByRole(Roles.COMMA)) {
       writeSpecialsUpToNode(comma);
       this.formatter.writeToken(",");
       this.lastWritten = LastWritten.Other;
     }
     
     writeToken(Roles.RIGHT_BRACKET);
     endNode(node);
     return null;
   }
   
   public Void visitComposedType(com.strobel.decompiler.languages.java.ast.ComposedType node, Void ignored)
   {
     startNode(node);
     node.getBaseType().acceptVisitor(this, ignored);
     
     boolean isVarArgs = false;
     
     if ((node.getParent() instanceof ParameterDeclaration)) {
       com.strobel.assembler.metadata.ParameterDefinition parameter = (com.strobel.assembler.metadata.ParameterDefinition)node.getParent().getUserData(com.strobel.decompiler.languages.java.ast.Keys.PARAMETER_DEFINITION);
       
       if ((parameter.getPosition() == parameter.getMethod().getParameters().size() - 1) && (parameter.getParameterType().isArray()) && ((parameter.getMethod() instanceof com.strobel.assembler.metadata.MethodReference)))
       {
 
 
         com.strobel.assembler.metadata.MethodReference method = (com.strobel.assembler.metadata.MethodReference)parameter.getMethod();
         MethodDefinition resolvedMethod = method.resolve();
         
         if ((resolvedMethod != null) && (com.strobel.assembler.metadata.Flags.testAny(resolvedMethod.getFlags(), 17179869312L))) {
           isVarArgs = true;
         }
       }
     }
     
     AstNodeCollection<ArraySpecifier> arraySpecifiers = node.getArraySpecifiers();
     int arraySpecifierCount = arraySpecifiers.size();
     
     int i = 0;
     
     for (ArraySpecifier specifier : arraySpecifiers) {
       if (isVarArgs) { i++; if (i == arraySpecifierCount) {
           writeToken(Roles.VARARGS);
           break label204;
         } }
       specifier.acceptVisitor(this, ignored);
     }
     
     label204:
     endNode(node);
     return null;
   }
   
   public Void visitWhileStatement(WhileStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(WhileStatement.WHILE_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeWhileParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinWhileParentheses);
     node.getCondition().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinWhileParentheses);
     rightParenthesis();
     writeEmbeddedStatement(node.getEmbeddedStatement());
     endNode(node);
     return null;
   }
   
   public Void visitPrimitiveExpression(PrimitiveExpression node, Void ignored)
   {
     node.setStartLocation(new com.strobel.decompiler.languages.TextLocation(this.output.getRow(), this.output.getColumn()));
     startNode(node);
     
     if (!StringUtilities.isNullOrEmpty(node.getLiteralValue())) {
       this.formatter.writeLiteral(node.getLiteralValue());
     }
     else if ((node.getValue() instanceof Number)) {
       long longValue = ((Number)node.getValue()).longValue();
       
       if ((longValue != -1L) && (isBitwiseContext(node.getParent(), node))) {
         this.formatter.writeLiteral(String.format((node.getValue() instanceof Long) ? "0x%1$XL" : "0x%1$X", new Object[] { node.getValue() }));
 
 
       }
       else
       {
 
 
         writePrimitiveValue(node.getValue());
       }
     }
     else {
       writePrimitiveValue(node.getValue());
     }
     
     endNode(node);
     return null;
   }
   
   private boolean isBitwiseContext(AstNode parent, AstNode node) {
     parent = parent != null ? com.strobel.decompiler.languages.java.utilities.TypeUtilities.skipParenthesesUp(parent) : null;
     node = node != null ? com.strobel.decompiler.languages.java.utilities.TypeUtilities.skipParenthesesUp(node) : null;
     
     if (((parent instanceof BinaryOperatorExpression)) || ((parent instanceof AssignmentExpression)))
     {
       com.strobel.decompiler.languages.java.ast.BinaryOperatorType operator;
       
       com.strobel.decompiler.languages.java.ast.BinaryOperatorType operator;
       if ((parent instanceof BinaryOperatorExpression)) {
         operator = ((BinaryOperatorExpression)parent).getOperator();
       }
       else {
         operator = AssignmentExpression.getCorrespondingBinaryOperator(((AssignmentExpression)parent).getOperator());
       }
       
       if (operator == null) {
         return false;
       }
       
       switch (operator) {
       case BITWISE_AND: 
       case BITWISE_OR: 
       case EXCLUSIVE_OR: 
         return true;
       
 
       case EQUALITY: 
       case INEQUALITY: 
         if (node != null) {
           BinaryOperatorExpression binary = (BinaryOperatorExpression)parent;
           AstNode comparand = node == binary.getLeft() ? binary.getRight() : binary.getLeft();
           
           return isBitwiseContext(com.strobel.decompiler.languages.java.utilities.TypeUtilities.skipParenthesesDown(comparand), null);
         }
         break;
       }
       
       return false;
     }
     
 
     if ((parent instanceof com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression)) {
       switch (((com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression)parent).getOperator()) {
       case BITWISE_NOT: 
         return true;
       }
       return false;
     }
     
 
     return false;
   }
   
   void writePrimitiveValue(Object val) {
     if (val == null) {
       writeKeyword("null");
       return;
     }
     
     if ((val instanceof Boolean)) {
       if (((Boolean)val).booleanValue()) {
         writeKeyword("true");
       }
       else {
         writeKeyword("false");
       }
       return;
     }
     
     if ((val instanceof String)) {
       this.formatter.writeTextLiteral(StringUtilities.escape(val.toString(), true, this.settings.isUnicodeOutputEnabled()));
       this.lastWritten = LastWritten.Other;
     }
     else if ((val instanceof Character)) {
       this.formatter.writeTextLiteral(StringUtilities.escape(((Character)val).charValue(), true, this.settings.isUnicodeOutputEnabled()));
       this.lastWritten = LastWritten.Other;
     }
     else if ((val instanceof Float)) {
       float f = ((Float)val).floatValue();
       if ((Float.isInfinite(f)) || (Float.isNaN(f))) {
         writeKeyword("Float");
         writeToken(Roles.DOT);
         if (f == Float.POSITIVE_INFINITY) {
           writeIdentifier("POSITIVE_INFINITY");
         }
         else if (f == Float.NEGATIVE_INFINITY) {
           writeIdentifier("NEGATIVE_INFINITY");
         }
         else {
           writeIdentifier("NaN");
         }
         return;
       }
       this.formatter.writeLiteral(Float.toString(f) + "f");
       this.lastWritten = LastWritten.Other;
     }
     else if ((val instanceof Double)) {
       double d = ((Double)val).doubleValue();
       if ((Double.isInfinite(d)) || (Double.isNaN(d))) {
         writeKeyword("Double");
         writeToken(Roles.DOT);
         if (d == Double.POSITIVE_INFINITY) {
           writeIdentifier("POSITIVE_INFINITY");
         }
         else if (d == Double.NEGATIVE_INFINITY) {
           writeIdentifier("NEGATIVE_INFINITY");
         }
         else {
           writeIdentifier("NaN");
         }
         return;
       }
       
       String number = Double.toString(d);
       
       if ((number.indexOf('.') < 0) && (number.indexOf('E') < 0)) {
         number = number + "d";
       }
       
       this.formatter.writeLiteral(number);
       this.lastWritten = LastWritten.KeywordOrIdentifier;
     }
     else if ((val instanceof Number)) {
       long longValue = ((Number)val).longValue();
       
       boolean writeHex = (longValue == 205452675308461L) || (longValue == -4982089500409860083L);
       
 
       if (!writeHex) {
         long msb = longValue & 0xFFFFFFFF00000000;
         long lsb = longValue & 0xFFFFFFFF;
         
         if (msb == 0L) {
           switch ((int)lsb) {
           case -1414812757: 
           case -1414677826: 
           case -1414673666: 
           case -1159869698: 
           case -1146241297: 
           case -1091581234: 
           case -889275714: 
           case -889270259: 
           case -889262164: 
           case -559039810: 
           case -559038737: 
           case -559038242: 
           case -559026163: 
           case -553727763: 
           case -86057299: 
           case 464367618: 
             writeHex = true;
           }
           
         }
       }
       
       String stringValue = writeHex ? String.format("0x%1$X", new Object[] { Long.valueOf(longValue) }) : String.valueOf(val);
       
 
       this.formatter.writeLiteral((val instanceof Long) ? stringValue + "L" : stringValue);
       this.lastWritten = LastWritten.Other;
     }
     else {
       this.formatter.writeLiteral(String.valueOf(val));
       this.lastWritten = LastWritten.Other;
     }
   }
   
   public Void visitCastExpression(com.strobel.decompiler.languages.java.ast.CastExpression node, Void ignored)
   {
     startNode(node);
     leftParenthesis();
     space(this.policy.SpacesWithinCastParentheses);
     node.getType().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinCastParentheses);
     rightParenthesis();
     space(this.policy.SpaceAfterTypecast);
     node.getExpression().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitBinaryOperatorExpression(BinaryOperatorExpression node, Void ignored)
   {
     startNode(node);
     node.getLeft().acceptVisitor(this, ignored);
     
     boolean spacePolicy;
     
     switch (node.getOperator()) {
     case BITWISE_AND: 
     case BITWISE_OR: 
     case EXCLUSIVE_OR: 
       spacePolicy = this.policy.SpaceAroundBitwiseOperator;
       break;
     case LOGICAL_AND: 
     case LOGICAL_OR: 
       spacePolicy = this.policy.SpaceAroundLogicalOperator;
       break;
     case GREATER_THAN: 
     case GREATER_THAN_OR_EQUAL: 
     case LESS_THAN_OR_EQUAL: 
     case LESS_THAN: 
       spacePolicy = this.policy.SpaceAroundRelationalOperator;
       break;
     case EQUALITY: 
     case INEQUALITY: 
       spacePolicy = this.policy.SpaceAroundEqualityOperator;
       break;
     case ADD: 
     case SUBTRACT: 
       spacePolicy = this.policy.SpaceAroundAdditiveOperator;
       break;
     case MULTIPLY: 
     case DIVIDE: 
     case MODULUS: 
       spacePolicy = this.policy.SpaceAroundMultiplicativeOperator;
       break;
     case SHIFT_LEFT: 
     case SHIFT_RIGHT: 
     case UNSIGNED_SHIFT_RIGHT: 
       spacePolicy = this.policy.SpaceAroundShiftOperator;
       break;
     default: 
       spacePolicy = true;
     }
     
     space(spacePolicy);
     writeToken(BinaryOperatorExpression.getOperatorRole(node.getOperator()));
     space(spacePolicy);
     node.getRight().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitInstanceOfExpression(com.strobel.decompiler.languages.java.ast.InstanceOfExpression node, Void ignored)
   {
     startNode(node);
     node.getExpression().acceptVisitor(this, ignored);
     space();
     writeKeyword(com.strobel.decompiler.languages.java.ast.InstanceOfExpression.INSTANCE_OF_KEYWORD_ROLE);
     node.getType().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitIndexerExpression(com.strobel.decompiler.languages.java.ast.IndexerExpression node, Void ignored)
   {
     startNode(node);
     node.getTarget().acceptVisitor(this, ignored);
     space(this.policy.SpaceBeforeMethodCallParentheses);
     writeToken(Roles.LEFT_BRACKET);
     node.getArgument().acceptVisitor(this, ignored);
     writeToken(Roles.RIGHT_BRACKET);
     endNode(node);
     return null;
   }
   
   public Void visitIdentifierExpression(com.strobel.decompiler.languages.java.ast.IdentifierExpression node, Void ignored)
   {
     startNode(node);
     writeIdentifier(node.getIdentifier());
     writeTypeArguments(node.getTypeArguments());
     endNode(node);
     return null;
   }
   
   public Void visitUnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression node, Void ignored)
   {
     startNode(node);
     
     com.strobel.decompiler.languages.java.ast.UnaryOperatorType operator = node.getOperator();
     TokenRole symbol = com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression.getOperatorRole(operator);
     
     if ((operator != com.strobel.decompiler.languages.java.ast.UnaryOperatorType.POST_INCREMENT) && (operator != com.strobel.decompiler.languages.java.ast.UnaryOperatorType.POST_DECREMENT)) {
       writeToken(symbol);
     }
     
     node.getExpression().acceptVisitor(this, ignored);
     
     if ((operator == com.strobel.decompiler.languages.java.ast.UnaryOperatorType.POST_INCREMENT) || (operator == com.strobel.decompiler.languages.java.ast.UnaryOperatorType.POST_DECREMENT)) {
       writeToken(symbol);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitConditionalExpression(ConditionalExpression node, Void ignored)
   {
     startNode(node);
     node.getCondition().acceptVisitor(this, ignored);
     
     space(this.policy.SpaceBeforeConditionalOperatorCondition);
     writeToken(ConditionalExpression.QUESTION_MARK_ROLE);
     space(this.policy.SpaceAfterConditionalOperatorCondition);
     
     node.getTrueExpression().acceptVisitor(this, ignored);
     
     space(this.policy.SpaceBeforeConditionalOperatorSeparator);
     writeToken(ConditionalExpression.COLON_ROLE);
     space(this.policy.SpaceAfterConditionalOperatorSeparator);
     
     node.getFalseExpression().acceptVisitor(this, ignored);
     
     endNode(node);
     return null;
   }
   
   public Void visitArrayInitializerExpression(com.strobel.decompiler.languages.java.ast.ArrayInitializerExpression node, Void ignored)
   {
     startNode(node);
     writeInitializerElements(node.getElements());
     endNode(node);
     return null;
   }
   
   private void writeInitializerElements(AstNodeCollection<Expression> elements) {
     if (elements.isEmpty()) {
       writeToken(Roles.LEFT_BRACE);
       writeToken(Roles.RIGHT_BRACE);
       return;
     }
     
     boolean wrapElements = this.policy.ArrayInitializerWrapping == Wrapping.WrapAlways;
     BraceStyle style = wrapElements ? BraceStyle.NextLine : BraceStyle.BannerStyle;
     
     openBrace(style);
     
     boolean isFirst = true;
     
     for (AstNode node : elements) {
       if (isFirst) {
         if (style == BraceStyle.BannerStyle) {
           space();
         }
         isFirst = false;
       }
       else {
         comma(node, wrapElements);
         
         if (wrapElements) {
           newLine();
         }
       }
       node.acceptVisitor(this, null);
     }
     
     optionalComma();
     
     if (wrapElements) {
       newLine();
     }
     else if ((!isFirst) && (style == BraceStyle.BannerStyle)) {
       space();
     }
     
     closeBrace(style);
   }
   
   public Void visitObjectCreationExpression(com.strobel.decompiler.languages.java.ast.ObjectCreationExpression node, Void ignored)
   {
     startNode(node);
     
     Expression target = node.getTarget();
     
     if ((target != null) && (!target.isNull())) {
       target.acceptVisitor(this, ignored);
       writeToken(Roles.DOT);
     }
     
     writeKeyword(com.strobel.decompiler.languages.java.ast.ObjectCreationExpression.NEW_KEYWORD_ROLE);
     node.getType().acceptVisitor(this, ignored);
     space(this.policy.SpaceBeforeMethodCallParentheses);
     writeCommaSeparatedListInParenthesis(node.getArguments(), this.policy.SpaceWithinMethodCallParentheses);
     endNode(node);
     return null;
   }
   
   public Void visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void ignored)
   {
     startNode(node);
     
     Expression target = node.getTarget();
     
     if ((target != null) && (!target.isNull())) {
       target.acceptVisitor(this, ignored);
       writeToken(Roles.DOT);
     }
     
     writeKeyword(com.strobel.decompiler.languages.java.ast.ObjectCreationExpression.NEW_KEYWORD_ROLE);
     node.getType().acceptVisitor(this, ignored);
     space(this.policy.SpaceBeforeMethodCallParentheses);
     writeCommaSeparatedListInParenthesis(node.getArguments(), this.policy.SpaceWithinMethodCallParentheses);
     node.getTypeDeclaration().acceptVisitor(new JavaOutputVisitor(this.output, this.settings), ignored);
     endNode(node);
     return null;
   }
   
   public Void visitWildcardType(com.strobel.decompiler.languages.java.ast.WildcardType node, Void ignored)
   {
     startNode(node);
     writeToken(com.strobel.decompiler.languages.java.ast.WildcardType.WILDCARD_TOKEN_ROLE);
     
     AstNodeCollection<AstType> extendsBounds = node.getExtendsBounds();
     
     if (!extendsBounds.isEmpty()) {
       space();
       writeKeyword(com.strobel.decompiler.languages.java.ast.WildcardType.EXTENDS_KEYWORD_ROLE);
       writePipeSeparatedList(extendsBounds);
     }
     else {
       AstNodeCollection<AstType> superBounds = node.getSuperBounds();
       
       if (!superBounds.isEmpty()) {
         space();
         writeKeyword(com.strobel.decompiler.languages.java.ast.WildcardType.SUPER_KEYWORD_ROLE);
         writePipeSeparatedList(superBounds);
       }
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitMethodGroupExpression(com.strobel.decompiler.languages.java.ast.MethodGroupExpression node, Void ignored)
   {
     startNode(node);
     node.getTarget().acceptVisitor(this, ignored);
     writeToken(com.strobel.decompiler.languages.java.ast.MethodGroupExpression.DOUBLE_COLON_ROLE);
     
     if (isKeyword(node.getMethodName())) {
       writeKeyword(node.getMethodName());
     }
     else {
       writeIdentifier(node.getMethodName());
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitEnumValueDeclaration(EnumValueDeclaration node, Void ignored)
   {
     startNode(node);
     writeAnnotations(node.getAnnotations(), true);
     writeIdentifier(node.getName());
     
     AstNodeCollection<Expression> arguments = node.getArguments();
     
     if (!arguments.isEmpty()) {
       writeCommaSeparatedListInParenthesis(arguments, this.policy.SpaceWithinEnumDeclarationParentheses);
     }
     
     AstNodeCollection<EntityDeclaration> members = node.getMembers();
     TypeDefinition enumType = (TypeDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.TYPE_DEFINITION);
     
     if (((enumType != null) && (enumType.isAnonymous())) || (!members.isEmpty())) {
       BraceStyle braceStyle = this.policy.AnonymousClassBraceStyle;
       
       openBrace(braceStyle);
       
       boolean first = true;
       EntityDeclaration lastMember = null;
       
       for (EntityDeclaration member : node.getMembers()) {
         if (first) {
           first = false;
         }
         else {
           int blankLines;
           int blankLines;
           if (((member instanceof FieldDeclaration)) && ((lastMember instanceof FieldDeclaration))) {
             blankLines = this.policy.BlankLinesBetweenFields;
           }
           else {
             blankLines = this.policy.BlankLinesBetweenMembers;
           }
           
           for (int i = 0; i < blankLines; i++) {
             this.formatter.newLine();
           }
         }
         member.acceptVisitor(this, ignored);
         lastMember = member;
       }
       
       closeBrace(braceStyle);
     }
     
     boolean isLast = true;
     
     for (AstNode next = node.getNextSibling(); next != null; next = next.getNextSibling()) {
       if (next.getRole() == Roles.TYPE_MEMBER) {
         if (!(next instanceof EnumValueDeclaration)) break;
         isLast = false; break;
       }
     }
     
 
 
     if (isLast) {
       semicolon();
     }
     else {
       comma(node.getNextSibling());
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitAssertStatement(com.strobel.decompiler.languages.java.ast.AssertStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.AssertStatement.ASSERT_KEYWORD_ROLE);
     space();
     node.getCondition().acceptVisitor(this, ignored);
     
     Expression message = node.getMessage();
     
     if ((message != null) && (!message.isNull())) {
       space();
       writeToken(Roles.COLON);
       space();
       message.acceptVisitor(this, ignored);
     }
     
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitLambdaExpression(LambdaExpression node, Void ignored)
   {
     startNode(node);
     
     if (lambdaNeedsParenthesis(node)) {
       writeCommaSeparatedListInParenthesis(node.getParameters(), this.policy.SpaceWithinMethodDeclarationParentheses);
     }
     else {
       ((ParameterDeclaration)node.getParameters().firstOrNullObject()).acceptVisitor(this, ignored);
     }
     
     space();
     writeToken(LambdaExpression.ARROW_ROLE);
     
     if (!(node.getBody() instanceof BlockStatement)) {
       space();
     }
     
     node.getBody().acceptVisitor(this, ignored);
     endNode(node);
     
     return null;
   }
   
   private static boolean lambdaNeedsParenthesis(LambdaExpression lambda) {
     return (lambda.getParameters().size() != 1) || (!((ParameterDeclaration)lambda.getParameters().firstOrNullObject()).getType().isNull());
   }
   
 
   public Void visitArrayCreationExpression(ArrayCreationExpression node, Void ignored)
   {
     startNode(node);
     
     boolean needType = true;
     
     if ((node.getDimensions().isEmpty()) && (node.getType() != null) && (((node.getParent() instanceof com.strobel.decompiler.languages.java.ast.ArrayInitializerExpression)) || ((node.getParent() instanceof com.strobel.decompiler.languages.java.ast.VariableInitializer))))
     {
 
 
 
       needType = false;
     }
     
     if (needType) {
       writeKeyword(ArrayCreationExpression.NEW_KEYWORD_ROLE);
       node.getType().acceptVisitor(this, ignored);
       
       for (Expression dimension : node.getDimensions()) {
         writeToken(Roles.LEFT_BRACKET);
         dimension.acceptVisitor(this, ignored);
         writeToken(Roles.RIGHT_BRACKET);
       }
       
       for (ArraySpecifier specifier : node.getAdditionalArraySpecifiers()) {
         specifier.acceptVisitor(this, ignored);
       }
       
       if ((node.getInitializer() != null) && (!node.getInitializer().isNull())) {
         space();
       }
     }
     
     node.getInitializer().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void ignored)
   {
     startNode(node);
     node.getLeft().acceptVisitor(this, ignored);
     space(this.policy.SpaceAroundAssignment);
     writeToken(AssignmentExpression.getOperatorRole(node.getOperator()));
     space(this.policy.SpaceAroundAssignment);
     node.getRight().acceptVisitor(this, ignored);
     endNode(node);
     return null;
   }
   
   public Void visitForStatement(ForStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(ForStatement.FOR_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeForParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinForParentheses);
     
     writeCommaSeparatedList(node.getInitializers());
     space(this.policy.SpaceBeforeForSemicolon);
     writeToken(Roles.SEMICOLON);
     space(this.policy.SpaceAfterForSemicolon);
     
     node.getCondition().acceptVisitor(this, ignored);
     space(this.policy.SpaceBeforeForSemicolon);
     writeToken(Roles.SEMICOLON);
     
     if (CollectionUtilities.any(node.getIterators())) {
       space(this.policy.SpaceAfterForSemicolon);
       writeCommaSeparatedList(node.getIterators());
     }
     
     space(this.policy.SpacesWithinForParentheses);
     rightParenthesis();
     writeEmbeddedStatement(node.getEmbeddedStatement());
     endNode(node);
     return null;
   }
   
   public Void visitForEachStatement(ForEachStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(ForEachStatement.FOR_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeForeachParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinForeachParentheses);
     writeModifiers(node.getChildrenByRole(EntityDeclaration.MODIFIER_ROLE));
     node.getVariableType().acceptVisitor(this, ignored);
     space();
     node.getVariableNameToken().acceptVisitor(this, ignored);
     space();
     writeToken(ForEachStatement.COLON_ROLE);
     space();
     node.getInExpression().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinForeachParentheses);
     rightParenthesis();
     writeEmbeddedStatement(node.getEmbeddedStatement());
     endNode(node);
     return null;
   }
   
   public Void visitTryCatchStatement(TryCatchStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(TryCatchStatement.TRY_KEYWORD_ROLE);
     
     AstNodeCollection<VariableDeclarationStatement> resources = node.getResources();
     
     if (!resources.isEmpty()) {
       space();
       leftParenthesis();
       
       VariableDeclarationStatement firstResource = (VariableDeclarationStatement)resources.firstOrNullObject();
       
       for (VariableDeclarationStatement resource = firstResource; 
           resource != null; 
           resource = (VariableDeclarationStatement)resource.getNextSibling(TryCatchStatement.TRY_RESOURCE_ROLE))
       {
         if (resource != firstResource) {
           semicolon();
           
           space();space();space();space();space();
         }
         
         writeVariableDeclaration(resource, false);
       }
       
       rightParenthesis();
     }
     
     node.getTryBlock().acceptVisitor(this, ignored);
     
     for (CatchClause catchClause : node.getCatchClauses()) {
       catchClause.acceptVisitor(this, ignored);
     }
     
     if (!node.getFinallyBlock().isNull()) {
       writeKeyword(TryCatchStatement.FINALLY_KEYWORD_ROLE);
       node.getFinallyBlock().acceptVisitor(this, ignored);
     }
     
     endNode(node);
     return null;
   }
   
   public Void visitGotoStatement(com.strobel.decompiler.languages.java.ast.GotoStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.GotoStatement.GOTO_KEYWORD_ROLE);
     writeIdentifier(node.getLabel(), Roles.LABEL);
     semicolon();
     endNode(node);
     return null;
   }
   
   public Void visitParenthesizedExpression(com.strobel.decompiler.languages.java.ast.ParenthesizedExpression node, Void ignored)
   {
     startNode(node);
     leftParenthesis();
     space(this.policy.SpacesWithinParentheses);
     node.getExpression().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinParentheses);
     rightParenthesis();
     endNode(node);
     return null;
   }
   
   public Void visitSynchronizedStatement(com.strobel.decompiler.languages.java.ast.SynchronizedStatement node, Void ignored)
   {
     startNode(node);
     writeKeyword(com.strobel.decompiler.languages.java.ast.SynchronizedStatement.SYNCHRONIZED_KEYWORD_ROLE);
     space(this.policy.SpaceBeforeSynchronizedParentheses);
     leftParenthesis();
     space(this.policy.SpacesWithinSynchronizedParentheses);
     node.getExpression().acceptVisitor(this, ignored);
     space(this.policy.SpacesWithinSynchronizedParentheses);
     rightParenthesis();
     writeEmbeddedStatement(node.getEmbeddedStatement());
     endNode(node);
     return null;
   }
   
 
   public static String convertCharacter(char ch)
   {
     switch (ch) {
     case '\\': 
       return "\\\\";
     case '\000': 
       return "\000";
     case '\b': 
       return "\\b";
     case '\f': 
       return "\\f";
     case '\n': 
       return "\\n";
     case '\r': 
       return "\\r";
     case '\t': 
       return "\\t";
     case '"': 
       return "\\\"";
     }
     
     if ((ch >= 'À') || (Character.isISOControl(ch)) || (Character.isSurrogate(ch)) || ((Character.isWhitespace(ch)) && (ch != ' ')))
     {
 
 
 
       return String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) });
     }
     
     return String.valueOf(ch);
   }
   
 
   public static String escapeUnicode(String s)
   {
     StringBuilder sb = null;
     
     int i = 0; for (int n = s.length(); i < n; i++) {
       char ch = s.charAt(i);
       
       if ((ch >= 'À') || (Character.isISOControl(ch)) || (Character.isSurrogate(ch)) || ((Character.isWhitespace(ch)) && (ch != ' ')))
       {
 
 
 
         if (sb == null) {
           sb = new StringBuilder(Math.max(16, s.length()));
           
           if (i > 0) {
             sb.append(s, 0, i);
           }
         }
         
         sb.append(String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) }));
 
       }
       else if (sb != null) {
         sb.append(ch);
       }
     }
     
 
     return sb != null ? sb.toString() : s;
   }
   
 
 
 
   static enum LastWritten
   {
     Whitespace, 
     Other, 
     KeywordOrIdentifier, 
     Plus, 
     Minus, 
     Ampersand, 
     QuestionMark, 
     Division, 
     Operator, 
     Delimiter, 
     LeftParenthesis;
     
 
     private LastWritten() {}
   }
   
 
   private static final String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static boolean isKeyword(String identifier)
   {
     return com.strobel.core.ArrayUtilities.contains(KEYWORDS, identifier);
   }
   
   public static boolean isKeyword(String identifier, AstNode context)
   {
     return com.strobel.core.ArrayUtilities.contains(KEYWORDS, identifier);
   }
 }


