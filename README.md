# SpringBoot CRUD com JWT, Roles, Docker e Testes

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![JWT](https://img.shields.io/badge/JWT-Enabled-yellow)

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

## Pré-requisitos

- **Java 21** ou superior
- **Maven 3.6+** 
- **Docker** e **Docker Compose** (opcional, para execução com containers)

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

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Resposta de sucesso (200):**
```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "role": "ROLE_ADMIN"
}
```

**Resposta de erro (401):**
```json
{
  "error": "Invalid email or password"
}
```

#### Registro
```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123"
}
```

### CRUD Usuários

- `GET /api/users` – listar todos usuários (protegido)
- `GET /api/users/{id}` – buscar usuário por ID (protegido)
- `POST /api/users` – criar usuário (protegido)
- `PUT /api/users/{id}` – atualizar usuário (protegido)
- `DELETE /api/users/{id}` – deletar usuário (protegido)

**Exemplo - Buscar usuário por ID:**
```http
GET /api/users/1
Authorization: Bearer <JWT_TOKEN>
```

**Exemplo - Criar usuário:**
```http
POST /api/users
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Maria Santos",
  "email": "maria@example.com",
  "password": "senha456",
  "role": "ROLE_USER"
}
```

### Endpoint Admin

```http
GET /api/admin/secret
Authorization: Bearer <JWT_TOKEN>
```

- Apenas usuários com **ROLE_ADMIN** podem acessar
- Usuários comuns recebem **403 Forbidden**

**Resposta:**
```json
{
  "secret": "only-admins-can-see-this"
}
```

## Códigos de Resposta

### Autenticação (/auth/login, /auth/register)
- `200` - Login/registro bem-sucedido
- `400` - Dados inválidos (email obrigatório, email já existe, password vazio)
- `401` - Credenciais incorretas
- `500` - Erro interno do servidor

### Endpoints Protegidos (/api/*)
- `200` - Sucesso
- `401` - Token inválido, ausente ou expirado
- `403` - Sem permissão (role insuficiente para acessar recurso)
- `404` - Recurso não encontrado

**Exemplos de respostas de erro:**
```json
{
  "error": "Email already exists"
}
```

```json
{
  "error": "Email is required"
}
```

```json
{
  "error": "User not found"
}
```

## Testes automatizados

Testes cobrem:

- Login válido / inválido
- Acesso admin vs usuário normal
- Proteção de rota com roles
- Validações de entrada

Rodar testes:

```bash
mvn test
```

## Swagger

Após rodar o app, acesse:

```
http://localhost:8081/swagger-ui.html
```

Para visualizar e testar endpoints interativamente.

## Docker / Variáveis de ambiente

- `JWT_SECRET` – segredo JWT (obrigatório em produção)
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
- Testa compilação com Java 21

## Solução de Problemas

### Erro de compilação Maven
```bash
mvn clean install -U
```

### Erro "Port 8081 already in use"
```bash
# Altere a porta no application.properties
server.port=8082
```

### Token expirado (401 Unauthorized)
- Tokens JWT expiram em **1 hora** por padrão
- Faça login novamente para obter novo token
- Configure tempo de expiração em `JwtUtil.java` se necessário

### @PreAuthorize não funciona
- Verifique se `@EnableMethodSecurity(prePostEnabled = true)` está no SecurityConfig
- Confirme que o token contém a role correta
- Verifique se o usuário tem a role necessária (`ROLE_ADMIN`)

### Banco H2 vs PostgreSQL
- **Desenvolvimento local:** Usa H2 em memória (arquivo `application.properties`)
- **Docker:** Usa PostgreSQL (configurado no `docker-compose.yml`)

### CORS errors
- Configure CORS no `SecurityConfig` se necessário para frontend
- Por padrão, permite requisições de qualquer origem

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   └── AdminController.java
│   │   ├── model/User.java
│   │   ├── repository/UserRepository.java
│   │   ├── security/
│   │   │   ├── JwtUtil.java
│   │   │   └── JwtFilter.java
│   │   └── service/
│   │       ├── DataLoader.java
│   │       └── UserDetailsServiceImpl.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/demo/
        └── AuthIntegrationTest.java
```

## Próximos passos

- [ ] Adicionar mais endpoints e microservices
- [ ] Integrar OpenFeign para APIs externas
- [ ] Implementar notificações em tempo real (WebSocket / SSE)
- [ ] Expandir testes de integração e unitários
- [ ] Adicionar rate limiting
- [ ] Implementar refresh tokens
- [ ] Adicionar logs estruturados
- [ ] Configurar profiles (dev, prod)
- [ ] Implementar cache (Redis)
- [ ] Adicionar monitoramento (Actuator)
