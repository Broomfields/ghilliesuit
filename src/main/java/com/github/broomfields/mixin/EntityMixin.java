package com.github.broomfields.mixin;

import com.github.broomfields.Ghilliesuit;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
	private void ghilliesuit_isInvisibleTo(Player player, CallbackInfoReturnable<Boolean> callback) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof LivingEntity livingEntity && Ghilliesuit.isStealthActive(livingEntity)) {
			callback.setReturnValue(false);
		}
	}
}
