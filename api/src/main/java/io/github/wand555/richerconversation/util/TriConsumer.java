package io.github.wand555.richerconversation.util;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);

    default <V> TriConsumer<A, B, C> andThen(
            TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (l, r, s) -> {
            accept(l, r, s);
            after.accept(l, r, s);
        };
    }
}
