package com.github.broomfields.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.broomfields.Ghilliesuit;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	private static final int EFFECT_DURATION = 200; // 10 seconds, refreshed while crouching
	// Standard hostile follow range is 16 blocks; each piece removes 2 blocks, so a full suit halves it.
	private static final double DETECTION_REDUCTION_PER_PIECE = 1.0 / 8.0;

	// Per-instance so we only ever remove the invisibility we applied, never a potion-brewed one.
	// A static UUID set would break in singleplayer: the client and the integrated server tick
	// separate instances of the same player in one JVM, and whichever ticks first consumes the
	// shared removal, leaving the other side's local effect stuck.
	@Unique
	private boolean ghilliesuitInvisibilityApplied;

	@Inject(at = @At("HEAD"), method = "tick")
	private void ghilliesuit_tick(CallbackInfo info) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!self.isAlive()) {
			this.ghilliesuitInvisibilityApplied = false;
			return;
		}

		boolean shouldHide = Ghilliesuit.hasFullSuit(self) && self.isCrouching();

		if (shouldHide) {
			// The vanilla Invisibility effect is what renders the wearer as a translucent ghost
			// to all viewers (see EntityMixin), rather than fully invisible.
			MobEffectInstance current = self.getEffect(MobEffects.INVISIBILITY);
			if (current == null || current.getDuration() <= 20) {
				self.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION, 0, false, false, false));
				this.ghilliesuitInvisibilityApplied = true;
			}
		} else if (this.ghilliesuitInvisibilityApplied) {
			self.removeEffect(MobEffects.INVISIBILITY);
			this.ghilliesuitInvisibilityApplied = false;
		}
	}

	// Each ghillie piece worn shrinks the detection range mobs use to find this entity
	// (TargetingConditions multiplies its search range by this value). The full-suit crouch
	// case is handled by TargetingConditionsMixin and MobMixin, which disable targeting entirely.
	@Inject(at = @At("RETURN"), method = "getVisibilityPercent", cancellable = true)
	private void ghilliesuit_getVisibilityPercent(Entity targetingEntity, CallbackInfoReturnable<Double> cir) {
		int pieces = Ghilliesuit.ghilliePiecesWorn((LivingEntity) (Object) this);
		if (pieces > 0) {
			cir.setReturnValue(cir.getReturnValueD() * (1.0 - pieces * DETECTION_REDUCTION_PER_PIECE));
		}
	}
}
