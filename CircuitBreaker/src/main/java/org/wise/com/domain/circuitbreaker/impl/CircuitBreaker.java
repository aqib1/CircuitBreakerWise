package org.wise.com.domain.circuitbreaker.impl;

import org.wise.com.domain.circuitbreaker.core.IBreaker;

import java.util.function.Supplier;

public class CircuitBreaker<T> {
    private final Supplier<T> execution;
    private final IBreaker breaker;
    public CircuitBreaker(
            Supplier<T> execution,
            IBreaker breaker
    ) {
        this.execution = execution;
        this.breaker = breaker;
    }

    public T execute(Supplier<T> fallback) {
        if(!breaker.isCallPermitted()) {
            return fallback.get();
        } else {
            try {
                T res = execution.get();
                breaker.onSuccess();
                return res;
            } catch(Exception exception) {
                exception.printStackTrace();
                breaker.onFailure();
                return fallback.get();
            }
        }
    }
}
