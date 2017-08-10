 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Comparer;
 import com.strobel.core.Predicate;
 import com.strobel.core.StrongBox;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class PatternMatching
 {
   private PatternMatching()
   {
     throw ContractUtils.unreachable();
   }
   
   public static boolean match(Node node, AstCode code) {
     return ((node instanceof Expression)) && (((Expression)node).getCode() == code);
   }
   
   public static boolean matchLeaveHandler(Node node)
   {
     return (match(node, AstCode.Leave)) || (match(node, AstCode.EndFinally));
   }
   
   public static <T> boolean matchGetOperand(Node node, AstCode code, StrongBox<? super T> operand)
   {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       
       if ((expression.getCode() == code) && (expression.getArguments().isEmpty()))
       {
 
         operand.set(expression.getOperand());
         return true;
       }
     }
     
     operand.set(null);
     return false;
   }
   
   public static <T> boolean matchGetOperand(Node node, AstCode code, Class<T> operandType, StrongBox<? super T> operand) {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       
       if ((expression.getCode() == code) && (expression.getArguments().isEmpty()) && (operandType.isInstance(expression.getOperand())))
       {
 
 
         operand.set(expression.getOperand());
         return true;
       }
     }
     
     operand.set(null);
     return false;
   }
   
   public static boolean matchGetArguments(Node node, AstCode code, List<Expression> arguments) {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       
       if (expression.getCode() == code) {
         assert (expression.getOperand() == null);
         arguments.clear();
         arguments.addAll(expression.getArguments());
         return true;
       }
     }
     
     arguments.clear();
     return false;
   }
   
   public static <T> boolean matchGetArguments(Node node, AstCode code, StrongBox<? super T> operand, List<Expression> arguments) {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       
       if (expression.getCode() == code) {
         operand.set(expression.getOperand());
         arguments.clear();
         arguments.addAll(expression.getArguments());
         return true;
       }
     }
     
     operand.set(null);
     arguments.clear();
     return false;
   }
   
   public static boolean matchGetArgument(Node node, AstCode code, StrongBox<Expression> argument) {
     ArrayList<Expression> arguments = new ArrayList(1);
     
     if ((matchGetArguments(node, code, arguments)) && (arguments.size() == 1)) {
       argument.set(arguments.get(0));
       return true;
     }
     
     argument.set(null);
     return false;
   }
   
 
 
 
 
   public static <T> boolean matchGetArgument(Node node, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument)
   {
     ArrayList<Expression> arguments = new ArrayList(1);
     
     if ((matchGetArguments(node, code, operand, arguments)) && (arguments.size() == 1)) {
       argument.set(arguments.get(0));
       return true;
     }
     
     argument.set(null);
     return false;
   }
   
 
 
 
 
 
   public static <T> boolean matchGetArguments(Node node, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument1, StrongBox<Expression> argument2)
   {
     ArrayList<Expression> arguments = new ArrayList(2);
     
     if ((matchGetArguments(node, code, operand, arguments)) && (arguments.size() == 2)) {
       argument1.set(arguments.get(0));
       argument2.set(arguments.get(1));
       return true;
     }
     
     argument1.set(null);
     argument2.set(null);
     return false;
   }
   
 
 
 
   public static <T> boolean matchSingle(Block block, AstCode code, StrongBox<? super T> operand)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() == 1) && (matchGetOperand((Node)body.get(0), code, operand)))
     {
 
       return true;
     }
     
     operand.set(null);
     return false;
   }
   
 
 
 
 
   public static <T> boolean matchSingle(Block block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() == 1) && (matchGetArgument((Node)body.get(0), code, operand, argument)))
     {
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     return false;
   }
   
   public static boolean matchNullOrEmpty(Block block) {
     return (block == null) || (block.getBody().size() == 0);
   }
   
   public static boolean matchEmptyReturn(Node node) {
     Node target = node;
     
     if (((node instanceof Block)) || ((node instanceof BasicBlock))) {
       List<Node> body = (node instanceof Block) ? ((Block)node).getBody() : ((BasicBlock)node).getBody();
       
 
       if (body.size() != 1) {
         return false;
       }
       
       target = (Node)body.get(0);
     }
     
     if ((target instanceof Expression)) {
       Expression e = (Expression)target;
       
       return (e.getCode() == AstCode.Return) && (e.getArguments().isEmpty());
     }
     
 
     return false;
   }
   
 
 
 
 
   public static <T> boolean matchSingle(BasicBlock block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() == 2) && ((body.get(0) instanceof Label)) && (matchGetArgument((Node)body.get(1), code, operand, argument)))
     {
 
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     return false;
   }
   
 
 
 
 
 
   public static <T> boolean matchSingleAndBreak(BasicBlock block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument, StrongBox<Label> label)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() == 3) && ((body.get(0) instanceof Label)) && (matchGetArgument((Node)body.get(1), code, operand, argument)) && (matchGetOperand((Node)body.get(2), AstCode.Goto, label)))
     {
 
 
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     label.set(null);
     return false;
   }
   
   public static boolean matchSimpleBreak(BasicBlock block, StrongBox<Label> label) {
     List<Node> body = block.getBody();
     
     if ((body.size() == 2) && ((body.get(0) instanceof Label)) && (matchGetOperand((Node)body.get(1), AstCode.Goto, label)))
     {
 
 
       return true;
     }
     
     label.set(null);
     return false;
   }
   
   public static boolean matchSimpleBreak(BasicBlock block, Label label) {
     List<Node> body = block.getBody();
     
     if ((body.size() == 2) && ((body.get(0) instanceof Label)) && (match((Node)body.get(1), AstCode.Goto)) && (((Expression)body.get(1)).getOperand() == label))
     {
 
 
 
       return true;
     }
     
     return false;
   }
   
 
 
 
 
 
 
   public static boolean matchAssignmentAndConditionalBreak(BasicBlock block, StrongBox<Expression> assignedValue, StrongBox<Expression> condition, StrongBox<Label> trueLabel, StrongBox<Label> falseLabel, StrongBox<Expression> equivalentLoad)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 4) && ((body.get(0) instanceof Label)) && ((body.get(body.size() - 3) instanceof Expression)) && (matchLastAndBreak(block, AstCode.IfTrue, trueLabel, condition, falseLabel)))
     {
 
 
 
       Expression e = (Expression)body.get(body.size() - 3);
       
       if (match(e, AstCode.Store)) {
         assignedValue.set(e.getArguments().get(0));
         equivalentLoad.set(new Expression(AstCode.Load, e.getOperand(), e.getOffset(), new Expression[0]));
         return true;
       }
       
       if (match(e, AstCode.PutStatic)) {
         assignedValue.set(e.getArguments().get(0));
         equivalentLoad.set(new Expression(AstCode.GetStatic, e.getOperand(), e.getOffset(), new Expression[0]));
         return true;
       }
       
       if (match(e, AstCode.StoreElement)) {
         assignedValue.set(e.getArguments().get(2));
         Expression arg0 = ((Expression)e.getArguments().get(0)).clone();
         Expression arg1 = ((Expression)e.getArguments().get(1)).clone();
         equivalentLoad.set(new Expression(AstCode.LoadElement, null, arg0.getOffset(), new Expression[] { arg0, arg1 }));
         return true;
       }
       
       if (match(e, AstCode.PutField)) {
         assignedValue.set(e.getArguments().get(1));
         Expression arg0 = ((Expression)e.getArguments().get(0)).clone();
         equivalentLoad.set(new Expression(AstCode.GetField, null, arg0.getOffset(), new Expression[] { arg0 }));
         return true;
       }
     }
     
     assignedValue.set(null);
     condition.set(null);
     trueLabel.set(null);
     falseLabel.set(null);
     return false;
   }
   
   public static boolean matchAssignment(Node node, StrongBox<Expression> assignedValue) {
     if ((match(node, AstCode.Store)) || (match(node, AstCode.PutStatic))) {
       assignedValue.set(((Expression)node).getArguments().get(0));
       return true;
     }
     
     if (match(node, AstCode.StoreElement)) {
       assignedValue.set(((Expression)node).getArguments().get(2));
       return true;
     }
     
     if (match(node, AstCode.PutField)) {
       assignedValue.set(((Expression)node).getArguments().get(1));
       return true;
     }
     
     assignedValue.set(null);
     return false;
   }
   
   public static boolean matchAssignment(Node node, StrongBox<Expression> assignedValue, StrongBox<Expression> equivalentLoad) {
     if ((node instanceof Expression)) {
       Expression e = (Expression)node;
       
       if (match(e, AstCode.Store)) {
         assignedValue.set(e.getArguments().get(0));
         equivalentLoad.set(new Expression(AstCode.Load, e.getOperand(), e.getOffset(), new Expression[0]));
         return true;
       }
       
       if (match(e, AstCode.PutStatic)) {
         assignedValue.set(e.getArguments().get(0));
         equivalentLoad.set(new Expression(AstCode.GetStatic, e.getOperand(), e.getOffset(), new Expression[0]));
         return true;
       }
       
       if (match(e, AstCode.StoreElement)) {
         assignedValue.set(e.getArguments().get(2));
         Expression arg0 = ((Expression)e.getArguments().get(0)).clone();
         Expression arg1 = ((Expression)e.getArguments().get(1)).clone();
         equivalentLoad.set(new Expression(AstCode.LoadElement, null, arg0.getOffset(), new Expression[] { arg0, arg1 }));
         return true;
       }
       
       if (match(e, AstCode.PutField)) {
         assignedValue.set(e.getArguments().get(1));
         Expression arg0 = ((Expression)e.getArguments().get(0)).clone();
         equivalentLoad.set(new Expression(AstCode.GetField, e.getOperand(), arg0.getOffset(), new Expression[] { arg0 }));
         return true;
       }
     }
     
     assignedValue.set(null);
     return false;
   }
   
   public static boolean matchLast(BasicBlock block, AstCode code) {
     List<Node> body = block.getBody();
     
     return (body.size() >= 1) && (match((Node)body.get(body.size() - 1), code));
   }
   
   public static boolean matchLast(Block block, AstCode code)
   {
     List<Node> body = block.getBody();
     
     return (body.size() >= 1) && (match((Node)body.get(body.size() - 1), code));
   }
   
 
 
 
 
   public static <T> boolean matchLast(BasicBlock block, AstCode code, StrongBox<? super T> operand)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 1) && (matchGetOperand((Node)body.get(body.size() - 1), code, operand)))
     {
 
       return true;
     }
     
     operand.set(null);
     return false;
   }
   
 
 
 
   public static <T> boolean matchLast(Block block, AstCode code, StrongBox<? super T> operand)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 1) && (matchGetOperand((Node)body.get(body.size() - 1), code, operand)))
     {
 
       return true;
     }
     
     operand.set(null);
     return false;
   }
   
 
 
 
 
   public static <T> boolean matchLast(Block block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 1) && (matchGetArgument((Node)body.get(body.size() - 1), code, operand, argument)))
     {
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     return false;
   }
   
 
 
 
 
   public static <T> boolean matchLast(BasicBlock block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 1) && (matchGetArgument((Node)body.get(body.size() - 1), code, operand, argument)))
     {
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     return false;
   }
   
 
 
 
 
 
   public static <T> boolean matchLastAndBreak(BasicBlock block, AstCode code, StrongBox<? super T> operand, StrongBox<Expression> argument, StrongBox<Label> label)
   {
     List<Node> body = block.getBody();
     
     if ((body.size() >= 2) && (matchGetArgument((Node)body.get(body.size() - 2), code, operand, argument)) && (matchGetOperand((Node)body.get(body.size() - 1), AstCode.Goto, label)))
     {
 
 
       return true;
     }
     
     operand.set(null);
     argument.set(null);
     label.set(null);
     return false;
   }
   
   public static boolean matchThis(Node node) {
     StrongBox<Variable> operand = new StrongBox();
     
     return (matchGetOperand(node, AstCode.Load, operand)) && (((Variable)operand.get()).isParameter()) && (((Variable)operand.get()).getOriginalParameter().getPosition() == -1);
   }
   
 
   public static boolean matchLoadAny(Node node, Iterable<Variable> expectedVariables)
   {
     CollectionUtilities.any(expectedVariables, new Predicate()
     {
 
       public boolean test(Variable variable)
       {
         return PatternMatching.matchLoad(this.val$node, variable);
       }
     });
   }
   
   public static boolean matchLoad(Node node, StrongBox<Variable> variable)
   {
     return matchGetOperand(node, AstCode.Load, variable);
   }
   
   public static boolean matchStore(Node node, StrongBox<Variable> variable, StrongBox<Expression> argument) {
     return matchGetArgument(node, AstCode.Store, variable, argument);
   }
   
   public static boolean matchStore(Node node, StrongBox<Variable> variable, List<Expression> argument) {
     return matchGetArguments(node, AstCode.Store, variable, argument);
   }
   
   public static boolean matchLoadOrRet(Node node, StrongBox<Variable> variable) {
     return (matchGetOperand(node, AstCode.Load, variable)) || (matchGetOperand(node, AstCode.Ret, variable));
   }
   
   public static boolean matchLoad(Node node, Variable expectedVariable)
   {
     StrongBox<Variable> operand = new StrongBox();
     
     return (matchGetOperand(node, AstCode.Load, operand)) && (Comparer.equals(operand.get(), expectedVariable));
   }
   
   public static boolean matchStore(Node node, Variable expectedVariable)
   {
     return (match(node, AstCode.Store)) && (Comparer.equals(((Expression)node).getOperand(), expectedVariable));
   }
   
   public static boolean matchStore(Node node, Variable expectedVariable, StrongBox<Expression> value)
   {
     StrongBox<Variable> v = new StrongBox();
     
     if ((matchGetArgument(node, AstCode.Store, v, value)) && (Comparer.equals(((Expression)node).getOperand(), expectedVariable)) && (v.get() == expectedVariable))
     {
 
 
       return true;
     }
     
     value.set(null);
     return false;
   }
   
   public static boolean matchLoad(Node node, Variable expectedVariable, StrongBox<Expression> argument) {
     StrongBox<Variable> operand = new StrongBox();
     
     return (matchGetArgument(node, AstCode.Load, operand, argument)) && (Comparer.equals(operand.get(), expectedVariable));
   }
   
   public static boolean matchLoadStore(Node node, Variable expectedVariable, StrongBox<Variable> targetVariable)
   {
     StrongBox<Expression> temp = new StrongBox();
     
     if ((matchGetArgument(node, AstCode.Store, targetVariable, temp)) && (matchLoad((Node)temp.get(), expectedVariable)))
     {
 
       return true;
     }
     
     targetVariable.set(null);
     return false;
   }
   
   public static boolean matchLoadStoreAny(Node node, Iterable<Variable> expectedVariables, StrongBox<Variable> targetVariable) {
     for (Variable variable : (Iterable)VerifyArgument.notNull(expectedVariables, "expectedVariables")) {
       if (matchLoadStore(node, variable, targetVariable)) {
         return true;
       }
     }
     
     return false;
   }
   
   public static boolean matchBooleanComparison(Node node, StrongBox<Expression> argument, StrongBox<Boolean> comparand) {
     List<Expression> a = new ArrayList(2);
     
     if ((matchGetArguments(node, AstCode.CmpEq, a)) || (matchGetArguments(node, AstCode.CmpNe, a))) {
       comparand.set(matchBooleanConstant((Node)a.get(0)));
       
       if (comparand.get() == null) {
         comparand.set(matchBooleanConstant((Node)a.get(1)));
         
         if (comparand.get() == null) {
           return false;
         }
         
         argument.set(a.get(0));
       }
       else {
         argument.set(a.get(1));
       }
       
       comparand.set(Boolean.valueOf(match(node, AstCode.CmpEq) ^ comparand.get() == Boolean.FALSE));
       return true;
     }
     
     return false;
   }
   
   public static boolean matchComparison(Node node, StrongBox<Expression> left, StrongBox<Expression> right) {
     if ((node instanceof Expression)) {
       Expression e = (Expression)node;
       
       switch (e.getCode()) {
       case CmpEq: 
       case CmpNe: 
       case CmpLt: 
       case CmpGt: 
       case CmpLe: 
       case CmpGe: 
         List<Expression> arguments = e.getArguments();
         left.set(arguments.get(0));
         right.set(arguments.get(1));
         return true;
       }
       
     }
     
     left.set(null);
     right.set(null);
     return false;
   }
   
   public static boolean matchSimplifiableComparison(Node node) {
     if ((node instanceof Expression)) {
       Expression e = (Expression)node;
       
       switch (e.getCode()) {
       case CmpEq: 
       case CmpNe: 
       case CmpLt: 
       case CmpGt: 
       case CmpLe: 
       case CmpGe: 
         Expression comparisonArgument = (Expression)e.getArguments().get(0);
         
         switch (comparisonArgument.getCode()) {
         case __LCmp: 
         case __FCmpL: 
         case __FCmpG: 
         case __DCmpL: 
         case __DCmpG: 
           Expression constantArgument = (Expression)e.getArguments().get(1);
           StrongBox<Integer> comparand = new StrongBox();
           
           return (matchGetOperand(constantArgument, AstCode.LdC, Integer.class, comparand)) && (((Integer)comparand.get()).intValue() == 0);
         }
         
         break;
       }
       
     }
     return false;
   }
   
   public static boolean matchReversibleComparison(Node node) {
     if (match(node, AstCode.LogicalNot)) {
       switch (((Expression)((Expression)node).getArguments().get(0)).getCode()) {
       case CmpEq: 
       case CmpNe: 
       case CmpLt: 
       case CmpGt: 
       case CmpLe: 
       case CmpGe: 
         return true;
       }
       
     }
     return false;
   }
   
   public static boolean matchReturnOrThrow(Node node) {
     return (match(node, AstCode.Return)) || (match(node, AstCode.AThrow));
   }
   
   public static Boolean matchTrue(Node node)
   {
     return Boolean.valueOf(Boolean.TRUE.equals(matchBooleanConstant(node)));
   }
   
   public static Boolean matchFalse(Node node) {
     return Boolean.valueOf(Boolean.FALSE.equals(matchBooleanConstant(node)));
   }
   
   public static Boolean matchBooleanConstant(Node node) {
     if (match(node, AstCode.LdC)) {
       Object operand = ((Expression)node).getOperand();
       
       if ((operand instanceof Boolean)) {
         return (Boolean)operand;
       }
       
       if (((operand instanceof Number)) && (!(operand instanceof Float)) && (!(operand instanceof Double))) {
         long longValue = ((Number)operand).longValue();
         
         if (longValue == 0L) {
           return Boolean.FALSE;
         }
         
         if (longValue == 1L) {
           return Boolean.TRUE;
         }
       }
     }
     
     return null;
   }
   
   public static Character matchCharacterConstant(Node node) {
     if (match(node, AstCode.LdC)) {
       Object operand = ((Expression)node).getOperand();
       
       if ((operand instanceof Character)) {
         return (Character)operand;
       }
       
       if (((operand instanceof Number)) && (!(operand instanceof Float)) && (!(operand instanceof Double))) {
         long longValue = ((Number)operand).longValue();
         
         if ((longValue >= 0L) && (longValue <= 65535L)) {
           return Character.valueOf((char)(int)longValue);
         }
       }
     }
     
     return null;
   }
   
   public static boolean matchBooleanConstant(Node node, StrongBox<Boolean> value) {
     Boolean booleanConstant = matchBooleanConstant(node);
     
     if (booleanConstant != null) {
       value.set(booleanConstant);
       return true;
     }
     
     value.set(null);
     return false;
   }
   
   public static boolean matchCharacterConstant(Node node, StrongBox<Character> value) {
     Character characterConstant = matchCharacterConstant(node);
     
     if (characterConstant != null) {
       value.set(characterConstant);
       return true;
     }
     
     value.set(null);
     return false;
   }
   
   public static boolean matchUnconditionalBranch(Node node) {
     return ((node instanceof Expression)) && (((Expression)node).getCode().isUnconditionalControlFlow());
   }
   
   public static boolean matchLock(List<Node> body, int position, StrongBox<LockInfo> result)
   {
     VerifyArgument.notNull(body, "body");
     VerifyArgument.notNull(result, "result");
     
     result.set(null);
     
     int head = position;
     
     if ((head < 0) || (head >= body.size())) {
       return false;
     }
     
     List<Expression> a = new ArrayList();
     
     Label leadingLabel;
     if ((body.get(head) instanceof Label)) {
       Label leadingLabel = (Label)body.get(head);
       head++;
     }
     else {
       leadingLabel = null;
     }
     
     if (head >= body.size()) {
       return false;
     }
     
     if (matchGetArguments((Node)body.get(head), AstCode.MonitorEnter, a)) {
       if (!match((Node)a.get(0), AstCode.Load)) {
         return false;
       }
       
       result.set(new LockInfo(leadingLabel, (Expression)body.get(head)));
       return true;
     }
     
     StrongBox<Variable> v = new StrongBox();
     
 
 
 
 
 
     if ((head < body.size() - 1) && (matchGetArguments((Node)body.get(head), AstCode.Store, v, a)))
     {
 
       Variable lockVariable = (Variable)v.get();
       Expression lockInit = (Expression)a.get(0);
       Expression lockStore = (Expression)body.get(head++);
       Expression lockStoreCopy;
       Expression lockStoreCopy; if (matchLoadStore((Node)body.get(head), lockVariable, v)) {
         lockStoreCopy = (Expression)body.get(head++);
       }
       else {
         lockStoreCopy = null;
       }
       
       if ((head < body.size()) && (matchGetArguments((Node)body.get(head), AstCode.MonitorEnter, a)))
       {
 
         if (!matchLoad((Node)a.get(0), lockVariable)) {
           if ((matchGetOperand(lockInit, AstCode.Load, v)) && (matchLoad((Node)a.get(0), (Variable)v.get())))
           {
 
             lockStoreCopy = lockStore;
             lockStore = null;
             lockInit = null;
           }
           else {
             return false;
           }
         }
         
         result.set(new LockInfo(leadingLabel, lockInit, lockStore, lockStoreCopy, (Expression)body.get(head)));
         
 
 
 
 
 
 
 
 
         return true;
       }
     }
     
     return false;
   }
   
   public static boolean matchUnlock(Node e, LockInfo lockInfo) {
     if (lockInfo == null) {
       return false;
     }
     
     StrongBox<Expression> a = new StrongBox();
     
     return (matchGetArgument(e, AstCode.MonitorExit, a)) && ((matchLoad((Node)a.get(), lockInfo.lock)) || ((lockInfo.lockCopy != null) && (matchLoad((Node)a.get(), lockInfo.lockCopy))));
   }
   
 
   public static boolean matchVariableMutation(Node node, Variable variable)
   {
     VerifyArgument.notNull(node, "node");
     VerifyArgument.notNull(variable, "variable");
     
     if ((node instanceof Expression)) {
       Expression e = (Expression)node;
       
       switch (e.getCode()) {
       case Store: 
       case Inc: 
         return e.getOperand() == variable;
       
       case PreIncrement: 
       case PostIncrement: 
         return matchLoad((Node)CollectionUtilities.single(e.getArguments()), variable);
       }
       
     }
     return false;
   }
 }


