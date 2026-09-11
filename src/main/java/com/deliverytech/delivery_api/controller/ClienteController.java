package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.dto.ClienteDTO;
import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
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
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
        ClienteResponseDTO cliente = clienteService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    /*
    Listar todos os clientes ativos
    */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /*
    Buscar cliente por ID
    */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id,
        @Valid @RequestBody ClienteDTO dto
    ) {
        ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);

        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Atualizar cliente
    */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO dto) {
        ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok(cliente);
    }

    /*
    ativa e desativar cliente (soft delete)
    */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(
            @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    /*
    Buscar cliente por email
    */
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }
}
