# User Self-Service API

## Purpose

讓登入使用者修改自己的基本資料及密碼。

## Endpoint

Base path: `/user`

| Method | Path | Purpose | Success response |
| --- | --- | --- | --- |
| GET | `/user/me` | 查詢目前使用者資料 | `GetUserInfoReponse` |
| PATCH | `/user/me` | 修改目前使用者名稱或電話 | Empty body |
| PATCH | `/user/password` | 驗證舊密碼後更新密碼 | Empty body |

## Authorization

JWT authentication required。更新目標固定為目前登入使用者。

## PATCH Request / Validation

- `/me` 使用 `UpdateUserRequest`：`userName`、`phone` 可選；null 表示保留原值。提供時不可為空白，字串長度依 DDL 分別不得超過 50、20 字元。
- `/password` 使用 `UpdatePasswordRequest`：`oldPassword` 必填；新密碼至少 8 字元。
- 舊密碼缺少時回傳 `REQUEST_001`；新密碼缺少、空白或不足 8 字元時，與忘記密碼重設流程一致，回傳 `AUTH_010`。

## Response

兩個 PATCH 成功時皆回傳空 body。錯誤回應使用共用 `ApiError` 格式。

## Error Codes / HTTP Status

| Error | HTTP | Meaning |
| --- | --- | --- |
| `USER_001` | 404 | 找不到目前使用者 |
| `USER_003` | 400 | 舊密碼錯誤 |
| `AUTH_010` | 400 | 新密碼至少 8 字元 |
| `REQUEST_001` | 400 | Request validation failed |
| Authentication failure | 401 | JWT 無效或缺少 |

## Business Rules

- `/me` 僅更新非 null 欄位。
- `/password` 必須先通過舊密碼驗證，儲存前以 PasswordEncoder 雜湊新密碼。
- 更新資料與時間戳應在 Service transaction 內保存。

## Related Tables / User Flow

- Tables: `USER_ACCOUNT`
- User Flow: 帳號資料與密碼維護流程尚未列入 Core-Flows。

## Change History

| Date | Change |
| --- | --- |
| 2026-10-30 | 明確記錄使用者資料 PATCH 語意及密碼長度規則。 |
