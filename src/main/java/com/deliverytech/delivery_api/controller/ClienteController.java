package com.deliverytech.delivery_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.entity.Cliente;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClienteController {
    
    // Simulando um banco de dados com uma lista de cliente
    // construir um Spring boot REST API returnando uma lista de cliente com nome e sobrenome
    // https://localhost:8080/cliente
    @GetMapping("/cliente")
    public Cliente getCliente() {
        return new Cliente("João", "Silva");
    }

    // Simulando um banco de dados com uma lista de clientes
    // Retornando uma List
    // https://localhost:8080/clientes
    @GetMapping("/clientes")
    public List<Cliente> getClientes() {
        List<Cliente> clientes = new ArrayList<>();
        clientes.add(new Cliente("João", "Silva"));
        clientes.add(new Cliente("Maria", "Souza"));
        clientes.add(new Cliente("Pedro", "Oliveira"));
        return clientes;
    }

    // O valor na URL diretamente injetado no método
    // https://localhost:8080/cliente/João/Silva
    @GetMapping("/cliente/{nome}/{sobrenome}")
    public Cliente getClienteByPathVariable(@PathVariable String nome, @PathVariable String sobrenome) {
        return new Cliente(nome, sobrenome);
    }

    // build onde os valores são passados como query params
    // https://localhost:8080/cliente/query?nome=João&sobrenome=Silva
    @GetMapping("/cliente/query")
    public Cliente getClienteByQueryParam(
        @RequestParam String nome, @RequestParam String sobrenome) {
        return new Cliente(nome, sobrenome);
    }

}