package com.github.broomfields.mixin;

import com.github.broomfields.Ghilliesuit;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

	@Inject(at = @At("HEAD"), method = "asValidTarget", cancellable = true)
	private void ghilliesuit_asValidTarget(LivingEntity target, CallbackInfoReturnable<LivingEntity> callback) {
		if (target != null && Ghilliesuit.isStealthActive(target)) {
			callback.setReturnValue(null);
		}
	}
}
