package com.deliverytech.delivery_api.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private final MeterRegistry meterRegistry;

    // Thresholds para alertas
    private static final double ERROR_RATE_THRESHOLD = 0.05; // 5%
    private static final double RESPONSE_TIME_THRESHOLD = 1000; // 1 segundo
    private static final double CPU_THRESHOLD = 0.8; // 80%
    private static final double MEMORY_THRESHOLD = 0.85; // 85%

    public AlertService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 30000) // A cada 30 segundos
    public void verificarAlertas() {
        verificarErrorRate();
        verificarResponseTime();
        verificarRecursosSistema();
        verificarHealthStatus();
    }

    private void verificarErrorRate() {
        double totalRequests = getCounterValue("delivery.pedidos.total");
        double errorRequests = getCounterValue("delivery.pedidos.erro");

        if (totalRequests > 0) {
            double errorRate = errorRequests / totalRequests;

            if (errorRate > ERROR_RATE_THRESHOLD) {
                enviarAlerta("HIGH_ERROR_RATE",
                    String.format("Taxa de erro alta: %.2f%% (threshold: %.2f%%)",
                        errorRate * 100, ERROR_RATE_THRESHOLD * 100),
                    "CRITICAL");
            }
        }
    }

    private void verificarResponseTime() {
        double avgResponseTime = getTimerMean("delivery.pedido.processamento.tempo");

        if (avgResponseTime > RESPONSE_TIME_THRESHOLD) {
            enviarAlerta("HIGH_RESPONSE_TIME",
                String.format("Tempo de resposta alto: %.2fms (threshold: %.2fms)",
                    avgResponseTime, RESPONSE_TIME_THRESHOLD),
                "WARNING");
        }
    }

    private void verificarRecursosSistema() {
        double cpuUsage = getGaugeValue("system.cpu.usage");
        double memoryUsed = getGaugeValue("jvm.memory.used");
        double memoryMax = getGaugeValue("jvm.memory.max");

        if (cpuUsage > CPU_THRESHOLD) {
            enviarAlerta("HIGH_CPU_USAGE",
                String.format("Uso de CPU alto: %.2f%% (threshold: %.2f%%)",
                    cpuUsage * 100, CPU_THRESHOLD * 100),
                "WARNING");
        }

        if (memoryMax > 0) {
            double memoryUsage = memoryUsed / memoryMax;
            if (memoryUsage > MEMORY_THRESHOLD) {
                enviarAlerta("HIGH_MEMORY_USAGE",
                    String.format("Uso de memória alto: %.2f%% (threshold: %.2f%%)",
                        memoryUsage * 100, MEMORY_THRESHOLD * 100),
                    "WARNING");
            }
        }
    }

    private void verificarHealthStatus() {
        // Verificar health checks customizados
        try {
            // Simular verificação de health
            boolean dbHealthy = verificarSaudeDatabase();
            boolean externalServiceHealthy = verificarSaudeServicoExterno();

            if (!dbHealthy) {
                enviarAlerta("DATABASE_DOWN",
                    "Banco de dados não está respondendo",
                    "CRITICAL");
            }

            if (!externalServiceHealthy) {
                enviarAlerta("EXTERNAL_SERVICE_DOWN",
                    "Serviço externo não está disponível",
                    "WARNING");
            }

        } catch (Exception e) {
            logger.error("Erro ao verificar health status", e);
        }
    }

    private void enviarAlerta(String tipo, String mensagem, String severidade) {
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("timestamp", System.currentTimeMillis());
        alerta.put("tipo", tipo);
        alerta.put("mensagem", mensagem);
        alerta.put("severidade", severidade);
        alerta.put("aplicacao", "delivery-api");

        // Log do alerta
        logger.warn("ALERTA [{}] {}: {}", severidade, tipo, mensagem);

        // Aqui você integraria com sistemas de notificação:
        // - Email, Slack, PagerDuty, SMS
        enviarWebhookAlerta(alerta);
    }

    private void enviarWebhookAlerta(Map<String, Object> alerta) {
        try {
            logger.info("Enviando alerta via webhook: {}", alerta);
            // Implementação de webhook para alertas
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta via webhook", e);
        }
    }

    private boolean verificarSaudeDatabase() {
        return true; // Simulado
    }

    private boolean verificarSaudeServicoExterno() {
        return true; // Simulado
    }

    private double getCounterValue(String name) {
        return meterRegistry.find(name).counter() != null ?
            meterRegistry.find(name).counter().count() : 0.0;
    }

    private double getTimerMean(String name) {
        return meterRegistry.find(name).timer() != null ?
            meterRegistry.find(name).timer().mean(java.util.concurrent.TimeUnit.MILLISECONDS) : 0.0;
    }

    private double getGaugeValue(String name) {
        return meterRegistry.find(name).gauge() != null ?
            meterRegistry.find(name).gauge().value() : 0.0;
    }
}
