 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 import com.strobel.util.ContractUtils;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CompilationUnit
   extends AstNode
 {
   public static final Role<AstNode> MEMBER_ROLE = new Role("Member", AstNode.class, AstNode.NULL);
   public static final Role<ImportDeclaration> IMPORT_ROLE = new Role("Import", ImportDeclaration.class, ImportDeclaration.NULL);
   private AstNode _topExpression;
   private String _fileName;
   
   public final AstNodeCollection<ImportDeclaration> getImports()
   {
     return getChildrenByRole(IMPORT_ROLE);
   }
   
   public final PackageDeclaration getPackage() {
     return (PackageDeclaration)getChildByRole(Roles.PACKAGE);
   }
   
   public final void setPackage(PackageDeclaration value) {
     setChildByRole(Roles.PACKAGE, value);
   }
   
   public final String getFileName() {
     return this._fileName;
   }
   
   public final void setFileName(String fileName) {
     verifyNotFrozen();
     this._fileName = fileName;
   }
   
   public final AstNode getTopExpression() {
     return this._topExpression;
   }
   
   final void setTopExpression(AstNode topExpression) {
     this._topExpression = topExpression;
   }
   
   public final AstNodeCollection<AstNode> getMembers() {
     return getChildrenByRole(MEMBER_ROLE);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitCompilationUnit(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof CompilationUnit)) && (!other.isNull()) && (getMembers().matches(((CompilationUnit)other).getMembers(), match));
   }
   
 
   public Iterable<TypeDeclaration> getTypes()
   {
     return getTypes(false);
   }
   
   public Iterable<TypeDeclaration> getTypes(final boolean includeInnerTypes) {
     new Iterable()
     {
       public final Iterator<TypeDeclaration> iterator() {
         new Iterator()
         {
           final Stack<AstNode> nodeStack;
           
 
           TypeDeclaration next;
           
 
           private TypeDeclaration selectNext()
           {
             if (this.next != null) {
               return this.next;
             }
             
             while (!this.nodeStack.isEmpty()) {
               AstNode current = (AstNode)this.nodeStack.pop();
               
/* ;0 */               if ((current instanceof TypeDeclaration)) {
/* ;1 */                 this.next = ((TypeDeclaration)current);
/* ;2 */                 break;
               }
               
/* ;5 */               for (AstNode child : current.getChildren()) {
/* ;6 */                 if ((!(child instanceof Statement)) && (!(child instanceof Expression)) && ((child.getRole() != Roles.TYPE_MEMBER) || (((child instanceof TypeDeclaration)) && (CompilationUnit.1.this.val$includeInnerTypes))))
                 {
 
/* ;9 */                   this.nodeStack.push(child);
                 }
               }
             }
             
/* <4 */             return null;
           }
           
           public final boolean hasNext()
           {
/* <9 */             return selectNext() != null;
           }
           
           public final TypeDeclaration next()
           {
/* =4 */             TypeDeclaration next = selectNext();
             
/* =6 */             if (next == null) {
/* =7 */               throw new NoSuchElementException();
             }
             
/* >0 */             this.next = null;
/* >1 */             return next;
           }
           
           public final void remove()
           {
/* >6 */             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
 }


