# 📋 TaskFlow API

API REST para gerenciamento de tarefas pessoais, desenvolvida com Spring Boot 4.0.3 e Java 17.

## 🎯 Sobre o Projeto

Sistema completo para gerenciamento de tarefas que permite:

- ✅ Cadastro e gerenciamento de usuários
- ✅ Criação de tarefas com prioridade e prazo
- ✅ Controle de status com regras de transição
- ✅ Listagem paginada com filtro por status
- ✅ Consulta de tarefas atrasadas
- ✅ Validação de dados e regras de negócio
- ✅ Tratamento centralizado de erros com respostas padronizadas

## ✨ Funcionalidades

### Usuários

- ✅ Cadastro com nome e email
- ✅ Email normalizado (minúsculas e sem espaços) e único no sistema
- ✅ Atualização de dados com verificação de conflito de email
- ✅ Exclusão bloqueada enquanto o usuário possuir tarefas

### Tarefas

- ✅ Criação de tarefas vinculadas a um usuário (sempre nascem `PENDENTE`)
- ✅ Definição de prioridade: `BAIXA`, `MEDIA`, `ALTA`
- ✅ Status: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`
- ✅ Regras de transição entre status (ex.: tarefa concluída não pode ser reaberta)
- ✅ Atualização completa via PUT
- ✅ Atualização parcial de status via PATCH
- ✅ Listagem paginada, ordenável e filtrável por status
- ✅ Listagem de todas as tarefas de um usuário
- ✅ Listagem de tarefas atrasadas (vencidas e ainda abertas)
- ✅ Validação de usuário existente ao criar ou atualizar tarefa

## 🛠 Tecnologias Utilizadas

### Backend

- ✅ Java 17
- ✅ Spring Boot 4.0.3
- ✅ Spring Web (MVC)
- ✅ Spring Data JPA
- ✅ Spring Validation
- ✅ Spring DevTools
- ✅ Hibernate (JPA Provider)
- ✅ Lombok: redução de boilerplate

### Banco de Dados

- ✅ PostgreSQL (execução)
- ✅ H2 Database em memória (testes)

### Documentação

- ✅ SpringDoc OpenAPI 3.0.2 (Swagger UI)

### Testes

- ✅ JUnit 5
- ✅ Mockito
- ✅ Spring Boot Test

### Build

- ✅ Maven

## 🏗 Arquitetura

O projeto segue uma arquitetura em camadas (Controller-Service-Repository), com mapeamento explícito entre entidades e DTOs:

```
com.thyago.taskflow_api
├── config/          # Fuso horário padrão e Clock
├── controller/      # Endpoints REST
├── dto/             # Requests e responses (records)
├── entity/          # Entidades JPA
├── enums/           # Enumerações (StatusTarefa, PrioridadeTarefa)
├── exception/       # Exceções de negócio e handler global
├── mapper/          # Conversão entre entidades e DTOs
├── repository/      # Repositórios Spring Data JPA
└── service/         # Lógica de negócio
```

### Decisões de design

- ✅ **Regra de status no domínio:** a transição é validada pelo enum `StatusTarefa` e aplicada pela entidade `Tarefa`, que não expõe setter de status
- ✅ **Exceções com status HTTP:** toda exceção de negócio estende `NegocioException`, então o handler global não precisa de um método novo a cada exceção
- ✅ **Fuso explícito:** datas calculadas em `America/Sao_Paulo`, com `Clock` injetado nos services
- ✅ **Mappers explícitos:** conversão entre objetos sem reflexão e sem configuração escondida

## 🚀 Instalação

### Pré-requisitos

- Java 17+
- Maven (ou o Maven Wrapper incluído no projeto)
- PostgreSQL

### 1. Clone o repositório

```bash
git clone https://github.com/Thyago1992/taskflow-api.git
cd taskflow-api
```

### 2. Configure o banco de dados

Crie o banco no PostgreSQL:

```sql
CREATE DATABASE taskflow_db;
```

### 3. Configure as variáveis de ambiente

| Variável      | Obrigatória | Padrão                                         |
|---------------|-------------|------------------------------------------------|
| `DB_PASSWORD` | Sim         | (nenhum)                                       |
| `DB_URL`      | Não         | `jdbc:postgresql://localhost:5432/taskflow_db` |
| `DB_USER`     | Não         | `postgres`                                     |

Linux/macOS:

```bash
export DB_PASSWORD=sua_senha
```

Windows (PowerShell):

```powershell
$env:DB_PASSWORD="sua_senha"
```

### 4. Compile e execute

```bash
mvn clean install
mvn spring-boot:run
```

Com o Maven Wrapper: `./mvnw spring-boot:run` (no Windows, `.\mvnw.cmd spring-boot:run`).

A aplicação estará disponível em: `http://localhost:8080`

Para exibir o SQL no console, ative o perfil de desenvolvimento:

```bash
export SPRING_PROFILES_ACTIVE=dev
```

## ⚙ Configuração

### application.properties

```properties
spring.application.name=taskflow-api

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/taskflow_db}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=false

spring.data.web.pageable.max-page-size=50
```

### application-dev.properties

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🔌 Endpoints da API

### Usuários

#### POST /usuarios
Cadastra um novo usuário.

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com"
}
```

**Response: 201 Created**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "dataCriacao": "2026-10-17T10:00:00"
}
```

#### GET /usuarios
Lista todos os usuários. **Response: 200 OK**

#### GET /usuarios/{id}
Retorna um usuário pelo ID. **Response: 200 OK**

#### PUT /usuarios/{id}
Atualiza os dados de um usuário. **Response: 200 OK**

#### DELETE /usuarios/{id}
Remove um usuário que não possui tarefas. **Response: 204 No Content**

---

### Tarefas

#### POST /tarefas
Cria uma nova tarefa. A tarefa sempre nasce com status `PENDENTE`, e o campo `status` enviado no corpo é ignorado.

**Request Body:**
```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar conceitos de JPA e REST",
  "status": "PENDENTE",
  "prioridade": "ALTA",
  "dataValidade": "2026-12-31",
  "idUsuario": 1
}
```

**Response: 201 Created**
```json
{
  "id": 1,
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar conceitos de JPA e REST",
  "status": "PENDENTE",
  "prioridade": "ALTA",
  "dataCriacao": "2026-10-17",
  "dataValidade":
