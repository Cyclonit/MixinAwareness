package de.cyclonit.mixinawareness;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Kinds.KindSelector;
import com.sun.tools.javac.comp.Resolve;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.Name;

import java.lang.reflect.Method;

public class ResolveUtils {

    private final Resolve resolve;

    private final Env<AttrContext> env;

    private final Method resolveIdentMethod;


    public ResolveUtils(CompilationUnitTree tree, Context context) {
        this.resolve = Resolve.instance(context);

        if (tree.getKind() == Tree.Kind.COMPILATION_UNIT) {
            Enter enter = Enter.instance(context);
            env = enter.getTopLevelEnv((JCTree.JCCompilationUnit)tree);
        }
        else {
            throw new RuntimeException("Unsupported Tree.Kind " + tree.getKind().name() + ", expected " + Tree.Kind.COMPILATION_UNIT.name());
        }

        try {
            resolveIdentMethod = resolve.getClass().getDeclaredMethod("resolveIdent", JCDiagnostic.DiagnosticPosition.class, Env.class, Name.class, Kinds.KindSelector.class);
            resolveIdentMethod.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException("Could not resolve method Resolve.resolveIdent(...).", ex);
        }
    }


    public Symbol resolveIdent(JCIdent ident, KindSelector kind) {
        try {
            return (Symbol) resolveIdentMethod.invoke(resolve, ident.pos(), env, ident.name, kind);
        } catch (Exception ex) {
            throw new RuntimeException("Could not invoke method Resolve.resolveIdent(...).", ex);
        }
    }
}
