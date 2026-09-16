package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public void initialize(ValidTelefone constraintAnnotaion) {
        // inicialização se necessãrio
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }

        // remover caracteres especiais e espaço
        String cleanTelefone = telefone.replaceAll("[^\\d]", "");

        //verificar o comprimento
        return cleanTelefone.length() == 10 || cleanTelefone.length() == 11;
    }
}
