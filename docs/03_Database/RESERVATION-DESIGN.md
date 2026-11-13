# Reservation Data Design

**權威來源：** [NSAS Schema 重建 SQL](NSAS-Schema-Rebuild.sql)。本文件描述預約主檔及目前提議／已確認的服務明細。

## Purpose

預約的日期時間、參與者、狀態及總額存於 `RESERVATION`。`RESERVATION_SERVICE` 保存目前提議或已確認的服務明細。

會員或美甲師皆可發起預約。建立 PENDING 時即將所選服務明細寫入 `RESERVATION_SERVICE`，供雙方查看目前待確認提議。美甲師提出服務內容變更時，以新明細取代目前明細，並同步更新主檔總價、總服務時間及結束時間；對方同意時只需確認目前提議，不需再次提交服務項目。

服務名稱、價格及時間均保存快照，避免 `SERVICE_ITEM` 後續修改影響預約提議或已確認內容。若新增服務尚不在目錄中，先建立 `SERVICE_ITEM`，再將其識別碼納入服務提議。

## RESERVATION

### Purpose

儲存單次預約的主體資料、預約時段、生命週期狀態及總價／總服務時間。

### Columns

| Column | Type | Nullable | Description |
|---|---|---:|---|
| `RESERVATION_ID` | `NUMBER` | No | 主鍵。 |
| `RESERVATION_CODE` | `VARCHAR2(36)` | Yes | 預約唯一代碼。 |
| `SHOP_ID` | `NUMBER` | No | 預約店家識別碼。 |
| `USER_ID` | `NUMBER` | No | 會員使用者識別碼。 |
| `MANICURIST_ID` | `NUMBER` | No | 服務美甲師識別碼。 |
| `START_TIME` | `TIMESTAMP` | No | 預約開始時間。 |
| `END_TIME` | `TIMESTAMP` | No | 預約結束時間；必須晚於開始時間。 |
| `STATUS` | `VARCHAR2(30)` | No | 預約生命週期狀態。 |
| `INITIATOR_TYPE` | `VARCHAR2(20)` | No | 目前待回覆預約內容的提議方角色：`MEMBER` 或 `MANICURIST`。 |
| `TOTAL_DURATION_MIN` | `NUMBER(5)` | No | 此預約服務總時間（分鐘）。 |
| `TOTAL_PRICE` | `NUMBER(10,2)` | No | 此預約服務總價。 |
| `CUSTOMER_NOTE` | `VARCHAR2(500)` | Yes | 會員備註。 |
| `MANICURIST_NOTE` | `VARCHAR2(500)` | Yes | 美甲師備註。 |
| `CREATE_TIME` | `TIMESTAMP` | Yes | 建立時間。 |
| `UPDATE_TIME` | `TIMESTAMP` | Yes | 最後更新時間。 |

### Status Codes

Java 狀態列舉定義於 `ReservationStatusEnum`。資料庫 `STATUS` 欄位使用相同代碼；狀態轉換與時段占用規則另由 Reservation 流程規格定義。

| Code | Description |
|---|---|
| `PENDING` | 預約待確認 |
| `EXPIRED` | 預約已逾時 |
| `CONFIRMED` | 預約已確認 |
| `REJECTED` | 預約已拒絕 |
| `FINISH` | 服務已完成 |
| `CANCELLED` | 預約已取消 |
| `NO_SHOW` | 預約未履行（放鳥） |

### Constraints and Indexes

- PK：`RESERVATION_ID`。
- Unique：`RESERVATION_CODE`。
- FK：`SHOP_ID` → `SHOP`、`USER_ID` → `USER_ACCOUNT`、`MANICURIST_ID` → `MANICURIST`。
- Check：`END_TIME > START_TIME`。
- Check：`STATUS` 限定為列出的預約狀態；`INITIATOR_TYPE` 限定為 `MEMBER`、`MANICURIST`。
- Indexes：`(MANICURIST_ID, START_TIME, END_TIME)` 用於美甲師時段衝突查詢；`USER_ID`、`SHOP_ID` 用於會員及店家預約查詢。

## RESERVATION_SERVICE

### Purpose

保存單次預約目前提議或已確認的服務明細。`PENDING` 時為待確認提議；`CONFIRMED` 時為雙方同意的內容。

### Columns

| Column | Type | Nullable | Description |
|---|---|---:|---|
| `RESERVATION_ID` | `NUMBER` | No | 預約識別碼；複合主鍵及外鍵。 |
| `SERVICE_SEQ` | `NUMBER` | No | 該預約內服務項目序號；複合主鍵。 |
| `SERVICE_ID` | `NUMBER` | Yes | 對應服務目錄項目。 |
| `SERVICE_NAME` | `VARCHAR2(200)` | No | 寫入該提議版本時的服務名稱快照。 |
| `PRICE` | `NUMBER(10,2)` | Yes | 寫入該提議版本時的價格快照。 |
| `DURATION_MIN` | `NUMBER` | Yes | 寫入該提議版本時的服務時間快照（分鐘）。 |
| `CREATE_TIME` | `TIMESTAMP` | Yes | 該明細版本建立時間。 |

### Constraints and Indexes

- PK：`(RESERVATION_ID, SERVICE_SEQ)`。
- FK：`RESERVATION_ID` → `RESERVATION`、`SERVICE_ID` → `SERVICE_ITEM`。
- 查詢：以複合主鍵前綴 `RESERVATION_ID` 取得該預約的正式服務明細；目前沒有依 `SERVICE_ID` 查詢此表的已確認需求，因此不額外建立索引。

## Query and Transaction Notes

- 顯示 PENDING 提議或已成立預約的服務內容時，讀取 `RESERVATION_SERVICE`。
- 建立預約時，需在同一交易內寫入主檔與服務明細；更改服務提議時，需在同一交易內取代明細並更新主檔總價、總服務時間及結束時間。
- 預約時段衝突查詢使用 `RESERVATION` 的美甲師、起迄時間索引；僅 `PENDING`、`CONFIRMED` 占用時段。詳細狀態轉換與 API 規格見 [Reservation API](../04_API/Reservation.md)。
- Reservation Controller、Service、Repository 與 API 第一版已建立；部署前仍須在目標 Oracle Schema 套用符合此 DDL 的結構。
- PENDING 期間的 `RESERVATION_SERVICE` 是目前提議內容；拒絕、取消或逾時後保留這些明細，作為該預約紀錄的一部分。服務提議被替換時不保留舊版本歷史。
- 第一版以 `UPDATE_TIME` 作為 24 小時回覆期限的起算時間；逾時狀態在讀取或操作預約時惰性更新，不包含背景排程。
- 本設計是重建 DDL，不是現有資料庫的 migration。正式資料庫套用方式另行管理。

## Change History

| Date | Change |
|---|---|
| 2026-11-13 | 調整 `RESERVATION_SERVICE` 保存時機：建立時即保存待確認提議；確認時沿用目前明細。 |

## Related Documents

- [Table Catalog](Table-Catalog.md)
- [Core User Flows](../05_UserFlow/Core-Flows.md#member-reservation-journey)
- [Reservation API Catalog](../04_API/API-Catalog.md)
