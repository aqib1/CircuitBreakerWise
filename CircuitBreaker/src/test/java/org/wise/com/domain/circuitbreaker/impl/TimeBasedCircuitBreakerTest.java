package org.wise.com.domain.circuitbreaker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wise.com.domain.circuitbreaker.base.IBreaker;
import org.wise.com.domain.circuitbreaker.types.TimeBasedCircuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeBasedCircuitBreakerTest {
    private static final int FAILURE_THRESHOLD = 3;
    private IBreaker breaker;
    private CircuitBreaker<String> circuitBreaker;

    @BeforeEach
    public void beforeEach() {
        breaker = new TimeBasedCircuitBreaker(FAILURE_THRESHOLD, 0);
    }

    @Test
    public void execute_WhenCircuitIsClosed_shouldReturnExecution() {
        // given
        this.circuitBreaker = new CircuitBreaker<>(() -> "This is a test", breaker);

        // when
        var result = circuitBreaker.execute(() -> "Not a test");

        // then
        assertEquals(result, "This is a test");
    }

    @Test
    public void execute_WhenCircuitIsOpen_shouldReturnFallback() {
        // given
        this.circuitBreaker = new CircuitBreaker<>(
                () -> {
                    throw new IllegalArgumentException("Failed request");
                },
                breaker
        );

        // when
        var result = circuitBreaker.execute(() -> "Not a test");

        // then
        assertEquals(result, "Not a test");
    }

    @Test
    public void execute_WhenCircuitIsOpen_shouldReturnFallback_AndOnceCircuitClosed_ShouldReturnExecution() {
        // given
        breaker = new TimeBasedCircuitBreaker(FAILURE_THRESHOLD, 3);
        AtomicInteger count = new AtomicInteger(0);
        this.circuitBreaker = new CircuitBreaker<>(
                () -> {
                    count.set(count.incrementAndGet());
                    if(count.get() <= 2) {
                        throw new IllegalArgumentException("Failed request");
                    } else {
                        return "This is a test";
                    }
                },
                breaker
        );

        // when
        for(int i = 0; i < 2; i++) {
            var fallbackResult = circuitBreaker.execute(() -> "Not a test");

            // then
            assertEquals(fallbackResult, "Not a test");
        }

        var executeResult = circuitBreaker.execute(() -> "Not a test");
        // then
        assertEquals(executeResult, "This is a test");
    }
}
