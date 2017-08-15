/* EditActionManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class EditActionManager
{
    public static EditAction parseAction(Operation operation, int cursor,
					 int length) {
	if (operation.getAction() == Action.MOVE
	    || operation.getAction() == Action.YANK) {
	    switch (ANONYMOUS CLASS org.jboss.jreadline.edit.actions.EditActionManager$1
		    .$SwitchMap$org$jboss$jreadline$edit$actions$Movement
		    [operation.getMovement().ordinal()]) {
	    case 1:
		return new SimpleAction(cursor, operation.getAction(),
					cursor + 1);
	    case 2:
		return new SimpleAction(cursor, operation.getAction(),
					cursor - 1);
	    case 3:
		return new NextWordAction(cursor, operation.getAction());
	    case 4:
		return new NextSpaceWordAction(cursor, operation.getAction());
	    case 5:
		return new PrevWordAction(cursor, operation.getAction());
	    case 6:
		return new PrevSpaceWordAction(cursor, operation.getAction());
	    case 7:
		return new SimpleAction(cursor, operation.getAction(), 0);
	    case 8:
		return new SimpleAction(cursor, operation.getAction(), length);
	    case 9:
		return new SimpleAction(0, operation.getAction(), length);
	    default:
		break;
	    }
	} else if (operation.getAction() == Action.DELETE
		   || operation.getAction() == Action.CHANGE) {
	    switch (ANONYMOUS CLASS org.jboss.jreadline.edit.actions.EditActionManager$1
		    .$SwitchMap$org$jboss$jreadline$edit$actions$Movement
		    [operation.getMovement().ordinal()]) {
	    case 1:
		return new DeleteAction(cursor, operation.getAction());
	    case 2:
		return new DeleteAction(cursor, operation.getAction(), true);
	    case 3:
		return new NextWordAction(cursor, operation.getAction());
	    case 4:
		return new NextSpaceWordAction(cursor, operation.getAction());
	    case 5:
		return new PrevWordAction(cursor, operation.getAction());
	    case 6:
		return new PrevSpaceWordAction(cursor, operation.getAction());
	    case 7:
		return new SimpleAction(cursor, operation.getAction(), 0);
	    case 8:
		return new SimpleAction(cursor, operation.getAction(), length);
	    case 9:
		return new SimpleAction(0, operation.getAction(), length);
	    }
	}
	return new SimpleAction(cursor, Action.NO_ACTION);
    }
}
