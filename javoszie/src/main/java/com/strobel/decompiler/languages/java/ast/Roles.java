 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Roles
 {
   public static final Role<AstNode> Root = AstNode.ROOT_ROLE;
   
 
 
 
 
   public static final Role<AstType> TYPE = new Role("Type", AstType.class, AstType.NULL);
   public static final Role<AstType> BASE_TYPE = new Role("BaseType", AstType.class, AstType.NULL);
   public static final Role<AstType> IMPLEMENTED_INTERFACE = new Role("ImplementedInterface", AstType.class, AstType.NULL);
   public static final Role<AstType> TYPE_ARGUMENT = new Role("TypeArgument", AstType.class, AstType.NULL);
   public static final Role<AstType> EXTENDS_BOUND = new Role("ExtendsBound", AstType.class, AstType.NULL);
   public static final Role<AstType> SUPER_BOUND = new Role("SuperBound", AstType.class, AstType.NULL);
   public static final Role<TypeParameterDeclaration> TYPE_PARAMETER = new Role("TypeParameter", TypeParameterDeclaration.class);
   public static final Role<Expression> ARGUMENT = new Role("Argument", Expression.class, Expression.NULL);
   public static final Role<ParameterDeclaration> PARAMETER = new Role("Parameter", ParameterDeclaration.class);
   public static final Role<Expression> EXPRESSION = new Role("Expression", Expression.class, Expression.NULL);
   public static final Role<Expression> TARGET_EXPRESSION = new Role("Target", Expression.class, Expression.NULL);
   public static final Role<Expression> CONDITION = new Role("Condition", Expression.class, Expression.NULL);
   public static final Role<Comment> COMMENT = new Role("Comment", Comment.class);
   public static final Role<Identifier> LABEL = new Role("Label", Identifier.class, Identifier.NULL);
   public static final Role<Identifier> IDENTIFIER = new Role("Identifier", Identifier.class, Identifier.NULL);
   public static final Role<Statement> EMBEDDED_STATEMENT = new Role("EmbeddedStatement", Statement.class, Statement.NULL);
   public static final Role<BlockStatement> BODY = new Role("Body", BlockStatement.class, BlockStatement.NULL);
   public static final Role<Annotation> ANNOTATION = new Role("Annotation", Annotation.class);
   public static final Role<VariableInitializer> VARIABLE = new Role("Variable", VariableInitializer.class, VariableInitializer.NULL);
   public static final Role<EntityDeclaration> TYPE_MEMBER = new Role("TypeMember", EntityDeclaration.class);
   public static final Role<TypeDeclaration> LOCAL_TYPE_DECLARATION = new Role("LocalTypeDeclaration", TypeDeclaration.class, TypeDeclaration.NULL);
   public static final Role<AstType> THROWN_TYPE = new Role("ThrownType", AstType.class, AstType.NULL);
   public static final Role<PackageDeclaration> PACKAGE = new Role("Package", PackageDeclaration.class, PackageDeclaration.NULL);
   public static final Role<NewLineNode> NEW_LINE = new Role("NewLine", NewLineNode.class);
   public static final Role<TextNode> TEXT = new Role("Text", TextNode.class);
   
 
 
 
 
   public static final TokenRole LEFT_PARENTHESIS = new TokenRole("(", 4);
   public static final TokenRole RIGHT_PARENTHESIS = new TokenRole(")", 4);
   public static final TokenRole LEFT_BRACKET = new TokenRole("[", 4);
   public static final TokenRole RIGHT_BRACKET = new TokenRole("]", 4);
   public static final TokenRole LEFT_BRACE = new TokenRole("{", 4);
   public static final TokenRole RIGHT_BRACE = new TokenRole("}", 4);
   public static final TokenRole LEFT_CHEVRON = new TokenRole("<", 4);
   public static final TokenRole RIGHT_CHEVRON = new TokenRole(">", 4);
   public static final TokenRole COMMA = new TokenRole(",", 4);
   public static final TokenRole DOT = new TokenRole(".", 4);
   public static final TokenRole SEMICOLON = new TokenRole(";", 4);
   public static final TokenRole COLON = new TokenRole(":", 4);
   public static final TokenRole DOUBLE_COLON = new TokenRole("::", 4);
   public static final TokenRole ASSIGN = new TokenRole("=", 2);
   public static final TokenRole PIPE = new TokenRole("|", 2);
   public static final TokenRole VARARGS = new TokenRole("...", 4);
   
 
 
 
 
   public static final TokenRole DEFAULT_KEYWORD = new TokenRole("default", 1);
   public static final TokenRole PACKAGE_KEYWORD = new TokenRole("package", 1);
   public static final TokenRole ENUM_KEYWORD = new TokenRole("enum", 1);
   public static final TokenRole INTERFACE_KEYWORD = new TokenRole("interface", 1);
   public static final TokenRole CLASS_KEYWORD = new TokenRole("class", 1);
   public static final TokenRole ANNOTATION_KEYWORD = new TokenRole("@interface", 1);
   public static final TokenRole EXTENDS_KEYWORD = new TokenRole("extends", 1);
   public static final TokenRole IMPLEMENTS_KEYWORD = new TokenRole("implements", 1);
 }


