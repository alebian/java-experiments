package com.alebian.javaexperiments.repository.pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alebian.javaexperiments.utils.extension.VerifyNoMoreInteractions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@VerifyNoMoreInteractions
class PageableIteratorTest {
    protected static final int PAGE_SIZE = 100;

    @Mock Fetcher<Object, Integer> fetcher;

    private PageableIterator<Integer> pageableIterator;

    @BeforeEach
    void setUp() {
        this.pageableIterator = new PageableIterator<>(fetcher, PAGE_SIZE);
    }

    @Test
    void iteratorSanity() {
        when(fetcher.fetch(any())).thenAnswer(buildFetcherAnswer());
        var result = new ArrayList<Integer>();
        while (pageableIterator.hasNext()) {
            result.add(pageableIterator.next());
        }

        assertThat(result).hasSize(950);
        verify(fetcher, times(11)).fetch(any());
    }

    protected static Answer<List<Integer>> buildFetcherAnswer() {
        return new Answer<>() {
            private long count = 0;

            public List<Integer> answer(InvocationOnMock invocation) {
                return IntStream.range(0, 950)
                        .boxed()
                        .skip(count++ * PAGE_SIZE)
                        .limit(PAGE_SIZE)
                        .collect(Collectors.toList());
            }
        };
    }
}
