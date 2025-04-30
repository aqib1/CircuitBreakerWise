package org.wise.com.domain.circuitbreaker.core;

import org.wise.com.domain.circuitbreaker.record.CircuitBreakerStatus;

import java.time.Instant;

public class TimeBasedBreaker extends ConcurrentIBreaker {
    private final long openStateTimeoutMillis;
    private final int halfOpenTestCallThreshold;
    private long lastFailureTimeMillis;
    private int openTestCallCount;

    public TimeBasedBreaker(
            long openStateTimeoutMillis,
            int halfOpenTestCallThreshold
    ) {
        this.openStateTimeoutMillis = openStateTimeoutMillis;
        this.halfOpenTestCallThreshold = halfOpenTestCallThreshold;
        this.lastFailureTimeMillis = Instant.EPOCH.toEpochMilli();
    }

    @Override
    public boolean isCallPermitted() {
        lock.lock();
        try {
            if (status == CircuitBreakerStatus.HALF_OPEN) {
                return callCheckForHalfOpen();
            }

            if (status == CircuitBreakerStatus.OPEN) {
                return callCheckForOpen();
            }

            return true;
        } finally {
            lock.unlock();
        }
    }

    private boolean callCheckForOpen() {
        long now = Instant.now().toEpochMilli();
        if ((now - lastFailureTimeMillis) >= openStateTimeoutMillis) {
            status = CircuitBreakerStatus.HALF_OPEN;
            return true;
        }
        return false;
    }

    private boolean callCheckForHalfOpen() {
        if (openTestCallCount >= halfOpenTestCallThreshold) {
            status = CircuitBreakerStatus.OPEN;
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess() {
        lock.lock();
        try {
            if (status == CircuitBreakerStatus.OPEN
                    || status == CircuitBreakerStatus.HALF_OPEN) {
                status = CircuitBreakerStatus.CLOSED;
                openTestCallCount = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onFailure() {
        lock.lock();
        try {
            if(status == CircuitBreakerStatus.HALF_OPEN) {
                openTestCallCount++;
            } else if(status == CircuitBreakerStatus.CLOSED) {
                lastFailureTimeMillis = Instant.now().toEpochMilli();
                status = CircuitBreakerStatus.OPEN;
            }
        } finally {
            lock.unlock();
        }
    }
}
