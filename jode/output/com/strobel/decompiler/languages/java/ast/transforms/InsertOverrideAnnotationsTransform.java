/* InsertOverrideAnnotationsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.metadata.CompilerTarget;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MetadataParser;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.Predicate;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.Annotation;
import com.strobel.decompiler.languages.java.ast.AstBuilder;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.SimpleType;

public final class InsertOverrideAnnotationsTransform
    extends ContextTrackingVisitor
{
    private static final String OVERRIDE_ANNOTATION_NAME
	= "java/lang/Override";
    private final AstBuilder _astBuilder;
    
    public InsertOverrideAnnotationsTransform(DecompilerContext context) {
	super(context);
	_astBuilder = (AstBuilder) context.getUserData(Keys.AST_BUILDER);
    }
    
    public Void visitMethodDeclaration(MethodDeclaration node, Void _) {
	tryAddOverrideAnnotation(node);
	return (Void) super.visitMethodDeclaration(node, _);
    }
    
    private void tryAddOverrideAnnotation(MethodDeclaration node) {
	boolean foundOverride = false;
	Iterator i$ = node.getAnnotations().iterator();
	while (i$.hasNext()) {
	    Annotation annotation;
	    annotation = (Annotation) i$.next();
	    TypeReference annotationType
		= ((TypeReference)
		   annotation.getType().getUserData(Keys.TYPE_REFERENCE));
	    IF (!StringUtilities.equals(annotationType.getInternalName(),
					"java/lang/Override"))
		/* empty */
	    foundOverride = true;
	    break;
	}
	if (!foundOverride) {
	    final MethodDefinition method
		= (MethodDefinition) node.getUserData(Keys.METHOD_DEFINITION);
	    if (!method.isStatic() && !method.isConstructor()
		&& !method.isTypeInitializer()) {
		TypeDefinition declaringType = method.getDeclaringType();
		if (declaringType.getCompilerMajorVersion()
		    >= CompilerTarget.JDK1_6.majorVersion) {
		    TypeReference annotationType
			= new MetadataParser(declaringType)
			      .parseTypeDescriptor("java/lang/Override");
		    List candidates = MetadataHelper
					  .findMethods(declaringType, new Predicate() {
			{
			    super();
			}
			
			public boolean test(MethodReference reference) {
			    return StringUtilities.equals(reference.getName(),
							  method.getName());
			}
		    }, false, true);
		    Iterator i$_3_ = candidates.iterator();
		    Annotation annotation;
		label_1736:
		    {
			while (i$_3_.hasNext()) {
			    MethodReference candidate
				= (MethodReference) i$_3_.next();
			    if (MetadataHelper.isOverride(method, candidate)) {
				annotation = new Annotation();
				if (_astBuilder == null)
				    annotation.setType
					(new SimpleType(annotationType
							    .getSimpleName()));
				else
				    annotation.setType(_astBuilder.convertType
						       (annotationType));
				break label_1736;
			    }
			}
			return;
		    }
		    node.getAnnotations().add(annotation);
		    break label_1736;
		} else {
		    /* empty */
		}
	    }
	}
	return;
    }
}
