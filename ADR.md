# Architecture Decision Records

StockPulse is evaluated as a reactive commerce advisor: it observes inventory or demand signals, reasons about pricing and replenishment, queues suggestions, and waits for a merchandiser checkpoint. These decisions keep that loop explicit and protect the current sprint from unrelated storefront scope.

## ADR-001: Commerce Logic Boundary
Context:

Pricing, reorder quantity, AI calls, inventory events, and persistence have different reasons to change. Putting them in `ProductService` would make HTTP and async paths diverge and would make a future competitor-aware strategy difficult to add.

Options:

- Put rules on `Product`.
- Put all decisions in application services.
- Define a dedicated `CommerceStrategy` contract and use `CommerceAdvisor` as the runtime boundary.

Decision:

Use `CommerceContext` as structured input and `CommerceRecommendation` as the combined output. `RuleBasedCommerceStrategy` and `AiCommerceStrategy` implement the same contract. `ProductService` owns inventory mutations and `SuggestionService` owns suggestion persistence; neither owns pricing policy.

Tradeoffs:

The context and recommendation objects add a small amount of mapping code, but HTTP requests and async event handlers share one decision path. A future `CompetitorAwareStrategy` can implement the contract without changing controllers or listeners.

---

## ADR-002: Unified vs Split AI Calls
Context:

Every signal needs both a pricing recommendation and a reorder recommendation. The async loop must persist both or use deterministic fallback behavior if the LLM fails.

Options:

- Make separate pricing and reorder LLM calls.
- Make one structured call that returns both recommendations.

Decision:

Use one structured OpenAI-compatible call returning pricing and reorder fields together. The response is parsed into one `CommerceRecommendation`, validated as a whole, and rejected as a whole if any required field is invalid. The rule strategy then supplies both outputs as fallback.

Tradeoffs:

One call reduces latency, cost, and inconsistent reasoning between price and stock decisions. Separate calls would allow independent retries and smaller prompts, but could leave only half of a recommendation pair available and complicate idempotency.

---

## ADR-003: Runtime Strategy Switching
Context:

The active commerce strategy must change without a restart, and both synchronous manual requests and asynchronous events must use the same selection mechanism.

Options:

- Hard-code an `if` statement in each controller.
- Use a factory with a switch statement.
- Register named strategies in a map and select the active name through one advisor.

Decision:

`CommerceAdvisor` maintains a registry of `RULE` and `AI` strategies. The default comes from `COMMERCE_STRATEGY`/`commerce.strategy`, while `PATCH /commerce-strategy` changes the active strategy at runtime. Controllers and `InventoryEventListener` call only the advisor boundary.

Tradeoffs:

The current selector is process-local and resets on restart unless configured again. That is sufficient for the demo and avoids a settings table; a later deployment can back the selector with configuration or a database without changing callers.

---

## ADR-004: LLM Failure Handling
Context:

The recommendation path must not disappear because of a timeout, gateway failure, malformed JSON, missing fields, invalid confidence, or an absurd price. Credentials must never appear in logs or source control.

Options:

- Treat LLM errors as request failures.
- Retry indefinitely.
- Log a safe warning and use the deterministic rule strategy.

Decision:

`LiteLlmClient` reads `LLM_BASE_URL`, `LLM_API_KEY`, `LLM_MODEL`, `LLM_PRODUCT`, and `LLM_COOKIE` from environment-backed configuration. `AiResponseParser` requires the expected fields, `AiRecommendationValidator` enforces positive prices, a 3x price bound, positive quantities, confidence in 0..1, valid direction, and non-empty reasoning. `SuggestionService` catches `AiServiceException`, logs only the product and safe error message, and creates both rule-based suggestions.

Tradeoffs:

Fallback recommendations are less adaptive than AI output, but they preserve the human checkpoint and make the async loop reliable. We do not log prompts, API keys, cookies, or raw gateway responses.

---

## ADR-005: Agentic Loop Trigger and Decoupling
Context:

The system must react to state changes rather than poll for them. Stock updates and orders must return without waiting for AI reasoning. Low inventory and demand spike can happen together, and repeated signals must not flood the review queue.

Options:

- Scheduled polling.
- Synchronous recommendation generation inside the HTTP request.
- Publish application events and process them after commit with `@Async`.

Decision:

`ProductService` commits the inventory/order mutation and publishes `InventoryChangedEvent` or `DemandSpikeEvent`. Demand velocity is compared with the average of category peers, excluding the product being ordered. `InventoryEventListener` receives events with `@TransactionalEventListener(AFTER_COMMIT)` and delegates to `SuggestionService` on `commerceTaskExecutor`. The repository checks for an existing `PENDING` suggestion by product, trigger reason, and suggestion type before saving.

Tradeoffs:

The UI must poll or refresh because the result is intentionally eventual. After-commit async processing prevents suggestions for rolled-back inventory changes and keeps HTTP latency independent of the LLM. A durable queue would improve crash recovery in production, but Spring events are appropriate for this local sprint.

---

## ADR-006: Extensibility and Deliberate Exclusions
Context:

The scoring brief prioritizes the inventory-signal to approval loop. Competitor pricing, margin floors, supplier integrations, automated purchase orders, storefront checkout, authentication, and SSE are valuable but would dilute the current demo.

Options:

- Build those features now.
- Leave no extension boundary.
- Keep the current contracts focused and document the next insertion points.

Decision:

Defer unrelated commerce automation. `Product` can gain nullable `costPrice` and `marginFloor`; `CommerceStrategy` is the insertion point for `CompetitorAwareStrategy`; `ReorderSuggestion` can later carry supplier information; and the approval services are the checkpoint before future auto-apply or purchase-order workflows.

Tradeoffs:

The current application does not place real orders or publish storefront prices, which is intentional risk control. The tradeoff is less automation today in exchange for a testable foundation, explicit human approval, and a focused five-minute walkthrough.

---

## Walkthrough Script

1. Open the merchandising console and show the seeded Hoodie with stock `11` and threshold `12`.
2. Click **Simulate sale**. The order endpoint returns immediately; stock drops and the async loop creates `INVENTORY_LOW` pricing and reorder suggestions.
3. Open **Suggestions** and point out the trigger badge, confidence, reasoning, and pending-only review queue.
4. Accept the pricing suggestion. Return to **Products** and show the current price changed only after approval.
5. Accept the reorder suggestion and show inbound stock increasing. The product returns to `ACTIVE`.
6. Open **Decision history** to show accepted suggestions remain auditable while the pending badge decreases.
7. Optionally switch to `AI` through `PATCH /commerce-strategy`; with missing or failing gateway configuration, show that rule fallback still creates recommendations.
