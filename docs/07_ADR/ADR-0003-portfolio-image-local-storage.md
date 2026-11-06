# ADR-0003：作品貼文圖片使用本機檔案系統

- **狀態：** Accepted
- **日期：** 2026-11-06

## Context

`NAIL_POST_IMAGE` 保存每張作品圖片的儲存名稱、相對路徑、原始檔名、MIME 類型與檔案大小。前端上傳圖片時不應自行指定由伺服器產生的 metadata。

## Decision

- `/nailPost/add` 與 `/nailPost/edit` 使用 `multipart/form-data`，以 Spring `MultipartFile` 接收每張圖片。
- 圖片儲存於 `D:\Nail\postImage\{MANICURIST_CODE}\yyyyMMdd\`；檔名由時間戳與唯一識別值組成，並保留上傳檔案副檔名。
- `storageName`、`storagePath`、`contentType`、`fileSize` 由後端產生。MIME 類型依檔案簽章辨識，檔案大小依實際儲存檔案計算。
- `STORAGE_PATH` 保存相對於儲存根目錄的路徑。
- 資料庫交易回滾時移除本次新寫入檔案；貼文圖片替換或貼文刪除時，在資料庫交易提交後移除舊檔案。

## Consequences

應用程式執行環境必須能寫入指定磁碟路徑。此決策適用於目前單機儲存環境；若部署拓撲改為多節點，應另行評估共享檔案系統或 Object Storage，並建立 ADR。
