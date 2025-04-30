package org.wise.com.domain.circuitbreaker.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBasedCircuitBreakerTest {
    private static final int HALF_OPEN_THRESHOLD = 0;
    private static final long OPEN_STATUS_TIMEOUT_MILLIS = Duration.ofSeconds(2).toMillis();
    private TimeBasedCircuitBreaker breaker;

    @BeforeEach
    public void beforeEach() {
        breaker = new TimeBasedCircuitBreaker(
                OPEN_STATUS_TIMEOUT_MILLIS,
                HALF_OPEN_THRESHOLD
        );
    }

    @Test
    public void isCallPermitted_WhenBreakerIsOpen_ShouldReturnFalse() {
        // when
        breaker.onFailure();
        var permitted = breaker.isCallPermitted();

        // then
        assertFalse(permitted);
        assertEquals(breaker.getStatus(), BreakerStatus.OPEN);
    }

    @Test
    public void isCallPermitted_WhenBreakerIsOpenAndOpenStatusTimeout_ShouldReturnTrue() throws InterruptedException {
        // when
        breaker.onFailure();
        TimeUnit.SECONDS.sleep(3);

        // given
        var permitted = breaker.isCallPermitted();

        // then
        assertTrue(permitted);
        assertEquals(breaker.getStatus(), BreakerStatus.CLOSED);
    }

    @Test
    public void isCallPermitted_WhenBreakerIsOpenAndHalfOpenThresholdDefined_ShouldReturnTrueAfterRetries() throws InterruptedException {
        // given
        breaker = new TimeBasedCircuitBreaker(
                OPEN_STATUS_TIMEOUT_MILLIS,
                3
        );
        for(int i = 0; i <= 3; i++)
            breaker.onFailure();

        TimeUnit.SECONDS.sleep(3);

        // given
        var permitted = breaker.isCallPermitted();

        // then
        assertTrue(permitted);
        assertEquals(breaker.getStatus(), BreakerStatus.CLOSED);

    }
}
