# Authentication API

## Purpose

提供帳號註冊、驗證、登入、Token 更新與密碼重設。

## Authorization

註冊、驗證、登入、refresh 與忘記密碼流程為公開端點；logout 需登入。

## Endpoints

| Method | Path | Authorization | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | 建立帳號並寄送驗證碼 |
| POST | `/auth/verify` | Public | 驗證 Email 並登入 |
| POST | `/auth/login` | Public | 登入並設定 refresh cookie |
| POST | `/auth/refresh` | Refresh cookie | 更新 access / refresh token |
| POST | `/auth/logout` | Authenticated | 撤銷 refresh token |
| POST | `/auth/forgot-password/request` | Public | 寄送重設密碼驗證碼 |
| POST | `/auth/forgot-password/reset` | Public | 驗證後重設密碼 |

## Request Validation

- 註冊：`userName` 必填且最多 50 字；`email` 必填、格式正確且最多 100 字；`phone` 必填且最多 20 字；密碼至少 8 字。
- 登入及 Email 流程：Email 最多 100 字；驗證碼最多 10 字。
- 密碼更新均使用共用最低長度規則；DB 儲存的是編碼後密碼。

## Business Rules

- 註冊 Request 僅接受帳號欄位，不接受資料庫 Entity 欄位，避免客戶端指定驗證狀態或識別碼。
- Refresh token 以 HttpOnly Cookie 傳送並於更新時輪替。

## Related Tables

`USER_ACCOUNT`, `EMAIL_VERIFICATION`, `REFRESH_TOKEN`

## Change History

- 2026-10-30：依實際 Controller 路徑及 Request 驗證更新。
