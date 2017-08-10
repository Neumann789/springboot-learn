/* CollapseImportsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.strobel.assembler.metadata.PackageReference;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.CompilationUnit;
import com.strobel.decompiler.languages.java.ast.Identifier;
import com.strobel.decompiler.languages.java.ast.ImportDeclaration;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.PackageDeclaration;
import com.strobel.decompiler.languages.java.ast.Roles;

public class CollapseImportsTransform implements IAstTransform
{
    private final DecompilerSettings _settings;
    
    public CollapseImportsTransform(DecompilerContext context) {
	_settings = context.getSettings();
    }
    
    public void run(AstNode root) {
	if (root instanceof CompilationUnit) {
	    CompilationUnit compilationUnit = (CompilationUnit) root;
	    AstNodeCollection imports;
	label_1697:
	    {
		if (!_settings.getForceExplicitImports()) {
		    imports = compilationUnit.getImports();
		    PackageDeclaration packageDeclaration
			= ((PackageDeclaration)
			   compilationUnit.getChildByRole(Roles.PACKAGE));
		    if (!packageDeclaration.isNull())
			PUSH packageDeclaration.getName();
		    else
			PUSH null;
		} else {
		    removeRedundantImports(compilationUnit);
		    return;
		}
	    }
	    String filePackage = POP;
	    if (!imports.isEmpty()) {
		java.util.Set newImports = new LinkedHashSet();
		java.util.List removedImports = new ArrayList();
		Iterator i$ = imports.iterator();
		for (;;) {
		    if (!i$.hasNext()) {
			if (!removedImports.isEmpty()) {
			    ImportDeclaration lastRemoved
				= ((ImportDeclaration)
				   removedImports
				       .get(removedImports.size() - 1));
			    Iterator i$_0_ = newImports.iterator();
			    for (;;) {
				if (!i$_0_.hasNext()) {
				    Iterator i$_1_ = removedImports.iterator();
				    while (i$_1_.hasNext()) {
					ImportDeclaration removedImport
					    = (ImportDeclaration) i$_1_.next();
					removedImport.remove();
				    }
				    break;
				}
				String packageName = (String) i$_0_.next();
				compilationUnit.insertChildAfter
				    (lastRemoved,
				     (new ImportDeclaration
				      (PackageReference.parse(packageName))),
				     CompilationUnit.IMPORT_ROLE);
			    }
			}
		    } else {
			ImportDeclaration oldImport
			    = (ImportDeclaration) i$.next();
			Identifier importedType
			    = oldImport.getImportIdentifier();
			if (importedType != null && !importedType.isNull()) {
			    TypeReference type
				= ((TypeReference)
				   oldImport.getUserData(Keys.TYPE_REFERENCE));
			    if (type != null) {
			    label_1698:
				{
				    String packageName = type.getPackageName();
				    if (!StringUtilities
					     .isNullOrEmpty(packageName)
					&& !StringUtilities.equals(packageName,
								   "java.lang")
					&& !(StringUtilities.equals
					     (packageName, filePackage)))
					newImports.add(packageName);
				    break label_1698;
				}
				removedImports.add(oldImport);
			    }
			}
			continue;
		    }
		    return;
		}
		return;
	    }
	    break label_1697;
	} else {
	    /* empty */
	}
	return;
    }
    
    private void removeRedundantImports(CompilationUnit compilationUnit) {
	AstNodeCollection imports = compilationUnit.getImports();
    label_1699:
	{
	    PackageDeclaration packageDeclaration
		= ((PackageDeclaration)
		   compilationUnit.getChildByRole(Roles.PACKAGE));
	    if (!packageDeclaration.isNull())
		PUSH packageDeclaration.getName();
	    else
		PUSH null;
	    break label_1699;
	}
	String filePackage = POP;
	Iterator i$ = imports.iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    ImportDeclaration oldImport = (ImportDeclaration) i$.next();
	    Identifier importedType = oldImport.getImportIdentifier();
	    if (importedType != null && !importedType.isNull()) {
		TypeReference type
		    = ((TypeReference)
		       oldImport.getUserData(Keys.TYPE_REFERENCE));
		if (type != null) {
		    String packageName = type.getPackageName();
		    if (StringUtilities.isNullOrEmpty(packageName)
			|| StringUtilities.equals(packageName, "java.lang")
			|| StringUtilities.equals(packageName, filePackage))
			oldImport.remove();
		}
	    }
	    continue;
	}
    }
}
