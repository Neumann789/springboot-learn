/* Completion - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.complete;

public abstract class Completion
{
    private boolean hasToAskForConfirmation = true;
    private boolean askForConfirmationResponse = true;
    
    public abstract void complete(CompleteOperation completeoperation,
				  boolean bool);
    
    public void setHasToAskForConfirmation(boolean hasToAsk) {
	hasToAskForConfirmation = hasToAsk;
    }
    
    public boolean hasToAskForConfirmation() {
	return hasToAskForConfirmation;
    }
    
    public void setAskForConfirmationResponse(boolean showAll) {
	askForConfirmationResponse = showAll;
    }
    
    public boolean getAskForConfirmationResponse() {
	return askForConfirmationResponse;
    }
}
