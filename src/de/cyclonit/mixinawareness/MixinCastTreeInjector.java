package de.cyclonit.mixinawareness;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.code.Kinds.KindSelector;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.List;

public class MixinCastTreeInjector extends TreeScanner<Object, Object> {

    private final TreeMaker treeMaker;

    private final Enter enter;

    private final Attr attr;

    private final MixinRegistry mixinRegistry;

    private final ResolveUtils resolveUtils;


    public MixinCastTreeInjector(Context context) {
        this.treeMaker = TreeMaker.instance(context);
        this.enter = Enter.instance(context);
        this.mixinRegistry = MixinRegistry.instance(context);
        this.attr = Attr.instance(context);
        this.resolveUtils = ResolveUtils.instance(context);
    }


    private Env<AttrContext> env;

    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, Object o) {
        if (node instanceof JCCompilationUnit tree) {
            this.env = enter.getTopLevelEnv(tree);
            attr.attribTopLevel(env);
        }

        return super.visitCompilationUnit(node, o);
    }

    @Override
    public Object visitClass(ClassTree node, Object o) {

        return super.visitClass(node, o);
    }


    /**
     * input:
     *   MCBlockPos blockPos = new BlockPos(1,2,3);
     *
     * output:
     *   MCBlockPos blockPos = (MCBlockPos) new BlockPos(1,2,3);
     *
     * @param node
     * @param o
     * @return
     */
    @Override
    public Object visitVariable(VariableTree node, Object o) {
        Object result = super.visitVariable(node, o);

        if (node instanceof JCVariableDecl variable
            && variable.init != null
            && variable.vartype instanceof JCIdent vartypeIdent) {

            Type.ClassType vartype = null;

            // if the variable's type is already determined, use it
            if (vartypeIdent.type != null) {
                vartype = (Type.ClassType) vartypeIdent.type;
            }

            // otherwise, resolve its type using the Resolver
            else {
                Symbol vartypeSym = resolveUtils.resolveIdent(env, vartypeIdent, KindSelector.TYP);
                if (vartypeSym.type instanceof Type.ClassType symType)
                    vartype = symType;
                else
                    System.out.println("Could not resolve type of variable " + node + ".");
            }

            // if the variable's type is a mixin interface, inject a cast
            // TODO: Determine type of the init expression and only add the cast if necessary.
            // TODO: Verify that the expression's type can be cast to the mixin interface.
            if (vartype != null && mixinRegistry.isMixinInterface(vartype))
                variable.init = treeMaker.TypeCast(vartype, variable.init);
        }

        return result;
    }


    /**
     * input:
     *   blockPos = new BlockPos(1,2,3);
     *
     * output:
     *   blockPos = (MCBlockPos) new BlockPos(1,2,3);
     *
     * @param node
     * @param o
     * @return
     */
    @Override
    public Object visitExpressionStatement(ExpressionStatementTree node, Object o) {

        if (node instanceof JCExpressionStatement expression
            && expression.expr instanceof JCAssign assignment) {

            // ex: x = y;
            if (assignment.lhs instanceof JCIdent varIdent) {

                // resolve the left-hand side using the Resolver
                Symbol vartypeSym = resolveUtils.resolveIdent(env, varIdent, KindSelector.VAR);

                System.out.println("!");
            }

            // ex: this.x = y;
            else if (assignment.lhs instanceof JCFieldAccess fieldIdent) {
                // TODO: Implement transformation for setting fields
            }

        }

        return super.visitExpressionStatement(node, o);
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

        Object result = super.visitMethodInvocation(node, o);
        return result;
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

        Object result = super.visitMemberSelect(node, o);
        return result;
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
