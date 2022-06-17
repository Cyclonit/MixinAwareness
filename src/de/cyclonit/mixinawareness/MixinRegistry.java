package de.cyclonit.mixinawareness;

import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.util.Context;

import java.util.ArrayList;
import java.util.List;

public class MixinRegistry {
    protected static final Context.Key<MixinRegistry> mixinRegistryKey = new Context.Key<>();

    private final List<MixinReference> mixins = new ArrayList<>();


    public static MixinRegistry instance(Context context) {
        MixinRegistry instance = context.get(mixinRegistryKey);
        if (instance == null)
            instance = new MixinRegistry(context);
        return instance;
    }

    protected MixinRegistry(Context context) {
        context.put(mixinRegistryKey, this);
    }


    public void addMixin(JCFieldAccess targetIdentifier, JCFieldAccess mixinIdentifier, List<JCFieldAccess> interfaceIdentifiers) {
        mixins.add(new MixinReference(targetIdentifier, mixinIdentifier, interfaceIdentifiers));
    }


    public class MixinReference {

        public final JCFieldAccess targetIdentifier;

        public final JCFieldAccess mixinIdentifier;

        public final List<JCFieldAccess> interfaceIdentifiers;


        public MixinReference(JCFieldAccess targetIdentifier, JCFieldAccess mixinIdentifier, List<JCFieldAccess> interfaceIdentifiers) {
            this.targetIdentifier = targetIdentifier;
            this.mixinIdentifier = mixinIdentifier;
            this.interfaceIdentifiers = interfaceIdentifiers;
        }
    }
}
