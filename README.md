# Todolist API

API REST para gerenciamento de tarefas desenvolvida em Java com Spring Boot. Esta aplicação permite criar, listar, atualizar e gerenciar tarefas com sistema de autenticação de usuários.

## 🚀 Como rodar o projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior
- Docker (opcional)

### Executando localmente

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd todolist
   ```

2. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

   A aplicação estará disponível em: `http://localhost:8080`

### Executando com Docker

1. **Construa a imagem**
   ```bash
   docker build -t todolist-api .
   ```

2. **Execute o container**
   ```bash
   docker run -p 8080:8080 todolist-api
   ```

## Tecnologias utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **Spring Security** - Autenticação e autorização
- **Lombok** - Redução de boilerplate
- **BCrypt** - Criptografia de senhas
- **SpringDoc OpenAPI** - Documentação da API (Swagger)
- **Jakarta Validation** - Validação de dados

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking de dependências
- **Spring Boot Test** - Testes de integração
- **@DataJpaTest** - Testes de repositório

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Docker** - Containerização

## 🧪 Como executar os testes

### Executar todos os testes
```bash
mvn test
```

### Executar testes específicos
```bash
# Testes de repositório
mvn test -Dtest=TaskRepositoryTest

# Testes de integração
mvn test -Dtest=*IntegrationTest
```

### Executar testes com relatório de cobertura
```bash
mvn test jacoco:report
```

## Documentação da API

Após iniciar a aplicação, a documentação Swagger estará disponível em:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## 🔧 Configuração

### Banco de dados
A aplicação utiliza H2 Database em memória por padrão. Para acessar o console do H2:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:todolist`
- **Username**: `admin`
- **Password**: `admin`

### Propriedades da aplicação
As configurações estão em `src/main/resources/application.properties`:
- Porta: 8080
- Banco: H2 em memória
- Logs: INFO

## Funcionalidades

### Usuários
- ✅ Cadastro de usuários
- ✅ Autenticação com JWT
- ✅ Validação de dados

### Tarefas
- ✅ CRUD completo de tarefas
- ✅ Filtros por nome, prioridade e situação
- ✅ Paginação e ordenação
- ✅ Validações de negócio
- ✅ Ações específicas (concluir/pendente)

### Validações
- ✅ Backend: Jakarta Validation
- ✅ Frontend: Validações de negócio
- ✅ Datas: Não permite datas passadas
- ✅ Campos obrigatórios

## Estrutura do projeto

```
src/
├── main/
│   ├── java/br/com/provaipog/todolist/
│   │   ├── config/          # Configurações
│   │   ├── errors/          # Tratamento de erros
│   │   ├── filter/          # Filtros de segurança
│   │   ├── task/            # Módulo de tarefas
│   │   ├── user/            # Módulo de usuários
│   │   └── utils/           # Utilitários
│   └── resources/
│       └── application.properties
└── test/
    └── java/br/com/provaipog/todolist/
        └── task/            # Testes de tarefas
```

## 🔐 Endpoints principais

### Autenticação
- `POST /users/` - Cadastrar usuário
- `POST /users/auth` - Autenticar usuário

### Tarefas
- `GET /tarefas/` - Listar tarefas (com filtros)
- `POST /tarefas/` - Criar tarefa
- `GET /tarefas/{id}` - Buscar tarefa por ID
- `PUT /tarefas/{id}` - Atualizar tarefa
- `DELETE /tarefas/{id}` - Excluir tarefa
- `PATCH /tarefas/{id}/concluir` - Marcar como concluída
- `PATCH /tarefas/{id}/pendente` - Marcar como pendente

## 🚨 Solução de problemas

### Porta 8080 em uso
```bash
# Encontrar processo usando a porta
netstat -ano | findstr :8080

# Encerrar processo (Windows)
taskkill /PID <PID> /F
```

### Erro de compilação
```bash
# Limpar e recompilar
mvn clean compile

# Reinstalar dependências
mvn clean install
```

## 📝 Notas de desenvolvimento

- A aplicação utiliza H2 em memória, então os dados são perdidos ao reiniciar
- Para produção, é necessário configurar um banco de dados persistente
- Os testes cobrem as funcionalidades principais de repositório
- A documentação Swagger é gerada automaticamente

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request