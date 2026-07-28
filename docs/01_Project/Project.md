# Project

Version: 1.0.0  
Status: Draft  
Last Updated: 2026-07-28

---

# 1. Purpose

Nail SaaS 是一套提供美甲店、美甲師及會員使用的 Software-as-a-Service（SaaS）平台。

平台以作品探索（Artwork Discovery）為核心，讓會員透過作品找到適合的美甲師，完成溝通、預約、服務及評價。

同時提供店家與美甲師管理作品、服務項目、預約、營業時間及顧客關係等功能，以提升營運效率與服務品質。

本文件作為專案最高層級文件，定義產品定位、產品願景、核心能力及設計原則。

---

# 2. Product Vision

打造以作品探索為核心的美甲 SaaS 平台。

會員透過作品探索找到適合自己的美甲師，完成溝通、預約及服務。

美甲師與店家透過作品展示、預約管理及顧客經營，提高工作效率並建立長期客戶關係。

所有功能設計皆應符合本產品願景。

---

# 3. Business Goals

本產品主要目標如下：

- 提供以作品探索為核心的會員體驗。
- 建立完整且清晰的預約流程。
- 降低美甲師管理預約的成本。
- 提供店家管理美甲師及營運資訊的能力。
- 建立雙向評價機制，提高平台信任度。
- 建立具備高擴充性與高維護性的 SaaS 架構。

---

# 4. Target Users

| Role | Description |
| --- | --- |
| Member | 瀏覽作品、搜尋美甲師、聊天、建立預約及留下評價。 |
| Manicurist | 管理個人資料、作品、服務項目、預約及顧客。 |
| Shop Owner | 建立店家、管理店家資訊、邀請及管理美甲師。 |
| Platform Administrator | 管理平台營運及系統設定。 |

---

# 5. Core Modules

| Module | Description |
| --- | --- |
| Authentication | 使用者註冊、登入及帳號安全。 |
| Shop Management | 店家建立、店家管理及邀請美甲師。 |
| Manicurist | 美甲師資料及店家歸屬管理。 |
| Portfolio | 美甲作品展示及管理。 |
| Service Item | 服務項目及價格管理。 |
| Reservation | 預約建立、處理及生命週期管理。 |
| Reservation Block | 不可預約時段管理。 |
| Review | 完成服務後的雙向評價。 |
| Blacklist | 黑名單管理。 |
| Search | 搜尋作品、美甲師及店家。 |
| Chat | 會員與美甲師溝通。 |
| Notification | 系統通知及預約通知。 |

---

# 6. Core User Journey

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

主要使用流程如下：

1. 會員瀏覽作品。
2. 查看作品及美甲師資訊。
3. 與美甲師進行溝通。
4. 建立預約申請。
5. 美甲師處理預約。
6. 完成服務。
7. 雙方完成評價。

---

# 7. Product Scope

## In Scope

目前版本包含下列功能：

- Authentication
- Shop Management
- Manicurist
- Portfolio
- Service Item
- Reservation
- Reservation Block
- Review
- Blacklist

## Out of Scope

目前版本不包含下列功能：

- Payment
- Recommendation
- Nearby Search
- Push Notification
- Mobile Application
- Back-office Management
- Analytics Dashboard
- AI Features

---

# 8. Product Principles

產品設計遵循下列原則：

| Principle | Description |
| --- | --- |
| Artwork First | 以作品探索作為會員進入平台的主要入口。 |
| Communication Before Reservation | 完成溝通後再建立預約。 |
| Documentation First | 功能開發前應完成相關文件。 |
| Source of Truth | 文件為系統設計唯一依據。 |
| Code Follows Design | 程式碼應依據設計文件實作。 |
| Extensibility | 系統設計應具備良好的擴充能力。 |
| Consistency | 系統命名、架構及設計應保持一致。 |

---

# 9. Notes

- 本文件定義 Nail SaaS 的產品定位及整體方向。
- 功能模組請參考 Module-Catalog.md。
- 開發規劃請參考 Roadmap.md。
- 詳細需求、流程及技術設計應建立於各自對應文件。
- 本文件不描述實作細節。