# API Catalog

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Review Required |
| Last Updated | 2026-07-28 |

---

## Purpose

本文件維護 Nail SaaS 所有 API 模組及其對應的 API Specification。

本文件僅作為 API 索引，不描述各 API 的 Request、Response、Validation 或商業規則。

各 API 的詳細規格應建立於對應的 API Specification 文件。

---

## API Modules

| Module | Base Path | Version | Status | Specification |
|---------|-----------|---------|--------|---------------|
| Health | `/api/health` | v1 | Implemented | Health.md |
| Authentication | `/api/auth` | v1 | Review Required | Authentication.md |
| User | `/api/users` | v1 | Review Required | User.md |
| Shop | `/api/shops` | v1 | Review Required | Shop.md |
| Manicurist | `/api/manicurists` | v1 | Review Required | Manicurist.md |
| Reservation | `/api/reservations` | v1 | Planned | Reservation.md |
| Reservation Block Time | `/api/reservation-block-times` | v1 | Review Required | Reservation-Block-Time.md |
| Portfolio | `/api/portfolio` | v1 | Review Required | Portfolio.md |
| Service Item | `/api/service-items` | v1 | Review Required | Service-Item.md |
| Blacklist | `/api/blacklists` | v1 | Review Required | Blacklist.md |
| Review | `/api/reviews` | v1 | Planned | Review.md |
| Notification | `/api/notifications` | v1 | Planned | Notification.md |

---

## API Status Definition

| Status | Description |
|--------|-------------|
| Planned | API 尚未開始設計。 |
| Draft | API Specification 撰寫中。 |
| Review Required | API 規格待確認。 |
| Implemented | API 已完成並可提供使用。 |
| Deprecated | API 已停止維護，不建議使用。 |

---

## API Specification Standard

每份 API Specification 至少應包含以下章節：

- Purpose
- Endpoint
- Authorization
- Request
- Response
- Validation
- Error Codes
- HTTP Status Codes
- Business Rules
- Related Tables
- Related User Flow
- Change History

---

## API Design Principles

所有 API 應遵循以下設計原則：

- 採用 RESTful API 設計。
- 使用一致的 URI 命名規範。
- 統一 Request 與 Response 格式。
- 正確使用 HTTP Method。
- 正確回傳 HTTP Status Code。
- Error Response 應遵循統一格式。
- API 不得直接暴露資料庫設計。
- API 規格應先完成文件，再進行程式開發。

---

## API Improvement Roadmap

目前仍待完成以下改善項目：

- 統一 Response Format。
- 建立 Error Code 規範。
- 建立 API Naming Standard。
- 建立 Pagination Standard。
- 建立 API Versioning Strategy。
- 建立 Authentication / Authorization Standard。

---

## Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案定位與產品目標。 |
| Module-Catalog.md | 功能模組總覽。 |
| System-Architecture.md | 系統架構。 |
| Table-Catalog.md | 資料表總覽。 |
| Core-Flows.md | 使用者流程。 |
| ADR | 架構決策紀錄。 |

---

## Notes

- 本文件僅維護 API 模組索引。
- 新增 API 模組時，應同步更新本文件。
- 各 API 的詳細規格應建立於對應的 API Specification 文件。
- API 實作應與 API Specification 保持一致。
- API 異動時，應同步更新對應 Specification 與本文件。