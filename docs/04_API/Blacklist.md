# Blacklist API

## Purpose

讓美甲師管理自己的黑名單。

## Authorization

所有端點需登入且目前帳號必須具有 Manicurist 身分。資料範圍由登入者決定。

## Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/blacklist/getAll` | 查詢登入美甲師的黑名單（無 Request Body） |
| POST | `/blacklist/add` | 將使用者加入自己的黑名單 |
| DELETE | `/blacklist/remove` | 從自己的黑名單移除使用者 |

## Request Validation

- Add / remove：`userId` 必填；`reason` 最多 500 字。
- 舊版 `manicuristId` 欄位若仍隨請求傳入會被忽略，伺服器以登入身分限定資料範圍。

## Related Tables

`MANICURIST_BLACKLIST`, `MANICURIST`, `USER_ACCOUNT`

## Change History

- 2026-10-30：記錄登入者資料範圍與欄位限制。
