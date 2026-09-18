#!/bin/bash

# Define o caminho para o arquivo .env
ENV_FILE="./.env"

# 1. Verifica se o arquivo .env existe
if [ -f "$ENV_FILE" ]; then
    echo "🔵 Carregando variáveis de ambiente do $ENV_FILE..."

    # O comando 'set -a' faz com que todas as variáveis criadas ou modificadas
    # a partir de agora sejam exportadas automaticamente para sub-processos
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "⚠️  Arquivo .env não encontrado em $ENV_FILE"
    echo "   Certifique-se de estar na pasta raiz do projeto backend."
    exit 1
fi

# 2. Inicia a aplicação Spring Boot
echo "🚀 Iniciando a aplicação..."

# Descomente a linha abaixo que faz mais sentido para o seu momento:

# OPÇÃO A: Para rodar em desenvolvimento usando o Maven Wrapper
mvn spring-boot:run -DskipTests

# OPÇÃO B: Para rodar em produção (após compilar com ./mvnw clean package)
# java -jar target/delivery-api-0.0.1-SNAPSHOT.jar
