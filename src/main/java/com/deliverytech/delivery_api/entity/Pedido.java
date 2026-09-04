package com.deliverytech.delivery_api.entity;

import com.deliverytech.delivery_api.enums.StatusPedido;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private Boolean entrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;

    //todo; add campo de observação do pedido

    /*
    todo: add campo de forma de pagamento 
    (enum: DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX, OUTROS)
    */

    //para armazenar o nome do enum no banco, usamos @Enumerated(EnumType.STRING)
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;

    // relacionamento com Cliente, Restaurante e ItemPedido
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // relacionamento com Restaurante com @ManyToOne, pois um pedido pertence a um restaurante
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    // relacionamento com ItemPedido, um pedido pode ter vários itens
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

}
