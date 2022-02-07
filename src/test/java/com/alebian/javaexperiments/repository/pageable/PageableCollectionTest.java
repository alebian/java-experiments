package com.alebian.javaexperiments.repository.pageable;

import static com.alebian.javaexperiments.repository.pageable.PageableIteratorTest.PAGE_SIZE;
import static com.alebian.javaexperiments.repository.pageable.PageableIteratorTest.buildFetcherAnswer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alebian.javaexperiments.utils.extension.VerifyNoMoreInteractions;
import org.assertj.core.util.Streams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.stream.Collectors;

@VerifyNoMoreInteractions
class PageableCollectionTest {
    @Mock
    Fetcher<Object, Integer> fetcher;

    private PageableCollection<Integer> pageableCollection;

    @BeforeEach
    void setUp() {
        this.pageableCollection = new PageableCollection<>(fetcher, PAGE_SIZE);
    }

    @Test
    void iteratorSanity() {
        when(fetcher.fetch(any())).thenAnswer(buildFetcherAnswer());
        var result = Streams.stream(pageableCollection.iterator()).collect(Collectors.toList());
        assertThat(result).hasSize(950);
        verify(fetcher, times(11)).fetch(any());
    }
}
