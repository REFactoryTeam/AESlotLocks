package com.ref.aeslotlocks;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import com.ref.aeslotlocks.network.AESlotLocksNetworking;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AESlotLocks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AESlotLocksClientConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKED_SEMANTIC =
      BUILDER
          .comment(
              "Disables all item movement (both Shift+Click and Space-move) for entire slot semantic groups.",
              "This is a pure client-side feature and does not require the mod to be on the server.",
              "Refer to appeng.menu.SlotSemantics for available IDs.",
              "Examples: 'STORAGE', 'PLAYER_HOTBAR'.")
          .defineListAllowEmpty("blocked_semantic", List.of(), AESlotLocksClientConfig::Validator);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKED_SEMANTIC_SLOTS =
      BUILDER
          .comment(
              "Provides fine-grained movement blocking for specific slot indexes within a semantic group.",
              "Format: 'SEMANTIC_ID:index1,index2...'. ",
              "Important Logic:",
              "1. Shift+Click blocking: Works pure client-side for specific indexes.",
              "2. Space-move blocking: Fine-grained (index-based) blocking REQUIRES the mod to be installed on the server.",
              "Example: 'PLAYER_HOTBAR:0' blocks only the first hotbar slot.")
          .defineListAllowEmpty(
              "blocked_semantic_slots", List.of(), AESlotLocksClientConfig::BlockedMapValidator);

  private static final ForgeConfigSpec.BooleanValue CLICKED_INFO =
      BUILDER.define("clicked_info", false);

  private static boolean Validator(final Object obj) {
    return obj instanceof final String Semantics && (SlotSemantics.get(Semantics) != null);
  }

  private static boolean BlockedMapValidator(final Object obj) {
    if (!(obj instanceof String s)) return false;

    int colonIndex = s.indexOf(':');
    if (colonIndex <= 0 || colonIndex == s.length() - 1) return false;

    String semanticId = s.substring(0, colonIndex);
    String indicesStr = s.substring(colonIndex + 1);

    if (SlotSemantics.get(semanticId) == null) return false;

    String[] indices = indicesStr.split(",");
    if (indices.length == 0) return false;

    try {
      for (String idx : indices) {
        Integer.parseInt(idx.trim());
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static Set<SlotSemantic> blocked_semantic;

  public static Map<SlotSemantic, Set<Integer>> blocked_semantic_slots;

  public static boolean clicked_info;

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    if (event.getConfig().getSpec() != SPEC) return;
    blocked_semantic = new HashSet<>();
    BLOCKED_SEMANTIC.get().forEach(semantic -> blocked_semantic.add(SlotSemantics.get(semantic)));
    blocked_semantic_slots = new HashMap<>();
    blocked_semantic_slots =
        AESlotLocksClientConfig.decodeBlockedSemantics(BLOCKED_SEMANTIC_SLOTS.get());
    AESlotLocksNetworking.sendConfigToServer();
    clicked_info = CLICKED_INFO.get();
  }

  public static boolean isSlotBlocked(SlotSemantic semantic, int slotIndex) {
    if (semantic == null) return false;

    if (blocked_semantic.contains(semantic)) return true;

    return blocked_semantic_slots
        .getOrDefault(semantic, Collections.emptySet())
        .contains(slotIndex);
  }

  public static Map<SlotSemantic, Set<Integer>> decodeBlockedSemantics(
      List<? extends String> configList) {
    Map<SlotSemantic, Set<Integer>> resultMap = new HashMap<>();

    for (String entry : configList) {
      String[] parts = entry.split(":");
      if (parts.length != 2) continue;

      String semanticId = parts[0].trim();
      String[] indices = parts[1].split(",");

      Set<Integer> indexSet =
          Arrays.stream(indices)
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(Integer::parseInt)
              .collect(Collectors.toSet());

      resultMap.merge(
          SlotSemantics.get(semanticId),
          indexSet,
          (oldSet, newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
          });
    }

    return resultMap;
  }
}
