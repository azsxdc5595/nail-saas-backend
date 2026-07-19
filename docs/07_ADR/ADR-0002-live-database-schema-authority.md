# ADR-0002：現行 Oracle Schema 為資料模型權威

- **狀態：** Accepted
- **日期：** 2026-07-19

## Context

專案未保存 DDL/DML 文件，而 Java Entity 與實際 Oracle `NSAS` schema 已出現欄位、主鍵策略與資料模型差異。

## Decision

在可重建的 DDL migration 納入 repository 前，以現行 Oracle `NSAS` schema 的資料字典作為唯一資料模型權威。Entity、DTO 與資料存取實作必須向 Schema 對齊；不得為配合舊程式而修改資料庫。

## Consequences

`NAIL_SAMPLE`、`RESERVATION`、`RESERVATION_SERVICE` 與主鍵產生策略已依實際 Schema 修正。未來所有資料庫變更必須同時提交 migration、詳細 Table 文件與 Index Design；DML 僅保存可重複執行且有用途說明的 seed data。
