# Aeslotlocks

**Aeslotlocks** 是一个针对 Applied Energistics 2 (AE2) 的辅助模组，旨在提供高度可配置的物品槽位锁定功能。它可以防止玩家在与 AE2 终端或界面交互时，意外通过 Shift+左键 或 空格键 移动特定槽位中的物品。

## ✨ 主要功能

*   **语义组锁定 (Semantic Blocking)**：禁止整个槽位组（例如玩家快捷栏、玩家背包、AE 存储区）的物品移动。
*   **精细化槽位锁定 (Fine-grained Blocking)**：支持锁定某个组内的特定槽位索引（例如只锁定快捷栏的第一个格子）。
*   **多种移动方式支持**：同时支持拦截 Shift+左键（快速移动）和 空格键（批量移动）。
*   **调试模式**：内置调试功能，方便玩家查询当前点击的槽位 ID。

## ⚙️ 配置说明

配置文件通常位于 `.minecraft/config` 文件夹下。

### 1. 禁用整个槽位组 (`blocked_semantic`)

禁用整个特定语义组（Semantic Group）的所有物品移动（包括 Shift+左键 和 空格键）。

*   **类型**: String List
*   **客户端/服务端**: **纯客户端 (Pure Client-side)**。无需服务器安装即可生效。
*   **参考 ID**: 请参考 `appeng.menu.SlotSemantics`。
*   **示例**:
    ```toml
    # 禁止操作所有存储槽位和玩家快捷栏
    blocked_semantic = ["STORAGE", "PLAYER_HOTBAR"]
    ```

### 2. 禁用特定槽位索引 (`blocked_semantic_slots`)

提供更精细的控制，允许你只锁定某个组内的特定格子。

*   **格式**: `'SEMANTIC_ID:index1,index2...'`
*   **逻辑说明**:
    1.  **Shift+Click**: 纯客户端生效。
    2.  **空格键移动 (Space-move)**: 这种精细化（基于索引）的锁定**需要服务器同时也安装本模组**才能生效。如果服务器未安装，空格键移动仍可能绕过此限制。
*   **示例**:
    ```toml
    # 仅锁定玩家快捷栏的第1个(索引0)和第2个(索引1)格子
    blocked_semantic_slots = ["PLAYER_HOTBAR:0,1"]
    ```

### 3. 获取槽位 ID 信息 (`clicked_info`)

为了方便配置，你可以开启此选项来获取槽位的语义 ID 和索引。

*   **类型**: Boolean
*   **默认值**: `false`
*   **用法**: 将其设置为 `true` 后，在游戏中点击任意 AE2 界面中的槽位，模组会将该槽位的 `Semantic ID` 和 `Index` 打印在日志或聊天栏中。配置完成后建议关闭。

## 📦 安装与依赖

*   **前置模组**: Applied Energistics 2 (AE2)
*   **安装位置**:
    *   大多数功能仅需安装在**客户端**。
    *   如果你需要“基于索引的空格键移动锁定”功能，则需要在**客户端和服务器**同时安装。

## 📝 常见 ID 示例

*(以下仅为示例，具体 ID 请使用 clicked_info 自行查询)*

*   `PLAYER_HOTBAR` - 玩家快捷栏
*   `PLAYER_INVENTORY` - 玩家背包主区域
*   `STORAGE` - AE 终端的存储区域
*   `INSCRIBER_INPUT_TOP` - 压印器顶部输入
*   `CRAFTING_GRID` - 合成面板的合成格
