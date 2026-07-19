# 資料庫設計

**權威來源：** Oracle DDL。Java Entity 僅為目前實作盤點，不能取代 DDL。

目前 repository 未包含 DDL migration 或建表 SQL。2026-07-19 已以唯讀方式從本機 Oracle `NSAS` schema 擷取實際資料字典；結果記錄於 [Schema 基線](NSAS-Schema-2026-07-19.md)，並以其取代先前的 Entity 推測。後續資料庫異動必須同時提交可重建的 DDL migration／SQL，不可只改資料庫或 Entity。

每張正式 Table 文件必須說明：存在目的、欄位／型別／nullable、PK/FK、索引、查詢情境、CRUD/API、商業限制與變更紀錄。
