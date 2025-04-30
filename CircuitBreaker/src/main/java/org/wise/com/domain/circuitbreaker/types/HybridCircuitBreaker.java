package org.wise.com.domain.circuitbreaker.types;

import org.wise.com.domain.circuitbreaker.base.IBreaker;
import java.util.List;

public class HybridCircuitBreaker implements IBreaker {
    private final List<IBreaker> breakers;

    public HybridCircuitBreaker(List<IBreaker> breakers) {
        this.breakers = breakers;
    }

    @Override
    public boolean isCallPermitted() {
        return breakers.stream().allMatch(IBreaker::isCallPermitted);
    }

    @Override
    public void onSuccess() {
        breakers.forEach(IBreaker::onSuccess);
    }

    @Override
    public void onFailure() {
        breakers.forEach(IBreaker::onFailure);
    }

}
