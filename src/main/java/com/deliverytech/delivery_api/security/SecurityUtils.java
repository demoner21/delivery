package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (Usuario) authentication.getPrincipal();
        }

        throw new RuntimeException("Usuário não autenticado");
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUserRole() {
        return getCurrentUser().getRole().name();
    }

    public static Long getCurrentRestauranteId() {
        return getCurrentUser().getRestauranteId();
    }

    public static boolean hasRole(String role) {
        try {
            return getCurrentUser().getRole().name().equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isCliente() {
        return hasRole("CLIENTE");
    }

    public static boolean isRestaurante() {
        return hasRole("RESTAURANTE");
    }

    public static boolean isEntregador() {
        return hasRole("ENTREGADOR");
    }
}