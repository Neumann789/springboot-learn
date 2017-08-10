 package com.strobel.core;
 
 import com.strobel.reflection.TargetInvocationException;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.UndeclaredThrowableException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ExceptionUtilities
 {
   public static RuntimeException asRuntimeException(Throwable t)
   {
     VerifyArgument.notNull(t, "t");
     
     if ((t instanceof RuntimeException)) {
       return (RuntimeException)t;
     }
     
     return new UndeclaredThrowableException(t, "An unhandled checked exception occurred.");
   }
   
   public static Throwable unwrap(Throwable t) {
     Throwable cause = t.getCause();
     
     if ((cause == null) || (cause == t)) {
       return t;
     }
     
     if (((t instanceof InvocationTargetException)) || ((t instanceof TargetInvocationException)) || ((t instanceof UndeclaredThrowableException)))
     {
 
 
       return unwrap(cause);
     }
     
     return t;
   }
   
   public static String getMessage(Throwable t)
   {
     String message = ((Throwable)VerifyArgument.notNull(t, "t")).getMessage();
     
     if (StringUtilities.isNullOrWhitespace(message)) {
       return t.getClass().getSimpleName() + " was thrown.";
     }
     
     return message;
   }
   
   /* Error */
   public static String getStackTraceString(Throwable t)
   {
     // Byte code:
     //   0: aload_0
     //   1: ldc 2
     //   3: invokestatic 3	com/strobel/core/VerifyArgument:notNull	(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
     //   6: pop
     //   7: new 22	java/io/ByteArrayOutputStream
     //   10: dup
     //   11: sipush 1024
     //   14: invokespecial 23	java/io/ByteArrayOutputStream:<init>	(I)V
     //   17: astore_1
     //   18: aconst_null
     //   19: astore_2
     //   20: new 24	java/io/PrintWriter
     //   23: dup
     //   24: aload_1
     //   25: invokespecial 25	java/io/PrintWriter:<init>	(Ljava/io/OutputStream;)V
     //   28: astore_3
     //   29: aconst_null
     //   30: astore 4
     //   32: aload_0
     //   33: aload_3
     //   34: invokevirtual 26	java/lang/Throwable:printStackTrace	(Ljava/io/PrintWriter;)V
     //   37: aload_3
     //   38: invokevirtual 27	java/io/PrintWriter:flush	()V
     //   41: aload_1
     //   42: invokevirtual 28	java/io/ByteArrayOutputStream:flush	()V
     //   45: aload_1
     //   46: invokevirtual 29	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
     //   49: invokestatic 30	com/strobel/core/StringUtilities:trimRight	(Ljava/lang/String;)Ljava/lang/String;
     //   52: astore 5
     //   54: aload_3
     //   55: ifnull +31 -> 86
     //   58: aload 4
     //   60: ifnull +22 -> 82
     //   63: aload_3
     //   64: invokevirtual 31	java/io/PrintWriter:close	()V
     //   67: goto +19 -> 86
     //   70: astore 6
     //   72: aload 4
     //   74: aload 6
     //   76: invokevirtual 32	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
     //   79: goto +7 -> 86
     //   82: aload_3
     //   83: invokevirtual 31	java/io/PrintWriter:close	()V
     //   86: aload_1
     //   87: ifnull +29 -> 116
     //   90: aload_2
     //   91: ifnull +21 -> 112
     //   94: aload_1
     //   95: invokevirtual 33	java/io/ByteArrayOutputStream:close	()V
     //   98: goto +18 -> 116
     //   101: astore 6
     //   103: aload_2
     //   104: aload 6
     //   106: invokevirtual 32	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
     //   109: goto +7 -> 116
     //   112: aload_1
     //   113: invokevirtual 33	java/io/ByteArrayOutputStream:close	()V
     //   116: aload 5
     //   118: areturn
     //   119: astore 5
     //   121: aload 5
     //   123: astore 4
     //   125: aload 5
     //   127: athrow
     //   128: astore 7
     //   130: aload_3
     //   131: ifnull +31 -> 162
     //   134: aload 4
     //   136: ifnull +22 -> 158
     //   139: aload_3
     //   140: invokevirtual 31	java/io/PrintWriter:close	()V
     //   143: goto +19 -> 162
     //   146: astore 8
     //   148: aload 4
     //   150: aload 8
     //   152: invokevirtual 32	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
     //   155: goto +7 -> 162
     //   158: aload_3
     //   159: invokevirtual 31	java/io/PrintWriter:close	()V
     //   162: aload 7
     //   164: athrow
     //   165: astore_3
     //   166: aload_3
     //   167: astore_2
     //   168: aload_3
     //   169: athrow
     //   170: astore 9
     //   172: aload_1
     //   173: ifnull +29 -> 202
     //   176: aload_2
     //   177: ifnull +21 -> 198
     //   180: aload_1
     //   181: invokevirtual 33	java/io/ByteArrayOutputStream:close	()V
     //   184: goto +18 -> 202
     //   187: astore 10
     //   189: aload_2
     //   190: aload 10
     //   192: invokevirtual 32	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
     //   195: goto +7 -> 202
     //   198: aload_1
     //   199: invokevirtual 33	java/io/ByteArrayOutputStream:close	()V
     //   202: aload 9
     //   204: athrow
     //   205: astore_1
     //   206: aload_0
     //   207: invokevirtual 34	java/lang/Throwable:toString	()Ljava/lang/String;
     //   210: areturn
     // Line number table:
     //   Java source line #65	-> byte code offset #0
     //   Java source line #67	-> byte code offset #7
     //   Java source line #68	-> byte code offset #20
     //   Java source line #67	-> byte code offset #29
     //   Java source line #70	-> byte code offset #32
     //   Java source line #72	-> byte code offset #37
     //   Java source line #73	-> byte code offset #41
     //   Java source line #75	-> byte code offset #45
     //   Java source line #76	-> byte code offset #54
     //   Java source line #67	-> byte code offset #119
     //   Java source line #76	-> byte code offset #128
     //   Java source line #67	-> byte code offset #165
     //   Java source line #76	-> byte code offset #170
     //   Java source line #77	-> byte code offset #205
     //   Java source line #78	-> byte code offset #206
     // Local variable table:
     //   start	length	slot	name	signature
     //   0	211	0	t	Throwable
     //   17	182	1	stream	java.io.ByteArrayOutputStream
     //   205	2	1	ignored	Throwable
     //   19	171	2	localThrowable3	Throwable
     //   28	131	3	writer	java.io.PrintWriter
     //   165	4	3	localThrowable2	Throwable
     //   30	119	4	localThrowable4	Throwable
     //   119	7	5	localThrowable1	Throwable
     //   119	7	5	localThrowable5	Throwable
     //   70	5	6	x2	Throwable
     //   101	4	6	x2	Throwable
     //   128	35	7	localObject1	Object
     //   146	5	8	x2	Throwable
     //   170	33	9	localObject2	Object
     //   187	4	10	x2	Throwable
     // Exception table:
     //   from	to	target	type
     //   63	67	70	java/lang/Throwable
     //   94	98	101	java/lang/Throwable
     //   32	54	119	java/lang/Throwable
     //   32	54	128	finally
     //   119	130	128	finally
     //   139	143	146	java/lang/Throwable
     //   20	86	165	java/lang/Throwable
     //   119	165	165	java/lang/Throwable
     //   20	86	170	finally
     //   119	172	170	finally
     //   180	184	187	java/lang/Throwable
     //   7	116	205	java/lang/Throwable
     //   119	205	205	java/lang/Throwable
   }
 }


