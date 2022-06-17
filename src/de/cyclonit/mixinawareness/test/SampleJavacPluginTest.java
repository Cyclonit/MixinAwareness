package de.cyclonit.mixinawareness.test;

import de.cyclonit.mixinawareness.mock.SimpleSourceFile;
import de.cyclonit.mixinawareness.test.TestCompiler;
import de.cyclonit.mixinawareness.test.TestRunner;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SampleJavacPluginTest {

    private static final String CLASS_TEMPLATE
      = "package com.baeldung.javac;\n\n" +
        "interface MCBlockPos {\n" +
        "  public String asCubePos() {\n" +
        "    return \"\";\n" +
        "  }\n" +
        "}\n" +
        "\n" +
        "@Mixin(BlockPos.class)\n" +
        "public class MixinBlockPos implements MCBlockPos {\n" +
        "    public string test = \"abc\";" +
        "    public static %1$s service(%1$s i) {\n" +
        "        return i;\n" +
        "    }\n" +
        "}\n" +
        "";

    private final TestCompiler compiler = new TestCompiler();
    private final TestRunner runner = new TestRunner();

    @Test(expected = IllegalArgumentException.class)
    public void givenInt_whenNegative_thenThrowsException() throws Throwable {
        compileAndRun(double.class,-1);
    }
    
    private Object compileAndRun(Class<?> argumentType, Object argument)
        throws Throwable {

        String[] sources = { "src/BlockPos.java", "src/CubePos.java", "src/Main.java", "src/MCBlockPos.java", "src/Mixin.java", "src/MixinBlockPos.java" };
        List<SimpleSourceFile> compilationUnits = new ArrayList<>();
        for (String source : sources) {
            compilationUnits.add(new SimpleSourceFile(source));
        }

        String qualifiedClassName = "com.baeldung.javac.Test";
        byte[] byteCode = compiler.compile(compilationUnits);
        return runner.run(byteCode, qualifiedClassName, "service", new Class[] {argumentType}, argument);
    }
}