package ardx.java.metrics.interceptor;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class RequestCounterInterceptor implements ClientHttpRequestInterceptor {

	private final Counter getRequestCounter;

	private final Timer responseSuccessTimer;

	private final Timer responseServerErrorTimer;
	
	private final Timer responseClientErrorTimer;
	
	private final Timer responseRedirectionTimer;
	
	private final MeterRegistry meterRegistry;

	@Autowired
	public RequestCounterInterceptor(MeterRegistry meterRegistry) {
		
		this.meterRegistry = meterRegistry;
		
		this.getRequestCounter = Counter.builder("ardx.get.request").description("Total GET requests")
				.register(meterRegistry);

		this.responseSuccessTimer = Timer.builder("ardx.request.success.time")
				.description("Tiempo de respuesta de una solicitud exitosa (2xx)").register(meterRegistry);
		
		this.responseRedirectionTimer = Timer.builder("ardx.request.redirection.time")
				.description("Tiempo de respuesta de una solicitud de server error (3xx)").register(meterRegistry);

		this.responseClientErrorTimer = Timer.builder("ardx.request.client.error.time")
				.description("Tiempo de respuesta de una solicitud de client error (4xx)").register(meterRegistry);

		this.responseServerErrorTimer = Timer.builder("ardx.request.server.error.time")
				.description("Tiempo de respuesta de una solicitud de server error (5xx)").register(meterRegistry);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		long startTime = System.currentTimeMillis();

		ClientHttpResponse response = execution.execute(request, body);

		long endTime = System.currentTimeMillis();
		
		Series status = response.getStatusCode().series();
		
		switch (status) {
	    case SUCCESSFUL:
	        long durationSuccess = endTime - startTime;
	        Duration responseDurationSuccess = Duration.ofMillis(durationSuccess);
	        responseSuccessTimer.record(responseDurationSuccess);
	        break;
	    case REDIRECTION:
	        long durationRedirection = endTime - startTime;
	        Duration responseDurationRedirection = Duration.ofMillis(durationRedirection);
	        responseRedirectionTimer.record(responseDurationRedirection);
	        break;
	    case CLIENT_ERROR:
	        long durationClientError = endTime - startTime;
	        Duration responseDurationClientError = Duration.ofMillis(durationClientError);
	        responseClientErrorTimer.record(responseDurationClientError);
	        break;
	    case SERVER_ERROR:
	        long durationClientServerError = endTime - startTime;
	        Duration responseDurationServerError = Duration.ofMillis(durationClientServerError);
	        responseServerErrorTimer.record(responseDurationServerError);
	        break;    
	        
	    default:
	        break;
	}
		
		return response;
	}
}