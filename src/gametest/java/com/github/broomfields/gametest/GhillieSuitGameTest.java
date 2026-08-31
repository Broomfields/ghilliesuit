package com.github.broomfields.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.github.broomfields.Ghilliesuit;

public class GhillieSuitGameTest {

	// Each ghillie piece worn reduces the detection multiplier by 1/8, so a full suit halves it
	// (the standard hostile follow range is 16 blocks, giving 16 -> 8).
	@GameTest(structure = "minecraft:empty")
	public void eachPieceReducesDetectionMultiplier(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, new Vec3(0.5, 1.0, 0.5));
		assertMultiplier(helper, zombie, 0, 1.0);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Ghilliesuit.GHILLIE_HELMET));
		assertMultiplier(helper, zombie, 1, 7.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Ghilliesuit.GHILLIE_CHESTPLATE));
		assertMultiplier(helper, zombie, 2, 6.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Ghilliesuit.GHILLIE_LEGGINGS));
		assertMultiplier(helper, zombie, 3, 5.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Ghilliesuit.GHILLIE_BOOTS));
		assertMultiplier(helper, zombie, 4, 4.0 / 8.0);
		helper.succeed();
	}

	private static void assertMultiplier(GameTestHelper helper, Zombie zombie, int pieces, double expected) {
		double actual = zombie.getVisibilityPercent(null);
		helper.assertTrue(
			Math.abs(actual - expected) < 1.0E-6,
			"with " + pieces + " pieces worn the detection multiplier should be " + expected + " but was " + actual
		);
	}
}
