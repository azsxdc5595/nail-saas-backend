# ADR-0001：以版本控制的 Markdown 作為主文件

- **狀態：** Accepted
- **日期：** 2026-07-19

## Context

Nail SaaS 已跨越單一 CRUD 規模，包含帳號、店家、美甲師、作品、時段、黑名單、預約與評價等領域。若設計只存在聊天紀錄、程式碼或分散 Excel，後續多人協作將無法可靠理解需求與架構。

## Decision

以 repository 的 `docs/` Markdown 文件作為產品、架構、資料庫、API、流程、索引及 ADR 的詳細來源。Excel 可作為匯總與管理工具，但不是詳細設計的唯一來源。

文件優先序為：DDL > Java Entity；Project > code；User Flow > API implementation；ADR 不得由程式推翻。所有程式變更須完成文件影響檢查。

## Consequences

每項功能的設計期較長，但變更可追溯、可 code review、可搜尋，且新成員可由文件理解系統。現有程式需逐步補齊文件，未確認部分一律標為待確認。
