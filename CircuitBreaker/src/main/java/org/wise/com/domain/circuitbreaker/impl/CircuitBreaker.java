package org.wise.com.domain.circuitbreaker.impl;

import org.wise.com.domain.circuitbreaker.base.IBreaker;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircuitBreaker<T> {
    private final Lock lock;
    private final Supplier<T> execution;
    private final IBreaker breaker;
    public CircuitBreaker(Supplier<T> execution, IBreaker breaker) {
        this.lock = new ReentrantLock();
        this.execution = execution;
        this.breaker = breaker;
    }

    public T execute(Supplier<T> fallback) {
        this.lock.lock();
        try {
            if (!breaker.isCallPermitted()) {
                return fallback.get();
            }
            try {
                T exec = execution.get();
                breaker.onSuccess();
                return exec;
            } catch (Exception exception) {
                exception.printStackTrace();
                breaker.onFailure();
                return fallback.get();
            }
        } finally {
            this.lock.unlock();
        }
    }
}
