package com.alebian.javaexperiments.repository.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** This class is in charge of iterating through the pages of a query returned by Fetcher. */
public class PageableIterator<T> implements Iterator<T> {
    private static final int FIRST_PAGE = 0;

    private final Fetcher<?, T> fetcher;
    private Pageable page;
    private List<T> currentData;
    private int cursor;

    public PageableIterator(Fetcher<?, T> fetcher, int pageSize) {
        this.fetcher = fetcher;
        this.page = PageRequest.of(FIRST_PAGE, pageSize);
        this.currentData = new ArrayList<>();
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        if (hasDataLoaded()) {
            return true;
        }

        tryToFetchMoreData();
        return !currentData.isEmpty();
    }

    @Override
    public T next() {
        try {
            return currentData.get(cursor++);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void tryToFetchMoreData() {
        currentData = fetcher.fetch(page);
        page = page.next();
        cursor = 0;
    }

    private boolean hasDataLoaded() {
        return cursor < currentData.size();
    }
}
