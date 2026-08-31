package com.github.broomfields.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.broomfields.Ghilliesuit;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	private static final int EFFECT_DURATION = 200; // 10 seconds, refreshed while crouching

	@Inject(at = @At("HEAD"), method = "tick")
	private void ghilliesuit_tick(CallbackInfo info) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!self.isAlive()) {
			return;
		}

		boolean hasFullSuit =
			self.getItemBySlot(EquipmentSlot.HEAD).is(Ghilliesuit.GHILLIE_HELMET)
				&& self.getItemBySlot(EquipmentSlot.CHEST).is(Ghilliesuit.GHILLIE_CHESTPLATE)
				&& self.getItemBySlot(EquipmentSlot.LEGS).is(Ghilliesuit.GHILLIE_LEGGINGS)
				&& self.getItemBySlot(EquipmentSlot.FEET).is(Ghilliesuit.GHILLIE_BOOTS);
		boolean shouldHide = hasFullSuit && self.isCrouching();

		// The vanilla Invisibility effect renders the wearer as a translucent ghost
		// rather than fully invisible.
		MobEffectInstance current = self.getEffect(MobEffects.INVISIBILITY);
		if (shouldHide) {
			if (current == null || current.getDuration() <= 20) {
				self.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION, 0, false, false, false));
			}
		} else if (current != null) {
			self.removeEffect(MobEffects.INVISIBILITY);
		}
	}
}
