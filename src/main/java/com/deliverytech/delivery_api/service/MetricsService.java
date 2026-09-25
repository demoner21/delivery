package com.deliverytech.delivery_api.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Contadores
    private final Counter pedidosProcessados;
    private final Counter pedidosComSucesso;
    private final Counter pedidosComErro;
    private final Counter receitaTotal;

    // Timers
    private final Timer tempoProcessamentoPedido;
    private final Timer tempoConsultaBanco;

    // Gauges
    private final AtomicInteger usuariosAtivos = new AtomicInteger(0);
    private final AtomicLong produtosEmEstoque = new AtomicLong(1000);

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Inicializar contadores
        this.pedidosProcessados = Counter.builder("delivery.pedidos.total")
            .description("Total de pedidos processados")
            .register(meterRegistry);

        this.pedidosComSucesso = Counter.builder("delivery.pedidos.sucesso")
            .description("Pedidos processados com sucesso")
            .register(meterRegistry);

        this.pedidosComErro = Counter.builder("delivery.pedidos.erro")
            .description("Pedidos com erro no processamento")
            .register(meterRegistry);

        this.receitaTotal = Counter.builder("delivery.receita.total")
            .description("Receita total em centavos")
            .baseUnit("centavos")
            .register(meterRegistry);

        // Inicializar timers
        this.tempoProcessamentoPedido = Timer.builder("delivery.pedido.processamento.tempo")
            .description("Tempo de processamento de pedidos")
            .register(meterRegistry);

        this.tempoConsultaBanco = Timer.builder("delivery.database.consulta.tempo")
            .description("Tempo de consulta ao banco de dados")
            .register(meterRegistry);

        // Inicializar gauges
        Gauge.builder("delivery.usuarios.ativos", usuariosAtivos, AtomicInteger::get)
            .description("Número de usuários ativos")
            .register(meterRegistry);

        Gauge.builder("delivery.produtos.estoque", produtosEmEstoque, AtomicLong::get)
            .description("Produtos em estoque")
            .register(meterRegistry);
    }

    // Métodos para incrementar contadores
    public void incrementarPedidosProcessados() {
        pedidosProcessados.increment();
    }

    public void incrementarPedidosComSucesso() {
        pedidosComSucesso.increment();
    }

    public void incrementarPedidosComErro() {
        pedidosComErro.increment();
    }

    public void adicionarReceita(double valor) {
        receitaTotal.increment(valor * 100); // Converter para centavos
    }

    // Métodos para timers
    public Timer.Sample iniciarTimerPedido() {
        return Timer.start(meterRegistry);
    }

    public void finalizarTimerPedido(Timer.Sample sample) {
        sample.stop(tempoProcessamentoPedido);
    }

    // Métodos para gauges
    public void setUsuariosAtivos(int quantidade) {
        usuariosAtivos.set(quantidade);
    }

    public void setProdutosEmEstoque(long quantidade) {
        produtosEmEstoque.set(quantidade);
    }

    public void incrementarRestaurantesCadastradosComSucesso() {
        this.meterRegistry.counter("delivery_restaurantes_cadastrados_total", "resultado", "sucesso").increment();
    }
    
    public void incrementarRestaurantesCadastradosComErro() {
        this.meterRegistry.counter("delivery_restaurantes_cadastrados_total", "resultado", "erro").increment();
    }
}
