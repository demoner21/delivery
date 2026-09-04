package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.service.ClienteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Iniciando o controller REST para o Cliente
@RestController
@RequestMapping("/clientesxpto")
@CrossOrigin(origins = "*") // Permições de acesso para qualquer origem utilizando a expressão "*"
public class ClienteController {

    // injetando o serviço de Cliente
    @Autowired
    private ClienteService clienteService;

    /*
    Cadastrar novo cliente
    através do verbo POST estamos cadastrando o clinete
    */
    @PostMapping
    /* 
    anotacão @Valid para validar o objeto cliente recebido no corpo da requisição, 
    através da anotação @RequestBody, 
    que indica que o objeto cliente será enviado no corpo da requisição
    */
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteSalvo = clienteService.cadastrarCliente(cliente);
            // retornando o cliente cadastrado com status 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
            // capture através do illegalArgumentException caso o cliente já 
            // esteja cadastrado e retorne um status 400 Bad Request
        } catch (IllegalArgumentException e) {
            // retornando o erro com status 400 Bad Request e a mensagem de erro
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            // retornando o erro com status 500 Internal Server Error e a mensagem de erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            // corpot da resposta com a mensagem de erro
                .body("Erro interno do servidor");
        }
    }

    /*
    Listar todos os clientes ativos
    */
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        List<Cliente> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(clientes);
    }

    /*
    Buscar cliente por ID
    */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarClientePorId(id);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Atualizar cliente
    */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                        @Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, cliente);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /*
    Inativar cliente (soft delete)
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            clienteService.inativarCliente(id);
            return ResponseEntity.ok().body("Cliente inativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /*
    Buscar clientes por nome
    */
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarClientesPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    /*
    Buscar cliente por email
    */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.buscarClientePorEmail(email);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
