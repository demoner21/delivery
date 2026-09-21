package com.deliverytech.delivery_api.dto;

public class ClienteResponseDTO {
    
    /* neste cenario teremos a mesma copia
       daquilo que declamos dentro da nossa
       entity
    */
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public boolean isAtivo() { return ativo;}
    public void setAtivo(boolean ativo) {this.ativo = ativo; }
}
