package com.theouterworld.block;

import com.theouterworld.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Block entity for Iron Golem Statues.
 * Stores pose data (body/head rotation) and custom name.
 */
public class IronGolemStatueBlockEntity extends BlockEntity {
    
    // Pose data
    private float bodyYaw = 0.0f;
    private float headYaw = 0.0f;
    private float headPitch = 0.0f;
    
    // Custom name (if the golem was named)
    @Nullable
    private String customNameJson = null;

    public IronGolemStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRON_GOLEM_STATUE, pos, state);
    }

    // Getters and setters
    public float getBodyYaw() {
        return bodyYaw;
    }

    public void setBodyYaw(float bodyYaw) {
        this.bodyYaw = bodyYaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public void setHeadYaw(float headYaw) {
        this.headYaw = headYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public void setHeadPitch(float headPitch) {
        this.headPitch = headPitch;
    }

    @Nullable
    public Text getCustomName() {
        if (customNameJson == null) return null;
        try {
            return Text.literal(customNameJson);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCustomName(@Nullable Text customName) {
        if (customName != null) {
            this.customNameJson = customName.getString();
        } else {
            this.customNameJson = null;
        }
    }

    public boolean hasCustomName() {
        return customNameJson != null;
    }

    public void readNbt(NbtCompound nbt) {
        nbt.getFloat("BodyYaw").ifPresent(val -> this.bodyYaw = val);
        nbt.getFloat("HeadYaw").ifPresent(val -> this.headYaw = val);
        nbt.getFloat("HeadPitch").ifPresent(val -> this.headPitch = val);
        
        if (nbt.contains("CustomName")) {
            nbt.getString("CustomName").ifPresent(name -> this.customNameJson = name);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("BodyYaw", bodyYaw);
        nbt.putFloat("HeadYaw", headYaw);
        nbt.putFloat("HeadPitch", headPitch);
        
        if (customNameJson != null) {
            nbt.putString("CustomName", customNameJson);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }
}

