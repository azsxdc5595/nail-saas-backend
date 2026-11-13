# Reservation API

## Purpose

提供會員及美甲師雙向建立預約、回覆預約提議、改提時間、查詢及結束預約的 API。

## Authorization

- 建立會員發起預約：`ROLE_MEMBER`。
- 建立美甲師發起預約：`ROLE_MANICURIST`。
- 預約查詢及後續操作：登入後須為該筆預約的會員或美甲師；Service 再驗證資源所有權。

## Endpoints

| Method | Path | Role | Purpose |
|---|---|---|---|
| POST | `/api/reservations/member` | MEMBER | 會員向指定美甲師提出預約。 |
| POST | `/api/reservations/manicurist` | MANICURIST | 美甲師向指定會員提出預約。 |
| GET | `/api/reservations/availability` | MEMBER / MANICURIST | 查詢指定服務組合可預約的起始時間。 |
| GET | `/api/reservations` | MEMBER / MANICURIST | 分頁查詢目前帳戶參與的預約。 |
| GET | `/api/reservations/{reservationId}` | MEMBER / MANICURIST | 查詢單筆預約。 |
| POST | `/api/reservations/{reservationId}/confirm` | 預約另一方 | 同意目前提議的預約內容。 |
| POST | `/api/reservations/{reservationId}/reject` | 預約另一方 | 拒絕待確認預約。 |
| POST | `/api/reservations/{reservationId}/propose-time` | MANICURIST | 對會員發起的 PENDING 預約提出替代時間。 |
| POST | `/api/reservations/{reservationId}/propose-services` | MANICURIST | 更新 PENDING 預約的服務項目提議。 |
| POST | `/api/reservations/{reservationId}/cancel` | 任一參與方 | 取消 PENDING 或 CONFIRMED 預約。 |
| POST | `/api/reservations/{reservationId}/finish` | MANICURIST | 標記服務完成。 |
| POST | `/api/reservations/{reservationId}/no-show` | MANICURIST | 標記會員未到場。 |

## Requests

所有日期時間使用 ISO 8601 本地日期時間格式，例如 `2026-11-20T13:30:00`。建立預約或提出服務項目變更時需提供至少一個服務項目識別碼；清單最多 20 個且不得重複。

### Create member-initiated reservation

`POST /api/reservations/member`

```json
{
  "manicuristId": 12,
  "startTime": "2026-11-20T13:30:00",
  "serviceIds": [101, 102],
  "customerNote": "希望做短甲"
}
```

會員識別碼取自登入帳戶。

### Create manicurist-initiated reservation

`POST /api/reservations/manicurist`

```json
{
  "userId": 35,
  "startTime": "2026-11-20T13:30:00",
  "serviceIds": [101, 102],
  "manicuristNote": "依溝通內容提出"
}
```

美甲師識別碼取自登入帳戶。

### Query availability

一般預約查詢：

`GET /api/reservations/availability?manicuristId=12&workDate=2026-11-20&serviceIds=101&serviceIds=102`

查詢既有 PENDING 預約的替代時間：

`GET /api/reservations/availability?manicuristId=12&workDate=2026-11-21&reservationId=9001`

依美甲師當日排班扣除有效 PENDING／CONFIRMED 預約占用區間，取得剩餘可工作區間；再依服務總時長篩出可完整容納服務的開始時間，起點以 30 分鐘為間隔。查詢既有預約替代時間時，必須提供 `reservationId`；後端驗證該筆為目前使用者參與的同一位美甲師之有效 PENDING 預約，採用已保存的服務總時長，並從占用區間排除該筆預約自身。可省略 `serviceIds`；若有提供，必須與該筆預約目前保存的服務項目一致。查詢結果是當下候選；送出建立或改期時，後端仍會在鎖定美甲師後重新檢查排班與時段衝突，以處理查詢後時段被其他預約占用的情況。

### Confirm

`POST /api/reservations/{reservationId}/confirm`

不需 Request Body。此操作確認目前已保存並展示的服務項目及預約時段，將狀態更新為 `CONFIRMED`。服務明細不會在確認時重新建立。

### Propose service changes

`POST /api/reservations/{reservationId}/propose-services`

```json
{
  "serviceIds": [101, 105]
}
```

美甲師更新目前 PENDING 提議的服務項目。系統驗證項目屬於該美甲師且仍啟用，重新計算總價與總時長，更新 `RESERVATION_SERVICE` 明細及預約結束時間，並重新檢查排班和時段衝突。此更新與 `RESERVATION` 主檔在同一交易內完成，提議方更新為美甲師並重設 24 小時回覆期限；會員可查詢新明細後確認或拒絕。

### Propose alternative time

`POST /api/reservations/{reservationId}/propose-time`

```json
{
  "startTime": "2026-11-20T15:00:00"
}
```

限美甲師對會員發起且尚待回覆的預約提出一次替代時間。新時段通過排班與衝突檢查後，更新預約起訖時間、將 `INITIATOR_TYPE` 設為 `MANICURIST`，並重新起算 24 小時期限；會員可確認或拒絕。原時段由更新後的時間區間釋出。

## Response

成功時回傳 Reservation response，含預約識別碼、代碼、會員／美甲師識別碼、起訖時間、狀態、`initiatorType`、總價、總時長、備註、回覆期限及目前提議／已確認的服務明細。`initiatorType` 表示目前待回覆提議的提出方；PENDING 預約也會回傳 `services`。

GET list 回傳 Spring 分頁格式。預約明細只回傳該參與者有權查看的內容。

## Business Rules

- 預約建立後狀態為 `PENDING`；只有預約另一方能確認或拒絕。拒絕後為 `REJECTED`，並保留預約主檔。
- `PENDING` 與 `CONFIRMED` 占用美甲師時段；`EXPIRED`、`REJECTED`、`CANCELLED` 釋出時段。
- 回覆期限為目前 PENDING 提議的 `UPDATE_TIME + 24 小時`。逾時改為 `EXPIRED`。第一版在查詢／操作時檢查逾時，不含背景排程。
- 預約建立與改時段均鎖定美甲師記錄，並使用與排班修改相同的鎖，避免同時預約或改班造成衝突。
- Availability 查詢依 `serviceIds` 計算時長並排除已占用時段；最終可用性以建立／改期／改服務時的鎖定檢查為準。
- 開始時間必須晚於現在、落在美甲師該日連續排班區間內，並以 30 分鐘為起點間隔；首尾相接的排班區間視為連續，服務不可跨越有間隔的區段。
- 服務時間為所選服務 `DURATION_MIN` 加總，總價為 `PRICE` 加總。建立預約時即將服務名稱、價格及時間快照寫入 `RESERVATION_SERVICE`；PENDING 期間的明細代表目前待確認提議，美甲師改提服務內容時以新明細取代。總時長不得超過 `NUMBER(5)`，總價不得超過 `NUMBER(10,2)`。
- 同一筆預約僅支援一次替代時間提議。會員確認時確認目前預約明細，不重新提交 `serviceIds`。
- 任一參與方可取消 PENDING 或 CONFIRMED 預約；美甲師可在 CONFIRMED 後標記 FINISH 或 NO_SHOW。每種終止狀態不能再次轉換。
- 美甲師只能在預約開始時間後標記 NO_SHOW，並只能在預約結束時間後標記 FINISH。
- 查詢或操作遇到已逾時的 PENDING 時，先將其轉為 `EXPIRED`，再回傳最新狀態或拒絕該操作。

## Error Codes

錯誤使用共用 `ApiError` 格式；包含預約不存在、非參與者、狀態不允許、開始時間無效、時段不在排班內、時段衝突，以及服務項目無效等錯誤。逾時請求回傳最新的 `EXPIRED` 預約狀態，不另回傳錯誤。

## Related Tables

- `RESERVATION`
- `RESERVATION_SERVICE`
- `MANICURIST`
- `MANICURIST_WORK_SCHEDULE`
- `SERVICE_ITEM`
- `USER_ACCOUNT`

## Related User Flow

- [Member Reservation Journey](../05_UserFlow/Core-Flows.md#member-reservation-journey)

## Change History

| Date | Change |
|---|---|
| 2026-11-13 | Draft first backend API for availability lookup, bidirectional reservation, 24-hour response, time/service proposals and persisted PENDING service details. |
