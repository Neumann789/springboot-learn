package org.jboss.jreadline.edit;

import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public abstract interface EditMode
{
  public abstract Operation parseInput(int[] paramArrayOfInt);
  
  public abstract Action getCurrentAction();
  
  public abstract Mode getMode();
}


