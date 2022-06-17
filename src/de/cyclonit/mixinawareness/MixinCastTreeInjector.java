package de.cyclonit.mixinawareness;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

import java.util.ArrayList;
import java.util.List;

public class MixinCastTreeInjector extends TreeScanner<Object, Object> {

    private final CompilationUnitTree tree;

    private final TreeMaker treeMaker;

    private final ResolveUtils resolveUtils;

    private final Enter enter;


    public MixinCastTreeInjector(CompilationUnitTree tree, Context context) {
        this.tree = tree;
        this.resolveUtils = new ResolveUtils(tree, context);
        this.treeMaker = TreeMaker.instance(context);
        this.enter = Enter.instance(context);
    }


    @Override
    public Object visitAssignment(AssignmentTree node, Object o) {
        return super.visitAssignment(node, o);
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object o) {

        // TODO: support type arguments
        List<? extends Tree> typeArguments = node.getTypeArguments();

        // TODO: what other options are there besides JCIdent?
        ExpressionTree methodSelect = node.getMethodSelect();

        // TODO: Support proper expressions as arguments instead of only JCIdent
        List<? extends ExpressionTree> arguments = node.getArguments();
        List<Type> argumentTypes = new ArrayList<>();
        for (ExpressionTree argumentExpr : arguments) {
            if (argumentExpr instanceof JCIdent) {
                System.out.println("!");
            }
        }

        /*
        JCTree.JCMethodInvocation invocation = (JCTree.JCMethodInvocation) node;
        JCIdent methodIdent = (JCIdent) invocation.meth;
        Symbol methodSymbol = resolveUtils.resolveIdent(methodIdent, KindSelector.MTH);
        */

        // ToDo: either
        //  pro-active: Consider mixed in interfaces during method resolution
        //  re-active: If the method could not be resolved, try by substituting the resolution with mixed in interfaces

        return super.visitMethodInvocation(node, o);
    }

    /**
     * Injects casts for accessing mixed in methods on objects.
     *
     * @param node
     * @param o
     * @return
     */
    @Override
    public Object visitMemberSelect(MemberSelectTree node, Object o) {

        // TODO:
        //  Check if node.selected is a mixed in object.
        //  If it is, attempt to resolve node.name normally.
        //  If normal resolving doesn't work, try using the mixin interfaces instead.
        //  If an interface implements the member, inject a cast thi the interface.

        return super.visitMemberSelect(node, o);
    }

    /*
    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {

        JCIdent ident = (JCIdent) node;
        Symbol symbol = resolveUtils.resolveIdent(ident, KindSelector.TYP);

        return super.visitIdentifier(node, o);
    }
     */
}
