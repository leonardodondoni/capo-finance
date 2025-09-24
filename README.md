# Capo Finance 💰

Um projeto pessoal de controle financeiro para rastrear, categorizar e analisar gastos, utilizando extratos bancários e faturas de cartão de crédito em CSV. Desenvolvido para uso local no macOS e como showcase no GitHub e LinkedIn.

## 🚀 Tecnologias Utilizadas

*   **Backend:** Java 17 com Spring Boot
*   **Frontend:** React com TypeScript (utilizando Lovable para UI/UX)
*   **Banco de Dados:** PostgreSQL
*   **Infraestrutura:** Docker e Docker Compose

## ✨ Features Planejadas (MVP)

*   Importação de extratos bancários e faturas de cartão de crédito (CSV)
*   Visualização de transações financeiras
*   Categorização manual e automática de gastos
*   Dashboard com resumos e gráficos de despesas

## 🏗️ Estrutura do Projeto

O projeto é organizado em um monorepo simplificado para facilitar o desenvolvimento e a orquestração via Docker Compose:

```
capo-finance/
├── backend/            # Aplicação Spring Boot (Java)
├── frontend/           # Aplicação React (TypeScript)
├── docker-compose.yml  # Orquestração Docker para serviços
├── .env                # Variáveis de ambiente para Docker Compose
├── README.md           # Este arquivo
└── ...
```

## 🛠️ Como Rodar Localmente (Desenvolvimento)

Para iniciar o ambiente de desenvolvimento, você precisará ter Docker, Docker Compose, Java 17, Maven (ou o wrapper Maven já incluído), Node.js e NPM/Yarn instalados em seu macOS.

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/leonardodondoni/capo-finance.git
    cd capo-finance
    ```

2.  **Configurar variáveis de ambiente:**
    Certifique-se de que o arquivo `.env` existe na raiz do projeto (ele foi criado automaticamente pelo script de setup):
    ```ini
    # Database Credentials
    DB_NAME=capofinance
    DB_USER=capofinance
    DB_PASSWORD=capofinancePass
    ```

3.  **Iniciar o Banco de Dados (PostgreSQL) com Docker Compose:**
    Isso subirá o container do PostgreSQL em `localhost:5432` e criará um volume persistente para os dados.
    ```bash
    docker-compose up -d capo-finance-db
    ```
    Aguarde alguns segundos para que o banco de dados esteja totalmente inicializado.

4.  **Rodar o Backend (Spring Boot):**
    Navegue até a pasta `backend` e use o wrapper Maven para rodar a aplicação Spring Boot.
    ```bash
    cd backend
    ./mvnw spring-boot:run
    ```
    O backend estará disponível em `http://localhost:8080`. Você verá logs no terminal indicando o status.

5.  **Rodar o Frontend (React):**
    Em um *novo terminal*, navegue até a pasta `frontend` e instale as dependências e inicie o servidor de desenvolvimento Vite.
    ```bash
    cd frontend
    npm install # Ou 'yarn install' se preferir Yarn
    npm run dev # Ou 'yarn dev'
    ```
    O frontend estará disponível em `http://localhost:5173` (ou a porta que o Vite indicar no terminal).

## 🐳 Como Rodar com Docker Compose (Backend e DB em containers)

Se você preferir rodar o backend também em um container Docker:

1.  **Buildar as imagens e iniciar os serviços:**
    ```bash
    docker-compose up --build -d
    ```
    Isso criará a imagem Docker para o backend, iniciará os containers do backend e do PostgreSQL.
    O backend estará acessível em `http://localhost:8080`.

## 📚 Documentação da API (Swagger/OpenAPI)

Uma vez que o backend estiver rodando (seja via `mvnw` ou Docker), você poderá acessar a documentação interativa da API em:
*   `http://localhost:8080/swagger-ui.html`
*   `http://localhost:8080/v3/api-docs` (arquivo JSON)

## 🤝 Contribuição

Contribuições são bem-vindas! Se tiver ideias ou encontrar problemas, por favor, abra uma [issue](https://github.com/leonardodondoni/capo-finance/issues) ou um [pull request](https://github.com/leonardodondoni/capo-finance/pulls).

## 📄 Licença

Este projeto está sob a [MIT License](LICENSE).

---
Criado com 💖 por [Leonardo Dondoni](https://www.linkedin.com/in/leonardodondoni/)
