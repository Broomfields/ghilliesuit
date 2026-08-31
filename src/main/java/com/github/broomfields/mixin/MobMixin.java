package com.github.broomfields.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.broomfields.Ghilliesuit;

@Mixin(Mob.class)
public abstract class MobMixin {

	// Both getTarget() and setTarget() route through asValidTarget, so rejecting a stealthed
	// wearer here both clears a mob's existing target the moment the player crouches and stops
	// it being re-acquired (or retaliation).
	@Inject(at = @At("HEAD"), method = "asValidTarget", cancellable = true)
	private void ghilliesuit_asValidTarget(LivingEntity target, CallbackInfoReturnable<LivingEntity> cir) {
		if (target != null && Ghilliesuit.hasFullSuit(target) && target.isCrouching()) {
			cir.setReturnValue(null);
		}
	}
}
