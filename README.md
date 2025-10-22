# 🚀 Gerenciador de Quadros Kanban

## 📋 Descrição do Projeto

Sistema completo de gerenciamento de projetos utilizando metodologia Kanban, desenvolvido com arquitetura backend em **Java Spring Boot 3.5.6** e frontend moderno. O sistema implementa todas as regras de negócio especificadas no desafio técnico, com transições de status automatizadas, cálculos de métricas em tempo real e interface intuitiva.

![Kanban Dashboard](https://via.placeholder.com/800x400/4F46E5/FFFFFF?text=Kanban+Dashboard)
*Interface do sistema Kanban com visualização em colunas*

## ✨ Funcionalidades Implementadas

### ✅ Etapa 1 - CRUD Completo
- **Gestão de Projetos**: CRUD completo com cálculos automáticos de status, dias de atraso e percentual de tempo restante
- **Gestão de Responsáveis**: CRUD com validação de e-mail único
- **Cálculos Automáticos**:
  - Status do projeto (A_INICIAR, EM_ANDAMENTO, ATRASADO, CONCLUIDO)
  - Dias de atraso baseados em datas previstas vs realizadas
  - Percentual de tempo restante com fórmulas precisas
- **Auditoria**: createdAt e updatedAt automáticos
- **Validações**: Bean Validation com mensagens claras

### ✅ Etapa 2 - Quadro Kanban
- **Visualização por Status**: Listagem organizada por colunas Kanban
- **Transições de Status**: Sistema robusto seguindo regras de negócio complexas
- **Validações**: Mensagens de erro claras para transições inválidas
- **Ações Automáticas**: Definição automática de datas em transições específicas

### ✅ Etapa 3 - API REST Completa
- **Endpoints RESTful** seguindo melhores práticas
- **Documentação Swagger/OpenAPI** completa
- **Paginação** em listagens
- **Tratamento de erros** padronizado
- **CORS** configurado para integração com frontend

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21** 
- **Spring Boot 3.5.6** (Web, Data JPA, Validation, Test)
- **SpringDoc OpenAPI 2.8.13** (Documentação Swagger)
- **PostgreSQL** (Produção) / **H2** (Testes)
- **Flyway** (Migrações de banco)
- **Lombok** (Redução de boilerplate)
- **JUnit 5 + Mockito** (Testes unitários e de integração)
- **Testcontainers** (Testes de integração com banco real)
- **Maven** (Gerenciamento de dependências)

### Frontend
- **React.js 18** com TypeScript
- **Vite** para build e desenvolvimento
- **Tailwind CSS** para estilização
- **Axios** para requisições HTTP
- **React Beautiful DnD** para drag and drop

### Infraestrutura
- **Docker & Docker Compose**
- **Docker Multi-stage Build** para otimização
- **Health Checks** automáticos
- **Git** (Controle de versão)

## 🚀 Como Executar com Docker

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+

### Método 1: Execução Rápida (Compose Recomendado)
```bash
# Clone o repositório
git clone https://github.com/ucaxa/gerenciador-de-projetos
cd gerenciador-de-projetos

# Executar toda a stack
docker-compose up -d

# Verificar status dos serviços
docker-compose ps


gerenciador-de-projetos/
├── backend/
│   ├── src/
│   │   ├── main/java/com/facilite/backend/
│   │   │   ├── config/           # Configurações
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── service/          # Lógica de negócio
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── repository/      # Camada de dados
│   │   │   └── exception/       # Tratamento de erros
│   │   ├── test/java/           # Testes automatizados
│   │   └── main/resources/
│   │       ├── db/migration/    # Scripts do Flyway
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── pages/              # Páginas da aplicação
│   │   ├── services/           # API clients
│   │   └── types/              # Definições TypeScript
│   ├── public/
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
├── docker-compose.prod.yml
└── README.md


📡 API Endpoints
Projetos
Método	Endpoint	Descrição
GET	/api/projetos	Listar todos os projetos
GET	/api/projetos/{id}	Buscar projeto por ID
POST	/api/projetos	Criar novo projeto
PUT	/api/projetos/{id}	Atualizar projeto
DELETE	/api/projetos/{id}	Excluir projeto
GET	/api/projetos/status/{status}	Listar por status
PATCH	/api/projetos/{id}/status/{novoStatus}	Transicionar status
GET	/api/projetos/paginado	Listar com paginação


Responsáveis
Método	Endpoint	Descrição
GET	/api/responsaveis	Listar todos os responsáveis
GET	/api/responsaveis/{id}	Buscar responsável por ID
POST	/api/responsaveis	Criar novo responsável
PUT	/api/responsaveis/{id}	Atualizar responsável
DELETE	/api/responsaveis/{id}	Excluir responsável
GET	/api/responsaveis/paginado	Listar com paginação

🎯 Serviços Disponíveis
Serviço	URL	Descrição
Frontend	http://localhost:3000	Interface do usuário
Backend API	http://localhost:8080	API REST
Swagger UI	http://localhost:8080/swagger-ui.html	Documentação interativa
API Docs	http://localhost:8080/api-docs	Especificação OpenAPI
Health Check	http://localhost:8080/actuator/health	Status da aplicação
PostgreSQL	localhost:5432	Banco de dados

🔧 Configurações
Variáveis de Ambiente

# Backend
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/kanban
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_FLYWAY_ENABLED=true

# Frontend
VITE_API_URL=http://localhost:8080/api

Criar Projeto

curl -X POST http://localhost:8080/api/projetos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Sistema de Gestão",
    "inicioPrevisto": "2024-02-01",
    "terminoPrevisto": "2024-06-01",
    "responsavelIds": [1, 2]
  }'

Transicionar Status
bash
curl -X PATCH http://localhost:8080/api/projetos/1/status/EM_ANDAMENTO
Listar Kanban
bash
# Projetos em andamento
curl http://localhost:8080/api/projetos/status/EM_ANDAMENTO

# Projetos concluídos
curl http://localhost:8080/api/projetos/status/CONCLUIDO

🎨 Interface do Usuário
Telas Implementadas
Dashboard Kanban - Visualização em colunas com drag and drop

Lista de Projetos - Visão detalhada com filtros

Formulário de Projeto - Criação e edição

Gestão de Responsáveis - CRUD de membros da equipe

https://via.placeholder.com/800x400/10B981/FFFFFF?text=Lista+de+Projetos
Lista de projetos com métricas e status

👥 Autores
Reinaldo Silva - rein.ant@gmail.com


📞 Suporte
Documentação API: http://localhost:8080/swagger-ui.html

Repositório: https://github.com/ucaxa/gerenciador-de-projetos

Issues: https://github.com/ucaxa/gerenciador-de-projetos/issues

🔄 Status do Projeto
✅ BACKEND - Concluido com principais funcionalidades, mas com melhorias a serem implementadas 
🚀 FRONTEND - Concluido com principais funcionalidades, mas com melhorias a serem implementadas
📦 DOCKER - Completamente containerizado
🧪 TESTES - Cobertura >80%
📚 DOCUMENTAÇÃO - Completa








# Visualizar logs
docker-compose logs -f backend
