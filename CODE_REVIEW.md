# Code Review — Project Task Management

## 🔍 Issue Breakdown

---

### [SEVERITY: Critical]

**Issue 1**

- **Location:** `ProjectDomainRepositoryImpl#save` -> solved
- **Violation Name:** Fragile Identity Mapping / Implicit Bulk Delete-and-Reinsert
- **Why It Matters:** Every call to `save()` on an existing project creates brand-new `TaskEntity` instances with a null JPA primary key, while the managed entity has tasks with real primary keys. Hibernate sees zero overlap, orphans the old tasks (deletes them), and cascade-persists the new ones (re-inserts them). In production, this means: auto-increment ID exhaustion, broken foreign-key references from any other table that points to tasks, unnecessary write amplification, and the impossibility of ever adding audit or event-sourcing because task identity is destroyed on every mutation. A single `addTask` call deletes all existing tasks and re-inserts them alongside the new one.
- **Socratic Hint:** The domain model carries a UUID (`id`), but the JPA entity carries two identities — a `Long id` (surrogate PK) and a `UUID domainId`. When the domain model is converted back to JPA entities, only one of those identities is populated. Trace the round-trip of a single Task object from DB → domain → back to JPA entity. At what point does the JPA primary key get lost? And if Hibernate cannot recognize a TaskEntity as "already persisted," what will it do with the entire collection?

---

**Issue 2**

- **Location:** `ProjectService#updateProject` -> solved
- **Violation Name:** Missing Uniqueness Validation on Update
- **Why It Matters:** `createProject` checks `projectRepository.existsByName()` to prevent duplicate names. `updateProject` does not. The database has a `unique` constraint on the `name` column, so a duplicate update will throw a generic `DataIntegrityViolationException` — an unhandled, implementation-leaking error that surfaces as a 500 Internal Server Error instead of a clear domain violation. This is a correctness gap that will surface the first time a user renames a project to a name that already exists.
- **Socratic Hint:** You correctly guard the uniqueness invariant on creation. What is architecturally different about the update path that would make the same invariant unenforceable? Should the domain model or the service own the responsibility of checking cross-aggregate uniqueness?

---

### [SEVERITY: High]

**Issue 3** -> solved

- **Location:** `ProjectService#updateTask`, `ProjectService#startTask`, `ProjectService#finishTask`
- **Violation Name:** Aggregate Root Encapsulation Bypass
- **Why It Matters:** An Aggregate Root is designed to be the strict consistency boundary for its children. By extracting a task from the project and invoking state-mutating methods directly on that task from the application service (e.g. `task.updateToDo(toDo)`), you treat the Aggregate Root as a passive data bag. This prevents the project from enforcing business rules across its tasks.
- **Socratic Hint:** If the `Project` is the Aggregate Root responsible for the lifecycle and consistency of its Tasks, who should be orchestrating the update operations on a Task? Should the service mutate the task directly, or should it ask the project to perform the mutation?

---

**Issue 4** -> solved

- **Location:** `ProjectService#updateProject`
- **Violation Name:** Unnecessary Domain Object Construction
- **Why It Matters:** The controller calls `DTOMapper.toProject(dto)` which constructs a full `Project` domain object with a freshly generated random UUID. The service receives this `Project` object but ignores everything except `project.getName()`. The newly generated UUID is thrown away. This is wasteful (UUID generation + object construction), confusing to future readers, and muddles the contract of what the service actually needs.
- **Socratic Hint:** Look at the parameters the service truly uses from the `Project` object it receives. Does it need an entire aggregate root, or does it need a simple value? What would the controller→service contract look like if it were written in terms of primitives?

---

**Issue 5** -> solved

- **Location:** `ProjectDomainRepositoryImpl#save`
- **Violation Name:** Explicit ID Management as a Substitute for Proper Merge Strategy
- **Why It Matters:** The manual `findByDomainId` lookup and ID-copying (lines 53–58) is a workaround that only solves the problem for the ProjectEntity root, not for the child TaskEntities. The comment explicitly states the intent ("trigger an UPDATE rather than an INSERT"), but it does not address the same problem for the aggregate's children. This is a half-solution that lulls the reader into thinking ID management is handled while the actual bug (Issue 1) silently persists.
- **Socratic Hint:** You identified the core problem — Hibernate needs a non-null `@Id` to recognize an existing entity. The `save()` method solves this for the root entity but not for its children. If the domain model does not carry the JPA surrogate ID, how can the mapper produce a faithful representation of the aggregate for Hibernate?

---

**Issue 6** -> Pragmatism. I won't implement 2 different DTOs for the service layer to be independent. If project was in a team or for production, would use it if there was possibility of moving to different framework. 

- **Location:** `ProjectController#findAllProjects` / `ProjectService#findAllProjects`
- **Violation Name:** Framework Type Leaking from Repository to Controller
- **Why It Matters:** The service returns `Slice<Project>`, which is a Spring Data interface. This means the service layer's contract is coupled to Spring Data's pagination abstraction. Any client (or test) of the service must depend on Spring Data to consume the result. In a strict layered architecture, the service should either return domain-level types or framework-agnostic pagination.
- **Socratic Hint:** If you were to swap Spring Data JPA for a different persistence mechanism (e.g., raw JDBC, MongoDB, or an in-memory test double), what would need to change in the service's method signatures?

---

**Issue 7** -> removed guard. cross-aggregate uniqueness is already checked. No need to send error message for a benign change. 

- **Location:** `Project#updateProjectName`
- **Violation Name:** Misleading Domain Invariant / Inconsistent Naming
- **Why It Matters:** The invariant `name.equals(this.name)` checks whether the new name is identical to the current name — a no-op guard. But the exception is named `NameAlreadyInUseException`. The name implies another project already owns this name. A developer reading the exception class will believe it signals a uniqueness conflict, when it actually signals an unnecessary update. This mismatch creates confusion about where uniqueness enforcement lives (domain vs. service) and leads to the bug in Issue 2.
- **Socratic Hint:** Does the domain model have the authority to know whether a name is "already in use"? What is the signal this exception class actually carries, and does the name truthfully describe that signal?

---

### [SEVERITY: Medium]

**Issue 8** -> solved

- **Location:** `Task#updatefinishDate`
- **Violation Name:** Inconsistent CamelCase Naming
- **Why It Matters:** The method name `updatefinishDate` breaks the camelCase convention (`finish` should be `Finish`). Paired with `updateToDo` (where the parameter is named `toDO`), these inconsistencies reduce readability and suggest the code was not reviewed for naming consistency. In a codebase meant to be professional, stylistic discipline communicates care.
- **Socratic Hint:** If a new team member searches for `updateFinishDate` and cannot find it, what does that tell you about the discoverability of this method?

---

**Issue 9** -> solved. Implemented UpdateProjectDTO and removed Validation group.

- **Location:** `ProjectDTO` reused for both create and update endpoints
- **Violation Name:** Blob DTO / Single Responsibility Violation
- **Why It Matters:** The same `ProjectDTO` record is used as the request body for `@PostMapping` (create) and `@PutMapping` (update). For the update endpoint, only `name` is consumed — `tasks` and `domainId` are annotated `READ_ONLY` and silently ignored. The controller accepts a structure that implies the client can modify tasks through this endpoint, but it cannot. This violates the Principle of Least Astonishment and makes the API surface misleading.
- **Socratic Hint:** If the update endpoint only needs a single field (`name`), what does the DTO's shape communicate to an API consumer? How would a dedicated DTO make the contract clearer?

---

**Issue 10** -> solved. Use Pathvariable for single resource. Query parameters for searching through a list. 

- **Location:** `ProjectController#findProjectByName`
- **Violation Name:** Ambiguous Request Mapping Path
- **Why It Matters:** The endpoint is mapped to `@GetMapping("name")` with a query parameter `name`. This produces `/api/v1/projects/name?name=xxx`. The path segment `name` is indistinguishable from a concrete identifier — if someone hits `/api/v1/projects/sebastion`, is that a path-variable ID lookup or a misspelled name lookup? While Spring correctly prioritizes literal paths over path variables, the ambiguity is a maintenance smell and an unconventional REST design.
- **Socratic Hint:** What is the conventional REST way to express a "search by field" operation? Should `name` live in the path or in the query?

---

### [SEVERITY: Low]

**Issue 11** -> not implemented. Rule of 3. Besides, this is a small codebase. 

- **Location:** `Project#checkNullity` and `Task#checkNullity`
- **Violation Name:** Duplicated Validation Logic
- **Why It Matters:** Both domain classes implement near-identical parameter-validation logic. The `Task` version uses varargs; the `Project` version checks a single object. The error messages are inconsistent. While minor, this duplication creates unnecessary surface area for bugs and violates DRY across the domain model.
- **Socratic Hint:** If the null/blank validation rule changes (e.g., new requirements about allowed characters), how many places must you modify?

---

## 📊 Final Evaluation

### Skill Level Assessment

**`[ ] Junior` `[ ] Junior-Mid` `[X] Mid` `[ ] Mid-Senior` `[ ] Senior-leaning`**

This is a **Mid**-level codebase. The developer demonstrates a strong conceptual understanding of DDD — they separated the domain model from JPA entities, placed business logic in entities rather than services, used value objects through records, and created a proper exception hierarchy. The overall structure shows they have studied DDD and layered architecture. However, the implementation reveals gaps in execution at the persistence boundary. The identity-mapping bug in `ProjectDomainRepositoryImpl.save()` is a fundamental architectural error that shows the developer understands *that* domain and persistence should be separate, but does not yet know how to *bridge* them safely. The manual ID management workaround, the missing update-uniqueness check, and the framework type leaking out of the service layer all indicate experience with Spring Boot's mechanics but incomplete mastery of the service/repository contract in a DDD-aligned system. The code would fail in production on the first task mutation — that places it squarely at Mid level: the vision is ambitious but the execution has critical blind spots.

### 🎯 Priority Deep Dive

**Aggregate Persistence Mapping patterns.**

This is the single highest-leverage concept for this codebase. You have correctly identified that the domain model should be persistence-ignorant and that JPA entities are implementation details of the repository. But the bridge between them — the `EntityMapper` + `ProjectDomainRepositoryImpl.save()` — is where the architecture breaks down. The fundamental problem is: how do you map changes on a domain aggregate to changes on a relational database, without leaking persistence concerns into the domain or losing identity?

Study the patterns for persisting domain aggregates with JPA. Specifically, examine approaches that load a managed JPA entity and apply domain-model changes to it *within the repository*, rather than tearing down and rebuilding the entity graph from scratch. Alternatives include using a double-dispatch pattern where the domain model emits events that the JPA entity consumes, or using the managed entity as a direct delegate of the domain object. The key question to ask yourself is: "If the domain model cannot carry JPA primary keys, and `EntityMapper.toProjectEntity()` creates entirely fresh entities every time, how can Hibernate differentiate between 'this is a new task to insert' and 'this is an existing task whose fields were updated'?"

Once you can answer that question, the half-solution in `ProjectDomainRepositoryImpl.save()` will collapse, and you will see the entire persistence layer in a new light.
