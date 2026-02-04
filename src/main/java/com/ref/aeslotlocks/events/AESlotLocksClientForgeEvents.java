package com.ref.aeslotlocks.events;

import com.ref.aeslotlocks.AESlotLocks;
import com.ref.aeslotlocks.network.AESlotLocksNetworking;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = AESlotLocks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class AESlotLocksClientForgeEvents {

  @SubscribeEvent
  public static void onJoinServer(ClientPlayerNetworkEvent.LoggingIn event) {
    AESlotLocksNetworking.sendConfigToServer();
  }
}
