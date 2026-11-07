# ADR-0004：以 Spring Security 執行系統角色授權

- **狀態：** Accepted
- **日期：** 2026-11-08

## Context

系統需要依帳戶系統角色限制 API。現有 JWT filter 驗證 Token，但建立的 `Authentication` 沒有角色權限；`SecurityConfig` 僅區分公開端點與已登入端點。部分 Controller 同時包含公開瀏覽與需要美甲師身分的管理方法，因此不能只在整個 Controller 設定相同角色。

## Decision

- `USER_ROLE` 是帳戶系統角色來源。JWT 驗證成功後，從資料庫載入使用者啟用中的角色，轉成 `ROLE_<ROLE_CODE>` authorities 放入 Spring Security `Authentication`。
- 以 `@EnableMethodSecurity` 啟用方法授權；對角色專屬 Controller 或方法使用 `@PreAuthorize`。混合公開與登入方法的 Controller 授權標註在各方法上。
- `SecurityConfig` 保留明確列出的公開端點；其他端點需先通過既有登入驗證，再依方法角色規則授權。
- 沒有符合角色的請求拒絕存取。JWT filter 每次驗證 Token 時讀取目前有效角色，角色停用會於下一個請求生效。
- Service 層繼續驗證資源所有權與業務條件；API 層角色授權不能取代此檢查。
- 未來 `PRO` 為會員方案／功能資格，不加入 `USER_ROLE`；其權益檢查另行設計。

## Consequences

- 固定系統角色與 API 能力以程式碼及註記審查，不需要每個 API request 查一張角色—API 對照表。
- 角色改變即時生效，但每次 JWT 驗證會增加一次啟用角色查詢；需監控查詢負載並沿用 `USER_ROLE` 的使用者索引前綴。
- 新增或修改 API 角色需求時，需同步更新 Controller 註記及 API Specification。
- 已驗證但尚未有 `USER_ROLE` 資料的既有帳戶會沒有角色；部署前需完成角色回填。
