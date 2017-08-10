/* AstOptimizationStep - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;

public final class AstOptimizationStep extends Enum
{
    public static final AstOptimizationStep RemoveRedundantCode
	= new AstOptimizationStep("RemoveRedundantCode", 0);
    public static final AstOptimizationStep ReduceBranchInstructionSet
	= new AstOptimizationStep("ReduceBranchInstructionSet", 1);
    public static final AstOptimizationStep InlineVariables
	= new AstOptimizationStep("InlineVariables", 2);
    public static final AstOptimizationStep CopyPropagation
	= new AstOptimizationStep("CopyPropagation", 3);
    public static final AstOptimizationStep RewriteFinallyBlocks
	= new AstOptimizationStep("RewriteFinallyBlocks", 4);
    public static final AstOptimizationStep SplitToMovableBlocks
	= new AstOptimizationStep("SplitToMovableBlocks", 5);
    public static final AstOptimizationStep RemoveUnreachableBlocks
	= new AstOptimizationStep("RemoveUnreachableBlocks", 6);
    public static final AstOptimizationStep TypeInference
	= new AstOptimizationStep("TypeInference", 7);
    public static final AstOptimizationStep RemoveInnerClassInitSecurityChecks
	= new AstOptimizationStep("RemoveInnerClassInitSecurityChecks", 8);
    public static final AstOptimizationStep PreProcessShortCircuitAssignments
	= new AstOptimizationStep("PreProcessShortCircuitAssignments", 9);
    public static final AstOptimizationStep SimplifyShortCircuit
	= new AstOptimizationStep("SimplifyShortCircuit", 10);
    public static final AstOptimizationStep JoinBranchConditions
	= new AstOptimizationStep("JoinBranchConditions", 11);
    public static final AstOptimizationStep SimplifyTernaryOperator
	= new AstOptimizationStep("SimplifyTernaryOperator", 12);
    public static final AstOptimizationStep JoinBasicBlocks
	= new AstOptimizationStep("JoinBasicBlocks", 13);
    public static final AstOptimizationStep SimplifyLogicalNot
	= new AstOptimizationStep("SimplifyLogicalNot", 14);
    public static final AstOptimizationStep SimplifyShiftOperations
	= new AstOptimizationStep("SimplifyShiftOperations", 15);
    public static final AstOptimizationStep SimplifyLoadAndStore
	= new AstOptimizationStep("SimplifyLoadAndStore", 16);
    public static final AstOptimizationStep TransformObjectInitializers
	= new AstOptimizationStep("TransformObjectInitializers", 17);
    public static final AstOptimizationStep TransformArrayInitializers
	= new AstOptimizationStep("TransformArrayInitializers", 18);
    public static final AstOptimizationStep InlineConditionalAssignments
	= new AstOptimizationStep("InlineConditionalAssignments", 19);
    public static final AstOptimizationStep MakeAssignmentExpressions
	= new AstOptimizationStep("MakeAssignmentExpressions", 20);
    public static final AstOptimizationStep IntroducePostIncrement
	= new AstOptimizationStep("IntroducePostIncrement", 21);
    public static final AstOptimizationStep InlineLambdas
	= new AstOptimizationStep("InlineLambdas", 22);
    public static final AstOptimizationStep InlineVariables2
	= new AstOptimizationStep("InlineVariables2", 23);
    public static final AstOptimizationStep MergeDisparateObjectInitializations
	= new AstOptimizationStep("MergeDisparateObjectInitializations", 24);
    public static final AstOptimizationStep FindLoops
	= new AstOptimizationStep("FindLoops", 25);
    public static final AstOptimizationStep FindConditions
	= new AstOptimizationStep("FindConditions", 26);
    public static final AstOptimizationStep FlattenNestedMovableBlocks
	= new AstOptimizationStep("FlattenNestedMovableBlocks", 27);
    public static final AstOptimizationStep RemoveRedundantCode2
	= new AstOptimizationStep("RemoveRedundantCode2", 28);
    public static final AstOptimizationStep GotoRemoval
	= new AstOptimizationStep("GotoRemoval", 29);
    public static final AstOptimizationStep DuplicateReturns
	= new AstOptimizationStep("DuplicateReturns", 30);
    public static final AstOptimizationStep ReduceIfNesting
	= new AstOptimizationStep("ReduceIfNesting", 31);
    public static final AstOptimizationStep GotoRemoval2
	= new AstOptimizationStep("GotoRemoval2", 32);
    public static final AstOptimizationStep ReduceComparisonInstructionSet
	= new AstOptimizationStep("ReduceComparisonInstructionSet", 33);
    public static final AstOptimizationStep RecombineVariables
	= new AstOptimizationStep("RecombineVariables", 34);
    public static final AstOptimizationStep RemoveRedundantCode3
	= new AstOptimizationStep("RemoveRedundantCode3", 35);
    public static final AstOptimizationStep CleanUpTryBlocks
	= new AstOptimizationStep("CleanUpTryBlocks", 36);
    public static final AstOptimizationStep InlineVariables3
	= new AstOptimizationStep("InlineVariables3", 37);
    public static final AstOptimizationStep TypeInference2
	= new AstOptimizationStep("TypeInference2", 38);
    public static final AstOptimizationStep None
	= new AstOptimizationStep("None", 39);
    /*synthetic*/ private static final AstOptimizationStep[] $VALUES
		      = { RemoveRedundantCode, ReduceBranchInstructionSet,
			  InlineVariables, CopyPropagation,
			  RewriteFinallyBlocks, SplitToMovableBlocks,
			  RemoveUnreachableBlocks, TypeInference,
			  RemoveInnerClassInitSecurityChecks,
			  PreProcessShortCircuitAssignments,
			  SimplifyShortCircuit, JoinBranchConditions,
			  SimplifyTernaryOperator, JoinBasicBlocks,
			  SimplifyLogicalNot, SimplifyShiftOperations,
			  SimplifyLoadAndStore, TransformObjectInitializers,
			  TransformArrayInitializers,
			  InlineConditionalAssignments,
			  MakeAssignmentExpressions, IntroducePostIncrement,
			  InlineLambdas, InlineVariables2,
			  MergeDisparateObjectInitializations, FindLoops,
			  FindConditions, FlattenNestedMovableBlocks,
			  RemoveRedundantCode2, GotoRemoval, DuplicateReturns,
			  ReduceIfNesting, GotoRemoval2,
			  ReduceComparisonInstructionSet, RecombineVariables,
			  RemoveRedundantCode3, CleanUpTryBlocks,
			  InlineVariables3, TypeInference2, None };
    
    public static AstOptimizationStep[] values() {
	return (AstOptimizationStep[]) $VALUES.clone();
    }
    
    public static AstOptimizationStep valueOf(String name) {
	return ((AstOptimizationStep)
		Enum.valueOf(AstOptimizationStep.class, name));
    }
    
    private AstOptimizationStep(String string, int i) {
	super(string, i);
    }
    
    public boolean isBlockLevelOptimization() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstOptimizationStep$1.$SwitchMap$com$strobel$decompiler$ast$AstOptimizationStep[ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	case 14:
	    return true;
	default:
	    return false;
	}
    }
}
