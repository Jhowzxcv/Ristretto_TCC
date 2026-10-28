# ☕ Ristretto — Backend API (TCC)

API RESTful desenvolvida em **Java 17** e **Spring Boot 3** como projeto de Conclusão de Curso (TCC). 

O **Ristretto** é um ecossistema de gestão de rotina, calendário e tarefas projetado especialmente para auxiliar indivíduos com **TDAH (Transtorno do Déficit de Atenção com Hiperatividade)** a organizarem suas atividades diárias, reduzindo sobrecarga cognitiva através da fragmentação de tarefas, lembretes estruturados e interfaces com foco.

---

## 🏛️ Arquitetura & Boas Práticas

O backend foi construído seguindo princípios de **Clean Architecture** e conceitos de **Domain-Driven Design (DDD)**:
- **Separação por Domínios:** Módulos independentes para cada entidade de negócio (`user`, `task`, `subTask`, `taskList`, `calendarEvent`, `category`, `goal`).
- **Data Transfer Objects (DTOs):** Isolamento estrito entre os modelos de persistência e a camada de transporte (`RegistrationData`, `DataDetails`, `DataListing`, `UpdateData`).
- **Autenticação Stateless:** Spring Security com tokens **JWT** (JSON Web Tokens via Auth0) e criptografia BCrypt.
- **Validações Customizadas:** Anotações e validadores personalizados para regras de calendário (`ValidRRule` segundo a RFC 5545, `ChronologicalDates`, `ConsistentRepetition`, `ValidTimezone`).
- **Tratamento Global de Erros:** `GlobalExceptionHandler` padronizando respostas de erro com códigos HTTP semânticos (400, 401, 403, 404, 500).
- **Documentação Interativa:** Interface OpenAPI 3.0 via **Swagger UI**.

---

## 🧩 Principais Módulos de Domínio

| Módulo | Descrição |
| :--- | :--- |
| **`User`** | Cadastro, autenticação, controle de acesso e personalização de tema visual. |
| **`Task` & `SubTask`** | Gestão de tarefas com prioridades (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), status e divisão em subtarefas para combater a procrastinação. |
| **`TaskList`** | Agrupamento de tarefas em listas com intervalos e regras de exibição. |
| **`CalendarEvent`** | Agendamento de eventos com suporte a fusos horários e regras de repetição periódica (*iCal4j / RFC 5545*). |
| **`Goal`** | Rastreamento e acompanhamento de metas de curto, médio e longo prazo. |
| **`Category`** | Rotulação e organização visual das atividades com codificação por cores. |

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.5+
- **Segurança:** Spring Security, Auth0 Java-JWT
- **Persistência & ORM:** Spring Data JPA, Hibernate
- **Banco de Dados & Migrations:** MySQL 8, Flyway
- **Validações:** Jakarta Bean Validation, validadores customizados
- **Performance & Cache:** Spring Cache, Ehcache
- **Padrões de Calendário:** iCal4j (RFC 5545)
- **Documentação:** SpringDoc OpenAPI / Swagger UI
- **Testes:** JUnit 5, Spring Security Test, Banco em memória H2

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 17** (JDK)
- **MySQL 8** rodando localmente ou via Docker
- **Maven** (ou utilizar o wrapper `./mvnw`)

### 1. Clonar o repositório
```bash
git clone https://github.com/Jhowzxcv/Ristretto_TCC.git
cd Ristretto_TCC/API
```

### 2. Configurar Variáveis de Ambiente
Configure as variáveis de ambiente necessárias ou preencha o `application.properties`:
```env
MYSQL_HOST=localhost
MYSQL_USER=seu_usuario
MYSQL_PASS=sua_senha
JWT_SECRET=sua_chave_secreta_jwt
```

### 3. Executar as Migrations e a Aplicação
```bash
./mvnw clean spring-boot:run
```

A API estará disponível em: `http://localhost:8080`  
Acesse o **Swagger UI** para testar os endpoints em: `http://localhost:8080/swagger-ui/index.html`
