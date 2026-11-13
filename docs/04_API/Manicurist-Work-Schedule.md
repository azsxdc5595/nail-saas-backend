# Manicurist Work Schedule API

## Purpose

Manage one authenticated manicurist's actual working intervals for a specific date. This API manages work schedules; member-facing availability search belongs to the Reservation API.

## Authorization

All endpoints require an authenticated account with `MANICURIST`, `SHOP_MANAGER`, or `ADMIN` role. The service resolves the current manicurist from the authenticated account; clients cannot submit a `manicuristId`.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/manicuristWorkSchedule/{workDate}` | Get the current manicurist's intervals for one date. |
| PUT | `/manicuristWorkSchedule/{workDate}` | Replace all intervals for one date. |

`workDate` uses ISO date format `yyyy-MM-dd`.

## Request

`PUT /manicuristWorkSchedule/{workDate}` accepts the complete set of intervals for that date:

```json
{
  "intervals": [
    { "startMinuteOfDay": 600, "endMinuteOfDay": 720 },
    { "startMinuteOfDay": 780, "endMinuteOfDay": 1080 }
  ]
}
```

Minute values are counted from midnight (`600` = 10:00). `startMinuteOfDay` must be 0–1439; `endMinuteOfDay` must be 1–1440, and must be greater than the start. Intervals in one request may not overlap. An empty `intervals` array clears that date's schedule.

## Response

Both endpoints return the date and its intervals ordered by start minute:

```json
{
  "workDate": "2026-11-20",
  "intervals": [
    { "startMinuteOfDay": 600, "endMinuteOfDay": 720 },
    { "startMinuteOfDay": 780, "endMinuteOfDay": 1080 }
  ]
}
```

## Business Rules

- Replacing a date is atomic: the complete set is validated and saved in one transaction.
- The update is rejected if any `PENDING` or `CONFIRMED` reservation overlapping that date is not fully contained in the proposed continuous working time. Adjacent intervals are treated as continuous; intervals separated by a gap remain separate. An empty interval list is also rejected when such a reservation exists.
- Schedule intervals are not reservation slots. Reservation availability is calculated from these intervals, selected service duration, and occupied reservation intervals.
- Invalid minute values or overlapping intervals return a validation error; reservation conflicts return HTTP 409.

## Related Tables

`MANICURIST_WORK_SCHEDULE`, `MANICURIST`, `RESERVATION`

## Change History

- 2026-11-11: Replaced the Reservation Block Time contract with date-specific full-replacement schedule management.
