 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentAnalysis;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.ForEachStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LocalTypeDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 public class DeclareLocalClassesTransform implements IAstTransform
 {
   protected final DecompilerContext context;
   protected final AstBuilder astBuilder;
   
   public DeclareLocalClassesTransform(DecompilerContext context)
   {
     this.context = ((DecompilerContext)com.strobel.core.VerifyArgument.notNull(context, "context"));
     this.astBuilder = ((AstBuilder)context.getUserData(Keys.AST_BUILDER));
   }
   
 
   public void run(AstNode node)
   {
     if (this.astBuilder == null) {
       return;
     }
     
     run(node, null);
   }
   
   private void run(AstNode node, DefiniteAssignmentAnalysis daa) {
     DefiniteAssignmentAnalysis analysis = daa;
     
     if ((node instanceof MethodDeclaration)) {
       MethodDeclaration method = (MethodDeclaration)node;
       List<TypeDeclaration> localTypes = new ArrayList();
       
       for (TypeDeclaration localType : method.getDeclaredTypes()) {
         localTypes.add(localType);
       }
       
       if (!localTypes.isEmpty())
       {
 
 
         for (TypeDeclaration localType : localTypes) {
           localType.remove();
         }
         
         if (analysis == null) {
           analysis = new DefiniteAssignmentAnalysis(method.getBody(), new com.strobel.decompiler.languages.java.ast.JavaResolver(this.context));
         }
         
 
 
         Set<TypeToDeclare> typesToDeclare = new java.util.LinkedHashSet();
         boolean madeProgress;
         do {
           madeProgress = false;
           
 
 
 
 
 
           for (Iterator<TypeDeclaration> iterator = localTypes.iterator(); iterator.hasNext();) {
             TypeDeclaration localType = (TypeDeclaration)iterator.next();
             
             if (declareTypeInBlock(method.getBody(), localType, true, typesToDeclare)) {
               madeProgress = true;
               iterator.remove();
             }
           }
           
           if ((!madeProgress) && (!localTypes.isEmpty()))
           {
 
 
 
 
 
 
             TypeDeclaration firstUndeclared = (TypeDeclaration)CollectionUtilities.first(localTypes);
             
             method.getBody().insertChildBefore(method.getBody().getFirstChild(), new LocalTypeDeclarationStatement(-34, firstUndeclared), BlockStatement.STATEMENT_ROLE);
             
 
 
 
 
             madeProgress = true;
             localTypes.remove(0);
           }
           
           for (TypeToDeclare v : typesToDeclare) {
             BlockStatement block = (BlockStatement)v.getInsertionPoint().getParent();
             
             if (block != null)
             {
 
 
               Statement insertionPoint = v.getInsertionPoint();
               
               while ((insertionPoint.getPreviousSibling() instanceof com.strobel.decompiler.languages.java.ast.LabelStatement)) {
                 insertionPoint = (Statement)insertionPoint.getPreviousSibling();
               }
               
               block.insertChildBefore(insertionPoint, new LocalTypeDeclarationStatement(-34, v.getDeclaration()), BlockStatement.STATEMENT_ROLE);
             }
           }
           
 
 
 
           typesToDeclare.clear();
 
 
 
 
 
 
         }
         while ((madeProgress) && (!localTypes.isEmpty()));
       }
     }
     
     for (AstNode child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
       if ((child instanceof TypeDeclaration)) {
         TypeDefinition currentType = this.context.getCurrentType();
         MethodDefinition currentMethod = this.context.getCurrentMethod();
         
         this.context.setCurrentType(null);
         this.context.setCurrentMethod(null);
         try
         {
           TypeDefinition type = (TypeDefinition)child.getUserData(Keys.TYPE_DEFINITION);
           
           if ((type != null) && (type.isInterface()))
           {
 
 
 
 
 
             this.context.setCurrentType(currentType);
             this.context.setCurrentMethod(currentMethod); continue;
           }
           new DeclareLocalClassesTransform(this.context).run(child);
         }
         finally {
           this.context.setCurrentType(currentType);
           this.context.setCurrentMethod(currentMethod);
         }
       }
       else {
         run(child, analysis);
       }
     }
   }
   
 
 
 
 
 
 
 
 
   private boolean declareTypeInBlock(BlockStatement block, TypeDeclaration type, boolean allowPassIntoLoops, Set<TypeToDeclare> typesToDeclare)
   {
     StrongBox<Statement> declarationPoint = new StrongBox();
     TypeDefinition typeDefinition = (TypeDefinition)type.getUserData(Keys.TYPE_DEFINITION);
     
     boolean canMoveVariableIntoSubBlocks = findDeclarationPoint(typeDefinition, allowPassIntoLoops, block, declarationPoint, null);
     
 
 
 
 
 
 
     if (declarationPoint.get() == null)
     {
 
 
       return false;
     }
     
     if (canMoveVariableIntoSubBlocks) {
       for (Statement statement : block.getStatements()) {
         if (referencesType(statement, typeDefinition))
         {
 
 
           for (AstNode child : statement.getChildren()) {
             if ((child instanceof BlockStatement)) {
               if (declareTypeInBlock((BlockStatement)child, type, allowPassIntoLoops, typesToDeclare)) {
                 return true;
               }
             }
             else if (hasNestedBlocks(child)) {
               for (AstNode nestedChild : child.getChildren()) {
                 if (((nestedChild instanceof BlockStatement)) && (declareTypeInBlock((BlockStatement)nestedChild, type, allowPassIntoLoops, typesToDeclare)))
                 {
 
                   return true;
                 }
               }
             }
           }
           
           boolean canStillMoveIntoSubBlocks = findDeclarationPoint(typeDefinition, allowPassIntoLoops, block, declarationPoint, statement);
           
 
 
 
 
 
 
           if ((!canStillMoveIntoSubBlocks) && (declarationPoint.get() != null)) {
             TypeToDeclare vtd = new TypeToDeclare(type, typeDefinition, (Statement)declarationPoint.get(), block);
             typesToDeclare.add(vtd);
             return true;
           }
         }
       }
       return false;
     }
     
     TypeToDeclare vtd = new TypeToDeclare(type, typeDefinition, (Statement)declarationPoint.get(), block);
     typesToDeclare.add(vtd);
     return true;
   }
   
 
 
 
 
 
   public static boolean findDeclarationPoint(TypeDeclaration declaration, BlockStatement block, StrongBox<Statement> declarationPoint, Statement skipUpThrough)
   {
     return findDeclarationPoint((TypeReference)declaration.getUserData(Keys.TYPE_DEFINITION), true, block, declarationPoint, skipUpThrough);
   }
   
 
 
 
 
 
   static boolean findDeclarationPoint(TypeReference localType, boolean allowPassIntoLoops, BlockStatement block, StrongBox<Statement> declarationPoint, Statement skipUpThrough)
   {
     declarationPoint.set(null);
     
     Statement waitFor = skipUpThrough;
     
     for (Statement statement : block.getStatements()) {
       if (waitFor != null) {
         if (statement == waitFor) {
           waitFor = null;
         }
         
 
       }
       else if (referencesType(statement, localType)) {
         if (declarationPoint.get() != null) {
           return false;
         }
         
         declarationPoint.set(statement);
         
         if (!canMoveLocalTypeIntoSubBlock(statement, localType, allowPassIntoLoops))
         {
 
 
 
           return false;
         }
         
 
 
 
 
 
 
         AstNode nextNode = statement.getNextSibling();
         
         while (nextNode != null) {
           if (referencesType(nextNode, localType)) {
             return false;
           }
           nextNode = nextNode.getNextSibling();
         }
       }
     }
     
     return true;
   }
   
 
 
 
   private static boolean canMoveLocalTypeIntoSubBlock(Statement statement, TypeReference localType, boolean allowPassIntoLoops)
   {
     if ((!allowPassIntoLoops) && (AstNode.isLoop(statement))) {
       return false;
     }
     
 
 
 
 
 
     for (AstNode child = statement.getFirstChild(); child != null; child = child.getNextSibling()) {
       if ((!(child instanceof BlockStatement)) && (referencesType(child, localType))) {
         if (hasNestedBlocks(child))
         {
 
 
           for (AstNode grandChild = child.getFirstChild(); grandChild != null; grandChild = grandChild.getNextSibling()) {
             if ((!(grandChild instanceof BlockStatement)) && (referencesType(grandChild, localType))) {
               return false;
             }
             
           }
         } else {
           return false;
         }
       }
     }
     
     return true;
   }
   
   private static boolean referencesType(AstType reference, TypeReference localType) {
     return (reference != null) && (referencesType((TypeReference)reference.getUserData(Keys.TYPE_REFERENCE), localType));
   }
   
   private static boolean referencesType(TypeReference reference, TypeReference localType)
   {
     if ((reference == null) || (localType == null)) {
       return false;
     }
     
     TypeReference type = reference;
     
     while (type.isArray()) {
       type = type.getElementType();
     }
     
     TypeReference target = localType;
     
     while (target.isArray()) {
       target = target.getElementType();
     }
     
     if (StringUtilities.equals(type.getInternalName(), target.getInternalName())) {
       return true;
     }
     
     if (type.hasExtendsBound()) {
       TypeReference bound = type.getExtendsBound();
       
       if ((!bound.isGenericParameter()) && (!MetadataHelper.isSameType(bound, type)) && (referencesType(bound, localType))) {
         return true;
       }
     }
     
     if (type.hasSuperBound()) {
       TypeReference bound = type.getSuperBound();
       
       if ((!bound.isGenericParameter()) && (!MetadataHelper.isSameType(bound, type)) && (referencesType(bound, localType))) {
         return true;
       }
     }
     
     if (type.isGenericType()) {
       if ((type instanceof IGenericInstance)) {
         List<TypeReference> typeArguments = ((IGenericInstance)type).getTypeArguments();
         
         for (TypeReference typeArgument : typeArguments) {
           if ((!MetadataHelper.isSameType(typeArgument, type)) && (referencesType(typeArgument, localType))) {
             return true;
           }
         }
       }
       else {
         for (TypeReference typeArgument : type.getGenericParameters()) {
           if ((!MetadataHelper.isSameType(typeArgument, type)) && (referencesType(typeArgument, localType))) {
             return true;
           }
         }
       }
     }
     
     return false;
   }
   
   private static boolean referencesType(AstNode node, TypeReference localType) {
     if ((node instanceof AnonymousObjectCreationExpression)) {
       for (com.strobel.decompiler.languages.java.ast.Expression argument : ((AnonymousObjectCreationExpression)node).getArguments()) {
         if (referencesType(argument, localType)) {
           return true;
         }
       }
       return false;
     }
     
     if ((node instanceof LocalTypeDeclarationStatement)) {
       return referencesType(((LocalTypeDeclarationStatement)node).getTypeDeclaration(), localType);
     }
     
     if ((node instanceof TypeDeclaration)) {
       TypeDeclaration type = (TypeDeclaration)node;
       
       AstType baseType = type.getBaseType();
       
       if ((baseType != null) && (!baseType.isNull()) && (referencesType(baseType, localType))) {
         return true;
       }
       
       for (AstType ifType : type.getInterfaces()) {
         if (referencesType(ifType, localType)) {
           return true;
         }
       }
       
       for (FieldDeclaration field : CollectionUtilities.ofType(type.getMembers(), FieldDeclaration.class)) {
         FieldDefinition fieldDefinition = (FieldDefinition)field.getUserData(Keys.FIELD_DEFINITION);
         
         if ((fieldDefinition != null) && (StringUtilities.equals(fieldDefinition.getFieldType().getInternalName(), localType.getInternalName())))
         {
 
           return true;
         }
         
         if ((!field.getVariables().isEmpty()) && (referencesType((AstNode)CollectionUtilities.first(field.getVariables()), localType))) {
           return true;
         }
       }
       
       for (MethodDeclaration method : CollectionUtilities.ofType(type.getMembers(), MethodDeclaration.class)) {
         MethodDefinition methodDefinition = (MethodDefinition)method.getUserData(Keys.METHOD_DEFINITION);
         
         if (methodDefinition != null) {
           if (StringUtilities.equals(methodDefinition.getReturnType().getInternalName(), localType.getInternalName())) {
             return true;
           }
           
           for (ParameterDefinition parameter : methodDefinition.getParameters()) {
             if (StringUtilities.equals(parameter.getParameterType().getInternalName(), localType.getInternalName())) {
               return true;
             }
           }
         }
         
         if (referencesType(method.getBody(), localType)) {
           return true;
         }
       }
       
       return false;
     }
     
     if ((node instanceof AstType)) {
       return referencesType((AstType)node, localType);
     }
     
     if ((node instanceof ForStatement)) {
       ForStatement forLoop = (ForStatement)node;
       
       for (Statement statement : forLoop.getInitializers()) {
         if ((statement instanceof VariableDeclarationStatement)) {
           AstType type = ((VariableDeclarationStatement)statement).getType();
           
           if (referencesType(type, localType)) {
             return true;
           }
         }
       }
     }
     
     if ((node instanceof ForEachStatement)) {
       ForEachStatement forEach = (ForEachStatement)node;
       
       if (referencesType(forEach.getVariableType(), localType)) {
         return true;
       }
     }
     
     if ((node instanceof TryCatchStatement)) {
       TryCatchStatement tryCatch = (TryCatchStatement)node;
       
       for (VariableDeclarationStatement resource : tryCatch.getResources()) {
         if (referencesType(resource.getType(), localType)) {
           return true;
         }
       }
     }
     
     if ((node instanceof CatchClause)) {
       for (AstType type : ((CatchClause)node).getExceptionTypes()) {
         if (referencesType(type, localType)) {
           return true;
         }
       }
     }
     for (AstNode child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
       if (referencesType(child, localType)) {
         return true;
       }
     }
     
     return false;
   }
   
   private static boolean hasNestedBlocks(AstNode node) {
     return ((node.getChildByRole(com.strobel.decompiler.languages.java.ast.Roles.EMBEDDED_STATEMENT) instanceof BlockStatement)) || ((node instanceof TryCatchStatement)) || ((node instanceof CatchClause)) || ((node instanceof com.strobel.decompiler.languages.java.ast.SwitchSection));
   }
   
 
 
   protected static final class TypeToDeclare
   {
     private final TypeDeclaration _declaration;
     
 
     private final TypeDefinition _typeDefinition;
     
 
     private final Statement _insertionPoint;
     
     private final BlockStatement _block;
     
 
     public TypeToDeclare(TypeDeclaration declaration, TypeDefinition definition, Statement insertionPoint, BlockStatement block)
     {
       this._declaration = declaration;
       this._typeDefinition = definition;
       this._insertionPoint = insertionPoint;
       this._block = block;
     }
     
     public BlockStatement getBlock() {
       return this._block;
     }
     
     public TypeDeclaration getDeclaration() {
       return this._declaration;
     }
     
     public TypeDefinition getTypeDefinition() {
       return this._typeDefinition;
     }
     
     public Statement getInsertionPoint() {
       return this._insertionPoint;
     }
     
     public String toString()
     {
       return "TypeToDeclare{Type=" + this._typeDefinition.getSignature() + ", InsertionPoint=" + this._insertionPoint + '}';
     }
   }
 }


