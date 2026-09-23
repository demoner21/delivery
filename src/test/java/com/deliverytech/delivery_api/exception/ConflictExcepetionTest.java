package com.deliverytech.delivery_api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ConflictExcepetion")
class ConflictExcepetionTest {

    @Test
    @DisplayName("Deve criar exceção apenas com mensagem")
    void should_CreateException_When_OnlyMessage() {
        ConflictExcepetion ex = new ConflictExcepetion("Conflito genérico");

        assertEquals("Conflito genérico", ex.getMessage());
        assertEquals("CONFLICT", ex.getErrorCode());
        assertNull(ex.getConflictField());
        assertNull(ex.getConflictValue());
    }

    @Test
    @DisplayName("Deve criar exceção com campo e valor em conflito")
    void should_CreateException_When_FieldAndValueProvided() {
        ConflictExcepetion ex = new ConflictExcepetion("Email já cadastrado", "email", "joao@email.com");

        assertEquals("Email já cadastrado", ex.getMessage());
        assertEquals("CONFLICT", ex.getErrorCode());
        assertEquals("email", ex.getConflictField());
        assertEquals("joao@email.com", ex.getConflictValue());
    }
}
