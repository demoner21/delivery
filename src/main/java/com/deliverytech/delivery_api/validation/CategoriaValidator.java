package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> {
    
    private static final List<String> CATEGORIAS_VALIDAS = Arrays.asList(
        "BRASILEIRA", "ITALIANA", "JAPONESA", "CHINESA", "MEXICANA",
        "FAST_FOOD", "PIZZA", "HAMBURGUER", "SAUDAVEL", "VEGETARIANA",
        "VEGANA", "DOCES", "BEBIDAS", "LANCHES", "ACAI"
    );

    @Override
    public void initialize(ValidCategoria constraintAnnotaion) {

    }

    @Override
    public boolean isValid(String categoria, ConstraintValidatorContext context) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return false;
        }

        return CATEGORIAS_VALIDAS.contains(categoria.toUpperCase());
    }
}
