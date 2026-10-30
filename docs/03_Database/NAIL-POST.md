# NAIL_POST 與 NAIL_POST_IMAGE

## 權威資料模型

欄位、序列、PK/FK、檢查條件及索引以 [NSAS Schema 重建 SQL](NSAS-Schema-Rebuild.sql) 為準。較早的 Schema Baseline 尚未列出 `NAIL_POST_IMAGE`；衝突時以重建 SQL 為準。

## NAIL_POST

用途：儲存美甲師作品貼文及作品屬性。主鍵 `POST_ID` 使用 `SEQ_NAIL_POST`。DDL 未定義 MANICURIST_ID 外鍵。

| Column | Oracle Type | Nullable | Notes |
| --- | --- | --- | --- |
| POST_ID | NUMBER | No | Primary key |
| MANICURIST_ID | NUMBER | Yes | 發布者識別碼 |
| PRICE | NUMBER(10,2) | Yes | 參考價格 |
| DESCRIPTION | VARCHAR2(500) | Yes | 貼文描述 |
| STYLE_CODE | VARCHAR2(20) | Yes | 風格代碼 |
| SEASON_CODE | VARCHAR2(20) | Yes | 季節代碼 |
| MAIN_COLOR_CODE | VARCHAR2(20) | Yes | 主色代碼 |
| ENABLED | NUMBER(1) | No | 是否啟用 |
| CREATE_TIME | TIMESTAMP | Yes | 建立時間 |
| UPDATE_TIME | TIMESTAMP | Yes | 更新時間 |

## NAIL_POST_IMAGE

用途：保存一篇貼文的多筆圖片 metadata。主鍵 `IMAGE_ID` 使用 `SEQ_NAIL_POST_IMAGE`。

| Column | Oracle Type | Nullable | Notes |
| --- | --- | --- | --- |
| IMAGE_ID | NUMBER | No | Primary key |
| POST_ID | NUMBER | No | FK → `NAIL_POST.POST_ID`；刪除貼文時 cascade |
| STORAGE_NAME | VARCHAR2(255) | No | 系統產生的儲存檔名 |
| STORAGE_PATH | VARCHAR2(500) | No | 實際儲存相對路徑 |
| ORIGINAL_NAME | VARCHAR2(255) | Yes | 使用者原始檔名 |
| CONTENT_TYPE | VARCHAR2(100) | No | MIME 類型 |
| FILE_SIZE | NUMBER(19) | No | Bytes；須大於或等於 0 |
| SORT_ORDER | NUMBER(5) | No | 預設 1；須大於或等於 1 |
| IS_COVER | NUMBER(1) | No | 預設 0；只允許 0 或 1 |
| CREATE_TIME | TIMESTAMP | Yes | 建立時間 |
| UPDATE_TIME | TIMESTAMP | Yes | 更新時間 |

索引 `IDX_NAIL_POST_IMAGE_POST_SORT` 位於 `(POST_ID, SORT_ORDER)`，支援依貼文排序讀取圖片。Schema 未限制每篇貼文的封面唯一性，也未規定貼文至少要有一張圖片。

## 查詢及交易考量

貼文搜尋先分頁，再以單次 `POST_ID IN (...)` 查詢該頁圖片，避免逐篇貼文查詢造成 N+1。新增貼文及圖片 metadata、更新貼文及圖片 metadata 均在 Service 交易內完成。貼文刪除依 FK cascade 刪除圖片資料列。

主要 CRUD/API 情境：美甲師新增、修改及刪除自己的貼文；使用者搜尋啟用貼文及查看啟用貼文明細；貼文明細依 `SORT_ORDER`、`IMAGE_ID` 讀取圖片。

## API 關聯

見 [Portfolio API](../04_API/Portfolio.md)。資料庫只保存 metadata；二進位檔案的上傳、讀取與刪除服務尚未由目前架構決策定義。

## Change History

| Date | Change |
| --- | --- |
| 2026-10-30 | 依最新 NSAS 重建 SQL 記錄貼文及一對多圖片資料模型。 |
