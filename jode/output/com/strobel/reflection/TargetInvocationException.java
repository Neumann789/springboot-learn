/* TargetInvocationException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.reflection;

public class TargetInvocationException extends RuntimeException
{
    private static final String DefaultMessage
	= "Exception has been thrown by the target of an invocation.";
    
    public TargetInvocationException() {
	super("Exception has been thrown by the target of an invocation.");
    }
    
    public TargetInvocationException(String message) {
	super(message);
    }
    
    public TargetInvocationException(String message, Throwable cause) {
	super(message, cause);
    }
    
    public TargetInvocationException(Throwable cause) {
	super("Exception has been thrown by the target of an invocation.",
	      cause);
    }
    
    public TargetInvocationException(Throwable cause,
				     boolean enableSuppression,
				     boolean writableStackTrace) {
	super("Exception has been thrown by the target of an invocation.",
	      cause, enableSuppression, writableStackTrace);
    }
    
    public TargetInvocationException(String message, Throwable cause,
				     boolean enableSuppression,
				     boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }
}
