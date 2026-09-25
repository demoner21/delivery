package com.deliverytech.delivery_api.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final MeterRegistry meterRegistry;

    public DashboardController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    public String dashboard() {
        return "redirect:/dashboard.html"; 
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public Map<String, Object> getMetricsData() {
        Map<String, Object> metrics = new HashMap<>();

        // Métricas de pedidos
        metrics.put("pedidos_total", getCounterValue("delivery.pedidos.total"));
        metrics.put("pedidos_sucesso", getCounterValue("delivery.pedidos.sucesso"));
        metrics.put("pedidos_erro", getCounterValue("delivery.pedidos.erro"));
        metrics.put("receita_total", getCounterValue("delivery.receita.total") / 100.0);

        // Métricas de performance
        metrics.put("tempo_medio_pedido", getTimerMean("delivery.pedido.processamento.tempo"));
        metrics.put("tempo_medio_banco", getTimerMean("delivery.database.consulta.tempo"));

        // Métricas de sistema
        metrics.put("memoria_usada", getGaugeValue("jvm.memory.used"));
        metrics.put("memoria_max", getGaugeValue("jvm.memory.max"));
        metrics.put("cpu_usage", getGaugeValue("system.cpu.usage"));

        // Métricas de estado
        metrics.put("usuarios_ativos", getGaugeValue("delivery.usuarios.ativos"));
        metrics.put("produtos_estoque", getGaugeValue("delivery.produtos.estoque"));

        // Health status
        metrics.put("health_status", "UP");

        return metrics;
    }

    private double getCounterValue(String name) {
        return meterRegistry.find(name).counter() != null ?
            meterRegistry.find(name).counter().count() : 0.0;
    }

    private double getTimerMean(String name) {
        return meterRegistry.find(name).timer() != null ?
            meterRegistry.find(name).timer().mean(TimeUnit.MILLISECONDS) : 0.0;
    }

    private double getGaugeValue(String name) {
        return meterRegistry.find(name).gauge() != null ?
            meterRegistry.find(name).gauge().value() : 0.0;
    }
}