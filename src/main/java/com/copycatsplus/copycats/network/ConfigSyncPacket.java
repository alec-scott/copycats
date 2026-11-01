package com.copycatsplus.copycats.network;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.config.CCConfigs;
import com.copycatsplus.copycats.config.SyncConfigBase;
import com.copycatsplus.copycats.utility.Platform;
import io.netty.buffer.ByteBuf;
import com.zurrtum.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import com.zurrtum.create.catnip.codecs.stream.CatnipStreamCodecs;
import com.copycatsplus.copycats.port.catnip.config.ConfigBase;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.config.ModConfig;

public record ConfigSyncPacket(CompoundTag config, ModConfig.Type configType) implements CustomPacketPayload {

    public static final StreamCodec<ByteBuf, ModConfig.Type> CONFIG_TYPE_CODEC = CatnipStreamCodecBuilders.ofEnum(ModConfig.Type.class);

    public static final StreamCodec<ByteBuf, ConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecs.COMPOUND_AS_TAG, ConfigSyncPacket::config,
            CONFIG_TYPE_CODEC, ConfigSyncPacket::configType,
            ((tag, type) -> tag instanceof CompoundTag comp ? new ConfigSyncPacket(comp, type) : null)
    );

    public void handle(LocalPlayer localPlayer) {
        Platform.Environment.CLIENT.runIfCurrent(() -> () -> {
            ConfigBase config = CCConfigs.byType(configType);
            if (config instanceof SyncConfigBase syncConfig) {
                syncConfig.setSyncConfig(this.config);
                Copycats.LOGGER.debug("Sync Config: Received and applied server config {}", config.toString());
            } else {
                Copycats.LOGGER.warn("Sync Config: Received data for non-synchronized config, ignoring {}", config.toString());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CCPackets.CONFIG_SYNC;
    }
}
