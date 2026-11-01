package com.copycatsplus.copycats.network;

import com.copycatsplus.copycats.Copycats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * PORT-NOTE(26.1): Catnip's packet registry is NeoForge only; Copycats+' two packets are plain Fabric payloads.
 */
public class CCPackets {
    public static final CustomPacketPayload.Type<ConfigSyncPacket> CONFIG_SYNC = new CustomPacketPayload.Type<>(Copycats.asResource("config_sync"));
    public static final CustomPacketPayload.Type<FillCopycatPacket> FILL_COPYCAT = new CustomPacketPayload.Type<>(Copycats.asResource("fill_copycat"));

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(CONFIG_SYNC, ConfigSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FILL_COPYCAT, FillCopycatPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FILL_COPYCAT, (packet, context) -> packet.handle(context.player()));
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(CONFIG_SYNC, (packet, context) -> packet.handle(context.player()));
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToAllClients(MinecraftServer server, CustomPacketPayload packet) {
        for (ServerPlayer player : PlayerLookup.all(server))
            ServerPlayNetworking.send(player, packet);
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }
}
