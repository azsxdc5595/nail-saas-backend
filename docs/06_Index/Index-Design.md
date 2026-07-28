# Index Design

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Version | v1.0.0 |
| Status | Review Required |
| Last Updated | 2026-07-28 |

---

## Purpose

本文件定義 Nail SaaS 的索引設計原則及各資料表的查詢需求。

索引設計應以實際查詢情境為依據，而非依資料表結構或外鍵自動建立。

本文件描述索引需求與設計方向，不代表所有候選索引皆已存在於正式資料庫。

---

## Design Principles

索引設計應遵循以下原則：

- 先分析查詢情境，再設計索引。
- DDL 為資料庫結構唯一權威。
- 優先依據實際 SQL 建立索引。
- 避免建立未被使用的索引。
- 建立索引前應完成執行計畫分析（EXPLAIN PLAN）。
- 索引設計應兼顧查詢效能與寫入成本。
- 不得因欄位為外鍵而自動建立索引。

---

## Current Index Baseline

目前正式存在之索引，請參閱：

- `NSAS-Schema-Baseline.md`

本文件僅描述索引需求與設計方向，不重複列出 Oracle Data Dictionary 已存在之索引。

---

## Index Design

| Table | Query Scenario | Search Condition | Candidate Index | Status |
|------|----------------|------------------|-----------------|--------|
| USER_ACCOUNT | 登入、查詢會員 | EMAIL、USER_CODE、USER_NAME | EMAIL (Unique)、USER_CODE (Unique)、USER_NAME（視需求） | Review Required |
| EMAIL_VERIFICATION | 查詢最新待驗證紀錄 | EMAIL、STATUS、CREATE_TIME | `(USER_CODE, EMAIL, STATUS)`；評估是否加入 `CREATE_TIME` | Review Required |
| REFRESH_TOKEN | Refresh Token 驗證、刪除 Token | TOKEN、USER_CODE | TOKEN (Unique)、USER_CODE | Review Required |
| MANICURIST | 依會員取得美甲師 | USER_ID | USER_ID | Review Required |
| SHOP | 查詢會員所屬店家 | USER_ID → SHOP_ID | `MANICURIST(USER_ID, SHOP_ID)` | Review Required |
| SHOP_INVITE_CODE | 驗證邀請碼、查詢有效邀請碼 | CODE、SHOP_ID、STATUS | CODE（PK）、`(SHOP_ID, STATUS)` | Review Required |
| RESERVATION_BLOCK_TIME | 查詢不可預約時段 | MANICURIST_ID、START_TIME、END_TIME | `(MANICURIST_ID, START_TIME, END_TIME)` | Verified |
| MANICURIST_BLACKLIST | 查詢黑名單、判斷是否封鎖 | MANICURIST_ID、USER_ID | MANICURIST_ID、`(MANICURIST_ID, USER_ID)`（Unique） | Review Required |
| NAIL_SAMPLE | 查詢美甲師作品 | MANICURIST_ID | MANICURIST_ID | Review Required |

---

## Index Evaluation Criteria

新增索引前，至少應完成以下評估：

- 預期 SQL。
- 查詢條件（WHERE Clause）。
- 排序條件（ORDER BY）。
- JOIN 條件。
- 預估資料量。
- 讀寫比例。
- 唯一性需求。
- EXPLAIN PLAN 分析結果。

未完成上述評估，不得建立正式索引。

---

## Index Design Guidelines

索引設計應遵循以下原則：

- 優先建立符合高頻查詢的索引。
- 避免建立重複或功能相近的索引。
- 複合索引應依查詢條件排序設計。
- 不應建立長期未使用的索引。
- 寫入頻繁的資料表應控制索引數量。
- 唯一索引僅用於具有唯一性需求之欄位。

---

## Future Improvements

目前規劃但尚未完成：

- 建立 Index Naming Standard。
- 建立 Index Review Checklist。
- 建立 SQL Performance Benchmark。
- 建立 Execution Plan Review 流程。
- 建立 Index Usage Analysis 機制。

---

## Related Documents

| Document | Purpose |
|----------|---------|
| NSAS-Schema-Baseline.md | Oracle Schema 基線。 |
| Table-Catalog.md | 資料表總覽。 |
| System-Architecture.md | 系統架構。 |
| ADR | 架構決策紀錄。 |

---

## Notes

- 本文件描述索引設計原則與需求，不代表資料庫已建立所有候選索引。
- Oracle 實際索引應以 `NSAS-Schema-Baseline.md` 為準。
- 新增、修改或移除索引時，應同步更新本文件。
- 所有索引變更皆應經過效能驗證與執行計畫分析。