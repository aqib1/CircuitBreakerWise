package org.wise.com.domain.circuitbreaker.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountBasedCircuitBreakerTest {
    private static final int FAILURE_THRESHOLD = 3;
    private static final int HALF_OPEN_THRESHOLD = 2;
    private CountBasedCircuitBreaker breaker;

    @BeforeEach
    public void beforeEach() {
        breaker = new CountBasedCircuitBreaker(FAILURE_THRESHOLD, HALF_OPEN_THRESHOLD);
    }

    @Test
    public void isCallPermit_WhenBreakerIsNotOpen_ReturnTrue() {
        // when
        var permitted = breaker.isCallPermitted();

        // then
        assertTrue(permitted);
    }

    @Test
    public void isCallPermit_WhenBreakerIsOpen_ReturnFalse() {
        // given
        for(int i = 0; i < FAILURE_THRESHOLD + HALF_OPEN_THRESHOLD; i++)
            breaker.onFailure();

        // when
        var permitted = breaker.isCallPermitted();

        // then
        assertFalse(permitted);
    }
}
