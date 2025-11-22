package com.theouterworld.mixin;

import com.theouterworld.util.GlassHelmetUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.slot.ArmorSlot")
public abstract class ArmorSlotGlassMixin {

	@Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
	private void theouterworld$allowGlassInHead(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (GlassHelmetUtil.isGlassHelmetItem(stack)) {
			cir.setReturnValue(true);
		}
	}
}


