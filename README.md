
# SpringBoot CRUD com JWT, Roles, Docker e Testes

## Descrição
Projeto de exemplo de uma **API REST** em **Spring Boot** com:

- CRUD de usuários (`/api/users`)  
- Autenticação JWT (`/auth/login`)  
- Roles e autorização (`ROLE_ADMIN`, `ROLE_USER`)  
- Endpoint protegido de exemplo: `/api/admin/secret`  
- Integração com **PostgreSQL** via Docker  
- Testes automatizados (JUnit + Spring Boot Test + MockMvc)  
- Dockerfile e docker-compose configurados  
- CI com GitHub Actions (build Maven)

## Usuários Seed

O projeto cria dois usuários iniciais no banco:

| Email | Senha | Role |
|-------|-------|------|
| admin@example.com | admin123 | ROLE_ADMIN |
| user@example.com | user123 | ROLE_USER |

## Configuração JWT

O JWT utiliza **variável de ambiente** `JWT_SECRET` para gerar tokens seguros.  

Exemplo:

```bash
export JWT_SECRET="uma-chave-super-secreta-e-comprida-256-bit"
```

Se não estiver definida, será usado um fallback padrão (apenas para desenvolvimento).

## Rodando o projeto

### 1. Com Docker Compose (recomendado)

```bash
docker-compose up --build
```

- Serviço app: `http://localhost:8081`
- Banco PostgreSQL: porta `5432`
- Usuários seed já criados

### 2. Localmente sem Docker

```bash
mvn -B package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Não se esqueça de setar o `JWT_SECRET` antes de rodar:

```bash
export JWT_SECRET="uma-chave-super-secreta-e-comprida-256-bit"
```

## Endpoints principais

### Autenticação

```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

Resposta:

```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "role": "ROLE_ADMIN"
}
```

### CRUD Usuários

- `GET /api/users` – listar usuários (protegido)
- `GET /api/users/{id}` – buscar usuário (protegido)
- `POST /api/users` – criar usuário (protegido)
- `PUT /api/users/{id}` – atualizar usuário (protegido)
- `DELETE /api/users/{id}` – deletar usuário (protegido)

### Endpoint Admin

```http
GET /api/admin/secret
Authorization: Bearer <JWT_TOKEN>
```

- Apenas usuários com **ROLE_ADMIN** podem acessar
- Usuários comuns recebem **403 Forbidden**

## Testes automatizados

Testes cobrem:

- Login válido / inválido
- Acesso admin vs usuário normal
- Proteção de rota com roles

Rodar testes:

```bash
mvn test
```

## Swagger

Após rodar o app, acesse:

```
http://localhost:8081/swagger-ui.html
```

Para visualizar e testar endpoints.

## Docker / Variáveis de ambiente

- `JWT_SECRET` – segredo JWT
- `SPRING_DATASOURCE_URL` – URL do banco
- `SPRING_DATASOURCE_USERNAME` – usuário DB
- `SPRING_DATASOURCE_PASSWORD` – senha DB

Docker-compose já configura:

```yaml
db:
  image: postgres:15
  environment:
    POSTGRES_USER: demo
    POSTGRES_PASSWORD: demo123
    POSTGRES_DB: demo

app:
  environment:
    JWT_SECRET: super-secret-change-me
    SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/demo
    SPRING_DATASOURCE_USERNAME: demo
    SPRING_DATASOURCE_PASSWORD: demo123
```

## GitHub Actions

- Build Maven automatizado
- Dispara em `push` e `pull_request` na branch `main`

## Próximos passos

- Adicionar mais endpoints e microservices
- Integrar OpenFeign para APIs externas
- Implementar notificações em tempo real (WebSocket / SSE)
- Expandir testes de integração e unitários
