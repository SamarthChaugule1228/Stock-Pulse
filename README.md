# StockPulse

## 1. About StockPulse

StockPulse is an inventory intelligence and dynamic pricing application for merchandisers. It detects low inventory and demand signals, creates pricing and replenishment suggestions, and keeps a human approval checkpoint before applying changes.

The current application includes a Spring Boot backend, a React merchandising console, rule-based recommendations, optional AI recommendations, asynchronous event processing, and persistent local H2 storage.

## 2. Key Features

- Product catalog with stock, price, category, demand velocity, and lifecycle state.
- Low-inventory detection when stock falls below the reorder threshold.
- Demand-spike detection using a configurable category comparison multiplier.
- Rule-based pricing and reorder recommendations.
- Optional OpenAI-compatible LLM strategy with response validation and rule fallback.
- Runtime switching between `RULE` and `AI` commerce strategies.
- Asynchronous Spring event processing with `@Async` after inventory changes commit.
- Idempotency for pending suggestions by product, trigger, and suggestion type.
- Human approval for pricing and reorder recommendations.
- Persistent accepted prices and stock changes using file-backed H2.
- React dashboard with polling, pending-only review queue, decision history, stock controls, and sale simulation.

## 3. Tech Stack

### Backend

- Java 17+
- Spring Boot 3.3
- Spring Web and RestClient
- Spring Data JPA / Hibernate
- Bean Validation
- H2
- Jackson
- Spring Events and `@Async`
- Maven

### Frontend

- React 18
- Vite
- JavaScript
- CSS
- Browser Fetch API

## 4. Architecture

The frontend calls the REST API. Product and inventory services persist state and publish domain events. The commerce advisor selects the active strategy, while suggestion services persist recommendations and apply only approved decisions.

```mermaid
flowchart LR
    UI[React Merchandising Console] --> API[Spring Boot REST API]
    API --> PS[ProductService]
    API --> SS[SuggestionService]
    PS --> PR[(ProductRepository)]
    SS --> SR[(Suggestion Repositories)]
    PR --> DB[(File-backed H2)]
    SR --> DB
    SS --> CA[CommerceAdvisor]
    CA --> RB[RuleBasedCommerceStrategy]
    CA --> AI[AiCommerceStrategy]
    AI --> LLM[LiteLlmClient]
    LLM --> G[OpenAI-compatible LLM Gateway]
```

## 5. Agentic Flow

Inventory changes and orders publish events after the transaction commits. The asynchronous listener creates both suggestion types through the same commerce advisor. Recommendations remain pending until a merchandiser accepts or rejects them.

```mermaid
flowchart TD
    A[Stock update or simulated order] --> B[Persist inventory change]
    B --> C{Signal detected?}
    C -->|Stock below threshold| D[InventoryChangedEvent]
    C -->|Demand crosses multiplier| E[DemandSpikeEvent]
    C -->|No| F[Return HTTP response]
    D --> G[Async InventoryEventListener]
    E --> G
    G --> H[CommerceAdvisor]
    H --> I{Active strategy}
    I -->|RULE| J[Rule recommendation]
    I -->|AI| K[LLM recommendation]
    K --> L{Valid response?}
    L -->|No or failure| J
    L -->|Yes| M[Validated recommendation]
    J --> N[Pricing + reorder suggestions]
    M --> N
    N --> O[Persist PENDING suggestions]
    O --> P{Merchandiser decision}
    P -->|Accept pricing| Q[Update product price]
    P -->|Accept reorder| R[Increase product stock]
    P -->|Reject| S[Keep product state]
```

## 6. Project Structure

```text
stockpulse/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/stockpulse/
│       │   ├── ai/             # LLM client, prompts, parser, validation
│       │   ├── commerce/       # Advisor, strategies, context, output
│       │   ├── config/         # Async, CORS, web, LLM configuration
│       │   ├── controller/     # REST endpoints
│       │   ├── dto/            # Request and response contracts
│       │   ├── entity/         # JPA domain model
│       │   ├── enums/          # Categories and state enums
│       │   ├── event/          # Inventory events and async listener
│       │   ├── exception/      # Domain errors and REST handler
│       │   ├── repository/     # Spring Data repositories
│       │   ├── seed/           # Demo data initializer
│       │   └── service/        # Application services
│       └── main/resources/
│           ├── application.yml
│           └── data.sql
├── frontend/
│   └── src/
│       ├── api/                # Backend request modules
│       ├── components/         # Layout, product, suggestion, common UI
│       ├── constants/          # UI constants and demo data
│       ├── hooks/              # Shared StockPulse state and polling
│       ├── pages/              # Dashboard, Products, Suggestions
│       └── styles/             # Responsive dark dashboard styling
├── ADR.md
└── docker-compose.yml
```

## 7. API Endpoints

### Products

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/products` | Create a product. |
| `GET` | `/products?status=&category=` | List products with optional lifecycle and category filters. |
| `PATCH` | `/products/{id}/stock` | Set stock level and evaluate inventory signals. |
| `POST` | `/products/{id}/orders` | Simulate a sale and update stock and demand velocity. |
| `POST` | `/products/{id}/suggest-pricing` | Create a manual pricing suggestion. |
| `POST` | `/products/{id}/suggest-reorder` | Create a manual reorder suggestion. |

### Suggestions

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/pricing-suggestions` | List pricing suggestions, including decision history. |
| `PATCH` | `/pricing-suggestions/{id}` | Accept or reject a pricing suggestion. |
| `GET` | `/reorder-suggestions` | List reorder suggestions, including decision history. |
| `PATCH` | `/reorder-suggestions/{id}` | Accept or reject a reorder suggestion. |

### Runtime Strategy

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `PATCH` | `/commerce-strategy` | Switch the active strategy between `RULE` and `AI` without restarting. |

For local development, run the backend with `mvn spring-boot:run` from `backend/` and the frontend with `npm run dev` from `frontend/`. LLM credentials are supplied through environment variables and are not stored in this repository.
