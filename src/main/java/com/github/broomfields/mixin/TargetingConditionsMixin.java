package com.github.broomfields.mixin;

import com.github.broomfields.Ghilliesuit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public abstract class TargetingConditionsMixin {

	@Inject(at = @At("HEAD"), method = "test", cancellable = true)
	private void ghilliesuit_test(ServerLevel level, LivingEntity targetingEntity, LivingEntity target, CallbackInfoReturnable<Boolean> callback) {
		if (Ghilliesuit.isStealthActive(target)) {
			callback.setReturnValue(false);
		}
	}
}
