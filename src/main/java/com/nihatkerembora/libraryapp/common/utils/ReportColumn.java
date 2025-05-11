package com.nihatkerembora.libraryapp.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public class ReportColumn<T> {
    private final String header;
    private final Function<T, String> valueExtractor;

    public String extract(T item) {
        return valueExtractor.apply(item);
    }
}
