package com.ref.aeslotlocks.mixin;

import appeng.client.gui.AEBaseScreen;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import com.ref.aeslotlocks.AESlotLocks;
import com.ref.aeslotlocks.AESlotLocksClientConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AEBaseScreen.class)
@OnlyIn(Dist.CLIENT)
public abstract class AEBaseScreenMixin<T extends AEBaseMenu> extends AbstractContainerScreen<T> {

  public AEBaseScreenMixin(T pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
  }

  @Inject(
      method = "slotClicked",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V"),
      cancellable = true)
  private void onSlotClickedHead(
      Slot slot, int slotIdx, int mouseButton, ClickType clickType, CallbackInfo ci) {
    if (clickType == ClickType.QUICK_MOVE) {
      SlotSemantic slotSemantic = this.getMenu().getSlotSemantic(slot);
      int containerSlot = slot.getContainerSlot();
      if (AESlotLocksClientConfig.clicked_info) {
        if (slotSemantic != null) {
          AESlotLocks.LOGGER.info("Shift+Click {}:{}", slotSemantic.id(), containerSlot);
        } else {
          AESlotLocks.LOGGER.info("Shift+Click {}", containerSlot);
        }
      }
      if (AESlotLocksClientConfig.isSlotBlocked(slotSemantic, containerSlot)) ci.cancel();
    }
  }

  @Inject(
      method = "slotClicked",
      at =
          @At(
              value = "FIELD",
              target =
                  "Lappeng/helpers/InventoryAction;MOVE_REGION:Lappeng/helpers/InventoryAction;",
              opcode = Opcodes.GETSTATIC),
      cancellable = true,
      remap = false)
  private void preventFilteredMoveRegion(
      Slot slot, int slotIdx, int mouseButton, ClickType clickType, CallbackInfo ci) {
    SlotSemantic slotSemantic = this.getMenu().getSlotSemantic(slot);
    if (AESlotLocksClientConfig.clicked_info) {
      if (slotSemantic != null) {
        AESlotLocks.LOGGER.info("Space-move {}", slotSemantic.id());
      }
    }
    if (AESlotLocksClientConfig.blocked_semantic.contains(slotSemantic)) ci.cancel();
  }
}
