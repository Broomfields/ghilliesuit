package com.github.broomfields.mixin;

import com.github.broomfields.Ghilliesuit;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Inject(method = "canCriticalAttack", at = @At("RETURN"), cancellable = true)
	private void ghilliesuit_poiseCritical(Entity target, CallbackInfoReturnable<Boolean> callback) {
		if (target instanceof LivingEntity && Ghilliesuit.hasActivePoise((Player) (Object) this)) {
			callback.setReturnValue(true);
		}
	}
}
