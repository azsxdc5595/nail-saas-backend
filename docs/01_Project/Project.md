# Nail SaaS Project

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Draft |
| Last Updated | 2026-07-19 |

---

# Purpose

Nail SaaS 是一套提供美甲店、美甲師與會員使用的 Software-as-a-Service（SaaS）平台。

平台以作品探索為入口，讓會員能探索作品、了解美甲師、進行溝通、建立預約，並於服務完成後留下評價。

同時提供店家與美甲師管理作品、服務項目、預約、營業時段及顧客關係等功能，提升營運效率與服務品質。

本文件作為產品最高層級文件，描述產品定位、目標、範圍及核心能力。

---

# Product Vision

打造以作品探索為核心的美甲 SaaS 平台。

會員透過作品找到適合自己的美甲師，完成溝通、預約及服務。

美甲師與店家透過作品展示、預約管理及顧客經營，提高工作效率並建立長期客戶關係。

所有功能皆應圍繞此產品願景進行設計與演進。

---

# Business Goals

本產品希望建立完整且可持續擴充的美甲預約平台。

主要目標如下：

- 提供以作品為核心的會員探索體驗。
- 建立完整且清楚的預約流程。
- 降低美甲師管理預約的成本。
- 協助店家管理美甲師與營運資訊。
- 建立雙向評價機制，提高平台信任度。
- 提供可持續維護與擴充的 SaaS 架構。

---

# Target Users

| Role | Description |
|------|-------------|
| Member | 瀏覽作品、搜尋美甲師、聊天、建立預約及留下評價。 |
| Manicurist | 管理個人資料、作品、服務項目、預約及顧客。 |
| Shop Owner | 建立店家、管理店家資訊、邀請及管理美甲師。 |
| Platform Administrator | 管理平台營運及系統設定（待規劃）。 |

---

# Core Modules

| Module | Description |
|---------|-------------|
| Authentication | 使用者註冊、登入、身份驗證及帳號安全。 |
| Shop Management | 店家建立、店家管理及邀請美甲師。 |
| Manicurist | 美甲師個人資料及店家歸屬管理。 |
| Portfolio | 美甲作品展示及作品管理。 |
| Service Item | 服務項目與價格管理。 |
| Reservation | 預約建立、處理及生命週期管理。 |
| Reservation Block | 不可預約時段管理。 |
| Review | 完成服務後的雙向評價。 |
| Blacklist | 黑名單管理。 |
| Notification | 系統通知與預約通知。 |
| Chat | 會員與美甲師溝通。 |
| Search | 搜尋作品、美甲師及店家。 |

---

# Core User Journey

```text
Browse Artwork
        ↓
Artwork Detail
        ↓
Manicurist Profile
        ↓
Chat
        ↓
Reservation Request
        ↓
Reservation Processing
        ↓
Completed Service
        ↓
Mutual Review
```

產品主要流程如下：

1. 會員瀏覽作品。
2. 查看作品及美甲師資訊。
3. 與美甲師進行溝通。
4. 建立預約申請。
5. 美甲師處理預約。
6. 完成服務。
7. 雙方留下評價。

尚未完成設計的功能，應先完成產品需求、User Flow、DDL、API 與必要的 ADR，再開始實作。

---

# Product Scope

## In Scope

目前版本主要涵蓋以下功能：

- Authentication
- Shop Management
- Manicurist
- Portfolio
- Service Item
- Reservation
- Reservation Block
- Review
- Blacklist

---

## Out of Scope

以下功能尚未完成需求確認，不得自行實作：

- Payment
- Recommendation
- Nearby Search
- Push Notification
- Mobile Application
- Back-office Management
- Analytics Dashboard
- AI Features

新增功能必須先完成需求設計並更新相關文件。

---

# Product Principles

產品遵循以下原則：

1. Artwork First  
   以作品探索作為會員進入平台的主要入口。

2. Communication Before Reservation  
   預約應建立於會員與美甲師完成溝通之後。

3. Documentation First  
   功能開發前應先完成產品與技術文件。

4. Source of Truth  
   文件為唯一設計依據，程式碼不得凌駕於文件之上。

5. Code Follows Design  
   程式碼應忠實實作文件所描述的設計。

6. Extensibility  
   所有設計皆應考量未來功能擴充及維護成本。

7. Consistency  
   系統命名、架構及設計應保持一致性。

---

# Development Roadmap

| Phase | Objective | Modules |
|---------|-----------|---------|
| Phase 1 | Foundation | Authentication、Shop Management、Manicurist、Portfolio、Service Item |
| Phase 2 | Reservation | Reservation、Reservation Block、Review |
| Phase 3 | Business Features | Promotion、Off Day、Blacklist |
| Phase 4 | Community Features | Search、Favorite、Notification |
| Phase 5 | Growth Features | Chat、Recommendation |

---

# Related Documents

| Document | Purpose |
|----------|---------|
| Module-Catalog.md | 功能模組總覽。 |
| System-Architecture.md | 系統整體架構。 |
| Core-Flows.md | 使用者流程。 |
| Table-Catalog.md | 資料表總覽。 |
| API-Catalog.md | API 總覽。 |
| ADR | 架構決策紀錄。 |

---

# Notes

本文件描述 Nail SaaS 的產品定位與整體方向。

詳細需求、商業規則、資料模型、API 規格及系統架構，應分別記錄於對應文件中。

當實作內容與本文件衝突時，應先確認產品設計是否需要調整，再決定修改程式碼或更新文件。

本文件不描述實作細節，而是定義產品應具備的核心能力與發展方向。