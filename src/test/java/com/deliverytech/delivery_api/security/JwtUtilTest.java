package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.enums.Role;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do JwtUtil")
class JwtUtilTest {

    // precisa ter pelo menos 32 bytes para HS256
    private static final String SECRET = "minhaChaveSecretaSuperSeguraParaTestesJWT123456";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hora

    private JwtUtil jwtUtil;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION_MS);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaCodificada");
        usuario.setNome("João Silva");
        usuario.setRole(Role.CLIENTE);
        usuario.setAtivo(true);
    }

    @Test
    @DisplayName("Deve gerar um token não nulo e não vazio")
    void should_GenerateToken_When_ValidUserDetails() {
        String token = jwtUtil.generateToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Deve extrair o username (subject) corretamente do token")
    void should_ExtractUsername_When_TokenIsValid() {
        String token = jwtUtil.generateToken(usuario);

        assertEquals("joao@email.com", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Deve extrair claims customizadas quando o UserDetails é um Usuario")
    void should_ExtractCustomClaims_When_UserDetailsIsUsuario() {
        usuario.setRestauranteId(10L);
        String token = jwtUtil.generateToken(usuario);

        assertEquals(1L, jwtUtil.extractUserId(token));
        assertEquals("CLIENTE", jwtUtil.extractRole(token));
        assertEquals("João Silva", jwtUtil.extractNome(token));
        assertEquals(10L, jwtUtil.extractRestauranteId(token));
    }

    @Test
    @DisplayName("Não deve incluir restauranteId no token quando o usuário não tem um vinculado")
    void should_NotIncludeRestauranteId_When_UsuarioHasNoRestaurante() {
        usuario.setRestauranteId(null);
        String token = jwtUtil.generateToken(usuario);

        assertNull(jwtUtil.extractRestauranteId(token));
    }

    @Test
    @DisplayName("Não deve gerar claims customizadas para UserDetails que não seja Usuario")
    void should_NotExtractCustomClaims_When_UserDetailsIsNotUsuario() {
        UserDetails genericUser = User.withUsername("generico@email.com")
                .password("senha")
                .authorities("ROLE_CLIENTE")
                .build();

        String token = jwtUtil.generateToken(genericUser);

        assertEquals("generico@email.com", jwtUtil.extractUsername(token));
        assertNull(jwtUtil.extractUserId(token));
        assertNull(jwtUtil.extractRole(token));
        assertNull(jwtUtil.extractNome(token));
        assertNull(jwtUtil.extractRestauranteId(token));
    }

    @Test
    @DisplayName("Deve retornar data de expiração no futuro para token recém-gerado")
    void should_ReturnFutureExpiration_When_TokenIsFresh() {
        String token = jwtUtil.generateToken(usuario);

        Date expiration = jwtUtil.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Deve considerar o token como não expirado quando dentro da validade")
    void should_ReturnFalse_When_TokenIsNotExpired() {
        String token = jwtUtil.generateToken(usuario);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Deve considerar o token como expirado quando a validade já passou")
    void should_ReturnTrue_When_TokenIsExpired() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String tokenExpirado = jwtUtil.generateToken(usuario);
    
        assertTrue(jwtUtil.isTokenExpired(tokenExpirado));
    }

    @Test
    @DisplayName("Deve validar o token quando username bate e token não está expirado")
    void should_ValidateToken_When_UsernameMatchesAndNotExpired() {
        String token = jwtUtil.generateToken(usuario);

        assertTrue(jwtUtil.validateToken(token, usuario));
    }

    @Test
    @DisplayName("Não deve validar o token quando o username não bate com o UserDetails")
    void should_NotValidateToken_When_UsernameDoesNotMatch() {
        String token = jwtUtil.generateToken(usuario);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro@email.com");

        assertFalse(jwtUtil.validateToken(token, outroUsuario));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar extrair claims de um token malformado")
    void should_ThrowException_When_TokenIsMalformed() {
        String tokenInvalido = "token.invalido.aqui";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.extractUsername(tokenInvalido));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar extrair claims de um token assinado com outra chave")
    void should_ThrowException_When_TokenSignedWithDifferentSecret() {
        String token = jwtUtil.generateToken(usuario);

        JwtUtil outroJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(outroJwtUtil, "secret", "outraChaveSecretaCompletamenteDiferenteDeTeste");
        ReflectionTestUtils.setField(outroJwtUtil, "expiration", EXPIRATION_MS);

        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> outroJwtUtil.extractUsername(token));
    }
}