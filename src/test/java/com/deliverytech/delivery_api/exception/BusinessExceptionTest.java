package com.deliverytech.delivery_api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Testes do BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("Deve criar exceção apenas com mensagem")
    void should_CreateException_When_OnlyMessage() {
        BusinessException ex = new BusinessException("Erro genérico");

        assertEquals("Erro genérico", ex.getMessage());
        assertNull(ex.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e código de erro")
    void should_CreateException_When_MessageAndErrorCode() {
        BusinessException ex = new BusinessException("Erro de negócio", "BUSINESS_ERROR");

        assertEquals("Erro de negócio", ex.getMessage());
        assertEquals("BUSINESS_ERROR", ex.getErrorCode());
    }

    @Test
    @DisplayName("Deve permitir alterar o código de erro após a criação")
    void should_UpdateErrorCode_When_SetterCalled() {
        BusinessException ex = new BusinessException("Erro");
        ex.setErrorCode("NOVO_CODIGO");

        assertEquals("NOVO_CODIGO", ex.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com causa")
    void should_CreateException_When_MessageAndCause() {
        Throwable causa = new RuntimeException("causa raiz");
        BusinessException ex = new BusinessException("Erro com causa", causa);

        assertEquals("Erro com causa", ex.getMessage());
        assertEquals(causa, ex.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, código de erro e causa")
    void should_CreateException_When_MessageErrorCodeAndCause() {
        Throwable causa = new RuntimeException("causa raiz");
        BusinessException ex = new BusinessException("Erro completo", "ERRO_X", causa);

        assertEquals("Erro completo", ex.getMessage());
        assertEquals("ERRO_X", ex.getErrorCode());
        assertEquals(causa, ex.getCause());
    }
}
