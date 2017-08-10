/* ExceptionUtilities - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import com.strobel.reflection.TargetInvocationException;

public final class ExceptionUtilities
{
    public static RuntimeException asRuntimeException(Throwable t) {
	VerifyArgument.notNull(t, "t");
	if (!(t instanceof RuntimeException))
	    return (new UndeclaredThrowableException
		    (t, "An unhandled checked exception occurred."));
	return (RuntimeException) t;
    }
    
    public static Throwable unwrap(Throwable t) {
	Throwable cause = t.getCause();
	if (cause != null && cause != t) {
	    if (!(t instanceof InvocationTargetException)
		&& !(t instanceof TargetInvocationException)
		&& !(t instanceof UndeclaredThrowableException))
		return t;
	    return unwrap(cause);
	}
	return t;
    }
    
    public static String getMessage(Throwable t) {
	String message
	    = ((Throwable) VerifyArgument.notNull(t, "t")).getMessage();
	if (!StringUtilities.isNullOrWhitespace(message))
	    return message;
	return t.getClass().getSimpleName() + " was thrown.";
    }
    
    public static String getStackTraceString(Throwable t) {
	VerifyArgument.notNull(t, "t");
    label_1409:
	{
	    ByteArrayOutputStream stream;
	    Throwable throwable;
	    try {
		stream = new ByteArrayOutputStream(1024);
		throwable = null;
	    } catch (Throwable PUSH) {
		break label_1409;
	    }
	label_1407:
	    {
	    label_1406:
		{
		    PrintWriter writer;
		    Throwable throwable_0_;
		    try {
			writer = new PrintWriter(stream);
			throwable_0_ = null;
		    } catch (Throwable PUSH) {
			break label_1406;
		    } finally {
			break label_1407;
		    } catch (Throwable PUSH) {
			break label_1409;
		    }
		    String string;
		label_1404:
		    {
			try {
			    t.printStackTrace(writer);
			    writer.flush();
			    stream.flush();
			    string
				= StringUtilities.trimRight(stream.toString());
			} catch (Throwable PUSH) {
			    try {
				Throwable throwable_1_ = POP;
				throwable_0_ = throwable_1_;
				throw throwable_1_;
			    } finally {
				break label_1404;
			    } catch (Throwable PUSH) {
				break label_1406;
			    } finally {
				break label_1407;
			    } catch (Throwable PUSH) {
				break label_1409;
			    }
			} finally {
			    break label_1404;
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
		    }
		    Object object;
		label_1405:
		    {
			try {
			    object = POP;
			    if (writer == null)
				break label_1405;
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
			try {
			    if (throwable_0_ != null) {
				try {
				    writer.close();
				    break label_1405;
				} catch (Throwable PUSH) {
				    /* empty */
				} catch (Throwable PUSH) {
				    break label_1406;
				} finally {
				    break label_1407;
				} catch (Throwable PUSH) {
				    break label_1409;
				}
			    }
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
			try {
			    writer.close();
			    break label_1405;
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
			try {
			    Throwable x2 = POP;
			    throwable_0_.addSuppressed(x2);
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
		    }
		    try {
			throw object;
		    } catch (Throwable PUSH) {
			break label_1406;
		    } finally {
			break label_1407;
		    } catch (Throwable PUSH) {
			break label_1409;
		    }
		label_1401:
		    {
		    label_1400:
			{
			    try {
				if (writer == null)
				    break label_1401;
				try {
				    if (throwable_0_ == null)
					break label_1400;
				} catch (Throwable PUSH) {
				    break label_1406;
				} finally {
				    break label_1407;
				} catch (Throwable PUSH) {
				    break label_1409;
				}
			    } catch (Throwable PUSH) {
				break label_1406;
			    } finally {
				break label_1407;
			    } catch (Throwable PUSH) {
				break label_1409;
			    }
			}
			try {
			    writer.close();
			    break label_1401;
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
			try {
			    writer.close();
			} catch (Throwable PUSH) {
			    try {
				Throwable x2 = POP;
				throwable_0_.addSuppressed(x2);
			    } catch (Throwable PUSH) {
				break label_1406;
			    } finally {
				break label_1407;
			    } catch (Throwable PUSH) {
				break label_1409;
			    }
			} catch (Throwable PUSH) {
			    break label_1406;
			} finally {
			    break label_1407;
			} catch (Throwable PUSH) {
			    break label_1409;
			}
		    }
		label_1403:
		    {
		    label_1402:
			{
			    try {
				if (stream == null)
				    break label_1403;
				try {
				    if (throwable == null)
					break label_1402;
				} catch (Throwable PUSH) {
				    break label_1409;
				}
			    } catch (Throwable PUSH) {
				break label_1409;
			    }
			}
			try {
			    stream.close();
			} catch (Throwable PUSH) {
			    break label_1409;
			}
			break label_1403;
			try {
			    stream.close();
			} catch (Throwable PUSH) {
			    try {
				Throwable x2 = POP;
				throwable.addSuppressed(x2);
			    } catch (Throwable PUSH) {
				break label_1409;
			    }
			} catch (Throwable PUSH) {
			    break label_1409;
			}
		    }
		    return string;
		}
		try {
		    Throwable throwable_2_ = POP;
		    throwable = throwable_2_;
		    throw throwable_2_;
		} finally {
		    break label_1407;
		} catch (Throwable PUSH) {
		    break label_1409;
		}
	    }
	    Object object;
	label_1408:
	    {
		try {
		    object = POP;
		    if (stream == null)
			break label_1408;
		} catch (Throwable PUSH) {
		    break label_1409;
		}
		try {
		    if (throwable != null) {
			try {
			    stream.close();
			    break label_1408;
			} catch (Throwable PUSH) {
			    /* empty */
			} catch (Throwable PUSH) {
			    break label_1409;
			}
		    }
		} catch (Throwable PUSH) {
		    break label_1409;
		}
		try {
		    stream.close();
		} catch (Throwable PUSH) {
		    break label_1409;
		}
		break label_1408;
		try {
		    Throwable x2 = POP;
		    throwable.addSuppressed(x2);
		} catch (Throwable PUSH) {
		    break label_1409;
		}
	    }
	    try {
		throw object;
	    } catch (Throwable PUSH) {
		/* empty */
	    }
	}
	Throwable ignored = POP;
	return t.toString();
    }
}
