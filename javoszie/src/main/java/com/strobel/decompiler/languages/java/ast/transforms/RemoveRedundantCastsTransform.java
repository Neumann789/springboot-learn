 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.annotations.Nullable;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.ParenthesizedExpression;
 import com.strobel.decompiler.languages.java.utilities.RedundantCastUtility;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class RemoveRedundantCastsTransform
   extends ContextTrackingVisitor<Void>
 {
   private final JavaResolver _resolver;
   
   public RemoveRedundantCastsTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   public void run(AstNode compilationUnit)
   {
     if (this.context.getSettings().getRetainRedundantCasts()) {
       return;
     }
     super.run(compilationUnit);
   }
   
   public Void visitCastExpression(CastExpression node, Void data)
   {
     super.visitCastExpression(node, data);
     
     List<CastExpression> redundantCasts = RedundantCastUtility.getRedundantCastsInside(this._resolver, skipParenthesesUp(node.getParent()));
     
 
 
 
     if (redundantCasts.contains(node)) {
       RedundantCastUtility.removeCast(node);
     }
     
     return null;
   }
   
   @Nullable
   private static AstNode skipParenthesesUp(AstNode e) {
     AstNode result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = result.getParent();
     }
     
     return result;
   }
 }


