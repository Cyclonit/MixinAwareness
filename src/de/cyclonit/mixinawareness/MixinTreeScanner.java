package de.cyclonit.mixinawareness;

import com.sun.source.tree.*;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.code.Type;

import java.util.ArrayList;
import java.util.List;

public class MixinTreeScanner extends TreeScanner<Object, Object> {

    private static final String MIXIN_ANNOTATION_FULLNAME = "de.cyclonit.mixinawareness.test.Mixin";


    private CompilationUnitTree tree;

    private final TreeMaker treeMaker;

    private final MixinRegistry mixinRegistry;

    private final Names names;


    public MixinTreeScanner(Context context) {
        this.treeMaker = TreeMaker.instance(context);
        this.mixinRegistry = MixinRegistry.instance(context);
        this.names = Names.instance(context);
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
        for (AnnotationTree annotationTree : modifiers.getAnnotations()) {
            JCAnnotation annotation = (JCAnnotation) annotationTree;

            JCIdent ident = (JCIdent) annotation.getAnnotationType();
            ClassSymbol annotationClassSym = (ClassSymbol) ident.sym;

            // TODO: Find a better way to identify the mixin annotation
            if (annotationClassSym.fullname.toString().equals(MIXIN_ANNOTATION_FULLNAME)) {

                // get the mixin type
                JCClassDecl classDecl = (JCClassDecl) node;
                Type.ClassType mixinType = (Type.ClassType) classDecl.sym.type;

                // get the target type
                Type.ClassType targetType = null;
                for (JCExpression argExpr : annotation.args) {

                    JCAssign argAssign = (JCAssign) argExpr;
                    JCIdent argIdent = (JCIdent) argAssign.getVariable();

                    if (argIdent.name.contentEquals("value"))
                    {
                        JCFieldAccess valueExpr = (JCFieldAccess) argAssign.getExpression();
                        targetType = (Type.ClassType) valueExpr.selected.type;
                    }
                }
                if (targetType == null) {
                    throw new RuntimeException("Could not resolve Mixin target type on Mixin class " + mixinType + ".");
                }

                mixinRegistry.addMixin(mixinType, targetType);

                // only a single @Mixin annotation is permitted per class
                break;
            }
        }

        return result;
    }

    private Name formFlatName(JCFieldAccess qualifiedAccess) {
        String nameString = qualifiedAccess.toString();
        return names.fromString(nameString);
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
