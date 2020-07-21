package com.maddenabbott.jcommander.util;

import java.util.function.Function;

public class ExceptionUtils {

    public static Throwable getCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return throwable != cause ? cause : null;
    }

    public static boolean hasMessage(Throwable throwable) {
        return throwable != null && throwable.getMessage() != null;
    }

    public static String getFirstMessage(Throwable throwable, Function<Throwable, String> orElse) {
        return StreamUtils.iterate(throwable, ExceptionUtils::hasMessage, ExceptionUtils::getCause)
                .map(Throwable::getMessage)
                .findFirst()
                .orElseGet(() -> orElse.apply(throwable));
    }
}
