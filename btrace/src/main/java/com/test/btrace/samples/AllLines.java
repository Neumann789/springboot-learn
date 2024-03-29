/*
 * Copyright 2008-2010 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.test.btrace.samples;

import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

/*
 * This sample prints a line every time any line
 * of code of java.lang.Thread class is reached.
 * The line param may be set to any particular
 * value so that the probe fires only when that line
 * is reached. But, the value -1 means all line numbers.
 */
@BTrace
public class AllLines {
    @OnMethod(
        clazz="java.lang.Thread",
        location=@Location(value=Kind.LINE, line=-1)
    )
    public static void online(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {
        print(Strings.strcat(pcn, "."));
        print(Strings.strcat(pmn, ":"));
        println(line);
    } 
}
