package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

public class CalculoPedidoResponseDTO {

    private BigDecimal valorTotal;

    public CalculoPedidoResponseDTO() {}

    public CalculoPedidoResponseDTO(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    // Getters e Setters
    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
