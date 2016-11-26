package com.hegel.core.functions;

import java.util.function.BooleanSupplier;

@SuppressWarnings({"WeakerAccess", "unused"})
@FunctionalInterface
public interface ExceptionalBooleanSupplier<E extends Throwable> extends BooleanSupplier {

    boolean is() throws E;

    @Override
    default boolean getAsBoolean() {
        try {
            return is();
        } catch (Throwable e) {
            //noinspection unchecked
            return mapException((E) e);
        }
    }

    default boolean mapException(E e) {
        throw new RuntimeException(e);
    }

    static <E extends Throwable> boolean call(ExceptionalBooleanSupplier<E> exceptionalBooleanSupplier) {
        return exceptionalBooleanSupplier.getAsBoolean();
    }
}
