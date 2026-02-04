package com.ref.aeslotlocks.mixin;

import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import com.ref.aeslotlocks.network.AESlotLocksNetworking;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AEBaseMenu.class, remap = false)
public abstract class AEBaseMenuMixin extends AbstractContainerMenu {

  protected AEBaseMenuMixin(@Nullable MenuType<?> pMenuType, int pContainerId) {
    super(pMenuType, pContainerId);
  }

  @Shadow
  public abstract List<Slot> getSlots(SlotSemantic semantic);

  @Shadow
  public abstract @Nullable SlotSemantic getSlotSemantic(Slot s);

  @Inject(
      method = "doAction",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lappeng/menu/AEBaseMenu;getSlotSemantic(Lnet/minecraft/world/inventory/Slot;)Lappeng/menu/SlotSemantic;"),
      cancellable = true)
  private void onDoMoveRegionAction(
      ServerPlayer player, InventoryAction action, int slot, long id, CallbackInfo ci) {
    if (action == InventoryAction.MOVE_REGION) {
      Map<SlotSemantic, Set<Integer>> playerConfig =
          AESlotLocksNetworking.SERVER_PLAYER_DATA.get(player.getUUID());
      if (playerConfig == null) return;

      Slot clickedSlot = this.getSlot(slot);
      SlotSemantic semantic = this.getSlotSemantic(clickedSlot);

      if (semantic == null || !playerConfig.containsKey(semantic)) {
        return;
      }

      Set<Integer> blockedIndices = playerConfig.get(semantic);
      if (blockedIndices == null || blockedIndices.isEmpty()) {
        return;
      }

      List<Slot> slotsInRegion = this.getSlots(semantic);

      for (Slot slotToMove : slotsInRegion) {
        if (blockedIndices.contains(slotToMove.getContainerSlot())) {
          continue;
        }
        this.quickMoveStack(player, slotToMove.index);
      }

      ci.cancel();
    }
  }
}
