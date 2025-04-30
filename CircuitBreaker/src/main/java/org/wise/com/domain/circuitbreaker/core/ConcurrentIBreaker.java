package org.wise.com.domain.circuitbreaker.core;

import org.wise.com.domain.circuitbreaker.record.CircuitBreakerStatus;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ConcurrentIBreaker implements IBreaker {
    protected final Lock lock;
    protected CircuitBreakerStatus status;
    public ConcurrentIBreaker() {
        this.lock = new ReentrantLock();
        this.status = CircuitBreakerStatus.CLOSED;
    }

    @Override
    public CircuitBreakerStatus getStatus() {
        lock.lock();
        try {
            return status;
        } finally {
            lock.unlock();
        }
    }
}
