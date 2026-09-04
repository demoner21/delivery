package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Iniciando o controller REST para o Pedido
@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    // injetando o serviço de Pedido
    @Autowired
    private PedidoService pedidoService;

    /*
    Criar novo pedido
    através do verbo POST estamos criando o pedido, associando
    um cliente e um restaurante através dos parâmetros da requisição (@RequestParam)
    */
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestParam Long clienteId,
                                          @RequestParam Long restauranteId) {
        try {
            Pedido pedido = pedidoService.criarPedido(clienteId, restauranteId);
            // retornando o pedido criado com status 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
            // capture através do illegalArgumentException caso o cliente ou
            // restaurante não existam, e retorne um status 400 Bad Request
        } catch (IllegalArgumentException e) {
            // retornando o erro com status 400 Bad Request e a mensagem de erro
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            // retornando o erro com status 500 Internal Server Error e a mensagem de erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                // corpo da resposta com a mensagem de erro
                .body("Erro interno do servidor");
        }
    }

    /*
    Adicionar item ao pedido
    através do verbo POST estamos adicionando um produto (e sua quantidade)
    a um pedido já existente, identificado pelo pedidoId na URL
    */
    @PostMapping("/{pedidoId}/itens")
    public ResponseEntity<?> adicionarItem(@PathVariable Long pedidoId,
                                            @RequestParam Long produtoId,
                                            @RequestParam Integer quantidade) {
        try {
            Pedido pedido = pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
            // retornando o pedido atualizado com o novo item, status 200 OK
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /*
    Confirmar pedido
    através do verbo PUT estamos alterando o status do pedido para CONFIRMADO,
    encerrando a fase de montagem (adição de itens)
    */
    @PutMapping("/{pedidoId}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long pedidoId) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(pedidoId);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /*
    Buscar pedido por ID
    */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Listar pedidos por cliente
    retorna o histórico de pedidos de um cliente específico,
    ordenado (no repository) do mais recente para o mais antigo
    */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /*
    Buscar pedido por número
    útil para o cliente rastrear o próprio pedido usando o
    código (numeroPedido) informado na hora da compra
    */
    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numeroPedido) {
        Optional<Pedido> pedido = pedidoService.buscarPorNumero(numeroPedido);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Atualizar status do pedido
    através do verbo PUT estamos alterando o status do pedido
    (ex: PENDENTE -> CONFIRMADO -> PREPARANDO -> SAIU_PARA_ENTREGA -> ENTREGUE)
    */
    @PutMapping("/{pedidoId}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long pedidoId,
                                              @RequestParam StatusPedido status) {
        try {
            // ATENÇÃO: este método precisa existir no PedidoService.
            // Ele deve apenas alterar o status do pedido para o valor recebido,
            // e não deve reutilizar a lógica de cancelamento.
            Pedido pedido = pedidoService.atualizarStatus(pedidoId, status);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /*
    Cancelar pedido
    através do verbo PUT estamos alterando o status do pedido para CANCELADO,
    com um motivo opcional informado via parâmetro da requisição
    */
    @PutMapping("/{pedidoId}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long pedidoId,
                                             @RequestParam(required = false) String motivo) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(pedidoId, motivo);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
}
