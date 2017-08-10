package jode.flow;

import java.util.Set;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.util.SimpleSet;

public abstract class InstructionContainer
  extends StructuredBlock
{
  Expression instr;
  
  public InstructionContainer(Expression paramExpression)
  {
    this.instr = paramExpression;
  }
  
  public InstructionContainer(Expression paramExpression, Jump paramJump)
  {
    this(paramExpression);
    setJump(paramJump);
  }
  
  public void makeDeclaration(Set paramSet)
  {
    if (this.instr != null) {
      this.instr.makeDeclaration(paramSet);
    }
    super.makeDeclaration(paramSet);
  }
  
  public void removeOnetimeLocals()
  {
    if (this.instr != null) {
      this.instr = this.instr.removeOnetimeLocals();
    }
    super.removeOnetimeLocals();
  }
  
  public void fillInGenSet(Set paramSet1, Set paramSet2)
  {
    if (this.instr != null) {
      this.instr.fillInGenSet(paramSet1, paramSet2);
    }
  }
  
  public Set getDeclarables()
  {
    SimpleSet localSimpleSet = new SimpleSet();
    if (this.instr != null) {
      this.instr.fillDeclarables(localSimpleSet);
    }
    return localSimpleSet;
  }
  
  public boolean doTransformations()
  {
    if (this.instr == null) {
      return false;
    }
    if ((this.instr instanceof InvokeOperator))
    {
      localObject = ((InvokeOperator)this.instr).simplifyAccess();
      if (localObject != null) {
        this.instr = ((Expression)localObject);
      }
    }
    Object localObject = this.flowBlock.lastModified;
    return (CreateNewConstructor.transform(this, (StructuredBlock)localObject)) || (CreateAssignExpression.transform(this, (StructuredBlock)localObject)) || (CreateExpression.transform(this, (StructuredBlock)localObject)) || (CreatePrePostIncExpression.transform(this, (StructuredBlock)localObject)) || (CreateIfThenElseOperator.create(this, (StructuredBlock)localObject)) || (CreateConstantArray.transform(this, (StructuredBlock)localObject)) || (CreateCheckNull.transformJavac(this, (StructuredBlock)localObject));
  }
  
  public final Expression getInstruction()
  {
    return this.instr;
  }
  
  public void simplify()
  {
    if (this.instr != null) {
      this.instr = this.instr.simplify();
    }
    super.simplify();
  }
  
  public final void setInstruction(Expression paramExpression)
  {
    this.instr = paramExpression;
  }
}


