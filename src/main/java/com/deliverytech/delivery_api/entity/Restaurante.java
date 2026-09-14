package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private BigDecimal taxaEntrega;
    private Integer tempoEntrega;
    private String horarioFuncionamento;
    private boolean ativo;
    private BigDecimal avaliacao;

    // Data e hora de cadastro do restaurante, preenchida automaticamente
    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    // Relacionamento com Pedido e Produto, 
    // tratativa através do @JsonIgnore para evitar problemas de serialização
    @JsonIgnore
    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;

    // Define automaticamente a data de cadastro antes de persistir
    @PrePersist
    protected void aoPersistir() {
        this.dataCadastro = LocalDateTime.now();
    }
}
