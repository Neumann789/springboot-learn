 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.BreakStatement;
 import com.strobel.decompiler.languages.java.ast.CaseLabel;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.patterns.IdentifierExpressionBackReference;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.OptionalNode;
 import com.strobel.decompiler.patterns.Repeat;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 public class StringSwitchRewriterTransform extends ContextTrackingVisitor<Void>
 {
   private static final VariableDeclarationStatement TABLE_SWITCH_INPUT;
   private static final com.strobel.decompiler.patterns.Pattern HASH_CODE_PATTERN;
   private static final BlockStatement CASE_BODY_PATTERN;
   
   public StringSwitchRewriterTransform(com.strobel.decompiler.DecompilerContext context)
   {
     super(context);
   }
   
 
 
 
 
 
   static
   {
     SimpleType intType = new SimpleType("int");
     
     intType.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
     
     TABLE_SWITCH_INPUT = new VariableDeclarationStatement(intType, "$any$", new PrimitiveExpression(-34, Integer.valueOf(-1)));
     
 
 
 
 
     HASH_CODE_PATTERN = new NamedNode("hashCodeCall", new InvocationExpression(-34, new MemberReferenceExpression(-34, new com.strobel.decompiler.patterns.AnyNode("target").toExpression(), "hashCode", new AstType[0]), new Expression[0]));
     
 
 
 
 
 
 
 
 
 
 
     BlockStatement caseBody = new BlockStatement();
     
     IfElseStatement test = new IfElseStatement(-34, new InvocationExpression(-34, new MemberReferenceExpression(-34, new NamedNode("input", new IdentifierExpression(-34, "$any$")).toExpression(), "equals", new AstType[0]), new Expression[] { new NamedNode("stringValue", new PrimitiveExpression(-34, "$any$")).toExpression() }), new BlockStatement(new Statement[] { new ExpressionStatement(new com.strobel.decompiler.languages.java.ast.AssignmentExpression(new NamedNode("tableSwitchInput", new IdentifierExpression(-34, "$any$")).toExpression(), new NamedNode("tableSwitchCaseValue", new PrimitiveExpression(-34, PrimitiveExpression.ANY_VALUE)).toExpression())), new OptionalNode(new BreakStatement(-34)).toStatement() }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     IfElseStatement additionalTest = new IfElseStatement(-34, new InvocationExpression(-34, new MemberReferenceExpression(-34, new IdentifierExpressionBackReference("input").toExpression(), "equals", new AstType[0]), new Expression[] { new NamedNode("stringValue", new PrimitiveExpression(-34, "$any$")).toExpression() }), new BlockStatement(new Statement[] { new ExpressionStatement(new com.strobel.decompiler.languages.java.ast.AssignmentExpression(new IdentifierExpressionBackReference("tableSwitchInput").toExpression(), new NamedNode("tableSwitchCaseValue", new PrimitiveExpression(-34, PrimitiveExpression.ANY_VALUE)).toExpression())), new OptionalNode(new BreakStatement(-34)).toStatement() }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     caseBody.add(test);
     caseBody.add(new Repeat(additionalTest).toStatement());
     caseBody.add(new BreakStatement(-34));
     
     CASE_BODY_PATTERN = caseBody;
   }
   
 
 
 
   public Void visitSwitchStatement(SwitchStatement node, Void data)
   {
     super.visitSwitchStatement(node, data);
     
     Statement previous = node.getPreviousStatement();
     
     if ((previous == null) || (previous.isNull())) {
       return null;
     }
     
     Statement next = node.getNextStatement();
     
     if ((next == null) || (next.isNull())) {
       return null;
     }
     
     if (!(next instanceof SwitchStatement))
     {
 
 
 
 
       next = next.getNextStatement();
       
       if ((next == null) || (next.isNull())) {
         return null;
       }
     }
     
     if (!(next instanceof SwitchStatement)) {
       return null;
     }
     
     Match m1 = TABLE_SWITCH_INPUT.match(previous);
     
     if (!m1.success()) {
       return null;
     }
     
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
     
     if ((!(hashCodeMethod instanceof com.strobel.assembler.metadata.MethodReference)) || (!"java/lang/String".equals(hashCodeMethod.getDeclaringType().getInternalName())))
     {
 
       return null;
     }
     
     final Map<Integer, List<String>> tableInputMap = new java.util.LinkedHashMap();
     
     IdentifierExpression tableSwitchInput = null;
     
     for (SwitchSection section : node.getSwitchSections()) {
       Match m3 = CASE_BODY_PATTERN.match(section.getStatements().firstOrNullObject());
       
       if (!m3.success()) {
         return null;
       }
       
       if (tableSwitchInput == null) {
         tableSwitchInput = (IdentifierExpression)CollectionUtilities.first(m3.get("tableSwitchInput"));
         assert (tableSwitchInput != null);
       }
       
       List<PrimitiveExpression> stringValues = CollectionUtilities.toList(m3.get("stringValue"));
       List<PrimitiveExpression> tableSwitchCaseValues = CollectionUtilities.toList(m3.get("tableSwitchCaseValue"));
       
       if ((stringValues.isEmpty()) || (stringValues.size() != tableSwitchCaseValues.size())) {
         return null;
       }
       
       for (int i = 0; i < stringValues.size(); i++) {
         PrimitiveExpression stringValue = (PrimitiveExpression)stringValues.get(i);
         PrimitiveExpression tableSwitchCaseValue = (PrimitiveExpression)tableSwitchCaseValues.get(i);
         
         if (!(tableSwitchCaseValue.getValue() instanceof Integer)) {
           return null;
         }
         
         Integer k = (Integer)tableSwitchCaseValue.getValue();
         String v = (String)stringValue.getValue();
         
         List<String> list = (List)tableInputMap.get(k);
         
         if (list == null) {
           tableInputMap.put(k, list = new java.util.ArrayList());
         }
         
         list.add(v);
       }
     }
     
     if (tableSwitchInput == null) {
       return null;
     }
     
     SwitchStatement tableSwitch = (SwitchStatement)next;
     
     if (!tableSwitchInput.matches(tableSwitch.getExpression())) {
       return null;
     }
     
     boolean allCasesFound = CollectionUtilities.all(tableSwitch.getSwitchSections(), new com.strobel.core.Predicate()
     {
 
       public boolean test(SwitchSection s)
       {
         (!s.getCaseLabels().isEmpty()) && (CollectionUtilities.all(s.getCaseLabels(), new com.strobel.core.Predicate()
         {
 
           public boolean test(CaseLabel c)
           {
 
             return (c.getExpression().isNull()) || (((c.getExpression() instanceof PrimitiveExpression)) && ((((PrimitiveExpression)c.getExpression()).getValue() instanceof Integer)) && (StringSwitchRewriterTransform.1.this.val$tableInputMap.containsKey(((PrimitiveExpression)c.getExpression()).getValue())));
           }
         }));
       }
     });
     
 
 
 
 
 
     if (!allCasesFound) {
       return null;
     }
     
     AstNode newInput = (AstNode)CollectionUtilities.first(m2.get("target"));
     
     newInput.remove();
     tableSwitch.getExpression().replaceWith(newInput);
     
     for (Iterator i$ = tableSwitch.getSwitchSections().iterator(); i$.hasNext();) { s = (SwitchSection)i$.next();
       for (CaseLabel c : s.getCaseLabels())
         if ((c.getExpression() != null) && (!c.getExpression().isNull()))
         {
 
 
           PrimitiveExpression test = (PrimitiveExpression)c.getExpression();
           Integer testValue = (Integer)test.getValue();
           List<String> stringValues = (List)tableInputMap.get(testValue);
           
           assert ((stringValues != null) && (!stringValues.isEmpty()));
           
           test.setValue(stringValues.get(0));
           
           CaseLabel insertionPoint = c;
           
           for (int i = 1; i < stringValues.size(); i++) {
             CaseLabel newLabel = new CaseLabel(new PrimitiveExpression(-34, stringValues.get(i)));
             s.getCaseLabels().insertAfter(insertionPoint, newLabel);
             insertionPoint = newLabel;
           }
         }
     }
     SwitchSection s;
     node.remove();
     previous.remove();
     
     return null;
   }
 }


