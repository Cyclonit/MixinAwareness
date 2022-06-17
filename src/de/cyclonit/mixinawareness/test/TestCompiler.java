package de.cyclonit.mixinawareness.test;

import de.cyclonit.mixinawareness.MixinAwarenessPlugin;
import de.cyclonit.mixinawareness.mock.SimpleFileManager;
import de.cyclonit.mixinawareness.mock.SimpleSourceFile;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TestCompiler {
    public byte[] compile(List<SimpleSourceFile> compilationUnits) {
        StringWriter output = new StringWriter();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        SimpleFileManager fileManager = new SimpleFileManager(compiler.getStandardFileManager(null, null, null));

        List<String> arguments = new ArrayList<>(asList("-classpath", System.getProperty("java.class.path"), "-Xplugin:" + MixinAwarenessPlugin.NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(output, fileManager, null, arguments, null, compilationUnits);
        
        boolean success = task.call();
        System.out.print(output);

        return fileManager.getCompiled().iterator().next().getCompiledBinaries();
    }
}