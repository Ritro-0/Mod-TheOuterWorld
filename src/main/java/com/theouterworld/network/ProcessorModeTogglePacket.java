package com.theouterworld.network;

import com.theouterworld.OuterWorldMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ProcessorModeTogglePacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ProcessorModeTogglePacket> ID = 
        new CustomPayload.Id<>(Identifier.of(OuterWorldMod.MOD_ID, "processor_mode_toggle"));
    
    public static final PacketCodec<RegistryByteBuf, ProcessorModeTogglePacket> CODEC = 
        PacketCodec.tuple(
            BlockPos.PACKET_CODEC, ProcessorModeTogglePacket::pos,
            ProcessorModeTogglePacket::new
        );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

