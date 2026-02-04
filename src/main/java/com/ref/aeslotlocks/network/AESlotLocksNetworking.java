package com.ref.aeslotlocks.network;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import com.ref.aeslotlocks.AESlotLocks;
import com.ref.aeslotlocks.AESlotLocksClientConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = AESlotLocks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AESlotLocksNetworking {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(
          ResourceLocation.fromNamespaceAndPath(AESlotLocks.MOD_ID, "main"),
          () -> PROTOCOL_VERSION,
          PROTOCOL_VERSION::equals,
          PROTOCOL_VERSION::equals);

  public static final Map<UUID, Map<SlotSemantic, Set<Integer>>> SERVER_PLAYER_DATA =
      new ConcurrentHashMap<>();

  public static void register() {
    INSTANCE.registerMessage(
        0,
        ConfigSyncPacket.class,
        ConfigSyncPacket::encode,
        ConfigSyncPacket::decode,
        ConfigSyncPacket::handle);
  }

  @SubscribeEvent
  public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    SERVER_PLAYER_DATA.remove(event.getEntity().getUUID());
  }

  @OnlyIn(Dist.CLIENT)
  public static void sendConfigToServer() {
    if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
      INSTANCE.sendToServer(new ConfigSyncPacket(AESlotLocksClientConfig.blocked_semantic_slots));
    }
  }

  public static class ConfigSyncPacket {
    private final Map<SlotSemantic, Set<Integer>> map;
    private static final int MAX_ENTRIES = 100;

    public ConfigSyncPacket(Map<SlotSemantic, Set<Integer>> map) {
      this.map = map;
    }

    public static void encode(ConfigSyncPacket msg, FriendlyByteBuf buf) {
      buf.writeMap(
          msg.map,
          (b, semantic) -> b.writeUtf(semantic.id()),
          (b, set) -> b.writeCollection(set, FriendlyByteBuf::writeInt));
    }

    public static ConfigSyncPacket decode(FriendlyByteBuf buf) {
      Map<SlotSemantic, Set<Integer>> map =
          buf.readMap(
              b -> {
                if (b.readableBytes() < 1) return null;
                String id = b.readUtf(256);
                return SlotSemantics.get(id);
              },
              b -> new HashSet<>(b.readCollection(ArrayList::new, FriendlyByteBuf::readInt)));

      map.entrySet().removeIf(e -> e.getKey() == null);
      return new ConfigSyncPacket(map);
    }

    public static void handle(ConfigSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get()
          .enqueueWork(
              () -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;

                if (msg.map.size() > MAX_ENTRIES) {
                  AESlotLocks.LOGGER.warn(
                      "Player {} sent an oversized config packet!", sender.getName().getString());
                  return;
                }

                SERVER_PLAYER_DATA.put(sender.getUUID(), msg.map);
              });
      ctx.get().setPacketHandled(true);
    }
  }
}
