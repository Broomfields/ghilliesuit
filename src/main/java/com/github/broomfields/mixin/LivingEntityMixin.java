package com.github.broomfields.mixin;

import com.github.broomfields.Ghilliesuit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	private static final int INVISIBILITY_DURATION_TICKS = 200;
	private static final int INVISIBILITY_REFRESH_THRESHOLD_TICKS = 20;

	private static final double DETECTION_REDUCTION_PER_PIECE = 1.0 / 8.0;

	@Unique
	private boolean ghilliesuitInvisibilityApplied;

	@Unique
	private double ghilliesuitPendingHungerDrain;

	@Inject(at = @At("HEAD"), method = "tick")
	private void ghilliesuit_tick(CallbackInfo callback) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!entity.isAlive()) {
			this.ghilliesuitInvisibilityApplied = false;
			return;
		}

		if (entity instanceof ServerPlayer serverPlayer) {
			this.ghilliesuitPendingHungerDrain = Ghilliesuit.drainStealthHunger(serverPlayer, this.ghilliesuitPendingHungerDrain);
		}
		boolean shouldHide = Ghilliesuit.isStealthActive(entity);

		if (shouldHide) {
			MobEffectInstance invisibilityEffect = entity.getEffect(MobEffects.INVISIBILITY);
			if (invisibilityEffect == null || invisibilityEffect.getDuration() <= INVISIBILITY_REFRESH_THRESHOLD_TICKS) {
				entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBILITY_DURATION_TICKS, 0, false, false, false));
				this.ghilliesuitInvisibilityApplied = true;
			}
		} else if (this.ghilliesuitInvisibilityApplied) {
			entity.removeEffect(MobEffects.INVISIBILITY);
			this.ghilliesuitInvisibilityApplied = false;
		}
	}

	@Inject(at = @At("RETURN"), method = "getVisibilityPercent", cancellable = true)
	private void ghilliesuit_getVisibilityPercent(ServerLevel level, Entity targetingEntity, CallbackInfoReturnable<Double> callback) {
		int ghilliePieceCount = Ghilliesuit.ghilliePiecesWorn((LivingEntity) (Object) this);
		if (ghilliePieceCount > 0) {
			callback.setReturnValue(callback.getReturnValueD() * (1.0 - ghilliePieceCount * DETECTION_REDUCTION_PER_PIECE));
		}
	}
}
