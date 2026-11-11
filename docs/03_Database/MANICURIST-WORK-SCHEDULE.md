# Manicurist Work Schedule

## Status

Proposed. The DDL is present in `NSAS-Schema-Rebuild.sql`; it has not been verified against the live Oracle schema, and the API and Service workflow remain to be designed.

## Purpose

Store a manicurist's actual working intervals for a specific calendar date. A row represents one continuous interval. A day with a break can contain multiple rows; a day off has no rows. Reservation availability is calculated from these intervals after excluding active reservations.

## Columns

| Column | Type | Nullable | Description |
|---|---|---:|---|
| `SCHEDULE_ID` | `NUMBER` | No | Primary key. |
| `MANICURIST_ID` | `NUMBER` | No | Manicurist who owns the interval. |
| `WORK_DATE` | `DATE` | No | Calendar date; the time portion must be midnight. |
| `START_MINUTE_OF_DAY` | `NUMBER(4)` | No | Start minute after midnight, from 0 through 1439. |
| `END_MINUTE_OF_DAY` | `NUMBER(4)` | No | Exclusive end minute after midnight, from 1 through 1440. |
| `CREATE_TIME` | `TIMESTAMP` | No | Creation timestamp. |
| `UPDATE_TIME` | `TIMESTAMP` | Yes | Last update timestamp. |

## Constraints

- `SCHEDULE_ID` is the primary key and is generated from `SEQ_MANICURIST_WORK_SCHEDULE`.
- `MANICURIST_ID` references `MANICURIST(MANICURIST_ID)`.
- `WORK_DATE` contains a date only.
- Start and end minutes are in range, and the end must be after the start.
- Overlapping intervals for the same manicurist and date must be rejected by the Service layer; the Oracle DDL does not enforce interval overlap.
- Schedule changes must be rejected when they conflict with reservations that still occupy time, including unexpired pending requests.
- Schedule replacement locks the owning `MANICURIST` row. Reservation submission must use the same lock before checking availability so a concurrent booking cannot race a schedule update.

## Query and Index

The primary lookup is the work intervals for one `MANICURIST_ID` on one `WORK_DATE`. The rebuild DDL includes `IDX_MANI_WORK_SCHEDULE_QRY` on `(MANICURIST_ID, WORK_DATE)`. Validate its use with the final Availability SQL and Oracle execution plan after deployment.

## API Mapping

The backend contract is documented in `docs/04_API/Manicurist-Work-Schedule.md`. It supports querying and replacing all intervals for one date. Member-facing availability search remains part of the Reservation API design.

## Change History

- 2026-11-11: Proposed replacing `RESERVATION_BLOCK_TIME` with date-specific manicurist work intervals.
