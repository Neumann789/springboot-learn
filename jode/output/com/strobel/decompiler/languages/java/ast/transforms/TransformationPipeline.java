/* TransformationPipeline - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.strobel.core.Predicate;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;

public final class TransformationPipeline
{
    private static final Logger LOG
	= Logger.getLogger(TransformationPipeline.class.getSimpleName());
    
    public static IAstTransform[] createPipeline(DecompilerContext context) {
	return (new <error>
		{ new EnumRewriterTransform(context),
		  new EnumSwitchRewriterTransform(context),
		  new EclipseEnumSwitchRewriterTransform(context),
		  new AssertStatementTransform(context),
		  new RemoveImplicitBoxingTransform(context),
		  new RemoveRedundantCastsTransform(context),
		  new ConvertLoopsTransform(context),
		  new BreakTargetRelocation(context),
		  new LabelCleanupTransform(context),
		  new TryWithResourcesTransform(context),
		  new DeclareVariablesTransform(context),
		  new StringSwitchRewriterTransform(context),
		  new EclipseStringSwitchRewriterTransform(context),
		  new SimplifyAssignmentsTransform(context),
		  new EliminateSyntheticAccessorsTransform(context),
		  new LambdaTransform(context),
		  new RewriteNewArrayLambdas(context),
		  new RewriteLocalClassesTransform(context),
		  new IntroduceOuterClassReferencesTransform(context),
		  new RewriteInnerClassConstructorCalls(context),
		  new RemoveRedundantInitializersTransform(context),
		  new FlattenElseIfStatementsTransform(context),
		  new FlattenSwitchBlocksTransform(context),
		  new IntroduceInitializersTransform(context),
		  new MarkReferencedSyntheticsTransform(context),
		  new RemoveRedundantCastsTransform(context),
		  new InsertNecessaryConversionsTransform(context),
		  new IntroduceStringConcatenationTransform(context),
		  new SimplifyAssignmentsTransform(context),
		  new InlineEscapingAssignmentsTransform(context),
		  new VarArgsTransform(context),
		  new InsertConstantReferencesTransform(context),
		  new SimplifyArithmeticExpressionsTransform(context),
		  new DeclareLocalClassesTransform(context),
		  new InsertOverrideAnnotationsTransform(context),
		  new AddReferenceQualifiersTransform(context),
		  new RemoveHiddenMembersTransform(context),
		  new CollapseImportsTransform(context) });
    }
    
    public static void runTransformationsUntil(AstNode node,
					       Predicate abortCondition,
					       DecompilerContext context) {
	if (node != null) {
	    IAstTransform[] arr$ = createPipeline(context);
	    int len$ = arr$.length;
	    for (int i$ = 0; i$ < len$; i$++) {
		IAstTransform transform = arr$[i$];
	    label_1800:
		{
		    if (abortCondition == null
			|| !abortCondition.test(transform)) {
			if (LOG.isLoggable(Level.FINE))
			    LOG.fine("Running Java AST transform: "
				     + transform.getClass().getSimpleName()
				     + "...");
			break label_1800;
		    }
		    return;
		}
		transform.run(node);
	    }
	}
	return;
    }
}
