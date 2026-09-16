package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


/* Incica que a notação deve ser colocada no JavaDOC */
@Documented
/* Ela diz "Lógica real que diz que se o CEP está certo ou errado não está aqui,
que ela mora dentro da class CEPValidator" */
@Constraint(validatedBy = CEPValidator.class)
/*
FIELD: diz que voce pode colocar ela como atributo de uma class
private String cep;

PARAMETER: Diz que voce pode usar em argumentos de metodos
Public void salvar(@ValidCEP string cep)
*/
@Target({ElementType.FIELD, ElementType.PARAMETER})
/*
Defini que essa anotação ira durar enqunato a aplicação JAVA estiver VIVA
*/
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    
    String message() default "Cep deve ter o formato válido ( 00000-00 ou 00000000)";
    Class<?>[] groups() default {};

    /* Class Curinga (Wildcard)
    Class <?>
    com o objetivo de não precisamos saber o tipo do objeto para fazer o trabalho
    é usando justamnte por que a validação ela por si só e 
    genérica o suficiente para não precisar de um tipo estrito.
    */
    Class<? extends  Payload>[] payload() default {};
}


