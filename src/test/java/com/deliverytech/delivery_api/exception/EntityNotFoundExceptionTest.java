package com.deliverytech.delivery_api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do EntityNotFoundException")
class EntityNotFoundExceptionTest {

    @Test
    @DisplayName("Deve montar mensagem padrão a partir de nome e ID da entidade")
    void should_BuildDefaultMessage_When_EntityNameAndIdProvided() {
        EntityNotFoundException ex = new EntityNotFoundException("Cliente", 10L);

        assertEquals("Cliente com ID 10 não foi encontrado(a)", ex.getMessage());
        assertEquals("Cliente", ex.getEntityName());
        assertEquals(10L, ex.getEntityId());
        assertEquals("ENTITY_NOT_FOUND", ex.getErrorCode());
    }

    @Test
    @DisplayName("Deve aceitar mensagem customizada")
    void should_UseCustomMessage_When_OnlyMessageProvided() {
        EntityNotFoundException ex = new EntityNotFoundException("Restaurante inativo não pode ser buscado");

        assertEquals("Restaurante inativo não pode ser buscado", ex.getMessage());
        assertEquals("ENTITY_NOT_FOUND", ex.getErrorCode());
        assertNull(ex.getEntityName());
    }
}
