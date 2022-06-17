package de.cyclonit.mixinawareness.mock;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.stream.Collectors;

public class SimpleSourceFile extends SimpleJavaFileObject {

    private final String content;

    public SimpleSourceFile(String filename) throws IOException {
        super(getUri(filename), JavaFileObject.Kind.SOURCE);

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    private static URI getUri(String filename) {
        return URI.create(
            String.format("file://%s", filename
            ));
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }
}
