package com.ref.aeslotlocks;

import com.mojang.logging.LogUtils;
import com.ref.aeslotlocks.network.AESlotLocksNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AESlotLocks.MOD_ID)
public class AESlotLocks {

  public static final String MOD_ID = "aeslotlocks";

  public static final Logger LOGGER = LogUtils.getLogger();

  public AESlotLocks(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    modEventBus.addListener(this::commonSetup);

    MinecraftForge.EVENT_BUS.register(this);

    context.registerConfig(ModConfig.Type.CLIENT, AESlotLocksClientConfig.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(AESlotLocksNetworking::register);
  }
}
