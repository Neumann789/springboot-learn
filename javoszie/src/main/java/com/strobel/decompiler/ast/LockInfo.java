 package com.strobel.decompiler.ast;
 
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class LockInfo
 {
   public final Label leadingLabel;
   public final Expression lockInit;
   public final Expression lockStore;
   public final Expression lockStoreCopy;
   public final Expression lockAcquire;
   public final Variable lock;
   public final Variable lockCopy;
   public final int operationCount;
   public final boolean isSimpleAcquire;
   
   public final List<Variable> getLockVariables()
   {
     if (this.lockCopy == null) {
       return Collections.singletonList(this.lock);
     }
     return Arrays.asList(new Variable[] { this.lock, this.lockCopy });
   }
   
 
 
   LockInfo(Label leadingLabel, Expression lockAcquire)
   {
     this(leadingLabel, null, null, null, lockAcquire);
   }
   
 
 
 
 
 
 
 
 
 
 
 
   LockInfo(Label leadingLabel, Expression lockInit, Expression lockStore, Expression lockStoreCopy, Expression lockAcquire)
   {
     this.leadingLabel = leadingLabel;
     this.lockInit = lockInit;
     this.lockStore = lockStore;
     this.lockStoreCopy = lockStoreCopy;
     this.lockAcquire = ((Expression)VerifyArgument.notNull(lockAcquire, "lockAcquire"));
     
     this.lock = ((Variable)((Expression)lockAcquire.getArguments().get(0)).getOperand());
     
     if (lockStoreCopy != null) {
       this.lockCopy = ((Variable)lockStoreCopy.getOperand());
     }
     else {
       this.lockCopy = null;
     }
     
     this.isSimpleAcquire = ((lockInit == null) && (lockStore == null) && (lockStoreCopy == null));
     
 
 
     this.operationCount = ((leadingLabel != null ? 1 : 0) + (lockStore != null ? 1 : 0) + (lockStoreCopy != null ? 1 : 0) + 1);
   }
 }


