package org.wise.com.domain.circuitbreaker.base;

import org.wise.com.domain.circuitbreaker.types.BreakerStatus;

public interface IBreaker {
    boolean isCallPermitted();
    void onSuccess();
    void onFailure();
    default BreakerStatus getStatus() {
        return BreakerStatus.CLOSED;
    }
}
