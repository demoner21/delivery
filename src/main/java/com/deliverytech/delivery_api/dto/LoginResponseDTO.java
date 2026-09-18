package com.deliverytech.delivery_api.dto;

public class LoginResponseDTO {
    
    private String token;
    private String tipo = "Bearer";
    private Long expiracao;
    private UserResponseDTO usuario;

    // Construtores
    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long expiracao, UserResponseDTO usuario) {
        this.token = token;
        this.expiracao = expiracao;
        this.usuario = usuario;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getExpiracao() { return expiracao; }
    public void setExpiracao(Long expiracao) { this.expiracao = expiracao; }

    public UserResponseDTO getUsuario() { return usuario; }
    public void setUsuario(UserResponseDTO usuario) { this.usuario = usuario; }
}
