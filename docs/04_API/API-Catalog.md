# API Catalog

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Review Required |
| Last Updated | 2026-07-19 |

---

# Purpose

本文件列出 Nail SaaS 所有 API 模組及其對應的 API Specification。

本文件僅作為 API 索引，不描述各 API 的 Request、Response 或商業規則。

詳細 API 規格應建立於各模組的 API 文件。

---

# API Modules

| Module | Base Path | Status | Specification |
|---------|-----------|--------|---------------|
| Health | /api | Implemented | Health.md |
| Authentication | /auth | Review Required | Authentication.md |
| User | /user | Review Required | User.md |
| Shop | /shop | Review Required | Shop.md |
| Manicurist | /manicurist | Review Required | Manicurist.md |
| Reservation | /reservation | Planned | Reservation.md |
| Reservation Block | /manicuristBlockTime | Review Required | Reservation-Block.md |
| Portfolio | /nailSample | Review Required | Portfolio.md |
| Service Item | /api/services | Review Required | Service-Item.md |
| Blacklist | /blacklist | Review Required | Blacklist.md |
| Review | /review | Planned | Review.md |
| Notification | /notification | Planned | Notification.md |

---

# API Specification Standard

每個 API Specification 至少應包含：

- Purpose
- Endpoint
- Roles
- Request
- Response
- Validation
- Error Codes
- HTTP Status Codes
- Business Rules
- Related Tables
- Related User Flow

---

# Known Issues

目前 API 設計仍存在以下待改善事項：

- 多個 GET API 使用 Request Body。
- URI 命名尚未完全符合 RESTful。
- Response 格式尚未統一。
- Error Code 尚未建立統一規範。
- HTTP Status Code 尚未建立一致標準。

上述問題將於 API Specification 完成後逐步改善。

---

# Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案定位。 |
| Module-Catalog.md | 功能模組總覽。 |
| System-Architecture.md | 系統架構。 |
| Table-Catalog.md | 資料表總覽。 |
| Core-Flows.md | 使用者流程。 |
| ADR | 架構決策紀錄。 |

---

# Notes

本文件僅維護 API 模組索引。

新增 API 模組時，應同步更新本文件。

各 API 的詳細規格應建立於對應的 API Specification 文件。