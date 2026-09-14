package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class ProdutoDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @DecimalMax(value = "500.00", message = "Preço não pode exceder R$ 500,00")
    private BigDecimal preco;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @NotNull(message = "Restaurante ID é obrigatório")
    @Positive(message = "Restaurante ID deve ser positivo")
    private Long restauranteId;

    @Pattern(regexp = "^(https?://).*\\.(jpg|jpeg|png|gif)$",
             message = "URL da imagem deve ser válida e ter formato JPG, JPEG, PNG ou GIF")
    private String imagemUrl;

    @AssertTrue(message = "Produto deve estar disponível por padrão")
    private Boolean disponivel = true;

}
