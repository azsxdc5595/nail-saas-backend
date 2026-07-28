# NSAS Schema Baseline

## Document Information

| Item         | Value      |
| ------------ | ---------- |
| Project      | Nail SaaS  |
| Version      | v1.1.0     |
| Status       | Verified   |
| Last Updated | 2026-07-28 |

---

## Purpose

本文件記錄目前 Oracle `NSAS` Schema 的正式基線（Baseline）。

內容來源為 Oracle Data Dictionary，作為目前資料模型唯一權威來源，直到可重建的 Migration DDL 納入 Repository 為止。

本文件僅描述 Schema 結構，不包含商業規則與 API 設計。

---

## Baseline Information

| Item         | Value                                                      |
| ------------ | ---------------------------------------------------------- |
| Capture Date | 2026-07-28                                                 |
| Schema       | NSAS                                                       |
| Source       | USER_TABLES、USER_TAB_COLUMNS、USER_CONSTRAINTS、USER_INDEXES |
| Type         | Oracle Data Dictionary Snapshot                            |

---

# Table Columns

| Table                  | Columns                                                                                                                                                                                                                                                                                                                                                |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| ADDRESS                | ADDRESS_ID NUMBER!, ADDRESS_CODE VARCHAR2(36), CITY VARCHAR2(100)!, DISTRICT VARCHAR2(100)!                                                                                                                                                                                                                                                            |
| EMAIL_VERIFICATION     | ID NUMBER!, USER_CODE VARCHAR2(50)!, EMAIL VARCHAR2(100)!, VERIFY_CODE VARCHAR2(10)!, EXPIRE_TIME TIMESTAMP!, STATUS VARCHAR2(20), FAIL_COUNT NUMBER, CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP                                                                                                                                                     |
| MANICURIST             | MANICURIST_ID NUMBER!, MANICURIST_CODE VARCHAR2(36), SHOP_ID NUMBER!, USER_ID NUMBER!, INTRO VARCHAR2(500), CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP, STATUS VARCHAR2(10)!, DISPLAY_NAME VARCHAR2(50)!, ROLE VARCHAR2(10)!                                                                                                                         |
| MANICURIST_BLACKLIST   | BLACKLIST_ID NUMBER!, MANICURIST_ID NUMBER, USER_ID NUMBER, REASON VARCHAR2(500), CREATE_TIME TIMESTAMP                                                                                                                                                                                                                                                |
| RESERVATION_BLOCK_TIME | BLOCK_ID NUMBER!, MANICURIST_ID NUMBER!, START_TIME TIMESTAMP!, END_TIME TIMESTAMP!, BLOCK_TYPE VARCHAR2(20)!, REASON VARCHAR2(255), CREATE_TIME TIMESTAMP!                                                                                                                                                                                            |
| MANICURIST_WORK        | WORK_ID NUMBER!, MANICURIST_ID NUMBER, IMAGE_URL VARCHAR2(500), DESCRIPTION VARCHAR2(500)                                                                                                                                                                                                                                                              |
| NAIL_SAMPLE            | SAMPLE_ID NUMBER!, MANICURIST_ID NUMBER, IMAGE_URL VARCHAR2(500), PRICE NUMBER(10,2), DESCRIPTION VARCHAR2(500), STYLE_CODE VARCHAR2(20), SEASON_CODE VARCHAR2(20), MAIN_COLOR_CODE VARCHAR2(20), ENABLED NUMBER(1)!, CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP                                                                                     |
| PROMOTION              | PROMOTION_ID NUMBER!, MANICURIST_ID NUMBER, TITLE VARCHAR2(255), DESCRIPTION VARCHAR2(500), DISCOUNT_PERCENT NUMBER, START_DATE DATE, END_DATE DATE, IS_ACTIVE NUMBER(1)                                                                                                                                                                               |
| REFRESH_TOKEN          | ID NUMBER!, USER_CODE VARCHAR2(50), TOKEN VARCHAR2(500), EXPIRE_TIME TIMESTAMP, CREATE_TIME TIMESTAMP                                                                                                                                                                                                                                                  |
| RESERVATION            | RESERVATION_ID NUMBER!, RESERVATION_CODE VARCHAR2(36), SHOP_ID NUMBER!, USER_ID NUMBER!, MANICURIST_ID NUMBER!, START_TIME TIMESTAMP!, END_TIME TIMESTAMP!, STATUS VARCHAR2(30)!, TOTAL_DURATION_MIN NUMBER(5)!, TOTAL_PRICE NUMBER(10)!, CUSTOMER_NOTE VARCHAR2(500), MANICURIST_NOTE VARCHAR2(500), CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP |
| RESERVATION_SERVICE    | RESERVATION_ID NUMBER!, SERVICE_SEQ NUMBER!, SERVICE_ID NUMBER, PRICE NUMBER(10,2), DURATION_MIN NUMBER, CREATE_TIME TIMESTAMP                                                                                                                                                                                                                         |
| REVIEW                 | REVIEW_ID NUMBER!, RESERVATION_ID NUMBER, REVIEWER_ID NUMBER, TARGET_ID NUMBER, RATING NUMBER, COMMENT_TEXT VARCHAR2(500), CREATE_TIME TIMESTAMP                                                                                                                                                                                                       |
| SERVICE_ITEM           | SERVICE_ID NUMBER!, SERVICE_CODE VARCHAR2(36), MANICURIST_ID NUMBER!, SERVICE_NAME VARCHAR2(200)!, PRICE NUMBER(10,2)!, DURATION_MIN NUMBER!, DESCRIPTION VARCHAR2(500), IS_ACTIVE NUMBER(1), CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP                                                                                                             |
| SHOP                   | SHOP_ID NUMBER!, SHOP_CODE VARCHAR2(36), SHOP_NAME VARCHAR2(255)!, ADDRESS_ID NUMBER!, PHONE VARCHAR2(50)!, DESCRIPTION VARCHAR2(500), CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP, ADDRESS VARCHAR2(255)!                                                                                                                                            |
| SHOP_INVITE_CODE       | CODE VARCHAR2(50)!, SHOP_ID NUMBER!, EXPIRE_TIME TIMESTAMP, STATUS VARCHAR2(20)!, CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP                                                                                                                                                                                                                         |
| USER_ACCOUNT           | USER_ID NUMBER!, USER_CODE VARCHAR2(36), USER_NAME VARCHAR2(50)!, PASSWORD VARCHAR2(200)!, EMAIL VARCHAR2(100)!, PHONE VARCHAR2(20)!, CREATE_TIME TIMESTAMP, UPDATE_TIME TIMESTAMP, VERIFIED VARCHAR2(1)!                                                                                                                                              |

---

# Constraints

## Foreign Keys

| Child Table                        | Parent Table               |
| ---------------------------------- | -------------------------- |
| SHOP.ADDRESS_ID                    | ADDRESS.ADDRESS_ID         |
| MANICURIST.SHOP_ID                 | SHOP.SHOP_ID               |
| MANICURIST.USER_ID                 | USER_ACCOUNT.USER_ID       |
| SHOP_INVITE_CODE.SHOP_ID           | SHOP.SHOP_ID               |
| SERVICE_ITEM.MANICURIST_ID         | MANICURIST.MANICURIST_ID   |
| RESERVATION.SHOP_ID                | SHOP.SHOP_ID               |
| RESERVATION.USER_ID                | USER_ACCOUNT.USER_ID       |
| RESERVATION.MANICURIST_ID          | MANICURIST.MANICURIST_ID   |
| RESERVATION_SERVICE.RESERVATION_ID | RESERVATION.RESERVATION_ID |
| RESERVATION_SERVICE.SERVICE_ID     | SERVICE_ITEM.SERVICE_ID    |

## Unique Constraints

* ADDRESS_CODE
* USER_CODE
* EMAIL
* SHOP_CODE
* MANICURIST_CODE
* SERVICE_CODE
* RESERVATION_CODE

## Check Constraints

| Table                  | Constraint                                |
| ---------------------- | ----------------------------------------- |
| SHOP_INVITE_CODE       | STATUS：ACTIVE / USED / EXPIRED            |
| RESERVATION_BLOCK_TIME | END_TIME > START_TIME                     |
| RESERVATION_BLOCK_TIME | BLOCK_TYPE：OFF / REST / BOOKED / MAINTAIN |
| RESERVATION            | END_TIME > START_TIME                     |

---

# Indexes

| Index                    | Table                  | Columns                             |
| ------------------------ | ---------------------- | ----------------------------------- |
| IDX_EMAIL_VERIFICATION_1 | EMAIL_VERIFICATION     | USER_CODE, EMAIL, STATUS            |
| IDX_EMAIL_VERIFICATION_2 | EMAIL_VERIFICATION     | EXPIRE_TIME                         |
| IDX_BLOCK_TIME_QUERY     | RESERVATION_BLOCK_TIME | MANICURIST_ID, START_TIME, END_TIME |
| IDX_RESERVATION_TIME     | RESERVATION            | MANICURIST_ID, START_TIME, END_TIME |
| IDX_RESERVATION_USER     | RESERVATION            | USER_ID                             |
| IDX_RESERVATION_SHOP     | RESERVATION            | SHOP_ID                             |
| IDX_INVITE_SHOP_ID       | SHOP_INVITE_CODE       | SHOP_ID                             |
| IDX_INVITE_STATUS        | SHOP_INVITE_CODE       | STATUS                              |

---

# Entity Alignment Status

| Severity | Description                                 | Resolution                     |
| -------- | ------------------------------------------- | ------------------------------ |
| Resolved | NAIL_SAMPLE Entity 缺少多個 DDL 欄位。             | 已依 DDL 完成修正。                   |
| Resolved | RESERVATION Entity 已改為 Time Interval Model。 | 已同步更新 DDL、Entity 與 Repository。 |
| Resolved | MANICURIST_OFF_DAY 無對應資料表。                  | 已移除 Entity。                    |
| P1       | 部分資料表缺少 FK。                                 | 待確認資料完整性策略。                    |
| Resolved | Entity 使用 IDENTITY。                         | 已改為 Oracle Sequence Mapping。   |
| Blocked  | Java 17 編譯驗證。                               | 待建置環境完成。                       |

---

# Notes

* 本文件為 Oracle Data Dictionary 快照。
* 本文件不包含商業規則與欄位用途說明。
* Schema 異動應以 Migration DDL 為準。
* Java Entity 必須與 DDL 保持一致。
* Reservation 已由 Slot Model（RESERVATION_DATE、START_SLOT_ID、SLOT_COUNT）調整為 Time Interval Model（START_TIME、END_TIME），作為後續預約功能的正式設計。