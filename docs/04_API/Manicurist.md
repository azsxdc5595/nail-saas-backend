# Manicurist API

## Purpose

查詢美甲師資料及由登入中的美甲師修改自己的公開資料。

## Endpoint

Base path: `/manicurist`

| Method | Path | Purpose | Success response |
| --- | --- | --- | --- |
| GET | `/manicurist/me` | 依 Request 中的 `manicuristCode` 查詢美甲師資料 | `GetManicuristInfoReponse` |
| PATCH | `/manicurist/me` | 修改目前登入美甲師的顯示名稱或介紹 | Empty body |

## Authorization

JWT authentication required. PATCH 的目標由目前登入者的 user code 決定，不接受 Request 指定其他美甲師。

## PATCH Request / Validation

Request 使用 `UpdateManicuristRequest`。`displayName`、`intro` 為可選欄位；null 表示維持原值。提供的 `displayName` 不可為空白，最多 50 字元；`intro` 最多 500 字元，符合 `MANICURIST` DDL。舊版 `manicuristCode` 欄位暫時保留相容性，但伺服器忽略其值。

## Response

PATCH 成功時回傳空 body。錯誤回應使用共用 `ApiError` 格式。

## Error Codes / HTTP Status

| Error | HTTP | Meaning |
| --- | --- | --- |
| `MANICURIST_002` | 404 | 找不到目前登入者的美甲師資料 |
| `REQUEST_001` | 400 | Request validation failed |
| Authentication failure | 401 | JWT 無效或缺少 |

## Business Rules

- PATCH 只更新目前 JWT 所屬的美甲師，不採信用戶提供的美甲師識別碼。
- 只有非 null 欄位會變更；null 不會清除既有資料。
- 更新資料與 `UPDATE_TIME` 應在 Service transaction 內保存。

## Related Tables / User Flow

- Table: `MANICURIST`
- User Flow: 美甲師個人資料維護流程尚未列入 Core-Flows。

## Change History

| Date | Change |
| --- | --- |
| 2026-10-30 | 明確定義 `/me` PATCH 以 JWT user code 為目標，不接受客戶端指定目標美甲師。 |
