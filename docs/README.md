# Nail SaaS Documentation

本目錄為 Nail SaaS 專案的設計文件，包含產品需求、系統架構、資料庫設計、API 規格、使用者流程、架構決策與相關技術文件。

所有文件皆採用 Markdown（`.md`）格式，並與程式碼一同納入 Git 版本控制，作為專案開發、維護及協作的依據。

---

## Source of Truth

本目錄為 Nail SaaS 專案的唯一設計依據（Source of Truth）。

新增功能、修改功能或重構程式時，應優先確認相關文件，再進行實作。

若程式碼與文件內容不一致，應先確認設計是否變更，再決定修改程式碼或更新文件。

---

## Repository Structure

```text
docs/
├── 01_Project
├── 02_Architecture
├── 03_Database
├── 04_API
├── 05_UserFlow
├── 06_Index
├── 07_ADR
└── 08_Management
```

---

## Reading Order

建議依照以下順序閱讀：

1. Project
2. System Architecture
3. Module Catalog
4. User Flow
5. Architecture Decision Record (ADR)
6. Database Design
7. Index Design
8. API Design
9. Management / TODO

---

## Directory Overview

| Directory | Description |
|-----------|-------------|
| 01_Project | Product requirement, roadmap and module planning |
| 02_Architecture | System architecture and technical architecture |
| 03_Database | DDL, table design, ER diagram and schema |
| 04_API | API specification |
| 05_UserFlow | User flow and business flow |
| 06_Index | Index strategy and SQL performance |
| 07_ADR | Architecture Decision Records |
| 08_Management | TODO and project management |

---

## Document Status

| Status | Description |
|--------|-------------|
| Confirmed | Requirement and design have been confirmed. |
| Implementation Review | Extracted from existing implementation and awaiting design confirmation. |
| Planned | Included in roadmap but not yet designed. |
| Pending | Requirement or design is not yet confirmed. |

---

## Development Workflow

Requirement Analysis

↓

User Flow

↓

Architecture Decision Record (ADR)

↓

Database Design (DDL)

↓

Index Design

↓

API Design

↓

Entity / DTO

↓

Repository

↓

Service

↓

Controller

↓

Frontend

↓

Test Case

---

## Documentation Principles

1. Documentation First
2. Source of Truth
3. Code Follows Design
4. DDL Is Database Authority
5. Update Documentation Together With Code

---

## Excel Usage

Excel 僅用於管理性資料：

- Roadmap
- Table List
- API List
- Index List
- TODO
- Progress Tracking

所有正式設計內容皆以本目錄文件為準。

---

## Document Information

| Item | Value |
|------|------|
| Project | Nail SaaS |
| Documentation Version | v1.0.0 |
| Repository | Backend / docs |
| Maintainer | Project Team |
| Last Updated | 2026-07-19 |
| Status | Active |