package com.alebian.javaexperiments.utils.extension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a test class with this annotation will enable VerifyNoMoreInteractionsExtension. It
 * will implicitly also extend with mockito extension.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MockitoExtension.class)
@ExtendWith(VerifyNoMoreInteractionsExtension.class)
public @interface VerifyNoMoreInteractions {}
