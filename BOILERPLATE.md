# Capo Finance - Complete Boilerplate

Este repositório contém um boilerplate completo seguindo as convenções do `.github/copilot-instructions.md`.

## 📦 O que foi criado

### Frontend (React + TypeScript + Vite + MobX + Tailwind)

#### Estrutura de pastas
```
frontend/src/
├── api/                    # API client com Axios
│   ├── apiClient.ts        # Configuração base do Axios
│   ├── accountsApi.ts      # API de contas
│   └── transactionsApi.ts  # API de transações
├── components/             # Componentes globais reutilizáveis
│   ├── Button.tsx
│   ├── Card.tsx
│   └── Input.tsx
├── scenes/                 # Páginas/views com lógica + UI separadas
│   └── Dashboard/
│       ├── index.ts        # Store MobX local + lógica
│       └── Dashboard.tsx   # Componente UI puro
├── store/                  # State management global (MobX)
│   ├── RootStore.ts        # Store raiz
│   └── StoreContext.tsx    # Context provider + hook useStore()
├── types/                  # TypeScript types
│   ├── Account.ts
│   └── Transaction.ts
└── utils/                  # Funções utilitárias
    └── formatters.ts       # Formatação de moeda e data
```

#### Bibliotecas instaladas
- ✅ React 18.2.0 (versão estável)
- ✅ TypeScript 5.2.2
- ✅ Vite 5.1.0
- ✅ MobX 6.12.0 + mobx-react-lite 4.0.5
- ✅ Axios 1.6.7
- ✅ Tailwind CSS 3.4.1
- ✅ React Router DOM 6.22.0

#### Configurações
- ✅ Path aliases configurados (`@/components`, `@/scenes`, etc.)
- ✅ Tailwind CSS integrado
- ✅ PostCSS configurado
- ✅ ESLint configurado

### Backend (Spring Boot + Java 17 + PostgreSQL + Flyway)

#### Estrutura de pastas (Clean Architecture)
```
backend/src/main/java/com/capofinance/
├── domain/                 # Entidades JPA
│   ├── AccountEntity.java
│   └── TransactionEntity.java
├── application/            # Services (lógica de negócio)
│   ├── AccountService.java
│   └── TransactionService.java
├── infrastructure/         # Repositories (persistência)
│   ├── AccountRepository.java
│   └── TransactionRepository.java
└── presentation/           # Controllers + DTOs (API layer)
    ├── AccountController.java
    ├── TransactionController.java
    └── dto/
        ├── AccountDTO.java
        ├── CreateAccountDTO.java
        ├── TransactionDTO.java
        └── CreateTransactionDTO.java
```

#### Features implementadas
- ✅ Entidades com Lombok (@Builder, @Getter, @Setter, etc.)
- ✅ Repositories JPA com Spring Data
- ✅ Services com @Transactional
- ✅ Controllers REST com validação (@Valid)
- ✅ DTOs separados para input/output
- ✅ CORS habilitado
- ✅ Conversão Entity ↔ DTO

#### Endpoints disponíveis

**Accounts:**
- `GET /api/accounts` - Listar todas as contas
- `GET /api/accounts/{id}` - Buscar conta por ID
- `POST /api/accounts` - Criar nova conta
- `PUT /api/accounts/{id}` - Atualizar conta
- `DELETE /api/accounts/{id}` - Deletar conta

**Transactions:**
- `GET /api/transactions` - Listar todas as transações
- `GET /api/transactions/{id}` - Buscar transação por ID
- `GET /api/transactions/account/{accountId}` - Listar transações de uma conta
- `POST /api/transactions` - Criar nova transação
- `PUT /api/transactions/{id}` - Atualizar transação
- `DELETE /api/transactions/{id}` - Deletar transação

## 🚀 Como executar

### 1. Frontend

```bash
cd frontend

# Instalar dependências (já instaladas)
npm install

# Criar arquivo .env
cp .env.example .env

# Iniciar servidor de desenvolvimento
npm run dev
```

O frontend estará rodando em `http://localhost:5173`

### 2. Backend

#### Opção 1: Com Docker Compose (recomendado)

```bash
# Na raiz do projeto
docker-compose up --build
```

#### Opção 2: Localmente

```bash
cd backend

# Rodar o backend
./mvnw spring-boot:run
```

O backend estará rodando em `http://localhost:8080`

### 3. Acessar aplicação

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html

## 📝 Convenções seguidas

### Frontend
- ✅ Scenes com `index.ts` (lógica) + `SceneName.tsx` (UI)
- ✅ MobX para state management
- ✅ Axios com interceptors configurados
- ✅ Tailwind para styling
- ✅ Path aliases absolutos
- ✅ Tipos TypeScript compartilhados

### Backend
- ✅ Clean Architecture (domain → application → infrastructure → presentation)
- ✅ Lombok para reduzir boilerplate
- ✅ Repositories JPA
- ✅ Services com @Transactional
- ✅ DTOs separados de Entities
- ✅ Validação com Bean Validation
- ✅ ResponseEntity com códigos HTTP apropriados

## 🎯 Próximos passos

1. **Frontend:**
   - Adicionar React Router para navegação entre páginas
   - Criar mais scenes (Transactions, Accounts, etc.)
   - Adicionar formulários de criação/edição
   - Implementar tratamento de erros global
   - Adicionar loading states e skeletons

2. **Backend:**
   - Implementar Exception Handlers globais
   - Adicionar paginação nos endpoints
   - Implementar autenticação (JWT)
   - Adicionar testes unitários e de integração
   - Configurar perfis (dev, prod)

3. **Geral:**
   - Configurar CI/CD
   - Adicionar Docker multi-stage builds
   - Documentar API com Swagger/OpenAPI
   - Adicionar logging estruturado

## 📚 Referências

- Copilot Instructions: `.github/copilot-instructions.md`
- Frontend README: `frontend/README.md`
- Backend HELP: `backend/HELP.md`

---

**Desenvolvido seguindo as convenções do Capo Finance**
