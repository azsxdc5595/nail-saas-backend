# Table Catalog

## Table Overview

以下資料表以 Oracle DDL 為準；標示 Proposed 的項目尚未於現行資料庫驗證。

貼文及圖片資料模型以目前提供的 [NSAS Schema 重建 SQL](NSAS-Schema-Rebuild.sql) 為準；較早的 Schema Baseline 尚未列出 `NAIL_POST_IMAGE`。

| Table | Description | Schema Summary | Status |
|------|-------------|----------------|--------|
| USER_ACCOUNT | 會員帳號 | PK `USER_ID`；`USER_CODE`、`EMAIL` 唯一 | Verified |
| USER_ROLE | 使用者角色指派與帳戶授權狀態 | 複合 PK `USER_ID`, `ROLE_CODE`；FK `USER_ID` → `USER_ACCOUNT` | Proposed |
| EMAIL_VERIFICATION | 信箱驗證紀錄 | PK `ID`；包含驗證碼、狀態與到期時間 | Verified |
| REFRESH_TOKEN | Refresh Token | PK `ID`；儲存 Token 與到期時間 | Verified |
| ADDRESS | 地址資料 | PK `ADDRESS_ID`；`ADDRESS_CODE` 唯一 | Verified |
| SHOP | 店家 | FK `ADDRESS_ID` → `ADDRESS` | Verified |
| SHOP_INVITE_CODE | 店家邀請碼 | PK `CODE`；FK `SHOP_ID` → `SHOP`；`STATUS` Check Constraint | Verified |
| MANICURIST | 美甲師 | FK `SHOP_ID`、`USER_ID`；`USER_ID` 唯一 | Verified |
| [MANICURIST_WORK_SCHEDULE](MANICURIST-WORK-SCHEDULE.md) | 美甲師特定日期的實際上班區間 | FK 至 `MANICURIST`；日期及分鐘區間 Check | Proposed |
| MANICURIST_BLACKLIST | 美甲師黑名單 | 僅 PK；尚未建立 FK 與唯一限制 | Pending Design |
| MANICURIST_WORK | 美甲師作品貼文 | 僅 PK；尚未建立 FK | Pending Design |
| NAIL_POST | 美甲作品貼文 | PK `POST_ID`；作品屬性及啟用狀態 | Verified |
| NAIL_POST_IMAGE | 美甲作品貼文圖片 | PK `IMAGE_ID`；FK `POST_ID` → `NAIL_POST`；貼文刪除時 cascade | Verified |
| SERVICE_ITEM | 服務項目 | FK `MANICURIST_ID` → `MANICURIST`；`SERVICE_CODE` 唯一 | Verified |
| PROMOTION | 優惠活動 | 僅 PK；尚未建立 FK | Pending Design |
| [RESERVATION](RESERVATION-DESIGN.md#reservation) | 預約主檔 | PK `RESERVATION_ID`；儲存提議方、預約對象、時間、狀態及總價／總時長 | Proposed |
| [RESERVATION_SERVICE](RESERVATION-DESIGN.md#reservation_service) | 預約服務明細 | 複合 PK；保存 PENDING 提議及已確認服務快照 | Proposed |
| REVIEW | 評價 | 僅 PK；尚未建立 FK 與唯一限制 | Pending Design |

---

## Notes

- `USER_ROLE` 已納入重建 DDL，但尚未於現行 Oracle Schema 驗證；正式 migration 與部署仍待安排。
- `USER_ROLE` 的詳細設計見 [USER-ROLE-Design.md](USER-ROLE-Design.md)。

