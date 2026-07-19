# Module Catalog

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Draft |
| Last Updated | 2026-07-19 |

---

# Purpose

本文件列出 Nail SaaS 所有功能模組、目前開發狀態及相關文件覆蓋情形。

本文件作為功能模組索引，不描述詳細需求、流程或技術設計。

各模組的詳細內容應分別記錄於 Product、User Flow、Database、API 及 ADR 文件。

---

# Status Definition

| Status | Description |
|---------|-------------|
| Planned | 已規劃，尚未開始設計。 |
| In Design | 已開始產品或架構設計。 |
| Review Required | 由既有程式盤點而來，尚待需求確認。 |
| In Implementation | 正在開發中。 |
| Partially Implemented | 已完成部分功能。 |
| Implemented | 功能已完成且文件同步。 |

---

# Module Overview

| Module | Status | Description | Primary Tables | Missing Documentation |
|---------|--------|-------------|----------------|-----------------------|
| Authentication | Review Required | 註冊、登入、信箱驗證、Refresh Token、忘記密碼。 | USER_ACCOUNT<br>EMAIL_VERIFICATION<br>REFRESH_TOKEN | Product Requirement、DDL、API、User Flow |
| Shop Management | Review Required | 建立店家、邀請碼、加入店家及店家管理。 | SHOP<br>ADDRESS<br>SHOP_INVITE_CODE | Product Requirement、User Flow、DDL、Permission Rules |
| Manicurist | Review Required | 美甲師建立、個人資料及店家歸屬。 | MANICURIST | Product Requirement、DDL、Permission Rules |
| Service Item | Review Required | 建立、查詢、修改及刪除服務項目。 | SERVICE_ITEM | DDL、API、User Flow |
| Portfolio | Review Required | 美甲作品展示及作品管理。 | NAIL_SAMPLE | Product Requirement、DDL、Permission Rules |
| Reservation Block | Review Required | 管理不可預約時段。 | MANICURIST_BLOCK_TIME | Reservation Conflict Rules、Index Design |
| Blacklist | Review Required | 美甲師維護會員黑名單。 | MANICURIST_BLACKLIST | Product Requirement、DDL、Index Design |
| Reservation | In Design | 預約建立、接受、取消、完成及狀態管理。 | RESERVATION<br>RESERVATION_SERVICE | Product Requirement、User Flow、DDL、API、Index Design |
| Review | In Design | 服務完成後雙向評價。 | REVIEW | Product Requirement、DDL、API |
| Portfolio Work | Planned | 美甲師作品集管理。 | MANICURIST_WORK | Product Requirement、ADR、DDL |
| Promotion | Planned | 優惠活動及促銷管理。 | PROMOTION | Product Requirement、ADR、DDL |
| Off Day | Planned | 固定公休及特殊休假管理。 | MANICURIST_OFF_DAY | Product Requirement、User Flow、DDL |
| Tag | Planned | 作品分類與標籤管理。 | TBD | Product Requirement、ADR、DDL、API |
| Search | Planned | 搜尋作品、美甲師及店家。 | TBD | Product Requirement、ADR、API |
| Favorite | Planned | 收藏作品及美甲師。 | TBD | Product Requirement、ADR、DDL |
| Chat | Planned | 會員與美甲師聊天。 | TBD | Product Requirement、User Flow、ADR、API |
| Notification | Planned | 系統通知及預約通知。 | TBD | Product Requirement、ADR、API |
| Recommendation | Planned | 推薦作品及推薦美甲師。 | TBD | Product Requirement、ADR、Algorithm Design |

---

# Module Dependency

```text
Authentication
    │
    ├── Shop Management
    │       │
    │       └── Manicurist
    │               │
    │               ├── Portfolio
    │               ├── Service Item
    │               ├── Reservation Block
    │               ├── Off Day
    │               └── Blacklist
    │
    └── Member
            │
            ├── Search
            ├── Tag
            ├── Favorite
            ├── Chat
            ├── Reservation
            │       │
            │       └── Review
            │
            └── Notification
```

此圖僅表示主要功能相依關係，不代表程式模組依賴。

---

# Documentation Coverage

每個模組開始實作前，至少應完成下列文件。

| Document | Required |
|----------|----------|
| Product Requirement | Required |
| User Flow | Required |
| Database Design (DDL) | Required |
| API Specification | Required |
| Architecture Decision Record (ADR) | Required When Needed |
| Index Design | Required For Database Changes |

---

# Development Priority

| Phase | Primary Modules |
|--------|-----------------|
| Phase 1 | Authentication、Shop Management、Manicurist、Portfolio、Service Item |
| Phase 2 | Reservation、Reservation Block、Review |
| Phase 3 | Promotion、Off Day、Blacklist |
| Phase 4 | Search、Tag、Favorite、Notification |
| Phase 5 | Chat、Recommendation |

Development Priority 僅代表目前產品規劃，不代表最終版本順序。

---

# Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案總覽與產品定位。 |
| System-Architecture.md | 系統整體架構。 |
| Core-Flows.md | 使用者流程。 |
| Table-Catalog.md | 資料表總覽。 |
| API-Catalog.md | API 總覽。 |
| ADR | 架構決策紀錄。 |

---

# Notes

- 本文件為功能模組索引，不作為需求文件。
- 每個模組的詳細需求應建立於對應 Product 文件。
- 模組流程應建立於 User Flow 文件。
- 資料模型應建立於 Database 文件。
- API 規格應建立於 API 文件。
- 重要架構決策應建立 ADR。
- 新增、移除或重大調整模組時，應同步更新本文件。
- 更新模組狀態時，應同步確認相關文件是否完整且保持一致。