# Portfolio API

## Purpose

建立、修改、刪除、搜尋及查看美甲師作品貼文。每篇貼文可以關聯多筆圖片 metadata。

## Endpoint

Controller base path: `/nailPost`

| Method | Path | Purpose | Success response |
| --- | --- | --- | --- |
| GET | `/nailPost/searchMyNailPost` | 美甲師查看自己的貼文（含停用貼文） | `SearchNailPostResponse` |
| POST | `/nailPost/add` | 建立貼文及可選的圖片 metadata | Empty body |
| PATCH | `/nailPost/edit` | 修改貼文及可選擇替換圖片 metadata | Empty body |
| DELETE | `/nailPost/remove` | 刪除自己的貼文 | Empty body |
| POST | `/nailPost/search` | 搜尋啟用貼文 | `SearchNailPostResponse` |
| POST | `/nailPost/detail` | 查看啟用貼文及其全部圖片 | `GetNailPostDetailResponse` |

## Authorization

所有端點目前均受 JWT authentication 保護。建立貼文要求目前使用者可查得美甲師資料；修改及刪除會比對貼文所屬美甲師。搜尋及明細僅回傳啟用貼文。

## Request

新增 Request 使用 `ApplyNailPostRequest`；更新使用 `UpdateNailPostRequest`，其中 `postId` 必填。兩者的貼文欄位為 `price`、`description`、`styleCode`、`seasonCode`、`mainColorCode`、`enabled`。`images` 是可選清單，每筆圖片 metadata 欄位為：`storageName`、`storagePath`、`originalName`、`contentType`、`fileSize`、`sortOrder`、`isCover`。圖片 ID 由資料庫產生。

更新貼文欄位為 PATCH 語意：未提供或為 null 的貼文欄位維持原值（目前不能用 null 清空可空欄位）。`images` 未提供或為 null 時保留舊圖片；空陣列代表移除所有圖片 metadata；非空陣列代表完整替換圖片 metadata 清單。

移除及明細 Request 使用 `postId`。搜尋 Request 欄位及分頁條件依 `SearchNailPostRequest`。

## Response

- 搜尋清單每篇貼文包含 `coverImage`；有標記封面時回傳該圖，否則回傳排序最前圖片，無圖片時為 null。
- 明細回傳依 `sortOrder`、`imageId` 排序的全部圖片 metadata。現有 `reservable` 欄位仍會回傳 null，因預約可用性規則尚未定義。
- 圖片 metadata 欄位為 `imageId`、`storageName`、`storagePath`、`originalName`、`contentType`、`fileSize`、`sortOrder`、`isCover`。
- 建立、修改及刪除目前回傳空 body。

## Validation

- `postId` 必填。
- `enabled` 可省略，提供時只接受 0 或 1。
- `price` 最多 8 位整數及 2 位小數；描述最多 500 字元，風格、季節及主色代碼各最多 20 字元。
- 圖片 `storageName`、`storagePath`、`contentType` 不可空白；`fileSize` 必填且不得小於 0。
- 圖片欄位長度依 DDL 限制：儲存名稱與原始檔名最多 255 字元、路徑最多 500 字元、MIME 類型最多 100 字元。
- `sortOrder` 可省略，省略時依清單位置從 1 起算；提供時須為 1 至 99999。
- `isCover` 可省略，省略時預設 0；提供時只接受 0 或 1。
- 格式驗證失敗回傳 `REQUEST_001`。

## Error Codes / HTTP Status

| Error | HTTP | Meaning |
| --- | --- | --- |
| `REQUEST_001` | 400 | Request validation failed |
| `NAIL_POST_001` | 400 | 貼文不存在或未啟用 |
| `PERMISSION_001` | 403 | 目前使用者無權操作此貼文 |
| Authentication failure | 401 | JWT 無效或缺少 |

## Business Rules

- 新增貼文及其圖片 metadata 在同一 Service transaction 內保存。
- 修改及刪除只限貼文所屬美甲師。
- 刪除貼文由資料庫 FK cascade 刪除圖片 metadata。
- Schema 未限制封面唯一性或每篇貼文最少圖片數量。
- 此 API 尚未實作圖片二進位上傳、讀取或檔案刪除；目前僅處理資料庫 metadata。`storageName` 與 `storagePath` 由目前 Request 傳入，應在接上正式上傳流程前重新檢視其可信來源。

## Related Tables / User Flow

- Tables: `NAIL_POST`, `NAIL_POST_IMAGE`
- User Flow: [Manicurist Portfolio Management](../05_UserFlow/Core-Flows.md#manicurist-portfolio-management)
- Database: [NAIL_POST and NAIL_POST_IMAGE](../03_Database/NAIL-POST.md)

## Change History

| Date | Change |
| --- | --- |
| 2026-10-30 | 對齊多圖貼文 schema，新增圖片 metadata request/response、排序、封面及圖片清單更新語意。 |
