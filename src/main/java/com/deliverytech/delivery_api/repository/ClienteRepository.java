package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.enums.StatusPedido;

import com.deliverytech.delivery_api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    //buscar cliente por email
    Optional<Cliente> findByEmail(String email);

    // verificar se o cliente está ativo
    boolean existsByEmailAndAtivoTrue(String email);

    // buscar clientes por nome
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // buscar clientes por telefone
    List<Cliente> findByTelefone(String telefone);

    // buscar clientes por endereço
    List<Cliente> findByEnderecoContainingIgnoreCase(String endereco);

    // buscar clientes ativos
    List<Cliente> findByAtivoTrue();

    // buscar customizada de clientes por status ativo
    @Query("SELECT c FROM Cliente c JOIN c.pedidos p WHERE p.statusPedido = :status")
    List<Cliente> findClientesComPedidosAtivos(@Param("status") StatusPedido status);

    // Query customizada para buscar clientes por endereço usando JPQL
    @Query("SELECT c FROM Cliente c WHERE c.endereco LIKE %:endereco")
    List<Cliente> findClientesPorEndereco(@Param("endereco") String endereco);

    // Query customizada para contar o número de clientes
    @Query("SELECT COUNT(c) FROM Cliente c")
    long countClientes();
}
