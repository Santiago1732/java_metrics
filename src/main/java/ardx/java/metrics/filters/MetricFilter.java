package ardx.java.metrics.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

//@Component
public class MetricFilter implements Filter{

	private final MeterRegistry meterRegistry;

    public MetricFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	try {
    		
    	HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        
        filterChain.doFilter(servletRequest, servletResponse);

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String method = httpServletRequest.getMethod();
        int statusCode = httpServletResponse.getStatus();

        meterRegistry.counter("http.requests.total", Tags.of("method", method)).increment();

        switch (method.toUpperCase()) {
            case "GET":
                meterRegistry.counter("ardx.http.requests.get").increment();
                if (statusCode >= 200 && statusCode < 300) {
                    meterRegistry.counter("ardx.http.response.get.success").increment();
                } else {
                    meterRegistry.counter("ardx.http.response.get.other.status").increment();
                }
                break;
            case "POST":
                meterRegistry.counter("ardx.http.requests.post").increment();
                break;
            case "PUT":
                meterRegistry.counter("ardx.http.requests.put").increment();
                break;
            case "DELETE":
                meterRegistry.counter("ardx.http.requests.delete").increment();
                break;
            case "PATCH":
                meterRegistry.counter("ardx.http.requests.patch").increment();
                break;
            case "HEAD":
                meterRegistry.counter("ardx.http.requests.head").increment();
                break;
            case "OPTIONS":
                meterRegistry.counter("ardx.http.requests.options").increment();
                break;
            case "TRACE":
                meterRegistry.counter("ardx.http.requests.trace").increment();
                break;
            default:
                break;
        }
    	}catch (Exception e) {
    		 meterRegistry.counter("ardx.http.exceptions").increment();
    	}
    }
    @Override
    public void destroy() {
    }

}
