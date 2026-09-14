package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.*;
import com.deliverytech.delivery_api.service.PedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Method;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoDTO dto) {
        // 1. Validar cliente existe e está ativo
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.isAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos");
        }

        // 2. Validar restaurante existe e está ativo
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        if (!restaurante.isAtivo()) {
            throw new BusinessException("Restaurante não está disponível");
        }

        // 3. Validar todos os produtos existem e estão disponíveis
        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (!produto.isDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
                throw new BusinessException("Produto não pertence ao restaurante selecionado");
            }

            // Criar item do pedido
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            itensPedido.add(item);
            subtotal = subtotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
        }

        // 4. Calcular total do pedido
        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal valorTotal = subtotal.add(taxaEntrega);

        // 5. Salvar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        setPedidoStatus(pedido, StatusPedido.PENDENTE);
        setProperty(pedido, "EnderecoEntrega", dto.getEnderecoEntrega());
        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 6. Salvar itens do pedido
        for (ItemPedido item : itensPedido) {
            item.setPedido(pedidoSalvo);
        }
        pedidoSalvo.setItens(itensPedido);

        // 7. Atualizar estoque (se aplicável) - Simulação
        // Em um cenário real, aqui seria decrementado o estoque

        // 8. Retornar pedido criado
        return modelMapper.map(pedidoSalvo, PedidoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getCliente() != null
                && clienteId.equals(pedido.getCliente().getId()))
            .collect(Collectors.toList());

        return pedidos.stream()
            .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status) {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getRestaurante() != null 
                && restauranteId.equals(pedido.getRestaurante().getId()))
            .filter(pedido -> status == null || status.equals(getPedidoStatus(pedido)))
            .collect(Collectors.toList());

        return pedidos.stream()
            .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable)
            .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class));
    }

    @Override
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        // Validar transições de status permitidas
        StatusPedido statusAtual = getPedidoStatus(pedido);
        if (!isTransicaoValida(statusAtual, novoStatus)) {
            throw new BusinessException("Transição de status inválida: " +
            statusAtual + " -> " + novoStatus);
        }

        setPedidoStatus(pedido, novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return modelMapper.map(pedidoAtualizado, PedidoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto) {
        BigDecimal total = BigDecimal.ZERO;

        for (Object itemObject : dto.getItens()) {
            ItemPedidoDTO item = (ItemPedidoDTO) itemObject;
            Produto produto = produtoRepository.findById(item.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            BigDecimal subtotalItem = produto.getPreco()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(subtotalItem);
        }

        return new CalculoPedidoResponseDTO(total);
    }

    @Override
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        StatusPedido statusAtual = getPedidoStatus(pedido);
        if (!podeSerCancelado(statusAtual)) {
            throw new BusinessException("Pedido não pode ser cancelado no status: " + statusAtual);
        }

        setPedidoStatus(pedido, StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    private StatusPedido getPedidoStatus(Pedido pedido) {
        Object status = getProperty(pedido, "Status");
        return status instanceof StatusPedido ? (StatusPedido) status : null;
    }

    private void setPedidoStatus(Pedido pedido, StatusPedido status) {
        setProperty(pedido, "Status", status);
    }

    private Object getProperty(Object target, String property) {
        try {
            Method method = target.getClass().getMethod("get" + property);
            return method.invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new BusinessException("Propriedade não encontrada: " + property);
        }
    }

    private void setProperty(Object target, String property, Object value) {
        try {
            for (Method method : target.getClass().getMethods()) {
                if (method.getName().equals("set" + property) && method.getParameterCount() == 1) {
                    method.invoke(target, value);
                    return;
                }
            }
            throw new NoSuchMethodException(property);
        } catch (ReflectiveOperationException e) {
            throw new BusinessException("Propriedade não encontrada: " + property);
        }
    }

    private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        // Implementar lógica de transições válidas
        switch (statusAtual) {
            case PENDENTE:
                return novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO:
                return novoStatus == StatusPedido.PREPARANDO || novoStatus == StatusPedido.CANCELADO;
            case PREPARANDO:
                return novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA:
                return novoStatus == StatusPedido.ENTREGUE;
            default:
                return false;
        }
    }

    private boolean podeSerCancelado(StatusPedido status) {
        return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
    }
}
