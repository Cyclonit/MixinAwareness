package de.cyclonit.mixinawareness.test;

import java.lang.String;

public @interface Mixin {

    Class<?> target() default String.class;

}
