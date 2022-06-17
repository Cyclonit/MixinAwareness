package de.cyclonit.mixinawareness.mock;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SimpleFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final List<SimpleClassFile> compiled = new ArrayList<>();

    /**
     * Creates a new instance of {@code ForwardingJavaFileManager}.
     *
     * @param fileManager delegate to this file manager
     */
    public SimpleFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        SimpleClassFile result = new SimpleClassFile(
            URI.create("string://" + className));
        compiled.add(result);
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return super.getJavaFileForInput(location, className, kind);
    }

    public List<SimpleClassFile> getCompiled() {
        return compiled;
    }
}
