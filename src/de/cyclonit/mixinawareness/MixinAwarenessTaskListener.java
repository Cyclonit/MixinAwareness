package de.cyclonit.mixinawareness;

import com.sun.source.tree.Tree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.source.util.TreeScanner;
import java.util.List;

public class MixinAwarenessTaskListener implements TaskListener {

    private final Context context;


    public MixinAwarenessTaskListener(Context context) {
        this.context = context;
    }


    public void started(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.ENTER) {
            TreeScanner<Object, Object> scanner = new MixinTreeScanner(context);
            e.getCompilationUnit().accept(scanner, null);
        }
    }

    public void finished(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.ENTER) {
            TreeScanner<Object, Object> scanner = new MixinCastTreeInjector(e.getCompilationUnit(), context);
            e.getCompilationUnit().accept(scanner, null);
        }
    }


    private JCTree.JCClassDecl findTopLevel(Symbol.ClassSymbol type, List<? extends Tree> typeDecls)
    {
        for( Tree tree: typeDecls )
        {
            if( tree instanceof JCTree.JCClassDecl && ((JCTree.JCClassDecl)tree).sym == type )
            {
                return (JCTree.JCClassDecl)tree;
            }
        }
        return null;
    }
}
