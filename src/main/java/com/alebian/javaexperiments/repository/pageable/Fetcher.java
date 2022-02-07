package com.alebian.javaexperiments.repository.pageable;

import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * This class is a wrapper around a JPA repository used to fetch a list of some entity T that is
 * paginated in a method of S.
 *
 * @param <S> a JPA repository
 * @param <T> a JPA entity returned by some method of the repository
 */
public abstract class Fetcher<S, T> {
    protected S source;

    protected Fetcher(S source) {
        this.source = source;
    }

    /**
     * The implementation of this method should call a paginated method of S that returns a List of
     * T.
     */
    public abstract List<T> fetch(Pageable pageable);
}
