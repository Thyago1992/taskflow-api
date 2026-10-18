# TaskFlow API

API REST para gerenciamento de tarefas pessoais, desenvolvida com Spring Boot 4.0.3 e Java 17.

## Sobre o Projeto

Sistema para gerenciamento de tarefas que permite:

- Cadastro e gerenciamento de usuários
- Criação de tarefas com prioridade e prazo
- Controle de status com regras de transição
- Listagem paginada e filtrada por status
- Consulta de tarefas atrasadas
- Validação de dados e regras de negócio
- Tratamento centralizado de erros com respostas padronizadas

## Funcionalidades

### Usuários

- Cadastro com nome e email
- Email normalizado (minúsculas, sem espaços) e único no sistema
- Atualização com verificação de conflito de email
- Exclusão bloqueada enquanto o usuário tiver tarefas

### Tarefas

- Criação de tarefas vinculadas a um usuário (sempre nascem `PENDENTE`)
- Prioridade: `BAIXA`, `MEDIA`, `ALTA`
- Status: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`
- Atualização completa via PUT e atualização de status via PATCH
- Listagem paginada com filtro opcional por status
- Listagem de tarefas por usuário
- Listagem de tarefas atrasadas (vencidas e ainda abertas)

## Tecnologias

- Java 17
- Spring Boot 4.0.3 (Web, Data JPA, Validation)
- Hibernate
- PostgreSQL (execução) e H2 em memória (testes)
- Lombok
- SpringDoc OpenAPI 3.0.2 (Swagger UI)
- JUnit 5 e Mockito
- Maven

## Arquitetura

Arquitetura em camadas (Controller, Service, Repository), com mapeamento explícito entre entidades e DTOs:

```
com.thyago.taskflow_api
├── config/          # Fuso horário padrão e Clock
├── controller/      # Endpoints REST
├── dto/             # Requests e responses (records)
├── entity/          # Entidades JPA
├── enums/           # StatusTarefa (com regra de transição) e PrioridadeTarefa
├── exception/       # Exceções de negócio e handler global
├── mapper/          # Conversão entre entidades e DTOs
├── repository/      # Repositórios Spring Data JPA
└── service/         # Regras de negócio
```

Decisões de design:

- **Regra de status no domínio:** a transição é validada pelo enum `StatusTarefa` e aplicada pela entidade `Tarefa`, que não expõe setter de status.
- **Exceções com status HTTP:** toda exceção de negócio estende `NegocioException`, então o handler global não precisa de um método novo a cada exceção.
- **Fuso explícito:** datas são calculadas em `America/Sao_Paulo`, via `Clock` injetado nos services.
- **Mappers explícitos:** sem reflexão nem configuração mágica na conversão de objetos.

## Instalação

### Pré-requisitos

- Java 17+
- PostgreSQL

### 1. Clone o repositório

```
git clone https://github.com/Thyago1992/taskflow-api.git
cd taskflow-api
```

### 2. Crie o banco

```sql
CREATE DATABASE taskflow_db;
```

### 3. Defina as variáveis de ambiente

| Variável | Obrigatória | Padrão |
| --- | --- | --- |
| `DB_PASSWORD` | Sim | (nenhum) |
| `DB_URL` | Não | `jdbc:postgresql://localhost:5432/taskflow_db` |
| `DB_USER` | Não | `postgres` |

Linux/macOS:

```
export DB_PASSWORD=sua_senha
```

Windows (PowerShell):

```
$env:DB_PASSWORD="sua_senha"
```

### 4. Execute

```
./mvnw spring-boot:run
```

No Windows, use `.\mvnw.cmd spring-boot:run`. A aplicação sobe em `http://localhost:8080`.

Para ver o SQL no console, ative o perfil de desenvolvimento:

```
export SPRING_PROFILES_ACTIVE=dev
```

## Endpoints

### Usuários

| Método | Rota | Descrição | Sucesso |
| --- | --- | --- | --- |
| POST | `/usuarios` | Cadastra um usuário | 201 |
| GET | `/usuarios` | Lista todos os usuários | 200 |
| GET | `/usuarios/{id}` | Busca um usuário | 200 |
| PUT | `/usuarios/{id}` | Atualiza um usuário | 200 |
| DELETE | `/usuarios/{id}` | Remove um usuário (sem tarefas) | 204 |

Exemplo de cadastro:

```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com"
}
```

### Tarefas

| Método | Rota | Descrição | Sucesso |
| --- | --- | --- | --- |
| POST | `/tarefas` | Cria uma tarefa (sempre `PENDENTE`) | 201 |
| GET | `/tarefas` | Lista paginada, com filtro por status | 200 |
| GET | `/tarefas/atrasadas` | Tarefas vencidas e ainda abertas | 200 |
| GET | `/tarefas/{id}` | Busca uma tarefa | 200 |
| GET | `/tarefas/usuario/{idUsuario}` | Tarefas de um usuário | 200 |
| PUT | `/tarefas/{id}` | Atualiza todos os dados | 200 |
| PATCH | `/tarefas/{id}/status?status=CONCLUIDA` | Atualiza só o status | 200 |
| DELETE | `/tarefas/{id}` | Remove uma tarefa | 204 |

Exemplo de criação (o campo `status` enviado na criação é ignorado):

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

### Paginação e filtro

`GET /tarefas` aceita os parâmetros:

| Parâmetro | Descrição | Padrão |
| --- | --- | --- |
| `status` | Filtra por status | todos |
| `page` | Número da página (começa em 0) | 0 |
| `size` | Itens por página (máximo 50) | 10 |
| `sort` | Campo e direção, ex.: `titulo,asc` | `dataValidade` |

Formato da resposta:

```json
{
  "conteudo": [],
  "pagina": 0,
  "tamanho": 10,
  "totalElementos": 25,
  "totalPaginas": 3
}
```

## Regras de Negócio

### Status das tarefas

Toda tarefa nasce `PENDENTE`. Transições permitidas:

| De | Para |
| --- | --- |
| `PENDENTE` | `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA` |
| `EM_ANDAMENTO` | `PENDENTE`, `CONCLUIDA`, `CANCELADA` |
| `CONCLUIDA` | nenhuma |
| `CANCELADA` | nenhuma |

Transição inválida retorna **422**. A regra vale para o PATCH e para o PUT.

### Validações

- Título: obrigatório, até 120 caracteres
- Descrição: obrigatória, até 255 caracteres
- Nome do usuário: obrigatório, até 100 caracteres
- Email: obrigatório, formato válido, até 255 caracteres, único
- Data de validade: obrigatória e não pode estar no passado (no PUT, só é validada se for alterada, o que permite editar tarefas já atrasadas)
- O usuário vinculado à tarefa deve existir

## Tratamento de Erros

Respostas padronizadas:

```json
{
  "status": 404,
  "mensagem": "Tarefa não encontrada",
  "timestamp": "2026-10-17T10:00:00"
}
```

| Situação | Status |
| --- | --- |
| Campos inválidos (todas as mensagens, separadas por `;`) | 400 |
| JSON malformado ou enum inexistente no corpo | 400 |
| Parâmetro inválido (ex.: `status=XYZ`) ou campo de ordenação inexistente | 400 |
| Data de validade no passado | 400 |
| Usuário ou tarefa não encontrados | 404 |
| Email já cadastrado | 409 |
| Exclusão de usuário que possui tarefas | 409 |
| Transição de status inválida | 422 |
| Erro inesperado | 500 |

## Documentação Swagger

Com a aplicação rodando: `http://localhost:8080/swagger-ui/index.html`

## Testes

```
./mvnw test
```

- Testes unitários dos services (cenários de sucesso e de erro, regras de status e de data)
- Teste da regra de transição do enum `StatusTarefa`
- Teste de contexto da aplicação, que usa H2 em memória e não exige PostgreSQL

## Licença

Projeto desenvolvido para fins educacionais.

## Autor

Desenvolvido por **Thyago Antonio Sampaio Valadares** como projeto de portfólio.

[![GitHub](https://img.shields.io/badge/GitHub-Thyago1992-181717?style=flat&logo=github)](https://github.com/Thyago1992)
