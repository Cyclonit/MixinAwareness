package de.cyclonit.mixinawareness;

import com.sun.source.tree.*;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.util.Name;

import java.util.ArrayList;
import java.util.List;

public class MixinTreeScanner extends TreeScanner<Object, Object> {

    private CompilationUnitTree tree;

    private final TreeMaker treeMaker;

    private final MixinRegistry mixinRegistry;


    public MixinTreeScanner(Context context) {
        this.treeMaker = TreeMaker.instance(context);
        this.mixinRegistry = MixinRegistry.instance(context);
    }


    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, Object o) {
        this.tree = node;

        return super.visitCompilationUnit(node, o);
    }

    @Override
    public Object visitClass(ClassTree node, Object o) {
        Object result = super.visitClass(node, o);

        ModifiersTree modifiers = node.getModifiers();
        for (AnnotationTree annotation : modifiers.getAnnotations()) {
            JCIdent ident = (JCIdent) annotation.getAnnotationType();
            if (ident.getName().toString().equals("Mixin")) {
                JCClassDecl classDecl = (JCClassDecl) node;

                // get the fully qualified identifier of this class
                JCFieldAccess packageName = (JCFieldAccess) tree.getPackageName();
                Name className = classDecl.name;
                JCFieldAccess mixinIdentifier = treeMaker.Select(packageName, className);

                // get the fully qualified identifier of the targeted class
                JCFieldAccess targetIdentifier = getMixinTarget(annotation);

                // get fully qualified identifiers for all implemented interfaces
                List<JCFieldAccess> interfaceIdentifiers = new ArrayList<>();
                for (JCExpression implementsExpr : classDecl.implementing) {
                    interfaceIdentifiers.add(getFullyQualifiedIdentifier(implementsExpr));
                }

                mixinRegistry.addMixin(targetIdentifier, mixinIdentifier, interfaceIdentifiers);

                // only a single @Mixin annotation is permitted per class
                break;
            }
        }

        return super.visitClass(node, o);
    }

    private JCFieldAccess getMixinTarget(AnnotationTree annotation) {
        for (ExpressionTree argTree : annotation.getArguments()) {
            if (argTree instanceof JCFieldAccess classIdentifier) {
                return getFullyQualifiedIdentifier(classIdentifier.selected);
            }
        }

        throw new RuntimeException("Could not extract target class from Mixin annotation.");
    }


    private JCFieldAccess getFullyQualifiedIdentifier(JCExpression identifierExpression) {
        if (identifierExpression instanceof JCFieldAccess)
            return getFullyQualifiedIdentifier((JCFieldAccess) identifierExpression);

        else if (identifierExpression instanceof JCIdent)
            return getFullyQualifiedIdentifier((JCIdent) identifierExpression);

        else
            throw new RuntimeException("Not an identifier.");
    }

    private JCFieldAccess getFullyQualifiedIdentifier(JCFieldAccess qualifiedIdentifier) {
        // TODO: make sure the identifier is actually FULLY qualified
        return qualifiedIdentifier;
    }

    private JCFieldAccess getFullyQualifiedIdentifier(JCIdent ident) {
        for (ImportTree importTree : tree.getImports()) {
            JCFieldAccess qualid = (JCFieldAccess) importTree.getQualifiedIdentifier();
            if (qualid.name == ident.name) {
                return qualid;
            }
        }

        throw new RuntimeException("Could not get qualified identifier for identifier " + ident + ".");
    }
}
