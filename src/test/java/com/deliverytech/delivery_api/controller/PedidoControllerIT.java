package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.config.TestDataConfiguration;
import com.deliverytech.delivery_api.dto.PedidoDTO;
import com.deliverytech.delivery_api.dto.ItemPedidoDTO;
import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Testes de Integração do PedidoController")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Deve criar pedido com dados válidos")
    void should_CreatePedido_When_ValidData() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);
        Produto produto = produtoRepository.findAll().get(0);

        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(2);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        // When & Then
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente.id", is(cliente.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PENDENTE")))
                .andExpect(jsonPath("$.valorTotal", is(59.80))) // 2 x 29.90
                .andExpect(jsonPath("$.itens", hasSize(1)));
    }

    @Test
    @DisplayName("Deve retornar erro quando produto não existe")
    void should_ReturnError_When_ProductNotExists() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);

        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(999L); // Produto inexistente
        itemDTO.setQuantidade(1);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        // When & Then
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Produto não encontrado")));
    }

    @Test
    @DisplayName("Deve retornar erro quando estoque insuficiente")
    void should_ReturnError_When_InsufficientStock() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);
        Produto produto = produtoRepository.findAll().get(0);

        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(100); // Quantidade maior que estoque

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        // When & Then
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Estoque insuficiente")));
    }

    @Test
    @DisplayName("Deve buscar histórico de pedidos do cliente")
    void should_ReturnClientePedidos_When_ClienteExists() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);

        // Criar um pedido primeiro
        Produto produto = produtoRepository.findAll().get(0);
        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(1);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)));

        // When & Then
        mockMvc.perform(get("/api/pedidos/cliente/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].cliente.id", is(cliente.getId().intValue())));
    }

    @Test
    @DisplayName("Deve atualizar status do pedido")
    void should_UpdatePedidoStatus_When_PedidoExists() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);
        Produto produto = produtoRepository.findAll().get(0);

        // Criar pedido primeiro
        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(1);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        String response = mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andReturn().getResponse().getContentAsString();

        // Extrair ID do pedido criado
        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        // When & Then
        mockMvc.perform(put("/api/pedidos/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")));
    }

    @Test
    @DisplayName("Deve validar cálculo correto do valor total")
    void should_CalculateCorrectTotal_When_MultipleItems() throws Exception {
        // Given
        Cliente cliente = clienteRepository.findAll().get(0);
        Produto produto = produtoRepository.findAll().get(0);

        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(produto.getId());
        item1.setQuantidade(2); // 2 x 29.90 = 59.80

        ItemPedidoDTO item2 = new ItemPedidoDTO();
        item2.setProdutoId(produto.getId());
        item2.setQuantidade(1); // 1 x 29.90 = 29.90

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setItens(Arrays.asList(item1, item2));

        // When & Then
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal", is(89.70))); // Total: 89.70
    }
}
