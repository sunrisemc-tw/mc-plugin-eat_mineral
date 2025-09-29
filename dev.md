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
| 鑽石 | 鑽石糖 | 8 | 1.2 | 閃閃發光的珍貴糖果 |
| 黃金 | 黃金巧克力 | 6 | 1.0 | 香濃的巧克力 |
| 煤炭 | 煤炭餅乾 | 4 | 0.8 | 黑黑的但味道不錯 |
| 鐵 | 鐵質能量棒 | 5 | 0.9 | 堅硬的能量棒 |
| 銅 | 銅製糖果 | 3 | 0.7 | 溫暖的糖果 |

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
