package org.wise.com.domain.circuitbreaker.core;

import org.wise.com.domain.circuitbreaker.record.CircuitBreakerStatus;

public interface IBreaker {
    boolean isCallPermitted();
    void onSuccess();
    void onFailure();
    CircuitBreakerStatus getStatus();
}
