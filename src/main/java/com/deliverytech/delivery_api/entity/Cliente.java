package com.deliverytech.delivery_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;

    //todo: add Data em que o cliente foi cadastrado no sistema
    // private LocalDateTime dataCadastro;

    /*
    Preenchendo dataCadastro automaticamente com a data/hora atual
    no momento em que o cliente é salvo pela primeira vez,
    sem precisar que o controller/service informe esse valor manualmente
    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }
    */


    /*
    @JsonIgnore evita LazyInitializationException ao serializar o Cliente em JSON,
    já que "pedidos" é uma coleção lazy e a sessão do Hibernate já está fechada
    quando o Jackson tenta serializar (open-in-view=false)
    */
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

}