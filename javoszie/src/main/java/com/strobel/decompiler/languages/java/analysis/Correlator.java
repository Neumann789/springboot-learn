 package com.strobel.decompiler.languages.java.analysis;
 
 import com.strobel.assembler.metadata.IMetadataTypeMember;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.utilities.TreeTraversal;
 import com.strobel.functions.Function;
 import java.util.Collection;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Correlator
 {
   public static boolean areCorrelated(Expression readExpression, Statement writeStatement)
   {
     Set<IMetadataTypeMember> readMembers = new LinkedHashSet();
     Set<IMetadataTypeMember> writeMembers = new LinkedHashSet();
     
     collectCorrelations(readExpression, CorrelationMode.Read, readMembers);
     
     if (readMembers.isEmpty()) {
       return false;
     }
     
     collectCorrelations(writeStatement, CorrelationMode.Write, writeMembers);
     
     if (writeMembers.isEmpty()) {
       return false;
     }
     
     for (IMetadataTypeMember typeMember : readMembers) {
       if (writeMembers.contains(typeMember)) {
         return true;
       }
     }
     
     return false;
   }
   
 
 
 
   private static void collectCorrelations(AstNode node, CorrelationMode mode, Collection<IMetadataTypeMember> members)
   {
     Iterable<AstNode> traversal = TreeTraversal.postOrder(node, new Function()
     {
 
       public Iterable<AstNode> apply(AstNode n)
       {
         return n.getChildren();
       }
     });
     
 
     for (AstNode n : traversal) {
       if ((n instanceof IdentifierExpression))
       {
 
 
         IdentifierExpression identifier = (IdentifierExpression)n;
         UsageType usage = UsageClassifier.getUsageType(identifier);
         
         if (mode == CorrelationMode.Read ? 
           (usage == UsageType.Read) && (usage == UsageType.ReadWrite) : 
           
 
 
           (usage == UsageType.Write) || (usage == UsageType.ReadWrite))
         {
 
 
           IMetadataTypeMember member = (IMetadataTypeMember)identifier.getUserData(Keys.MEMBER_REFERENCE);
           
           if (member != null) {
             members.add(member);
           }
           else
           {
             Variable variable = (Variable)identifier.getUserData(Keys.VARIABLE);
             
             if (variable != null) {
               if (variable.isParameter()) {
                 member = variable.getOriginalParameter();
               }
               else if (variable.getOriginalVariable() != null) {
                 member = variable.getOriginalVariable();
               }
               
               if (member != null) {
                 members.add(member);
               }
             }
           }
         }
       }
     }
   }
   
   private static enum CorrelationMode {
     Read, 
     Write;
     
     private CorrelationMode() {}
   }
 }


