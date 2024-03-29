/* SerializePreserver Copyright (C) 1999-2002 Jochen Hoenicke.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * $Id: SerializePreserver.java 1367 2002-05-29 12:06:47Z hoenicke $
 */

package net.sf.jode.obfuscator.modules;
import net.sf.jode.obfuscator.*;

import java.lang.reflect.Modifier;
///#def COLLECTIONS java.util
import java.util.Collection;
///#enddef

public class SerializePreserver implements IdentifierMatcher {
    boolean onlySUID = true;

    public SerializePreserver() {
    }

    public void setOption(String option, Collection values) {
	if (option.equals("all")) {
	    onlySUID = false;
	} else
	    throw new IllegalArgumentException("Invalid option `"+option+"'.");
    }

    public final boolean matchesSub(Identifier ident, String name) {
	if (ident instanceof PackageIdentifier)
	    return true;
	if (ident instanceof ClassIdentifier) {
	    ClassIdentifier clazz = (ClassIdentifier) ident;
	    return (clazz.isSerializable() 
		    && (!onlySUID || clazz.hasSUID()));
	}
	return false;
    }

    public final boolean matches(Identifier ident) {
	ClassIdentifier clazz;
	if (ident instanceof ClassIdentifier)
	    clazz = (ClassIdentifier) ident;
	else if (ident instanceof FieldIdentifier)
	    clazz = (ClassIdentifier) ident.getParent();
	else
	    return false;

	if (!clazz.isSerializable() 
	    || (onlySUID && !clazz.hasSUID()))
	    return false;

	if (ident instanceof FieldIdentifier) {
	    FieldIdentifier field = (FieldIdentifier) ident;
	    if ((field.getModifiers() 
		 & (Modifier.TRANSIENT | Modifier.STATIC)) == 0)
		return true;
	    if (ident.getName().equals("serialPersistentFields")
		|| ident.getName().equals("serialVersionUID"))
		return true;
	} else if (ident instanceof MethodIdentifier) {
	    if (ident.getName().equals("writeObject")
		&& ident.getType().equals("(Ljava.io.ObjectOutputStream)V"))
		return true;
	    if (ident.getName().equals("readObject")
		&& ident.getType().equals("(Ljava.io.ObjectInputStream)V"))
		return true;
	} else if (ident instanceof ClassIdentifier) {
	    if (!clazz.hasSUID())
		clazz.addSUID();
	    return true;
	}
	return false;
    }

    public final String getNextComponent(Identifier ident) {
	return null;
    }
}
