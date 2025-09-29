# EatMineral 插件開發文檔

## 專案概述
EatMineral 是一個 Minecraft 插件，讓玩家可以將各種礦物錠轉換為可食用的食物。

## 功能特色
- 支援 5 種礦物：鑽石、黃金、煤炭、鐵、銅
- 每種礦物都有獨特的名稱、描述和食物效果
- 完整的權限系統
- 可配置的設定文件
- 管理員命令支援

## 礦物轉換列表
| 原礦物 | 轉換後名稱 | 飽食度 | 飽和度 | 描述 |
|--------|------------|--------|--------|------|
| 鑽石 | §b可食用礦物-鑽石錠 | 8 | 1.2 | 閃閃發光的鑽石錠 |
| 黃金 | §6可食用礦物-黃金錠 | 6 | 1.0 | 金黃色的黃金錠 |
| 煤炭 | §8可食用礦物-煤炭 | 4 | 0.8 | 黑黑的煤炭 |
| 鐵 | §7可食用礦物-鐵錠 | 5 | 0.9 | 堅硬的鐵錠 |
| 銅 | §c可食用礦物-銅錠 | 3 | 0.7 | 溫暖的銅錠 |

## 命令列表
- `/eatmineral` - 顯示幫助信息
- `/eatmineral reload` - 重新載入配置
- `/eatmineral list` - 列出所有可食用礦物
- `/eatmineral give <礦物名稱>` - 給予可食用礦物

## 權限節點
- `eatmineral.admin` - 管理員權限（默認：OP）
- `eatmineral.use` - 使用礦物食用功能（默認：true）

## 開發環境
- Java 17
- Maven 3.6+
- Spigot API 1.20.4

## 編譯說明
1. 確保已安裝 Java 17 和 Maven
2. 執行 `mvn clean package`
3. 編譯完成的 JAR 文件位於 `target/` 目錄

## GitHub Actions
專案配置了自動編譯流程：
- 推送到 `main` 或 `develop` 分支時自動編譯
- 編譯成功後自動創建 Release
- 支援 Maven 依賴緩存以提升編譯速度

## 更新日誌

### v1.0.0 (2024-01-XX)
- 初始版本發布
- 實現基本礦物食用功能
- 添加 5 種礦物支援
- 配置管理員命令系統
- 設置 GitHub Actions 自動編譯

### v1.0.1 (2024-01-XX)
- 修改礦物名稱為「可食用礦物-XXX錠」格式
- 為每種礦物添加不同的顏色代碼
- 更新配置文件和文檔

### v1.0.2 (2024-01-XX)
- 更新礦物描述文字，增加幽默元素
- 修復 GitHub Actions 中 upload-artifact 版本問題（v3 → v4）
- 同步更新配置文件中的礦物描述

### v1.0.3 (2024-01-XX)
- 修正煤炭名稱：從「可食用礦物-煤炭錠」改為「可食用礦物-煤炭」
- 更新相關文檔和配置文件

## 技術架構
```
src/main/java/com/eatmineral/
├── EatMineral.java              # 主插件類
├── commands/
│   └── EatMineralCommand.java   # 命令處理器
├── items/
│   └── EatableMineral.java      # 可食用礦物類
├── listeners/
│   └── PlayerInteractListener.java # 事件監聽器
├── managers/
│   └── MineralManager.java      # 礦物管理器
└── utils/
    └── MessageUtil.java         # 消息工具類
```

## 配置說明
所有設定都可以在 `config.yml` 中修改：
- 礦物名稱和描述
- 食物效果數值
- 消息文本
- 插件開關

## 未來計劃
- [ ] 添加更多礦物支援
- [ ] 實現礦物合成配方
- [ ] 添加特殊效果（如藥水效果）
- [ ] 支援多語言
- [ ] 添加統計功能

## 貢獻指南
1. Fork 此專案
2. 創建功能分支
3. 提交更改
4. 發起 Pull Request

## 授權
此專案採用 MIT 授權條款。
