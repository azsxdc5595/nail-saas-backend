# Nail SaaS Development Rules

This repository contains a commercial SaaS product.

This document defines how AI assistants should understand, modify, and extend the project.

The project documentation is the single source of truth. Source code is an implementation of the documented design and must not redefine product behavior or architecture.

---

# Documentation Structure

The `docs/` directory contains all official project documentation.

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

Each directory has a specific responsibility and should remain independent.

---

# Source of Truth

Project documentation is authoritative.

When documentation and implementation conflict, documentation takes precedence unless the user explicitly approves changing the documentation.

Source of Truth priority:

1. Project Requirements
2. Architecture Decision Records (ADR)
3. System Architecture
4. User Flow
5. Database Design (DDL)
6. API Specification
7. Existing Implementation

Implementation never overrides approved documentation.

---

# Design Rules

## DDL is authoritative

The database DDL defines the canonical schema.

Never:

- invent new columns
- rename existing columns
- remove columns
- change data types
- change relationships

to satisfy existing code.

If Entity and DDL differ:

DDL wins.

Report the inconsistency and update the implementation unless instructed otherwise.

---

## Product Requirements

Product requirements define expected behavior.

Do not weaken business rules because the current implementation behaves differently.

If implementation conflicts with documented requirements:

Treat the implementation as incorrect until confirmed otherwise.

---

## User Flow

Business flow is authoritative.

Do not:

- bypass required steps
- skip approvals
- merge independent states
- simplify documented workflows

only because implementation is easier.

---

## Architecture Decision Records

Architecture Decision Records are binding.

Never replace an approved architecture because another implementation appears simpler.

If an ADR exists:

Follow it.

---

# Required Workflow

Before implementing any feature:

1. Read the relevant Project documentation.
2. Read System Architecture.
3. Read related ADRs.
4. Read User Flow.
5. Read DDL.
6. Read API documentation.
7. Inspect existing implementation.
8. Report documentation conflicts before changing code.

Never assume undocumented business behavior.

---

# Documentation Impact Check

Every code change requires a documentation review.

When implementation changes:

| Change | Documentation |
|---------|---------------|
| Product behavior | Project / UserFlow |
| Database schema | DDL / Database |
| API | API documentation |
| Architecture | ADR |
| Index | Index documentation |

If documentation does not require updating:

Explicitly state why.

Never silently skip documentation review.

---

# Database Rules

Treat the DDL as the canonical schema.

When modifying persistence:

- evaluate indexes
- evaluate constraints
- evaluate transactions
- evaluate locking
- evaluate query performance

Avoid schema changes that exist only to satisfy application code.

---

# Performance Review

Before introducing new queries or business logic, evaluate:

- N+1 queries
- unnecessary joins
- index usage
- full table scans
- transaction scope
- lock contention
- pagination
- batch processing

Performance should be considered before implementation, not after deployment.

---

# Spring Boot Conventions

Controllers should remain thin.

Business logic belongs in Services.

Repositories should contain persistence logic only.

Prefer:

- constructor injection
- immutable DTOs where practical
- transaction boundaries in Service layer
- validation before business logic

Avoid putting business rules inside Controllers.

---

# Vue Frontend Conventions

Frontend should consume documented APIs only.

Do not introduce frontend-only business rules.

Validation may improve user experience but must not replace backend validation.

---

# Naming Conventions

Follow existing project naming.

Do not introduce inconsistent naming styles.

Respect existing:

- package structure
- module boundaries
- DTO naming
- Repository naming
- Service naming
- API naming

Consistency is preferred over personal preference.

---

# AI Responsibilities

Before writing code:

- understand the requirement
- verify assumptions
- identify conflicts
- explain significant design decisions
- minimize unrelated modifications

Never:

- invent business rules
- invent database fields
- invent APIs
- silently change architecture
- silently change DDL

If information is missing:

Ask.

Do not guess.

---

# Commercial SaaS Principles

This project targets production quality.

Every implementation should consider:

- maintainability
- scalability
- extensibility
- performance
- security
- readability
- consistency

Prefer long-term maintainability over short-term convenience.

---

# Code Review Checklist

When reviewing code, verify:

- Documentation consistency
- DDL consistency
- API consistency
- User Flow consistency
- ADR consistency
- Naming consistency
- Performance
- Security
- Transaction boundaries
- Exception handling

Review architecture before reviewing syntax.

---

# Git Rules

Keep commits focused.

Avoid unrelated modifications.

Preserve existing behavior unless intentionally changing requirements.

Never overwrite unrelated user changes.

---

# Final Principle

Documentation defines the product.

Architecture defines the system.

DDL defines the data model.

Implementation realizes those decisions.

Code is never the source of truth.