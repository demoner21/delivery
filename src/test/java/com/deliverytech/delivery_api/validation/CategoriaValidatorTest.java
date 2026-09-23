package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes do CategoriaValidator")
class CategoriaValidatorTest {

    private CategoriaValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CategoriaValidator();
        context = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {"BRASILEIRA", "ITALIANA", "JAPONESA", "PIZZA", "ACAI"})
    @DisplayName("Deve aceitar categorias válidas (maiúsculas)")
    void should_Accept_When_ValidCategoryUppercase(String categoria) {
        assertTrue(validator.isValid(categoria, context));
    }

    @Test
    @DisplayName("Deve aceitar categoria válida independente de caixa")
    void should_Accept_When_ValidCategoryLowercase() {
        assertTrue(validator.isValid("italiana", context));
        assertTrue(validator.isValid("Pizza", context));
    }

    @Test
    @DisplayName("Deve rejeitar categoria fora da lista")
    void should_Reject_When_CategoryNotInList() {
        assertFalse(validator.isValid("COREANA", context));
    }

    @Test
    @DisplayName("Deve rejeitar categoria nula ou em branco")
    void should_Reject_When_NullOrBlank() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("   ", context));
    }
}
