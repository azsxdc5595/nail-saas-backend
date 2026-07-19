# System Architecture

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Review Required |
| Last Updated | 2026-07-19 |

---

# Purpose

本文件描述 Nail SaaS 的整體系統架構、技術基線及分層設計原則。

本文件僅描述已確認採用的架構與技術，不代表未來規劃。

尚未經過 ADR 確認的技術，不得視為專案正式架構。

---

# Architecture Goals

系統架構應符合以下目標：

- 保持清楚的分層架構。
- 提供良好的可維護性。
- 保持模組低耦合。
- 支援未來功能擴充。
- 維持資料一致性與系統穩定性。
- 避免未經確認的技術引入。

---

# High-Level Architecture

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

# Technology Baseline

目前已確認採用之技術如下。

| Layer | Technology |
|------|------------|
| Backend | Java 17、Spring Boot 3.2.5 |
| API | Spring MVC REST |
| Security | Spring Security、JWT |
| Persistence | Spring Data JPA、Hibernate (`ddl-auto: none`) |
| Database | Oracle (`NSAS` Schema) |
| Mail | Spring Mail |

---

# Architecture Layers

| Layer | Responsibility |
|------|----------------|
| Controller | 接收 HTTP Request、參數驗證及回傳 Response。 |
| Service | 商業流程、交易控制及跨模組協作。 |
| Repository | 資料查詢及持久化。 |
| Entity | 對應資料庫 DDL。 |
| Database | 儲存系統資料。 |

---

# Layering Principles

系統遵循分層架構。

- Controller 不得承載商業規則。
- Service 為商業邏輯唯一實作位置。
- Repository 僅負責資料存取。
- Entity 應忠實對應 DDL。
- Business Rule 不得分散於多個 Layer。

---

# Technology Decisions

目前尚未確認採用下列技術：

- Redis
- Message Queue
- WebSocket
- Object Storage
- Firebase
- MySQL
- API Versioning Strategy
- Deployment Architecture

上述技術如需導入，必須：

1. 建立 Architecture Decision Record（ADR）。
2. 完成架構評估。
3. 更新相關文件。

不得直接於程式中引入。

---

# Future Architecture

目前保留以下擴充方向：

- Redis Cache
- Message Queue
- Object Storage
- Push Notification
- Horizontal Scaling
- API Gateway
- Container Deployment

以上僅代表可能發展方向，不代表已決定採用。

---

# Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案定位與產品目標。 |
| Module-Catalog.md | 功能模組總覽。 |
| Core-Flows.md | 使用者流程。 |
| Table-Catalog.md | 資料表總覽。 |
| API-Catalog.md | API 總覽。 |
| ADR | 架構決策紀錄。 |

---

# Notes

本文件描述系統整體架構。

詳細 API、資料模型、資料表及商業流程，應分別記錄於對應文件。

新增架構元件、修改分層方式或導入新技術時，應同步更新本文件及相關 ADR。

程式實作不得作為架構依據，架構應以本文件及 ADR 為準。