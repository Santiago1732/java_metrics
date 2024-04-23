package ardx.java.metrics.counter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionsCounter {

    private final Counter totalExceptionsCounter;

    public ExceptionsCounter(MeterRegistry meterRegistry) {
        totalExceptionsCounter = Counter.builder("ardx_total_exceptions")
                .description("Total number of exceptions occurred")
                .register(meterRegistry);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
        totalExceptionsCounter.increment();
    }

}
