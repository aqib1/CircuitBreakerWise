package org.wise.com.domain.circuitbreaker.core;

import java.util.List;

public class HybridCircuitBreaker extends ConcurrentIBreaker {
    private final List<ConcurrentIBreaker> delegates;

    public HybridCircuitBreaker(List<ConcurrentIBreaker> delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean isCallPermitted() {
        return delegates.stream().allMatch(ConcurrentIBreaker::isCallPermitted);
    }

    @Override
    public void onSuccess() {
        delegates.forEach(ConcurrentIBreaker::onSuccess);
    }

    @Override
    public void onFailure() {
        delegates.forEach(ConcurrentIBreaker::onFailure);
    }
}
