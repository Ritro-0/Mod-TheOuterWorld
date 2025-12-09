package com.theouterworld.block;

import com.theouterworld.item.ModItems;
import com.theouterworld.registry.ModBlockEntities;
import com.theouterworld.screen.ProcessorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import com.theouterworld.OuterWorldMod;

public class ProcessorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    // Slot indices
    public static final int PRIMARY_INPUT_SLOT = 0;  // Main ingredient (like brewing ingredient)
    public static final int SECONDARY_INPUT_SLOT = 1; // Secondary input (like blaze powder slot)
    public static final int OUTPUT_SLOT = 2;
    public static final int INVENTORY_SIZE = 3;
    
    // Processing constants
    public static final int MAX_PROGRESS = 400;      // Total ticks for processing (20 seconds)
    public static final int MAX_HEAT = 200;          // Total ticks for heating (10 seconds)
    
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    
    // State variables
    private int progress = 0;       // Processing progress (0 to MAX_PROGRESS)
    private int heat = 0;           // Heat level (0 to MAX_HEAT)
    private boolean heatMode = false; // false = processing mode, true = heat mode
    
    private final Random random = new Random();
    
    // Property delegate for syncing to client
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_PROGRESS;
                case 2 -> heat;
                case 3 -> MAX_HEAT;
                case 4 -> heatMode ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> heat = value;
                case 4 -> heatMode = (value == 1);
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public ProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROCESSOR, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.theouterworld.processor");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ProcessorScreenHandler(syncId, playerInventory, this, this.propertyDelegate, this.pos);
    }

    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Progress", progress);
        nbt.putInt("Heat", heat);
        nbt.putBoolean("HeatMode", heatMode);
        
        // Write inventory - save item ID and count
        NbtList items = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putInt("Slot", i);
                itemNbt.putString("id", Registries.ITEM.getId(stack.getItem()).toString());
                itemNbt.putInt("Count", stack.getCount());
                items.add(itemNbt);
            }
        }
        nbt.put("Items", items);
    }

    protected void readNbt(NbtCompound nbt) {
        nbt.getInt("Progress").ifPresent(val -> this.progress = val);
        nbt.getInt("Heat").ifPresent(val -> this.heat = val);
        nbt.getBoolean("HeatMode").ifPresent(val -> this.heatMode = val);
        
        // Read inventory - clear first, then load saved items
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        if (nbt.contains("Items")) {
            NbtList items = nbt.getListOrEmpty("Items");
            for (int i = 0; i < items.size(); i++) {
                NbtCompound itemNbt = items.getCompoundOrEmpty(i);
                int slot = itemNbt.getInt("Slot").orElse(-1);
                if (slot >= 0 && slot < inventory.size()) {
                    itemNbt.getString("id").ifPresent(idStr -> {
                        var itemId = net.minecraft.util.Identifier.tryParse(idStr);
                        if (itemId != null) {
                            var item = Registries.ITEM.get(itemId);
                            int count = itemNbt.getInt("Count").orElse(1);
                            inventory.set(slot, new ItemStack(item, count));
                        }
                    });
                }
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, ProcessorBlockEntity entity) {
        if (world.isClient()) return;
        
        boolean dirty = false;
        
        if (entity.heatMode) {
            dirty = tickHeatMode(entity);
        } else {
            dirty = tickProcessingMode(entity);
        }
        
        if (dirty) {
            entity.markDirty();
        }
    }
    
    private static boolean tickProcessingMode(ProcessorBlockEntity entity) {
        if (!hasProcessingRecipe(entity)) {
            if (entity.progress > 0) {
                entity.progress = 0;
                return true;
            }
            return false;
        }
        
        entity.progress++;
        if (entity.progress >= MAX_PROGRESS) {
            entity.progress = 0;
            processRecipe(entity);
            return true;
        }
        
        return true;
    }
    
    private static boolean tickHeatMode(ProcessorBlockEntity entity) {
        boolean hasRecipe = hasHeatRecipe(entity);
        
        if (!hasRecipe) {
            if (entity.progress > 0 || entity.heat > 0) {
                entity.progress = 0;
                entity.heat = 0;
                return true;
            }
            return false;
        }
        
        // Heat mode: first heat up, then process
        if (entity.heat < MAX_HEAT) {
            // Heating phase
            entity.heat++;
            return true;
        } else {
            // Processing phase (after fully heated)
            entity.progress++;
            if (entity.progress >= MAX_PROGRESS) {
                entity.progress = 0;
                entity.heat = 0;
                processHeatRecipe(entity);
                return true;
            }
            return true;
        }
    }
    
    private static boolean hasProcessingRecipe(ProcessorBlockEntity entity) {
        ItemStack primary = entity.getStack(PRIMARY_INPUT_SLOT);
        
        // Recipe: Regolith -> chance at iron ingot
        return !primary.isEmpty() && primary.isOf(ModBlocks.REGOLITH.asItem());
    }
    
    private static boolean hasHeatRecipe(ProcessorBlockEntity entity) {
        ItemStack primary = entity.getStack(PRIMARY_INPUT_SLOT);
        ItemStack secondary = entity.getStack(SECONDARY_INPUT_SLOT);
        
        // Recipe: Oxidized Basalt (primary/slot 0) + Regolith (secondary/slot 1) -> chance at Rust Splint
        boolean primaryValid = !primary.isEmpty() && primary.isOf(ModBlocks.OXIDIZED_BASALT.asItem());
        boolean secondaryValid = !secondary.isEmpty() && secondary.isOf(ModBlocks.REGOLITH.asItem());
        
        return primaryValid && secondaryValid;
    }
    
    private static void processRecipe(ProcessorBlockEntity entity) {
        ItemStack primary = entity.getStack(PRIMARY_INPUT_SLOT);
        
        // Recipe: Regolith -> 40% iron nugget, 60% nothing
        if (primary.isOf(ModBlocks.REGOLITH.asItem())) {
            primary.decrement(1);
            
            if (entity.random.nextFloat() < 0.4f) {
                // 40% chance for iron nugget
                ItemStack output = entity.getStack(OUTPUT_SLOT);
                if (output.isEmpty()) {
                    entity.setStack(OUTPUT_SLOT, new ItemStack(Items.IRON_NUGGET));
                } else if (output.isOf(Items.IRON_NUGGET) && output.getCount() < output.getMaxCount()) {
                    output.increment(1);
                }
                // If output is full, item is lost (or we could prevent processing)
            }
            // 60% chance: nothing happens (input consumed, no output)
        }
    }
    
    private static void processHeatRecipe(ProcessorBlockEntity entity) {
        ItemStack primary = entity.getStack(PRIMARY_INPUT_SLOT);
        ItemStack secondary = entity.getStack(SECONDARY_INPUT_SLOT);
        
        // Recipe: Oxidized Basalt + Regolith -> 20% Rust Splint, 80% nothing
        if (primary.isOf(ModBlocks.OXIDIZED_BASALT.asItem()) 
            && secondary.isOf(ModBlocks.REGOLITH.asItem())) {
            primary.decrement(1);
            secondary.decrement(1);
            
            if (entity.random.nextFloat() < 0.2f) {
                // 20% chance for rust splint
                ItemStack output = entity.getStack(OUTPUT_SLOT);
                if (output.isEmpty()) {
                    entity.setStack(OUTPUT_SLOT, new ItemStack(ModItems.RUST_SPLINT));
                } else if (output.isOf(ModItems.RUST_SPLINT) && output.getCount() < output.getMaxCount()) {
                    output.increment(1);
                }
            }
            // 80% chance: nothing happens (inputs consumed, no output)
        }
    }
    
    public void toggleMode() {
        this.heatMode = !this.heatMode;
        this.progress = 0;
        this.heat = 0;
        this.markDirty();
    }
    
    public boolean isHeatMode() {
        return heatMode;
    }

    // Inventory implementation
    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result;
        if (stack.getCount() <= amount) {
            result = stack;
            inventory.set(slot, ItemStack.EMPTY);
        } else {
            result = stack.split(amount);
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }
}
