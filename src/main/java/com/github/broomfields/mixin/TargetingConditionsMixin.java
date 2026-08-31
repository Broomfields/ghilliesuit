package com.github.broomfields.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.broomfields.Ghilliesuit;

@Mixin(TargetingConditions.class)
public abstract class TargetingConditionsMixin {

	// Mobs cannot acquire a crouching full-suit wearer as a target. The target-search range is
	// still reduced for partial suits via LivingEntityMixin#getVisibilityPercent.
	@Inject(at = @At("HEAD"), method = "test", cancellable = true)
	private void ghilliesuit_test(ServerLevel level, LivingEntity targeter, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
		if (Ghilliesuit.hasFullSuit(target) && target.isCrouching()) {
			cir.setReturnValue(false);
		}
	}
}
