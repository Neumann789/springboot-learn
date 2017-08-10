 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.core.Predicate;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TransformationPipeline
 {
   private static final Logger LOG = Logger.getLogger(TransformationPipeline.class.getSimpleName());
   
   public static IAstTransform[] createPipeline(DecompilerContext context)
   {
     return new IAstTransform[] { new EnumRewriterTransform(context), new EnumSwitchRewriterTransform(context), new EclipseEnumSwitchRewriterTransform(context), new AssertStatementTransform(context), new RemoveImplicitBoxingTransform(context), new RemoveRedundantCastsTransform(context), new ConvertLoopsTransform(context), new BreakTargetRelocation(context), new LabelCleanupTransform(context), new TryWithResourcesTransform(context), new DeclareVariablesTransform(context), new StringSwitchRewriterTransform(context), new EclipseStringSwitchRewriterTransform(context), new SimplifyAssignmentsTransform(context), new EliminateSyntheticAccessorsTransform(context), new LambdaTransform(context), new RewriteNewArrayLambdas(context), new RewriteLocalClassesTransform(context), new IntroduceOuterClassReferencesTransform(context), new RewriteInnerClassConstructorCalls(context), new RemoveRedundantInitializersTransform(context), new FlattenElseIfStatementsTransform(context), new FlattenSwitchBlocksTransform(context), new IntroduceInitializersTransform(context), new MarkReferencedSyntheticsTransform(context), new RemoveRedundantCastsTransform(context), new InsertNecessaryConversionsTransform(context), new IntroduceStringConcatenationTransform(context), new SimplifyAssignmentsTransform(context), new InlineEscapingAssignmentsTransform(context), new VarArgsTransform(context), new InsertConstantReferencesTransform(context), new SimplifyArithmeticExpressionsTransform(context), new DeclareLocalClassesTransform(context), new InsertOverrideAnnotationsTransform(context), new AddReferenceQualifiersTransform(context), new RemoveHiddenMembersTransform(context), new CollapseImportsTransform(context) };
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static void runTransformationsUntil(AstNode node, Predicate<IAstTransform> abortCondition, DecompilerContext context)
   {
     if (node == null) {
       return;
     }
     
     for (IAstTransform transform : createPipeline(context)) {
       if ((abortCondition != null) && (abortCondition.test(transform))) {
         return;
       }
       
       if (LOG.isLoggable(Level.FINE)) {
         LOG.fine("Running Java AST transform: " + transform.getClass().getSimpleName() + "...");
       }
       
       transform.run(node);
     }
   }
 }


