package org.wise.com.domain.circuitbreaker.types;

import org.wise.com.domain.circuitbreaker.base.IBreaker;

public class CountBasedCircuitBreaker implements IBreaker {
    private BreakerStatus status;
    private final int failureCountThreshold;
    private final int halfOpenThreshold;
    private int failureCount;
    private int halfOpenCount;

    public CountBasedCircuitBreaker(
            int failureCountThreshold,
            int halfOpenThreshold
    ) {
        this.failureCountThreshold = failureCountThreshold;
        this.halfOpenThreshold = halfOpenThreshold;
        this.status = BreakerStatus.CLOSED;
    }

    @Override
    public boolean isCallPermitted() {
        if(getStatus() == BreakerStatus.HALF_OPEN) {
            return isCallPermittedForHalfOpenState();
        }
        return getStatus() == BreakerStatus.CLOSED;
    }

    private boolean isCallPermittedForHalfOpenState() {
        return halfOpenCount < halfOpenThreshold;
    }

    @Override
    public void onSuccess() {
        if (getStatus() == BreakerStatus.OPEN
                || getStatus() == BreakerStatus.HALF_OPEN) {
            failureCount = 0;
            halfOpenCount = 0;
            status = BreakerStatus.CLOSED;
        }
    }

    @Override
    public void onFailure() {
        if (getStatus() == BreakerStatus.CLOSED) {
            failureCount++;
            if (failureCount >= failureCountThreshold) {
                if(halfOpenThreshold > 0) {
                    status = BreakerStatus.HALF_OPEN;
                } else {
                    status = BreakerStatus.OPEN;
                }
                return;
            }
        }

        if (getStatus() == BreakerStatus.HALF_OPEN) {
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
