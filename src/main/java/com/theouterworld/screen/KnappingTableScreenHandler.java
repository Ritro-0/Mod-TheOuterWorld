package com.theouterworld.screen;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.registry.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class KnappingTableScreenHandler extends ScreenHandler {
    private final CraftingInventory input;
    private final CraftingResultInventory result;
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public KnappingTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public KnappingTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.KNAPPING_TABLE_SCREEN_HANDLER, syncId);
        this.context = context;
        this.player = playerInventory.player;
        this.input = new CraftingInventory(this, 3, 3);
        this.result = new CraftingResultInventory();

        // Result slot
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35));

        // 3x3 crafting grid
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.input, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        // Player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContentChanged(net.minecraft.inventory.Inventory inventory) {
        this.context.run((world, pos) -> {
            updateResult(this, world, this.player, this.input, this.result);
        });
    }

    private static void updateResult(ScreenHandler handler, World world, PlayerEntity player, 
                                     CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            ItemStack result = ItemStack.EMPTY;
            
            // Create recipe input from the crafting inventory
            CraftingRecipeInput.Positioned positioned = craftingInventory.createPositionedRecipeInput();
            CraftingRecipeInput recipeInput = positioned.input();
            
            // Find matching recipe - use proper generic type
            var optional = world.getServer()
                .getRecipeManager()
                .<CraftingRecipeInput, net.minecraft.recipe.CraftingRecipe>getFirstMatch(
                    RecipeType.CRAFTING, recipeInput, world);
            
            if (optional.isPresent()) {
                RecipeEntry<net.minecraft.recipe.CraftingRecipe> recipeEntry = optional.get();
                if (resultInventory.shouldCraftRecipe(serverPlayer, recipeEntry)) {
                    ItemStack craftResult = recipeEntry.value().craft(recipeInput, world.getRegistryManager());
                    if (craftResult.isItemEnabled(world.getEnabledFeatures())) {
                        result = craftResult;
                    }
                }
            }
            
            resultInventory.setStack(0, result);
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, result));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.input);
        });
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.KNAPPING_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        
        if (slot2 != null && slot2.hasStack()) {
            ItemStack slotStack = slot2.getStack();
            itemStack = slotStack.copy();
            
            if (slot == 0) {
                // Result slot - move to player inventory
                this.context.run((world, pos) -> slotStack.getItem().onCraftByPlayer(slotStack, player));
                if (!this.insertItem(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(slotStack, itemStack);
            } else if (slot >= 10 && slot < 46) {
                // Player inventory - move to crafting grid
                if (!this.insertItem(slotStack, 1, 10, false)) {
                    if (slot < 37) {
                        if (!this.insertItem(slotStack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(slotStack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(slotStack, 10, 46, false)) {
                // Crafting grid - move to player inventory
                return ItemStack.EMPTY;
            }
            
            if (slotStack.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot2.onTakeItem(player, slotStack);
            if (slot == 0) {
                player.dropItem(slotStack, false);
            }
        }
        
        return itemStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
    }
}
