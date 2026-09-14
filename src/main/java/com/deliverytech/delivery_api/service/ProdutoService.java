package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;

import java.util.List;

/*
 Contrato da camada de serviço de Produto.
 A implementação fica em service.impl.ProdutoServiceImpl
*/
public interface ProdutoService {

    // Cadastra um novo produto vinculado a um restaurante
    ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto);

    // Busca um produto pelo id, lançando exceção caso não exista
    ProdutoResponseDTO buscarProdutoPorId(Long id);

    // Atualiza os dados de um produto existente
    ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto);

    // Remove um produto do sistema
    void removerProduto(Long id);

    // Alterna a disponibilidade do produto
    ProdutoResponseDTO alterarDisponibilidade(Long id);

    // Busca produtos disponíveis por categoria
    List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria);

    // Busca produtos pelo nome (busca parcial)
    List<ProdutoResponseDTO> buscarProdutosPorNome(String nome);

    // Lista os produtos de um restaurante, opcionalmente filtrando por disponibilidade
    List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel);
}
