package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Iniciando a classe de serviço para o Produto
@Service
@Transactional
public class ProdutoService {

    // injetando o repositório de Produto
    @Autowired
    private ProdutoRepository produtoRepository;

    // injetando o repositório de Restaurante, necessário para vincular o produto ao restaurante
    @Autowired
    private RestauranteRepository restauranteRepository;

    /*
     Cadastrando novo produto e vinculando ao restaurante informado,
     lançando exceção caso o restaurante não seja encontrado
     através do método findById do repositório de Restaurante
     e retornando o produto cadastrado através do método save do repositório de Produto
     */
    public Produto cadastrar(Produto produto, Long restauranteId) {
        // buscando o restaurante pelo id e lançando exceção caso não seja encontrado
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        // validação de produto
        validarDadosProduto(produto);

        // vinculando o produto ao restaurante encontrado
        produto.setRestaurante(restaurante);

        // definindo o status do produto como disponível
        produto.setDisponivel(true);

        return produtoRepository.save(produto);
    }

    /*
    Buscar o produto por Id
    */
    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    /*
    Listar os produtos disponíveis de um restaurante
    */
    @Transactional(readOnly = true)
    public List<Produto> listarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
    }

    /*
    Buscar produtos disponíveis por categoria
    */
    @Transactional(readOnly = true)
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
    }

    /*
    Atualizar os dados do produto
    */
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        // buscando o produto pelo id e lançando exceção caso não seja encontrado
        Produto produto = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        // validação de produto
        validarDadosProduto(produtoAtualizado);

        // Atualizando os dados do produto com os dados do produtoAtualizado
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setCategoria(produtoAtualizado.getCategoria());

        // retornando o produto atualizado através do método save do repositório de Produto
        return produtoRepository.save(produto);
    }

    /* alteração da disponibilidade do produto através do metodo void */
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        // produto acessando produto e buscando pelo id
        Produto produto = buscarPorId(id)
                // através do método orElseThrow lançando exceção caso o produto não seja encontrado
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        // definindo a disponibilidade do produto conforme o parâmetro recebido
        produto.setDisponivel(disponivel);

        // salvando no banco de dados
        produtoRepository.save(produto);
    }

    /*
    Buscar produtos disponíveis dentro de uma faixa de preço
    */
    @Transactional(readOnly = true)
    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.findByPrecoBetweenAndDisponivelTrue(precoMin, precoMax);
    }

    /*
        Validação das regras de Negocio do Produto,
        como nome e preço
    */
    private void validarDadosProduto(Produto produto) {
        // verificando se o nome do produto está preenchido
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        // verificando se o preço do produto é maior que zero
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
    }
}
