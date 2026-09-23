package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.*;
import com.deliverytech.delivery_api.service.impl.PedidoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste da PedidoService")
public class PedidoServiceTest {
    
   @Mock
   private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private ModelMapper modelMapper;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    /*Configuramos o nosso objeto inicial */
    @BeforeEach
    void setUP() {
        /* Vamos isar o ModelMapper real (CUIDADO, NÂO RECOMENDADO, NÂO MOCKADO)
        para termos o comportamento de mapeamento mais proximo do real
        */
       modelMapper = new ModelMapper();
       org.springframework.test.util.ReflectionTestUtils.setField(pedidoService, "modelMapper", modelMapper);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setAtivo(true);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Pizzaria Bella");
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Pizza Margherita");
        produto.setPreco(new BigDecimal("35.90"));
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);
    }

    // Criar o Pedido

    @Test
    @DisplayName("Deve Criar um pedido com sucesso quando os dados forém válidos")
    void should_CreatePedido_When_ValidData() {

        PedidoDTO dto = criarPedidoDTO();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.criarPedido(dto);

        assertNotNull(response);
        assertEquals(StatusPedido.PENDENTE, response.getStatusPedido());
        assertEquals(new BigDecimal("71.80"), response.getSubtotal());
        assertEquals(new BigDecimal("5.00"), response.getTaxaEntrega());
        assertEquals(new BigDecimal("76.80"), response.getValorTotal());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    private PedidoDTO criarPedidoDTO() {
        PedidoDTO dto = new PedidoDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(produto.getId());
        item.setQuantidade(2);
        dto.setItens(Arrays.asList(item));
        return dto;
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com cliente inexistente")
    void should_ThrowException_When_ClienteNotFound() {
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.criarPedido(dto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com cliente inativo")
    void should_ThrowException_When_ClienteInativo() {
        cliente.setAtivo(false);
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(dto));
        assertEquals("Cliente inativo não pode fazer pedidos", ex.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com restaurante inexistente")
    void should_ThrowException_When_RestauranteNotFound() {
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.criarPedido(dto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com restaurante inativo")
    void should_ThrowException_When_RestauranteInativo() {
        restaurante.setAtivo(false);
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(dto));
        assertEquals("Restaurante não está disponível", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com produto inexistente")
    void should_ThrowException_When_ProdutoNotFound() {
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.criarPedido(dto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com produto indisponível")
    void should_ThrowException_When_ProdutoIndisponivel() {
        produto.setDisponivel(false);
        PedidoDTO dto = criarPedidoDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(dto));
        assertTrue(ex.getMessage().contains("Produto indisponível"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não pertence ao restaurante do pedido")
    void should_ThrowException_When_ProdutoNaoPertenceAoRestaurante() {
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(2L); // esse id dois está sendo passado para mostrar outro restaurante
        produto.setRestaurante(outroRestaurante);

        PedidoDTO dto = criarPedidoDTO(); // dto aponta restauranteId = 1L
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(dto));
        assertEquals("Produto não pertence ao restaurante selecionado", ex.getMessage());
    }

    // Buscar Pedido por ID

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void should_FindPedidoById_When_Exists() {
        Pedido pedido = criarPedidoEntity(1L, StatusPedido.PENDENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDTO response = pedidoService.buscarPedidoPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedido inexistente por ID")
    void should_ThrowException_When_PedidoNotFoundById() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.buscarPedidoPorId(999L));
    }

    // Atualizar Status do Pedido

    @ParameterizedTest(name = "{0} -> {1} deve ser uma transição válida")
    @CsvSource({
        "PENDENTE, CONFIRMADO",
        "PENDENTE, CANCELADO",
        "CONFIRMADO, PREPARANDO",
        "CONFIRMADO, CANCELADO",
        "PREPARANDO, SAIU_PARA_ENTREGA",
        "SAIU_PARA_ENTREGA, ENTREGUE"
    })
    @DisplayName("Deve permitir transições de status válidas")
    void should_UpdateStatus_When_TransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        Pedido pedido = criarPedidoEntity(1L, statusAtual);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponseDTO response = pedidoService.atualizarStatusPedido(1L, novoStatus);

        assertEquals(novoStatus, response.getStatusPedido());
    }

    @ParameterizedTest(name = "{0} -> {1} deve ser uma transição inválida")
    @CsvSource({
        "PENDENTE, ENTREGUE",
        "PENDENTE, PREPARANDO",
        "CONFIRMADO, ENTREGUE",
        "PREPARANDO, CANCELADO",
        "SAIU_PARA_ENTREGA, CANCELADO",
        "ENTREGUE, CANCELADO",
        "CANCELADO, CONFIRMADO"
    })
    @DisplayName("Deve rejeitar transições de status inválidas")
    void should_ThrowException_When_TransicaoInvalida(StatusPedido statusAtual, StatusPedido novoStatus) {
        Pedido pedido = criarPedidoEntity(1L, statusAtual);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.atualizarStatusPedido(1L, novoStatus));
        assertTrue(ex.getMessage().contains("Transição de status inválida"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status de pedido inexistente")
    void should_ThrowException_When_UpdatingStatusOfNonExistentPedido() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.atualizarStatusPedido(999L, StatusPedido.CONFIRMADO));
    }

    // ---------- cancelarPedido ----------

    @ParameterizedTest
    @EnumSource(value = StatusPedido.class, names = {"PENDENTE", "CONFIRMADO"})
    @DisplayName("Deve cancelar pedido quando status permite cancelamento")
    void should_CancelPedido_When_StatusPermiteCancelamento(StatusPedido statusAtual) {
        Pedido pedido = criarPedidoEntity(1L, statusAtual);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.cancelarPedido(1L);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatusPedido());
        verify(pedidoRepository).save(pedido);
    }

    @ParameterizedTest
    @EnumSource(value = StatusPedido.class, names = {"PREPARANDO", "SAIU_PARA_ENTREGA", "ENTREGUE", "CANCELADO"})
    @DisplayName("Não deve cancelar pedido quando status não permite mais cancelamento")
    void should_NotCancelPedido_When_StatusNaoPermiteCancelamento(StatusPedido statusAtual) {
        Pedido pedido = criarPedidoEntity(1L, statusAtual);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pedidoService.cancelarPedido(1L));
        assertTrue(ex.getMessage().contains("não pode ser cancelado"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar pedido inexistente")
    void should_ThrowException_When_CancelingNonExistentPedido() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.cancelarPedido(999L));
    }

    // ---------- calcularTotalPedido ----------

    @Test
    @DisplayName("Deve calcular total do pedido corretamente")
    void should_CalculateTotal_When_ValidItems() {
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setPreco(new BigDecimal("18.90"));

        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(1L);
        item1.setQuantidade(2); // 2 x 35.90 = 71.80

        ItemPedidoDTO item2 = new ItemPedidoDTO();
        item2.setProdutoId(2L);
        item2.setQuantidade(1); // 1 x 18.90 = 18.90

        CalculoPedidoDTO dto = new CalculoPedidoDTO();
        dto.setItens(Arrays.asList(item1, item2));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(produto2));

        CalculoPedidoResponseDTO response = pedidoService.calcularTotalPedido(dto);

        assertEquals(new BigDecimal("90.70"), response.getValorTotal());
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular total com produto inexistente")
    void should_ThrowException_When_CalculatingTotalWithNonExistentProduct() {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(999L);
        item.setQuantidade(1);

        CalculoPedidoDTO dto = new CalculoPedidoDTO();
        dto.setItens(List.of(item));

        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.calcularTotalPedido(dto));
    }

    // ---------- listarPedidos / buscarPedidosPorCliente / buscarPedidosPorRestaurante ----------

    @Test
    @DisplayName("Deve buscar pedidos filtrando por cliente")
    void should_FindPedidos_When_FilteringByCliente() {
        Pedido pedidoDoCliente = criarPedidoEntity(1L, StatusPedido.PENDENTE);
        pedidoDoCliente.setCliente(cliente);

        Pedido pedidoDeOutroCliente = criarPedidoEntity(2L, StatusPedido.PENDENTE);
        Cliente outroCliente = new Cliente();
        outroCliente.setId(2L);
        pedidoDeOutroCliente.setCliente(outroCliente);

        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoDoCliente, pedidoDeOutroCliente));

        List<PedidoResponseDTO> resultado = pedidoService.buscarPedidosPorCliente(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getClienteId());
    }

    @Test
    @DisplayName("Deve buscar pedidos filtrando por restaurante e status")
    void should_FindPedidos_When_FilteringByRestauranteAndStatus() {
        Pedido pedidoConfirmado = criarPedidoEntity(1L, StatusPedido.CONFIRMADO);
        pedidoConfirmado.setRestaurante(restaurante);

        Pedido pedidoPendente = criarPedidoEntity(2L, StatusPedido.PENDENTE);
        pedidoPendente.setRestaurante(restaurante);

        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoConfirmado, pedidoPendente));

        List<PedidoResponseDTO> resultado =
                pedidoService.buscarPedidosPorRestaurante(1L, StatusPedido.CONFIRMADO);

        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.CONFIRMADO, resultado.get(0).getStatusPedido());
    }

    // ---------- helpers ----------

    private Pedido criarPedidoEntity(Long id, StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatusPedido(status);
        pedido.setSubtotal(new BigDecimal("35.90"));
        pedido.setTaxaEntrega(new BigDecimal("5.00"));
        pedido.setValorTotal(new BigDecimal("40.90"));
        return pedido;
    }
}
