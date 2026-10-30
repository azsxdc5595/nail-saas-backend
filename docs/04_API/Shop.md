# Shop API

## Purpose

建立店家、查詢登入者所屬店家、產生邀請碼及加入店家。

## Authorization

所有端點需登入。建立邀請碼僅允許角色 `OWNER` 或 `ADMIN`。

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

## Business Rules

- Invite 權限由登入者所屬 Manicurist 的角色判斷，不接受客戶端指定角色或店家。
- 邀請碼有效期限為 5 分鐘，成功加入後只能使用一次。

## Related Tables

`SHOP`, `MANICURIST`, `SHOP_INVITE_CODE`, `ADDRESS`

## Change History

- 2026-10-30：新增邀請碼 OWNER / ADMIN 權限及欄位長度規格。
