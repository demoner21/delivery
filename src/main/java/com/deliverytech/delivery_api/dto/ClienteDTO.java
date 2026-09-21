package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.*;

public class ClienteDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max =100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @NotBlank(message  = "")
    @Email(message = "Email e obrigatorio")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Telefone deve ter Apenas Digitos e deve ser DD + Telefone") 
    private String telefone;

    @NotBlank(message = "Endereço é obrigatorio")
    @Size(max = 200, message = "Endereco deve conter no maximo 200 caracteres")
    private String endereco;

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email;}

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone;}

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco;}

}
