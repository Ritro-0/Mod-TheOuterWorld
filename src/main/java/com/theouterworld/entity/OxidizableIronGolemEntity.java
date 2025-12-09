package com.theouterworld.entity;

import com.theouterworld.block.IronGolemStatueBlock;
import com.theouterworld.block.IronGolemStatueBlockEntity;
import com.theouterworld.block.ModBlocks;
import com.theouterworld.block.OxidizableIronBehavior;
import com.theouterworld.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/**
 * An Iron Golem that oxidizes over time in the Outerworld dimension.
 * Oxidation reduces movement speed and attack damage.
 * Can be waxed with honeycomb to prevent oxidation.
 * Can be scraped with axe to remove wax or de-oxidize.
 */
public class OxidizableIronGolemEntity extends IronGolemEntity {
    
    private static final TrackedData<Integer> OXIDATION_LEVEL = DataTracker.registerData(
        OxidizableIronGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAXED = DataTracker.registerData(
        OxidizableIronGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    
    // Base oxidation chance per tick (scaled by randomTickSpeed)
    // With default randomTickSpeed=3, similar to block oxidation timing
    private static final float BASE_OXIDATION_CHANCE_PER_TICK = 0.0001f;
    
    // Petrification chance: 1/172 per tick when fully oxidized (~0.58%)
    private static final float PETRIFICATION_CHANCE = 1.0f / 172.0f;
    
    // Stat modifiers per oxidation level
    private static final float[] SPEED_MODIFIERS = {1.0f, 0.85f, 0.65f, 0.4f};
    private static final float[] DAMAGE_MODIFIERS = {1.0f, 0.9f, 0.75f, 0.5f};
    private static final float[] KNOCKBACK_MODIFIERS = {1.0f, 0.9f, 0.75f, 0.6f};

    public OxidizableIronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createOxidizableIronGolemAttributes() {
        return IronGolemEntity.createIronGolemAttributes();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OXIDATION_LEVEL, 0);
        builder.add(WAXED, false);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("OxidationLevel", getOxidationLevel());
        nbt.putBoolean("Waxed", isWaxed());
    }

    public void readNbt(NbtCompound nbt) {
        nbt.getInt("OxidationLevel").ifPresent(this::setOxidationLevel);
        nbt.getBoolean("Waxed").ifPresent(this::setWaxed);
    }

    public int getOxidationLevel() {
        return this.dataTracker.get(OXIDATION_LEVEL);
    }

    public void setOxidationLevel(int level) {
        this.dataTracker.set(OXIDATION_LEVEL, Math.max(0, Math.min(3, level)));
    }

    public Oxidizable.OxidationLevel getOxidationState() {
        return switch (getOxidationLevel()) {
            case 0 -> Oxidizable.OxidationLevel.UNAFFECTED;
            case 1 -> Oxidizable.OxidationLevel.EXPOSED;
            case 2 -> Oxidizable.OxidationLevel.WEATHERED;
            default -> Oxidizable.OxidationLevel.OXIDIZED;
        };
    }

    public boolean isWaxed() {
        return this.dataTracker.get(WAXED);
    }

    public void setWaxed(boolean waxed) {
        this.dataTracker.set(WAXED, waxed);
    }

    @Override
    public void tick() {
        super.tick();
        
        // TODO: Add random chance for golems to jump hundreds of blocks in the air in low gravity biome
        // - When golem performs a jump action in low gravity biome, add random chance for super jump
        // - Check if golem is in low gravity biome (Outerworld dimension)
        // - On jump, roll random chance (e.g., 1-5% chance) for super jump
        // - If triggered, apply massive upward velocity (hundreds of blocks worth)
        // - This should be a fun feature, not a bug - golems can accidentally launch themselves!
        // - Consider adding visual/audio effects when super jump occurs
        
        // TODO: Add basalt weights for iron golems' feet to avoid super jumping
        // - Create basalt weight item/block that can be equipped to golems
        // - When equipped, golems should have increased weight/mass
        // - This should prevent the random super jump behavior in low gravity
        // - Could be implemented as an equipment slot or persistent effect
        // - Check for basalt weights in golem's inventory or as equipped item
        // - Players can use this to prevent golems from accidentally launching themselves
        
        World world = getEntityWorld();
        // Only process on server side
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            // Check for petrification first (only when fully oxidized)
            if (getOxidationLevel() == 3 && !isWaxed() && canPetrify()) {
                // Get randomTickSpeed to scale petrification chance for testing
                int randomTickSpeed = serverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
                float petrifyChance = PETRIFICATION_CHANCE * (randomTickSpeed / 3.0f);
                
                if (random.nextFloat() < petrifyChance) {
                    petrify(serverWorld);
                    return; // Entity is removed, don't continue
                }
            }
            
            // Oxidation logic (only in Outerworld, when not waxed and not fully oxidized)
            if (OxidizableIronBehavior.shouldOxidize(serverWorld, getBlockPos()) && !isWaxed() && getOxidationLevel() < 3) {
                int randomTickSpeed = serverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
                
                if (randomTickSpeed > 0) {
                    float baseChance = OxidizableIronBehavior.getOxidationChance(getOxidationState(), 0);
                    float scaledChance = BASE_OXIDATION_CHANCE_PER_TICK * randomTickSpeed * baseChance;
                    
                    if (random.nextFloat() < scaledChance) {
                        oxidize();
                    }
                }
            }
        }
    }
    
    /**
     * Check if the golem can petrify (turn into a statue).
     * Must be in air blocks (not in water, lava, or partially inside solid blocks).
     */
    private boolean canPetrify() {
        // Check if golem is in air (not in water, lava, etc.)
        return !this.isSubmergedInWater() && 
               !this.isInLava() && 
               this.isOnGround() &&
               getEntityWorld().getBlockState(getBlockPos()).isAir();
    }
    
    /**
     * Turn this golem into a statue block.
     */
    private void petrify(ServerWorld world) {
        // Get the statue block based on oxidation level (always oxidized for petrification)
        // Convert yaw to cardinal direction
        Direction facing = Direction.fromHorizontalDegrees(this.getYaw());
        BlockState statueState = ModBlocks.OXIDIZED_IRON_GOLEM_STATUE.getDefaultState()
            .with(Properties.HORIZONTAL_FACING, facing);
        
        // Place the statue block at the golem's feet
        world.setBlockState(getBlockPos(), statueState);
        
        // Get the block entity and store golem data
        if (world.getBlockEntity(getBlockPos()) instanceof IronGolemStatueBlockEntity statueEntity) {
            // Store pose data
            statueEntity.setBodyYaw(this.bodyYaw);
            statueEntity.setHeadYaw(this.headYaw);
            statueEntity.setHeadPitch(this.getPitch());
            
            // Store custom name if present
            if (this.hasCustomName()) {
                statueEntity.setCustomName(this.getCustomName());
            }
            
            statueEntity.markDirty();
        }
        
        // Play petrification sound
        world.playSound(null, getX(), getY(), getZ(), 
            SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0f, 0.5f);
        
        // Remove the golem entity
        this.discard();
    }

    private void oxidize() {
        if (getOxidationLevel() < 3) {
            setOxidationLevel(getOxidationLevel() + 1);
            // Play oxidation sound
            getEntityWorld().playSound(null, getX(), getY(), getZ(), 
                SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.NEUTRAL, 1.0f, 0.8f);
        }
    }

    @Override
    public float getMovementSpeed() {
        float baseSpeed = super.getMovementSpeed();
        return baseSpeed * SPEED_MODIFIERS[getOxidationLevel()];
    }

    @Override
    public boolean tryAttack(ServerWorld world, net.minecraft.entity.Entity target) {
        // Get base attack damage from attributes
        float baseDamage = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        float baseKnockback = (float) this.getAttributeValue(EntityAttributes.ATTACK_KNOCKBACK);
        
        // Apply oxidation modifiers
        float damage = baseDamage * DAMAGE_MODIFIERS[getOxidationLevel()];
        float knockback = baseKnockback * KNOCKBACK_MODIFIERS[getOxidationLevel()];
        
        // Deal damage manually with our modified values
        if (target instanceof net.minecraft.entity.LivingEntity livingTarget) {
            boolean success = livingTarget.damage(world, this.getDamageSources().mobAttack(this), damage);
            if (success) {
                // Apply knockback
                if (knockback > 0) {
                    livingTarget.takeKnockback(
                        knockback * 0.5,
                        net.minecraft.util.math.MathHelper.sin(this.getYaw() * ((float) Math.PI / 180F)),
                        -net.minecraft.util.math.MathHelper.cos(this.getYaw() * ((float) Math.PI / 180F))
                    );
                }
                this.onAttacking(target);
            }
            return success;
        }
        return false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        World world = getEntityWorld();
        
        // Honeycomb waxing
        if (stack.isOf(Items.HONEYCOMB) && !isWaxed()) {
            if (!world.isClient()) {
                setWaxed(true);
                world.playSound(null, getX(), getY(), getZ(), 
                    SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                
                // Spawn wax particles
                if (world instanceof ServerWorld serverWorld) {
                    for (int i = 0; i < 10; i++) {
                        serverWorld.spawnParticles(ParticleTypes.WAX_ON,
                            getX() + random.nextGaussian() * 0.5,
                            getY() + 1.0 + random.nextGaussian() * 0.5,
                            getZ() + random.nextGaussian() * 0.5,
                            1, 0, 0, 0, 0);
                    }
                }
                
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        // Axe interactions
        if (stack.getItem() instanceof AxeItem) {
            // If waxed, remove wax first
            if (isWaxed()) {
                if (!world.isClient()) {
                    setWaxed(false);
                    world.playSound(null, getX(), getY(), getZ(), 
                        SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                    
                    // Spawn wax off particles
                    if (world instanceof ServerWorld serverWorld) {
                        for (int i = 0; i < 10; i++) {
                            serverWorld.spawnParticles(ParticleTypes.WAX_OFF,
                                getX() + random.nextGaussian() * 0.5,
                                getY() + 1.0 + random.nextGaussian() * 0.5,
                                getZ() + random.nextGaussian() * 0.5,
                                1, 0, 0, 0, 0);
                        }
                    }
                    
                    if (!player.isCreative()) {
                        stack.damage(1, player, hand);
                    }
                }
                return ActionResult.SUCCESS;
            }
            
            // If not waxed, de-oxidize one stage
            if (getOxidationLevel() > 0) {
                if (!world.isClient()) {
                    setOxidationLevel(getOxidationLevel() - 1);
                    world.playSound(null, getX(), getY(), getZ(), 
                        SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                    
                    // Spawn scrape particles
                    if (world instanceof ServerWorld serverWorld) {
                        for (int i = 0; i < 10; i++) {
                            serverWorld.spawnParticles(ParticleTypes.SCRAPE,
                                getX() + random.nextGaussian() * 0.5,
                                getY() + 1.0 + random.nextGaussian() * 0.5,
                                getZ() + random.nextGaussian() * 0.5,
                                1, 0, 0, 0, 0);
                        }
                    }
                    
                    if (!player.isCreative()) {
                        stack.damage(1, player, hand);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        
        // Fall back to parent interaction (iron ingot healing)
        return super.interactMob(player, hand);
    }

    // Override to scale damage dealt based on oxidation
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return super.damage(world, source, amount);
    }
}

