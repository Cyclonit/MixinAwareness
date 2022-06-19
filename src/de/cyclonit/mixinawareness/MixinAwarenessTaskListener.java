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
//        if (e.getKind() == TaskEvent.Kind.ENTER) {
//            TreeScanner<Object, Object> scanner = new MixinTreeScanner(context);
//            System.out.println("Started ENTER ");
//            e.getCompilationUnit().accept(scanner, null);
//        }

        if (e.getKind() == TaskEvent.Kind.ANALYZE) {
            TreeScanner<Object, Object> scanner = new MixinCastTreeInjector(context);
            e.getCompilationUnit().accept(scanner, null);
        }
    }

    public void finished(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.ENTER) {
            TreeScanner<Object, Object> scanner = new MixinTreeScanner(context);
            e.getCompilationUnit().accept(scanner, null);
        }

    }
}
