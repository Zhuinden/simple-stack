package com.google.auto.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoValue {

    /**
     * Specifies that AutoValue should generate an implementation of the annotated class or interface,
     * to serve as a <i>builder</i> for the value-type class it is nested within. As a simple example,
     * here is an alternative way to write the {@code Person} class mentioned in the {@link AutoValue}
     * example: <pre>
     *
     *   &#64;AutoValue
     *   abstract class Person {
     *     static Builder builder() {
     *       return new AutoValue_Person.Builder();
     *     }
     *
     *     abstract String name();
     *     abstract int id();
     *
     *     &#64;AutoValue.Builder
     *     interface Builder {
     *       Builder name(String x);
     *       Builder id(int x);
     *       Person build();
     *     }
     *   }</pre>
     *
     * @author Éamonn McManus
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    public @interface Builder {
    }
}