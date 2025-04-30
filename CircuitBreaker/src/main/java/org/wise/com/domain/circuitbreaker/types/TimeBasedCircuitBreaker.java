package org.wise.com.domain.circuitbreaker.types;

import org.wise.com.domain.circuitbreaker.base.IBreaker;

import java.time.Instant;

public class TimeBasedCircuitBreaker implements IBreaker {
    private final long openStateTimeoutMillis;
    private final int halfOpenThreshold;
    private long lastFailureTimestampMillis;
    private BreakerStatus status;
    private int halfOpenCount;

    public TimeBasedCircuitBreaker(
            long openStateTimeoutMillis,
            int halfOpenThreshold
    ) {
        this.openStateTimeoutMillis = openStateTimeoutMillis;
        this.lastFailureTimestampMillis = Instant.EPOCH.toEpochMilli();
        this.status = BreakerStatus.CLOSED;
        this.halfOpenThreshold = halfOpenThreshold;
    }

    @Override
    public boolean isCallPermitted() {
        if(status == BreakerStatus.OPEN) {
            return isCallPermittedForOpenState();
        }
        if(status == BreakerStatus.HALF_OPEN) {
            return isCallPermittedForHalfOpenState();
        }

        return true;
    }

    private boolean isCallPermittedForHalfOpenState() {
        return halfOpenCount <= halfOpenThreshold;
    }

    private boolean isCallPermittedForOpenState() {
        var now = Instant.now().toEpochMilli();
        if ((now - lastFailureTimestampMillis) >= openStateTimeoutMillis) {
            status = BreakerStatus.CLOSED;
            return true;
        }
        return false;
    }


    @Override
    public void onSuccess() {
        if (status == BreakerStatus.OPEN
                || status == BreakerStatus.HALF_OPEN) {
            status = BreakerStatus.CLOSED;
            halfOpenCount = 0;
        }
    }

    @Override
    public void onFailure() {
        if (status == BreakerStatus.CLOSED) {
            lastFailureTimestampMillis = Instant.now().toEpochMilli();
            if(halfOpenThreshold > 0) {
                status = BreakerStatus.HALF_OPEN;
            } else {
                status = BreakerStatus.OPEN;
            }
            return;
        }

        if (status == BreakerStatus.HALF_OPEN) {
            halfOpenCount++;
            if (halfOpenCount >= halfOpenThreshold) {
                status = BreakerStatus.OPEN;
            }
        }
    }

    @Override
    public BreakerStatus getStatus() {
        return status;
    }
}
