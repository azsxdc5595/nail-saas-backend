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

本文件列出目前專案已存在的 API 端點，作為 API 索引使用。

本文件不描述 API 詳細規格、Request、Response 或商業規則。

正式 API 規格應建立於各模組對應的 API 文件。

---

# API Overview

| Module | Method | Path | Description |
|---------|--------|------|-------------|
| Health | GET | /api/health | 健康檢查。 |
| Authentication | POST | /auth/register | 註冊並建立信箱驗證。 |
| Authentication | POST | /auth/verify | 驗證信箱並核發 Token。 |
| Authentication | POST | /auth/login | 使用者登入。 |
| Authentication | POST | /auth/refresh | 更新 Access Token。 |
| Authentication | POST | /auth/forgot-password/request | 申請忘記密碼。 |
| Authentication | POST | /auth/forgot-password/reset | 重設密碼。 |
| Authentication | POST | /auth/logout | 使用者登出。 |
| User | GET | /user/me | 取得目前會員資訊。 |
| User | PATCH | /user/me | 修改會員資料。 |
| User | PATCH | /user/password | 修改密碼。 |
| User | POST | /user/email/request | 申請更換信箱。 |
| User | POST | /user/email/confirm | 確認更換信箱。 |
| Shop | POST | /shop/create | 建立店家。 |
| Shop | GET | /shop/me | 取得目前店家資訊。 |
| Shop | POST | /shop/invite | 建立邀請碼。 |
| Shop | POST | /shop/join | 使用邀請碼加入店家。 |
| Manicurist | GET | /manicurist/getInfo | 取得美甲師資訊。 |
| Reservation Block | GET | /manicuristBlockTime/getBlockTime | 查詢不可預約時段。 |
| Reservation Block | POST | /manicuristBlockTime/add | 新增不可預約時段。 |
| Reservation Block | DELETE | /manicuristBlockTime/remove | 刪除不可預約時段。 |
| Portfolio | GET | /nailSample/getNailSample | 查詢作品。 |
| Portfolio | POST | /nailSample/add | 新增作品。 |
| Portfolio | DELETE | /nailSample/remove | 刪除作品。 |
| Service Item | GET | /api/services | 查詢服務項目。 |
| Service Item | POST | /api/services | 建立服務項目。 |
| Service Item | DELETE | /api/services/{id} | 刪除服務項目。 |
| Blacklist | POST | /blacklist/getAll | 查詢黑名單。 |
| Blacklist | POST | /blacklist/add | 加入黑名單。 |
| Blacklist | DELETE | /blacklist/remove | 移除黑名單。 |

---

# Known Issues

目前已發現以下 API 設計問題，待正式 API 規範建立後統一調整。

- 多個 GET API 使用 Request Body。
- URI 命名風格尚未統一。
- RESTful 命名規則尚未完全遵循。
- Response 格式尚未統一。
- Error Code 尚未建立標準。
- Status Code 尚未建立一致規範。

本文件僅盤點現況，不直接修改既有 API。

---

# Future Improvements

正式 API Specification 應至少包含：

- Purpose
- Roles
- Request
- Response
- Validation
- Error Codes
- HTTP Status Codes
- Business Rules
- Related User Flow

完成 API Specification 後，再逐步調整 Controller 實作。

---

# Related Documents

| Document | Purpose |
|----------|---------|
| Project.md | 專案定位與產品目標。 |
| Module-Catalog.md | 功能模組總覽。 |
| Core-Flows.md | 使用者流程。 |
| Table-Catalog.md | 資料表總覽。 |
| System-Architecture.md | 系統架構。 |
| ADR | 架構決策紀錄。 |

---

# Notes

本文件為 API 索引。

正式 API 規格應建立於各模組的 API 文件中。

新增、移除或修改 API 時，應同步更新本文件。

本文件不描述 API 商業規則，商業流程應以 Product、User Flow 及 ADR 為準。