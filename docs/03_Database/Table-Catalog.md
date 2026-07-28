# Table Catalog

## Table Overview

以下資料表皆已依 Oracle DDL 驗證。

| Table | Description | Schema Summary | Status |
|------|-------------|----------------|--------|
| USER_ACCOUNT | 會員帳號 | PK `USER_ID`；`USER_CODE`、`EMAIL` 唯一 | Verified |
| EMAIL_VERIFICATION | 信箱驗證紀錄 | PK `ID`；包含驗證碼、狀態與到期時間 | Verified |
| REFRESH_TOKEN | Refresh Token | PK `ID`；儲存 Token 與到期時間 | Verified |
| ADDRESS | 地址資料 | PK `ADDRESS_ID`；`ADDRESS_CODE` 唯一 | Verified |
| SHOP | 店家 | FK `ADDRESS_ID` → `ADDRESS` | Verified |
| SHOP_INVITE_CODE | 店家邀請碼 | PK `CODE`；FK `SHOP_ID` → `SHOP`；`STATUS` Check Constraint | Verified |
| MANICURIST | 美甲師 | FK `SHOP_ID`、`USER_ID`；`USER_ID` 唯一 | Verified |
| RESERVATION_BLOCK_TIME | 不可預約時段 | Block Type Check；提供時段查詢索引 | Verified |
| MANICURIST_BLACKLIST | 美甲師黑名單 | 僅 PK；尚未建立 FK 與唯一限制 | Pending Design |
| MANICURIST_WORK | 美甲師作品 | 僅 PK；尚未建立 FK | Pending Design |
| NAIL_SAMPLE | 美甲範例 | Style / Season / Color Code；Entity 與 DDL 不一致 | Entity Mismatch |
| SERVICE_ITEM | 服務項目 | FK `MANICURIST_ID` → `MANICURIST`；`SERVICE_CODE` 唯一 | Verified |
| PROMOTION | 優惠活動 | 僅 PK；尚未建立 FK | Pending Design |
| RESERVATION | 預約主檔 | 使用 `RESERVATION_DATE`、`START_SLOT_ID`、`SLOT_COUNT` 表示預約時段 | Entity Mismatch |
| RESERVATION_SERVICE | 預約服務明細 | 複合 PK；FK `RESERVATION`、`SERVICE_ITEM` | Verified |
| REVIEW | 評價 | 僅 PK；尚未建立 FK 與唯一限制 | Pending Design |

---

## Notes

