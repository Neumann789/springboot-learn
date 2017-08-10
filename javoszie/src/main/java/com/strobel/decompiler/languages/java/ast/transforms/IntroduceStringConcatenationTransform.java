 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.CommonTypeReferences;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.OptionalNode;
 import com.strobel.decompiler.patterns.TypedExpression;
 import com.strobel.decompiler.semantics.ResolveResult;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 public class IntroduceStringConcatenationTransform
   extends ContextTrackingVisitor<Void>
 {
   private final INode _stringBuilderArgumentPattern;
   
   public IntroduceStringConcatenationTransform(DecompilerContext context)
   {
     super(context);
     
     this._stringBuilderArgumentPattern = new OptionalNode(new TypedExpression("firstArgument", CommonTypeReferences.String, new JavaResolver(context)));
   }
   
 
 
 
 
 
 
   public Void visitObjectCreationExpression(ObjectCreationExpression node, Void data)
   {
     AstNodeCollection<Expression> arguments = node.getArguments();
     
     if ((arguments.isEmpty()) || (arguments.hasSingleElement()))
     {
       Expression firstArgument;
       
       Expression firstArgument;
       if (arguments.hasSingleElement()) {
         Match m = this._stringBuilderArgumentPattern.match(arguments.firstOrNullObject());
         
         if (!m.success()) {
           return (Void)super.visitObjectCreationExpression(node, data);
         }
         
         firstArgument = (Expression)CollectionUtilities.firstOrDefault(m.get("firstArgument"));
       }
       else {
         firstArgument = null;
       }
       
       TypeReference typeReference = node.getType().toTypeReference();
       
       if ((typeReference != null) && (isStringBuilder(typeReference)))
       {
 
         convertStringBuilderToConcatenation(node, firstArgument);
       }
     }
     
     return (Void)super.visitObjectCreationExpression(node, data);
   }
   
   private boolean isStringBuilder(TypeReference typeReference) {
     if (StringUtilities.equals(typeReference.getInternalName(), "java/lang/StringBuilder")) {
       return true;
     }
     
     return (this.context.getCurrentType() != null) && (this.context.getCurrentType().getCompilerMajorVersion() < 49) && (StringUtilities.equals(typeReference.getInternalName(), "java/lang/StringBuffer"));
   }
   
 
   private void convertStringBuilderToConcatenation(ObjectCreationExpression node, Expression firstArgument)
   {
     if ((node.getParent() == null) || (node.getParent().getParent() == null)) {
       return;
     }
     
     ArrayList<Expression> operands = new ArrayList();
     
     if (firstArgument != null) {
       operands.add(firstArgument);
     }
     
 
 
 
     AstNode current = node.getParent(); for (AstNode parent = current.getParent(); 
         ((current instanceof MemberReferenceExpression)) && ((parent instanceof InvocationExpression)) && (parent.getParent() != null); 
         parent = current.getParent())
     {
       String memberName = ((MemberReferenceExpression)current).getMemberName();
       AstNodeCollection<Expression> arguments = ((InvocationExpression)parent).getArguments();
       
       if ((!StringUtilities.equals(memberName, "append")) || (arguments.size() != 1)) break;
       operands.add(arguments.firstOrNullObject());current = parent.getParent();
     }
     
 
 
 
 
     if ((operands.size() > 1) && (anyIsString(operands.subList(0, 2))) && ((current instanceof MemberReferenceExpression)) && ((parent instanceof InvocationExpression)) && (!(parent.getParent() instanceof ExpressionStatement)) && (StringUtilities.equals(((MemberReferenceExpression)current).getMemberName(), "toString")) && (((InvocationExpression)parent).getArguments().isEmpty()))
     {
 
 
 
 
 
 
       for (Expression operand : operands) {
         operand.remove();
       }
       
       Expression concatenation = new BinaryOperatorExpression((Expression)operands.get(0), BinaryOperatorType.ADD, (Expression)operands.get(1));
       
       for (int i = 2; i < operands.size(); i++) {
         concatenation = new BinaryOperatorExpression(concatenation, BinaryOperatorType.ADD, (Expression)operands.get(i));
       }
       
       parent.replaceWith(concatenation);
     }
   }
   
   private boolean anyIsString(List<Expression> expressions) {
     JavaResolver resolver = new JavaResolver(this.context);
     
     for (int i = 0; i < expressions.size(); i++) {
       ResolveResult result = resolver.apply((AstNode)expressions.get(i));
       
       if ((result != null) && (result.getType() != null) && (CommonTypeReferences.String.isEquivalentTo(result.getType())))
       {
 
 
         return true;
       }
     }
     
     return false;
   }
 }


