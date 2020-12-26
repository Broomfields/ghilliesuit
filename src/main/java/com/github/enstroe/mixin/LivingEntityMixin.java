package com.github.enstroe.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)

public class LivingEntityMixin {
    
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        // ItemStack helmetStack = equipped
    }
}
