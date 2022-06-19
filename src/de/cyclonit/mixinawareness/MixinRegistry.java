package de.cyclonit.mixinawareness;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Kinds.KindSelector;

import java.util.*;

public class MixinRegistry {
    protected static final Context.Key<MixinRegistry> mixinRegistryKey = new Context.Key<>();

    private final Map<Type.ClassType, List<Type.ClassType>> mixins = new HashMap<>();

    private final Set<Type.ClassType> mixinInterfaces = new HashSet<>();


    private Enter enter;

    private ResolveUtils resolveUtils;

    private Names names;


    public static MixinRegistry instance(Context context) {
        MixinRegistry instance = context.get(mixinRegistryKey);
        if (instance == null)
            instance = new MixinRegistry(context);
        return instance;
    }

    protected MixinRegistry(Context context) {
        context.put(mixinRegistryKey, this);
        resolveUtils = new ResolveUtils(context);
        enter = Enter.instance(context);
        names = Names.instance(context);
    }


    public void addMixin(Type.ClassType mixinType, Type.ClassType targetType) {

        if (!mixins.containsKey(targetType))
            mixins.put(targetType, new ArrayList<>());

        mixins.get(targetType).add(mixinType);

        for (Type mixinInterface : mixinType.interfaces_field)
            mixinInterfaces.add((Type.ClassType) mixinInterface);
    }

    public boolean isMixinInterface(Type.ClassType type) {
        return mixinInterfaces.contains(type);
    }
}
