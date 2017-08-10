 package com.strobel.decompiler.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.MutableInteger;
 import com.strobel.core.Predicate;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.util.ContractUtils;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class Inlining
 {
   private final DecompilerContext _context;
   private final Block _method;
   private final boolean _aggressive;
   final Map<Variable, MutableInteger> loadCounts;
   final Map<Variable, MutableInteger> storeCounts;
   final Map<Variable, List<Expression>> loads;
   
   public Inlining(DecompilerContext context, Block method)
   {
     this(context, method, false);
   }
   
   public Inlining(DecompilerContext context, Block method, boolean aggressive) {
     this._context = context;
     this._method = method;
     this._aggressive = aggressive;
     
     this.loadCounts = new DefaultMap(MutableInteger.SUPPLIER);
     this.storeCounts = new DefaultMap(MutableInteger.SUPPLIER);
     this.loads = new DefaultMap(CollectionUtilities.listFactory());
     
     analyzeMethod();
   }
   
 
   final void analyzeMethod()
   {
     this.loadCounts.clear();
     this.storeCounts.clear();
     
     analyzeNode(this._method);
   }
   
   final void analyzeNode(Node node) {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       Object operand = expression.getOperand();
       
       if ((operand instanceof Variable)) {
         AstCode code = expression.getCode();
         Variable localVariable = (Variable)operand;
         
         if (code == AstCode.Load) {
           increment(this.loadCounts, localVariable);
           ((List)this.loads.get(localVariable)).add(expression);
         }
         else if (code == AstCode.Store) {
           increment(this.storeCounts, localVariable);
         }
         else if (code == AstCode.Inc) {
           increment(this.loadCounts, localVariable);
           increment(this.storeCounts, localVariable);
           ((List)this.loads.get(localVariable)).add(expression);
         }
         else if (code == AstCode.PostIncrement) {
           increment(this.loadCounts, localVariable);
           increment(this.storeCounts, localVariable);
           ((List)this.loads.get(localVariable)).add(expression);
         }
         else if (code == AstCode.Ret) {
           increment(this.loadCounts, localVariable);
           ((List)this.loads.get(localVariable)).add(expression);
         }
         else {
           throw new IllegalStateException("Unexpected AST op code: " + code.getName());
         }
       }
       
       for (Expression argument : expression.getArguments()) {
         analyzeNode(argument);
       }
     }
     else {
       if ((node instanceof CatchBlock)) {
         CatchBlock catchBlock = (CatchBlock)node;
         Variable exceptionVariable = catchBlock.getExceptionVariable();
         
         if (exceptionVariable != null) {
           increment(this.storeCounts, exceptionVariable);
         }
       }
       
       for (Node child : node.getChildren()) {
         analyzeNode(child);
       }
     }
   }
   
 
 
 
   final boolean inlineAllVariables()
   {
     boolean modified = false;
     
     for (Block block : this._method.getSelfAndChildrenRecursive(Block.class)) {
       modified |= inlineAllInBlock(block);
     }
     
     return modified;
   }
   
   final boolean inlineAllInBlock(Block block) {
     boolean modified = false;
     
     List<Node> body = block.getBody();
     StrongBox<Variable> tempVariable = new StrongBox();
     StrongBox<Expression> tempExpression = new StrongBox();
     
     if (((block instanceof CatchBlock)) && (!body.isEmpty())) {
       CatchBlock catchBlock = (CatchBlock)block;
       Variable v = catchBlock.getExceptionVariable();
       
       if ((v != null) && (v.isGenerated()) && (count(this.storeCounts, v) == 1) && (count(this.loadCounts, v) <= 1))
       {
 
 
 
         if ((PatternMatching.matchGetArgument((Node)body.get(0), AstCode.Store, tempVariable, tempExpression)) && (PatternMatching.matchLoad((Node)tempExpression.get(), v)))
         {
 
           body.remove(0);
           catchBlock.setExceptionVariable((Variable)tempVariable.get());
           modified = true;
         }
       }
     }
     
     for (int i = 0; i < body.size() - 1;) {
       Node node = (Node)body.get(i);
       
       if ((PatternMatching.matchGetArgument(node, AstCode.Store, tempVariable, tempExpression)) && (inlineOneIfPossible(block.getBody(), i, this._aggressive)))
       {
 
         modified = true;
         i = 0;
       }
       else {
         i++;
       }
     }
     
     for (Node node : body) {
       if ((node instanceof BasicBlock)) {
         modified |= inlineAllInBasicBlock((BasicBlock)node);
       }
     }
     
     return modified;
   }
   
   final boolean inlineAllInBasicBlock(BasicBlock basicBlock) {
     boolean modified = false;
     
     List<Node> body = basicBlock.getBody();
     StrongBox<Variable> tempVariable = new StrongBox();
     StrongBox<Expression> tempExpression = new StrongBox();
     
     for (int i = 0; i < body.size();) {
       Node node = (Node)body.get(i);
       
       if ((PatternMatching.matchGetArgument(node, AstCode.Store, tempVariable, tempExpression)) && (inlineOneIfPossible(basicBlock.getBody(), i, this._aggressive)))
       {
 
         modified = true;
         i = Math.max(0, i - 1);
       }
       else {
         i++;
       }
     }
     
     return modified;
   }
   
   final boolean inlineIfPossible(List<Node> body, MutableInteger position) {
     int currentPosition = position.getValue();
     
     if (inlineOneIfPossible(body, currentPosition, true)) {
       position.setValue(currentPosition - inlineInto(body, currentPosition, this._aggressive));
       return true;
     }
     
     return false;
   }
   
   final int inlineInto(List<Node> body, int position, boolean aggressive) {
     if (position >= body.size()) {
       return 0;
     }
     
     int count = 0;
     int p = position;
     for (;;) {
       p--; if (p < 0) break;
       Node node = (Node)body.get(p);
       
       if (!(node instanceof Expression)) break;
       Expression e = (Expression)node;
       
       if (e.getCode() != AstCode.Store) {
         break;
       }
       
       if (inlineOneIfPossible(body, p, aggressive)) {
         count++;
       }
     }
     
 
 
 
 
     return count;
   }
   
 
 
   final boolean inlineIfPossible(Variable variable, Expression inlinedExpression, Node next, boolean aggressive)
   {
     int storeCount = count(this.storeCounts, variable);
     int loadCount = count(this.loadCounts, variable);
     
     if ((storeCount != 1) || (loadCount > 1)) {
       return false;
     }
     
     if (!canInline(aggressive, variable)) {
       return false;
     }
     
     Node n = next;
     
     if ((n instanceof Condition)) {
       n = ((Condition)n).getCondition();
     }
     else if ((n instanceof Loop)) {
       n = ((Loop)n).getCondition();
     }
     
     if (!(n instanceof Expression)) {
       return false;
     }
     
     StrongBox<Variable> v = new StrongBox();
     StrongBox<Expression> parent = new StrongBox();
     MutableInteger position = new MutableInteger();
     
     if ((PatternMatching.matchStore(inlinedExpression, v, parent)) && (PatternMatching.match((Node)parent.value, AstCode.InitArray)) && ((PatternMatching.match(n, AstCode.LoadElement)) || (PatternMatching.match(n, AstCode.StoreElement))))
     {
 
 
 
 
 
       return false;
     }
     
     if (findLoadInNext((Expression)n, variable, inlinedExpression, parent, position) == Boolean.TRUE) {
       if ((!aggressive) && (!variable.isGenerated()) && ((!notFromMetadata(variable)) || (!PatternMatching.matchReturnOrThrow(n))) && (!nonAggressiveInlineInto((Expression)n, (Expression)parent.get(), inlinedExpression)))
       {
 
 
 
         return false;
       }
       
       List<Expression> parentArguments = ((Expression)parent.get()).getArguments();
       Map<Expression, Expression> parentLookup = new IdentityHashMap();
       
       for (Iterator i$ = next.getSelfAndChildrenRecursive(Expression.class).iterator(); i$.hasNext();) { node = (Expression)i$.next();
         for (Expression child : node.getArguments()) {
           parentLookup.put(child, node);
         }
       }
       Expression node;
       List<Expression> nestedAssignments = inlinedExpression.getSelfAndChildrenRecursive(Expression.class, new Predicate()
       {
 
         public boolean test(Expression node)
         {
           return node.getCode() == AstCode.Store;
         }
       });
       
 
 
 
 
 
 
 
       for (Iterator i$ = nestedAssignments.iterator(); i$.hasNext();) { assignment = (Expression)i$.next();
         lastParent = (Expression)parentArguments.get(position.getValue());
         
         for (Expression e : getParents((Expression)n, parentLookup, (Expression)parentArguments.get(position.getValue()))) { boolean lastParentFound;
           if (e.getCode().isWriteOperation()) {
             lastParentFound = false;
             
             for (Expression a : e.getArguments()) {
               if (lastParentFound) {
                 if (AstOptimizer.references(a, (Variable)assignment.getOperand())) {
                   return false;
                 }
               }
               else if (a == lastParent) {
                 lastParentFound = true;
               }
             }
           }
           lastParent = e;
         }
       }
       
       Expression assignment;
       
       Expression lastParent;
       inlinedExpression.getRanges().addAll(((Expression)parentArguments.get(position.getValue())).getRanges());
       
 
 
       parentArguments.set(position.getValue(), inlinedExpression);
       
       return true;
     }
     
     return false;
   }
   
   private boolean notFromMetadata(Variable variable) {
     return (variable.isGenerated()) || ((!variable.isParameter()) && (!variable.getOriginalVariable().isFromMetadata()));
   }
   
 
 
 
 
   private boolean nonAggressiveInlineInto(Expression next, Expression parent, Expression inlinedExpression)
   {
     if (inlinedExpression.getCode() == AstCode.DefaultValue) {
       return true;
     }
     
     switch (next.getCode()) {
     case Return: 
     case IfTrue: 
     case Switch: 
       List<Expression> arguments = next.getArguments();
       return (arguments.size() == 1) && (arguments.get(0) == parent);
     
 
     case DefaultValue: 
       return true;
     }
     
     
     return false;
   }
   
 
 
 
 
 
 
 
   final Boolean findLoadInNext(Expression expression, Variable variable, Expression expressionBeingMoved, StrongBox<Expression> parent, MutableInteger position)
   {
     parent.set(null);
     position.setValue(0);
     
     if (expression == null) {
       return Boolean.FALSE;
     }
     
     AstCode code = expression.getCode();
     List<Expression> arguments = expression.getArguments();
     
     for (int i = 0; i < arguments.size(); i++)
     {
 
 
 
       if ((i == 1) && ((code == AstCode.LogicalAnd) || (code == AstCode.LogicalOr) || (code == AstCode.TernaryOp)))
       {
 
 
 
         return Boolean.FALSE;
       }
       
       Expression argument = (Expression)arguments.get(i);
       
       if ((argument.getCode() == AstCode.Load) && (argument.getOperand() == variable)) {
         switch (code) {
         case PreIncrement: 
         case PostIncrement: 
           if (expressionBeingMoved.getCode() != AstCode.Load) {
             return Boolean.FALSE;
           }
           break;
         }
         parent.set(expression);
         position.setValue(i);
         return Boolean.TRUE;
       }
       
       StrongBox<Expression> tempExpression = new StrongBox();
       StrongBox<Object> tempOperand = new StrongBox();
       
       if ((PatternMatching.matchGetArgument(argument, AstCode.PostIncrement, tempOperand, tempExpression)) && (PatternMatching.matchGetOperand((Node)tempExpression.get(), AstCode.Load, tempOperand)) && (tempOperand.get() == variable))
       {
 
         return Boolean.FALSE;
       }
       
       Boolean result = findLoadInNext(argument, variable, expressionBeingMoved, parent, position);
       
       if (Boolean.TRUE.equals(result)) {
         return result;
       }
     }
     
     if (isSafeForInlineOver(expression, expressionBeingMoved))
     {
 
 
       return null;
     }
     
 
 
 
     return Boolean.FALSE;
   }
   
   static boolean isSafeForInlineOver(Expression expression, Expression expressionBeingMoved) {
     switch (expression.getCode()) {
     case Load: 
       Variable loadedVariable = (Variable)expression.getOperand();
       
       for (Expression potentialStore : expressionBeingMoved.getSelfAndChildrenRecursive(Expression.class)) {
         if (PatternMatching.matchVariableMutation(potentialStore, loadedVariable)) {
           return false;
         }
       }
       
 
 
 
       return true;
     }
     
     
 
 
 
     return hasNoSideEffect(expression);
   }
   
 
   final boolean inlineOneIfPossible(List<Node> body, int position, boolean aggressive)
   {
     StrongBox<Variable> variable = new StrongBox();
     StrongBox<Expression> inlinedExpression = new StrongBox();
     
     Node node = (Node)body.get(position);
     
     if (PatternMatching.matchGetArgument(node, AstCode.Store, variable, inlinedExpression)) {
       Node next = (Node)CollectionUtilities.getOrDefault(body, position + 1);
       Variable v = (Variable)variable.get();
       Expression e = (Expression)inlinedExpression.get();
       Expression current = (Expression)node;
       
       if (inlineIfPossible(v, e, next, aggressive))
       {
 
 
         e.getRanges().addAll(current.getRanges());
         
 
 
 
         body.remove(position);
         return true;
       }
       
       if ((PatternMatching.match(e, AstCode.Store)) && (canInline(true, (Variable)variable.value)) && (count(this.storeCounts, (Variable)variable.value) == 1) && (count(this.loadCounts, (Variable)variable.value) <= 1) && (count(this.loadCounts, (Variable)e.getOperand()) <= 1))
       {
 
 
 
 
 
 
 
 
 
 
         Variable currentVariable = (Variable)variable.value;
         Variable nestedVariable = (Variable)e.getOperand();
         
         if (MetadataHelper.isSameType(currentVariable.getType(), nestedVariable.getType())) {
           List<Expression> currentLoads = (List)this.loads.get(currentVariable);
           List<Expression> nestedLoads = (List)this.loads.get(nestedVariable);
           
           if (nestedVariable.isGenerated()) {
             for (Expression load : nestedLoads) {
               load.setOperand(currentVariable);
               currentLoads.add(load);
               increment(this.loadCounts, currentVariable);
             }
             
             nestedLoads.clear();
           }
           else {
             current.setOperand(nestedVariable);
             
             for (Expression load : currentLoads) {
               load.setOperand(nestedVariable);
               nestedLoads.add(load);
               increment(this.loadCounts, nestedVariable);
             }
             
             currentLoads.clear();
           }
           
           Expression nestedValue = (Expression)CollectionUtilities.single(e.getArguments());
           
           current.getArguments().set(0, nestedValue);
           
           return true;
         }
       }
       
       if (PatternMatching.matchStore(e, variable, inlinedExpression))
       {
 
 
 
 
         Expression loadThisInstead = new Expression(AstCode.Load, v, current.getOffset(), new Expression[0]);
         
         if (inlineIfPossible((Variable)variable.get(), loadThisInstead, next, aggressive))
         {
 
 
 
 
           current.getArguments().set(0, CollectionUtilities.single(e.getArguments()));
           
           ((MutableInteger)this.storeCounts.get(variable.get())).setValue(0);
           ((MutableInteger)this.loadCounts.get(variable.get())).setValue(0);
           
           increment(this.loadCounts, v);
           
           return true;
         }
       }
       
       if ((count(this.loadCounts, v) == 0) && (canInline(aggressive, v)))
       {
 
 
 
 
         if (hasNoSideEffect(e))
         {
 
 
           body.remove(position);
           return true;
         }
         
         if (canBeExpressionStatement(e))
         {
 
 
           e.getRanges().addAll(current.getRanges());
           
 
 
 
           body.set(position, e);
           return true;
         }
       }
     }
     
     return false;
   }
   
   private boolean canInline(boolean aggressive, Variable variable) {
     return aggressive ? notFromMetadata(variable) : variable.isGenerated();
   }
   
 
 
 
 
   final void copyPropagation()
   {
     for (Block block : this._method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       
       StrongBox<Variable> variable = new StrongBox();
       StrongBox<Expression> copiedExpression = new StrongBox();
       
       for (int i = 0; i < body.size(); i++) {
         if ((PatternMatching.matchGetArgument((Node)body.get(i), AstCode.Store, variable, copiedExpression)) && (!((Variable)variable.get()).isParameter()) && (count(this.storeCounts, (Variable)variable.get()) == 1) && (canPerformCopyPropagation((Expression)copiedExpression.get(), (Variable)variable.get())))
         {
 
 
 
 
 
 
 
           List<Expression> arguments = ((Expression)copiedExpression.get()).getArguments();
           Variable[] uninlinedArgs = new Variable[arguments.size()];
           
           for (int j = 0; j < uninlinedArgs.length; j++) {
             Variable newVariable = new Variable();
             
             newVariable.setGenerated(true);
             newVariable.setName(String.format("%s_cp_%d", new Object[] { ((Variable)variable.get()).getName(), Integer.valueOf(j) }));
             
             uninlinedArgs[j] = newVariable;
             
             body.add(i++, new Expression(AstCode.Store, uninlinedArgs[j], -34, new Expression[0]));
           }
           
 
 
 
 
           for (Expression expression : this._method.getSelfAndChildrenRecursive(Expression.class)) {
             if ((expression.getCode().isLoad()) && (expression.getOperand() == variable.get()))
             {
 
 
               expression.setOperand(((Expression)copiedExpression.get()).getOperand());
               
               for (Variable uninlinedArg : uninlinedArgs) {
                 expression.getArguments().add(new Expression(AstCode.Load, uninlinedArg, -34, new Expression[0]));
               }
             }
           }
           
           body.remove(i);
           
           if (uninlinedArgs.length > 0)
           {
 
 
             analyzeMethod();
           }
           
 
 
 
           inlineInto(body, i, this._aggressive);
           
           i -= uninlinedArgs.length + 1;
         }
       }
     }
   }
   
   final boolean canPerformCopyPropagation(Expression expr, Variable copyVariable) {
     switch (expr.getCode()) {
     case Load: 
       Variable v = (Variable)expr.getOperand();
       
       if (v.isParameter())
       {
 
 
         return count(this.storeCounts, v) == 0;
       }
       
 
 
 
 
       return (v.isGenerated()) && (copyVariable.isGenerated()) && (count(this.storeCounts, v) == 1);
     }
     
     
 
 
     return false;
   }
   
 
 
 
 
 
   static boolean hasNoSideEffect(Expression expression)
   {
     switch (expression.getCode())
     {
     case Load: 
     case AConstNull: 
     case LdC: 
       return true;
     
     case Add: 
     case Sub: 
     case Mul: 
     case Div: 
     case Rem: 
     case Shl: 
     case Shr: 
     case UShr: 
     case And: 
     case Or: 
     case Xor: 
       return (hasNoSideEffect((Expression)expression.getArguments().get(0))) && (hasNoSideEffect((Expression)expression.getArguments().get(1)));
     
 
     case Not: 
     case Neg: 
       return hasNoSideEffect((Expression)expression.getArguments().get(0));
     }
     
     
 
 
     return false;
   }
   
   static boolean canBeExpressionStatement(Expression expression)
   {
     switch (expression.getCode()) {
     case PreIncrement: 
     case PostIncrement: 
     case PutStatic: 
     case PutField: 
     case InvokeVirtual: 
     case InvokeSpecial: 
     case InvokeStatic: 
     case InvokeInterface: 
     case InvokeDynamic: 
     case __New: 
     case Store: 
     case StoreElement: 
     case Inc: 
       return true;
     }
     
     return false;
   }
   
   static int count(Map<Variable, MutableInteger> map, Variable variable)
   {
     MutableInteger count = (MutableInteger)map.get(variable);
     return count != null ? count.getValue() : 0;
   }
   
   private static void increment(Map<Variable, MutableInteger> map, Variable variable) {
     MutableInteger count = (MutableInteger)map.get(variable);
     
     if (count == null) {
       map.put(variable, new MutableInteger(1));
     }
     else {
       count.increment();
     }
   }
   
   private static Iterable<Expression> getParents(final Expression scope, final Map<Expression, Expression> parentLookup, Expression node) {
     new Iterable()
     {
       @NotNull
       public final Iterator<Expression> iterator() {
         new Iterator() {
           Expression current = updateCurrent(Inlining.2.this.val$node);
           
           private Expression updateCurrent(Expression node)
           {
             if ((node != null) && (node != Node.NULL)) {
               if (node == Inlining.2.this.val$scope) {
                 return null;
               }
               
               node = (Expression)Inlining.2.this.val$parentLookup.get(node);
               
               return node;
             }
             
             return null;
           }
           
           public final boolean hasNext()
           {
             return this.current != null;
           }
           
           public final Expression next()
           {
             Expression next = this.current;
             
             if (next == null) {
               throw new NoSuchElementException();
             }
             
             this.current = updateCurrent(next);
             return next;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
 }


