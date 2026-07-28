# System Architecture

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Review Required |
| Last Updated | 2026-07-19 |

---

## Purpose

本文件定義 Nail SaaS 的整體系統架構、技術基線及分層設計原則。

本文件僅記錄已正式採用的系統架構與技術，不包含尚未完成評估或核准的方案。

所有架構異動皆應透過 ADR（Architecture Decision Record）進行管理。

---

## Architecture Goals

系統架構應符合以下目標：

- 採用清楚且一致的分層架構。
- 提高程式可維護性與可讀性。
- 降低模組間耦合度。
- 支援未來功能擴充。
- 維持資料一致性與系統穩定性。
- 避免未經評估的技術導入。

---

## High-Level Architecture

```mermaid
flowchart LR

Client[Web Client / App (TBD)]

Client --> API[Spring Boot REST API]

API --> Security[Spring Security + JWT]

API --> Service[Service Layer]

Service --> Repository[Spring Data JPA]

Repository --> Oracle[(Oracle / NSAS Schema)]

API --> Mail[SMTP Mail]
```

---

## Technology Baseline

目前正式採用之技術如下：

| Layer | Technology |
|------|------------|
| Backend | Java 17、Spring Boot 3.2.5 |
| API | Spring MVC REST |
| Security | Spring Security、JWT |
| Persistence | Spring Data JPA、Hibernate (`ddl-auto: none`) |
| Database | Oracle (`NSAS Schema`) |
| Mail | Spring Mail |

---

## Architecture Layers

| Layer | Responsibility |
|------|----------------|
| Controller | 接收 HTTP Request、參數驗證及回傳 Response。 |
| DTO | Request / Response 資料交換模型。 |
| Service | 商業邏輯、交易控制及跨模組協作。 |
| Repository | 資料查詢與持久化。 |
| Entity | 對應資料庫 DDL。 |
| Database | 儲存系統資料。 |

---

## Layering Principles

系統遵循標準分層架構，並遵守以下原則：

- Controller 不得實作商業邏輯。
- Service 為商業規則唯一實作位置。
- Repository 僅負責資料存取。
- Entity 應忠實對應資料庫 DDL。
- DTO 不得包含商業邏輯。
- Business Rule 不得散落於多個 Layer。

---

## Pending Technology Decisions

目前尚未正式採用以下技術：

- Redis
- Message Queue
- WebSocket
- Object Storage
- Firebase
- API Versioning Strategy
- Deployment Architecture

如需導入上述技術，必須完成以下流程：

1. 建立 Architecture Decision Record（ADR）。
2. 完成架構評估。
3. 更新相關文件。
4. 經確認後方可於程式中使用。

---

## Future Architecture

目前已規劃但尚未導入之擴充方向：

- Redis Cache
- Message Queue
- Object Storage
- Push Notification
- Horizontal Scaling
- API Gateway
- Container Deployment

以上內容僅代表未來可能發展方向，不代表已正式採用。

---

## Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案定位與產品目標。 |
| Module-Catalog.md | 功能模組總覽。 |
| Core-Flows.md | 使用者流程。 |
| Table-Catalog.md | 資料表總覽。 |
| API-Catalog.md | API 總覽。 |
| ADR | 架構決策紀錄。 |

---

## Notes

- 本文件描述系統整體架構。
- 詳細 API、資料模型、資料表及商業流程應記錄於各自對應文件。
- 新增架構元件、修改分層方式或導入新技術時，應同步更新本文件及相關 ADR。
- 程式實作不得作為架構依據，系統架構應以本文件及 ADR 為唯一依據。

---

## Package Structure

專案採用依職責劃分的 Package 結構，各 Package 應遵循單一職責原則（Single Responsibility Principle）。

```text
com.nailsaas
├─ common
├─ config
├─ controller
├─ dto
├─ entity
├─ exception
├─ repository
├─ security
├─ service
└─ util
```

### Package Responsibilities

| Package | Responsibility |
|---------|----------------|
| common | 共用常數、Enum、工具類別及基礎元件。 |
| config | Spring Boot 與第三方元件設定。 |
| controller | REST API Controller。 |
| dto | Request / Response DTO。 |
| entity | JPA Entity，對應資料庫 DDL。 |
| exception | 自訂 Exception 及全域例外處理。 |
| repository | Spring Data JPA Repository。 |
| security | Spring Security、JWT 及相關安全機制。 |
| service | 商業邏輯實作。 |
| util | 工具類別（Utility Classes）。 |

---

## Dependency Rules

各層之間應遵循以下依賴方向：

```text
Controller
      ↓
Service
      ↓
Repository
      ↓
Database
```

依賴規範：

- Controller 僅能呼叫 Service。
- Service 可依需求呼叫多個 Repository。
- Repository 不得呼叫 Service。
- Controller 不得直接存取 Repository。
- Repository 不得包含商業邏輯。
- DTO 僅作為資料交換模型，不得包含商業邏輯。
- Entity 應忠實對應資料庫 DDL，不得承載商業流程。