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
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
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


    private JCCompilationUnit tree;

    private Env<AttrContext> env;

    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, Object o) {
        if (node instanceof JCCompilationUnit tree) {
            this.env = enter.getTopLevelEnv(tree);
            this.tree = tree;
            attr.attrib(env);
        }

        return super.visitCompilationUnit(node, o);
    }


    /**
     * <p>
     *     Handles class declarations. <code>node</code> is always of type <code>JCClassDecl</code>.
     * </p>
     * 
     * @param node
     * @param o
     * @return
     */
    @Override
    public Object visitClass(ClassTree node, Object o) {
        if (node instanceof JCClassDecl clazz) {
            attr.attribClass(clazz.pos(), ((JCClassDecl) node).sym);
        }

        return super.visitClass(node, o);
    }


    /**
     * <p>
     *     Handles variable declarations with initializers. <code>node</code> is always of type <code>JCVariableDecl</code>.
     *     We only care about declarations that include an initializer for adding casts.
     * </p>
     * <p>
     *     We must determine the variables type. If it is a Mixin interface, we inject the appropriate cast.
     * </p>
     * <note>For assignments outside of declarations see <code>visitExpressionStatement</code>.</note>
     * <p>
     *     input: <code>MCBlockPos blockPos = new BlockPos(1,2,3);</code><br />
     *     output: <code>MCBlockPos blockPos = <i>(MCBlockPos)</i> new BlockPos(1,2,3);</code>
     * </p>
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
     * <p>
     *     Handles assignments to previously declared variables and fields. <code>node</code> is always of type
     *     <code>JCExpressionStatement</code>. If the expression is an assignment, its <code>expr</code> will be of type
     *     <code>JCAssign</code>.
     * </p>
     * <p>
     *     We have to differentiate between assignments to variables (lhs is <code>JCIdent</code>) and assignments to
     *     fields (lhs is <code>JCFieldAccess</code>). In either case, we need to determine the type of the variable/field
     *     and inject a cast, if it is a Mixin interface.
     * </p>
     * <note>For variable initializers see <code>visitVariable</code>.</note>
     * <p>
     *     input: <code>blockPos = new BlockPos(1,2,3);</code><br />
     *     output: <code>blockPos = <i>(MCBlockPos)</i> new BlockPos(1,2,3);</code>
     * </p>
     */
    @Override
    public Object visitExpressionStatement(ExpressionStatementTree node, Object o) {

        if (node instanceof JCExpressionStatement expression
            && expression.expr instanceof JCAssign assignment) {

            // ex: x = y;
            if (assignment.lhs instanceof JCIdent varIdent) {

                // resolve the left-hand side using the Resolver
                Symbol vartypeSym = resolveUtils.resolveIdent(env, varIdent, KindSelector.VAR);

            }

            // ex: this.x = y;
            else if (assignment.lhs instanceof JCFieldAccess fieldIdent) {
                // TODO: Implement transformation for setting fields
            }

        }

        return super.visitExpressionStatement(node, o);
    }


    /**
     * <p>
     *     Handles method invocations. The node provides the list of type arguments, the method arguments as well as a
     *     method selector. The method selector may either be a <code>JCIdent</code> (e.g. <code><i>doSomething</i>()</code>,
     *     a <code>JCFieldAccess</code> (e.g. <code><i>object.doSomething</i>()</code>) or an entire expression.
     * </p>
     * <p>
     *     Given the method selector, type arguments and method arguments, we must first determine the method being called.
     *     If the method signature uses Mixin interfaces, javac's default method resolution will fail, unless the provided
     *     arguments implement the interface.
     * </p>
     * <p>
     *     input: <code>doSomething(blockPos);</code><br />
     *     output: <code>doSomething((MCBlockPos) blockPos);</code>
     * </p>
     */
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
}
