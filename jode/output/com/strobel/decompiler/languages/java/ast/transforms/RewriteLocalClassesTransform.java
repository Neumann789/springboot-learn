/* RewriteLocalClassesTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.LocalClassHelper;
import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.TypeDeclaration;

public class RewriteLocalClassesTransform extends ContextTrackingVisitor
{
    private final Map _localTypes = new LinkedHashMap();
    private final Map _instantiations = new LinkedHashMap();
    
    private final class PhaseOneVisitor extends ContextTrackingVisitor
    {
	protected PhaseOneVisitor(DecompilerContext context) {
	    super(context);
	}
	
	public Void visitTypeDeclaration(TypeDeclaration typeDeclaration,
					 Void _) {
	label_1797:
	    {
		TypeDefinition type
		    = ((TypeDefinition)
		       typeDeclaration.getUserData(Keys.TYPE_DEFINITION));
		if (type != null
		    && (isLocalOrAnonymous(type) || type.isAnonymous()))
		    _localTypes.put(type, typeDeclaration);
		break label_1797;
	    }
	    return (Void) super.visitTypeDeclaration(typeDeclaration, _);
	}
    }
    
    public RewriteLocalClassesTransform(DecompilerContext context) {
	super(context);
    }
    
    public void run(AstNode compilationUnit) {
	PhaseOneVisitor phaseOneVisitor = new PhaseOneVisitor(context);
	compilationUnit.acceptVisitor(phaseOneVisitor, null);
	super.run(compilationUnit);
	Iterator i$ = _localTypes.keySet().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    TypeReference localType = (TypeReference) i$.next();
	    TypeDeclaration declaration
		= (TypeDeclaration) _localTypes.get(localType);
	    List instantiations = (List) _instantiations.get(localType);
	    PUSH context;
	label_1790:
	    {
		PUSH declaration;
		if (instantiations == null)
		    PUSH Collections.emptyList();
		else
		    PUSH instantiations;
		break label_1790;
	    }
	    LocalClassHelper.replaceClosureMembers(POP, POP, POP);
	}
    }
    
    public Void visitObjectCreationExpression(ObjectCreationExpression node,
					      Void _) {
	super.visitObjectCreationExpression(node, _);
	TypeReference type;
    label_1791:
	{
	    type = ((TypeReference)
		    node.getType().getUserData(Keys.TYPE_REFERENCE));
	    if (type == null)
		PUSH null;
	    else
		PUSH type.resolve();
	    break label_1791;
	}
    label_1793:
	{
	    TypeDefinition resolvedType = POP;
	    if (resolvedType != null && isLocalOrAnonymous(resolvedType)) {
		List instantiations;
	    label_1792:
		{
		    instantiations = (List) _instantiations.get(type);
		    if (instantiations == null)
			_instantiations.put(type,
					    instantiations = new ArrayList());
		    break label_1792;
		}
		instantiations.add(node);
	    }
	    break label_1793;
	}
	return null;
    }
    
    private static boolean isLocalOrAnonymous(TypeDefinition type) {
    label_1794:
	{
	    if (type != null) {
		if (!type.isLocalClass() && !type.isAnonymous())
		    PUSH false;
		else
		    PUSH true;
	    } else
		return false;
	}
	return POP;
	break label_1794;
    }
    
    public Void visitAnonymousObjectCreationExpression
	(AnonymousObjectCreationExpression node, Void _) {
	super.visitAnonymousObjectCreationExpression(node, _);
    label_1796:
	{
	    TypeDefinition resolvedType
		= (TypeDefinition) node.getTypeDeclaration()
				       .getUserData(Keys.TYPE_DEFINITION);
	    if (resolvedType != null && isLocalOrAnonymous(resolvedType)) {
		List instantiations;
	    label_1795:
		{
		    instantiations = (List) _instantiations.get(resolvedType);
		    if (instantiations == null)
			_instantiations.put(resolvedType,
					    instantiations = new ArrayList());
		    break label_1795;
		}
		instantiations.add(node);
	    }
	    break label_1796;
	}
	return null;
    }
}
