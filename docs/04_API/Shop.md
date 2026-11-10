# Shop API

## Purpose

建立店家、查詢登入者所屬店家、產生邀請碼及加入店家。

## Authorization

所有端點需登入。系統角色授權如下：Create / Join 需 `MEMBER` 或 `ADMIN`；Me / Invite 需 `MANICURIST`、`SHOP_MANAGER` 或 `ADMIN`。建立邀請碼仍由 Service 檢查店內職務 `OWNER` 或 `ADMIN`。

## Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/shop/create` | 建立店家 |
| GET | `/shop/me` | 查詢目前所屬店家 |
| POST | `/shop/invite` | 取得有效邀請碼或建立新邀請碼 |
| POST | `/shop/join` | 使用邀請碼加入店家 |

## Request Validation

- Create: `shopName`、`address`、`phone` 必填，長度上限分別 255、255、50；`description` 最多 500。
- Join: `inviteCode` 必填且最多 50。

## Error Responses

- 已登入但缺少端點所需系統角色時，回傳 HTTP 403，錯誤碼 `PERMISSION_001`，訊息「無權限操作此資源」。

## Business Rules

- Invite 權限由登入者所屬 Manicurist 的角色判斷，不接受客戶端指定角色或店家。
- 邀請碼有效期限為 5 分鐘，成功加入後只能使用一次。
- `/shop/create` 成功保存 Manicurist 後，指派帳戶系統角色 `SHOP_MANAGER` 與 `MANICURIST`。
- `/shop/join` 成功保存 Manicurist 後，指派帳戶系統角色 `MANICURIST`；保留帳戶既有其他系統角色。
- Manicurist 資料與對應角色指派在同一 Service transaction 內提交。

## Related Tables

`SHOP`, `MANICURIST`, `SHOP_INVITE_CODE`, `ADDRESS`, `USER_ROLE`

## Change History

- 2026-10-30：新增邀請碼 OWNER / ADMIN 權限及欄位長度規格。
- 2026-11-08：定義建立店家及加入店家成功後的帳戶系統角色指派。
- 2026-11-10：明確定義系統角色授權拒絕時的 403 錯誤回應。
