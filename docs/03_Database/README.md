# 資料庫設計

**權威來源：** Oracle DDL。Java Entity 僅為目前實作盤點，不能取代 DDL。

目前採用最新提供的 [NSAS Schema 重建 SQL](NSAS-Schema-Rebuild.sql) 作為資料模型基準；該腳本包含 `NAIL_POST` 與一對多的 `NAIL_POST_IMAGE`。既有 Schema Baseline 為較早快照，遇到衝突時以此重建腳本為準。後續資料庫異動必須同步更新重建 SQL 與詳細 Table 文件，不可只改資料庫或 Entity。

每張正式 Table 文件必須說明：存在目的、欄位／型別／nullable、PK/FK、索引、查詢情境、CRUD/API、商業限制與變更紀錄。
