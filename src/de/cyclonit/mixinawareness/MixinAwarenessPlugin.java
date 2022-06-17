package de.cyclonit.mixinawareness;

import com.sun.source.util.*;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.api.BasicJavacTask;

public class MixinAwarenessPlugin implements Plugin {

    public static final String NAME = "MixinAwarenessPlugin";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init(JavacTask task, String... args) {

        BasicJavacTask javacTask = (BasicJavacTask)task;
        Context context = javacTask.getContext();

        task.addTaskListener(new MixinAwarenessTaskListener(context));
    }
}
