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

/**
 * This script demonstrates the possibility to intercept
 * method calls that are about to be executed from the body of
 * a certain method. This is achieved by using the {@linkplain Kind#CALL}
 * location value.
 */
@BTrace public class AllCalls1 {
    @OnMethod(clazz="javax.swing.JTextField", method="/.*/",
              location=@Location(value=Kind.CALL, clazz="/.*/", method="/.*/"))
    public static void m(@Self Object self, @TargetMethodOrField String method, @ProbeMethodName String probeMethod) { // all calls to the methods with signature "()"
        println(Strings.strcat(method, Strings.strcat(" in ", probeMethod)));
    }
}
