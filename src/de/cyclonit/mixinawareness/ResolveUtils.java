package de.cyclonit.mixinawareness;

import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Kinds.KindSelector;
import com.sun.tools.javac.comp.Resolve;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.Name;

import java.lang.reflect.Method;

public class ResolveUtils {
    protected static final Context.Key<ResolveUtils> resolveUtilsKey = new Context.Key<>();


    private final Resolve resolve;

    private final Method resolveIdentMethod;

    private final Method findTypeMethod;

    private final Enter enter;


    public static ResolveUtils instance(Context context) {
        ResolveUtils instance = context.get(resolveUtilsKey);
        if (instance == null)
            instance = new ResolveUtils(context);
        return instance;
    }


    protected ResolveUtils(Context context) {
        this.resolve = Resolve.instance(context);
        this.enter = Enter.instance(context);

        try {
            resolveIdentMethod = resolve.getClass().getDeclaredMethod("resolveIdent", JCDiagnostic.DiagnosticPosition.class, Env.class, Name.class, Kinds.KindSelector.class);
            resolveIdentMethod.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException("Could not resolve method Resolve.resolveIdent(...).", ex);
        }

        try {
            findTypeMethod = resolve.getClass().getDeclaredMethod("findType", Env.class, Name.class);
            findTypeMethod.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException("Could not resolve method Resolve.findType(...).", ex);
        }
    }


    public Symbol resolveName(Env<AttrContext> env, Name name, KindSelector kind) {
        try {
            return (Symbol) resolveIdentMethod.invoke(resolve, null /* TODO: Provide a correct pos */, env, name, kind);
        } catch (Exception ex) {
            throw new RuntimeException("Could not invoke method Resolve.resolveIdent(...).", ex);
        }
    }

    public Symbol resolveIdent(Env<AttrContext> env, JCIdent ident, KindSelector kind) {
        try {
            return (Symbol) resolveIdentMethod.invoke(resolve, ident.pos(), env, ident.name, kind);
        } catch (Exception ex) {
            throw new RuntimeException("Could not invoke method Resolve.resolveIdent(...).", ex);
        }
    }

    public Symbol.ClassSymbol findType(Env<AttrContext> env, Name name) {
        try {
            return (Symbol.ClassSymbol) findTypeMethod.invoke(resolve, env, name);
        } catch (Exception ex) {
            throw new RuntimeException("Could not invoke method Resolve.findType(...).", ex);
        }
    }
}
