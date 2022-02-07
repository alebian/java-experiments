package com.alebian.javaexperiments.repository.pageable;

import javax.validation.constraints.NotNull;
import java.util.Iterator;

/** This class represents a pageable collection and hides the pagination from the caller. */
public class PageableCollection<T> implements Iterable<T> {
    private static final int DEFAULT_PAGE_SIZE = 1000;

    private final Fetcher<?, T> fetcher;
    private final int pageSize;

    public PageableCollection(Fetcher<?, T> fetcher, int pageSize) {
        this.fetcher = fetcher;
        this.pageSize = pageSize;
    }

    public PageableCollection(Fetcher<?, T> fetcher) {
        this(fetcher, DEFAULT_PAGE_SIZE);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new PageableIterator<>(fetcher, pageSize);
    }
}
