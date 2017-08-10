 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.PackageReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ImportDeclaration
   extends AstNode
 {
   public static final TokenRole IMPORT_KEYWORD_RULE = new TokenRole("import", 1);
   
   public ImportDeclaration() {}
   
   public ImportDeclaration(String packageOrTypeName)
   {
     setImport(packageOrTypeName);
   }
   
   public ImportDeclaration(PackageReference pkg) {
     setImport(((PackageReference)VerifyArgument.notNull(pkg, "pkg")).getFullName() + ".*");
     putUserData(Keys.PACKAGE_REFERENCE, pkg);
   }
   
   public ImportDeclaration(TypeReference type) {
     setImport(((TypeReference)VerifyArgument.notNull(type, "pkg")).getFullName() + ".*");
     putUserData(Keys.TYPE_REFERENCE, type);
   }
   
   public ImportDeclaration(AstType type) {
     TypeReference typeReference = ((AstType)VerifyArgument.notNull(type, "type")).toTypeReference();
     
     if (typeReference != null) {
       setImport(typeReference.getFullName());
       putUserData(Keys.TYPE_REFERENCE, typeReference);
     }
     else {
       setImport(type.toString());
     }
   }
   
   public final String getImport() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setImport(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getImportIdentifier() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setImportIdentifier(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final JavaTokenNode getImportToken() {
     return (JavaTokenNode)getChildByRole(IMPORT_KEYWORD_RULE);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitImportDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ImportDeclaration)) && (getImportIdentifier().matches(((ImportDeclaration)other).getImportIdentifier(), match));
   }
   
 
 
 
   public static final ImportDeclaration NULL = new NullImportDeclaration(null);
   
   private static final class NullImportDeclaration extends ImportDeclaration
   {
     public final boolean isNull() {
       return true;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return null;
     }
     
     public boolean matches(INode other, Match match)
     {
       return (other == null) || (other.isNull());
     }
   }
 }


