package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes do TelefoneValidator")
class TelefoneValidatorTest {

    private TelefoneValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new TelefoneValidator();
        context = null;
    }

    @Test
    @DisplayName("Deve aceitar telefone com 10 dígitos")
    void should_Accept_When_TenDigits() {
        assertTrue(validator.isValid("1133334444", context));
    }

    @Test
    @DisplayName("Deve aceitar telefone com 11 dígitos")
    void should_Accept_When_ElevenDigits() {
        assertTrue(validator.isValid("11999998888", context));
    }

    @Test
    @DisplayName("Deve aceitar telefone formatado com máscara")
    void should_Accept_When_FormattedWithMask() {
        assertTrue(validator.isValid("(11) 99999-8888", context));
    }

    @Test
    @DisplayName("Deve rejeitar telefone com quantidade errada de dígitos")
    void should_Reject_When_WrongLength() {
        assertFalse(validator.isValid("123456789", context));   // 9 dígitos
        assertFalse(validator.isValid("123456789012", context)); // 12 dígitos
    }

    @Test
    @DisplayName("Deve rejeitar telefone nulo ou em branco")
    void should_Reject_When_NullOrBlank() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("   ", context));
    }
}