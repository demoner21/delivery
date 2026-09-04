package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.ItemPedido;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Iniciando a classe de Service para o Pedido
@Service
/*
Adicionando a anotação @Transactional para gerenciar transações de banco de dados
a utilização de transactional é importante para garantir a consistência dos dados,
especialmente em operações que envolvem múltiplas entidades e repositórios, 
como criar um pedido, adicionar itens e confirmar ou cancelar pedidos.
Isso ajuda a evitar problemas de integridade e garante que todas as alterações 
sejam aplicadas de forma atômica.
*/
@Transactional
public class PedidoService {

    // injetando o repositório de Pedido
    @Autowired
    private PedidoRepository pedidoRepository;

    // injetando o repositório de Cliente, necessário para vincular o pedido ao cliente
    @Autowired
    private ClienteRepository clienteRepository;

    /* 
    injetando o repositório de Restaurante, 
    necessário para vincular o pedido ao restaurante
    */ 
    @Autowired
    private RestauranteRepository restauranteRepository;

    // injetando o repositório de Produto, necessário para adicionar itens ao pedido
    @Autowired
    private ProdutoRepository produtoRepository;

    /*
     Criando novo pedido vinculado a um cliente e a um restaurante,
     lançando exceção caso o cliente ou o restaurante não sejam encontrados
     através do método findById dos respectivos repositórios,
     e também caso o cliente ou o restaurante estejam inativos
     */
    public Pedido criarPedido(Long clienteId, Long restauranteId) {
        // buscando o cliente pelo id e lançando exceção caso não seja encontrado
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));

        // buscando o restaurante pelo id e lançando exceção caso não seja encontrado
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        /*  verificando se o cliente está ativo
            utilizamos o ! para negar a condição, ou seja, 
            se o cliente não estiver ativo, lançamos a exceção 
        */
        if (!cliente.isAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos");
        }

        // verificando se o restaurante está ativo
        if (!restaurante.isAtivo()) {
            throw new IllegalArgumentException("Restaurante não está disponível");
        }

        // montando o pedido com status inicial pendente
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatusPedido(StatusPedido.PENDENTE);

        return pedidoRepository.save(pedido);
    }

    /*
     Adicionando um item ao pedido, validando se o pedido e o produto existem,
     se o produto está disponível, se a quantidade é válida
     e se o produto pertence ao mesmo restaurante do pedido
     */
    public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        // buscando o pedido pelo id e lançando exceção caso não seja encontrado
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        // buscando o produto pelo id e lançando exceção caso não seja encontrado
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        // verificando se o produto está disponível
        if (!produto.isDisponivel()) {
            throw new IllegalArgumentException("Produto não disponível: " + produto.getNome());
        }

        // verificando se a quantidade informada é válida
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Verificar se produto pertence ao mesmo restaurante do pedido
        if (!produto.getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
            throw new IllegalArgumentException("Produto não pertence ao restaurante do pedido");
        }

        // montando o item do pedido com os dados do produto
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        // calculando o preço total do item multiplicando o preço unitário pela quantidade
        item.setPrecoTotal(produto.getPreco().multiply(java.math.BigDecimal.valueOf(quantidade)));

        // adicionando o item à lista de itens do pedido
        pedido.getItens().add(item);

        return pedidoRepository.save(pedido);
    }

    /*
     Confirmando o pedido, validando se ele está pendente
     e se possui ao menos um item
     */
    public Pedido confirmarPedido(Long pedidoId) {
        // buscando o pedido pelo id e lançando exceção caso não seja encontrado
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        // verificando se o pedido está com status pendente
        if (pedido.getStatusPedido() != StatusPedido.PENDENTE) {
            throw new IllegalArgumentException("Apenas pedidos pendentes podem ser confirmados");
        }

        // verificando se o pedido possui ao menos um item
        if (pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
        }

        // atualizando o status do pedido para confirmado
        pedido.setStatusPedido(StatusPedido.CONFIRMADO);

        return pedidoRepository.save(pedido);
    }

    /*
    Buscar o pedido por Id, já trazendo os itens junto (fetch join).
    Usamos findByIdComItens em vez de findById para evitar o
    LazyInitializationException ao devolver o pedido como JSON no controller,
    já que a coleção "itens" é carregada dentro da mesma consulta.
    */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findByIdComItens(id);
    }

    /*
    Listar os pedidos de um cliente, ordenados do mais recente para o mais antigo.
    Usamos findByClienteIdComItens em vez de findByClienteIdOrderByDataPedidoDesc
    para trazer os itens de cada pedido já carregados (fetch join),
    evitando o LazyInitializationException ao devolver a lista como JSON.
    */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdComItens(clienteId);
    }

    /*
    Buscar pedido pelo número do pedido
    */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorNumero(String numeroPedido) {
        return Optional.ofNullable(pedidoRepository.findByNumeroPedido(numeroPedido));
    }

    /*
     Atualizando o status do pedido para o valor informado.
     Diferente do cancelarPedido, este método é genérico:
     não valida regras específicas de cancelamento, apenas
     aplica a transição de status recebida via parâmetro.
     */
    public Pedido atualizarStatus(Long pedidoId, StatusPedido status) {
        // buscando o pedido pelo id e lançando exceção caso não seja encontrado
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        // validando se um status foi informado
        if (status == null) {
            throw new IllegalArgumentException("Status deve ser informado");
        }

        // atualizando o status do pedido para o valor recebido
        pedido.setStatusPedido(status);

        return pedidoRepository.save(pedido);
    }

    /*
     Cancelando o pedido, validando se ele ainda não foi entregue
     e se ainda não está cancelado
     */
    public Pedido cancelarPedido(Long pedidoId, String motivo) {
        // buscando o pedido pelo id e lançando exceção caso não seja encontrado
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        // verificando se o pedido já foi entregue
        if (pedido.getStatusPedido() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Pedido já entregue não pode ser cancelado");
        }

        // verificando se o pedido já está cancelado
        if (pedido.getStatusPedido() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido já está cancelado");
        }
        // TO-DO: add caso seja cancelado o extorno da compra do cliente, caso tenha sido pago

        // atualizando o status do pedido para cancelado
        pedido.setStatusPedido(StatusPedido.CANCELADO);

        return pedidoRepository.save(pedido);
    }
}