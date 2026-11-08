# Portfolio API

## Purpose

建立、修改、刪除、搜尋及查看美甲師作品貼文。每篇貼文可以關聯多筆圖片 metadata。

## Endpoint

Controller base path: `/nailPost`

| Method | Path | Purpose | Success response |
| --- | --- | --- | --- |
| GET | `/nailPost/searchMyNailPost` | 美甲師查看自己的貼文（含停用貼文） | `SearchNailPostResponse` |
| POST | `/nailPost/add` | multipart/form-data 建立貼文並上傳多張圖片 | Empty body |
| PATCH | `/nailPost/edit` | multipart/form-data 修改貼文及可選擇替換圖片 | Empty body |
| DELETE | `/nailPost/remove` | 刪除自己的貼文 | Empty body |
| POST | `/nailPost/search` | 搜尋啟用貼文 | `SearchNailPostResponse` |
| POST | `/nailPost/detail` | 查看啟用貼文及其全部圖片 | `GetNailPostDetailResponse` |
| POST | `/nailPost/my/detail` | 美甲師查看自己的貼文明細（含未啟用貼文） | `GetNailPostDetailResponse` |
| GET | `/nailPost/image/{imageId}` | 取得公開啟用貼文圖片；美甲師可讀取自己未啟用貼文的圖片 | Image binary |

## Authorization

`POST /nailPost/search` 與 `POST /nailPost/detail` 為公開瀏覽端點，不要求 JWT，且只回傳啟用貼文。`POST /nailPost/my/detail`、查看自己的貼文清單、建立、修改及刪除需要 `MANICURIST`、`SHOP_MANAGER` 或 `ADMIN` 系統角色。個人明細可讀取啟用或未啟用貼文，但會比對目前使用者的美甲師身分與貼文所屬美甲師。建立貼文要求目前使用者可查得美甲師資料；修改及刪除也會比對貼文所屬美甲師。

## Request

新增 Request 使用 `ApplyNailPostRequest`；更新使用 `UpdateNailPostRequest`，其中 `postId` 必填。兩者的貼文欄位為 `price`、`description`、`styleCode`、`seasonCode`、`mainColorCode`、`enabled`，使用 `multipart/form-data` 傳送。多圖以 `images[0].file`、`images[1].file` 等欄位上傳；每張圖可帶 `images[i].originalName`、`images[i].sortOrder`、`images[i].isCover`。`file` 以 Spring `MultipartFile` 接收。`storageName`、`storagePath`、`contentType`、`fileSize` 均由後端儲存及解析產生，前端不得傳送。圖片 ID 由資料庫產生。

更新貼文欄位為 PATCH 語意：未提供或為 null 的貼文欄位維持原值（目前不能用 null 清空可空欄位）。`images` 未提供時保留舊圖片；傳送非空 `images[i].file` 清單時完整替換圖片；傳送 `clearImages=true` 並且不傳 `images` 時移除全部圖片。`clearImages=true` 不可與圖片清單同時使用。

移除及明細 Request 使用 `postId`。搜尋 Request 欄位及分頁條件依 `SearchNailPostRequest`。

## Response

- 搜尋清單每篇貼文包含 `coverImage`；有標記封面時回傳該圖，否則回傳排序最前圖片，無圖片時為 null。
- 明細回傳依 `sortOrder`、`imageId` 排序的全部圖片 metadata。清單封面及明細每張圖片皆包含可直接用於載入圖片的相對 `imageUrl`（例如 `/nailPost/image/123`）；前端應以 API 網域解析此路徑。圖片端點回傳實際檔案及其 MIME 類型；未啟用貼文圖片僅限所屬美甲師讀取。現有 `reservable` 欄位仍會回傳 null，因預約可用性規則尚未定義。
- 明細包含 `enabled` 狀態；公開明細只會回傳啟用貼文（`1`），作者明細可回傳 `0` 或 `1`，管理端應使用此欄位呈現及調整公開狀態。
- 圖片 metadata 欄位為 `imageId`、`storageName`、`storagePath`、`imageUrl`、`originalName`、`contentType`、`fileSize`、`sortOrder`、`isCover`。
- 建立、修改及刪除目前回傳空 body。

## Validation

- `postId` 必填。
- `enabled` 可省略，提供時只接受 0 或 1。
- `price` 最多 8 位整數及 2 位小數；描述最多 500 字元，風格、季節及主色代碼各最多 20 字元。
- 每筆圖片必須提供非空的 `file`；目前接受 JPEG、PNG、GIF、WebP、BMP，MIME 類型由檔案內容簽章判定，大小由已儲存檔案計算。
- `originalName` 最多 255 字元；資料庫儲存名稱與相對路徑符合 DDL 的 255 / 500 字元限制。
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
- 圖片寫入本機 `D:\Nail\postImage\{MANICURIST_CODE}\yyyyMMdd\`，檔名由後端時間戳加唯一識別值產生並保留副檔名；資料庫 `STORAGE_PATH` 儲存相對路徑。貼文交易回滾時清除新檔，替換或刪除貼文時於交易提交後刪除舊檔。
- 圖片透過 `GET /nailPost/image/{imageId}` 提供，不將本機檔案系統路徑暴露為可直接存取 URL；讀取公開啟用貼文圖片不需登入，讀取未啟用貼文圖片則需確認目前使用者為所屬美甲師，並限制檔案位於設定的儲存根目錄內。

## Related Tables / User Flow

- Tables: `NAIL_POST`, `NAIL_POST_IMAGE`
- User Flow: [Manicurist Portfolio Management](../05_UserFlow/Core-Flows.md#manicurist-portfolio-management)
- Database: [NAIL_POST and NAIL_POST_IMAGE](../03_Database/NAIL-POST.md)

## Change History

| Date | Change |
| --- | --- |
| 2026-11-05 | 開放作品搜尋與明細免登入；僅公開啟用貼文。 |
| 2026-11-06 | 新增及修改改用 multipart 圖片上傳；metadata 由後端儲存與解析產生。 |
| 2026-11-08 | 搜尋／明細圖片回傳 `imageUrl`，新增啟用貼文圖片讀取端點；新增美甲師讀取自己未啟用貼文明細的端點及 `enabled` 狀態欄位。 |
| 2026-10-30 | 對齊多圖貼文 schema，新增圖片 metadata request/response、排序、封面及圖片清單更新語意。 |
