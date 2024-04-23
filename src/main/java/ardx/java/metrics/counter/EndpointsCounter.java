package ardx.java.metrics.counter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


import javax.annotation.PostConstruct;

@Component
public class EndpointsCounter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    MeterRegistry meterRegistry;

    public EndpointsCounter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @PostConstruct
    public void countEndpoints() {
        int count = 0;
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod method = map.get(info);
            java.lang.reflect.Method javaMethod = method.getMethod();
            if (javaMethod.getDeclaringClass().isAnnotationPresent(RestController.class)) {
                count++;
            }
        }
        System.out.println("Número total de endpoints: " + count);


//        ardx_endpoints_count
//        public ExceptionsCounter(MeterRegistry meterRegistry) {
//        	Counter.builder("ardx_h")
//                    .description("Total number of exceptions occurred")
//                    .register(meterRegistry);
//        }

    }
}
