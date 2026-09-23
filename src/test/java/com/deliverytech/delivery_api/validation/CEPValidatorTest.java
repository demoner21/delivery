package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes do CEPValidator")
class CEPValidatorTest {

    private CEPValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CEPValidator();
        context = null; // não é usado na implementação atual
    }

    @Test
    @DisplayName("Deve aceitar CEP no formato 00000-000")
    void should_Accept_When_FormatWithHyphen() {
        assertTrue(validator.isValid("01310-100", context));
    }

    @Test
    @DisplayName("Deve aceitar CEP no formato 00000000")
    void should_Accept_When_FormatWithoutHyphen() {
        assertTrue(validator.isValid("01310100", context));
    }

    @Test
    @DisplayName("Deve rejeitar CEP com menos de 8 dígitos")
    void should_Reject_When_TooShort() {
        assertFalse(validator.isValid("1234", context));
    }

    @Test
    @DisplayName("Deve rejeitar CEP com letras")
    void should_Reject_When_ContainsLetters() {
        assertFalse(validator.isValid("abcde-fgh", context));
    }

    @Test
    @DisplayName("Deve rejeitar CEP nulo")
    void should_Reject_When_Null() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Deve rejeitar CEP vazio ou em branco")
    void should_Reject_When_BlankOrEmpty() {
        assertFalse(validator.isValid("", context));
        assertFalse(validator.isValid("   ", context));
    }
}