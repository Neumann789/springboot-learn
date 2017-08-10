 package com.strobel.decompiler.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.collections.SmartList;
 import com.strobel.componentmodel.Key;
 import com.strobel.componentmodel.UserDataStore;
 import com.strobel.componentmodel.UserDataStoreBase;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.Comparer;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.NameSyntax;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Expression
   extends Node
   implements Cloneable, UserDataStore
 {
   public static final Object ANY_OPERAND = new Object();
   
 
   public static final int MYSTERY_OFFSET = -34;
   
   private final SmartList<Expression> _arguments = new SmartList();
   
   private final SmartList<Range> _ranges = new SmartList()
   {
     public boolean add(Range range) {
       return (!contains(range)) && (super.add(range));
     }
     
     public void add(int index, Range element)
     {
       if (contains(element)) {
         return;
       }
       super.add(index, element);
     }
   };
   
   private AstCode _code;
   
   private Object _operand;
   
   private int _offset;
   private TypeReference _expectedType;
   private TypeReference _inferredType;
   private UserDataStoreBase _userData;
   
   public Expression(AstCode code, Object operand, int offset, List<Expression> arguments)
   {
     this._code = ((AstCode)VerifyArgument.notNull(code, "code"));
     this._operand = VerifyArgument.notInstanceOf(Expression.class, operand, "operand");
     this._offset = offset;
     
     if (arguments != null) {
       this._arguments.addAll(arguments);
     }
   }
   
   public Expression(AstCode code, Object operand, int offset, Expression... arguments) {
     this._code = ((AstCode)VerifyArgument.notNull(code, "code"));
     this._operand = VerifyArgument.notInstanceOf(Expression.class, operand, "operand");
     this._offset = offset;
     
     if (arguments != null) {
       Collections.addAll(this._arguments, arguments);
     }
   }
   
   public final List<Expression> getArguments() {
     return this._arguments;
   }
   
   public final AstCode getCode() {
     return this._code;
   }
   
   public final void setCode(AstCode code) {
     this._code = code;
   }
   
   public final Object getOperand() {
     return this._operand;
   }
   
   public final void setOperand(Object operand) {
     this._operand = operand;
   }
   
 
 
   public final int getOffset()
   {
     return this._offset;
   }
   
   public final TypeReference getExpectedType() {
     return this._expectedType;
   }
   
   public final void setExpectedType(TypeReference expectedType) {
     this._expectedType = expectedType;
   }
   
   public final TypeReference getInferredType() {
     return this._inferredType;
   }
   
   public final void setInferredType(TypeReference inferredType) {
     this._inferredType = inferredType;
   }
   
   public final boolean isBranch() {
     return ((this._operand instanceof Label)) || ((this._operand instanceof Label[]));
   }
   
   public final List<Label> getBranchTargets()
   {
     if ((this._operand instanceof Label)) {
       return Collections.singletonList((Label)this._operand);
     }
     
     if ((this._operand instanceof Label[])) {
       return ArrayUtilities.asUnmodifiableList((Label[])this._operand);
     }
     
     return Collections.emptyList();
   }
   
   public final List<Range> getRanges() {
     return this._ranges;
   }
   
   public final List<Node> getChildren()
   {
     ArrayList<Node> childrenCopy = new ArrayList();
     
     childrenCopy.addAll(this._arguments);
     
     if ((this._operand instanceof Lambda)) {
       childrenCopy.add((Node)this._operand);
     }
     
     return childrenCopy;
   }
   
   public final boolean containsReferenceTo(Variable variable) {
     if (this._operand == variable) {
       return true;
     }
     
     for (int i = 0; i < this._arguments.size(); i++) {
       if (((Expression)this._arguments.get(i)).containsReferenceTo(variable)) {
         return true;
       }
     }
     
     return false;
   }
   
   public final void writeTo(ITextOutput output)
   {
     AstCode code = this._code;
     Object operand = this._operand;
     TypeReference inferredType = this._inferredType;
     TypeReference expectedType = this._expectedType;
     
     if ((operand instanceof Variable))
     {
 
       if (AstCodeHelpers.isLocalStore(code)) {
         output.write(((Variable)operand).getName());
         output.write(" = ");
         ((Expression)getArguments().get(0)).writeTo(output);
         return;
       }
       
       if (AstCodeHelpers.isLocalLoad(code)) {
         output.write(((Variable)operand).getName());
         
         if (inferredType != null) {
           output.write(':');
           DecompilerHelpers.writeType(output, inferredType, NameSyntax.SHORT_TYPE_NAME);
           
           if ((expectedType != null) && (!Comparer.equals(expectedType.getInternalName(), inferredType.getInternalName())))
           {
 
             output.write("[expected:");
             DecompilerHelpers.writeType(output, expectedType, NameSyntax.SHORT_TYPE_NAME);
             output.write(']');
           }
         }
         
         return;
       }
     }
     
     output.writeReference(code.name().toLowerCase(), code);
     
     if (inferredType != null) {
       output.write(':');
       DecompilerHelpers.writeType(output, inferredType, NameSyntax.SHORT_TYPE_NAME);
       
       if ((expectedType != null) && (!Comparer.equals(expectedType.getInternalName(), inferredType.getInternalName())))
       {
 
         output.write("[expected:");
         DecompilerHelpers.writeType(output, expectedType, NameSyntax.SHORT_TYPE_NAME);
         output.write(']');
       }
     }
     else if (expectedType != null) {
       output.write("[expected:");
       DecompilerHelpers.writeType(output, expectedType, NameSyntax.SHORT_TYPE_NAME);
       output.write(']');
     }
     
     output.write('(');
     
     boolean first = true;
     
     if (operand != null) {
       if ((operand instanceof Label)) {
         output.writeReference(((Label)operand).getName(), operand);
       }
       else if ((operand instanceof Label[])) {
         Label[] labels = (Label[])operand;
         
         for (int i = 0; i < labels.length; i++) {
           if (i != 0) {
             output.write(", ");
           }
           
           output.writeReference(labels[i].getName(), labels[i]);
         }
       }
       else if (((operand instanceof MethodReference)) || ((operand instanceof FieldReference)))
       {
 
         MemberReference member = (MemberReference)operand;
         TypeReference declaringType = member.getDeclaringType();
         
         if (declaringType != null) {
           DecompilerHelpers.writeType(output, declaringType, NameSyntax.SHORT_TYPE_NAME);
           output.write("::");
         }
         
         output.writeReference(member.getName(), member);
       }
       else if ((operand instanceof Node)) {
         ((Node)operand).writeTo(output);
       }
       else {
         DecompilerHelpers.writeOperand(output, operand);
       }
       
       first = false;
     }
     
     for (Expression argument : getArguments()) {
       if (!first) {
         output.write(", ");
       }
       
       argument.writeTo(output);
       first = false;
     }
     
     output.write(')');
   }
   
   public final Expression clone()
   {
     Expression clone = new Expression(this._code, this._operand, this._offset, new Expression[0]);
     
     clone._code = this._code;
     clone._expectedType = this._expectedType;
     clone._inferredType = this._inferredType;
     clone._operand = this._operand;
     clone._userData = (this._userData != null ? this._userData.clone() : null);
     clone._offset = this._offset;
     
     for (Expression argument : this._arguments) {
       clone._arguments.add(argument.clone());
     }
     
     return clone;
   }
   
   public boolean isEquivalentTo(Expression e) {
     if ((e == null) || (this._code != e._code)) {
       return false;
     }
     
     if ((this._operand instanceof FieldReference)) {
       if (!(e._operand instanceof FieldReference)) {
         return false;
       }
       
       FieldReference f1 = (FieldReference)this._operand;
       FieldReference f2 = (FieldReference)e._operand;
       
       if (!StringUtilities.equals(f1.getFullName(), f2.getFullName())) {
         return false;
       }
     }
     else if ((this._operand instanceof MethodReference)) {
       if (!(e._operand instanceof MethodReference)) {
         return false;
       }
       
       MethodReference f1 = (MethodReference)this._operand;
       MethodReference f2 = (MethodReference)e._operand;
       
       if ((!StringUtilities.equals(f1.getFullName(), f2.getFullName())) || (!StringUtilities.equals(f1.getErasedSignature(), f2.getErasedSignature())))
       {
 
         return false;
       }
     }
     else if (!Comparer.equals(e._operand, this._operand)) {
       return false;
     }
     
     if (this._arguments.size() != e._arguments.size()) {
       return false;
     }
     
     int i = 0; for (int n = this._arguments.size(); i < n; i++) {
       Expression a1 = (Expression)this._arguments.get(i);
       Expression a2 = (Expression)e._arguments.get(i);
       
       if (!a1.isEquivalentTo(a2)) {
         return false;
       }
     }
     
     return true;
   }
   
   public <T> T getUserData(@NotNull Key<T> key)
   {
     if (this._userData == null) {
       return null;
     }
     return (T)this._userData.getUserData(key);
   }
   
   public <T> void putUserData(@NotNull Key<T> key, @Nullable T value)
   {
     if (this._userData == null) {
       this._userData = new UserDataStoreBase();
     }
     this._userData.putUserData(key, value);
   }
   
   public <T> T putUserDataIfAbsent(@NotNull Key<T> key, @Nullable T value)
   {
     if (this._userData == null) {
       this._userData = new UserDataStoreBase();
     }
     return (T)this._userData.putUserDataIfAbsent(key, value);
   }
   
   public <T> boolean replace(@NotNull Key<T> key, @Nullable T oldValue, @Nullable T newValue)
   {
     if (this._userData == null) {
       this._userData = new UserDataStoreBase();
     }
     return this._userData.replace(key, oldValue, newValue);
   }
 }


