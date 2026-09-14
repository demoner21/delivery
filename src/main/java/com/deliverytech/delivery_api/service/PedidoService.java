package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.CalculoPedidoDTO;
import com.deliverytech.delivery_api.dto.CalculoPedidoResponseDTO;
import com.deliverytech.delivery_api.dto.PedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoService {

    PedidoResponseDTO criarPedido(PedidoDTO dto);

    PedidoResponseDTO buscarPedidoPorId(Long id);

    List buscarPedidosPorCliente(Long clienteId);

    List buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status);

    Page listarPedidos(Pageable pageable);

    PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status);

    CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto);

    void cancelarPedido(Long id);

}
