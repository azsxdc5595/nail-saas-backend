# Reservation Block Time API

## Purpose

管理美甲師不可預約時段。

## Authorization

所有端點需登入。新增與移除只可操作登入者自己的時段；新增時美甲師 ID 由登入身分解析。

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/reservationBlockTime/getBlockTime` | 依美甲師查詢不可預約時段 |
| POST | `/reservationBlockTime/add` | 新增登入美甲師的不可預約時段 |
| DELETE | `/reservationBlockTime/remove` | 移除登入美甲師自己的時段 |

## Request Validation

- 新增：`startTime`、`endTime` 必填且結束時間需晚於開始時間；`blockType` 限 `OFF`、`REST`、`BOOKED`、`MAINTAIN`；`reason` 最多 255 字。
- 移除：`blockId` 必填。

## Related Tables

`RESERVATION_BLOCK_TIME`, `MANICURIST`

## Change History

- 2026-10-30：對齊 DDL 的 BLOCK_TYPE 值並記錄寫入者權限與限制。
