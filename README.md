<div >

**English** | [简体中文](README_zh_CN.md)

</div>

***

# Aeslotlocks

**Aeslotlocks** is an addon mod for **Applied Energistics 2 (AE2)** designed to provide customizable slot locking capabilities. It prevents players from accidentally moving items in or out of specific AE2 interface slots via Shift+Click or Space-Click (inventory sorting/moving).

## ✨ Features

*   **Semantic Group Blocking**: Lock entire groups of slots (e.g., the entire Player Hotbar or the AE Storage grid) from being interacted with via quick-move actions.
*   **Fine-Grained Slot Blocking**: Lock specific slot indexes within a group (e.g., lock only the first slot of the hotbar).
*   **Debug Tool**: Built-in functionality to help you easily find the internal IDs required for configuration.

## ⚙️ Configuration

The configuration file allows you to define exactly which slots should be locked.

### 1. Block Entire Groups (`blocked_semantic`)

Disables all item movement (both Shift+Click and Space-move) for entire slot semantic groups.

*   **Type**: String List
*   **Side**: **Pure Client-Side**. Does not require the mod to be on the server.
*   **Reference**: IDs correspond to `appeng.menu.SlotSemantics`.
*   **Example**:
    ```toml
    # Blocks interaction with the entire storage grid and the player's hotbar
    blocked_semantic = ["STORAGE", "PLAYER_HOTBAR"]
    ```

### 2. Block Specific Slots (`blocked_semantic_slots`)

Provides fine-grained movement blocking for specific slot indexes within a semantic group.

*   **Format**: `'SEMANTIC_ID:index1,index2...'`
*   **Logic & Requirements**:
    1.  **Shift+Click**: Works **Pure Client-Side**.
    2.  **Space-move**: Fine-grained blocking (index-based) **REQUIRES the mod to be installed on the Server**. If the mod is not on the server, space-moving items might bypass the lock for specific indexes.
*   **Example**:
    ```toml
    # Blocks only the first (index 0) and second (index 1) slots of the hotbar
    blocked_semantic_slots = ["PLAYER_HOTBAR:0,1"]
    ```

### 3. Debug Mode (`clicked_info`)

A helper utility to find the correct IDs and Indexes.

*   **Type**: Boolean
*   **Default**: `false`
*   **Usage**: Set this to `true`. When you click a slot in an AE2 interface in-game, the mod will print the `Semantic ID` and the `Slot Index` to the log/chat. Use this info to fill out the config, then disable it.

## 📦 Installation & Dependencies

*   **Required**: Applied Energistics 2 (AE2)
*   **Client**: Required for all locking features.
*   **Server**: Required **only** if you want to block **Space-move** actions on **specific slot indexes** (defined in `blocked_semantic_slots`).

## 📝 Common Semantic IDs

*(Examples only - enable `clicked_info` to find exact IDs)*

*   `PLAYER_HOTBAR` - The player's hotbar.
*   `PLAYER_INVENTORY` - The main player inventory.
*   `STORAGE` - The main storage grid in a Terminal.
*   `CRAFTING_GRID` - The 3x3 grid in a Crafting Terminal.
*   `INSCRIBER_INPUT_TOP` - Top input slot of an Inscriber.
