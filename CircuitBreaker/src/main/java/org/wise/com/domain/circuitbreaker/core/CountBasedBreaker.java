package org.wise.com.domain.circuitbreaker.core;

import org.wise.com.domain.circuitbreaker.record.CircuitBreakerStatus;

public class CountBasedBreaker extends ConcurrentIBreaker {
    private final int failureThreshold;
    private int failureCount;

    public CountBasedBreaker(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    @Override
    public boolean isCallPermitted() {
        return status == CircuitBreakerStatus.CLOSED;
    }


    @Override
    public void onSuccess() {
        lock.lock();
        try {
            if(status == CircuitBreakerStatus.OPEN) {
                failureCount = 0;
                status = CircuitBreakerStatus.CLOSED;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onFailure() {
        lock.lock();
        try {
            failureCount++;
            if(failureCount >= failureThreshold) {
                status = CircuitBreakerStatus.OPEN;
            }
        } finally {
            lock.unlock();
        }
    }
}
