package com.deliverytech.delivery_api.config.monitoring.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("externalService")
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        try {
            // Simular chamada para serviço externo
            String url = "https://httpbin.org/status/200";
            restTemplate.getForObject(url, String.class);

            return Health.up()
                .withDetail("service", "Payment Gateway")
                .withDetail("url", url)
                .withDetail("status", "Disponível")
                .withDetail("responseTime", "< 100ms")
                .build();

        } catch (Exception e) {
            return Health.down()
                .withDetail("service", "Payment Gateway")
                .withDetail("error", e.getMessage())
                .withDetail("status", "Indisponível")
                .build();
        }
    }
}
