package com.deliverytech.delivery_api.exception;

import com.deliverytech.delivery_api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/clientes/1");
    }

    @Test
    @DisplayName("Deve retornar 404 ao tratar EntityNotFoundException")
    void should_Return404_When_EntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Cliente", 1L);

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Entidade não encontrada", response.getBody().getError());
        assertEquals(ex.getMessage(), response.getBody().getMessage());
        assertEquals("/api/clientes/1", response.getBody().getPath());
    }

    @Test
    @DisplayName("Deve retornar 400 ao tratar BusinessException")
    void should_Return400_When_BusinessException() {
        BusinessException ex = new BusinessException("Cliente inativo não pode fazer pedidos");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro de regra de negócio", response.getBody().getError());
        assertEquals(ex.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar 400 com detalhes ao tratar ConstraintViolationException")
    void should_Return400WithDetails_When_ConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("buscarProximos.cep");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Cep deve ter o formato válido");

        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>();
        violations.add(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Dados inválidos", response.getBody().getError());
        assertNotNull(response.getBody().getDetails());
        assertEquals("Cep deve ter o formato válido", response.getBody().getDetails().get("cep"));
    }

    @Test
    @DisplayName("Deve extrair apenas o último nó do path quando a propriedade não tem prefixo de método")
    void should_ExtractFieldName_When_PropertyPathHasNoDot() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("cep");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Cep inválido");

        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>();
        violations.add(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertEquals("Cep inválido", response.getBody().getDetails().get("cep"));
    }

    @Test
    @DisplayName("Deve retornar 500 ao tratar exceção genérica")
    void should_Return500_When_GenericException() {
        Exception ex = new RuntimeException("Falha inesperada no banco");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Erro interno do servidor", response.getBody().getError());
        // mensagem genérica de propósito - não deve vazar detalhe da exceção original
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().getMessage());
    }
}
