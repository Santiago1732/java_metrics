package ardx.java.metrics.config;

import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

@Configuration
public class MetricsRegister {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsRegister.class);

	private MeterRegistry meterRegistry;

	private CollectorRegistry collectorRegistry;
	
	private PrometheusMeterRegistry prometheusMeter;

	private final MetricsEndpoint metricsEndpoint;
	
	private static final String COUNTER_BATCHMANAGER_SENT_REQUESTS = "logback_events_total";
	private static final String METHOD_TAG = "method";
	private static final String PATH_TAG = "path";
	
    @Autowired
    public MetricsRegister(MeterRegistry meterRegistry, CollectorRegistry collectorRegistry, MetricsEndpoint metricsEndpoint) {
        this.meterRegistry = meterRegistry;
        this.collectorRegistry = collectorRegistry;
        this.metricsEndpoint = metricsEndpoint;
        setMetricName();
        iterateOverMeters();
    }

    public void iterateOverMeters() {
    	        meterRegistry.getMeters().forEach(meter -> {
    	            String description = meter.getId().getDescription();
    	            Type type = meter.getId().getType();
    	            String metricName = meter.getId().getName();
    	            
    	            
    	            StringBuilder stringBuilder = new StringBuilder();
    	            
    	            stringBuilder.append("# HELP ").append(metricName).append(" ").append(description).append("\n");
    	            stringBuilder.append("# TYPE ").append(metricName).append(" ").append(type).append("\n");
    	            
    	            meter.getId().getTags().forEach(tag -> {
    	                stringBuilder.append(metricName).append("{").append(tag.getKey()).append("=\"").append(tag.getValue()).append("\"} ").append(meter.measure().iterator().next().getValue()).append("\n");
    	            });
    	            
    	            System.out.println(stringBuilder.toString());
    	        });
    	
    }
    
    public void registerGauge(String oldNameMetric, String newNameMetric){
    	Gauge originalMetricGauge = meterRegistry.find(oldNameMetric).gauge();
        if(originalMetricGauge != null) {
        	Gauge newMetric = Gauge.builder(newNameMetric, () -> originalMetricGauge.value())
        			.description("New Metric")	
        			.register(meterRegistry);
        			meterRegistry.remove(originalMetricGauge);
        }			 
    }

    public void registerCounter(String oldNameMetric, String newNameMetric) {
    	
    	List<Counter> counterList = (List<Counter>) meterRegistry.find(oldNameMetric).counters();
    	List<Meter> meterList = (List<Meter>) meterRegistry.find(oldNameMetric).meters();
    	
//    	 Counter counter = Metrics.globalRegistry.find("nombre_de_la_metrica").counter();
    	Optional<Counter> counterOptional = Optional.ofNullable(Metrics.globalRegistry
    		      .find(oldNameMetric).counter());
    	
    	List<Meter> meterList2 = new ArrayList<>();
    	
    	meterList2 = meterRegistry.getMeters();
    	
    	Counter counter = meterRegistry.counter(oldNameMetric,"level","info");
    	
    	double value = counter.count();
    	
    	
    	
    	List <Sample> s = new ArrayList<>();
    	List<Counter> metricasFiltradas = counterList.stream()
    	        .filter(m -> m.getId().getTag("level") != null)
    	        .map(m -> (Counter) m)
    	        .collect(Collectors.toList());
    	
    	List <String> listTagsStrings = new ArrayList<>();
    	
    	metricasFiltradas.forEach(metrica -> {
    		listTagsStrings.add(metrica.getId().getTags().toString());
    		}
    	);
    	
    	for (Meter metrica : counterList) {
    		s.addAll(metricsEndpoint.metric(metrica.getId().getName(), null).getMeasurements());
    	    Counter newCounter = Counter.builder(newNameMetric)
    	            .description(metrica.getId().getDescription())
    	            .tags(metrica.getId().getTags())
    	            .register(meterRegistry);
    	}
    	
    	s.forEach(sa -> {
    		System.out.println(sa.getStatistic().getTagValueRepresentation());
    		System.out.println(sa.getValue().longValue());
    		System.out.println(sa.getValue().floatValue());
    		System.out.println(sa.getValue().byteValue());
    		System.out.println(sa.getValue().shortValue());
    		System.out.println(sa.getValue().doubleValue());
    		System.out.println(sa.getValue());
    		System.out.println();
    	});
    	
    }

    public void setMetricName() {
    	registerGauge("jvm.threads.live", "ardx_threads_total");
    	registerGauge("process.start.time", "ardx_process_start_time_seconds");
    	registerGauge("jvm.memory.max", "ardx_percentage_memory_in_use");
    	registerCounter("logback.events", "ardx_total_logs");
    }

}



