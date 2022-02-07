package com.alebian.javaexperiments.utils.extension;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.util.ReflectionTestUtils.getField;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.Spy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class VerifyNoMoreInteractionsExtension implements AfterEachCallback {
    public VerifyNoMoreInteractionsExtension() {
    }

    public void afterEach(ExtensionContext context) {
        Object target = context.getRequiredTestInstance();
        List<Object> mockedObjects = getMockObjects(target);
        if (!mockedObjects.isEmpty()) {
            verifyNoMoreInteractions(mockedObjects.toArray());
        }
    }

    private List<Object> getMockObjects(Object target) {
        return Arrays.stream(target.getClass().getDeclaredFields())
                .filter(this::isMockOrSpyField)
                .map(f -> getField(target, f.getName()))
                .collect(toList());
    }

    private boolean isMockOrSpyField(Field f) {
        return f.isAnnotationPresent(Mock.class) || f.isAnnotationPresent(Spy.class);
    }
}
