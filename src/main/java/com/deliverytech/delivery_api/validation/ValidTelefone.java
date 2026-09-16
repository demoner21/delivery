package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


/* Incica que a notação deve ser colocada no JavaDOC */
@Documented
/* Ela diz "Lógica real que diz que se o Telefone está certo ou errado não está aqui,
que ela mora dentro da class TelefoneValidator" */
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
   
    String message() default "Telefone deve ter o formato válido (10 ou 11 digitos)";
    Class<?>[] groups() default {};
    Class<? extends  Payload>[] payload() default {};

}
