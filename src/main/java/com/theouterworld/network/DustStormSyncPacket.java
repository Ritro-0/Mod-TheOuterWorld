package com.theouterworld.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DustStormSyncPacket(boolean active) implements CustomPayload {
    public static final Id<DustStormSyncPacket> ID = new Id<>(Identifier.of("theouterworld", "dust_storm_sync"));
    
    public static final PacketCodec<RegistryByteBuf, DustStormSyncPacket> CODEC = PacketCodec.of(
        DustStormSyncPacket::write, 
        DustStormSyncPacket::new
    );
    
    public DustStormSyncPacket(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }
    
    public void write(RegistryByteBuf buf) {
        buf.writeBoolean(active);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

