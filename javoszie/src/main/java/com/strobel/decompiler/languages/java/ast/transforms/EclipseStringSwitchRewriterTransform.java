 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.BreakStatement;
 import com.strobel.decompiler.languages.java.ast.CaseLabel;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.Choice;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Repeat;
 import com.strobel.decompiler.patterns.SingleOrBinaryAggregateNode;
 import java.util.ArrayList;
 import java.util.List;
 
 public class EclipseStringSwitchRewriterTransform extends ContextTrackingVisitor<Void>
 {
   public EclipseStringSwitchRewriterTransform(DecompilerContext context)
   {
     super(context);
   }
   
 
 
 
 
 
 
   private static final Pattern HASH_CODE_PATTERN = new NamedNode("hashCodeCall", new InvocationExpression(-34, new MemberReferenceExpression(-34, new AnyNode("target").toExpression(), "hashCode", new AstType[0]), new Expression[0]));
   
 
 
 
   private static final BlockStatement CASE_BODY_PATTERN;
   
 
 
 
   static
   {
     BlockStatement caseBody = new BlockStatement();
     
     IfElseStatement test = new IfElseStatement(-34, new com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorType.NOT, new SingleOrBinaryAggregateNode(com.strobel.decompiler.languages.java.ast.BinaryOperatorType.LOGICAL_OR, new InvocationExpression(-34, new MemberReferenceExpression(-34, new NamedNode("input", new IdentifierExpression(-34, "$any$")).toExpression(), "equals", new AstType[0]), new Expression[] { new NamedNode("stringValue", new PrimitiveExpression(-34, "$any$")).toExpression() })).toExpression()), new BlockStatement(new com.strobel.decompiler.languages.java.ast.Statement[] { new Choice(new INode[] { new NamedNode("defaultBreak", new BreakStatement(-34, "$any$")), new com.strobel.decompiler.languages.java.ast.ReturnStatement(-34) }).toStatement() }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     caseBody.add(new NamedNode("test", test).toStatement());
     caseBody.add(new Repeat(new AnyNode("statements")).toStatement());
     
     CASE_BODY_PATTERN = caseBody;
   }
   
 
 
 
   public Void visitSwitchStatement(SwitchStatement node, Void data)
   {
     super.visitSwitchStatement(node, data);
     
     Expression input = node.getExpression();
     
     if ((input == null) || (input.isNull())) {
       return null;
     }
     
     Match m2 = HASH_CODE_PATTERN.match(input);
     
     if (!m2.success()) {
       return null;
     }
     
     InvocationExpression hashCodeCall = (InvocationExpression)CollectionUtilities.first(m2.get("hashCodeCall"));
     MemberReference hashCodeMethod = (MemberReference)hashCodeCall.getUserData(Keys.MEMBER_REFERENCE);
     
     if ((!(hashCodeMethod instanceof MethodReference)) || (!"java/lang/String".equals(hashCodeMethod.getDeclaringType().getInternalName())))
     {
 
       return null;
     }
     
     List<Match> matches = new ArrayList();
     
     AstNodeCollection<SwitchSection> sections = node.getSwitchSections();
     
     for (SwitchSection section : sections) {
       AstNodeCollection<CaseLabel> caseLabels = section.getCaseLabels();
       
       if ((caseLabels.isEmpty()) || ((caseLabels.hasSingleElement()) && (((CaseLabel)caseLabels.firstOrNullObject()).isNull())))
       {
 
 
 
 
         return null;
       }
       
       Match m3 = CASE_BODY_PATTERN.match(section.getStatements().firstOrNullObject());
       
       if (m3.success()) {
         matches.add(m3);
       }
       else {
         return null;
       }
     }
     
     int matchIndex = 0;
     BreakStatement defaultBreak = null;
     
     for (SwitchSection section : sections) {
       Match m = (Match)matches.get(matchIndex++);
       IfElseStatement test = (IfElseStatement)CollectionUtilities.first(m.get("test"));
       List<PrimitiveExpression> stringValues = CollectionUtilities.toList(m.get("stringValue"));
       AstNodeCollection<CaseLabel> caseLabels = section.getCaseLabels();
       
       if (defaultBreak == null) {
         defaultBreak = (BreakStatement)CollectionUtilities.firstOrDefault(m.get("defaultBreak"));
       }
       
       caseLabels.clear();
       test.remove();
       
       for (int i = 0; i < stringValues.size(); i++) {
         PrimitiveExpression stringValue = (PrimitiveExpression)stringValues.get(i);
         
         stringValue.remove();
         caseLabels.add(new CaseLabel(stringValue));
       }
     }
     
     if (defaultBreak != null) {
       SwitchSection defaultSection = new SwitchSection();
       
       defaultBreak.remove();
       
       defaultSection.getCaseLabels().add(new CaseLabel());
       defaultSection.getStatements().add(defaultBreak);
       
       sections.add(defaultSection);
     }
     
     AstNode newInput = (AstNode)CollectionUtilities.first(m2.get("target"));
     
     newInput.remove();
     node.getExpression().replaceWith(newInput);
     
     return null;
   }
 }


