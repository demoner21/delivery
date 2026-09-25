package com.deliverytech.delivery_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logUserAction(String userId, String action, String resource, Object details) {
        try {
            Map<String, Object> auditEvent = new HashMap<>();
            auditEvent.put("timestamp", LocalDateTime.now().toString());
            auditEvent.put("userId", userId);
            auditEvent.put("action", action);
            auditEvent.put("resource", resource);
            auditEvent.put("details", details);
            auditEvent.put("correlationId", MDC.get("correlationId"));
            auditEvent.put("sessionId", MDC.get("sessionId"));

            String jsonLog = objectMapper.writeValueAsString(auditEvent);
            auditLogger.info(jsonLog);

        } catch (Exception e) {
            auditLogger.error("Erro ao registrar evento de auditoria", e);
        }
    }

    public void logDataChange(String userId, String entity, String entityId,
                             Object oldValue, Object newValue, String operation) {
        try {
            Map<String, Object> changeEvent = new HashMap<>();
            changeEvent.put("timestamp", LocalDateTime.now().toString());
            changeEvent.put("userId", userId);
            changeEvent.put("entity", entity);
            changeEvent.put("entityId", entityId);
            changeEvent.put("operation", operation);
            changeEvent.put("oldValue", oldValue);
            changeEvent.put("newValue", newValue);
            changeEvent.put("correlationId", MDC.get("correlationId"));

            String jsonLog = objectMapper.writeValueAsString(changeEvent);
            auditLogger.info(jsonLog);

        } catch (Exception e) {
            auditLogger.error("Erro ao registrar mudança de dados", e);
        }
    }

    public void logSecurityEvent(String userId, String event, String details, boolean success) {
        try {
            Map<String, Object> securityEvent = new HashMap<>();
            securityEvent.put("timestamp", LocalDateTime.now().toString());
            securityEvent.put("userId", userId);
            securityEvent.put("event", event);
            securityEvent.put("details", details);
            securityEvent.put("success", success);
            securityEvent.put("correlationId", MDC.get("correlationId"));
            securityEvent.put("ipAddress", MDC.get("clientIp"));
            securityEvent.put("userAgent", MDC.get("userAgent"));

            String jsonLog = objectMapper.writeValueAsString(securityEvent);
            auditLogger.info(jsonLog);

        } catch (Exception e) {
            auditLogger.error("Erro ao registrar evento de segurança", e);
        }
    }
}
