package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.ClienteDTO;
import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    // ModelMapper real: os testes validam o mapeamento DTO <-> entidade de verdade
    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setCpf("12345678901");
        cliente.setEndereco("Rua A, 100");
        cliente.setAtivo(true);
    }

    private ClienteDTO criarDto() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNome("João Silva");
        dto.setCpf("12345678901");
        dto.setEmail("joao@email.com");
        dto.setTelefone("11999999999");
        dto.setEndereco("Rua A, 100");
        return dto;
    }

    // ---------- cadastrarCliente ----------

    @Test
    @DisplayName("Deve salvar cliente com dados válidos")
    void should_SaveCliente_When_ValidData() {
        // Given
        ClienteDTO dto = criarDto();
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        // When
        ClienteResponseDTO resultado = clienteService.cadastrarCliente(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        assertEquals("12345678901", resultado.getCpf());
        assertTrue(resultado.isAtivo());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        assertEquals("12345678901", captor.getValue().getCpf());
        assertTrue(captor.getValue().isAtivo());
        verify(clienteRepository).findByEmail("joao@email.com");
        verify(clienteRepository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void should_ThrowException_When_EmailAlreadyExists() {
        // Given
        ClienteDTO dto = criarDto();
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(cliente));

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> clienteService.cadastrarCliente(dto)
        );

        assertEquals("Email já cadastrado: joao@email.com", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void should_ThrowException_When_CpfAlreadyExists() {
        // Given
        ClienteDTO dto = criarDto();
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> clienteService.cadastrarCliente(dto)
        );

        assertEquals("CPF já cadastrado: 12345678901", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    // ---------- buscas ----------

    @Test
    @DisplayName("Deve buscar cliente por ID existente")
    void should_ReturnCliente_When_IdExists() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        ClienteResponseDTO resultado = clienteService.buscarClientePorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(clienteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID não existe")
    void should_ThrowException_When_IdNotExists() {
        // Given
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> clienteService.buscarClientePorId(999L));
        verify(clienteRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF existente")
    void should_ReturnCliente_When_CpfExists() {
        // Given
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));

        // When
        ClienteResponseDTO resultado = clienteService.buscarClientePorCpf("12345678901");

        // Then
        assertNotNull(resultado);
        assertEquals("12345678901", resultado.getCpf());
        assertEquals("João Silva", resultado.getNome());
        verify(clienteRepository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF não existe")
    void should_ThrowException_When_CpfNotExists() {
        // Given
        when(clienteRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> clienteService.buscarClientePorCpf("00000000000")
        );

        assertTrue(exception.getMessage().contains("00000000000"));
    }

    @Test
    @DisplayName("Deve listar apenas clientes ativos")
    void should_ReturnActiveClientes_When_Requested() {
        // Given
        when(clienteRepository.findByAtivoTrue()).thenReturn(List.of(cliente));

        // When
        List<ClienteResponseDTO> resultado = clienteService.listarClientesAtivos();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(clienteRepository).findByAtivoTrue();
    }

    // ---------- atualizarCliente ----------

    @Test
    @DisplayName("Deve atualizar cliente existente")
    void should_UpdateCliente_When_ClienteExists() {
        // Given
        ClienteDTO dto = criarDto();
        dto.setNome("João Santos");
        dto.setEmail("joao.santos@email.com");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByEmail("joao.santos@email.com")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, dto);

        // Then
        assertNotNull(resultado);
        assertEquals("João Santos", resultado.getNome());
        assertEquals("joao.santos@email.com", resultado.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
        // CPF não mudou, então não deve consultar duplicidade
        verify(clienteRepository, never()).existsByCpf(anyString());
    }

    @Test
    @DisplayName("Não deve atualizar quando o novo email pertence a outro cliente")
    void should_ThrowException_When_UpdateEmailBelongsToAnotherCliente() {
        // Given
        ClienteDTO dto = criarDto();
        dto.setEmail("outro@email.com");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(new Cliente()));

        // When & Then
        assertThrows(BusinessException.class,
            () -> clienteService.atualizarCliente(1L, dto));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando o novo CPF pertence a outro cliente")
    void should_ThrowException_When_UpdateCpfBelongsToAnotherCliente() {
        // Given
        ClienteDTO dto = criarDto();
        dto.setCpf("99999999999");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByCpf("99999999999")).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> clienteService.atualizarCliente(1L, dto)
        );

        assertEquals("CPF já cadastrado: 99999999999", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void should_ThrowException_When_UpdatingNonExistentCliente() {
        // Given
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> clienteService.atualizarCliente(999L, criarDto()));
        verify(clienteRepository, never()).save(any());
    }

    // ---------- ativarDesativarCliente ----------

    @Test
    @DisplayName("Deve desativar cliente ativo")
    void should_DeactivateCliente_When_ClienteIsActive() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ClienteResponseDTO resultado = clienteService.ativarDesativarCliente(1L);

        // Then
        assertFalse(resultado.isAtivo());
        verify(clienteRepository).save(cliente);
    }
}
