# Service Item API

## Purpose

管理美甲師服務項目，並供使用者搜尋啟用的服務。

## Endpoint

Base path: `/serviceItem`

| Method | Path | Purpose | Success response |
| --- | --- | --- | --- |
| GET | `/serviceItem/searchMyServiceItem` | 美甲師查看自己的服務 | `SearchServiceItemResponse` |
| POST | `/serviceItem/add` | 建立服務項目 | Empty body |
| PATCH | `/serviceItem/edit` | 修改自己的服務項目 | Empty body |
| DELETE | `/serviceItem/remove` | 刪除自己的服務項目 | Empty body |
| POST | `/serviceItem/search` | 搜尋服務項目 | `SearchServiceItemResponse` |

## Authorization

JWT authentication required。修改及刪除須確認服務項目屬於目前登入美甲師。

## PATCH Request / Validation

Request 使用 `UpdateServiceItemRequest`。`serviceId` 必填；`serviceName`、`price`、`description`、`durationMin`、`isActive` 可選，null 表示維持現值。`manicuristId` 不可改變服務所有權，伺服器以目前 JWT 使用者查得所有權。

欄位長度及必要性須符合 `SERVICE_ITEM` DDL：名稱最多 200 字元且不得為空；價格、服務時間及 MANICURIST_ID 不得被 PATCH 清成 null。`isActive` 為 0 或 1。

DDL 未定義 `durationMin` 的正數下限；此 PATCH 規格不額外假設其值域。

## Response

PATCH 成功時回傳空 body。錯誤回應使用共用 `ApiError` 格式。

## Error Codes / HTTP Status

| Error | HTTP | Meaning |
| --- | --- | --- |
| `SERVICE_ITEM_001` | 400 | 服務項目不存在 |
| `PERMISSION_001` | 403 | 服務項目不屬於目前登入美甲師 |
| `REQUEST_001` | 400 | Request validation failed |
| Authentication failure | 401 | JWT 無效或缺少 |

## Business Rules

- PATCH 只修改有提供且非 null 的欄位。
- `SERVICE_CODE` 與 `MANICURIST_ID` 是識別及所有權資料，不因一般服務內容修改而變更。
- `UPDATE_TIME` 在服務更新交易中更新。

## Related Tables / User Flow

- Tables: `SERVICE_ITEM`, `MANICURIST`
- User Flow: 美甲師服務項目維護流程尚未列入 Core-Flows。

## Change History

| Date | Change |
| --- | --- |
| 2026-10-30 | 明確定義 PATCH 部分更新、所有權驗證及穩定服務代碼。 |
