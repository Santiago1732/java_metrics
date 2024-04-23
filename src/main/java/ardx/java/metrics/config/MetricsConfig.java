package ardx.java.metrics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@Configuration
public class MetricsConfig {
	
    @Bean
    MeterRegistry meterRegistry(){
        return new SimpleMeterRegistry();
    }
    
    /*
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer() {
        return (registry) -> registry.config().namingConvention(new NamingConvention() {
            @Override
            public String name(String name, Meter.Type type, String baseUnit) {
                return "" + Arrays.stream(name.split("\\."))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("_"));
            }
        });
    }
     */
}