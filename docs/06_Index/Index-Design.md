# Index Design

**原則：** 先以查詢情境設計索引，再建表；DDL 是最終權威。現行索引已由 Oracle 驗證，詳見 [Schema 基線](../03_Database/NSAS-Schema-2026-07-19.md)；下表保留「查詢需要」與缺口，而非把候選索引誤寫成既有索引。

|Table|查詢情境|程式可見條件|候選索引|狀態|
|---|---|---|---|---|
|USER_ACCOUNT|登入／查帳號|EMAIL、USER_CODE、USER_NAME|EMAIL unique；USER_CODE unique；視需求 USER_NAME|待 DDL／負載確認|
|EMAIL_VERIFICATION|取最新待驗證碼|EMAIL + STATUS + CREATE_TIME|現有 `(USER_CODE, EMAIL, STATUS)`；檢查是否需 CREATE_TIME|待執行計畫|
|REFRESH_TOKEN|以 token 刷新／刪除帳號 token|TOKEN；USER_CODE|TOKEN unique；USER_CODE|現有索引缺口|
|MANICURIST|以帳號判定美甲師|USER_ID|USER_ID|待 DDL|
|SHOP|由會員尋找所屬店家|MANICURIST.USER_ID → SHOP_ID|MANICURIST(USER_ID, SHOP_ID)|待 DDL|
|SHOP_INVITE_CODE|驗證邀請碼／查有效店家邀請碼|CODE；SHOP_ID + STATUS|CODE PK；`(SHOP_ID, STATUS)`|待 DDL|
|MANICURIST_BLOCK_TIME|列出美甲師不可預約時段|MANICURIST_ID|現有 `(MANICURIST_ID, START_TIME, END_TIME)`|已存在，待驗證|
|MANICURIST_BLACKLIST|美甲師名單、判斷是否封鎖|MANICURIST_ID；MANICURIST_ID + USER_ID|MANICURIST_ID；`(MANICURIST_ID, USER_ID)` unique|現有索引缺口|
|NAIL_SAMPLE|列出美甲師作品|MANICURIST_ID|MANICURIST_ID|現有索引缺口|

建立索引前必須記錄預期 SQL、資料量、讀寫頻率、唯一性與 `EXPLAIN PLAN` 結果；禁止只因欄位像外鍵就無條件建立索引。
