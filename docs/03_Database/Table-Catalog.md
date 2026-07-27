# Table 目錄（已由 Oracle DDL 驗證）

|Table|存在目的／目前推定關係|正式 Schema 摘要|狀態|
|---|---|---|---|
|USER_ACCOUNT|會員帳號|PK USER_ID；USER_CODE、EMAIL 唯一|已驗證|
|EMAIL_VERIFICATION|信箱驗證紀錄|PK ID；USER_CODE、EMAIL、STATUS、驗證碼與期限|已驗證|
|REFRESH_TOKEN|登入刷新 Token|PK ID；USER_CODE、TOKEN、期限|已驗證|
|ADDRESS|店家地址代碼|PK ADDRESS_ID；ADDRESS_CODE 唯一|已驗證|
|SHOP|店家|FK ADDRESS_ID → ADDRESS|已驗證|
|SHOP_INVITE_CODE|店家邀請碼|PK CODE；FK SHOP_ID → SHOP；STATUS Check|已驗證|
|MANICURIST|美甲師與帳號／店家關係|FK SHOP_ID、USER_ID；USER_ID 唯一|已驗證|
|RESERVATION_BLOCK_TIME|美甲師不可預約時段|時段合法性與 BLOCK_TYPE Check；複合查詢索引|已驗證|
|MANICURIST_BLACKLIST|美甲師封鎖會員|僅 PK；尚無 FK／重複防護|已驗證，待設計|
|MANICURIST_WORK|美甲師作品集|僅 PK；尚無 FK|已驗證，待設計|
|NAIL_SAMPLE|美甲作品範例|含 STYLE/SEASON/COLOR code、ENABLED、時間欄位|已驗證，Entity 不一致|
|SERVICE_ITEM|美甲師服務項目|FK MANICURIST_ID → MANICURIST；SERVICE_CODE 唯一|已驗證|
|PROMOTION|美甲師促銷活動|僅 PK；尚無 FK|已驗證，待設計|
|RESERVATION|會員預約|以 RESERVATION_DATE + START_SLOT_ID + SLOT_COUNT 表示時段|已驗證，Entity 不一致|
|SERVICE_ITEM|預約服務明細|複合 PK；FK Reservation、Service Item|已驗證|
|REVIEW|評價|僅 PK；尚無 FK／唯一限制|已驗證，待設計|

> `MANICURIST_OFF_DAY` 僅存在 Entity，Oracle schema 沒有此 Table；`NAIL_SAMPLE` 與 `MANICURIST_WORK` 的角色是否重複，也尚未決定。兩者皆不可在未確認前直接修改。
