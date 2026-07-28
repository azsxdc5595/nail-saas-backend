# Module Catalog

Version: 1.0.0  
Status: Draft  
Last Updated: 2026-07-28

---

# 1. Purpose

本文件定義 Nail SaaS 系統所有功能模組及其目前開發狀態。

本文件作為功能模組索引（Module Catalog），提供系統功能總覽，不描述詳細需求、流程、資料模型或技術設計。

---

# 2. Status Definition

| Status | Description |
| --- | --- |
| Planned | 功能已完成規劃，尚未開始設計。 |
| In Design | 功能需求或系統架構設計中。 |
| Review Required | 由既有程式盤點建立，尚待需求確認。 |
| In Implementation | 功能開發中。 |
| Partially Implemented | 已完成部分功能。 |
| Implemented | 功能已完成，且已通過驗證。 |

---

# 3. Module Overview

| Module | Status | Description | Primary Tables |
| --- | --- | --- | --- |
| Authentication | Review Required | 註冊、登入、信箱驗證、Refresh Token 及忘記密碼。 | USER_ACCOUNT<br>EMAIL_VERIFICATION<br>REFRESH_TOKEN |
| Shop Management | Review Required | 建立店家、邀請碼、加入店家及店家管理。 | SHOP<br>ADDRESS<br>SHOP_INVITE_CODE |
| Manicurist | Review Required | 建立及維護美甲師資料與店家歸屬。 | MANICURIST |
| Service Item | Review Required | 建立、查詢、修改及刪除服務項目。 | SERVICE_ITEM |
| Portfolio | Review Required | 美甲作品展示及作品管理。 | NAIL_SAMPLE |
| Reservation Block | Review Required | 管理不可預約時段。 | RESERVATION_BLOCK_TIME |
| Blacklist | Review Required | 管理會員黑名單。 | MANICURIST_BLACKLIST |
| Reservation | In Design | 建立、接受、取消、完成及管理預約。 | RESERVATION<br>RESERVATION_ITEM |
| Review | In Design | 完成服務後建立雙向評價。 | REVIEW |
| Portfolio Work | Planned | 管理美甲師作品集。 | MANICURIST_WORK |
| Promotion | Planned | 管理優惠活動及促銷資訊。 | PROMOTION |
| Off Day | Planned | 管理固定公休及特殊休假。 | MANICURIST_OFF_DAY |
| Tag | Planned | 管理作品分類及標籤。 | N/A |
| Search | Planned | 搜尋作品、美甲師及店家。 | N/A |
| Favorite | Planned | 收藏作品及美甲師。 | N/A |
| Chat | Planned | 提供會員與美甲師即時聊天。 | N/A |
| Notification | Planned | 發送系統通知及預約通知。 | N/A |
| Recommendation | Planned | 推薦作品及推薦美甲師。 | N/A |

---

# 4. Module Dependency

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

本圖僅表示主要功能模組之間的相依關係，不代表實際程式碼依賴關係。

---

# 5. Notes

- 本文件作為 Nail SaaS 功能模組索引。
- 每個模組的詳細需求、流程及技術設計應建立於對應文件。
- 新增、移除或重大調整模組時，應同步更新本文件。
- 模組開發狀態異動時，應同步更新 Status。
- Primary Tables 為模組主要資料表，實際資料模型應以 Database Design (DDL) 為準。