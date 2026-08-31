package com.github.broomfields.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.broomfields.Ghilliesuit;

@Mixin(Entity.class)
public abstract class EntityMixin {

	// An invisible entity is only rendered as a translucent ghost while isInvisibleTo(viewer) is
	// false. Making a stealthed wearer "not invisible to" everyone applies that ghost to all
	// viewers, including the wearer's own third-person camera, instead of vanishing entirely.
	@Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
	private void ghilliesuit_isInvisibleTo(Player player, CallbackInfoReturnable<Boolean> cir) {
		Entity self = (Entity) (Object) this;
		if (self instanceof LivingEntity living && Ghilliesuit.hasFullSuit(living) && living.isCrouching()) {
			cir.setReturnValue(false);
		}
	}
}
