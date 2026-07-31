Here are the complete, detailed architectural descriptions and **SOLID-compliant** implementation guidelines for all 10 files in the `rakam-api` project. These descriptions are structured so that another LLM can accurately regenerate each file with precise technical specifications and strict architectural alignment.

---

### 1. `AdminHttpService.java`

* **File Purpose & Responsibility:** Acts as a restricted administrative controller routing API requests for cluster management, system configurations, and maintenance operations.
* **LLM Regeneration Specification:** Implement as a JAX-RS annotated service extending `HttpService`. It should require master-key authorization annotations and handle administrative endpoint mappings.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Handles administrative operations strictly separated from public event collection endpoints.
* *Interface Segregation Principle (ISP):* Exposes isolated administrative routes (`/admin/*`) keeping management concerns decoupled.



---

### 2. `CsvEventDeserializer.java`

* **File Purpose & Responsibility:** A Jackson JSON/data deserializer that converts incoming CSV text inputs into strongly typed domain `Event` objects.
* **LLM Regeneration Specification:** Implement by extending Jackson’s `StdDeserializer<Event>` or `JsonDeserializer<Event>`, parsing tabular comma-separated formats into properties and mapping metadata dynamically.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Concentrates solely on string-to-object parsing logic.
* *Open/Closed Principle (OCP):* Easily extensible for other text input parsing strategies without modifying core mapping classes.



---

### 3. `EventListDeserializer.java`

* **File Purpose & Responsibility:** A specialized Jackson deserializer designed to parse arrays or lists of event payloads into structured collections of domain `Event` objects.
* **LLM Regeneration Specification:** Implement using Jackson's deserialization context to iterate through JSON arrays and delegate individual item mapping to the base event deserializer.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Manages batch or container-level deserialization separately from single-event parsers.
* *Dependency Inversion Principle (DIP):* Relies on abstract Jackson parser interfaces rather than concrete format-specific bindings.



---

### 4. `HeaderDefaultFullHttpResponse.java`

* **File Purpose & Responsibility:** Extends Netty's HTTP response infrastructure to automatically inject default response headers (such as CORS, content type, and caching headers) into every outgoing `FullHttpResponse`.
* **LLM Regeneration Specification:** Subclass Netty's HTTP response container, overriding initialization behavior to append standard required headers before transmitting data down the pipeline.
* **SOLID Principles Guidance:**
* *Liskov Substitution Principle (LSP):* Seamlessly substitutes standard Netty response objects anywhere an HTTP response is expected without altering pipeline contracts.



---

### 5. `HttpServerConfig.java`

* **File Purpose & Responsibility:** Encapsulates the configuration metadata for the HTTP server, including port bindings, timeout limits, and worker thread pool sizes.
* **LLM Regeneration Specification:** Implement as a configuration properties class (utilizing configuration binding annotations like Airlift configuration annotations) with immutable fields and validation getters.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Exclusively holds data configuration properties, completely decoupled from socket opening or protocol logic.



---

### 6. `LogModule.java`

* **File Purpose & Responsibility:** A dependency injection module responsible for configuring, initializing, and binding logging systems across the application architecture.
* **LLM Regeneration Specification:** Implement as a Guice `Module` (or equivalent DI module) that binds logging services, metrics loggers, and database loggers to their respective interfaces.
* **SOLID Principles Guidance:**
* *Dependency Inversion Principle (DIP):* Binds concrete logging backends to abstract interfaces, preventing classes from depending directly on specific logging frameworks.



---

### 7. `SystemRegistry.java`

* **File Purpose & Responsibility:** Maintains a central runtime registry or lookup dictionary mapping active system plugins, modules, and component configurations.
* **LLM Regeneration Specification:** Implement as a thread-safe singleton or managed bean containing internal maps (`ConcurrentMap`) to register and retrieve system components dynamically.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Focuses exclusively on storing and retrieving component references, separate from how those components are built or initialized.



---

### 8. `SystemRegistryGenerator.java`

* **File Purpose & Responsibility:** Responsible for discovering, compiling, or building component metadata to automatically populate the `SystemRegistry` during startup phases.
* **LLM Regeneration Specification:** Implement a builder or scanner class that scans classpath annotations or module definitions and registers them into the `SystemRegistry`.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Handles component discovery and generation logic independently from the runtime lookup registry.



---

### 9. `WebHookHttpService.java`

* **File Purpose & Responsibility:** Manages webhook collection endpoints (`/event/hook/*`), executing sandboxed JavaScript logic via Nashorn engines (`Invocable`), caching compiled functions, storing configurations in a JDBC database (`DBI`), and writing error logs.
* **LLM Regeneration Specification:** Implement an `HttpService` subclass using Guava Caching for script compilation, Netty event executors for asynchronous script sandboxing, and JBDI queries for database persistence.
* **SOLID Principles Guidance:**
* *Single Responsibility Principle (SRP):* Encapsulates webhook lifecycle handling, script parsing, and sandboxed execution separate from general event handling.
* *Interface Segregation Principle (ISP):* Provides specific functional routes (`activate`, `delete`, `get`, `list`, `test`, `collect`) tailored to distinct webhook actions.



---

### 10. `WebServiceModule.java`

* **File Purpose & Responsibility:** Acts as the main dependency injection binder that hooks up web controllers, JSON object mappers, database pools, and HTTP routing components.
* **LLM Regeneration Specification:** Implement as a Guice configuration module that installs dependent sub-modules, provisions custom Jackson modules, and configures route bindings.
* **SOLID Principles Guidance:**
* *Dependency Inversion Principle (DIP):* Centralizes wiring of abstractions to concrete infrastructure components, ensuring high-level modules remain independent of low-level service implementations.
