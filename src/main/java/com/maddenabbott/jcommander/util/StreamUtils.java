package com.maddenabbott.jcommander.util;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {
    public static <T> Stream<T> iterate(final T seed, final Predicate<? super T> hasNext, final UnaryOperator<T> next) {
        Objects.requireNonNull(next);
        Objects.requireNonNull(hasNext);
        Spliterator<T> spliterator = new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            T prev;
            boolean started;
            boolean finished;

            public boolean tryAdvance(Consumer<? super T> action) {
                Objects.requireNonNull(action);
                if (this.finished) {
                    return false;
                } else {
                    T t;
                    if (this.started) {
                        t = next.apply(this.prev);
                    } else {
                        t = seed;
                        this.started = true;
                    }

                    if (!hasNext.test(t)) {
                        this.prev = null;
                        this.finished = true;
                        return false;
                    } else {
                        action.accept(this.prev = t);
                        return true;
                    }
                }
            }

            public void forEachRemaining(Consumer<? super T> action) {
                Objects.requireNonNull(action);
                if (!this.finished) {
                    this.finished = true;
                    T t = this.started ? next.apply(this.prev) : seed;

                    for(this.prev = null; hasNext.test(t); t = next.apply(t)) {
                        action.accept(t);
                    }

                }
            }
        };
        return StreamSupport.stream(spliterator, false);
    }


}
