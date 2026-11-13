# 文件與設計待辦

## P0：建立可靠基線

- [x] 以唯讀方式擷取 Oracle `NSAS` schema 的現行資料字典基線（2026-07-19）。
- [x] 依 DDL 調整 Entity 欄位、型別、sequence 主鍵與複合主鍵對應。
- [ ] 將現行資料庫建立 SQL 正式納入版本控制的 migration；資料字典快照不是可重建 DDL 的替代品。
- [ ] 依 DDL 為 16 張現有 Table 建立逐表設計文件，補欄位用途、預設值、商業規則與 API 對應。
- [ ] 確認 `NAIL_POST` 與 `MANICURIST_WORK` 的產品職責與是否需共存。
- [ ] 確認黑名單的產品影響範圍（搜尋、作品貼文、聊天、預約），再設計流程與 API。
- [ ] 審查 Reservation API 第一版的狀態轉換、取消／放鳥權限與服務內容協商限制。
- [ ] 實作 PENDING 逾時背景排程；第一版暫以查詢／操作時惰性更新 EXPIRED。
- [ ] 建立 USER_ROLE migration/backfill，為既有已驗證帳戶與美甲師補齊角色，並定義角色授予／撤銷及稽核流程。
- [ ] 評估 JWT Filter 每次驗證載入啟用角色所增加的 Oracle 查詢負載。
- [ ] 安裝或指定 JDK 17，執行完整 Maven 編譯與測試。

## P1：補齊既有模組契約

- [ ] 為 Auth、User、Shop、Manicurist、Service、Block Time、Nail Sample、Blacklist 建立逐支 API 規格。
- [ ] 統一 API 路徑、HTTP method、request/response envelope、錯誤碼與版本策略。
- [ ] 為所有已存在查詢建立資料量假設與執行計畫驗證紀錄。

## P2：進入下一階段前必須設計

- [ ] Tag Many-to-Many ADR、DDL、Index 與 API。
- [ ] Favorite、Chat、Notification、Search、Recommendation 的需求與 User Flow。
- [ ] Redis、MQ、WebSocket、Object Storage、Firebase 的採用 ADR（如需要）。
