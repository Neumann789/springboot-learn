 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PackageDeclaration
   extends AstNode
 {
   public PackageDeclaration() {}
   
   public PackageDeclaration(String name)
   {
     setName(name);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public final JavaTokenNode getPackageToken() {
     return (JavaTokenNode)getChildByRole(Roles.PACKAGE_KEYWORD);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public final AstNodeCollection<Identifier> getIdentifiers() {
     return getChildrenByRole(Roles.IDENTIFIER);
   }
   
   public final String getName() {
     StringBuilder sb = new StringBuilder();
     
     for (Identifier identifier : getIdentifiers()) {
       if (sb.length() > 0) {
         sb.append('.');
       }
       
       sb.append(identifier.getName());
     }
     
     return sb.toString();
   }
   
   public final void setName(String name) {
     if (name == null) {
       getChildrenByRole(Roles.IDENTIFIER).clear();
       return;
     }
     
     String[] parts = name.split("\\.");
     Identifier[] identifiers = new Identifier[parts.length];
     
     for (int i = 0; i < identifiers.length; i++) {
       identifiers[i] = Identifier.create(parts[i]);
     }
     
     getChildrenByRole(Roles.IDENTIFIER).replaceWith(ArrayUtilities.asUnmodifiableList(identifiers));
   }
   
   public static String BuildQualifiedName(String name1, String name2) {
     if (StringUtilities.isNullOrEmpty(name1)) {
       return name2;
     }
     
     if (StringUtilities.isNullOrEmpty(name2)) {
       return name1;
     }
     
     return name1 + "." + name2;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitPackageDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof PackageDeclaration)) && (!other.isNull()) && (matchString(getName(), ((PackageDeclaration)other).getName()));
   }
   
 
 
 
 
   public static final PackageDeclaration NULL = new NullPackageDeclaration(null);
   
   private static final class NullPackageDeclaration extends PackageDeclaration
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


