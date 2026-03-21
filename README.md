# 📋 TaskFlow API

API REST para gerenciamento de tarefas pessoais, desenvolvida com Spring Boot 4.0.3 e Java 17.

## 🎯 Sobre o Projeto

Sistema completo para gerenciamento de tarefas que permite:

- ✅ Cadastro e gerenciamento de usuários
- ✅ Criação de tarefas com prioridade e prazo
- ✅ Atualização parcial de status via PATCH
- ✅ Listagem de tarefas por usuário
- ✅ Validação de dados e regras de negócio
- ✅ Tratamento centralizado de erros com respostas padronizadas

## ✨ Funcionalidades

### Usuários

- ✅ Cadastro com nome e email
- ✅ Validação de email único no sistema
- ✅ Validação de formato de email
- ✅ Atualização de dados com verificação de conflito de email

### Tarefas

- ✅ Criação de tarefas vinculadas a um usuário
- ✅ Definição de prioridade: `BAIXA`, `MEDIA`, `ALTA`
- ✅ Definição de status: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`
- ✅ Atualização completa via PUT
- ✅ Atualização parcial de status via PATCH
- ✅ Listagem de todas as tarefas de um usuário
- ✅ Validação de usuário existente ao criar ou atualizar tarefa

## 🛠 Tecnologias Utilizadas

### Backend

- ✅ Java 17
- ✅ Spring Boot 4.0.3
- ✅ Spring Web
- ✅ Spring Data JPA
- ✅ Spring Validation
- ✅ Spring DevTools
- ✅ Hibernate (JPA Provider)
- ✅ Lombok — redução de boilerplate
- ✅ ModelMapper 3.2.0 — mapeamento de objetos

### Banco de Dados

- ✅ PostgreSQL (produção)
- ✅ H2 Database (em memória, disponível para testes)

### Documentação

- ✅ SpringDoc OpenAPI 3.0.2 (Swagger UI)

### Testes

- ✅ JUnit 5
- ✅ Mockito
- ✅ Spring Boot Test

### Build

- ✅ Maven

## 🏗 Arquitetura

O projeto segue uma arquitetura em camadas (Controller-Service-Repository):

```
com.thyago.taskflow_api
├── config/          # Configurações (ModelMapper)
├── controller/      # Endpoints REST
├── dto/             # Data Transfer Objects (Request e Response)
├── entity/          # Entidades JPA
├── enums/           # Enumerações (StatusTarefa, PrioridadeTarefa)
├── exception/       # Exceções customizadas e handler global
├── repository/      # Repositórios Spring Data JPA
└── service/         # Lógica de negócio
```

## 🚀 Instalação

### Pré-requisitos

- Java 17+
- Maven
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

Defina a variável de ambiente com a senha do banco:

```bash
export DB_PASSWORD=sua_senha
```

### 4. Compile e execute

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## ⚙ Configuração

### application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
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
  "dataCriacao": "2025-03-21T10:00:00"
}
```

#### GET /usuarios
Lista todos os usuários. **Response: 200 OK**

#### GET /usuarios/{id}
Retorna um usuário pelo ID. **Response: 200 OK**

#### PUT /usuarios/{id}
Atualiza os dados de um usuário. **Response: 200 OK**

#### DELETE /usuarios/{id}
Remove um usuário. **Response: 204 No Content**

---

### Tarefas

#### POST /tarefas
Cria uma nova tarefa.

**Request Body:**
```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar conceitos de JPA e REST",
  "status": "PENDENTE",
  "prioridade": "ALTA",
  "dataValidade": "2025-04-30",
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
  "dataCriacao": "2025-03-21",
  "dataValidade": "2025-04-30",
  "idUsuario": 1
}
```

#### GET /tarefas
Lista todas as tarefas. **Response: 200 OK**

#### GET /tarefas/{id}
Retorna uma tarefa pelo ID. **Response: 200 OK**

#### GET /tarefas/usuario/{idUsuario}
Lista todas as tarefas de um usuário específico. **Response: 200 OK**

#### PUT /tarefas/{id}
Atualiza todos os dados de uma tarefa. **Response: 200 OK**

#### PATCH /tarefas/{id}/status
Atualiza apenas o status de uma tarefa.

**Query param:** `?status=CONCLUIDA`

**Response: 200 OK**

#### DELETE /tarefas/{id}
Remove uma tarefa. **Response: 204 No Content**

## 🗄 Estrutura do Banco de Dados

### Tabela: usuarios

| Campo       | Tipo          | Restrições             |
|-------------|---------------|------------------------|
| id          | BIGINT        | PK, AUTO_INCREMENT     |
| nome        | VARCHAR       | NOT NULL               |
| email       | VARCHAR       | NOT NULL, UNIQUE       |
| data_criacao| TIMESTAMP     | NOT NULL               |

### Tabela: tarefas

| Campo          | Tipo    | Restrições                  |
|----------------|---------|-----------------------------|
| id             | BIGINT  | PK, AUTO_INCREMENT          |
| titulo         | VARCHAR | NOT NULL                    |
| descricao      | VARCHAR | NOT NULL                    |
| status         | VARCHAR | NOT NULL (enum)             |
| prioridade     | VARCHAR | NOT NULL (enum)             |
| data_criacao   | DATE    | NOT NULL                    |
| data_validade  | DATE    | NOT NULL                    |
| id_usuario     | BIGINT  | FK (usuarios.id), NOT NULL  |

### Relacionamentos

```
tarefas.id_usuario → usuarios.id (N:1)
```

## ✔ Validações e Regras de Negócio

### Usuários

- ✅ Nome é obrigatório
- ✅ Email é obrigatório e deve ter formato válido
- ✅ Email deve ser único no sistema
- ✅ Na atualização, verifica se o novo email pertence a outro usuário

### Tarefas

- ✅ Título é obrigatório
- ✅ Descrição é obrigatória
- ✅ Status é obrigatório (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`)
- ✅ Prioridade é obrigatória (`BAIXA`, `MEDIA`, `ALTA`)
- ✅ Data de validade é obrigatória
- ✅ Usuário vinculado deve existir no sistema
- ✅ Data de criação preenchida automaticamente via `@PrePersist`

## 🛡 Tratamento de Exceções

A API possui tratamento centralizado via `@RestControllerAdvice` com respostas padronizadas.

### Exceções customizadas

| Exceção                      | Status | Descrição                        |
|------------------------------|--------|----------------------------------|
| `ObjectNotFoundException`    | 404    | Recurso não encontrado           |
| `EmailAlreadyExistsException`| 409    | Email já cadastrado no sistema   |
| `MethodArgumentNotValidException` | 400 | Erro de validação de campos  |
| `Exception`                  | 500    | Erro interno do servidor         |

### Formato de resposta de erro

```json
{
  "status": 404,
  "mensagem": "Tarefa não encontrada",
  "timestamp": "2025-03-21T10:00:00"
}
```

## 📚 Documentação Swagger

A API possui documentação interativa via Swagger UI.

**Acesso:** `http://localhost:8080/swagger-ui/index.html`

A documentação inclui:

- Descrição de todos os endpoints
- Modelos de request e response
- Códigos de status HTTP
- Possibilidade de testar endpoints diretamente

## 💡 Exemplos de Uso

### 1. Cadastrar um usuário

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com"
  }'
```

### 2. Criar uma tarefa

```bash
curl -X POST http://localhost:8080/tarefas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Estudar Spring Boot",
    "descricao": "Revisar conceitos de JPA e REST",
    "status": "PENDENTE",
    "prioridade": "ALTA",
    "dataValidade": "2025-04-30",
    "idUsuario": 1
  }'
```

### 3. Listar tarefas de um usuário

```bash
curl -X GET http://localhost:8080/tarefas/usuario/1
```

### 4. Atualizar status de uma tarefa

```bash
curl -X PATCH "http://localhost:8080/tarefas/1/status?status=CONCLUIDA"
```

### 5. Deletar uma tarefa

```bash
curl -X DELETE http://localhost:8080/tarefas/1
```

## 🧪 Testes

O projeto possui testes unitários cobrindo todos os métodos dos services com os cenários de sucesso e de erro.

### Executar os testes

```bash
mvn test
```

### Cobertura

| Classe           | Cenários testados                                              |
|------------------|----------------------------------------------------------------|
| `TarefaService`  | findAll, findById, save, update, delete, findByUsuario, atualizarStatus |
| `UsuarioService` | findAll, findById, save, update (com e sem mudança de email), delete |

## 📝 Licença

Este projeto foi desenvolvido para fins educacionais.

## 👤 Autor

Desenvolvido por **Thyago Antonio Sampaio Valadares** como projeto de portfólio.

[![GitHub](https://img.shields.io/badge/GitHub-Thyago1992-181717?style=flat&logo=github)](https://github.com/Thyago1992)
