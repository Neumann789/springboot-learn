 package com.strobel.decompiler.ast;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum AstOptimizationStep
 {
   RemoveRedundantCode, 
   ReduceBranchInstructionSet, 
   InlineVariables, 
   CopyPropagation, 
   RewriteFinallyBlocks, 
   SplitToMovableBlocks, 
   RemoveUnreachableBlocks, 
   TypeInference, 
   RemoveInnerClassInitSecurityChecks, 
   PreProcessShortCircuitAssignments, 
   SimplifyShortCircuit, 
   JoinBranchConditions, 
   SimplifyTernaryOperator, 
   JoinBasicBlocks, 
   SimplifyLogicalNot, 
   SimplifyShiftOperations, 
   SimplifyLoadAndStore, 
   TransformObjectInitializers, 
   TransformArrayInitializers, 
   InlineConditionalAssignments, 
   MakeAssignmentExpressions, 
   IntroducePostIncrement, 
   InlineLambdas, 
   InlineVariables2, 
   MergeDisparateObjectInitializations, 
   FindLoops, 
   FindConditions, 
   FlattenNestedMovableBlocks, 
   RemoveRedundantCode2, 
   GotoRemoval, 
   DuplicateReturns, 
   ReduceIfNesting, 
   GotoRemoval2, 
   ReduceComparisonInstructionSet, 
   RecombineVariables, 
   RemoveRedundantCode3, 
   CleanUpTryBlocks, 
   InlineVariables3, 
   TypeInference2, 
   None;
   
   private AstOptimizationStep() {}
   public boolean isBlockLevelOptimization() { switch (this) {
     case RemoveInnerClassInitSecurityChecks: 
     case SimplifyShortCircuit: 
     case SimplifyTernaryOperator: 
     case JoinBasicBlocks: 
     case SimplifyLogicalNot: 
     case SimplifyShiftOperations: 
     case SimplifyLoadAndStore: 
     case TransformObjectInitializers: 
     case TransformArrayInitializers: 
     case MakeAssignmentExpressions: 
     case IntroducePostIncrement: 
     case InlineLambdas: 
     case InlineVariables2: 
     case MergeDisparateObjectInitializations: 
       return true;
     }
     
     return false;
   }
 }


