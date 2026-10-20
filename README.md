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
- ✅ Execução com Docker Compose (API + PostgreSQL)

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
- ✅ Data de validade não pode estar no passado

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

### Infraestrutura

- ✅ Docker e Docker Compose

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
- ✅ **Paginação padronizada:** as páginas são devolvidas em um DTO próprio (`PaginaResponseDTO`), sem expor a estrutura interna do Spring Data

## 🚀 Instalação

### Pré-requisitos

Escolha uma das opções:

- **Com Docker:** Docker e Docker Compose
- **Sem Docker:** Java 17+, Maven (ou o Maven Wrapper incluído no projeto) e PostgreSQL

### 1. Clone o repositório

```bash
git clone https://github.com/Thyago1992/taskflow-api.git
cd taskflow-api
```

### Opção A: com Docker (recomendado)

Copie o arquivo de exemplo e defina a senha do banco em `DB_PASSWORD`:

```bash
cp .env.example .env
```

No Windows (PowerShell): `copy .env.example .env`

Suba a API e o banco:

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- PostgreSQL exposto em `localhost:5433` (para não conflitar com um PostgreSQL local)

Para parar: `docker compose down`. Use `docker compose down -v` para apagar também os dados do banco.

### Opção B: sem Docker

#### 2. Configure o banco de dados

Crie o banco no PostgreSQL:

```sql
CREATE DATABASE taskflow_db;
```

#### 3. Configure as variáveis de ambiente

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

#### 4. Compile e execute

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

> Com Docker, o `docker-compose.yml` sobrescreve a conexão com o banco usando as variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`, então não é preciso alterar este arquivo.

### application-dev.properties

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🔌 Endpoints da API

### Resumo

| Método | Rota                          | Descrição                                      |
|--------|-------------------------------|------------------------------------------------|
| POST   | `/usuarios`                   | Cadastra um usuário                            |
| GET    | `/usuarios`                   | Lista todos os usuários                        |
| GET    | `/usuarios/{id}`              | Busca um usuário pelo ID                       |
| PUT    | `/usuarios/{id}`              | Atualiza um usuário                            |
| DELETE | `/usuarios/{id}`              | Remove um usuário sem tarefas                  |
| POST   | `/tarefas`                    | Cria uma tarefa                                |
| GET    | `/tarefas`                    | Lista tarefas (paginada, filtro por status)    |
| GET    | `/tarefas/{id}`               | Busca uma tarefa pelo ID                       |
| GET    | `/tarefas/usuario/{idUsuario}`| Lista as tarefas de um usuário                 |
| GET    | `/tarefas/atrasadas`          | Lista tarefas vencidas e ainda abertas         |
| PUT    | `/tarefas/{id}`               | Atualiza todos os dados de uma tarefa          |
| PATCH  | `/tarefas/{id}/status`        | Atualiza apenas o status de uma tarefa         |
| DELETE | `/tarefas/{id}`               | Remove uma tarefa                              |

### Usuários

#### POST /usuarios
Cadastra um novo usuário. O email é salvo em minúsculas e sem espaços.

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
  "dataCriacao": "2026-10-19T10:00:00"
}
```

**Erros:** `400` (dados inválidos), `409` (email já cadastrado).

#### GET /usuarios
Lista todos os usuários. **Response: 200 OK**

#### GET /usuarios/{id}
Retorna um usuário pelo ID. **Response: 200 OK**

**Erros:** `404` (usuário não encontrado).

#### PUT /usuarios/{id}
Atualiza os dados de um usuário. O corpo é o mesmo do `POST /usuarios`. **Response: 200 OK**

**Erros:** `400` (dados inválidos), `404` (usuário não encontrado), `409` (email já pertence a outro usuário).

#### DELETE /usuarios/{id}
Remove um usuário que não possui tarefas. **Response: 204 No Content**

**Erros:** `404` (usuário não encontrado), `409` (usuário possui tarefas).

---

### Tarefas

#### POST /tarefas
Cria uma nova tarefa. A tarefa sempre nasce com status `PENDENTE`: o campo `status` é obrigatório no corpo, mas seu valor é ignorado na criação.

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
  "dataCriacao": "2026-10-19",
  "dataValidade": "2026-12-31",
  "idUsuario": 1
}
```

**Erros:** `400` (dados inválidos ou data de validade no passado), `404` (usuário não encontrado).

#### GET /tarefas
Lista as tarefas de forma paginada, com filtro opcional por status.

**Parâmetros de consulta:**

| Parâmetro | Obrigatório | Padrão         | Descrição                                           |
|-----------|-------------|----------------|-----------------------------------------------------|
| `status`  | Não         | (todas)        | `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA` ou `CANCELADA` |
| `page`    | Não         | `0`            | Número da página (começa em 0)                      |
| `size`    | Não         | `10`           | Itens por página (máximo 50)                        |
| `sort`    | Não         | `dataValidade` | Campo e direção, ex.: `sort=prioridade,desc`        |

**Exemplo:** `GET /tarefas?status=PENDENTE&page=0&size=5&sort=dataValidade,asc`

**Response: 200 OK**
```json
{
  "conteudo": [
    {
      "id": 1,
      "titulo": "Estudar Spring Boot",
      "descricao": "Revisar conceitos de JPA e REST",
      "status": "PENDENTE",
      "prioridade": "ALTA",
      "dataCriacao": "2026-10-19",
      "dataValidade": "2026-12-31",
      "idUsuario": 1
    }
  ],
  "pagina": 0,
  "tamanho": 5,
  "totalElementos": 1,
  "totalPaginas": 1
}
```

**Erros:** `400` (status ou campo de ordenação inválido).

#### GET /tarefas/{id}
Retorna uma tarefa pelo ID. **Response: 200 OK**

**Erros:** `404` (tarefa não encontrada).

#### GET /tarefas/usuario/{idUsuario}
Lista todas as tarefas de um usuário específico, sem paginação. **Response: 200 OK**

**Erros:** `404` (usuário não encontrado).

#### GET /tarefas/atrasadas
Lista as tarefas com data de validade anterior a hoje que não estão `CONCLUIDA` nem `CANCELADA`, da mais atrasada para a menos atrasada. **Response: 200 OK**

#### PUT /tarefas/{id}
Atualiza todos os dados de uma tarefa. O corpo é o mesmo do `POST /tarefas`, e aqui o `status` enviado é aplicado, respeitando as regras de transição. A data de validade só é validada se for alterada, o que permite editar uma tarefa que já está atrasada. **Response: 200 OK**

**Erros:** `400` (dados inválidos ou nova data no passado), `404` (tarefa ou usuário não encontrado), `422` (transição de status inválida).

#### PATCH /tarefas/{id}/status
Atualiza apenas o status de uma tarefa.

**Query param:** `?status=CONCLUIDA`

Pedir o mesmo status que a tarefa já possui não altera nada e retorna `200`.

**Response: 200 OK**

**Erros:** `400` (status inválido), `404` (tarefa não encontrada), `422` (transição de status inválida).

#### DELETE /tarefas/{id}
Remove uma tarefa. **Response: 204 No Content**

**Erros:** `404` (tarefa não encontrada).

## 🔄 Regras de Transição de Status

| Status atual   | Pode ir para                                |
|----------------|---------------------------------------------|
| `PENDENTE`     | `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`    |
| `EM_ANDAMENTO` | `PENDENTE`, `CONCLUIDA`, `CANCELADA`        |
| `CONCLUIDA`    | nenhum (status final)                       |
| `CANCELADA`    | nenhum (status final)                       |

Uma transição não permitida retorna `422 Unprocessable Entity`.

## 🗄 Estrutura do Banco de Dados

As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`).

### Tabela: usuarios

| Campo         | Tipo      | Restrições          |
|---------------|-----------|---------------------|
| id            | BIGINT    | PK, AUTO_INCREMENT  |
| nome          | VARCHAR   | NOT NULL            |
| email         | VARCHAR   | NOT NULL, UNIQUE    |
| data_criacao  | TIMESTAMP | NOT NULL            |

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

- ✅ Nome é obrigatório (máximo de 100 caracteres)
- ✅ Email é obrigatório, deve ter formato válido (máximo de 255 caracteres)
- ✅ Email é normalizado (minúsculas e sem espaços) antes de salvar
- ✅ Email deve ser único no sistema
- ✅ Na atualização, verifica se o novo email pertence a outro usuário
- ✅ Não é possível excluir um usuário que possui tarefas

### Tarefas

- ✅ Título é obrigatório (máximo de 120 caracteres)
- ✅ Descrição é obrigatória (máximo de 255 caracteres)
- ✅ Status é obrigatório (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`)
- ✅ Prioridade é obrigatória (`BAIXA`, `MEDIA`, `ALTA`)
- ✅ Data de validade é obrigatória e não pode estar no passado
- ✅ Usuário vinculado deve existir no sistema
- ✅ Toda mudança de status passa pelas regras de transição
- ✅ Data de criação preenchida automaticamente via `@PrePersist`

## 🛡 Tratamento de Exceções

A API possui tratamento centralizado via `@RestControllerAdvice` com respostas padronizadas. As exceções de negócio estendem `NegocioException`, que carrega o status HTTP.

| Exceção                             | Status | Quando ocorre                                        |
|-------------------------------------|--------|------------------------------------------------------|
| `ObjectNotFoundException`           | 404    | Usuário ou tarefa não encontrado                     |
| `EmailAlreadyExistsException`       | 409    | Email já cadastrado                                  |
| `UsuarioPossuiTarefasException`     | 409    | Exclusão de usuário que possui tarefas               |
| `DataValidadeInvalidaException`     | 400    | Data de validade no passado                          |
| `TransicaoStatusInvalidaException`  | 422    | Mudança de status não permitida                      |
| `MethodArgumentNotValidException`   | 400    | Campos inválidos (mensagens separadas por `;`)       |
| `HttpMessageNotReadableException`   | 400    | Corpo ausente, JSON malformado ou enum inválido      |
| `MethodArgumentTypeMismatchException` | 400  | Parâmetro com valor inválido (ex.: `status=XYZ`)     |
| `PropertyReferenceException`        | 400    | Campo de ordenação inexistente                       |
| `Exception`                         | 500    | Erro interno do servidor                             |

### Formato de resposta de erro

```json
{
  "status": 404,
  "mensagem": "Tarefa não encontrada",
  "timestamp": "2026-10-19T10:00:00"
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

### Linux, macOS e Git Bash (curl)

#### 1. Cadastrar um usuário

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com"
  }'
```

#### 2. Criar uma tarefa

```bash
curl -X POST http://localhost:8080/tarefas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Estudar Spring Boot",
    "descricao": "Revisar conceitos de JPA e REST",
    "status": "PENDENTE",
    "prioridade": "ALTA",
    "dataValidade": "2026-12-31",
    "idUsuario": 1
  }'
```

#### 3. Listar tarefas pendentes, paginadas

```bash
curl "http://localhost:8080/tarefas?status=PENDENTE&page=0&size=5"
```

#### 4. Listar tarefas de um usuário

```bash
curl http://localhost:8080/tarefas/usuario/1
```

#### 5. Listar tarefas atrasadas

```bash
curl http://localhost:8080/tarefas/atrasadas
```

#### 6. Atualizar o status de uma tarefa

```bash
curl -X PATCH "http://localhost:8080/tarefas/1/status?status=CONCLUIDA"
```

#### 7. Deletar uma tarefa

```bash
curl -X DELETE http://localhost:8080/tarefas/1
```

### Windows (PowerShell)

No PowerShell, use `Invoke-RestMethod` com o JSON entre aspas simples:

```powershell
# Cadastrar um usuário
Invoke-RestMethod -Method Post -Uri http://localhost:8080/usuarios -ContentType "application/json" -Body '{"nome": "Joao Silva", "email": "joao.silva@email.com"}'

# Criar uma tarefa
Invoke-RestMethod -Method Post -Uri http://localhost:8080/tarefas -ContentType "application/json" -Body '{"titulo": "Estudar Spring Boot", "descricao": "Revisar conceitos de JPA e REST", "status": "PENDENTE", "prioridade": "ALTA", "dataValidade": "2026-12-31", "idUsuario": 1}'

# Listar tarefas pendentes, paginadas
Invoke-RestMethod "http://localhost:8080/tarefas?status=PENDENTE&page=0&size=5"

# Listar tarefas atrasadas
Invoke-RestMethod http://localhost:8080/tarefas/atrasadas

# Atualizar o status de uma tarefa
Invoke-RestMethod -Method Patch "http://localhost:8080/tarefas/1/status?status=CONCLUIDA"

# Deletar uma tarefa
Invoke-RestMethod -Method Delete http://localhost:8080/tarefas/1
```

## 🧪 Testes

O projeto possui testes unitários cobrindo os services, com cenários de sucesso e de erro, e as regras de transição do enum de status.

### Executar os testes

```bash
mvn test
```

### Cobertura

| Classe           | Cenários testados                                                                                                                                                  |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TarefaService`  | listagem paginada (com e sem filtro), findById, save (inclui usuário inexistente e data no passado), update (inclui tarefa atrasada sem mudar a data), delete, findByUsuario, findAtrasadas, atualizarStatus (inclui transição inválida) |
| `UsuarioService` | findAll, findById, save (inclui normalização e email duplicado), update (com e sem mudança de email, email de outro usuário), delete (inclui usuário com tarefas)    |
| `StatusTarefa`   | todas as transições permitidas e bloqueadas                                                                                                                        |

## 📝 Licença

Este projeto foi desenvolvido para fins educacionais.

## 👤 Autor

Desenvolvido por **Thyago Antonio Sampaio Valadares** como projeto de portfólio.

[![GitHub](https://img.shields.io/badge/GitHub-Thyago1992-181717?style=flat&logo=github)](https://github.com/Thyago1992)
